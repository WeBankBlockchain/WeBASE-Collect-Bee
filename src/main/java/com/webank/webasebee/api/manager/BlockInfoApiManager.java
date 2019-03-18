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
package com.webank.webasebee.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.dao.BlockInfoDAO;
import com.webank.webasebee.entity.CommonResponse;
import com.webank.webasebee.tools.ResponseUtils;

/**
 * BlockInfoApiManager is used for querying block base info.
 *
 * @Description: BlockInfoApiManager
 * @author graysonzhang
 * @data 2018-11-30 17:59:18
 *
 */
@Service
public class BlockInfoApiManager {
    
    /** @Fields blockInfoDao : block base info data access object */
    @Autowired
    private BlockInfoDAO blockInfoDao;

    /**    
     * get block base info 
     *      
     * @return: CommonResponse        
     */
    public CommonResponse getBlockInfo() {
        return ResponseUtils.data(blockInfoDao.getBlockInfo());
    }
}
