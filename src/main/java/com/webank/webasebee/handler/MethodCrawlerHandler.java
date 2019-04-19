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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.methods.response.Transaction;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.webank.webasebee.bo.ContractMapsInfo;
import com.webank.webasebee.crawler.face.BcosMethodCrawlerInterface;
import com.webank.webasebee.dao.BlockTxDetailInfoDAO;
import com.webank.webasebee.service.ContractConstructorService;
import com.webank.webasebee.tools.NameValueVO;

import lombok.extern.slf4j.Slf4j;

/**
 * MethodCrawlerHandler is responsible for crawling transaction's info.
 *
 * @Description: MethodCrawlerHandler
 * @author graysonzhang
 * @author maojiayu
 * @data 2018-12-5 11:43:30
 *
 */
@Service
@EnableScheduling
@Slf4j
public class MethodCrawlerHandler {

    /** @Fields web3j : web3j */
    @Autowired
    private Web3j web3j;

    /** @Fields bcosMethodCrawlerMap : method crawler map for crawling method data */
    @Autowired
    private Map<String, BcosMethodCrawlerInterface> bcosMethodCrawlerMap;

    /** @Fields contractConstructorService : contract constructor service */
    @Autowired
    private ContractConstructorService contractConstructorService;

    /** @Fields contractMapsInfo : contract maps infp */
    @Autowired
    private ContractMapsInfo contractMapsInfo;

    /** @Fields blockTxDetailInfoDAO : block transaction detail info data access object */
    @Autowired
    private BlockTxDetailInfoDAO blockTxDetailInfoDAO;

    /**
     * Get transaction from TransactionReceipt object firstly, then will get transaction input param, if transaction's
     * param named to is null, get contract name and method name by contractConstructorService else transaction is
     * normal transaction, get contract name and method name by MehodIdMap that contains the current transaction
     * methodId, then parsing transaction by method crawler that correspond with current transaction, and save the
     * transaction data into database.
     * 
     * throw the IOException.
     * 
     * @param receipt: TransactionReceipt
     * @param blockTimeStamp: block timestamp
     * @return void
     * @throws IOException
     */
    public void handle(TransactionReceipt receipt, BigInteger blockTimeStamp) throws IOException {
        Optional<Transaction> optt;
        optt = web3j.ethGetTransactionByHash(receipt.getTransactionHash()).send().getTransaction();
        if (optt.isPresent()) {
            Transaction transaction = optt.get();
            String input = transaction.getInput();
            String methodId = input.substring(0, 10);
            String methodName = null;
            String contractName = null;
            Entry<String, String> entry = null;
            if (transaction.getTo() == null) {
                entry = contractConstructorService.getConstructorNameByBinary(input);
                if (entry == null) {
                    log.info("block:{} constructor binary can't find!", transaction.getBlockNumber().longValue());
                    return;
                }
                contractName = entry.getValue();
                methodName = contractName + contractName;
            } else if (input != null && contractMapsInfo.getMethodIdMap().containsKey(methodId)) {
                NameValueVO<String> nameValue = contractMapsInfo.getMethodIdMap().get(methodId);
                contractName = nameValue.getName();
                methodName = nameValue.getValue();
            } else {
                return;
            }
            blockTxDetailInfoDAO.save(receipt, blockTimeStamp, contractName, methodName);

            if (!bcosMethodCrawlerMap.containsKey(StringUtils.uncapitalize(methodName) + "MethodCrawlerImpl")) {
                log.error("The methodName {} doesn't exist, please check it !", methodName);
                return;
            }
            bcosMethodCrawlerMap.get(StringUtils.uncapitalize(methodName) + "MethodCrawlerImpl")
                    .transactionHandler(transaction, blockTimeStamp, entry, methodName);

        }
    }
}
