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
package com.webank.webasebee.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.webank.webasebee.api.manager.BlockInfoApiManager;
import com.webank.webasebee.entity.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * BlockInfoController is used for querying blockchain base info 
 *
 * @Description: BlockInfoController
 * @author graysonzhang
 * @data 2018-12-3 15:49:05
 *
 */
@RestController
@RequestMapping("/api/blockInfo")
@Api(value = "BlockInfoController", tags = "Block Basic Infomation Query")
public class BlockInfoController {

    @Autowired
    private BlockInfoApiManager blockInfoApiService;

    @PostMapping("/get")
    @ApiOperation(value = "get block base info", httpMethod = "POST")
    public CommonResponse getBlockTxInfo() {
        return blockInfoApiService.getBlockInfo();
    }

}
