package com.whut.pan.service;

import com.whut.pan.domain.FileMsg;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Sandeepin
 * 2018/2/12 0012
 */
public interface IFileService {

    // 上传文件
    boolean upload(MultipartFile file, String userName, String path);

    // 下载文件
    String download(String fileName, String userName, String path);

    // 列出用户文件
    List<FileMsg> userFileList(String userName, String path);

    // 删除文件
    boolean userFileDelete(String fileName, String userName, String path);

    // 重命名文件
    boolean userFileRename(String oldName, String newName, String userName, String path);

    // 新建文件夹
    boolean userDirCreate(String dirName, String path);

    // 文件提取码-生成
    String fileShareCodeEncode(String filePathAndName);

    // 文件提取码-解析
    String fileShareCodeDecode(String code);

    // 移动文件、文件夹
    boolean userFileDirMove(String fileName, String oldPath, String newPath, String userName);

}
