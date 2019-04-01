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

import java.util.List;

import org.bcos.web3j.protocol.core.methods.response.EthBlock.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.webank.webasebee.crawler.service.BlockSyncService;
import com.webank.webasebee.enums.TxInfoStatusEnum;
import com.webank.webasebee.sys.db.entity.BlockTaskPool;
import com.webank.webasebee.sys.db.repository.BlockTaskPoolRepository;

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
public class DepotJob implements DataflowJob<Block> {

    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;

    @Autowired
    private BlockSyncService blockSyncService;

    @Override
    public List<Block> fetchData(ShardingContext shardingContext) {
        List<BlockTaskPool> tasks =
                blockTaskPoolRepository.findBySyncStatusModByBlockHeightLimit(shardingContext.getShardingTotalCount(),
                        shardingContext.getShardingItem(), TxInfoStatusEnum.INIT.getStatus(), 1);
        return blockSyncService.getTasks(tasks);
    }

    @Override
    public void processData(ShardingContext shardingContext, List<Block> data) {
        blockSyncService.processDataSequence(data);
    }

}