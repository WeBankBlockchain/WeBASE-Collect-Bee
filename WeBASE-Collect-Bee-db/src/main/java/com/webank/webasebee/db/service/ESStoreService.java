package com.webank.webasebee.db.service;

import com.webank.webasebee.common.bo.data.BlockInfoBO;
import com.webank.webasebee.db.dao.ESHandleDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Component
@Slf4j
public class ESStoreService implements DataStoreService<BlockInfoBO>{

    @Autowired
    private ESHandleDao esHandleDao;

    @Override
    public void store(BlockInfoBO blockInfo) {
        try {
            esHandleDao.saveBlockInfo(blockInfo);
        } catch (Exception e) {
            log.error("ES store failed, reason : " , e);
        }
    }
}
