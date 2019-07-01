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
import com.webank.blockchain.wecredit.contracts.Member.NewMemberEventEventResponse;
import com.webank.webasebee.core.crawler.face.BcosEventCrawlerInterface;
import com.webank.webasebee.core.generated.db.event.entity.MemberNewMemberEvent;
import com.webank.webasebee.core.generated.db.event.repository.MemberNewMemberEventRepository;
import com.webank.webasebee.core.tools.BigIntegerUtils;
import com.webank.webasebee.core.tools.BytesUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(name = "monitor.Member.NewMemberEventCrawlerService", havingValue = "on")
public class MemberNewMemberEventCrawlerImpl implements BcosEventCrawlerInterface {
	@Autowired
    private Web3j web3j;
    @Autowired
    private Credentials credentials;
    @Autowired
    private ContractGasProvider contractGasProvider;
	@Autowired
	private MemberNewMemberEventRepository repository;
	
	
	@Override
	public boolean handleReceipt(TransactionReceipt receipt, BigInteger blockTimeStamp) {
		Member contract = Member.load("0x0000000000000000000000000000000000000000", web3j, credentials, contractGasProvider); 
		List<NewMemberEventEventResponse> NewMemberEventEventResponseList = contract.getNewMemberEventEvents(receipt);
		if(CollectionUtils.isEmpty(NewMemberEventEventResponseList)) {
		    return false;
		}
		List<MemberNewMemberEvent> list = new ArrayList<>(NewMemberEventEventResponseList.size());
		for (NewMemberEventEventResponse NewMemberEventEventResponse : NewMemberEventEventResponseList) {
			MemberNewMemberEvent memberNewMemberEvent = new MemberNewMemberEvent();
			memberNewMemberEvent.setBlockHeight(receipt.getBlockNumber().longValue());
			memberNewMemberEvent.setTxHash(receipt.getTransactionHash());
			memberNewMemberEvent.setMemberId(BigIntegerUtils.toLong(NewMemberEventEventResponse.memberId));
			memberNewMemberEvent.setExamId(BigIntegerUtils.toLong(NewMemberEventEventResponse.examId));
			memberNewMemberEvent.setEventId(BigIntegerUtils.toLong(NewMemberEventEventResponse.eventId));
			memberNewMemberEvent.setUserId(BigIntegerUtils.toLong(NewMemberEventEventResponse.userId));
			memberNewMemberEvent.setScore(BigIntegerUtils.toLong(NewMemberEventEventResponse.score));
			memberNewMemberEvent.setCreateTime(BigIntegerUtils.toLong(NewMemberEventEventResponse.createTime));
			memberNewMemberEvent.setReserve1(String.valueOf(NewMemberEventEventResponse.reserve1));
			memberNewMemberEvent.setOwner(String.valueOf(NewMemberEventEventResponse.owner));
			memberNewMemberEvent.setAddr(String.valueOf(NewMemberEventEventResponse.addr));
			memberNewMemberEvent.setBlockTimeStamp(new Date(blockTimeStamp.longValue()));
			log.debug("depot NewMemberEvent:{}", memberNewMemberEvent.toString());
			list.add(memberNewMemberEvent);
		}
		repository.saveAll(list);
		return true;
	}
}
