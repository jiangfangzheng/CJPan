package com.whut.pan.dao.impl;

import com.whut.pan.dao.IHDFSDao;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.zookeeper.common.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.whut.pan.util.FileUtil.fileSizeToString;
import static com.whut.pan.util.SystemUtil.isWindows;

/**
 * @author Sandeepin
 * 2018/3/19 0019
 */
@Component
public class HDFSDaoImpl implements IHDFSDao {

    private static String hdfsUrl = "hdfs://127.0.0.1:9000";

    static {
        // 非Windows路径
        if (!isWindows()) {
            hdfsUrl = "hdfs://192.168.1.2:9000";
        }
    }

    @Override
    public boolean mkdir(String dir) throws IOException {
        if (StringUtils.isBlank(dir)) {
            return false;
        }
        dir = hdfsUrl + dir;
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dir), conf);
        if (!fs.exists(new Path(dir))) {
            fs.mkdirs(new Path(dir));
        }
        fs.close();
        return true;
    }

    @Override
    public boolean deleteDir(String dir) throws IOException {
        if (StringUtils.isBlank(dir)) {
            return false;
        }
        dir = hdfsUrl + dir;
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dir), conf);
        fs.delete(new Path(dir), true);
        fs.close();
        return true;
    }

    @Override
    public List<String> listAll(String dir) throws IOException {
        if (StringUtils.isBlank(dir)) {
            return new ArrayList<>();
        }
        dir = hdfsUrl + dir;
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dir), conf);
        FileStatus[] stats = fs.listStatus(new Path(dir));
        List<String> names = new ArrayList<>();
        for (int i = 0; i < stats.length; ++i) {
            if (stats[i].isFile()) {
                // 常规文件
                names.add(stats[i].getPath().toString());
            } else if (stats[i].isDirectory()) {
                // 目录
                names.add(stats[i].getPath().toString());
            } else if (stats[i].isSymlink()) {
                // linux的软连接
                names.add(stats[i].getPath().toString());
            }
        }
        fs.close();
        return names;
    }

    @Override
    public List<String> listAllMsg(String dir) throws IOException {
        if (StringUtils.isBlank(dir)) {
            return new ArrayList<>();
        }
        dir = hdfsUrl + dir;
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dir), conf);
        FileStatus[] stats = fs.listStatus(new Path(dir));
        List<String> names = new ArrayList<>();
        for (int i = 0; i < stats.length; ++i) {
            if (stats[i].isFile()) {
                // 常规文件 输出 文件名 大小 修改时间 完整路径
                String path = stats[i].getPath().toString();
                String[] nameArr = path.split("/");
                String name = nameArr[nameArr.length - 1];
                String size = fileSizeToString(stats[i].getLen());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastModTime = formatter.format(stats[i].getModificationTime());
                names.add(name + "\t" + size + "\t" + lastModTime + "\t" + path);
            } else if (stats[i].isDirectory()) {
                // 目录 输出 文件名 Directory 修改时间 完整路径
                String path = stats[i].getPath().toString();
                String[] nameArr = path.split("/");
                String name = nameArr[nameArr.length - 1];
//                String size = fileSizeToString(stats[i].getLen());
                String size = "Directory";
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastModTime = formatter.format(stats[i].getModificationTime());
                names.add(name + "\t" + size + "\t" + lastModTime + "\t" + path);
            } else if (stats[i].isSymlink()) {
                // linux软连接 不处理
                // names.add(stats[i].getPath().toString());
            }
        }
        fs.close();
        return names;
    }

    @Override
    public boolean uploadLocalFile2HDFS(String localFile, String hdfsFile) throws IOException {
        if (StringUtils.isBlank(localFile) || StringUtils.isBlank(hdfsFile)) {
            return false;
        }
        hdfsFile = hdfsUrl + hdfsFile;
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsUrl), config);
        Path src = new Path(localFile);
        Path dst = new Path(hdfsFile);
        fs.copyFromLocalFile(src, dst);
        fs.close();
