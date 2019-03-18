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
package com.webank.webasebee.sys.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webank.webasebee.entity.IdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * BlockInfo entity storage of block chain base info.
 *
 * @Description: BlockInfo
 * @author graysonzhang
 * @data 2018-11-15 00:41:39
 *
 */
@SuppressWarnings("serial")
@Data
@Accessors(chain = true)
@Entity
@Table(name = "block_info")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockInfo extends IdEntity {
    
    /** @Fields currentBlockHeight : the max block height of blocks that have been processed */
    @Column(name = "current_block_height")
    private long currentBlockHeight;
    
    /** @Fields txCount : the transaction count of all blocks that have been processed */
    @Column(name = "tx_count")
    private long txCount;
    
    /** @Fields status : roll back status */
    @Column(name = "status")
    private int status;
    
    /** @Fields updatetime : update time */
    @UpdateTimestamp
    @Column(name = "depot_updatetime")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date depotUpdatetime;
}
