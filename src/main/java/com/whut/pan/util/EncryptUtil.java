package com.whut.pan.util;

import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

/**
 * @author Sandeepin
 * 2018/3/18 0018
 */
public class EncryptUtil {

    private final byte[] DESIV = new byte[] { 0x12, 0x34, 0x56, 120, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef };// 向量

    private AlgorithmParameterSpec iv = null;// 加密算法的参数接口
    private Key key = null;

    private String charset = "utf-8";

    /**
     * 初始化
     * @param deSkey    密钥
     * @throws Exception
     */
    public EncryptUtil(String deSkey, String charset) throws Exception {
        if (StringUtils.isNotBlank(charset)) {
            this.charset = charset;
        }
        DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes(this.charset));// 设置密钥参数
        iv = new IvParameterSpec(DESIV);// 设置向量
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
        key = keyFactory.generateSecret(keySpec);// 得到密钥对象
    }

    /**
     * 加密
     * @author ershuai
     * @date 2017年4月19日 上午9:40:53
     * @param data
     * @return
     * @throws Exception
     */
    public String encode(String data) throws Exception {
        Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
        enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
        byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String encodePlus = base64Encoder.encode(pasByte);
        String encodePure = encodePlus.replace("+", "_");
        encodePure = encodePure.replace("/", "-");
        if(SystemUtil.isWindows()){
            encodePure = encodePure.replace("\r\n", "~");
        }else{
            encodePure = encodePure.replace("\n", "~");
        }

        return encodePure;
    }

    /**
     * 解密
     * @author ershuai
     * @date 2017年4月19日 上午9:41:01
     * @param data
     * @return
     * @throws Exception
     */
    public String decode(String data) throws Exception {
        data = data.replace("_", "+");
        data = data.replace("-", "/");
        if(SystemUtil.isWindows()){
            data = data.replace("~", "\r\n");
        }else{
            data = data.replace("~", "\n");
        }
        data = data.replace("~", "\r\n");
        Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        deCipher.init(Cipher.DECRYPT_MODE, key, iv);
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));
        return new String(pasByte, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            String test1 = "cdata/2017年2月26日~11月15日.7z";
            String test = "admin/2016.09.05CS_PSO.zip";
            String test2 = "zc/zc.txt";
            String test3 = "cdata/2017年2月-2018年7月/2017年2月26日~11月15日.7z";
            String test4 = "cdata/2017年2月-2018年7月/2017年11月23日-12月7日.7z";
            String key = "whut5bfd5116cflower03adsandeepin";// 自定义密钥
            EncryptUtil des = new EncryptUtil(key, "utf-8");
            System.out.println("加密前的字符：" + test3);
            String str1=des.encode(test3);
            System.out.println("加密后的字符：" +str1);
            char[] str2=str1.toCharArray();
//           int index=0;
//            if(str1.contains("\n")){
//                for(int i=0;i<str2.length;i++){
//                   if(str2[i]==1108){
//                       index=i;
//                   }
//                }
//            }
//            System.out.println("index:"+index);


//            System.out.println(str2.length);
//            for(int i=0;i<str2.length;i++){
//                System.out.print(str2[i]);
//                System.out.println(""+(int)(str2[i]));
//        }

//            String str2 = str1.replace("\n","");
//            System.out.println("加密后的字符去掉换行：" +str2);
//            System.out.println("加密后的字符1：" + des.encode(test3).replace("\n", ""));
            System.out.println("解密后的字符：" + des.decode(des.encode(test3)));

            System.out.println("加密前的字符：" + test4);
            String str33= des.encode(test4);
            System.out.println("加密后的字符：" + des.encode(test4));
            char[] str3=str33.toCharArray();
//            System.out.println(str3.length);
//            for(int i=0;i<str3.length;i++){
//                System.out.print(str3[i]);
//                System.out.println(""+(int)(str3[i]));
//            }

            System.out.println("解密后的字符：" + des.decode(des.encode(test4)));
            System.out.println("加密后的字符：BD-dCogX1b1iKZ93rXeRaKaCmfljKcd4eahWNiPxSsmZa37ZBQGS1Ctx5V5yqWtCC4qdrV3nYWps\nl3Kwn1k3XA==");
           String testzc="BD-dCogX1b1iKZ93rXeRaKaCmfljKcd4eahWNiPxSsmZa37ZBQGS1Ctx5V5yqWtCC4qdrV3nYWps";
           char[] testChar=testzc.toCharArray();
            System.out.println(testzc.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
