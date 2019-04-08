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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bcos.web3j.protocol.core.methods.response.EthBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.webank.webasebee.constants.BlockForkConstants;
import com.webank.webasebee.dao.BlockDetailInfoDAO;
import com.webank.webasebee.enums.BlockCertaintyEnum;
import com.webank.webasebee.enums.TxInfoStatusEnum;
import com.webank.webasebee.ods.EthClient;
import com.webank.webasebee.sys.db.entity.BlockTaskPool;
import com.webank.webasebee.sys.db.repository.BlockTaskPoolRepository;
import com.webank.webasebee.tools.JacksonUtils;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * BlockTaskPoolService
 *
 * @Description: BlockTaskPoolService
 * @author maojiayu
 * @data Apr 1, 2019 5:08:19 PM
 *
 */
@Service
@Slf4j
public class BlockTaskPoolService {

    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;
    @Autowired
    private BlockDetailInfoDAO blockDetailInfoDAO;
    @Autowired
    private RollBackService rollBackService;
    @Autowired
    private EthClient ethClient;

    public long getTaskPoolHeight() {
        Optional<BlockTaskPool> item = blockTaskPoolRepository.findTopByOrderByBlockHeightDesc();
        long height = 0;
        if (item.isPresent()) {
            height = item.get().getBlockHeight() + 1;
        }
        return height;
    }

    public void prepareTask(long begin, long end) {
        List<BlockTaskPool> list = Lists.newArrayList();
        for (; begin <= end; begin++) {
            BlockTaskPool pool =
                    new BlockTaskPool().setBlockHeight(begin).setSyncStatus(TxInfoStatusEnum.INIT.getStatus());
            if (begin <= end - BlockForkConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER) {
                pool.setCertainty(BlockCertaintyEnum.FIXED.getCertainty());
            } else {
                pool.setCertainty(BlockCertaintyEnum.UNCERTAIN.getCertainty());
            }
            list.add(pool);
        }
        blockTaskPoolRepository.saveAll(list);
    }

    public void processErrors() {
        List<BlockTaskPool> unnormalRecords = blockTaskPoolRepository.findUnNormalRecords();
        if (CollectionUtils.isEmpty(unnormalRecords)) {
            return;
        } else {
            unnormalRecords.parallelStream().map(b -> b.getBlockHeight()).forEach(e -> {
                rollBackService.rollback(e, e + 1);
                blockTaskPoolRepository.setSyncStatusByBlockHeight(TxInfoStatusEnum.INIT.getStatus(), new Date(), e);
            });
        }
    }

    public void checkForks(long currentBlockHeight) throws IOException {
        List<BlockTaskPool> uncertainBlocks =
                blockTaskPoolRepository.findByCertainty(BlockCertaintyEnum.UNCERTAIN.getCertainty());
        for (BlockTaskPool pool : uncertainBlocks) {
            if (pool.getBlockHeight() <= currentBlockHeight - BlockForkConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER) {
                if (pool.getSyncStatus() == TxInfoStatusEnum.DOING.getStatus()) {
                    log.error("block {} is doing!", pool.getBlockHeight());
                    continue;
                }
                if (pool.getSyncStatus() == TxInfoStatusEnum.INIT.getStatus()) {
                    log.error("block {} is not sync!", pool.getBlockHeight());
                    blockTaskPoolRepository.setCertaintyByBlockHeight(BlockCertaintyEnum.FIXED.getCertainty(),
                            pool.getBlockHeight());
                    continue;
                }
                EthBlock.Block block = ethClient.getBlock(BigInteger.valueOf(pool.getBlockHeight()));
                String newHash = block.getHash();
                if (!newHash.equals(
                        blockDetailInfoDAO.getBlockDetailInfoByBlockHeight(pool.getBlockHeight()).getBlockHash())) {
                    log.info("Block {} is forked!!! ready to resync", pool.getBlockHeight());
                    rollBackService.rollback(pool.getBlockHeight(), pool.getBlockHeight() + 1);
                    blockTaskPoolRepository.setSyncStatusAndCertaintyByBlockHeight(TxInfoStatusEnum.INIT.getStatus(),
                            BlockCertaintyEnum.FIXED.getCertainty(), pool.getBlockHeight());
                } else {
                    log.info("Block {} is not forked!", pool.getBlockHeight());
                    blockTaskPoolRepository.setCertaintyByBlockHeight(BlockCertaintyEnum.FIXED.getCertainty(),
                            pool.getBlockHeight());
                }

            }
        }

    }

    public void checkTimeOut() {
        Date offsetDate = DateUtil.offsetSecond(DateUtil.date(), 0 - BlockForkConstants.DEPOT_TIME_OUT);
        List<BlockTaskPool> list = blockTaskPoolRepository
                .findBySyncStatusAndDepotUpdatetimeLessThan(TxInfoStatusEnum.DOING.getStatus(), offsetDate);
        list.forEach(p -> {
            log.info("{}", JacksonUtils.toJson(p));
            log.error("Block {} sync timeout!!, the depot_time is {}, and the threshold time is {}", p.getBlockHeight(),
                    p.getUpdatetime(), offsetDate);
            blockTaskPoolRepository.setSyncStatusByBlockHeight(TxInfoStatusEnum.TIMEOUT.getStatus(), new Date(),
                    p.getBlockHeight());
        });

    }

}
