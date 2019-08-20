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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.TransactionResult;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosTransactionReceipt;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.bo.contract.MethodMetaInfo;
import com.webank.webasebee.common.bo.data.BlockMethodInfo;
import com.webank.webasebee.common.bo.data.BlockTxDetailInfoBO;
import com.webank.webasebee.common.bo.data.MethodBO;
import com.webank.webasebee.extractor.ods.EthClient;
import com.webank.webasebee.parser.service.MethodCrawlService;
import com.webank.webasebee.parser.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

/**
 * MethodCrawlerHandler
 *
 * @Description: MethodCrawlerHandler
 * @author maojiayu
 * @data Jul 3, 2019 10:17:15 AM
 *
 */
@Slf4j
@Service
public class MethodCrawlerHandler {
    @Autowired
    private EthClient ethClient;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MethodCrawlService methodCrawlService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BlockMethodInfo crawl(Block block, Map<String, String> txHashContractAddressMapping) throws IOException {
        BlockMethodInfo blockMethodInfo = new BlockMethodInfo();
        List<BlockTxDetailInfoBO> blockTxDetailInfoList = new ArrayList<>();
        List<MethodBO> methodInfoList = new ArrayList();
        List<TransactionResult> transactionResults = block.getTransactions();
        Map<String, String> txHashContractNameMapping = new HashMap<>();
        for (TransactionResult result : transactionResults) {
            BcosTransactionReceipt bcosTransactionReceipt = ethClient.getTransactionReceipt(result);
            Optional<TransactionReceipt> opt = bcosTransactionReceipt.getTransactionReceipt();
            if (opt.isPresent()) {
                TransactionReceipt receipt = opt.get();
                Optional<Transaction> optt = ethClient.getTransactionByHash(receipt);
                if (optt.isPresent()) {
                    Transaction transaction = optt.get();
                    Optional<Entry<String, String>> optional =
                            transactionService.getContractNameByTransaction(transaction, txHashContractAddressMapping);
                    if (!optional.isPresent()) {
                        continue;
                    }
                    Entry<String, String> contractEntry = optional.get();
                    MethodMetaInfo methodMetaInfo =
                            transactionService.getMethodMetaInfo(transaction, contractEntry.getValue());
                    if (methodMetaInfo == null) {
                        continue;
                    }
                    // get block tx detail info
                    BlockTxDetailInfoBO blockTxDetailInfo =
                            getBlockTxDetailInfo(block, transaction, receipt, methodMetaInfo);
                    blockTxDetailInfoList.add(blockTxDetailInfo);
                    txHashContractNameMapping.putIfAbsent(blockTxDetailInfo.getTxHash(),
                            blockTxDetailInfo.getContractName());
                    if (!methodCrawlService
                            .getMethodCrawler(
                                    StringUtils.uncapitalize(methodMetaInfo.getMethodName()) + "MethodCrawlerImpl")
                            .isPresent()) {
                        log.info("The methodName {} doesn't exist or is constant, please check it !",
                                methodMetaInfo.getMethodName());
                        continue;
                    }
                    // get method bo
                    methodInfoList.add(methodCrawlService
                            .getMethodCrawler(
                                    StringUtils.uncapitalize(methodMetaInfo.getMethodName()) + "MethodCrawlerImpl")
                            .get()
                            .transactionHandler(transaction, block.getTimestamp(), contractEntry,
                                    methodMetaInfo.getMethodName(), txHashContractAddressMapping)
                            .setMethodStatus(receipt.getStatus()));
                }
            }
        }
        blockMethodInfo.setBlockTxDetailInfoList(blockTxDetailInfoList).setMethodInfoList(methodInfoList)
                .setTxHashContractNameMapping(txHashContractNameMapping);
        return blockMethodInfo;

    }

    public BlockTxDetailInfoBO getBlockTxDetailInfo(Block block, Transaction transaction, TransactionReceipt receipt,
            MethodMetaInfo methodMetaInfo) {
        BlockTxDetailInfoBO blockTxDetailInfo = new BlockTxDetailInfoBO();
        blockTxDetailInfo.setBlockHash(receipt.getBlockHash()).setBlockHeight(receipt.getBlockNumber().longValue())
                .setContractName(methodMetaInfo.getContractName())
                .setMethodName(methodMetaInfo.getMethodName().substring(methodMetaInfo.getContractName().length()))
                .setTxFrom(transaction.getFrom()).setTxTo(transaction.getTo()).setTxHash(receipt.getTransactionHash())
                .setBlockTimeStamp(new Date(block.getTimestamp().longValue()));
        return blockTxDetailInfo;
    }

}
