package com.whut.pan.service;

import com.whut.pan.domain.FileMsg;
import com.whut.pan.domain.LinkSecret;
import com.whut.pan.domain.ResponseMsg;
import com.whut.pan.domain.ResponseMsgAdd;
import com.whut.pan.domain.ShareMessage;
import com.whut.pan.domain.User;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 远程调用Rest v1版接口可用方法
 */
public interface IPanRestV1Service {

    Map<String, Object> login(String userName, String password);

    Map<String, Object> signin(String alias, String userName, String password, String regcode, String email, String phone);

    ResponseMsg updateUserByUserName(String username, String alias, String password, String email, String phone, User user);

    void alterSecret(String userName, String password);

    void deleteUser(String[] userName);

    ResponseMsg proRegisterCode(String customName, String userName);

    ResponseMsg upload(MultipartFile file, String userName, String path);

    ResponseMsgAdd download(String fileName, String userName, String path);

    List<FileMsg> userFileList(String userName, String path);

    ResponseMsg userFileDelete(String fileName, String userName, String path);

    ResponseMsg fileRename(String oldName, String newName, String userName, String path);

    ResponseMsg userDirCreate(String dirName, String path, String userName);

    ResponseMsg shareCallBack(String link);

    List<FileMsg> search(String key, String userName, String path);

    String share(String link);

    ResponseMsg shareFileSecret(String link, String secret);

    boolean mergeChunks(String fileName, String userName, String path) throws InterruptedException;

    ResponseMsg generateShareLink(@RequestParam String expireDay, String fileName, String path, String userName);

    ResponseMsg fileMove(String fileName, String oldPath, String newPath, String userName);

    ResponseMsg checkChunk(String userName, String fileName, String chunk, String chunkSize);

    LinkSecret shareFile(String secretLink);

    List<ShareMessage> shareRecord(String userName);

    ResponseMsg shareToMyPan(User user, String path, String link);

    boolean healthCheck();
}
