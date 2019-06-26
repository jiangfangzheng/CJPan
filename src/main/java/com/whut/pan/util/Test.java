package com.whut.pan.util;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * Created by zc on 2018/11/14.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        File oldfile=new File("D:\\zcFile\\zc\\mytest.rar");
       String name=oldfile.getName();
        File newfilePath=new File("D:\\zc2018");
        if(!newfilePath.exists()){
            newfilePath.mkdirs();
        }
        try {
            InputStream inputStream=new FileInputStream(oldfile) ;
            FileUtils.copyInputStreamToFile(new FileInputStream(oldfile),new File("D:\\zc2018\\",name));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static  String fileShareCodeDecode(String code) {
        EncryptUtil des;
        try {
            des = new EncryptUtil("whut5bfd5116cflower03adsandeepin", "utf-8");
            System.out.println("00 code:" + code);
            String filePathAndName = des.decode(code);
            return filePathAndName;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

}
