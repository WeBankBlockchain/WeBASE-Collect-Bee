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
package com.webank.webasebee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.druid.util.StringUtils;
import com.webank.webasebee.tools.PropertiesUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * webasebeeApplication connect to fisco-bcos, and depot its datas.
 *
 * @Description: webase-bee project is a java project created by webase-monkey project, that using for crawling data from
 *               fisco-bcos netwwork, including block data，event data, transaction data and account data.
 * @author maojiayu
 * @data Dec 28, 2018 4:15:20 PM
 *
 */
@SpringBootApplication
@Slf4j
public class WebasebeeApplication {

    public static void main(String[] args) {
        String maxThreadNo = PropertiesUtils.getProperty("system.maxScheduleThreadNo");
        if (StringUtils.isNumber(maxThreadNo)) {
            int no = Integer.parseInt(maxThreadNo);
            if (no > 0) {
                System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", no + "");
                log.info("parallelism: {}", System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism"));
            }
        }
        SpringApplication.run(WebasebeeApplication.class, args);
    }
}
