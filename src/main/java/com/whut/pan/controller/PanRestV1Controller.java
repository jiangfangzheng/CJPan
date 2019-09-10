package com.whut.pan.controller;

import com.alibaba.fastjson.JSON;
import com.whut.pan.model.DirMsg;
import com.whut.pan.model.FileMsg;
import com.whut.pan.model.ResponseMsg;
import com.whut.pan.service.IFileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import static com.whut.pan.util.WebUtil.getUserNameByRequest;

/**
 * 云盘v1版Rest接口
 *
 * @author Sandeepin
 * @date 2019/9/6 0006
 */
@RestController
@RequestMapping(value = "/rest/pan")
public class PanRestV1Controller {

    /**
     * 文件目录
     */
    @Value("${fileRootPath}")
    private String root;

    @Autowired
    private IFileService fileService;


    /**
     * 文件上传
     *
     * @param path    上传路径
     * @param request Servlet3.0方式上传文件，获取parts
     * @return result
     */
    @RequestMapping("upload")
    public Object upload(@RequestParam String path, HttpServletRequest request) {
        try {
            // Servlet3.0方式上传文件
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                // 忽略路径字段,只处理文件类型
                if (part.getContentType() != null) {
                    String fullPath = root + "sandeepin/" + path;
                    System.out.println("fullPath:" + fullPath);
                    File f = new File(fullPath, getFileName(part.getHeader("content-disposition")));
                    if (!write(part.getInputStream(), f)) {
                        throw new Exception("文件上传失败");
                    }
                }
            }
//            return success();
        } catch (Exception e) {
//            return error(e.getMessage());
        }
        return "";
    }

    public static String getFileName(String header) {
        String[] tempArr1 = header.split(";");
        String[] tempArr2 = tempArr1[2].split("=");
        // 获取文件名，兼容各种浏览器的写法
        return tempArr2[1].substring(tempArr2[1].lastIndexOf("\\") + 1).replaceAll("\"", "");
    }

    public static boolean write(InputStream inputStream, File f) {
        boolean ret = false;

        try (OutputStream outputStream = new FileOutputStream(f)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            ret = true;

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }


    /**
     * 用户目录文件列出
     *
     * @param path    路径
     * @param request HttpServletRequest
     * @return List<FileMsg>
     */
    @RequestMapping(value = "/list", produces = "application/json; charset=utf-8")
    public List<FileMsg> list(String path, HttpServletRequest request) {
        String userName = getUserNameByRequest(request);
        String fullFilePath = root + userName + "/";
        if (path != null) {
            fullFilePath += path;
        }
        return fileService.list(fullFilePath, userName);
    }

    /**
     * 新建文件夹
     *
     * @param dirMsg   路径、目录名json体
     * @param request HttpServletRequest
     * @return ResponseMsg
     */
    @PostMapping(value = "/newdir", produces = "application/json; charset=utf-8")
    public ResponseMsg newDir(@RequestBody DirMsg dirMsg, HttpServletRequest request) {
        String userName = getUserNameByRequest(request);
        try {
            FileUtils.forceMkdir(new File(root + userName + "/" + dirMsg.getPath() + "/" + dirMsg.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResponseMsg responseMsg = new ResponseMsg();
        responseMsg.setSuccess(true);
        responseMsg.setMsg(dirMsg.getPath() + "/" + dirMsg.getName());
        return responseMsg;
    }
}
