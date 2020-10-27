package com.webank.webasebee.db.dao;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.webasebee.common.bo.data.BlockInfoBO;
import com.webank.webasebee.common.bo.data.BlockTxDetailInfoBO;
import com.webank.webasebee.common.bo.data.ContractInfoBO;
import com.webank.webasebee.common.bo.data.DeployedAccountInfoBO;
import com.webank.webasebee.common.bo.data.EventBO;
import com.webank.webasebee.common.bo.data.MethodBO;
import com.webank.webasebee.common.bo.data.TxRawDataBO;
import com.webank.webasebee.common.bo.data.TxReceiptRawDataBO;
import com.webank.webasebee.db.config.ESBeanConfig;
import com.webank.webasebee.db.entity.DeployedAccountInfo;
import com.webank.webasebee.db.service.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Component
public class ESHandleDao {

    @Autowired
    private ESService esService;
    @Autowired
    private ESBeanConfig esBeanConfig;

    public static final String BLOCK_DETAIL = "blockdetailinfo";

    public static final String BLOCK_RAW_DATA = "blockrawdatabO";

    public static final String TX_RAW_DATA = "txrawdata";

    public static final String DEPLOY_ACCOUNT = "deployaccountinfo";

    public static final String CONTRACT_INFO = "contractinfo";

    public static final String TX_RECEIPT_RAW_DATA = "txreceiptrawdata";

    public static final String BLOCK_TX_DETAIL = "blocktxdetailinfo";

    public static final String EVENT = "event";

    public static final String METHOD = "method";

    public void saveBlockInfo(BlockInfoBO blockInfoBO) throws Exception {

        if (!esBeanConfig.isEsEnabled()) {
            return;
        }
        esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                BLOCK_DETAIL, blockInfoBO.getBlockDetailInfo());
        esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                BLOCK_RAW_DATA, blockInfoBO.getBlockRawDataBO());

        for (TxRawDataBO txRawDataBO : blockInfoBO.getTxRawDataBOList()) {
            esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                    TX_RAW_DATA, txRawDataBO);
        }

        for (DeployedAccountInfoBO deployedAccountInfoBO : blockInfoBO.getDeployedAccountInfoBOS()) {
            DeployedAccountInfo deployedAccountInfo = new DeployedAccountInfo();
            BeanUtil.copyProperties(deployedAccountInfoBO, deployedAccountInfo, true);
            esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                    DEPLOY_ACCOUNT, deployedAccountInfo);
        }

        for (TxReceiptRawDataBO txReceiptRawDataBO : blockInfoBO.getTxReceiptRawDataBOList()) {
            esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                    TX_RECEIPT_RAW_DATA, txReceiptRawDataBO);
        }

        for (BlockTxDetailInfoBO blockTxDetailInfoBO : blockInfoBO.getBlockTxDetailInfoList()) {
            esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                    BLOCK_TX_DETAIL, blockTxDetailInfoBO);
        }

        for (EventBO eventBO : blockInfoBO.getEventInfoList()) {
            esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                    EVENT, eventBO);
        }

        for (MethodBO methodBO : blockInfoBO.getMethodInfoList()) {
            esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                    METHOD, methodBO);
        }
    }

    public void saveContractInfo(ContractInfoBO contractInfoBO) throws Exception {
        esService.createDocument(esBeanConfig.getClient(),esBeanConfig.getIndex(),
                CONTRACT_INFO, contractInfoBO);
    }


}
