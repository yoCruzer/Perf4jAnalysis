/**
 *
 */
package com.baidu.perf.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 *
 * @Title: TimeUtil.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author maolei
 * @date 2015年10月14日 下午9:11:18
 * @version V1.0
 */
public class TimeUtil {

    /**
     * 获得偏移的日期，偏移0为当天，可以为负值
     */
    public static String getDateStrShift(int shift) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, shift);
        String yesterday = TimeUtil.getDateStrWithoutTime(cal.getTimeInMillis());

        return yesterday;
    }

    /**
     * 根据时间毫秒值获得格式化的日期
     *
     * @param timeInMillis
     * @return
     */
    public static String getDateStrWithoutTime(long timeInMillis) {

        Date d = new Date(timeInMillis);
        String fmt = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);

        return sdf.format(d);
    }

    /**
     * 根据时间毫秒值获得格式化的日期时间
     *
     * @param timeInMillis
     * @return
     */
    public static String getDateStr(long timeInMillis) {

        Date d = new Date(timeInMillis);
        String fmt = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);

        return sdf.format(d);
    }

}
