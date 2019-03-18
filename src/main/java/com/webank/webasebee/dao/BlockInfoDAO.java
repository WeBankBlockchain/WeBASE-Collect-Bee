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
package com.webank.webasebee.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.enums.TxInfoStatusEnum;
import com.webank.webasebee.sys.db.entity.BlockInfo;
import com.webank.webasebee.sys.db.repository.BlockInfoRepository;

/**
 * BlockInfoDAO
 *
 * @author maojiayu
 * @data Dec 12, 2018 2:25:23 PM
 *
 */
@Service
public class BlockInfoDAO implements SaveInterface<BlockInfo> {
    @Autowired
    private BlockInfoRepository blockInfoRepository;

    public BlockInfo getBlockInfo() {
        List<BlockInfo> blockInfoList = blockInfoRepository.findAll();
        if (blockInfoList != null && blockInfoList.size() != 0) {
            return blockInfoList.get(0);
        } else {
            return new BlockInfo().setCurrentBlockHeight(0).setTxCount(0)
                    .setStatus(TxInfoStatusEnum.INIT.getStatus());
        }
    }

    public void save(BlockInfo blockInfo) {
        BaseDAO.saveWithTimeLog(blockInfoRepository, blockInfo);
    }

}
