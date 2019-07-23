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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.TransactionResult;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosTransactionReceipt;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.bo.contract.ContractMapsInfo;
import com.webank.webasebee.common.bo.data.AccountInfoBO;
import com.webank.webasebee.common.bo.data.BlockAccountsInfoBO;
import com.webank.webasebee.common.constants.ContractConstants;
import com.webank.webasebee.extractor.ods.EthClient;
import com.webank.webasebee.parser.service.ContractConstructorService;
import com.webank.webasebee.parser.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * AccountCrawlerHandler is responsible for crawling account info.
 *
 * @author graysonzhang
 * @author maojiayu
 * @data 2018-12-19 18:27:47
 *
 */
@Service
@EnableScheduling
@Slf4j
public class AccountCrawlerHandler {

    @Autowired
    private EthClient ethClient;

    /** @Fields contractConstructorService : contract constructor service */
    @Autowired
    private ContractConstructorService contractConstructorService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ContractMapsInfo contractMapsInfo;

    @SuppressWarnings("rawtypes")
    public BlockAccountsInfoBO crawl(Block block) throws IOException {
        List<AccountInfoBO> accountInfoList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        List<TransactionResult> transactionResults = block.getTransactions();
        for (TransactionResult result : transactionResults) {
            BcosTransactionReceipt bcosTransactionReceipt = ethClient.getTransactionReceipt(result);
            Optional<TransactionReceipt> opt = bcosTransactionReceipt.getTransactionReceipt();
            if (opt.isPresent()) {
                TransactionReceipt tr = opt.get();
                handle(tr, block.getTimestamp()).ifPresent(e -> {
                    accountInfoList.add(e);
                    map.putIfAbsent(e.getTxHash(), e.getContractAddress());
                });

            }
        }
        return new BlockAccountsInfoBO(accountInfoList, map);
    }

    /**
     * Get transaction from TransactionReceipt object firstly, then will get transaction input param, and get
     * constructor function transaction by judging if transaction's param named to is null, parsing transaction data and
     * save into database.
     * 
     * throw the IOException.
     * 
     * @param receipt: TransactionReceipt
     * @param blockTimeStamp: block timestamp
     * @return void
     * @throws IOException
     */
    public Optional<AccountInfoBO> handle(TransactionReceipt receipt, BigInteger blockTimeStamp) throws IOException {
        Optional<Transaction> optt = ethClient.getTransactionByHash(receipt);
        if (optt.isPresent()) {
            Transaction transaction = optt.get();
            // get constructor function transaction by judging if transaction's param named to is null
            if (transaction.getTo() == null || transaction.getTo().equals(ContractConstants.EMPTY_ADDRESS)) {
                String contractAddress = receipt.getContractAddress();
                String input = ethClient.getCodeByContractAddress(contractAddress);
                log.debug("blockNumber: {}, input: {}", receipt.getBlockNumber(), input);
                Entry<String, String> entry = contractConstructorService.getConstructorNameByCode(input);
                if (entry == null) {
                    log.info("block:{} constructor binary can't find!", receipt.getBlockNumber().longValue());
                    return Optional.empty();
                }
                AccountInfoBO accountInfo = new AccountInfoBO();
                accountInfo.setBlockTimeStamp(new Date(blockTimeStamp.longValue()))
                        .setBlockHeight(receipt.getBlockNumber().longValue())
                        .setContractAddress(receipt.getContractAddress()).setContractName(entry.getValue())
                        .setTxHash(receipt.getTransactionHash());
                return Optional.of(accountInfo);
            }
        }
        return Optional.empty();

    }

}
