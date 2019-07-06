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
package com.webank.webasebee.db.convert;

import com.webank.webasebee.common.bo.data.BlockDetailInfoBO;
import com.webank.webasebee.common.bo.data.BlockInfoBO;
import com.webank.webasebee.common.tools.JacksonUtils;

import cn.hutool.core.bean.BeanUtil;

/**
 * BeanConvert
 *
 * @Description: BeanConvert
 * @author maojiayu
 * @data Jul 5, 2019 4:18:21 PM
 *
 */
public class BeanConvert {

    public static void main(String[] args) {
        BlockInfoBO blockInfo = JacksonUtils.fromJson(
                "{\"blockDetailInfo\":{\"blockHeight\":14,\"blockHash\":\"0x1aea73d1de4b9df1d4e96e9aa122eabf6ff106b0163e963c7300f205fc8442bc\",\"txCount\":1,\"blockTimeStamp\":1562144823590,\"status\":\"COMPLETED\"},\"accountInfoList\":[],\"blockTxDetailInfoList\":[{\"blockHeight\":14,\"blockHash\":\"0x1aea73d1de4b9df1d4e96e9aa122eabf6ff106b0163e963c7300f205fc8442bc\",\"contractName\":\"Rule\",\"methodName\":\"SetRule\",\"txHash\":\"0x746030a974b53e9e950b516e6f53d49da5c606f9390eb43b3b0d3ac5aa0df066\",\"txFrom\":\"0xaf640a28cf25ccb4d4f05b74e55194e50ca7bb39\",\"txTo\":\"0x2876a097c79e54cc36cff96fb05c8b3ae32307c7\",\"blockTimeStamp\":1562144823590}],\"eventInfoList\":[],\"methodInfoList\":[{\"identifier\":\"RuleSetRule\",\"blockHeight\":14,\"txHash\":\"0x746030a974b53e9e950b516e6f53d49da5c606f9390eb43b3b0d3ac5aa0df066\",\"blockTimeStamp\":1562144823590,\"id\":\"888\",\"ruleType\":2,\"createTime\":888}]}\n"
                        + "",
                BlockInfoBO.class);
        BlockDetailInfoBO entity = new BlockDetailInfoBO();
        BeanUtil.copyProperties(blockInfo.getBlockDetailInfo(), entity);
        System.out.println(JacksonUtils.toJson(entity));
    }

}
