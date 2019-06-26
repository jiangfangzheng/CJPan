package com.whut.pan.util;

import java.io.File;

/**
 * @author Sandeepin
 * 2018/2/11 0011
 */
public class FileUtil {
    /**
     * 获得分片文件临时保存路径
     * @param tempPath
     * @param userName
     * @param fileName
     * @return
     */
    public static String getTempDir(String tempPath, String userName, String fileName) {
        StringBuilder dir = new StringBuilder(tempPath);
        dir.append("/").append(userName);
        dir.append("/").append(DateUtil.getNowDate());
        dir.append("/").append(fileName);
        return dir.toString();
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileUtil.deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileUtil.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文件移动
     *
     * @param oldName 要移动的文件
     * @param newName 新的路径
     */
    public static boolean renameFile(String oldName, String newName) {
        // 路径
        if (!oldName.equals(newName)) {
            File oldfile = new File(oldName);
            File newfile = new File(newName);
            // 重命名文件不存在
            if (!oldfile.exists()) {
                return false;
            }
            // 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
            if (newfile.exists()) {
                System.out.println(newName + "已经存在！");
                return false;
            } else {
                return oldfile.renameTo(newfile);
            }
        } else {
            System.out.println("移动路径没有变化相同...");
            return false;
        }
    }

    public static String fileSizeToString(long size) {
        String sizeStr;
        if (size >= 1073741824) {
            sizeStr = size / 1073741824 + "GB";
        } else if (size >= 1048576) {
            sizeStr = size / 1048576 + "MB";
        } else if (size >= 1024) {
            sizeStr = size / 1024 + "KB";
        } else if (size >= 1) {
            sizeStr = size + "Byte";
        } else {
            sizeStr = "0";
        }
        return sizeStr;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean
     */
    public static boolean deleteDir(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        boolean b = true;
        if (dir.exists()){
            b = dir.delete();
        }
        return b;
    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dirName 文件夹字符串
     * @return boolean
     */
    public static boolean deleteDir(String dirName) {
        File dir = new File(dirName);
        return deleteDir(dir);
    }


    public static String getPathLastSplash(String path){
        String fileName=path;
        if(path.contains("/")){
             fileName=path.substring(path.lastIndexOf("/")+1);
        }else{
            fileName=path.substring(path.lastIndexOf("\\")+1);
        }
        return fileName;

    }

    public static void main(String[] args) {
        String fileName="D:\\home\\web\\upload\\upload\\files";

        System.out.println( getPathLastSplash(fileName));
//  // 删除单个文件
//  String file = "c:/test/test.txt";
//  DeleteFileUtil.deleteFile(file);
//  System.out.println();
        // 删除一个目录
//        String dir = "D:/home/web/upload/upload/files";
//        FileUtil.deleteDirectory(dir);
//  System.out.println();
//  // 删除文件
//  dir = "c:/test/test0";
//  DeleteFileUtil.delete(dir);
    }

}
