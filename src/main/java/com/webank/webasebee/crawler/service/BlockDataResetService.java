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

import com.webank.webasebee.entity.CommonResponse;
import com.webank.webasebee.enums.TxInfoStatusEnum;
import com.webank.webasebee.sys.db.entity.BlockTaskPool;
import com.webank.webasebee.sys.db.repository.BlockTaskPoolRepository;
import com.webank.webasebee.tools.ResponseUtils;

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
    private RollBackService rollBackService;
    @Autowired
    private SingleBlockCrawlerService singleBlockCrawlerService;
    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;

    public CommonResponse resetBlockDataByBlockId(long blockHeight) throws IOException {

        BlockTaskPool blockTaskPool = blockTaskPoolRepository.findByBlockHeight(blockHeight);
        if (blockTaskPool == null) {
            return CommonResponse.NOBLOCK;
        }
        if (blockTaskPool.getSyncStatus() == TxInfoStatusEnum.DOING.getStatus()) {
            return ResponseUtils.error("Some task is still running. please resend the request later.");
        }
        blockTaskPoolRepository.setSyncStatusByBlockHeight(TxInfoStatusEnum.DOING.getStatus(), blockHeight);
        rollBackService.rollback(blockHeight, blockHeight + 1);
        singleBlockCrawlerService.handleSingleBlock(blockHeight);
        blockTaskPoolRepository.setSyncStatusByBlockHeight(TxInfoStatusEnum.DONE.getStatus(), blockHeight);

        return CommonResponse.SUCCESS;
    }
}
