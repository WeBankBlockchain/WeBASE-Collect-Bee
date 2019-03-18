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
package com.webank.webasebee.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import lombok.extern.slf4j.Slf4j;

/**
 * PropertiesUtils
 *
 * @Description: PropertiesUtils
 * @author maojiayu
 * @data Dec 28, 2018 4:10:49 PM
 *
 */
@Slf4j
public class PropertiesUtils {

    public static final String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";

    public static String getProperty(String...args) {
        Properties properties = new Properties();
        try {
            properties.load(getStream());
            for (String key : args) {
                Iterator ite = properties.keySet().iterator();
                while (ite.hasNext()) {
                    String s = (String) ite.next();
                    if (key.equalsIgnoreCase(s)) {
                        return properties.getProperty(s);
                    }
                }
            }
        } catch (IOException e) {
            log.error("getProperty error {}", e.getMessage());
        }
        return args[args.length - 1];
    }

    public static InputStream getStream() throws FileNotFoundException {
        File configFile = new File(".config/" + APPLICATION_PROPERTIES_FILE_NAME);
        if (FileUtil.exist(configFile)) {
            log.info("find application.properties, path is {}", configFile.getAbsolutePath());
            FileInputStream s = new FileInputStream(configFile);
            return s;
        }
        ClassPathResource resource = new ClassPathResource(APPLICATION_PROPERTIES_FILE_NAME);
        return resource.getStream();
    }

    public static void main(String[] args) {
        String a = getProperty("system.crawlBatchUnit");
        System.out.println(a);
    }

}
