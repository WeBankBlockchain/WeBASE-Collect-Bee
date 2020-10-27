package com.webank.webasebee.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/23
 */
@Data
@Accessors(chain = true)
@Entity(name = "block_raw_data")
@Table(name = "block_raw_data", indexes = { @Index(name = "block_hash", columnList = "block_hash"),
        @Index(name = "block_timestamp", columnList = "block_timestamp") })
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BlockRawData extends IdEntity {

    /** @Fields blockHash : block hash */
    @Column(name = "block_hash")
    private String blockHash;

    /** @Fields blockHeight : block height */
    @Column(name = "block_height", unique = true)
    private long blockHeight;

    @Column(name = "block_object")
    private String blockObject;

    /** @Fields blockTimeStamp : block timestamp */
    @Column(name = "block_timestamp")
    private Date blockTimeStamp;

    @Column(name = "parentHash")
    private String parentHash;

    @Column(name = "logsBloom")
    private String logsBloom;

    @Column(name = "transactionsRoot")
    private String transactionsRoot;

    @Column(name = "receiptsRoot")
    private String receiptsRoot;

    @Column(name = "dbHash")
    private String dbHash;

    @Column(name = "stateRoot")
    private String stateRoot;

    @Column(name = "sealer")
    private String sealer;

    @Column(name = "sealerList")
    private String sealerList;

    @Column(name = "extraData")
    private String extraData;

    @Column(name = "gasLimit")
    private String gasLimit;

    @Column(name = "gasUsed")
    private String gasUsed;

    @Column(name = "signatureList")
    private String signatureList;

    @Column(name = "transactionList")
    private String transactionList;

    /** @Fields updatetime : depot update time */
    @UpdateTimestamp
    @Column(name = "depot_updatetime")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date depotUpdatetime;

}
