package com.whut.pan.service;

import com.whut.pan.dao.model.User;

import java.util.List;
import java.util.Map;

/**
 * @author Sandeepin
 */
public interface IUserService {
    int alterPassword(String userName, String secret);

    int add(User user);

    int update(User user);

    int deleteByIds(String[] ids);

    int deleteByUsernames(String[] userNames);

    User queryUserByUsername(String userName);

    List<User> queryUserList(Map<String, Object> params);
}