//        System.out.println("写HDFS成功！");
        return true;
    }

    @Override
    public boolean downloadHDFS2LocalFile(String hdfsFile, String localFile) throws IOException {
        if (StringUtils.isBlank(localFile) || StringUtils.isBlank(hdfsFile)) {
            return false;
        }
        hdfsFile = hdfsUrl + hdfsFile;
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsFile), config);
        FSDataInputStream fsdi = fs.open(new Path(hdfsFile));
        OutputStream output = new FileOutputStream(localFile);
        IOUtils.copyBytes(fsdi, output, 4096, true);
        return true;
    }

    @Override
    public boolean createNewHDFSFile(String newFile, String content) throws IOException {
        if (StringUtils.isBlank(newFile) || null == content) {
            return false;
        }
        newFile = hdfsUrl + newFile;
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(newFile), config);
        FSDataOutputStream os = fs.create(new Path(newFile));
        os.write(content.getBytes("UTF-8"));
        os.close();
        fs.close();
        return true;
    }

    @Override
    public boolean deleteHDFSFile(String hdfsFile) throws IOException {
        if (StringUtils.isBlank(hdfsFile)) {
            return false;
        }
        hdfsFile = hdfsUrl + hdfsFile;
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsFile), config);
        Path path = new Path(hdfsFile);
        boolean isDeleted = fs.delete(path, true);
        fs.close();
        return isDeleted;
    }

    @Override
    public boolean renameHDFSFile(String hdfsFileOldName, String hdfsFileNewName) throws IOException {
        if (StringUtils.isBlank(hdfsFileOldName) || StringUtils.isBlank(hdfsFileNewName)) {
            return false;
        }
        hdfsFileOldName = hdfsUrl + hdfsFileOldName;
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsFileOldName), config);
        Path oldPath = new Path(hdfsFileOldName);
        Path newPath = new Path(hdfsFileNewName);
        boolean isRenamed = fs.rename(oldPath, newPath);
        fs.close();
        return isRenamed;
    }

    @Override
    public byte[] readHDFSFile(String hdfsFile) throws Exception {
        if (StringUtils.isBlank(hdfsFile)) {
            return null;
        }
        hdfsFile = hdfsUrl + hdfsFile;
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsFile), conf);
        // 检查文件是否存在
        Path path = new Path(hdfsFile);
        if (fs.exists(path)) {
            FSDataInputStream is = fs.open(path);
            // 获取文件信息
            FileStatus stat = fs.getFileStatus(path);
            // 创建缓冲区
            byte[] buffer = new byte[Integer.parseInt(String.valueOf(stat.getLen()))];
            is.readFully(0, buffer);
            is.close();
            fs.close();
            return buffer;
        } else {
            throw new Exception("在HDFS上没有找到文件：" + hdfsFile);
        }
    }

    @Override
    public boolean append(String hdfsFile, String content) throws IOException {
        if (StringUtils.isBlank(hdfsFile)) {
            return false;
        }
        if (StringUtils.isEmpty(content)) {
            return true;
        }

        hdfsFile = hdfsUrl + hdfsFile;
        Configuration conf = new Configuration();
        // 解决hadoop单datanode环境的问题
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
        FileSystem fs = FileSystem.get(URI.create(hdfsFile), conf);
        // 检查文件是否存在
        Path path = new Path(hdfsFile);
        if (fs.exists(path)) {
            try {
                InputStream in = new ByteArrayInputStream(content.getBytes());
                OutputStream out = fs.append(new Path(hdfsFile));
                IOUtils.copyBytes(in, out, 4096, true);
                out.close();
                in.close();
                fs.close();
            } catch (Exception ex) {
                fs.close();
                throw ex;
            }
        } else {
            createNewHDFSFile(hdfsFile, content);
        }
        return true;
    }

    @Override
    public boolean moveHDFSFileOrDir(String hdfsFileOldName, String hdfsFileNewName) throws Exception {
        return renameHDFSFile(hdfsFileOldName, hdfsFileNewName);
    }
}
