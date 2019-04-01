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
package com.webank.webasebee.handler;

import java.sql.Date;

import org.bcos.web3j.protocol.core.methods.response.EthBlock.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.webank.webasebee.dao.BaseDAO;
import com.webank.webasebee.sys.db.entity.BlockDetailInfo;
import com.webank.webasebee.sys.db.repository.BlockDetailInfoRepository;

/**
 * BlockCrawlerHandler is responsible for crawling block info.
 *
 * @Description: BlockCrawlerHandler
 * @author graysonzhang
 * @data 2018-12-20 14:32:58
 *
 */
@Service
@EnableScheduling
public class BlockCrawlerHandler {

    /** @Fields blockDetailInfoRepository : block detail info repository */
    @Autowired
    private BlockDetailInfoRepository blockDetailInfoRepository;

    /**
     * Get block detail info form block object, and insert into db. Block detail info contains block height,
     * transaction's count in current block, block hash and block timestamp.
     * 
     * @param block
     * @param blockHeight
     * @return boolean
     */
    public boolean handleBlockDetail(Block block, long blockHeight) {
        BlockDetailInfo blockDetailInfo = new BlockDetailInfo();
        blockDetailInfo = new BlockDetailInfo();
        blockDetailInfo.setBlockHeight(blockHeight);
        blockDetailInfo.setTxCount(block.getTransactions().size());
        blockDetailInfo.setBlockHash(block.getHash());
        blockDetailInfo.setBlockTimeStamp(new Date(block.getTimestamp().longValue()));
        BaseDAO.saveWithTimeLog(blockDetailInfoRepository, blockDetailInfo);
        return true;
    }
}
