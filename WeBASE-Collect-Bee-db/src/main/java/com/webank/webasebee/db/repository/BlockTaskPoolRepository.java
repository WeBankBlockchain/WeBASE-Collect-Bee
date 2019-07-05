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
package com.webank.webasebee.db.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.webank.webasebee.db.entity.BlockTaskPool;

/**
 * BlockTaskPoolRepository
 *
 * @Description: BlockTaskPoolRepository
 * @author maojiayu
 * @data Jan 11, 2019 10:10:04 AM
 *
 */
@Repository
public interface BlockTaskPoolRepository
        extends JpaRepository<BlockTaskPool, Long>, JpaSpecificationExecutor<BlockTaskPool> {

    public Optional<BlockTaskPool> findTopByOrderByBlockHeightDesc();

    public Optional<BlockTaskPool> findByBlockHeight(long blockHeight);

    public List<BlockTaskPool> findByCertainty(int certainty);

    public List<BlockTaskPool> findByBlockHeightBetween(long startNumber, long endNumber);

    public long countBySyncStatus(int syncStatus);

    public long countByBlockHeightBetween(long startNumber, long endNumber);

    @Query(value = "select * from #{#entityName} where sync_status = 4 or sync_status = 3 ", nativeQuery = true)
    public List<BlockTaskPool> findUnNormalRecords();

    @Query(value = "select * from #{#entityName} where sync_status = ?1 order by block_height limit ?2", nativeQuery = true)
    public List<BlockTaskPool> findBySyncStatusOrderByBlockHeightLimit(int syncStatus, int limit);

    @Query(value = "select * from #{#entityName} where block_height% ?1 = ?2 and sync_status = ?3 limit ?4", nativeQuery = true)
    public List<BlockTaskPool> findBySyncStatusModByBlockHeightLimit(int shardingCount, int shardingItem,
            int syncStatus, int limit);

    public List<BlockTaskPool> findBySyncStatusAndDepotUpdatetimeLessThan(int syncStatus, Date time);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set sync_status = ?1, depot_updatetime= ?2 where block_height = ?3", nativeQuery = true)
    public void setSyncStatusByBlockHeight(int syncStatus, Date updateTime, long blockHeight);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set certainty = ?1 where block_height = ?2", nativeQuery = true)
    public void setCertaintyByBlockHeight(int certainty, long blockHeight);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set sync_status = ?1, certainty = ?2 where block_height = ?3", nativeQuery = true)
    public void setSyncStatusAndCertaintyByBlockHeight(int syncStatus, int certainty, long blockHeight);

    /*
     * @see com.webank.webasebee.sys.db.repository.RollbackInterface#rollback(long)
     */
    @Transactional
    @Modifying
    @Query(value = "delete from  #{#entityName} where block_height >= ?1", nativeQuery = true)
    public void rollback(long blockHeight);

    /*
     * @see com.webank.webasebee.sys.db.repository.RollbackInterface#rollback(long, long)
     */
    @Transactional
    @Modifying
    @Query(value = "delete from  #{#entityName} where block_height >= ?1 and block_height< ?2", nativeQuery = true)
    public void rollback(long startBlockHeight, long endBlockHeight);
}
