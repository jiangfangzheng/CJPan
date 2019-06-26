package com.whut.pan.util;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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


    /**
     * 获取本地Mac地址，不用-分隔
     *
     * @return mac字符串
     * @throws SocketException      异常
     * @throws UnknownHostException 异常
     */
    public static String getLocalMac() throws SocketException, UnknownHostException {
        InetAddress ia = InetAddress.getLocalHost();
        //获取网卡，获取地址
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
//        System.out.println("mac数组长度："+mac.length);
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
//                sb.append("-");
                // mac地址不用-号分割
                sb.append("");
            }
            //字节转换为整数
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
//            System.out.println("每8位:"+str);
            if (str.length() == 1) {
                sb.append("0").append(str);
            } else {
                sb.append(str);
            }
        }
//        System.out.println("本机MAC地址:"+sb.toString().toUpperCase());
        return sb.toString().toUpperCase();
    }

    /**
     * 获取一个String的md5值
     *
     * @param str 输入字符串
     * @return md5值
     */
    public static String getMd5(String str) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bs = md5.digest(str.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for (byte x : bs) {
            if ((x & 0xff) >> 4 == 0) {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return sb.toString();
    }

    // 快速切片 字符串
    public static List<String> fastSplit(String str) {
        List<String> out = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens()) {
            out.add(st.nextToken());
        }
        return out;
    }

    // 快速切片 二维
    public static String[][] fastSplit(List<String> textList) {
        if (textList.size() > 0) {
            String[] line = textList.get(0).split(",");
            int row = textList.size();
            int col = line.length;
            String[][] mat = new String[row][col];
            for (int i = 0; i < row; i++) {
                StringTokenizer st = new StringTokenizer(textList.get(i), ",");
                int j = 0;
                while (st.hasMoreTokens()) {
                    mat[i][j++] = st.nextToken();
                }
            }
            return mat;
        }
        return null;
    }

    // 执行控制台指令
    public static String executeCmd(String cmdString) {
        StringBuilder output = new StringBuilder();
        try {
            Process pr = Runtime.getRuntime().exec(cmdString);
            InputStream errorStream = pr.getErrorStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(errorStream));
            String line;
            System.out.println("executeCmd:" + cmdString);
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                output.append("\n").append(line);
            }
            errorStream.close();
            in.close();
            pr.waitFor();
            pr.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    // 无需解压直接读取Zip文件和文件内容
    public static List<String> readZipFile(String file) throws Exception {
        List<String> outFileNameList = new ArrayList<>();
        ZipFile zf = new ZipFile(file);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            outFileNameList.add(ze.getName());
            if (ze.isDirectory()) {
                // 文件夹
//                System.err.println("文件夹:" + ze.getName() + "\t大小:"+ ze.getSize() + " bytes");
            } else {
                // 文件
//                System.err.println("文件:" + ze.getName() + "\t大小:"+ ze.getSize() + " bytes");
                // 读取文件内容
//                long size = ze.getSize();
//                if (size > 0) {
//                    BufferedReader br = new BufferedReader(
//                            new InputStreamReader(zf.getInputStream(ze)));
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        System.out.println(line);
//                    }
//                    br.close();
//                }
//                System.out.println();
            }
        }
        zin.closeEntry();
        return outFileNameList;
    }

    // 判断Spark的zip文件是否套了一层文件夹
    public static String isDirZipFile(String file) {
        String firstDir = "run.py";
        try {
            List<String> outZipFileNameList = readZipFile(file);
            for (String aName : outZipFileNameList) {
                if (aName.length() > 5 && "run.py".equals(aName.substring(0, 6))) {
                    System.out.println("ZIP文件没有套一层文件夹！");
                    firstDir = "run.py";
                    return firstDir;
                }
            }
            if (outZipFileNameList.size()>0){
                firstDir = outZipFileNameList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return firstDir;
    }

    // Java强制删除java程序占用的文件
    public static boolean forceDelete(File file) {
        boolean result = file.delete();
        int tryCount = 0;
        while (!result && tryCount++ < 10) {
            System.gc();    //回收资源
            result = file.delete();
        }
        return result;
    }

    // Java 删除文件夹
    public static void deleteAllFilesOfDir(File path) {
        if (!path.exists()){
            return;
        }
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFilesOfDir(files[i]);
        }
        path.delete();
    }


    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(isDirZipFile("E:\\1f8db793_710229096@qq.com.zip"));
////        List<String> out = fastSplit("1,,,,5,");
//        List<String> textList = new ArrayList<>();
//        textList.add("1,2,,,,5,");
//        textList.add("6,7,,,,0,");
//        String[][] A = fastSplit(textList);
//        System.out.println(getMd5("sandeepin"));
    }
}
