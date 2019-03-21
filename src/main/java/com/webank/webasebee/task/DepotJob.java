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
package com.webank.webasebee.task;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.bcos.web3j.protocol.core.methods.response.EthBlock.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.webank.webasebee.crawler.service.CommonCrawlerService;
import com.webank.webasebee.crawler.service.SingleBlockCrawlerService;
import com.webank.webasebee.enums.TxInfoStatusEnum;
import com.webank.webasebee.ods.EthClient;
import com.webank.webasebee.sys.db.entity.BlockTaskPool;
import com.webank.webasebee.sys.db.repository.BlockTaskPoolRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * MyDataflowJob
 *
 * @Description: MyDataflowJob
 * @author maojiayu
 * @data Jan 10, 2019 12:03:37 PM
 *
 */
@Service
@ConditionalOnProperty(name = "system.multiLiving", havingValue = "true")
@Slf4j
public class DepotJob implements DataflowJob<Block> {
    @Autowired
    private EthClient ethClient;
    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;
    @Autowired
    private CommonCrawlerService commonCrawlerService;
    @Autowired
    private SingleBlockCrawlerService singleBlockCrawlerService;

    @Override
    public List<Block> fetchData(ShardingContext shardingContext) {
        List<BlockTaskPool> tasks =
                blockTaskPoolRepository.findByStatusModByBlockHeightLimit(shardingContext.getShardingTotalCount(),
                        shardingContext.getShardingItem(), TxInfoStatusEnum.INIT.getStatus(), 1);
        List<Block> result = new ArrayList<>();
        for (BlockTaskPool task : tasks) {
            task.setStatus(TxInfoStatusEnum.DOING.getStatus());
            blockTaskPoolRepository.save(task);
            //rollBackService.rollback(task.getBlockHeight(), task.getBlockHeight() + 1);
            BigInteger bigBlockHeight = new BigInteger(Long.toString(task.getBlockHeight()));
            try {
                Block block = ethClient.getBlock(bigBlockHeight);
                result.add(block);
            } catch (IOException e) {
                log.error("Job  {}, Block {},  exception occur in job processing: {}", shardingContext.getTaskId(),
                        task.getBlockHeight(), e.getMessage());
                blockTaskPoolRepository.setStatusByBlockHeight(TxInfoStatusEnum.INIT.getStatus(),
                        task.getBlockHeight());
            }
        }
        return result;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<Block> data) {
        for (Block b : data) {
            try {
                singleBlockCrawlerService.handleSingleBlock(b);
                blockTaskPoolRepository.setStatusByBlockHeight(TxInfoStatusEnum.DONE.getStatus(),
                        b.getNumber().longValue());
            } catch (IOException e) {
                log.error("Job {}, block {}, exception occur in job processing: {}", shardingContext.getTaskId(),
                        b.getNumber().longValue(), e.getMessage());
                blockTaskPoolRepository.setStatusByBlockHeight(TxInfoStatusEnum.INIT.getStatus(),
                        b.getNumber().longValue());
            }
        }

    }

}