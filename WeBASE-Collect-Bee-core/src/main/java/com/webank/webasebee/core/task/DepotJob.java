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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.webank.webasebee.common.enums.TxInfoStatusEnum;
import com.webank.webasebee.core.service.BlockDepotService;
import com.webank.webasebee.db.entity.BlockTaskPool;
import com.webank.webasebee.db.repository.BlockTaskPoolRepository;

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
@Slf4j
@ConditionalOnProperty(name = "system.multiLiving", havingValue = "true")
public class DepotJob implements DataflowJob<Block> {

    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;
    @Autowired
    private BlockDepotService blockSyncService;
    @Autowired
    private Web3j web3j;

    /*
     * @see com.dangdang.ddframe.job.api.dataflow.DataflowJob#fetchData(com.dangdang.ddframe.job.api.ShardingContext)
     */
    @Override
    public List<Block> fetchData(ShardingContext shardingContext) {
        List<BlockTaskPool> tasks =
                blockTaskPoolRepository.findBySyncStatusModByBlockHeightLimit(shardingContext.getShardingTotalCount(),
                        shardingContext.getShardingItem(), TxInfoStatusEnum.INIT.getStatus(), 1);
        if (CollectionUtils.isEmpty(tasks)) {
            return new ArrayList<Block>();
        }
        return blockSyncService.getTasks(tasks);
    }

    /*
     * @see com.dangdang.ddframe.job.api.dataflow.DataflowJob#processData(com.dangdang.ddframe.job.api.ShardingContext,
     * java.util.List)
     */
    @Override
    public void processData(ShardingContext shardingContext, List<Block> data) {
        try {
            BigInteger blockNumber = web3j.getBlockNumber().send().getBlockNumber();
            blockSyncService.processDataSequence(data, blockNumber.longValue());
        } catch (IOException e) {
            log.error("Job {}, exception occur in job processing: {}", shardingContext.getTaskId(), e.getMessage());
        }
    }

}