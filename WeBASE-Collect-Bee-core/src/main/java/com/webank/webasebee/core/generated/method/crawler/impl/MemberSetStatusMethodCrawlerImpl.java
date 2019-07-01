/**
 * Copyright (C) 2018 WeBank, Inc. All Rights Reserved.
 */
package com.webank.webasebee.core.generated.method.crawler.impl;


import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.web3j.abi.FunctionReturnDecoder;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition.NamedType;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.webank.webasebee.core.generated.db.method.entity.MemberSetStatus;
import com.webank.webasebee.core.generated.db.method.repository.MemberSetStatusMethodRepository;
import com.webank.webasebee.core.crawler.face.BcosMethodCrawlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import com.webank.webasebee.core.bo.ContractMapsInfo;
import com.webank.webasebee.core.tools.AddressUtils;
import com.webank.webasebee.core.tools.BigIntegerUtils;
import com.webank.webasebee.core.tools.BoolUtils;
import com.webank.webasebee.core.tools.BytesUtils;
import com.webank.webasebee.core.tools.MethodUtils;
import lombok.extern.slf4j.Slf4j;
import java.sql.Date;

@Slf4j
@Service
@ConditionalOnProperty(name = "monitor.Member.setStatusMethodCrawlerService", havingValue = "on")
public class MemberSetStatusMethodCrawlerImpl implements BcosMethodCrawlerInterface {

	@Autowired
	private ContractMapsInfo contractMapsInfo; 
	@Autowired
	private MemberSetStatusMethodRepository repository;

	@Override
	public void transactionHandler(Transaction transaction, BigInteger blockTimeStamp, Map.Entry<String, String> entry, String methodName) {
		log.debug("Begin process MemberSetStatus Transaction");
		MemberSetStatus entity = new MemberSetStatus();
		entity.setTxHash(transaction.getHash());
		entity.setBlockHeight(transaction.getBlockNumber().longValue());
		
		String input = transaction.getInput();
		String mapKey = null;
		if(transaction.getTo() == null || transaction.getTo().equals("0x0000000000000000000000000000000000000000")){
			input = input.substring(2 + entry.getKey().length());
			mapKey = entry.getValue() + entry.getValue();
		}else{
			input = input.substring(10);
			mapKey = methodName;
		}
		
		log.debug("input : {}", input);
				
		List<NamedType> list = contractMapsInfo.getMethodFiledsMap().get(mapKey);
		List<Type> params = FunctionReturnDecoder.decode(input, MethodUtils.getMethodTypeReferenceList(list));
		
		entity.setStatus(BigIntegerUtils.toInteger(params.get(0).getValue()));
		entity.setBlockTimeStamp(new Date(blockTimeStamp.longValue()));		
		repository.save(entity);
		
		log.debug("end process MemberSetStatus Transaction");
	}
}
