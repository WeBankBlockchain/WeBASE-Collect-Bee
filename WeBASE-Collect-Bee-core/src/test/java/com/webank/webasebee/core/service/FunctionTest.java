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
package com.webank.webasebee.core.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fisco.bcos.web3j.abi.FunctionReturnDecoder;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.TransactionResult;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosTransactionReceipt;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webank.webasebee.common.tools.AbiTypeRefUtils;
import com.webank.webasebee.common.tools.JacksonUtils;
import com.webank.webasebee.core.BaseTest;
import com.webank.webasebee.core.config.SystemEnvironmentConfig;
import com.webank.webasebee.core.parser.ContractParser;
import com.webank.webasebee.extractor.ods.EthClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * FunctionTest
 *
 * @Description: FunctionTest
 * @author graysonzhang
 * @data 2018-12-04 15:15:32
 *
 */
@Slf4j
public class FunctionTest extends BaseTest {

    @Autowired
    private SystemEnvironmentConfig systemEnvironmentConfig;
    @Autowired
    private EthClient ethClient;

    @Autowired
    private Web3j web3j;

    @Autowired
    private ContractParser factory;

    @Test
    public void testInput() throws IOException {
        BigInteger bigBlockHeight = new BigInteger(Integer.toString(8));
        Block block = ethClient.getBlock(bigBlockHeight);
        List<TransactionResult> transactionResults = block.getTransactions();
        log.info("transactionResults.size:{}", transactionResults.size());
        for (TransactionResult result : transactionResults) {
            BcosTransactionReceipt ethGetTransactionReceipt = web3j.getTransactionReceipt((String) result.get()).send();
            Optional<TransactionReceipt> opt = ethGetTransactionReceipt.getTransactionReceipt();
            if (opt.isPresent()) {
                log.info("TransactionReceipt hash: {}", opt.get().getTransactionHash());
                Optional<Transaction> optt =
                        web3j.getTransactionByHash(opt.get().getTransactionHash()).send().getTransaction();
                if (optt.isPresent()) {
                    String rawInput = optt.get().getInput();

                    log.info("input : {}", optt.get().getInput());
                    List<TypeReference<Type>> referencesTypeList = new ArrayList<>(1);
                    TypeReference exScore = AbiTypeRefUtils.getTypeRef("uint128");
                    referencesTypeList.add(exScore);
                    TypeReference operationType = AbiTypeRefUtils.getTypeRef("uint8");
                    referencesTypeList.add(operationType);

                    List<Type> listT = FunctionReturnDecoder.decode(rawInput.substring(10), referencesTypeList);
                    for (Type type : listT) {
                        log.info("type value : {}", type.getValue());
                    }
                    log.info("type info : {}", JacksonUtils.toJson(listT));
                }
            }
        }
    }

    // @Test
    public void testActivity() throws IOException {
        BigInteger bigBlockHeight = new BigInteger(Integer.toString(200));
        Block block = ethClient.getBlock(bigBlockHeight);
        List<TransactionResult> transactionResults = block.getTransactions();
        log.info("transactionResults.size:{}", transactionResults.size());
        for (TransactionResult result : transactionResults) {
            BcosTransactionReceipt ethGetTransactionReceipt = web3j.getTransactionReceipt((String) result.get()).send();
            Optional<TransactionReceipt> opt = ethGetTransactionReceipt.getTransactionReceipt();
            if (opt.isPresent()) {
                log.info("TransactionReceipt hash: {}", opt.get().getTransactionHash());
                Optional<Transaction> optt =
                        web3j.getTransactionByHash(opt.get().getTransactionHash()).send().getTransaction();
                if (optt.isPresent()) {
                    String rawInput = optt.get().getInput();
                    log.info("input : {}", optt.get().getInput());
                    List<TypeReference<Type>> referencesTypeList = new ArrayList<>(1);
                    TypeReference exScore = AbiTypeRefUtils.getTypeRef("uint64");
                    referencesTypeList.add(exScore);

                    List<Type> listT = FunctionReturnDecoder.decode(rawInput.substring(10), referencesTypeList);
                    for (Type type : listT) {
                        log.info("type value : {}", type.getValue());
                    }
                    log.info("type info : {}", JacksonUtils.toJson(listT));
                }
            }
        }
    }

    // @Test
    public void testGetMap() {
        log.info("contract path : {}", systemEnvironmentConfig.getContractPath());
        // log.info("maps : {}", JacksonUtils.toJson(factory.getMethodIdMap()));
    }
}
