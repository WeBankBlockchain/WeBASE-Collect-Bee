package com.webank.webasebee.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Data
@Accessors(chain = true)
@Entity(name = "contract_info")
@Table(name = "contract_info")
@ToString(callSuper = true)
public class ContractInfo extends IdEntity{

    @Column(name = "abi_hash")
    protected Long abiHash;

    @Lob
    @Column(name = "contractABI")
    private String contractABI;

    @Lob
    @Column(name = "contractBinary")
    private String contractBinary;

    @Column(name = "version")
    private int version;

    @Column(name = "contractName")
    private String contractName;

    /** @Fields updatetime : depot update time */
    @UpdateTimestamp
    @Column(name = "depot_updatetime")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date depotUpdatetime;
}
