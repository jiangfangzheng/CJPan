package com.whut.pan.dao;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Sandeepin
 * 2018/3/19 0019
 */
public interface IHDFSDao {

    boolean mkdir(String dir) throws IOException;

    boolean deleteDir(String dir) throws IOException;

    List<String> listAll(String dir) throws IOException;

    List<String> listAllMsg(String dir) throws IOException;

    boolean uploadLocalFile2HDFS(String localFile, String hdfsFile) throws IOException;

    boolean downloadHDFS2LocalFile(String hdfsFile, String localFile) throws IOException;

    boolean createNewHDFSFile(String newFile, String content) throws IOException;

    boolean deleteHDFSFile(String hdfsFile) throws IOException;

    boolean renameHDFSFile(String hdfsFileOldName, String hdfsFileNewName) throws IOException;

    byte[] readHDFSFile(String hdfsFile) throws Exception;

    boolean append(String hdfsFile, String content) throws IOException;

    boolean moveHDFSFileOrDir(String hdfsFileOldName, String hdfsFileNewName) throws Exception;

    List<String> listAllIncludeDirMsg(String key,String dir,List<String> names) throws IOException;


}
