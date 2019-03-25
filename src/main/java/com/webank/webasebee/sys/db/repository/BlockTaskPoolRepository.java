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
package com.webank.webasebee.sys.db.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.webank.webasebee.sys.db.entity.BlockTaskPool;

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

    public BlockTaskPool findTopByOrderByBlockHeightDesc();
    
    public BlockTaskPool findByBlockHeight(long blockHeight);

    @Query(value = "select * from block_task_pool where block_height% ?1 = ?2 and status = ?3 limit ?4", nativeQuery = true)
    public List<BlockTaskPool> findByStatusModByBlockHeightLimit(int shardingCount, int shardingItem, int status,
            int limit);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set status = ?1 where block_height = ?2", nativeQuery = true)
    public void setStatusByBlockHeight(int status, long blockHeight);
    
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
