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

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.webank.webasebee.constants.BlockForkConstants;
import com.webank.webasebee.enums.BlockCertaintyEnum;
import com.webank.webasebee.enums.TxInfoStatusEnum;
import com.webank.webasebee.sys.db.entity.BlockTaskPool;
import com.webank.webasebee.sys.db.repository.BlockTaskPoolRepository;

/**
 * BlockTaskPoolService
 *
 * @Description: BlockTaskPoolService
 * @author maojiayu
 * @data Apr 1, 2019 5:08:19 PM
 *
 */
@Service
public class BlockTaskPoolService {

    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;
    @Autowired
    private RollBackService rollBackService;

    public long getTaskPoolHeight() {
        Optional<BlockTaskPool> item = blockTaskPoolRepository.findTopByOrderByBlockHeightDesc();
        long height = 0;
        if (item.isPresent()) {
            height = item.get().getBlockHeight() + 1;
        }
        return height;
    }

    public void prepareTask(long begin, long end) {
        for (; begin <= end; begin++) {
            BlockTaskPool pool =
                    new BlockTaskPool().setBlockHeight(begin).setSyncStatus(TxInfoStatusEnum.INIT.getStatus());
            if (begin <= end - BlockForkConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER) {
                pool.setCertainty(BlockCertaintyEnum.FIXED.getCertainty());
            } else {
                pool.setCertainty(BlockCertaintyEnum.UNCERTAIN.getCertainty());
            }
            blockTaskPoolRepository.save(pool);
        }
    }

    public void processErrors() {
        List<BlockTaskPool> unnormalRecords = blockTaskPoolRepository.findUnNormalRecords();
        if (CollectionUtils.isEmpty(unnormalRecords)) {
            return;
        } else {
            unnormalRecords.parallelStream().map(b -> b.getBlockHeight()).forEach(e -> {
                rollBackService.rollback(e, e + 1);
                blockTaskPoolRepository.setSyncStatusByBlockHeight(TxInfoStatusEnum.INIT.getStatus(), e);
            });
        }
    }

}
