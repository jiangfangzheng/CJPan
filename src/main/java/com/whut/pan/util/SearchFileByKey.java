package com.whut.pan.util;

/**
 * Created by zc on 2018/10/23.
 */
import com.whut.pan.domain.FileMsg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFileByKey {
    /**
     * 搜索
     * @param oldFileMsgList
     * @param key：关键词
     * @return
     */
    public static List<FileMsg> searchFileMsg(List<FileMsg> oldFileMsgList,String key){
        List<FileMsg> newFileMsgList=new ArrayList<>();
        for(int i=0;i<oldFileMsgList.size();i++){
            FileMsg fileMsg=oldFileMsgList.get(i);
            String name=fileMsg.getName();
            if (name.toLowerCase().contains(key.trim().toLowerCase()))
            {
                newFileMsgList.add(fileMsg);
            }
        }
      return  newFileMsgList;
    }
    /**
     *@param dir 搜索目录
     *@param key 搜索的关键字
     *@param searchHidden  是否搜索隐藏文件，false不搜索
     */
    public static List<File> searchFile(String dir,String key, boolean searchHidden,List<File> files)
    {
        int fileNum=0;
        File file = new File(dir);
        File[] fileList = file.listFiles();
        String fileName = "";
        String filePath = "";
        if (fileList == null || fileList.length == 0)
        {
            return null;
        }
        for (File f : fileList)
        {
            // 不搜索隐藏文件
            if (!searchHidden && f.isHidden())
            {
                continue;
            }
            fileName = f.getName();
            filePath = f.getPath();
            if (f.isFile())
            {
                    // 获取文件名忽略后缀
                    String fileNameIgnoreSuffix = fileName.substring(0,fileName.lastIndexOf("."));
                    if (fileNameIgnoreSuffix.toLowerCase().contains(key.trim().toLowerCase()))
                    {
                        System.out.println("file path -->" + filePath);
                        // 统计搜索到的文件数
                        fileNum ++;
                        // 搜索到的文件
                        files.add(f);
                    }
            }
            else if (f.isDirectory())
            {
                searchFile(filePath, key, searchHidden,files);
            }
        }
        return files;

    }
}
