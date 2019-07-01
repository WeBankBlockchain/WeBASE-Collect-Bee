/**
 * Copyright (C) 2018 WeBank, Inc. All Rights Reserved.
 */
package com.webank.webasebee.core.generated.event.crawler.impl;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.gas.ContractGasProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.webank.blockchain.wecredit.contracts.Member;
import com.webank.blockchain.wecredit.contracts.Member.SetStatusEventEventResponse;
import com.webank.webasebee.core.crawler.face.BcosEventCrawlerInterface;
import com.webank.webasebee.core.generated.db.event.entity.MemberSetStatusEvent;
import com.webank.webasebee.core.generated.db.event.repository.MemberSetStatusEventRepository;
import com.webank.webasebee.core.tools.BigIntegerUtils;
import com.webank.webasebee.core.tools.BytesUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(name = "monitor.Member.SetStatusEventCrawlerService", havingValue = "on")
public class MemberSetStatusEventCrawlerImpl implements BcosEventCrawlerInterface {
	@Autowired
    private Web3j web3j;
    @Autowired
    private Credentials credentials;
    @Autowired
    private ContractGasProvider contractGasProvider;
	@Autowired
	private MemberSetStatusEventRepository repository;
	
	
	@Override
	public boolean handleReceipt(TransactionReceipt receipt, BigInteger blockTimeStamp) {
		Member contract = Member.load("0x0000000000000000000000000000000000000000", web3j, credentials, contractGasProvider); 
		List<SetStatusEventEventResponse> SetStatusEventEventResponseList = contract.getSetStatusEventEvents(receipt);
		if(CollectionUtils.isEmpty(SetStatusEventEventResponseList)) {
		    return false;
		}
		List<MemberSetStatusEvent> list = new ArrayList<>(SetStatusEventEventResponseList.size());
		for (SetStatusEventEventResponse SetStatusEventEventResponse : SetStatusEventEventResponseList) {
			MemberSetStatusEvent memberSetStatusEvent = new MemberSetStatusEvent();
			memberSetStatusEvent.setBlockHeight(receipt.getBlockNumber().longValue());
			memberSetStatusEvent.setTxHash(receipt.getTransactionHash());
			memberSetStatusEvent.setStatus(BigIntegerUtils.toLong(SetStatusEventEventResponse.status));
			memberSetStatusEvent.setOwner(String.valueOf(SetStatusEventEventResponse.owner));
			memberSetStatusEvent.setAddr(String.valueOf(SetStatusEventEventResponse.addr));
			memberSetStatusEvent.setBlockTimeStamp(new Date(blockTimeStamp.longValue()));
			log.debug("depot SetStatusEvent:{}", memberSetStatusEvent.toString());
			list.add(memberSetStatusEvent);
		}
		repository.saveAll(list);
		return true;
	}
}
