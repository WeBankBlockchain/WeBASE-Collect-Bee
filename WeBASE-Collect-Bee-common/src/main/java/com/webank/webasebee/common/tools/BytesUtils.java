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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.fisco.bcos.web3j.abi.datatypes.Bytes;
import org.fisco.bcos.web3j.abi.datatypes.DynamicArray;
import org.fisco.bcos.web3j.abi.datatypes.Int;
import org.fisco.bcos.web3j.abi.datatypes.StaticArray;
import org.fisco.bcos.web3j.abi.datatypes.Uint;
import org.fisco.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.web3j.abi.datatypes.generated.Int256;
import org.fisco.bcos.web3j.abi.datatypes.generated.Int8;
import org.fisco.bcos.web3j.abi.datatypes.generated.Uint256;

import cn.hutool.core.util.StrUtil;

/**
 * BytesUtils
 *
 * @Description: BytesUtils
 * @author maojiayu
 * @data Dec 17, 2018 4:19:25 PM
 *
 */
public class BytesUtils {

    public static Bytes32 stringToBytes32(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    public static String bytesToString(Object obj) {
        return bytesToString((Bytes) obj);
    }

    public static String bytesToString(Bytes b) {
        return StrUtil.str(b.getValue(), Charset.defaultCharset());
    }

    public static String bytesArrayToString(byte[] b) {
        return StrUtil.str(b, Charset.defaultCharset());
    }

    public static String bytesArrayToString(Object obj) {
        byte[] b = (byte[]) obj;
        return bytesArrayToString(b);
    }

    public static List<String> bytes32ListToStringList(List<Bytes32> list) {
        List<String> strList = new ArrayList<>();
        for (Bytes32 b : list) {
            String s = bytesToString(b);
            strList.add(s);
        }
        return strList;
    }

    public static String bytes32DynamicArrayToString(DynamicArray<Bytes32> bytes32DynamicArray) {
        return JacksonUtils.toJson(bytes32DynamicArrayToList(bytes32DynamicArray));
    }

    public static String uint256DynamicArrayToString(List<Uint256> list) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getValue().toString());
        }
        return JacksonUtils.toJson(stringList);
    }

    public static String uintDynamicArrayToString(List<Uint> list) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getValue().toString());
        }
        return JacksonUtils.toJson(stringList);
    }

    public static String int8DynamicArrayToString(List<Int8> list) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getValue().toString());
        }
        return JacksonUtils.toJson(stringList);
    }

    public static String int256DynamicArrayToString(List<Int256> list) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getValue().toString());
        }
        return JacksonUtils.toJson(stringList);
    }

    public static String intDynamicArrayToString(List<Int> list) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getValue().toString());
        }
        return JacksonUtils.toJson(stringList);
    }

    public static String bytes32DynamicArrayToString(List<Bytes32> bytes32List) {
        return JacksonUtils.toJson(bytes32DynamicArrayToList(bytes32List));
    }

    public static List<String> bytes32DynamicArrayToList(List<Bytes32> bytes32List) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < bytes32List.size(); i++) {
            stringList.add(bytesToString(bytes32List.get(i)).trim());
        }
        return stringList;
    }

    public static List<String> bytes32DynamicArrayToList(DynamicArray<Bytes32> bytes32DynamicArray) {
        return bytes32DynamicArrayToList(bytes32DynamicArray.getValue());
    }

    public static String bytes32ListToString(List<Bytes32> list) {
        return JacksonUtils.toJson(bytes32ListToStringList(list));
    }

    public static String bytes32ListToString(Object obj) {
        return JacksonUtils.toJson(bytes32ListToStringList((List<Bytes32>) obj));
    }

    public static String staticArrayBytes32ToString(Object obj) {
        StaticArray<Bytes32> sa = (StaticArray<Bytes32>) obj;
        List<Bytes32> list = sa.getValue();
        return bytes32ListToString(list);
    }
}
