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
import java.util.stream.LongStream;
import org.bcos.web3j.protocol.Web3j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import com.webank.webasebee.config.SystemEnvironmentConfig;
import com.webank.webasebee.dao.BlockDetailInfoDAO;
import com.webank.webasebee.dao.BlockInfoDAO;
import com.webank.webasebee.enums.TxInfoStatusEnum;
import com.webank.webasebee.sys.db.entity.BlockInfo;

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
    private BlockInfoDAO blockInfoDao;
    @Autowired
    private BlockDetailInfoDAO blockDetailInfoDao;
    @Autowired
    private SystemEnvironmentConfig systemEnvironmentConfig;
    @Autowired
    private RollBackService rollBackService;
    @Autowired
    private SingleBlockCrawlerService singleBlockCrawlerService;

    /**
     * The key driving entrance of depot. 1. get block info; 2. execute rollback; 3. decide to enter multi-thread mode;
     * 4. do block handle; 5. update tx info; 6. continue...
     * 
     */
    public void handle() {
        try {
            if (systemEnvironmentConfig.getMaxScheduleThreadNo() > 0) {
                log.info("MaxScheduleThreadNo {}", systemEnvironmentConfig.getMaxScheduleThreadNo());
                System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
                        systemEnvironmentConfig.getMaxScheduleThreadNo() + "");
                System.out.println(System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism"));
            }
            BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            long total = blockNumber.longValue();
            log.info("Current chain block number is:{}", blockNumber);
            BlockInfo blockInfo = blockInfoDao.getBlockInfo();
            long height = rollBackService.processRollback(blockInfo);
            while (true) {
                log.info(
                        "Current blockNumber is {}, now height to depot is {}, and the max block height threshold is {}.",
                        total, height, systemEnvironmentConfig.getMaxBlockHeightThreshold());
                if (height + systemEnvironmentConfig.getMaxBlockHeightThreshold() < total) {
                    for (long beginIndex = height; beginIndex
                            + systemEnvironmentConfig.getCrawlBatchUnit() <= total; beginIndex +=
                                    systemEnvironmentConfig.getCrawlBatchUnit()) {
                        log.info("begin to start Multi-thread system, start: {}, end: {}", height, total);
                        long tmpUpper = beginIndex + systemEnvironmentConfig.getCrawlBatchUnit();
                        long maxBlockNumber = tmpUpper <= total ? tmpUpper : total;
                        LongStream.range(beginIndex, maxBlockNumber).parallel().forEach(t -> {
                            try {
                                singleBlockCrawlerService.handleSingleBlock(t);
                            } catch (IOException e) {
                                log.error("handle error IOException, {}", e.getMessage());
                            }
                        });
                        height = beginIndex + systemEnvironmentConfig.getCrawlBatchUnit();
                        long tempTxCount = blockDetailInfoDao.sumByTxCountBetweens(0, height);
                        blockInfo.setTxCount((long) tempTxCount).setStatus(TxInfoStatusEnum.DONE.getStatus())
                                .setCurrentBlockHeight(height);
                        blockInfoDao.save(blockInfo);
                    }
                }
                // single thread process
                while (height <= total) {
                    log.info("back to checkoout single-thread system, start: {}", height);
                    singleBlockCrawlerService.handleSingleBlock(height);
                    blockInfo.setCurrentBlockHeight(height)
                            .setTxCount(blockDetailInfoDao.sumByTxCountBetweens(0, height + 1))
                            .setStatus(TxInfoStatusEnum.DONE.getStatus());
                    blockInfoDao.save(blockInfo);
                    height++;
                }
                // single circle sleep time is read from the application.properties
                Thread.sleep(systemEnvironmentConfig.getFrequency() * 1000);
                blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
                total = blockNumber.longValue();
            }
        } catch (IOException e) {
            log.error("depot IOError, {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("depot InterruptedException, {}", e.getMessage());
        }
    }

}
