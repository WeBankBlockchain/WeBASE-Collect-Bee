package com.webank.webasebee.db.config;

import com.webank.webasebee.db.service.ESService;
import lombok.Data;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetAddress;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/22
 */
@Configuration
@Data
@ConditionalOnProperty(name = "es.enabled", havingValue = "true")
public class ESBeanConfig {

    @Value("${es.clusterName}")
    private String clusterName;
    @Value("${es.ip}")
    private String ip;
    @Value("${es.port}")
    private int port;

    private TransportClient client;

    @Autowired
    private ESService esService;

    @PostConstruct
    protected void init() throws Exception {
        System.setProperty("es.set.netty.runtime.available.processors","false");
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        client = new PreBuiltTransportClient(settings);
        TransportAddress node = new TransportAddress(
                InetAddress.getByName(ip),
                port
        );
        client.addTransportAddress(node);
    }
}
