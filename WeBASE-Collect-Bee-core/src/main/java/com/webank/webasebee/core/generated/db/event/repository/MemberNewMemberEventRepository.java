/**
 * Copyright (C) 2018 WeBank, Inc. All Rights Reserved.
 */
package com.webank.webasebee.core.generated.db.event.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.webank.webasebee.core.generated.db.event.entity.MemberNewMemberEvent;
import com.webank.webasebee.core.sys.db.repository.RollbackInterface;


@Repository
public interface MemberNewMemberEventRepository extends JpaRepository<MemberNewMemberEvent, Long>, JpaSpecificationExecutor<MemberNewMemberEvent>, RollbackInterface {
	public MemberNewMemberEvent findTopByOrderByBlockHeightDesc();
	
	@Transactional
    @Modifying
    @Query(value = "delete from  #{#entityName} where block_height >= ?1", nativeQuery = true)
    public void rollback(long blockHeight);
    
    @Transactional
    @Modifying
    @Query(value = "delete from  #{#entityName} where block_height >= ?1 and block_height< ?2", nativeQuery = true)
    public void rollback(long startBlockHeight, long endBlockHeight);
}
