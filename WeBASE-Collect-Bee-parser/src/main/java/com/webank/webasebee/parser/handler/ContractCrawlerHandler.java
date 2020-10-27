package com.webank.webasebee.parser.handler;

import com.webank.webasebee.common.bo.contract.ContractDetail;
import com.webank.webasebee.common.bo.data.BlockContractInfoBO;
import com.webank.webasebee.common.bo.data.DeployedAccountInfoBO;
import com.webank.webasebee.common.constants.ContractConstants;
import com.webank.webasebee.common.tools.DateUtils;
import com.webank.webasebee.extractor.ods.EthClient;
import com.webank.webasebee.parser.service.ContractConstructorService;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.Numeric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Service
@EnableScheduling
@Slf4j
public class ContractCrawlerHandler {

    @Autowired
    private EthClient ethClient;

    /** @Fields contractConstructorService : contract constructor service */
    @Autowired
    private ContractConstructorService contractConstructorService;


    @SuppressWarnings("rawtypes")
    public BlockContractInfoBO crawl(BcosBlock.Block block) throws IOException {
        List<DeployedAccountInfoBO> deployedAccountInfoBOList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        List<BcosBlock.TransactionResult> transactionResults = block.getTransactions();
        for (BcosBlock.TransactionResult result : transactionResults) {
            BcosBlock.TransactionObject to = (BcosBlock.TransactionObject) result;
            JsonTransactionResponse transaction = to.get();
            BcosTransactionReceipt bcosTransactionReceipt = ethClient.getTransactionReceipt(transaction.getHash());
            Optional<TransactionReceipt> opt = bcosTransactionReceipt.getTransactionReceipt();
            if (opt.isPresent()) {
                TransactionReceipt tr = opt.get();

                handle(tr, DateUtils.hexStrToDate(block.getTimestamp())).ifPresent(e -> {
                    deployedAccountInfoBOList.add(e);
                    map.putIfAbsent(e.getTxHash(), e.getContractAddress());
                });

            }
        }
        return new BlockContractInfoBO(map,deployedAccountInfoBOList);
    }

    public Optional<DeployedAccountInfoBO> handle(TransactionReceipt receipt, Date blockTimeStamp) throws IOException {
        Optional<JsonTransactionResponse> optt = ethClient.getTransactionByHash(receipt);
        if (optt.isPresent()) {
            JsonTransactionResponse transaction = optt.get();
            // get constructor function transaction by judging if transaction's param named to is null
            if (transaction.getTo() == null || transaction.getTo().equals(ContractConstants.EMPTY_ADDRESS)) {
                String contractAddress = receipt.getContractAddress();
                String input = ethClient.getCodeByContractAddress(contractAddress);
                log.debug("blockNumber: {}, input: {}", receipt.getBlockNumber(), input);
                Map.Entry<String, ContractDetail> entry = contractConstructorService.getConstructorNameByCode(input);
                if (entry == null) {
                    log.info("block:{} constructor binary can't find!", receipt.getBlockNumber());
                    return Optional.empty();
                }
                DeployedAccountInfoBO deployedAccountInfoBO = new DeployedAccountInfoBO();
                deployedAccountInfoBO.setBlockTimeStamp(blockTimeStamp)
                        .setBlockHeight(Numeric.toBigInt(receipt.getBlockNumber()).longValue())
                        .setContractAddress(receipt.getContractAddress())
                        .setContractName(entry.getValue().getContractInfoBO().getContractName())
                        .setBinary(entry.getValue().getContractInfoBO().getContractBinary())
                        .setTxHash(receipt.getTransactionHash());
                return Optional.of(deployedAccountInfoBO);
            }
        }
        return Optional.empty();
    }
}
