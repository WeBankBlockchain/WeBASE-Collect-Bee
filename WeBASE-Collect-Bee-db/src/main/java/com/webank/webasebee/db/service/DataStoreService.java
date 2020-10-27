package com.webank.webasebee.db.service;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */

public interface DataStoreService<T> {
    void store(T t);
}
