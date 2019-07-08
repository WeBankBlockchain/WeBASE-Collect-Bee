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

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.bo.data.BlockInfoBO;
import com.webank.webasebee.common.bo.data.CommonBO;
import com.webank.webasebee.db.dao.AccountInfoDAO;
import com.webank.webasebee.db.dao.BlockCommonDAO;
import com.webank.webasebee.db.dao.BlockDetailInfoDAO;
import com.webank.webasebee.db.dao.BlockTxDetailInfoDAO;

/**
 * BlockStoreService
 *
 * @Description: BlockStoreService
 * @author maojiayu
 * @data Jul 5, 2019 4:09:23 PM
 *
 */
@Service
public class BlockStoreService {
    @Autowired
    private BlockDetailInfoDAO blockDetailInfoDao;
    @Autowired
    private AccountInfoDAO accountInfoDao;
    @Autowired
    private BlockTxDetailInfoDAO blockTxDetailInfoDao;
    @Autowired
    private BlockCommonDAO blockEventDao;

    public void store(BlockInfoBO blockInfo) {
        blockDetailInfoDao.save(blockInfo.getBlockDetailInfo());
        accountInfoDao.save(blockInfo.getAccountInfoList());
        blockTxDetailInfoDao.save(blockInfo.getBlockTxDetailInfoList());
        blockEventDao.save(blockInfo.getEventInfoList().stream().map(e -> (CommonBO) e).collect(Collectors.toList()),
                "event");
        blockEventDao.save(blockInfo.getMethodInfoList().stream().map(e -> (CommonBO) e).collect(Collectors.toList()),
                "method");
    }

}
