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
package com.webank.webasebee.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * AccountInfo entity storage of account info  
 *
 * @Description: AccountInfo
 * @author graysonzhang
 * @data 2018-12-13 18:55:03
 *
 */
@SuppressWarnings("serial")
@Data
@Accessors(chain = true)
@Entity(name = "account_info")
@Table(name = "account_info", indexes = { @Index(name = "block_height", columnList = "block_height"),
        @Index(name = "contract_address", columnList = "contract_address"),
        @Index(name = "block_timestamp", columnList = "block_timestamp") })
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountInfo extends IdEntity {
    
    /** @Fields contractName : contract name */
    @Column(name = "contract_name")
    private String contractName;
    
    /** @Fields contractAddress : contract address */
    @Column(name = "contract_address")
    private String contractAddress;
    
    /** @Fields blockHeight : block height */
    @Column(name = "block_height")
    private long blockHeight;
    
    /** @Fields blockTimeStamp : block timestamp */
    @Column(name = "block_timestamp")
    private Date blockTimeStamp;
    
    /** @Fields updatetime : depot update time */
    @UpdateTimestamp
    @Column(name = "depot_updatetime")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date depotUpdatetime;
}
