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
package com.webank.webasebee.dao;

import java.math.BigInteger;
import java.sql.Date;

import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.sys.db.entity.AccountInfo;
import com.webank.webasebee.sys.db.repository.AccountInfoRepository;

/**
 * AccountInfoDAO
 *
 * @Description: AccountInfoDAO
 * @author graysonzhang
 * @data 2018-12-19 17:53:29
 *
 */
@Service
public class AccountInfoDAO {

	/** @Fields accountInfoRepository : account info repository */
	@Autowired
	private AccountInfoRepository accountInfoRepository;

	/**    
	 * Get account info from transaction receipt and insert AccountInfo object into db.  
	 * 
	 * @param receipt:TransactionReceipt
	 * @param blockTimeStamp: block timestamp
	 * @param contractName: contract name    
	 * @return void       
	 */
	public void save(TransactionReceipt receipt, BigInteger blockTimeStamp, String contractName) {
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setBlockHeight(receipt.getBlockNumber().longValue());
		accountInfo.setBlockTimeStamp(new Date(blockTimeStamp.longValue()));
		accountInfo.setContractAddress(receipt.getContractAddress());
		accountInfo.setContractName(contractName);
		accountInfoRepository.save(accountInfo);
	}
	
	/**    
	 * Get account info by contract address  from db.
	 * 
	 * @param contractAddress: contract address     
	 * @return AccountInfo       
	 */
	public AccountInfo getAccountInfoByContractAddress(String contractAddress){
		return accountInfoRepository.findByContractAddress(contractAddress);
	}
}
