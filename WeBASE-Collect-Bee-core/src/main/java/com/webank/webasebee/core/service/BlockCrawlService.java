/*
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
package com.webank.webasebee.core.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.webank.webasebee.common.bo.data.BlockInfoBO;
import com.webank.webasebee.common.tools.JacksonUtils;
import com.webank.webasebee.extractor.ods.EthClient;
import com.webank.webasebee.parser.facade.ParseInterface;

import lombok.extern.slf4j.Slf4j;

/**
 * SingleBlockCrawlerService
 *
 * @Description: SingleBlockCrawlerService
 * @author maojiayu
 * @author graysonzhang
 * @data 2019-03-21 15:00:30
 *
 */
@Service
@Slf4j
public class BlockCrawlService {
    @Autowired
    private EthClient ethClient;
    @Autowired
    private ParseInterface parser;

    /**
     * handle a single block: 1. download a new block. 2. handle event. 3. handle method. 4. handle account. 5. insert
     * block detail.
     * 
     * @param blockHeight
     * @return transaction size.
     * @throws IOException
     */
    public void handleSingleBlock(long blockHeight) throws IOException {
        BigInteger bigBlockHeight = new BigInteger(Long.toString(blockHeight));
        Block block = ethClient.getBlock(bigBlockHeight);
        handleSingleBlock(block);
    }

    public void handleSingleBlock(Block block) throws IOException {
        Stopwatch st1 = Stopwatch.createStarted();
        BlockInfoBO blockInfo = parser.parse(block);
        System.out.println(JacksonUtils.toJson(blockInfo));
        log.info("bcosCrawlerMap block:{} succeed, bcosCrawlerMap.handleReceipt useTime: {}",
                block.getNumber().longValue(), st1.stop().elapsed(TimeUnit.MILLISECONDS));
    }

}
