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

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * BlockInfoBO
 *
 * @Description: BlockInfoBO
 * @author maojiayu
 * @data Jul 1, 2019 8:55:51 PM
 *
 */
@Data
@Accessors(chain = true)
public class BlockInfoBO {
    private BlockDetailInfoBO blockDetailInfo;

    private BlockRawDataBO blockRawDataBO;

    private List<TxRawDataBO> txRawDataBOList;

    private List<DeployedAccountInfoBO> deployedAccountInfoBOS;

    private List<TxReceiptRawDataBO> txReceiptRawDataBOList;

    private List<BlockTxDetailInfoBO> blockTxDetailInfoList;

    private List<EventBO> eventInfoList;

    private List<MethodBO> methodInfoList;

}
