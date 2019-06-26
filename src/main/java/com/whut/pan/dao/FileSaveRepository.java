package com.whut.pan.dao;

import com.whut.pan.domain.FileSave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FileSaveRepository extends JpaRepository<FileSave,Integer> {
    FileSave findFileSaveByLocalLink(String localLink);
    List<FileSave> findFileSavesByUserName(String useName);
    FileSave findFileSaveByPanPath(String panPath);
    FileSave findFileSaveByUserNameAndFileName(String userName,String fileName);
    FileSave save(FileSave fileSave);

    void delete(FileSave fileSave);

}
