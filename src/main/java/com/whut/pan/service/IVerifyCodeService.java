package com.whut.pan.service;

import com.whut.pan.dao.model.VerifyCode;

import java.util.List;

/**
 * Created by zc on 2018/12/4.
 */
public interface IVerifyCodeService {
    List<VerifyCode> findVerifyCodeByCustomName(String customName);

    int modifyVerifyState(VerifyCode verifyCode);

    boolean save(VerifyCode verifyCode);

    VerifyCode findVerifyCodeByCOR(String customName, String operatePerson, String registerCode);

    boolean isValid(String registerCode);
}
