package com.whut.pan.service.impl;

import com.whut.pan.dao.IHDFSDao;
import com.whut.pan.domain.FileMsg;
import com.whut.pan.domain.FileSave;
import com.whut.pan.domain.LinkSecret;
import com.whut.pan.service.IFileService;
import com.whut.pan.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    SaveServiceImpl saveService;
    @Autowired
    LinkSecretServiceImpl linkSecretService;

    @Autowired
    private IHDFSDao hdfsDao;
    static private boolean usingHDFS ;
    private static String hdfsUrl;

    public static String fileRootPath ;
    public static String tempPath; //分块文件临时存储地址
    @Value("${tempPath}")
    public  void setTempPath(String tempPath) {
        FileServiceImpl.tempPath = tempPath;
    }

    @Value("${panHDFSRootDir}")
    public String panHDFSRootDir =" /pan/";

    @Value("${hdfsUrl}")
    public  void setHdfsUrl(String hdfsUrl) {
        FileServiceImpl.hdfsUrl = hdfsUrl;
    }

    // 自定义密钥
    static private String key ;

    @Value("${fileRootPath}")
    public  void setFileRootPath(String fileRootPath) {
        FileServiceImpl.fileRootPath = fileRootPath;
    }

    @Value("${key}")
    public  void setKey(String key) {
        FileServiceImpl.key = key;
    }

    @Value("${usingHDFS}")
    public void setUsingHDFS(boolean usingHDFS) {
        FileServiceImpl.usingHDFS = usingHDFS;
    }

    //    static {
