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
package com.webank.webasebee.common.tools;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.crypto.Hash;
import org.fisco.bcos.web3j.protocol.ObjectMapperFactory;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition.NamedType;
import org.fisco.bcos.web3j.utils.Numeric;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MethodUtils
 *
 * @Description: MethodUtils
 * @author graysonzhang
 * @data 2018-12-10 15:12:38
 *
 */
@Slf4j
@Service
public class MethodUtils {

    /**
     * Translate NamedType list to TypeReference<Type> list.
     * 
     * @param list
     * @return List<TypeReference<Type>>
     */
    public static List<TypeReference<Type>> getMethodTypeReferenceList(List<NamedType> list) {
        List<TypeReference<Type>> referencesTypeList = Lists.newArrayList();
        for (NamedType type : list) {
            TypeReference reference = AbiTypeRefUtils.getTypeRef(type.getType().split(" ")[0]);
            log.debug("type: {} TypeReference converted: {}", type.getType(), JacksonUtils.toJson(reference));
            referencesTypeList.add(reference);
        }
        return referencesTypeList;
    }

    /**
     * Get contract binary.
     * 
     * @param clazz
     * @return String
     */
    public static String getContractBinary(Class<?> clazz) {
        String binary = null;
        try {
            Field field = clazz.getDeclaredField("BINARY");
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            binary = (String) field.get("BINARY");
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            log.error("Read BINARY error: {}", e.getMessage());
        }
        return binary;
    }

    /**
     * Get contract abi definitions.
     * 
     * @param clazz
     * @return AbiDefinition[]
     */
    public static AbiDefinition[] getContractAbiList(Class<?> clazz) {
        String abi = null;
        try {
            Field field = clazz.getDeclaredField("ABI");
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            abi = (String) field.get("ABI");
        } catch (NoSuchFieldException | SecurityException e) {
            log.error("{}", e.getMessage());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("{}", e.getMessage());
        }

        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        AbiDefinition[] abiDefinition = null;

        try {
            abiDefinition = objectMapper.readValue(abi, AbiDefinition[].class);
        } catch (IOException e) {
            log.error("Read ABI definition error: {}", e.getMessage());
        }
        log.debug("class: {} {}", clazz.getSimpleName(), JacksonUtils.toJson(abiDefinition));
        return abiDefinition;
    }

    /**
     * build contract method signature
     * 
     * @param methodName
     * @param parameters
     * @return String
     */
    public static String buildMethodSignature(String methodName, List<NamedType> parameters) {
        StringBuilder result = new StringBuilder();
        result.append(methodName);
        result.append("(");
        String params = parameters.stream().map(NamedType::getType).collect(Collectors.joining(","));
        result.append(params);
        result.append(")");
        log.debug("methodName: {}, joint method paras: {}", methodName, result);
        return result.toString();
    }

    /**
     * compute contract method id by method signature
     * 
     * @param methodSignature
     * @return String
     */
    public static String buildMethodId(String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = Hash.sha3(input);
        log.debug("methodId is {}", Numeric.toHexString(hash).substring(0, 10));
        return Numeric.toHexString(hash).substring(0, 10);
    }
}
