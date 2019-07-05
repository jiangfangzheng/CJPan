package com.whut.pan.controller;

import com.whut.pan.domain.FileMsg;
import com.whut.pan.domain.LinkSecret;
import com.whut.pan.domain.ResponseMsg;
import com.whut.pan.domain.ResponseMsgAdd;
import com.whut.pan.domain.ShareMessage;
import com.whut.pan.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.whut.pan.Activate.panRestV1Service;
import static com.whut.pan.util.SystemUtil.getUserBySession;
import static com.whut.pan.util.SystemUtil.getUserNameBySession;

/**
 * 文件操作接口、分享接口
 *
 * @author Sandeepin
 * @date 2019/7/2 0002
 */
@Controller
public class RestV1Controller {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 分块文件临时存储地址
    @Value("${tempPath}")
    public String tempPath;

    @Value("${fileRootPath}")
    public String fileRootPath;

    /**
     * 下载文件
     *
     * @param fileName
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/download", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsgAdd download(@RequestParam String fileName, String path, HttpServletRequest request) {
        return panRestV1Service.download(fileName, getUserNameBySession(request), path);
    }


    /**
     * 搜索指定路径下的文件
     *
     * @param key
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", produces = "application/json; charset=utf-8")
    @ResponseBody
    public List<FileMsg> search(@RequestParam String key, String path, HttpServletRequest request) {
        return panRestV1Service.search(key, getUserNameBySession(request), path);
    }

    /**
     * 列出用户根目录文件
     *
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/userfilelist", produces = "application/json; charset=utf-8")
    @ResponseBody
    public List<FileMsg> userFileList(String path, HttpServletRequest request) {
        return panRestV1Service.userFileList(getUserNameBySession(request), path);
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/filedelete", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg fileDelete(String fileName, String path, HttpServletRequest request) {
        return panRestV1Service.userFileDelete(fileName, getUserNameBySession(request), path);
    }

    /**
     * 重命名文件
     * 文件夹重命名时 老名字写path 新名字写newName oldName填@dir@
     *
     * @param oldName
     * @param newName
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/filerename", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg fileRename(String oldName, String newName, String path, HttpServletRequest request) {
        return panRestV1Service.fileRename(oldName, newName, getUserNameBySession(request), path);
    }

    /**
     * 创建文件夹
     *
     * @param dirName
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/dircreate", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg dirCreate(String dirName, String path, HttpServletRequest request) {
        return panRestV1Service.userDirCreate(dirName, path, getUserNameBySession(request));
    }

    /**
     * 文件提取码->真实地址（验证提取码是否正确）
     *
     * @param link
     * @return
     */
    @RequestMapping(value = "/shareCallBack", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg shareCallBack(String link) {
        return panRestV1Service.shareCallBack(link);
    }

    /**
     * 文件分享下载地址sharefile（创建链接）-----share（定位到分享页面）-shareCallBack(验证提取码是否正确)
     *
     * @param link
     * @return
     */
    @RequestMapping(value = "/sharefile", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ModelAndView shareFile(String link) {
        LinkSecret linkSecret = panRestV1Service.shareFile(link);
        if (linkSecret == null) {
            ModelAndView modelAndView = new ModelAndView("errorPage");
            modelAndView.addObject("message", "链接失效");
            return modelAndView;
        } else {
            Date date1 = linkSecret.getExpireDate();
            // 表示链接永久有效
            if (date1 == null) {
                ModelAndView modelAndView = new ModelAndView("shareSecret");
                modelAndView.addObject("link", link);
                return modelAndView;
            } else {
                // 得到long类型当前时间
                long dataTemp = System.currentTimeMillis();
                Date date2 = new Date(dataTemp);
                if (date1.before(date2)) {
                    // 不能下载
                    ModelAndView modelAndView = new ModelAndView("errorPage");
                    modelAndView.addObject("message", link + "链接失效");
                    // 删除本地共享文件夹的内容
                    FileUtil.delete(link);
                    return modelAndView;
                } else {
                    ModelAndView modelAndView = new ModelAndView("shareSecret");
                    modelAndView.addObject("link", link);
                    return modelAndView;
                }
            }
        }
    }

    /**
     * 定位到分享页面
     *
     * @param link
     * @param request
     * @return
     */
    @RequestMapping("/share")
    public ModelAndView share(String link, HttpServletRequest request) {
        String linkDecoder = panRestV1Service.share(link);
        ModelAndView modelAndView = new ModelAndView("share");
        modelAndView.addObject("link", link);
        modelAndView.addObject("linkDecoder", linkDecoder);
        if (getUserBySession(request) != null) {
            modelAndView.addObject("author", getUserNameBySession(request));
        }
        return modelAndView;
    }

    @RequestMapping("/errorPage")
    public ModelAndView errorPage(String message) {
        logger.warn("zzc:{}", message);
        ModelAndView modelAndView = new ModelAndView("errorPage");
        modelAndView.addObject("message", message);
        return modelAndView;
    }

    @RequestMapping(value = "/sharefileSecret", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg shareFileSecret(String link, String secret) {
        return panRestV1Service.shareFileSecret(link, secret);
    }

    /**
     * 文件提取码生成
     * 当再次分享同一个文件，只更新过期时间
     *
     * @param expireDay
     * @param fileName
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/generateShareLink", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg generateShareLink(@RequestParam String expireDay, String fileName, String path, HttpServletRequest request) {
        return panRestV1Service.generateShareLink(expireDay, fileName, path, getUserNameBySession(request));
    }

    /**
     * 移动文件、文件夹
     * 文件夹移动时fileName=@dir@
     *
     * @param fileName
     * @param oldPath
     * @param newPath
     * @param request
     * @return
     */
    @RequestMapping(value = "/filemove", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg fileMove(String fileName, String oldPath, String newPath, HttpServletRequest request) {
        return panRestV1Service.fileMove(fileName, oldPath, newPath, getUserNameBySession(request));
    }

    /**
     * 分块上传 有断点续传的功能
     *
     * @param request
     * @param response
     * @param file
     * @param path
     */
    @PostMapping(value = "/uploadsevlet")
    @ResponseBody
    public void uploadSevlet(MultipartFile file, String path, HttpServletRequest request, HttpServletResponse response) {
        String chunk = request.getParameter("chunk");
        logger.warn("chunk:{} path:{}", chunk, path);
        String fileName = file.getOriginalFilename();
        String userName = getUserNameBySession(request);
        MultipartHttpServletRequest Murequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> files = Murequest.getFileMap();
        logger.warn("执行前---------");
        if (null != files && !files.isEmpty()) {
            for (MultipartFile item : files.values()) {
                String tempDir = FileUtil.getTempDir(tempPath, userName, fileName);
                tempDir = FileUtil.stringSlashToOne(tempDir);
                logger.warn("tempDir:{}", tempDir);
                File dir = new File(tempDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 创建分片文件并保存
                String chunkFilePath = tempDir + "/" + chunk;
                File chunkFile = new File(chunkFilePath);
                logger.warn(chunkFilePath);
                try {
                    chunkFile.createNewFile();
                    item.transferTo(chunkFile);
                } catch (IllegalStateException e) {
                    logger.error("保存分片文件出错：", e);
                } catch (IOException e) {
                    logger.error("保存分片文件出错, IOException：", e);
                }
            }
        }
    }

    /**
     * 上传之前检查
     *
     * @param request
     * @param response
     * @return
     */

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMsg checkChunk(HttpServletRequest request, HttpServletResponse response) {
        String userName = getUserNameBySession(request);
        String fileName = request.getParameter("fileName");
        String chunk = request.getParameter("chunk");
        String chunkSize = request.getParameter("chunkSize");
        return panRestV1Service.checkChunk(userName, fileName, chunk, chunkSize);
    }

    /**
     * 所有分块上传完成后合并
     *
     * @param request
     * @param response
     * @param path
     */
    @RequestMapping(value = "/merge")
    @ResponseBody
    public void mergeChunks(HttpServletRequest request, HttpServletResponse response, String path) throws InterruptedException {
        String fileName = request.getParameter("fileName");
        String userName = getUserNameBySession(request);
        boolean result = panRestV1Service.mergeChunks(fileName, userName, path);
        logger.warn("mergeChunks result:{}", request);
    }

    /**
     * 安卓上查看分享记录的接口
     *
     * @return
     */
    @RequestMapping(value = "/shareRecord", produces = "application/json; charset=utf-8")
    @ResponseBody
    public List<ShareMessage> shareRecord(HttpServletRequest request) {
        return panRestV1Service.shareRecord(getUserNameBySession(request));
    }

    /**
     * 保存到网盘
     * link是加密的链接     downloadLink解密后的链接/data/share/zc2/Fuck.java,  path：保存路径--是用户名后面的路径
     *
     * @param request
     * @param path
     * @param link
     * @return
     */
    @RequestMapping(value = "/shareToMyPan")
    @ResponseBody
    public ResponseMsg shareToMyPan(HttpServletRequest request, String path, String link) {
        return panRestV1Service.shareToMyPan(getUserBySession(request), path, link);
    }

    /**
     * 下载客户端的apk
     * filename:下载apk的名字
     * downloadPath:下载的文件夹，放在/root/pan/share目录中
     */
    @RequestMapping(value = "/downloadApk", produces = {"application/json; charset=UTF-8"})
    @ResponseBody
    public ResponseMsg shareToMyPan(HttpServletRequest request, HttpServletResponse response, String filename, String downloadPath)
            throws UnsupportedEncodingException {
        // 读到流中
        ResponseMsg responseMsg = new ResponseMsg();
        String filePath = fileRootPath + downloadPath + "/" + filename;
        if (!new File(filePath).exists()) {
            responseMsg.setMsg("找不到文件");
        }
        // 设置输出的格式
        String agent = request.getHeader("user-agent");
        String fileNameFix;
        if (agent.contains("Firefox")) {
            fileNameFix = new String(filename.getBytes(), StandardCharsets.ISO_8859_1);
        } else {
            // 空格，（，），；，@，#，&，逗号在谷歌浏览器中出现乱码
            fileNameFix = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20").
                    replaceAll("%28", "\\(").
                    replaceAll("%29", "\\)").
                    replaceAll("%3B", ";").
                    replaceAll("%40", "@").
                    replaceAll("%23", "\\#").
                    replaceAll("%26", "\\&").
                    replaceAll("%2C", "\\,");
        }
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileNameFix + "\"");
        // 循环取出流中的数据
        byte[] b = new byte[100];
        int len;
        try (InputStream inStream = new FileInputStream(filePath)) {
            while ((len = inStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
            responseMsg.setSuccess(true);
        } catch (IOException e) {
            responseMsg.setSuccess(false);
            logger.error("shareToMyPan() IOException:{}", e.getMessage());
        }
        return responseMsg;
    }

}
