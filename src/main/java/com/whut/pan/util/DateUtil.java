package com.whut.pan.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sandeepin
 *         2017/11/11 0011
 */
public class DateUtil {

    public static String getNowDate() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static String getNowTime() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }

    public static String getNowTimeS() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    public static String getNowTimeMS() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return df.format(new Date());
    }
}
