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
package com.webank.webasebee.ods;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.DefaultBlockParameter;
import org.bcos.web3j.protocol.core.methods.response.EthBlock.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;

import lombok.extern.slf4j.Slf4j;

/**
 * EthClient
 *
 * @Description: EthClient
 * @author maojiayu
 * @date 2018年11月13日 下午2:44:21
 * 
 */
@Service
@Slf4j
public class EthClient {
    @Autowired
    private Web3j web3j;

    public Block getBlock(BigInteger blockHeightNumber) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("get block number: {}", blockHeightNumber);
        Block block =
                web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockHeightNumber), false).send().getBlock();
        Stopwatch st1 = stopwatch.stop();
        log.info("get block:{} succeed, eth.getBlock useTime: {}", blockHeightNumber,
                st1.elapsed(TimeUnit.MILLISECONDS));
        return block;
    }

}
