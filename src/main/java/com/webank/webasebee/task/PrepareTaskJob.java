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

import org.bcos.web3j.protocol.Web3j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.webank.webasebee.enums.TxInfoStatusEnum;
import com.webank.webasebee.sys.db.entity.BlockTaskPool;
import com.webank.webasebee.sys.db.repository.BlockTaskPoolRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * PrepareTaskJob
 *
 * @Description: PrepareTaskJob
 * @author maojiayu
 * @data Jan 11, 2019 10:03:29 AM
 *
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "system.multiLiving", havingValue = "true")
public class PrepareTaskJob implements SimpleJob {
    @Autowired
    private Web3j web3j;
    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            long total = blockNumber.longValue();
            log.info("Current chain block number is:{}", total);
            BlockTaskPool item = blockTaskPoolRepository.findTopByOrderByBlockHeightDesc();
            long height = 0;
            if (item != null) {
                height = item.getBlockHeight() + 1;
            }
            for (; height <= total; height++) {
                BlockTaskPool pool = new BlockTaskPool().setBlockHeight(height)
                        .setStatus(TxInfoStatusEnum.INIT.getStatus()).setHandleItem(shardingContext.getShardingItem());
                blockTaskPoolRepository.save(pool);
            }
        } catch (IOException e) {
            log.error("Job {}, exception occur in job processing: {}", shardingContext.getTaskId(), e.getMessage());

        }

    }

}
