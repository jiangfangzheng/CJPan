package com.whut.pan.controller;

import com.alibaba.fastjson.JSONObject;
import com.whut.pan.model.DirMsg;
import com.whut.pan.model.FileMsg;
import com.whut.pan.model.RenameMsg;
import com.whut.pan.model.ResponseMsg;
import com.whut.pan.service.IFileService;
import com.whut.pan.util.SystemUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
     * 空间占用展示
     *
     * @param request HttpServletRequest
     */
    @GetMapping(value = "/space")
    @ResponseBody
    public ResponseMsg getSpaceSize(HttpServletRequest request) {
        // 普通用户限制80G，guest用户限制40G，
        String userName = getUserNameByRequest(request);
        Map<String, String> spaceMap = new HashMap<>(2);
        spaceMap.put("totalSpace", "80");
        double totalSpace = 80;
        if ("guest".equals(userName)) {
            spaceMap.put("totalSpace", "40");
            totalSpace = 40;
        }
        long dirlength = SystemUtil.getDirSpaceSize(root + userName);
        double dirlengthDouble = dirlength / 1024.0 / 1024 / 1024;
        String usedeSpace = String.format("%.2f", dirlengthDouble);
        String freeSpace = String.format("%.2f", totalSpace - Double.parseDouble(usedeSpace));
        spaceMap.put("freeSpace", freeSpace);
        ResponseMsg responseMsg = new ResponseMsg();
        responseMsg.setSuccess(true);
        responseMsg.setMsg(JSONObject.toJSONString(spaceMap));
        return responseMsg;
    }


    /**
     * 用户文件列表展示
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
     * 创建目录
     *
     * @param dirMsg  路径、目录名json体
     * @param request HttpServletRequest
     * @return ResponseMsg
     */
    @PostMapping(value = "/newdir", produces = "application/json; charset=utf-8")
    public ResponseMsg newDir(@RequestBody DirMsg dirMsg, HttpServletRequest request) {
        String userName = getUserNameByRequest(request);
        String newDir = dirMsg.getPath() + "/" + dirMsg.getName();
        logger.warn("newDir() newDir:{}", newDir);
        try {
            FileUtils.forceMkdir(new File(root + userName + "/" + newDir));
        } catch (IOException e) {
            logger.error("newDir() IOException! newDir:{}", newDir);
        }
        ResponseMsg responseMsg = new ResponseMsg();
        responseMsg.setSuccess(true);
        responseMsg.setMsg(dirMsg.getPath() + "/" + dirMsg.getName());
        return responseMsg;
    }

    /**
     * 重命名文件或目录
     *
     * @param renameMsg 路径、目录名json体
     * @param request   HttpServletRequest
     * @return ResponseMsg
     */
    @PutMapping(value = "/rename", produces = "application/json; charset=utf-8")
    public ResponseMsg rename(@RequestBody RenameMsg renameMsg, HttpServletRequest request) {
        String userName = getUserNameByRequest(request);
        logger.warn("oldName:{} newName:{}", renameMsg.getBefore(), renameMsg.getAfter());
        File oldName = new File(root + userName + "/" + renameMsg.getBefore());
        File newName = new File(root + userName + "/" + renameMsg.getAfter());
        if (renameMsg.getBefore().endsWith("/")) {
            // 重命名目录
            logger.warn("重命名目录");
            try {
                FileUtils.moveDirectory(oldName, newName);
            } catch (IOException e) {
                logger.error("rename IOException.");
            }
        } else {
            // 重命名文件
            logger.warn("重命名文件");
            if (oldName.renameTo(newName)) {
                logger.warn("已重命名");
            } else {
                logger.error("rename Error.");
            }
        }
//        try {
//            FileUtils.forceMkdir(new File(root + userName + "/" + newName));
//        } catch (IOException e) {
//            logger.error("rename() IOException! rename:{}", newName);
//        }
        ResponseMsg responseMsg = new ResponseMsg();
        responseMsg.setSuccess(true);
        responseMsg.setMsg(renameMsg.getAfter());
        return responseMsg;
    }

    /**
     * 删除文件或目录(批量)
     *
     * @param dirMsgList 路径、目录名jsonList
     * @param request    HttpServletRequest
     * @return ResponseMsg
     */
    @DeleteMapping(value = "/delete", produces = "application/json; charset=utf-8")
    public ResponseMsg delete(@RequestBody List<DirMsg> dirMsgList, HttpServletRequest request) {
        String userName = getUserNameByRequest(request);
        dirMsgList.forEach(e -> {
            String path = e.getPath() + "/" + e.getName();
            logger.warn("delete() file:{}", path);
            try {
                FileUtils.forceDelete(new File(root + userName + "/" + path));
            } catch (IOException ex) {
                logger.error("delete() IOException! file:{}", path);
            }
        });
        ResponseMsg responseMsg = new ResponseMsg();
        responseMsg.setSuccess(true);
        responseMsg.setMsg("delete successful.");
        return responseMsg;
    }
}
