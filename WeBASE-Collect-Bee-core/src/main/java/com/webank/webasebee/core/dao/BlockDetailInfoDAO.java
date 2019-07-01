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
package com.webank.webasebee.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.core.sys.db.entity.BlockDetailInfo;
import com.webank.webasebee.core.sys.db.repository.BlockDetailInfoRepository;

/**
 * BlockDetailInfoDAO
 *
 * @author maojiayu
 * @data Dec 12, 2018 2:45:13 PM
 *
 */
@Service
public class BlockDetailInfoDAO implements SaveInterface<BlockDetailInfo> {
    @Autowired
    private BlockDetailInfoRepository blockDetailInfoRepository;

    public void save(BlockDetailInfo blockDetailInfo) {
        BaseDAO.saveWithTimeLog(blockDetailInfoRepository, blockDetailInfo);
    }

    public BlockDetailInfo getBlockDetailInfoByBlockHeight(long blockHeight) {
        return blockDetailInfoRepository.findByBlockHeight(blockHeight);
    }

    public BlockDetailInfo getBlockDetailInfoByBlockHash(String blockHash) {
        return blockDetailInfoRepository.findByBlockHash(blockHash);
    }

    public long sumByTxCountBetweens(long beginIndex, long endIndex) {
        return blockDetailInfoRepository.sumByTxCountBetweens(beginIndex, endIndex);
    }
    
    public BlockDetailInfo getCurrentMaxBlock(){
        return blockDetailInfoRepository.findTopByOrderByBlockHeightDesc();
    }
}
