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
package com.webank.webasebee.common.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * ClazzScanUtils
 *
 * @Description: ClazzScanUtils
 * @author maojiayu
 * @data Dec 28, 2018 6:14:38 PM
 *
 */
@Slf4j
public class ClazzScanUtils {
    public static Set<Class<?>> scan(String contractPath, String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        log.info("Scan package path is {}", contractPath);
        File dir = new File(contractPath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("{} can't be read.", contractPath);
            return classes;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isDirectory()) || (file.getName().endsWith(".java"));
            }
        });
        log.info("The package dir length is {}", dirfiles.length);
        for (File file : dirfiles) {
            log.info("begin to scan file: {}", file.getName());
            String className = StringUtils.substringBefore(file.getName(), ".");
            try {
                classes.add(loadClass(packageName + '.' + className));
            } catch (ClassNotFoundException e) {
                log.error("can't find .class files.");
            }
        }
        return classes;
    }
    
    public static Set<Class<?>> scanJar(String contractPath, String packageName) {
        log.info("Scan package path is {}", contractPath);
        File dir = new File(contractPath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("{} can't be read.", contractPath);
            return null;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().endsWith(".jar");
            }
        });
        if (dirfiles == null || dirfiles.length ==0) {
            log.error("can't find jar in your path, the path is {}.", contractPath);
            return null;
        }
        if (dirfiles.length > 1) {
            log.error("too many jar file in your path, the path is {}.", contractPath);
            return null;
        }
        String jarPath = dirfiles[0].getAbsolutePath();
        try {
            log.info("begin to scan file: {}", jarPath);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarPath)}, 
                Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
            return findClassesByJar(packageName, new JarFile(jarPath));
        } catch (ClassNotFoundException e) {
            log.error("can't find class in the jar");
        } catch (IOException e) {
            log.error("{} can't be read.", jarPath);
        }
        return null;
    }
    
    private static Set<Class<?>> findClassesByJar(String packageName, JarFile jar) 
        throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        String pkgDir = packageName.replace(".", "/");
        Enumeration<JarEntry> entry = jar.entries();

        JarEntry jarEntry;
        String name, className;
        Class<?> claze;
        while (entry.hasMoreElements()) {
            jarEntry = entry.nextElement();
            name = jarEntry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (jarEntry.isDirectory() || !name.startsWith(pkgDir) 
                || !name.endsWith(".class") || name.indexOf("$") > 0) {
                continue;
            }
            className = name.substring(0, name.length() - 6);
            claze = loadClass(className.replace("/", "."));
            if (claze != null) {
                classes.add(claze);
            }
        }
        return classes;
    }
    
    private static Class<?> loadClass(String fullClzName) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(fullClzName);
    }
}
