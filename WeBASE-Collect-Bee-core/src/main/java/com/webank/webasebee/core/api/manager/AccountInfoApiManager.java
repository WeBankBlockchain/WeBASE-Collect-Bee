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
package com.webank.webasebee.core.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.tools.ResponseUtils;
import com.webank.webasebee.common.vo.CommonResponse;
import com.webank.webasebee.db.dao.AccountInfoDAO;
import com.webank.webasebee.db.repository.AccountInfoRepository;
import com.webank.webasebee.db.service.CommonQueryService;
import com.webank.webasebee.db.service.TimeRangeQueryService;
import com.webank.webasebee.db.vo.CommonSpecificationQueryPageReq;
import com.webank.webasebee.db.vo.ContractNameQueryReq;
import com.webank.webasebee.db.vo.TimeRangeQueryReq;

/**
 * AccountInfoApiService is a service for querying account info.
 *
 * @Description: AccountInfoApiService
 * @author graysonzhang
 * @data 2018-12-21 14:51:04
 *
 */
@Service
public class AccountInfoApiManager {

    /** @Fields timeRangeQueryService : time range query service */
    @Autowired
    private TimeRangeQueryService timeRangeQueryService;
    
    /** @Fields accountInfoDAO : account info data access object */
    @Autowired
    private AccountInfoDAO accountInfoDAO;

    /** @Fields repository : account info repository */
    @Autowired
    private AccountInfoRepository repository;

    /** @Fields commonQueryService : common query service  */
    @Autowired
    private CommonQueryService commonQueryService;

   
    /**    
     * @Description: get account info by contract address   
     * @param: contractAddress: contract address    
     * @return: CommonResponse      
     * @throws   
     */
    public CommonResponse getAccountInfoByContractAddresss(String contractAddress) {
        return ResponseUtils.data(accountInfoDAO.getAccountInfoByContractAddress(contractAddress));
    }

    /**    
     * @Description: get account info list by time range   
     * @param: req: TimeRangeQueryReq contains order, orderBy, pageNo, pageSize, startTime, endTime    
     * @return: CommonResponse      
     * @throws   
     */
    public CommonResponse getPageListByTimeRange(TimeRangeQueryReq req) {
        return timeRangeQueryService.getPageListByTimeRange(req, repository);
    }

    /**    
     * @Description: get account info list by contract name   
     * @param: req: ContractNameQueryReq contains order, orderBy, pageNo, pageSize, contractName     
     * @return: CommonResponse      
     * @throws   
     */
    public CommonResponse getAccountsPageListByReq(ContractNameQueryReq req) {
        return commonQueryService.getPageListByCommonReq(req.toCommonParaQueryPageReq(), repository);
    }
    
    public CommonResponse getPageListBySpecification(CommonSpecificationQueryPageReq req) {
        return commonQueryService.getPageListByCommonReq(req, repository);
    }

}
