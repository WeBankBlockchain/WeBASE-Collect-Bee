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
package com.webank.webasebee.parser.handler;

import java.sql.Date;

import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.bo.data.BlockDetailInfoBO;
import com.webank.webasebee.common.bo.data.BlockDetailInfoBO.Status;

/**
 * BlockCrawlerHandler is responsible for crawling block info.
 *
 * @Description: BlockCrawlerHandler
 * @author graysonzhang
 * @author maojiayu
 * @data 2018-12-20 14:32:58
 *
 */
@Service
public class BlockCrawlerHandler {

    /**
     * Get block detail info form block object, and insert into db. Block detail info contains block height,
     * transaction's count in current block, block hash and block timestamp.
     * 
     * @param block
     * @param blockHeight
     * @return boolean
     */
    public BlockDetailInfoBO handleBlockDetail(Block block) {
        BlockDetailInfoBO blockDetailInfo = new BlockDetailInfoBO();
        blockDetailInfo.setBlockHeight(block.getNumber().longValue());
        blockDetailInfo.setTxCount(block.getTransactions().size());
        blockDetailInfo.setBlockHash(block.getHash());
        blockDetailInfo.setBlockTimeStamp(new Date(block.getTimestamp().longValue()));
        blockDetailInfo.setStatus(Status.COMPLETED);
        return blockDetailInfo;
    }
}
