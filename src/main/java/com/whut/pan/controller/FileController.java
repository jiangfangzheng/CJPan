package com.whut.pan.controller;

import com.whut.pan.domain.FileMsg;
import com.whut.pan.domain.ResponseMsg;
import com.whut.pan.domain.User;
import com.whut.pan.service.IFileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.SerializablePermission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.whut.pan.util.FileUtil.delete;
import static com.whut.pan.util.FileUtil.renameFile;
import static com.whut.pan.util.StringUtil.stringSlashToOne;
import static com.whut.pan.util.SystemUtil.isWindows;
import static com.whut.pan.util.WebUtil.getSessionUserName;

/**
 * 文件管理
 *
 * @author Sandeepin
 * 2018/2/9 0009
 */
@Controller
public class FileController {

    @Autowired
    private IFileService fileService;

    // 文件上传
    @RequestMapping(value = "/upload", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg upload(@RequestParam MultipartFile file, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        ResponseMsg j = new ResponseMsg();
        if (file.isEmpty()) {
            j.setMsg("请选择要上传的文件！");
            return j;
        }
        // 获取用户名
        String userName = getSessionUserName(request);
        // 上传文件
        boolean b = fileService.upload(file, userName, path);
        // 反馈用户信息
        if (b) {
            j.setSuccess(true);
            j.setMsg("上传成功！");
        } else {
            j.setMsg("上传失败！");
        }
        return j;
    }

    // 文件下载
    @RequestMapping(value = "/download", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg download(String fileName, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        ResponseMsg j = new ResponseMsg();
        if (fileName.isEmpty()) {
            j.setMsg("文件名字为空！");
            return j;
        }
        // 获取用户名
        String userName = getSessionUserName(request);
        // 下载文件，获取下载路径
        String link = fileService.download(fileName, userName, path);
        if (!link.isEmpty()) {
            j.setSuccess(true);
            j.setMsg(link);
        } else {
            j.setMsg("");
        }
        return j;
    }

    // 用户根目录文件列出
    @RequestMapping(value = "/userfilelist", produces = "application/json; charset=utf-8")
    @ResponseBody
    public List<FileMsg> userFileList(String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        // 获取用户名
        String userName = getSessionUserName(request);
        // 列出用户文件
        return fileService.userFileList(userName, path);
    }

    // 文件删除
    @RequestMapping(value = "/filedelete", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg fileDelete(String fileName, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        ResponseMsg j = new ResponseMsg();
        if (fileName.isEmpty()) {
            j.setMsg("文件名字为空！");
            return j;
        }
        // 获取用户名
        String userName = getSessionUserName(request);
        // 删除文件
        boolean b = fileService.userFileDelete(fileName, userName, path);
        if (b) {
            j.setSuccess(true);
            j.setMsg("删除成功！");
        } else {
            j.setMsg("删除失败！");
        }
        return j;
    }

    // 文件重命名 文件夹重命名时 老名字写path 新名字写newName oldName填@dir@
    @RequestMapping(value = "/filerename", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg fileRename(String oldName, String newName, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        ResponseMsg j = new ResponseMsg();
        if (oldName.isEmpty() || newName.isEmpty()) {
            j.setMsg("文件名字为空！");
            return j;
        }
        // 获取用户名
        String userName = getSessionUserName(request);
        // 重命名文件
        boolean b = fileService.userFileRename(oldName, newName, userName, path);
        if (b) {
            j.setSuccess(true);
            j.setMsg("重命名成功！");
        } else {
            j.setMsg("重命名失败！");
        }
        return j;
    }

    // 文件夹创建
    @RequestMapping(value = "/dircreate", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg dirCreate(String dirName, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        ResponseMsg j = new ResponseMsg();
        if (dirName.isEmpty() || path.isEmpty()) {
            j.setMsg("文件夹名字为空！");
            return j;
        }
        // 获取用户名
        String userName = getSessionUserName(request);
        // path = /pan/userName/当前path
        path = "/pan/" + userName + path;
        // 重命名文件
        boolean b = fileService.userDirCreate(dirName, path);
        if (b) {
            j.setSuccess(true);
            j.setMsg("文件夹创建成功！");
        } else {
            j.setMsg("文件夹创建失败！");
        }
        return j;
    }

    // 文件提取码->真实地址
    @RequestMapping(value = "/share", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg share(String code) {
        ResponseMsg j = new ResponseMsg();
        if (code.isEmpty()) {
            j.setMsg("提取码为空！");
            return j;
        }
        String downloadLink = fileService.fileShareCodeDecode(code);
        if (!"null".equals(downloadLink)) {
            j.setSuccess(true);
            j.setMsg(downloadLink);
        } else {
            j.setMsg("提取码不正确！");
        }
        return j;
    }

    // 文件分享下载地址
    @RequestMapping(value = "/sharefile", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ModelAndView shareFile(String code) {
        if (code.isEmpty()) {
            code = "null";
        }
        ModelAndView modelAndView = new ModelAndView("share");
        modelAndView.addObject("filecode", code);
        return modelAndView;
    }

    // 文件提取码生成
    @RequestMapping(value = "/getcode", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg getCode(String fileName, String path, HttpServletRequest request) {
        if (path == null) {
            path = "/";
        }
        ResponseMsg j = new ResponseMsg();
        if (fileName.isEmpty() || path.isEmpty()) {
            j.setMsg("文件夹名字为空！");
            return j;
        }
        // 获取用户名
        String userName = getSessionUserName(request);
        String filePathAndName = userName + "/" + path + "/" + fileName;
        filePathAndName = stringSlashToOne(filePathAndName);
        System.out.println("filePathAndName:" + filePathAndName);
        String b = fileService.fileShareCodeEncode(filePathAndName);
        if (!"null".equals(b)) {
            j.setSuccess(true);
            j.setMsg(b);
        } else {
            j.setMsg("提取码生成失败！");
        }
        return j;
    }

    // 文件、文件夹 移动 文件夹移动时fileName=@dir@
    @RequestMapping(value = "/filemove", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg fileMove(String fileName, String oldPath, String newPath, HttpServletRequest request) {
        if (fileName == null) {
            fileName = "@dir@";
        }
        ResponseMsg j = new ResponseMsg();
        if (oldPath.isEmpty() || newPath.isEmpty()) {
            j.setMsg("路径名字为空！");
            return j;
        }
        // 获取用户名
        String userName = getSessionUserName(request);
        // 移动文件
        boolean b = fileService.userFileDirMove(fileName, oldPath, newPath, userName);
        if (b) {
            j.setSuccess(true);
            j.setMsg("移动成功！");
        } else {
            j.setMsg("移动失败！");
        }
        return j;
    }


}
