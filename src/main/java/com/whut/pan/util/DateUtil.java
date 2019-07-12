package com.whut.pan.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sandeepin
 * 2017/11/11 0011
 */
public class DateUtil {

    public static String getNowDate() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }
}
