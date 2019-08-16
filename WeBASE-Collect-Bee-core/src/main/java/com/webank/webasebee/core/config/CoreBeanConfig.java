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
package com.webank.webasebee.core.config;

import java.io.IOException;

import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * BeanConfig registers system common beans.
 *
 * @author maojiayu
 * @data Dec 28, 2018 5:07:41 PM
 *
 */
@Configuration
@EnableTransactionManagement
public class CoreBeanConfig {

    /**
     * Beetl render template.
     * 
     * @return GroupTemplate
     * @throws IOException
     */
    @Bean
    public static GroupTemplate getGroupTEmplateInstance() throws IOException {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("");
        org.beetl.core.Configuration cfg = org.beetl.core.Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        return gt;
    }

    @Bean
    public static Credentials getCredentials() {
        return GenCredential.create();
    }
    
}
