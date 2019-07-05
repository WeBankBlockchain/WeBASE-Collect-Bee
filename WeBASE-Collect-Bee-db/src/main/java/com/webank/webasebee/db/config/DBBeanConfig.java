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
package com.webank.webasebee.db.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * BeanConfig registers system common beans.
 *
 * @author maojiayu
 * @data Dec 28, 2018 5:07:41 PM
 *
 */
@Configuration
@ComponentScan
@EntityScan(basePackages = { "com.webank.webasebee.db.entity", "com.webank.webasebee.db.generated.entity" })
@EnableJpaRepositories(basePackages = { "com.webank.webasebee.db.repository",
        "com.webank.webasebee.db.generated.repository" })
public class DBBeanConfig {

}
