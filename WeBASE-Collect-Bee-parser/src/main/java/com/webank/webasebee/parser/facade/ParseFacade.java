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
package com.webank.webasebee.parser.facade;

import java.io.IOException;

import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.bo.data.BlockAccountsInfoBO;
import com.webank.webasebee.common.bo.data.BlockInfoBO;
import com.webank.webasebee.common.bo.data.BlockMethodInfo;
import com.webank.webasebee.parser.handler.AccountCrawlerHandler;
import com.webank.webasebee.parser.handler.BlockCrawlerHandler;
import com.webank.webasebee.parser.handler.EventCrawlerHandler;
import com.webank.webasebee.parser.handler.MethodCrawlerHandler;

/**
 * ParseFacade
 *
 * @Description: ParseFacade
 * @author maojiayu
 * @data Jul 3, 2019 11:04:14 AM
 *
 */
@Service
public class ParseFacade implements ParseInterface {
    @Autowired
    private AccountCrawlerHandler accountCrawlerHandler;
    @Autowired
    private BlockCrawlerHandler blockCrawlerHandler;
    @Autowired
    private EventCrawlerHandler eventCrawlHandler;
    @Autowired
    private MethodCrawlerHandler methodCrawlerHandler;

    /*
     * dependency: P1) getAccounts-> Accounts. P2) depend on 1, txHashContractAddress(in order to get method address) ->
     * methods. P3) depend on 2, txHashContractName.
     */
    @Override
    public BlockInfoBO parse(Block block) throws IOException {
        BlockInfoBO blockInfo = new BlockInfoBO();
        BlockAccountsInfoBO accountsBo = accountCrawlerHandler.crawl(block);
        BlockMethodInfo blockMethodInfo =
                methodCrawlerHandler.crawl(block, accountsBo.getTxHashContractAddressMapping());
        blockInfo.setAccountInfoList(accountsBo.getAccounts())
                .setBlockDetailInfo(blockCrawlerHandler.handleBlockDetail(block))
                .setEventInfoList(eventCrawlHandler.crawl(block, blockMethodInfo.getTxHashContractNameMapping()))
                .setMethodInfoList(blockMethodInfo.getMethodInfoList())
                .setBlockTxDetailInfoList(blockMethodInfo.getBlockTxDetailInfoList());
        return blockInfo;
    }

}
