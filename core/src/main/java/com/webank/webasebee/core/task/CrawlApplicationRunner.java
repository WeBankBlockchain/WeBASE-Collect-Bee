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
package com.webank.webasebee.core.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.webank.webasebee.core.config.SystemEnvironmentConfig;
import com.webank.webasebee.core.crawler.service.CommonCrawlerService;

import lombok.extern.slf4j.Slf4j;

/**
 * GenerateCodeApplicationRunner
 *
 * @Description: GenerateCodeApplicationRunner
 * @author maojiayu
 * @date 2018年11月29日 下午4:37:38
 * 
 */

@Component
@Order(value = 1)
@ConditionalOnProperty(name = "system.multiLiving", havingValue = "false")
@Slf4j
public class CrawlApplicationRunner implements ApplicationRunner {
    @Autowired
    private CommonCrawlerService commonCrawlerService;
    @Autowired
    private SystemEnvironmentConfig systemEnvironmentConfig;

    @Override
    public void run(ApplicationArguments var1) throws InterruptedException {
        if (systemEnvironmentConfig.getCrawlBatchUnit() < 1) {
            log.error("The batch unit threshold can't be less than 1!!");
            System.exit(1);
        }
        commonCrawlerService.handle();
    }
}