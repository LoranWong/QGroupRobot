/*
 * Copyright (C) <2013> 神马才注册 <373575012@qq.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jackwong025.qgrouprobot.Utils;

/**
 * 字符串工具类
 *
 * @author chenhetong(chenhetong@baidu.com)
 * @author 神马才注册
 */
public class StringUtil {

    /**
     * 判断字符串为空
     *
     * @param str 字符串信息
     * @return true or false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断字符数组，不为空
     *
     * @param values 字符数组
     * @return true or false
     */
    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
                if (result == false) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * join方法将 Stirng数组，通过separater分隔符进行分割
     *
     * @param resource 源数组
     * @param separater 分隔符
     * @return
     */
    public static String join(String[] resource, String separater) {
        if (resource == null || resource.length == 0) {
            return null;
        }
        int len = resource.length;
        StringBuilder sb = new StringBuilder();
        if (len > 0) {
            sb.append(resource[0]);
        }
        for (int i = 1; i < len; i++) {
            sb.append(separater);
            sb.append(resource[i]);
        }
        return sb.toString();
    }

    public static String getMiddle(String sourse, String first, String last) {
        if (!areNotEmpty(sourse, first, last)) {
            return null;
        }
        int beginIndex = sourse.indexOf(first) + first.length();
        int endIndex = sourse.lastIndexOf(last);
        return sourse.substring(beginIndex, endIndex);
    }

    public static String repaceTabs(String src) {
        return src.trim().replaceAll("\t|\r", " ");
    }
}
