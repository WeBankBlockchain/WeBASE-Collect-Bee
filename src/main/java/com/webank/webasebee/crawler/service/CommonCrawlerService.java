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
package com.webank.webasebee.crawler.service;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.webank.webasebee.config.SystemEnvironmentConfig;
import com.webank.webasebee.constants.BlockForkConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * CommonCrawlerService is the main control process. The driven method is handle().
 *
 * @Description: CommonCrawlerService
 * @author maojiayu
 * @data Dec 28, 2018 5:25:35 PM
 *
 */
@Service
@Slf4j
@EnableScheduling
public class CommonCrawlerService {

    @Autowired
    private Web3j web3j;
    @Autowired
    private SystemEnvironmentConfig systemEnvironmentConfig;
    @Autowired
    private BlockTaskPoolService blockTaskPoolService;
    @Autowired
    private BlockSyncService blockSyncService;

    @Autowired
    private BlockIndexService blockIndexService;

    private long startBlockNumber;

    @PostConstruct
    public void setStartBlockNumber() throws ParseException, IOException, InterruptedException {
        startBlockNumber = blockIndexService.getStartBlockIndex();
        log.info("Start succeed, and the block number is {}", startBlockNumber);
    }

    /**
     * The key driving entrance of single instance depot: 1. check timeout txs and process errors; 2. produce tasks; 3.
     * consume tasks; 4. check the fork status; 5. rollback; 6. continue and circle;
     * 
     */
    public void handle() {
        try {
            while (true) {
                long total = getCurrentBlockHeight();
                long height = blockTaskPoolService.getTaskPoolHeight();
                height = height > startBlockNumber ? height : startBlockNumber;
                log.info(
                        "Current blockNumber is {}, now height to depot is {}, and the max block height threshold is {}.",
                        total, height, systemEnvironmentConfig.getMaxBlockHeightThreshold());
                blockTaskPoolService.checkTimeOut();
                blockTaskPoolService.processErrors();
                // control the batch unit number
                long end = height + systemEnvironmentConfig.getCrawlBatchUnit() - 1;
                long batchNo = total < end ? total : end;
                boolean certainty = end + 1 < total - BlockForkConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER;
                blockTaskPoolService.prepareTask(height, batchNo, certainty);
                List<Block> taskList = blockSyncService.fetchData(systemEnvironmentConfig.getCrawlBatchUnit());
                while (!CollectionUtils.isEmpty(taskList)) {
                    if (taskList.size() < systemEnvironmentConfig.getCrawlBatchUnit()) {
                        blockSyncService.processDataSequence(taskList);
                    } else {
                        blockSyncService.processDataParallel(taskList);
                    }
                    taskList = blockSyncService.fetchData(systemEnvironmentConfig.getCrawlBatchUnit());
                }
                // single circle sleep time is read from the application.properties
                Thread.sleep(systemEnvironmentConfig.getFrequency() * 1000);
                total = getCurrentBlockHeight();
                if (!certainty) {
                    blockTaskPoolService.checkForks(total);
                }
            }
        } catch (IOException e) {
            log.error("depot IOError, {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("depot InterruptedException, {}", e.getMessage());
        }
    }

    public long getCurrentBlockHeight() throws IOException {
        BigInteger blockNumber = web3j.getBlockNumber().send().getBlockNumber();
        long total = blockNumber.longValue();
        log.info("Current chain block number is:{}", blockNumber);
        return total;
    }

}
