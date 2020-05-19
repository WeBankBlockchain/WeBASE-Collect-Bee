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
package com.webank.webasebee.core.utils;

import org.fisco.bcos.web3j.abi.FunctionEncoder;
import org.junit.jupiter.api.Test;

import com.webank.webasebee.core.WebaseBeeApplicationTests;

/**
 * MethodUtilsTest
 *
 * @Description: MethodUtilsTest
 * @author maojiayu
 * @data May 19, 2020 5:53:38 PM
 *
 */
public class MethodUtilsTest extends WebaseBeeApplicationTests {
    private String name = "setBytes(bytes)";

    @Test
    public void testMethod() {
        System.out.println(FunctionEncoder.buildMethodId(name));

    }
}
