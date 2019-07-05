package com.whut.pan.util;

import com.whut.pan.Activate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sandeepin
 * 2018/2/11 0011
 */
public final class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activate.class);

    private FileUtil() {
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

    public static String getNowDate() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    /**
     * 获得分片文件临时保存路径
     *
     * @param tempPath
     * @param userName
     * @param fileName
     * @return
     */
    public static String getTempDir(String tempPath, String userName, String fileName) {
        StringBuilder dir = new StringBuilder(tempPath);
        dir.append("/").append(userName);
        dir.append("/").append(getNowDate());
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
            LOGGER.error("删除文件失败, 文件不存在:{}", fileName);
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
                LOGGER.warn("删除单个文件成功:{}", fileName);
                return true;
            } else {
                LOGGER.error("删除单个文件失败:{}", fileName);
                return false;
            }
        } else {
            LOGGER.error("删除单个文件失败, 文件不存在:{}", fileName);
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
            LOGGER.error("删除目录失败, 目录不存在:{}", dir);
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
            LOGGER.error("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            LOGGER.warn("删除目录成功:{}", dir);
            return true;
        } else {
            return false;
        }
    }

}
