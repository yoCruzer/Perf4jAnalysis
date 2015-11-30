/**
 *
 */
package com.baidu.perf.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @Title: StringUtil.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author maolei
 * @date 2015年10月16日 下午3:03:49
 * @version V1.0
 */
public class StringUtil {

    /**
     * 字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (null == str || str.trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 正则表达式匹配子字符串
     *
     * @param str
     * @param pattern
     * @return
     */
    public static List<String> regularMatch(String str, String pattern) {

        List<String> result = new ArrayList<String>();
        if (isBlank(str) || isBlank(pattern)) {
            return result;
        }

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);

        while (m.find()) {
            result.add(m.group(0));
        }

        return result;
    }

    /**
     * 正则表达式的切割
     */
    public static String[] regularSplit(String str, String regex) {

        if (isBlank(str) || isBlank(regex)) {
            return new String[] { str };
        }

        return str.split(regex);
    }

    /**
     * 正则表达式的替换
     */
    public static String regularReplaceAll(String str, String regex, String newstr) {

        if (isBlank(str) || isBlank(regex)) {
            return str;
        }

        return str.replaceAll(regex, newstr);
    }

}
