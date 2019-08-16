/**
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.webasebee.core.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition.NamedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.webank.webasebee.common.bo.contract.ContractMapsInfo;
import com.webank.webasebee.common.bo.contract.ContractMethodInfo;
import com.webank.webasebee.common.bo.contract.MethodMetaInfo;
import com.webank.webasebee.common.constants.AbiTypeConstants;
import com.webank.webasebee.common.tools.ClazzScanUtils;
import com.webank.webasebee.common.tools.MethodUtils;
import com.webank.webasebee.common.vo.NameValueVO;
import com.webank.webasebee.core.config.SystemEnvironmentConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContractParser using for getting contract java code file info, that will be used to parse transaction data.
 *
 * @Description: ContractParser
 * @author graysonzhang
 * @author maojiayu
 * @data 2018-12-17 15:06:51
 *
 */
@Configuration
@Slf4j
public class ContractParser {

    /** @Fields monitorGeneratedConfig : monitor config params start with monitor in application.properties file */
    @Autowired
    private SystemEnvironmentConfig systemEnvironmentConfig;

    /**
     * Parsing all contract java code files from contract path, and storage contract info into ContractMethodInfo
     * object, and return ContractMethodInfo object list.
     * 
     * @return List<ContractMethodInfo>
     */
    @Bean
    public List<ContractMethodInfo> initContractMethodInfo() {
        List<ContractMethodInfo> contractMethodInfos = Lists.newArrayList();
        Set<Class<?>> clazzs = ClazzScanUtils.scan(systemEnvironmentConfig.getContractPath(),
                systemEnvironmentConfig.getContractPackName());
        for (Class<?> clazz : clazzs) {
            contractMethodInfos.add(parse(clazz));
        }
        return contractMethodInfos;
    }

    /**
     * Parsing single class object of contract java code file, and storage contract info into ContractMethodInfo object,
     * firstly, remove event function, query function and functions that param's null, compute methodId and save method
     * info into ContractMethodInfo object.
     * 
     * @param clazz: class object of contract java code file.
     * @return ContractMethodInfo
     */
    public ContractMethodInfo parse(Class<?> clazz) {
        AbiDefinition[] abiDefinitions = MethodUtils.getContractAbiList(clazz);
        if (abiDefinitions == null || abiDefinitions.length == 0) {
            return null;
        }
        String className = clazz.getSimpleName();
        String binary = MethodUtils.getContractBinary(clazz);
        ContractMethodInfo contractMethodInfo = new ContractMethodInfo();
        contractMethodInfo.setContractName(className);
        contractMethodInfo.setContractBinary(binary);

        List<MethodMetaInfo> methodIdList = Lists.newArrayList();
        contractMethodInfo.setMethodMetaInfos(methodIdList);

        for (AbiDefinition abiDefinition : abiDefinitions) {
            String abiType = abiDefinition.getType();
            // remove event function and query function
            if (abiType.equals(AbiTypeConstants.ABI_EVENT_TYPE) || abiDefinition.isConstant()) {
                continue;
            }
            // remove functions that input'params is null
            List<NamedType> inputs = abiDefinition.getInputs();
            if (inputs == null || inputs.isEmpty()) {
                continue;
            }
            String methodName = abiDefinition.getName();
            if (abiType.equals(AbiTypeConstants.ABI_CONSTRUCTOR_TYPE)) {
                methodName = clazz.getSimpleName();
            }
            // compute method id by method name and method input's params.
            String methodId = MethodUtils.buildMethodId(MethodUtils.buildMethodSignature(methodName, inputs));
            MethodMetaInfo metaInfo = new MethodMetaInfo();
            metaInfo.setMethodId(methodId);
            methodName = className + StringUtils.capitalize(methodName);
            metaInfo.setMethodName(methodName);
            metaInfo.setFieldsList(inputs);
            methodIdList.add(metaInfo);
        }
        return contractMethodInfo;
    }
    
    /**
     * Translate all contract info of ContractMethodInfo's objects to methodIdMap, methodFiledsMap and
     * contractBinaryMap.
     * 
     * @param contractMethodInfos: contractMethodInfos contains methodIdMap, methodFiledsMap and contractBinaryMap.
     * @return ContractMapsInfo
     */
    @Bean
    public ContractMapsInfo transContractMethodInfo2ContractMapsInfo(List<ContractMethodInfo> contractMethodInfos) {
        ContractMapsInfo contractMapsInfo = new ContractMapsInfo();
        Map<String, NameValueVO<String>> methodIdMap = new HashMap<>();
        Map<String, List<NamedType>> methodFiledsMap = new HashMap<>();
        Map<String, String> contractBinaryMap = new HashMap<>();
        for (ContractMethodInfo contractMethodInfo : contractMethodInfos) {
            contractBinaryMap.put(contractMethodInfo.getContractBinary(), contractMethodInfo.getContractName());
            for (MethodMetaInfo methodMetaInfo : contractMethodInfo.getMethodMetaInfos()) {
                NameValueVO<String> nameValue = new NameValueVO<>();
                nameValue.setName(contractMethodInfo.getContractName());
                nameValue.setValue(methodMetaInfo.getMethodName());
                methodIdMap.put(methodMetaInfo.getMethodId(), nameValue);
                methodFiledsMap.put(methodMetaInfo.getMethodName(), methodMetaInfo.getFieldsList());
            }
        }
        log.info("Init sync block: find {} contract constructors.", contractBinaryMap.size());
        contractMapsInfo.setContractBinaryMap(contractBinaryMap);
        contractMapsInfo.setMethodFiledsMap(methodFiledsMap);
        log.info("Init sync block: find {} contract methods.", methodIdMap.size());
        contractMapsInfo.setMethodIdMap(methodIdMap);
        return contractMapsInfo;
    }
}
