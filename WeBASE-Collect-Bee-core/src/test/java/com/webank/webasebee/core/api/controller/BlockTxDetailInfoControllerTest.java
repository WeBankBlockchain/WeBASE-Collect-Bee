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
package com.webank.webasebee.core.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.webank.webasebee.common.tools.JacksonUtils;
import com.webank.webasebee.core.BaseTest;
import com.webank.webasebee.db.vo.TimeRangeQueryReq;

/**
 * BlockTxDetailInfoControllerTest
 *
 * @Description: BlockTxDetailInfoControllerTest
 * @author maojiayu
 * @data Dec 21, 2018 11:56:18 AM
 *
 */
public class BlockTxDetailInfoControllerTest extends BaseTest {

    @Test
    public void testGetBlockTxInfo() throws Exception {
        TimeRangeQueryReq req = new TimeRangeQueryReq();
        req.setBeginTime("2018-10-17 10:20:04");
        req.setEndTime("2019-10-17 10:20:04");
        mvc.perform(post("/api/blockTxDetail/time/get").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JacksonUtils.toJson(req)).accept(MediaType.APPLICATION_JSON_UTF8)).andDo(print()).andReturn();
    }

}
