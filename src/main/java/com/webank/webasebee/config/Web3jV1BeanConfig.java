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
package com.webank.webasebee.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcos.channel.client.Service;
import org.bcos.channel.handler.ChannelConnections;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Web3jBeanConfig
 *
 * @Description: Web3jBeanConfig
 * @author maojiayu
 * @data Apr 16, 2019 10:37:00 AM
 *
 */
@Configuration
@Slf4j
public class Web3jV1BeanConfig {
    @Autowired
    private SystemEnvironmentConfig systemEnvironmentConfig;

    public File loadFile(String filePath, String fileName) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
            FileUtils.copyInputStreamToFile(stream, file);
        }
        return file;
    }

    /**
     * web3j run
     * 
     * @return Web3j
     * @throws Exception
     */
    @Bean
    public Web3j getWeb3j() throws Exception {
        Service service = getService();
        service.run();
        Thread.sleep(3000);
        log.info("开始启动web3j Service.");
        log.info("初始化AOMP的ChannelEthereumService");
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        // set all the timeout of sync request. Prevent the zombie thread.
        channelEthereumService.setTimeout(30 * 1000);
        log.info("初始化AOMP的ChannelEthereumService 结束");
        channelEthereumService.setChannelService(service);
        // 使用AMOP消息信道初始化web3j
        Web3j web3j = Web3j.build(channelEthereumService);
        return web3j;
    }

    /**
     * web3j basic config.
     * 
     * @return Service
     * @throws IOException
     */
    @Bean
    public Service getService() throws IOException {
        Service service = new Service();
        service.setOrgID(systemEnvironmentConfig.getOrgId() + "");
        ThreadPoolTaskExecutor threadPool = getThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(30);
        service.setThreadPool(threadPool);
        service.setConnectSeconds(10);
        service.setConnectSleepPerMillis(10);
        ChannelConnections con = new ChannelConnections();
        ArrayList<String> list = new ArrayList<>();
        String[] nodes = StringUtils.split(systemEnvironmentConfig.getNodeStr(), ";");
        List<String> nodesList = Lists.newArrayList(nodes);
        list.addAll(nodesList);
        list.stream().forEach(s -> log.info("connect address: {}", s));
        con.setConnectionsStr(list);

        File caFile = loadFile(systemEnvironmentConfig.getConfigPath() + "ca.crt", "ca.crt");
        con.setCaCertPath(caFile.getAbsolutePath());
        log.info("Ca cert path is {}", caFile.getAbsolutePath());

        File clientKeystoreFile =
                loadFile(systemEnvironmentConfig.getConfigPath() + "client.keystore", "client.keystore");
        con.setClientKeystorePath(clientKeystoreFile.getAbsolutePath());
        log.info("clientKeystoreFile path is {}", clientKeystoreFile.getAbsolutePath());

        con.setKeystorePassWord(systemEnvironmentConfig.getKeystorePassword());
        con.setClientCertPassWord(systemEnvironmentConfig.getClientCertPassword());

        ConcurrentHashMap<String, ChannelConnections> map = new ConcurrentHashMap<>();
        map.put(systemEnvironmentConfig.getOrgId() + "", con);
        service.setAllChannelConnections(map);
        return service;
    }

    /**
     * web3j thread pool config.
     * 
     * @return ThreadPoolTaskExecutor
     */
    @Bean
    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(50);
        pool.setMaxPoolSize(500);
        pool.setQueueCapacity(500);
        pool.setKeepAliveSeconds(30);
        pool.setRejectedExecutionHandler(new AbortPolicy());
        return pool;
    }

}
