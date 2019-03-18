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
package com.webank.webasebee.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.webank.webasebee.api.vo.CommonPageRes;
import com.webank.webasebee.api.vo.CommonParaQueryPageReq;
import com.webank.webasebee.entity.CommonResponse;
import com.webank.webasebee.sys.db.specification.CommonReqParaSpecification;
import com.webank.webasebee.tools.ResponseUtils;

import cn.hutool.core.date.DateException;

/**
 * CommonQueryService is a common service for querying one page of object list by a param type and value.
 *
 * @Description: CommonQueryService
 * @author graysonzhang
 * @data 2018-12-24 10:57:02
 *
 */
@Service
public class CommonQueryService {

    /**
     * get one page of object list by param type and value.
     * 
     * @param req: CommonParaQueryPageReq contains order, orderBy, pageNo, pageSize, paramType, paramValue
     * @param repository
     * @return: CommonResponse
     */
    public <T> CommonResponse getPageListByCommonReq(CommonParaQueryPageReq<String> req,
            JpaSpecificationExecutor<T> repository) {
        PageRequest pr = (PageRequest) req.convert();
        try {
            Specification<T> spec = CommonReqParaSpecification.queryByCriteriaEqual(req);
            Page<T> page = repository.findAll(spec, pr);
            CommonPageRes<T> ret = new CommonPageRes<>(req);
            ret.setResult(page.getContent()).setTotalCount(page.getTotalElements()).setPageNo(req.getPageNo())
                    .setPageSize(req.getPageSize());
            return ResponseUtils.data(ret);
        } catch (DateException e) {
            return ResponseUtils.paramError("invalid date format " + e.getMessage());
        }
    }
}
