package com.whut.pan.util;

/**
 * Created by zc on 2018/11/2.
 */
import java.io.*;
public class FileSplit {
    public static void main(String[] args){
        String filePath="D:\\zcFile\\软件安装的文档\\MVI_9720.MP4";
//        String filePath="D:\\zcFile\\软件安装的文档\\zcTest.txt";
//        File file=new File(filePath);
//        System.out.println(file.length());
        int size=50;
        String fileTemp="F:\\zcTest\\";
        try {
//            String[] md5Str=splitBySizeAddUp(filePath, size,fileTemp);
//            System.out.println("累加MD5加密");
//            for(int i=0;i<md5Str.length;i++){
//                System.out.println(md5Str[i]);
//            }
            String[] md5Str2=splitBySizeSubSection(filePath, size,fileTemp);
            System.out.println("单个加密");
            for(int i=0;i<md5Str2.length;i++){
                System.out.println(md5Str2[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分割：根据size的大小智能判断切割成多少份，然后返回md5的数组
     * @param filePath 文件路劲
     * @param size 文件每部分分割的大小
     * @param fileTemp 临时文件的目录F:\zcTest\:应该以临时文件目录加用户名
     * @return
     * @throws Exception
     */
    public  static String[] splitBySizeSubSection(String filePath,int size,String fileTemp) throws Exception {
        long tempSize = size*1024*1024;
        File fileDirTemp=new File(fileTemp);
        if(!fileDirTemp.exists()){
            fileDirTemp.mkdirs();
        }
        System.out.println("fileTemp:"+fileTemp);
        System.out.println("filePath:"+filePath);
        File oldFile=new File(filePath);
        BufferedInputStream in=new BufferedInputStream(new FileInputStream(oldFile));
        //byte类型
        long length=oldFile.length();
//        分块的数量
        int number= (int) Math.ceil(length/(size*1.0)/(1024*1.0)/(1024*1.0));
        String[] md5Array=new String[number];
        for(int i=0;i<number;i++){
            if(i==number-1){
                tempSize=oldFile.length()-(number-1)*tempSize;
            }
            String newFilePath=fileTemp+randNumber()+".file";
            File newFile=new File(newFilePath);
            BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(newFile));
            byte[] buf=new byte[(int)tempSize];
            in.read(buf);
            out.write(buf);
            out.close();
            md5Array[i]=MD5.getFileMD5ToString(new File(newFilePath));
            System.out.println("md5:"+md5Array[i]);
        }
        //删除临时文件
        FileUtil.delete(fileTemp);
        return md5Array;

    }

    /**
     * 分割：根据size大小智能判断切割成多少份，然后进行累加进行MD5值判断，返回MD5数组
     * @param filePath：文件的路劲
     * @param size：文件分割的大小界限
     * @param fileTemp：文件临时存放的路径F:\zcTest\
     * @return
     * @throws Exception
     */
    public  static String[] splitBySizeAddUp(String filePath,int size,String fileTemp) throws Exception {
        File fileDirTemp=new File(fileTemp);
        if(!fileDirTemp.exists()){
            fileDirTemp.mkdirs();
        }
        File oldFile=new File(filePath);
        BufferedInputStream in=new BufferedInputStream(new FileInputStream(oldFile));
        //byte类型
        long length=oldFile.length();
//        分块的数量
        int number= (int) Math.ceil(length/(size*1.0)/(1024*1.0)/(1024*1.0));
        String[] md5Array=new String[number];
        String newFilePath=fileTemp+randNumber()+".file";

        for(int i=0;i<number;i++){
            if(i==number-1){
                size=(int)oldFile.length()-(number-1)*size;
            }
            RandomAccessFile randomFile = new RandomAccessFile(newFilePath, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            byte[] buf=new byte[size];
            in.read(buf);
            randomFile.write(buf);
            randomFile.close();
            //使用MD5进行加密
            md5Array[i]=MD5.getFileMD5ToString(new File(newFilePath));
        }
        //删除临时文件
        FileUtil.delete(fileTemp);
        return md5Array;
    }
    /**
     * 随机数
     * @return
     */
    public static String randNumber(){
        double number=Math.random();
        String str= String.valueOf(number);
        str=str.replace(".","");
        return str;
    }

}
