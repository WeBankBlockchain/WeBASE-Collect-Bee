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
package com.webank.webasebee.sys.db.specification;

import java.util.Date;

import org.springframework.data.jpa.domain.Specification;

import com.webank.webasebee.api.vo.CommonParaQueryPageReq;

import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DateUtil;

/**
 * CommonReqParaSpecification
 *
 * @Description: CommonReqParaSpecification
 * @author graysonzhang
 * @data 2018年12月24日 上午10:59:31
 *
 */
public class CommonReqParaSpecification {

    public static <T> Specification queryByCriteriaEqual(CommonParaQueryPageReq<T> req) throws DateException {
        return (root, query, cb) -> {
            if (req.getReqParaName().endsWith("TimeStamp") || req.getReqParaName().endsWith("Updatetime")) {
                Date value = DateUtil.parseDate((String) req.getReqParaValue());
                return cb.equal(root.get(req.getReqParaName()), value);
            }
            return cb.equal(root.get(req.getReqParaName()), req.getReqParaValue());
        };
    }
}
