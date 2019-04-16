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
package com.webank.webasebee.crawler.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.TransactionResult;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosTransactionReceipt;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.webank.webasebee.handler.AccountCrawlerHandler;
import com.webank.webasebee.handler.BlockCrawlerHandler;
import com.webank.webasebee.handler.EventCrawlerHandler;
import com.webank.webasebee.handler.MethodCrawlerHandler;
import com.webank.webasebee.ods.EthClient;

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
public class SingleBlockCrawlerService {

    @Autowired
    private Web3j web3j;
    @Autowired
    private BlockCrawlerHandler blockCrawlerHandler;
    @Autowired
    private EthClient ethClient;
    @Autowired
    private EventCrawlerHandler eventCrawlerHandler;
    @Autowired
    private MethodCrawlerHandler methodCrawlerHandler;
    @Autowired
    private AccountCrawlerHandler accountCrawlerHandler;

    /**
     * handle a single block: 1. download a new block. 2. handle event. 3. handle method. 4. handle account. 5. insert
     * block detail.
     * 
     * @param blockHeight
     * @return transaction size.
     * @throws IOException
     */
    public long handleSingleBlock(long blockHeight) throws IOException {
        BigInteger bigBlockHeight = new BigInteger(Long.toString(blockHeight));
        Block block = ethClient.getBlock(bigBlockHeight);
        return handleSingleBlock(block);
    }

    public long handleSingleBlock(Block block) throws IOException {
        Stopwatch st1 = Stopwatch.createStarted();
        log.info("Begin to sync block {}", block.getNumber().longValue());
        List<TransactionResult> transactionResults = block.getTransactions();
        for (TransactionResult result : transactionResults) {
            BcosTransactionReceipt bcosTransactionReceipt = web3j.getTransactionReceipt((String) result.get()).send();
            Optional<TransactionReceipt> opt = bcosTransactionReceipt.getTransactionReceipt();
            if (opt.isPresent()) {
                TransactionReceipt tr = opt.get();

                // crawler block event data
                eventCrawlerHandler.handle(tr, block.getTimestamp());

                // crawler transaction data
                methodCrawlerHandler.handle(tr, block.getTimestamp());

                // crawler account data
                accountCrawlerHandler.handle(tr, block.getTimestamp());
            }
        }

        log.info("bcosCrawlerMap block:{} succeed, bcosCrawlerMap.handleReceipt useTime: {}",
                block.getNumber().longValue(), st1.stop().elapsed(TimeUnit.MILLISECONDS));
        blockCrawlerHandler.handleBlockDetail(block, block.getNumber().longValue());
        return transactionResults.size();
    }

}
