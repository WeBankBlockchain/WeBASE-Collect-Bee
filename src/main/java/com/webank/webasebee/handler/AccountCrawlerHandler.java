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

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Map.Entry;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.methods.response.Transaction;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import com.webank.webasebee.dao.AccountInfoDAO;
import com.webank.webasebee.service.ContractConstructorService;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * AccountCrawlerHandler is responsible for crawling account info.
 *
 * @author graysonzhang
 * @data 2018-12-19 18:27:47
 *
 */
@Service
@EnableScheduling
@Slf4j
public class AccountCrawlerHandler {

    /** @Fields web3j : web3j */
    @Autowired
    private Web3j web3j;

    /** @Fields contractConstructorService : contract constructor service */
    @Autowired
    private ContractConstructorService contractConstructorService;

    /** @Fields accountInfoDAO : account info data access object */
    @Autowired
    private AccountInfoDAO accountInfoDAO;

    /**
     * Get transaction from TransactionReceipt object firstly, then will get transaction input param, and get
     * constructor function transaction by judging if transaction's param named to is null, parsing transaction data and
     * save into database.
     * 
     * @param receipt: TransactionReceipt
     * @param blockTimeStamp: block timestamp
     * @return void
     */
    public void handle(TransactionReceipt receipt, BigInteger blockTimeStamp) {
        Optional<Transaction> optt;
        try {
            optt = web3j.ethGetTransactionByHash(receipt.getTransactionHash()).send().getTransaction();
            if (optt.isPresent()) {
                Transaction transaction = optt.get();
                String input = transaction.getInput();
                // get constructor function transaction by judging if transaction's param named to is null
                if (transaction.getTo() == null) {
                    Entry<String, String> entry = contractConstructorService.getConstructorNameByBinary(input);
                    if (entry == null) {
                        log.info("block:{} constructor binary can't find!", receipt.getBlockNumber().longValue());
                        return;
                    }
                    accountInfoDAO.save(receipt, blockTimeStamp, entry.getValue());
                }
            }
        } catch (IOException e1) {
            log.error("MethodCrawlerHandler Error: {}", e1.getMessage());
        }
    }

}