//        // 非Windows路径
//        if (!isWindows()) {
//            fileRootPath = "/root/pan/";
//        }
//    }

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
                System.out.println(ex.getMessage());
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
        if(SystemUtil.isWindows()){
            String link=saveFilePath.replace(fileRootPath, "/data/");
            link = stringSlashToOne(link);
            System.out.println("返回的路径："+link);
            return  link;
        }else{
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

    }

    @Override
    public List<FileMsg> userFileList( String userName, String path) {
        System.out.println("执行userFileList函数！");
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
                    link = link.replace(fileRootPath, "/data/");
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
                } else {
                    FileMsg fileMsg = new FileMsg();
                    String link = tempList[i].toString().replace("\\", "/");
                    String[] nameArr = link.split("/");
                    String name = nameArr[nameArr.length - 1];
                    if (!name.equals("userIcon")) {
                        fileMsg.setName(name);
                        fileMsg.setSize("Directory");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String lastModTime = formatter.format(tempList[i].lastModified());
                        fileMsg.setTime(lastModTime);
                        fileMsgList.add(fileMsg);
                    }
                }
            }
            //排序
            ListUtil.listSort(fileMsgList);

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
                //排序
                ListUtil.listSort(fileMsgListHDFS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileMsgList = fileMsgListHDFS;
        }
        return fileMsgList;
    }

    @Override
    public Boolean[] userFileDelete(String fileName, String userName, String path) {
        //解析fileName: 以$$符号分割
        String[] fileNames=null;
        if(fileName.contains("$$")){
            fileNames=fileName.split("\\$\\$");
        }else{
            fileNames=new String[1];
            fileNames[0]=fileName;
        }
        Boolean[] b = new Boolean[fileNames.length];
        for(int i=0;i<fileNames.length;i++){
            // 删除-本地文件
            if (!usingHDFS) {
                String saveFilePath = fileRootPath + userName + "/" + path;
                File file=new File(saveFilePath);
                File[] listFiles=file.listFiles();
                boolean b1=false;
                //判断是否是文件夹
                if (fileName.equals("@dir@")){
                    //是文件夹
                    b1=delete(saveFilePath);
                }else {
                    b1 = delete(saveFilePath + "/" + fileNames[i]);
                }

//                if (!b1){
//                    FileSave fileSave=saveService.findFileSaveByUserNameAndFileName(userName,fileNames[i]);
//                    saveService.delete(fileSave);
//                    b1=true;
//                }
                b[i] = b1;
            }
            // 删除-HDFS文件
            if (usingHDFS) {
                boolean b2 = false;
                try {
                    System.out.println("文件路径：" + panHDFSRootDir + userName + "/" + path + fileName);
                    if ("@dir@".equals(fileName)) {
                        b2 = hdfsDao.deleteHDFSFile(panHDFSRootDir + userName + "/" + path);
                    } else {
                        b2 = hdfsDao.deleteHDFSFile(panHDFSRootDir + userName + "/" + path + base64Encoder(fileNames[i]));
                    }
                    b[i] = b2;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (b2) {
                    System.out.println("删除HDFS上的文件成功！");
                }
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
            File file=new File(oldNameWithPath);
            if (!file.exists()){

            }
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
        if (!usingHDFS) {
            File file=new File(path + "/" + dirName);
            b=file.mkdir();
        }else{
            try {
                // mkdir自带ip/ 建立的文件夹名字 path/dirName
                b = hdfsDao.mkdir(path + "/" + dirName);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            LinkSecret linkSecret=linkSecretService.findLinkSecretBysecretLink(code);
            String[] localLink=linkSecret.getLocalLink().split("/");
            String userName=localLink[3];
//            String userName = arr[0];
            String fileName = arr[arr.length - 1];
            arr[arr.length - 1] = "";
//            String path = StringUtils.join(arr, "/");
            String path = userName+"/";
            if (localLink.length>5){
                for (int k=4;k<localLink.length-1;k++){
                    path=path+localLink[k]+"/";
                }
            }
            System.out.println("0 userName:" + userName);
            System.out.println("1 filePathAndName:" + filePathAndName);
            System.out.println("2 fileName:" + fileName);
            System.out.println("3 path:" + path);
            // 服务器下载的文件所在的本地路径的文件夹
            String saveFilePath = fileRootPath + "share" + "/" + path;
//            String saveFilePath = fileRootPath + "/" + path;
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
//            String hdfsFilePath = panHDFSRootDir + path + fileName;
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
        // 移动-本地磁盘文件
        if (!usingHDFS) {
            String saveFilePath = fileRootPath + userName + "/";
            String oldNameWithPath = saveFilePath + oldPath + "/" + fileName;
            String newNameWithPath = saveFilePath + newPath + "/" + fileName;
            boolean b1 = renameFile(oldNameWithPath, newNameWithPath);
            b = b1;
        }
        // 移动-HDFS文件
        if (usingHDFS) {
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
        }
        return b;
    }

    @Override
    public List<FileMsg> search(String key, String userName, String path) {
        List<FileMsg> fileMsgList = new ArrayList<>();
        // 拉取文件列表-本地磁盘
        if (!usingHDFS) {
            String webSaveFilePath = fileRootPath + userName + "/" + path;
            File files = new File(webSaveFilePath);
            if (!files.exists()) {
                files.mkdir();
            }
//            File[] tempList = files.listFiles();
            List<File> tempList=new ArrayList<>();
            tempList=SearchFileByKey.searchFile(webSaveFilePath,key,false,tempList);
            for (int i = 0; i < tempList.size(); i++) {
                if (tempList.get(i).isFile()) {
//                System.out.println("用户：" + userName + " 文件：" + tempList[i]);
                    FileMsg fileMsg = new FileMsg();
                    // 获取文件名和下载地址
                    String link = tempList.get(i).toString().replace("\\", "/");
                    String[] nameArr = link.split("/");
                    String name = nameArr[nameArr.length - 1];
                    link = link.replace(fileRootPath, "/data/");
                    link = link.replace("/root/pan/", "/data/");
                    String size = fileSizeToString(tempList.get(i).length());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String lastModTime = formatter.format(tempList.get(i).lastModified());
                    // 赋值到json
                    fileMsg.setName(name);
                    fileMsg.setLink(link);
                    fileMsg.setSize(size);
                    fileMsg.setTime(lastModTime);
                    fileMsgList.add(fileMsg);
                } else {
                    FileMsg fileMsg = new FileMsg();
                    String link = tempList.get(i).toString().replace("\\", "/");
                    String[] nameArr = link.split("/");
                    String name = nameArr[nameArr.length - 1];
                    if (!name.equals("userIcon")) {
                        fileMsg.setLink(link);
                        fileMsg.setName(name);
                        fileMsg.setSize("Directory");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String lastModTime = formatter.format(tempList.get(i).lastModified());
                        fileMsg.setTime(lastModTime);
                        fileMsgList.add(fileMsg);
                    }
                }
            }
        }
        // 拉取文件列表-HDFS
        if (usingHDFS) {
            String webSaveFilePath = fileRootPath + userName + "/" + path;
//            String webSaveFilePath = "D:/logs/" + userName + "/" + path;
            File files = new File(webSaveFilePath);
            if (!files.exists()) {
                files.mkdir();
            }
            List<FileMsg> fileMsgListHDFS = new ArrayList<>();
            try {
                String dir = panHDFSRootDir + userName + "/" + path;
                hdfsDao.mkdir(dir);
                List<String> listFile=new ArrayList<>();
                listFile = hdfsDao.listAllIncludeDirMsg(key,dir,listFile);
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
                        String linkDecoder = StringUtils.join(linkDecoderArr, "/").replace(hdfsUrl,"");
                        linkDecoder = linkDecoder.replace("/pan/", "/data/");
                        // 赋值到json
                        fileMsgHDFS.setName(fileName);
                        fileMsgHDFS.setLink(linkDecoder);
                        // 如果使用HDFS模式，则下载连接为“HDFS”字符串，使用download方式下载
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
    public boolean merge(String fileName, String userName, String path) throws InterruptedException {
        boolean b = false;
        String savePath  = fileRootPath + userName + "/" + path;
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String tempDirPath = FileUtil.getTempDir(tempPath, userName, fileName);
        File tempDir = new File(tempDirPath);
        // 获得分片文件列表
        File[] fileArray = tempDir.listFiles(new FileFilter() {
            // 只需要文件
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false;
                } else {
                    return true;
                }
            }
        });
//        System.out.println("【要合成的文件有】："+fileArray);
//       while (fileArray==null){
//       }
        // 转成集合进行排序后合并文件
        List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
        Collections.sort(fileList, new Comparator<File>() {
            // 按文件名升序排列
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        // 目标文件
        File outfile = new File(savePath + File.separator + fileName);
        try {
            outfile.createNewFile();
        } catch (IOException e) {
            b=false;
            System.out.println("创建目标文件出错：" + e.getMessage());
            e.printStackTrace();
        }

        // 执行合并操作
        FileChannel outChannel = null;
        FileChannel inChannel;
        try {
            outChannel = new FileOutputStream(outfile).getChannel();
            for (File file1 : fileList) {
                inChannel = new FileInputStream(file1).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                inChannel.close();
                file1.delete();
            }
            outChannel.close();
        } catch (FileNotFoundException e) {
            b=false;
            System.out.println("合并分片文件出错：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            b=false;
            System.out.println("合并分片文件出错：" + e.getMessage());
            e.printStackTrace();
        }

        // 删除临时文件夹 根目录/temp/userName/fileName
        File tempFileDir = new File(
                tempPath+ File.separator + userName + File.separator + fileName);
        FileUtil.deleteDir(tempFileDir);
        // 上传文件到-HDFS
        if (usingHDFS) {
            String localFile = savePath + File.separator + fileName;
            System.out.println("2 localFile:" + localFile);
            String remoteFile = panHDFSRootDir + userName + "/" + path + base64Encoder(fileName);
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
                b=false;
                ex.printStackTrace();
            }
        }
        return b;
    }

    //locallink是原始文件路径，path:存取路径
    @Override
    public boolean copyFileToMyPan(String userName,String localLink,String path) {
        boolean b=false;
        //share文件所在的地方
        System.out.println("0 localLink:"+localLink);
        localLink=localLink.replace("/data/", fileRootPath);
        System.out.println("0.1 localLink2:"+localLink);
        File oldfile = new File(localLink);
        String[] msg = localLink.split("/");
        String saveFileName=oldfile.getName();
        String saveFilePath  = fileRootPath + userName + "/" + path;
        System.out.println("0.2 saveFilePath:"+saveFilePath);
        File newfileDir=new File(saveFilePath);
        if (!newfileDir.exists()){
            newfileDir.mkdir();
        }
        try {
            if (oldfile.exists()){
                FileUtils.copyInputStreamToFile(new FileInputStream(oldfile),new File(saveFilePath,saveFileName));
                b=true;
            }else{
                //TODO
                System.out.println("存在同名文件");
                b=false;
            }
            } catch (IOException e) {

               e.printStackTrace();
               return false;
        }
        System.out.println(b);

        // 上传文件到-HDFS
        if (usingHDFS) {
            System.out.println("1 localLink:" +localLink);

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
                b=false;
                ex.printStackTrace();
            }
        }
        return b;
    }


}
