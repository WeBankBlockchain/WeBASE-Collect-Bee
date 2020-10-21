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

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.fisco.bcos.sdk.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition.NamedType;
import org.springframework.stereotype.Service;

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
    public static List<ABIDefinition> getContractAbiList(Class<?> clazz) {
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
        return ContractAbiUtil.getFuncABIDefinition(abi);
    }
}
