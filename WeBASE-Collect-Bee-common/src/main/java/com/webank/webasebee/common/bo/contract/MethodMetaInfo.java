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
package com.webank.webasebee.common.bo.contract;

import java.util.List;

import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition.NamedType;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * MethodMetaInfo
 *
 * @Description: MethodMetaInfo storages method config data for parsing method data.
 * @author graysonzhang
 * @data 2018-12-17 14:20:00
 *
 */
@Data
@Accessors(chain = true)
public class MethodMetaInfo {

    /** @Fields methodId : method id */
    private String methodId;

    /** @Fields methodName : method name */
    private String methodName;

    /** @Fields methodTableCount : for sharding method table */
    private int methodTableCount;

    /** @Fields ignoreParams : when parsing method data, the ignore params will be ignored */
    private List<String> ignoreParams;

    /** @Fields contractName : contract name */
    private String contractName;

    /** @Fields fieldsList : method input param list */
    private List<NamedType> fieldsList;
    
    private List<NamedType> outputFieldsList;

}
