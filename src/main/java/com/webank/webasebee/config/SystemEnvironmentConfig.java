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
package com.webank.webasebee.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * System Environment Config.
 *
 * @Description: SystemEnvironmentConfig
 * @author maojiayu
 * @data Dec 28, 2018 5:21:48 PM
 *
 */
@Configuration
@ConfigurationProperties("system")
@Data
public class SystemEnvironmentConfig {
    private int orgId;
    private String nodeStr;

    private String configPath;
    private String keystorePassword;
    private String clientCertPassword;

    private String privateKey;
    private int maxBlockHeightThreshold;
    private int crawlBatchUnit;

    private boolean multiLiving;
    private String zookeeperServiceLists;
    private String zookeeperNamespace;
    private String elasticJobName;
    private String elasticJobcron;
    private int elasticJobshardingTotalCount;

    private long frequency;
    private String contractPackName;
    private String group;
    private String baseProjectPath;
    private String projectName;
    private String contractPath;

    private long startBlockHeight;
    private String startDate;

}
