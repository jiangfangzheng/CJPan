package com.whut.pan.service;

import com.whut.pan.dao.model.LinkSecret;

import java.util.Date;
import java.util.List;

/**
 * Created by zc on 2018/10/18.
 */
public interface LinkSecretService {
    LinkSecret findLinkSecretByLink(String link);

    LinkSecret findLinkSecretBysecretLink(String secretLink);

    List<LinkSecret> findLinkSecretsByFileName(String fileName);

    LinkSecret findLinkSecretByLocalLinkAndUserName(String localLink, String userName);

    LinkSecret save(LinkSecret linkSecret);

    LinkSecret deleteLinkSecretByLink(String link);

    int addOneToDownloadNum(LinkSecret linkSecret);

    Date updateExpireDay(LinkSecret linkSecret, Date date);

    List<LinkSecret> findLinkSecretsByUserName(String userName);

    Date updateShareDate(LinkSecret linkSecret, Date date);
}
