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
package com.webank.webasebee.parser.service;

import java.util.Map;

import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.constants.ContractConstants;

/**
 * TransactionService
 *
 * @Description: TransactionService
 * @author maojiayu
 * @data Jul 23, 2019 10:15:46 AM
 *
 */
@Service
public class TransactionService {

    public String getContractAddressByTransaction(Transaction transaction,
            Map<String, String> txHashContractAddressMapping) {
        if (transaction.getTo() == null || transaction.getTo().equals(ContractConstants.EMPTY_ADDRESS)) {
            return txHashContractAddressMapping.get(transaction.getHash());
        } else {
            return transaction.getTo();
        }
    }

}
