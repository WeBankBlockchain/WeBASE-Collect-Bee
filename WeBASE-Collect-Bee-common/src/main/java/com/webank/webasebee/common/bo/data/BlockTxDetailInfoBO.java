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
package com.webank.webasebee.common.bo.data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * BlockTxDetailInfo
 *
 * @Description: BlockTxDetailInfo
 * @author maojiayu
 * @data Jul 1, 2019 4:16:12 PM
 *
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockTxDetailInfoBO {

    /** @Fields blockHeight : block height */
    private String blockHeight;

    /** @Fields blockHash : block hash */
    private String blockHash;

    /** @Fields contractName : contract name */
    private String contractName;

    /** @Fields methodName : contract method name */
    private String methodName;

    /** @Fields txHash : transaction hash */
    private String txHash;

    /** @Fields txFrom : transaction' s from */
    private String txFrom;

    /** @Fields txTo : transaction's to */
    private String txTo;

    /** @Fields blockTimeStamp : block timestamp */
    private Date blockTimeStamp;

}
