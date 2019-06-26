package com.whut.pan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    /**
     * md5加密的字符组成
     */
	private static final char HEX_DIGITS[] =
	        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 由文件获得
     * @param file
     * @return
     */
	public static String getFileMD5ToString(final File file) {
        return bytes2HexString(getFileMD5(file));
    }

    /**
     * 获得文件的md5的byte数组
     * @param file
     * @return
     */
	public static byte[] getFileMD5(final File file) {
        if (file == null) return null;
        DigestInputStream dis = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            dis = new DigestInputStream(fis, md);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(dis.read(buffer) > 0)) break;
            }
            md = dis.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将byte数组转换为String类型
     * @param bytes
     * @return
     */
	private static String bytes2HexString(final byte[] bytes) {
	    if (bytes == null) return "";
	    int len = bytes.length;
	    if (len <= 0) return "";
	    char[] ret = new char[len << 1];
	    for (int i = 0, j = 0; i < len; i++) {
	        ret[j++] = HEX_DIGITS[bytes[i] >> 4 & 0x0f];
	        ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
	    }
	    return new String(ret);
	}

	
	public static void main(String[] args) {
		File file1=new File("D:\\zcFile\\�������\\С����2\\00-2018-2-6litterpaper2.docx");
		File file11=new File("D:\\zcFile\\�������\\С����2\\00-2018-2-6litterpaper2.docx");
		File file2=new File("D:\\zcFile\\�������\\С����2\\01-2018-2-6litterpaper2.docx");
		String file1Md5=getFileMD5ToString(file1);
		String file11Md5=getFileMD5ToString(file11);
		String file2Md5=getFileMD5ToString(file2);
		System.out.println("file1Md5:"+file1Md5);
		System.out.println("file11Md5:"+file11Md5);
		System.out.println("file2Md5:"+file2Md5);
		System.out.println(file1.length());

	}

}
