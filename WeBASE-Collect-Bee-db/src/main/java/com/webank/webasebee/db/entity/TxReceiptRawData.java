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
@Entity(name = "(tx_receipt_raw_data")
@Table(name = "(tx_receipt_raw_data", indexes = { @Index(name = "block_hash", columnList = "block_hash"),
        @Index(name = "block_timestamp", columnList = "block_timestamp") })
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TxReceiptRawData extends IdEntity{

    /** @Fields blockHeight : block height */
    @Column(name = "block_height")
    private long blockHeight;

    /** @Fields blockHash : block hash */
    @Column(name = "block_hash")
    private String blockHash;

    /** @Fields txHash : transaction hash */
    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "receipt_object")
    private String receiptObject;

    /** @Fields blockTimeStamp : block timestamp */
    @Column(name = "block_timestamp")
    private Date blockTimeStamp;

    @Column(name = "txIndex")
    private String txIndex;

    @Column(name = "root")
    private String root;

    @Column(name = "from")
    private String from;

    @Column(name = "to")
    private String to;

    @Column(name = "gasUsed")
    private String gasUsed;

    @Column(name = "contractAddress")
    private String contractAddress;

    @Column(name = "logs")
    private String logs;

    @Column(name = "logsBloom")
    private String logsBloom;

    @Column(name = "status")
    private String status;

    @Column(name = "input")
    private String input;

    @Column(name = "output")
    private String output;

    @Column(name = "txProof")
    private String txProof;

    @Column(name = "receiptProof")
    private String receiptProof;

    @Column(name = "message")
    private String message;

    /** @Fields updatetime : depot update time */
    @UpdateTimestamp
    @Column(name = "depot_updatetime")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date depotUpdatetime;

}
