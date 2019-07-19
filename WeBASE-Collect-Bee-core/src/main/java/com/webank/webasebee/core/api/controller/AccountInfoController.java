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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.webank.webasebee.common.tools.ResponseUtils;
import com.webank.webasebee.common.vo.CommonResponse;
import com.webank.webasebee.core.api.manager.AccountInfoApiManager;
import com.webank.webasebee.db.vo.CommonSpecificationQueryPageReq;
import com.webank.webasebee.db.vo.ContractNameQueryReq;
import com.webank.webasebee.db.vo.TimeRangeQueryReq;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * AccountInfoController is used for querying account info.
 *
 * @Description: AccountInfoController
 * @author graysonzhang
 * @data 2018-12-21 14:50:19
 *
 */
@RestController
@RequestMapping("/api/account")
@Api(value = "AccountInfoController", tags = "Account Infomation Query")
public class AccountInfoController {

    @Autowired
    private AccountInfoApiManager accountInfoApiManager;

    @PostMapping("address/get")
    @ApiOperation(value = "get by address", httpMethod = "POST")
    public CommonResponse getAccountInfoByContractAddress(@RequestBody @Valid String contractAddress,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseUtils.validateError(result);
        }
        return accountInfoApiManager.getAccountInfoByContractAddresss(contractAddress);
    }

    @ResponseBody
    @RequestMapping("time/get")
    @ApiOperation(value = "Base on time range", httpMethod = "POST")
    public CommonResponse getAccountInfoListByTimeRange(@RequestBody @Valid TimeRangeQueryReq req,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseUtils.validateError(result);
        }
        return accountInfoApiManager.getPageListByTimeRange(req);
    }

    @PostMapping("name/get")
    @ApiOperation(value = "get by contract name", httpMethod = "POST")
    public CommonResponse getAccountInfoListByContractName(@RequestBody @Valid ContractNameQueryReq req,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseUtils.validateError(result);
        }
        return accountInfoApiManager.getAccountsPageListByReq(req);
    }

    @ResponseBody
    @RequestMapping("specification/get")
    @ApiOperation(value = "Base on time range", httpMethod = "POST")
    public CommonResponse getAccountInfoListBySpecification(@RequestBody @Valid CommonSpecificationQueryPageReq req,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseUtils.validateError(result);
        }
        return accountInfoApiManager.getPageListBySpecification(req);
    }
}
