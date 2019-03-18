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
package com.webank.webasebee.handler;

import java.math.BigInteger;
import java.util.Map;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import com.webank.webasebee.crawler.face.BcosEventCrawlerInterface;


/**
 * EventCrawlerHandler is responsible for crawling events info.
 *
 * @Description: EventCrawlerHandler
 * @author maojiayu
 * @data Dec 28, 2018 6:08:56 PM
 *
 */
@Service
@EnableScheduling
public class EventCrawlerHandler {
	
	/** @Fields bcosEventCrawlerMap : event crawler map for crawling event data */
	@Autowired
    private Map<String, BcosEventCrawlerInterface> bcosEventCrawlerMap;
	
	/**    
	 * Each TransactionReceipt will be processed by all event's crawlers that implements BcosEventCrawlerInterface. 
	 * 
	 * @param receipt: TransactionReceipt
	 * @param blockTimeStamp: block timestamp     
	 * @return void       
	 */
	public void handle(TransactionReceipt receipt, BigInteger blockTimeStamp){
		bcosEventCrawlerMap.forEach((k, v) -> {
            v.handleReceipt(receipt, blockTimeStamp);
        });
	}
}
