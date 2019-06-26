package com.whut.pan.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 *
 * @author ouzhb
 */
public class StringUtil {

    /**
     * 判断字符串是否为null、“ ”、“null”
     *
     * @param obj
     * @return
     */
    public static boolean isNull(String obj) {
        if (obj == null) {
            return true;
        } else if (obj.toString().trim().equals("")) {
            return true;
        } else if (obj.toString().trim().toLowerCase().equals("null")) {
            return true;
        }

        return false;
    }

    /**
     * 正则验证是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[+-]?[0-9]+[0-9]*(\\.[0-9]+)?");
        Matcher match = pattern.matcher(str);

        return match.matches();
    }

    /**
     * 将一个长整数转换位字节数组(8个字节)，b[0]存储高位字符，大端
     *
     * @param l 长整数
     * @return 代表长整数的字节数组
     */
    public static byte[] longToBytes(long l) {
        byte[] b = new byte[8];
        b[0] = (byte) (l >>> 56);
        b[1] = (byte) (l >>> 48);
        b[2] = (byte) (l >>> 40);
        b[3] = (byte) (l >>> 32);
        b[4] = (byte) (l >>> 24);
        b[5] = (byte) (l >>> 16);
        b[6] = (byte) (l >>> 8);
        b[7] = (byte) (l);
        return b;
    }

    /**
     * Base64编码 java8方法
     *
     * @param input 字符串
     * @return 编码结果
     */
    public static String base64Encoder(String input) {
        try {
            return Base64.getUrlEncoder().encodeToString(input.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Base64编码 java8方法
     *
     * @param input byte
     * @return 编码结果
     */
    public static String base64Encoder(byte[] input) {
        return Base64.getUrlEncoder().encodeToString(input);
    }

    /**
     * Base64解码 java8方法
     *
     * @param input 字符串
     * @return 解码结果
     */
    public static String base64Decoder(String input) {
        try {
            byte[] base64decodedBytes = Base64.getUrlDecoder().decode(input);
            return new String(base64decodedBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Base64解码 java8方法
     *
     * @param input 字符串
     * @return 解码结果
     */
    public static byte[] base64DecoderByte(String input) {
        return Base64.getUrlDecoder().decode(input);
    }

    /**
     * 字符串中将 // /// 等 统一为/
     *
     * @param input 字符串
     * @return 解码结果
     */
    public static String stringSlashToOne(String input) {
        String out = input.replace("////", "/");
        out = out.replace("///", "/");
        out = out.replace("//", "/");
        return out;
    }
}
