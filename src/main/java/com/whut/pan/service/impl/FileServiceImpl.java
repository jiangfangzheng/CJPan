package com.whut.pan.service.impl;

import com.whut.pan.dao.IHDFSDao;
import com.whut.pan.domain.FileMsg;
import com.whut.pan.service.IFileService;
import com.whut.pan.util.EncryptUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.whut.pan.util.FileUtil.*;
import static com.whut.pan.util.StringUtil.*;
import static com.whut.pan.util.SystemUtil.isWindows;

/**
 * @author Sandeepin
 * 2018/2/12 0012
 */
@Service
public class FileServiceImpl implements IFileService {

    @Autowired
    private IHDFSDao hdfsDao;

    static private boolean usingHDFS = true;
    static public String fileRootPath = "D:/logs/";
    public String panHDFSRootDir = "/pan/";
    // 自定义密钥
    static private String key = "whut5bfd5116cflower03adsandeepin";

    static {
        // 非Windows路径
        if (!isWindows()) {
            fileRootPath = "/root/pan/";
        }
    }

    @Override
    public boolean upload(MultipartFile file, String userName, String path) {
        boolean b = false;
        // 服务器上传的文件所在路径
        String saveFilePath = fileRootPath + userName + "/" + path;
        System.out.println("1 saveFilePath:" + saveFilePath);
        // 判断文件夹是否存在-建立文件夹
        File filePathDir = new File(saveFilePath);
        if (!filePathDir.exists()) {
            filePathDir.mkdir();
        }
        // 获取上传文件的原名 例464e7a80_710229096@qq.com.zip
        String saveFileName = file.getOriginalFilename();
        // 上传文件到-磁盘
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(saveFilePath, saveFileName));
            b = true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // 上传文件到-HDFS
        if (usingHDFS) {
            String localFile = saveFilePath + "/" + saveFileName;
            System.out.println("2 localFile:" + localFile);
            String remoteFile = panHDFSRootDir + userName + "/" + path + base64Encoder(saveFileName);
            System.out.println("3 remoteFile:" + remoteFile);
            try {
                synchronized (this) {
                    String dir = panHDFSRootDir + userName + "/" + path;
                    System.out.println("4 dir:" + dir);
                    hdfsDao.mkdir(dir);
                    System.out.println("5 mkdired");
                    hdfsDao.deleteHDFSFile(remoteFile);
                    System.out.println("开始上传文件");
                    b = hdfsDao.uploadLocalFile2HDFS(localFile, remoteFile);
                    System.out.println("上传文件状态：" + b);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return b;
    }

    @Override
    public String download(String fileName, String userName, String path) {
        // 服务器下载的文件所在的本地路径的文件夹
        String saveFilePath = fileRootPath + userName + "/" + path;
        System.out.println("1 saveFilePath:" + saveFilePath);
        // 判断文件夹是否存在-建立文件夹
        File filePathDir = new File(saveFilePath);
        if (!filePathDir.exists()) {
            filePathDir.mkdir();
        }
        // hdfs文件路径 ex: /pan/cflower/cite2.txt
        String hdfsFilePath = panHDFSRootDir + userName + "/" + path + base64Encoder(fileName);
        System.out.println("2 hdfsFilePath:" + hdfsFilePath);
        // 本地路径
        saveFilePath = saveFilePath + "/" + fileName;
        System.out.println("3 saveFilePath:" + saveFilePath);
        // 下载hdfs文件到本地
        try {
            hdfsDao.downloadHDFS2LocalFile(hdfsFilePath, saveFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String link = saveFilePath.replace(fileRootPath, "/data/");
        link = stringSlashToOne(link);
        // 返回下载路径
        return link;
    }

    @Override
    public List<FileMsg> userFileList(String userName, String path) {
        List<FileMsg> fileMsgList = new ArrayList<>();
        // 拉取文件列表-本地磁盘
        if (!usingHDFS) {
            String webSaveFilePath = fileRootPath + userName + "/" + path;
            File files = new File(webSaveFilePath);
            if (!files.exists()) {
                files.mkdir();
            }
            File[] tempList = files.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isFile()) {
//                System.out.println("用户：" + userName + " 文件：" + tempList[i]);
                    FileMsg fileMsg = new FileMsg();
                    // 获取文件名和下载地址
                    String link = tempList[i].toString().replace("\\", "/");
                    String[] nameArr = link.split("/");
                    String name = nameArr[nameArr.length - 1];
                    link = link.replace("D:/logs/", "/data/");
                    link = link.replace("/root/pan/", "/data/");
                    String size = fileSizeToString(tempList[i].length());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String lastModTime = formatter.format(tempList[i].lastModified());
                    // 赋值到json
                    fileMsg.setName(name);
                    fileMsg.setLink(link);
                    fileMsg.setSize(size);
                    fileMsg.setTime(lastModTime);
                    fileMsgList.add(fileMsg);
                }
            }
        }
        // 拉取文件列表-HDFS
        if (usingHDFS) {
            String webSaveFilePath = fileRootPath + userName + "/" + path;
            File files = new File(webSaveFilePath);
            if (!files.exists()) {
                files.mkdir();
            }
            List<FileMsg> fileMsgListHDFS = new ArrayList<>();
            try {
                String dir = panHDFSRootDir + userName + "/" + path;
                hdfsDao.mkdir(dir);
                List<String> listFile = hdfsDao.listAllMsg(dir);
                // 此处的下载连接是hdfs路径，需要先将hdfs文件下载到本地
                for (int i = 0; i < listFile.size(); i++) {
                    String fileMsg = listFile.get(i);
                    String[] msgArr = fileMsg.split("\t");
                    if (msgArr.length >= 4) {
                        FileMsg fileMsgHDFS = new FileMsg();
                        // 解码文件名
                        String[] linkDecoderArr = msgArr[3].split("/");
                        // 文件夹后加/
                        String fileName = msgArr[0] + "/";
                        if (!"Directory".equals(msgArr[1])) {
                            linkDecoderArr[linkDecoderArr.length - 1] = base64Decoder(linkDecoderArr[linkDecoderArr.length - 1]);
                            fileName = base64Decoder(msgArr[0]);
                        }
                        String linkDecoder = StringUtils.join(linkDecoderArr, "/");
                        // 赋值到json
                        fileMsgHDFS.setName(fileName);
                        fileMsgHDFS.setLink(linkDecoder);
                        // 如果使用HDFS模式，则下载连接为“HDFS”字符串，使用download方式下载
                        fileMsgHDFS.setLink("HDFS");
                        fileMsgHDFS.setSize(msgArr[1]);
                        fileMsgHDFS.setTime(msgArr[2]);
                        fileMsgListHDFS.add(fileMsgHDFS);
                        System.out.println("文件信息：" + fileMsg + "\t解码linkDecoder：" + linkDecoder);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileMsgList = fileMsgListHDFS;
        }
        return fileMsgList;
    }

    @Override
    public boolean userFileDelete(String fileName, String userName, String path) {
        boolean b = false;
        // 删除-本地文件
        if (!usingHDFS) {
            String saveFilePath = fileRootPath + userName + "/" + path;
            boolean b1 = delete(saveFilePath + "/" + fileName);
            b = b1;
        }
        // 删除-HDFS文件
        if (usingHDFS) {
            boolean b2 = false;
            try {
                System.out.println("文件路径：" + panHDFSRootDir + userName + "/" + path + fileName);
                if ("@dir@".equals(fileName)) {
                    b2 = hdfsDao.deleteHDFSFile(panHDFSRootDir + userName + "/" + path);
                } else {
                    b2 = hdfsDao.deleteHDFSFile(panHDFSRootDir + userName + "/" + path + base64Encoder(fileName));
                }
                b = b2;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (b2) {
                System.out.println("删除HDFS上的文件成功！");
            }
        }
        return b;
    }

    @Override
    public boolean userFileRename(String oldName, String newName, String userName, String path) {
        boolean b = false;
        // 重命名-本地磁盘文件
        if (!usingHDFS) {
            String saveFilePath = fileRootPath + userName + "/" + path;
            String oldNameWithPath = saveFilePath + "/" + oldName;
            String newNameWithPath = saveFilePath + "/" + newName;
            boolean b1 = renameFile(oldNameWithPath, newNameWithPath);
            b = b1;
        }
        // 重命名-HDFS文件
        if (usingHDFS) {
            String hdfsFileOldName = "";
            String hdfsFileNewName = "";
            if ("@dir@".equals(oldName)) {
                String hdfsDir = panHDFSRootDir + userName + "/" + path;
                System.out.println("1 hdfsDir:" + hdfsDir);
                hdfsFileOldName = hdfsDir + "/";
                System.out.println("2 hdfsFileOldName:" + hdfsFileOldName);
                String[] arr = hdfsDir.split("/");
                arr[arr.length - 1] = newName;
                hdfsFileNewName = StringUtils.join(arr, "/");
                System.out.println("3 hdfsFileNewName: " + hdfsFileNewName);
            } else {
                String hdfsDir = panHDFSRootDir + userName + "/" + path;
                hdfsFileOldName = hdfsDir + "/" + base64Encoder(oldName);
                hdfsFileNewName = hdfsDir + "/" + base64Encoder(newName);
            }
            boolean b2;
            try {
                b2 = hdfsDao.renameHDFSFile(hdfsFileOldName, hdfsFileNewName);
                b = b2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    @Override
    public boolean userDirCreate(String dirName, String path) {
        boolean b = false;
        try {
            // mkdir自带ip/ 建立的文件夹名字 path/dirName
            b = hdfsDao.mkdir(path + "/" + dirName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    @Override
    public String fileShareCodeEncode(String filePathAndName) {
        EncryptUtil des;
        try {
            des = new EncryptUtil(key, "utf-8");
            return des.encode(filePathAndName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    @Override
    public String fileShareCodeDecode(String code) {
        EncryptUtil des;
        try {
            des = new EncryptUtil(key, "utf-8");
            System.out.println("00 code:" + code);
            String filePathAndName = des.decode(code);
            System.out.println("00 filePathAndName:" + filePathAndName);
            String[] arr = filePathAndName.split("/");
            String userName = arr[0];
            String fileName = arr[arr.length - 1];
            arr[arr.length - 1] = "";
            String path = StringUtils.join(arr, "/");
            System.out.println("0 userName:" + userName);
            System.out.println("1 filePathAndName:" + filePathAndName);
            System.out.println("2 fileName:" + fileName);
            System.out.println("3 path:" + path);
            // 服务器下载的文件所在的本地路径的文件夹
            String saveFilePath = fileRootPath + "share" + "/" + path;
            System.out.println("1 saveFilePath:" + saveFilePath);
            // 判断文件夹是否存在-建立文件夹
            File filePathDir = new File(saveFilePath);
            if (!filePathDir.exists()) {
                // mkdirs递归创建父目录
                boolean b = filePathDir.mkdirs();
                System.out.println("递归创建父目录:" + b);
            }
            // hdfs文件路径 ex: /pan/cflower/cite2.txt
            String hdfsFilePath = panHDFSRootDir + path + base64Encoder(fileName);
            System.out.println("2 hdfsFilePath:" + hdfsFilePath);
            // 本地路径
            saveFilePath = saveFilePath + "/" + fileName;
            System.out.println("3 saveFilePath:" + saveFilePath);
            // 下载hdfs文件到本地
            try {
                hdfsDao.downloadHDFS2LocalFile(hdfsFilePath, saveFilePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String link = saveFilePath.replace(fileRootPath, "/data/");
            link = stringSlashToOne(link);
            System.out.println("4 link:" + link);
            // 返回下载路径
            return link;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    @Override
    public boolean userFileDirMove(String fileName, String oldPath, String newPath, String userName) {
        boolean b = false;
        String hdfsFileOldName = "";
        String hdfsFileNewName = "";
        String hdfsDir = panHDFSRootDir + userName + "/";
        if ("@dir@".equals(fileName)) {
            hdfsFileOldName = hdfsDir + oldPath;
            hdfsFileNewName = hdfsDir + newPath;
        } else {
            hdfsFileOldName = hdfsDir + oldPath + "/" + base64Encoder(fileName);
            hdfsFileNewName = hdfsDir + newPath + "/" + base64Encoder(fileName);
        }
        try {
            b = hdfsDao.moveHDFSFileOrDir(hdfsFileOldName, hdfsFileNewName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
}
