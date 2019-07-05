package com.whut.pan.util;

import com.alibaba.fastjson.JSON;
import com.whut.pan.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统工具、获取系统的一些信息
 *
 * @author Sandeepin
 * 2017/11/9 0009
 */
public final class SystemUtil {

    private SystemUtil() {
    }

    public static User getUserBySession(HttpServletRequest request) {
        String userStr = (String) request.getSession().getAttribute("user");
        return JSON.parseObject(userStr, User.class);
    }

    public static String getUserNameBySession(HttpServletRequest request) {
        User user = getUserBySession(request);
        String userName = user.getUserName();
        if (userName == null) {
            userName = "null";
        }
        return userName;
    }

    /**
     * 判断操作系统是否为Windows
     *
     * @return
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("win");
    }

}
