/**
 *
 */
package com.baidu.perf.utils;

/**
 * 数字相关的工具类
 *
 * @Title: NumberUtil.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author maolei
 * @date 2015年10月14日 下午9:19:26
 * @version V1.0
 */
public class NumberUtil {

    public static int getValueFromArithmetic(String value) {

        if (null == value) {
            return -1;
        }

        String[] nums = value.split("\\*");
        if (nums.length > 1) {
            int size = 1;
            for (String num : nums) {
                size = size * Integer.parseInt(num.trim());
            }
            return size;
        }

        return Integer.parseInt(value);
    }
}
