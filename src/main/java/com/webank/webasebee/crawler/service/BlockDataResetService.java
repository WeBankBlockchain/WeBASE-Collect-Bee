/*
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
package com.webank.webasebee.crawler.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.config.SystemEnvironmentConfig;
import com.webank.webasebee.dao.BlockInfoDAO;
import com.webank.webasebee.entity.CommonResponse;
import com.webank.webasebee.sys.db.entity.BlockInfo;

/**
 * BlockDataResetService
 *
 * @Description: BlockDataResetService
 * @author graysonzhang
 * @data 2019-03-21 14:51:50
 *
 */
@Service
public class BlockDataResetService {
	@Autowired
    private SystemEnvironmentConfig systemEnvironmentConfig;
    @Autowired
    private BlockInfoDAO blockInfoDao;
    @Autowired
    private RollBackService rollBackService;
    @Autowired
    private SingleBlockCrawlerService singleBlockCrawlerService;
    
    public CommonResponse resetBlockDataByBlockId(long blockHeight) throws IOException{ 
        
        if(systemEnvironmentConfig.isMultiLiving()) {
        	//TODO:add multiLiving process
        	return CommonResponse.SUCCESS;
        }else {
        	BlockInfo blockInfo = blockInfoDao.getBlockInfo();
        	
            if(blockInfo == null) return CommonResponse.SYSERROR;
            if(blockHeight > blockInfo.getCurrentBlockHeight()) return CommonResponse.NOBLOCK;
            
            rollBackService.rollback(blockHeight);
            singleBlockCrawlerService.handleSingleBlock(blockHeight);
            
            return CommonResponse.SUCCESS;
        }         
    }
}
