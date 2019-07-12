package com.whut.pan.util;

/**
 * 系统工具、获取系统的一些信息
 *
 * @author Sandeepin
 * 2017/11/9 0009
 */
public class SystemUtil {

    // 判断操作系统是否为Windows
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("win");
    }
}
