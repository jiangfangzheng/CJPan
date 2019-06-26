package com.whut.pan.controller;

import com.alibaba.fastjson.JSON;
import com.whut.pan.domain.User;
import com.whut.pan.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sandeepin
 */
@RestController
public class TestController {

    @Autowired
    IUserService userService;

    @RequestMapping("/test")
    public String test() throws Exception {

        // 增添用户
//        User user1 = new User("poi","000","1","xx@163.com","13512345678");
//        Integer bool1 = userService.add(user1);
//        User user2 = new User("sandeepin","111","1","11@111.com","11111111111");
//        Integer bool2 = userService.add(user2);
        // 删除用户 By用户名
//        String[] userNames = {"poi"};
//        Integer bool3 = userService.deleteByUsernames(userNames);
        // 更改用户 By用户名
//        User user3 = new User("jfz","000","0","00@00.com","00000000000");
//        Integer bool4 = userService.update(user3);
        // 查询用户 By用户名
        User user4 = userService.queryUserByUsername("sandeepin");
        User user5 = userService.queryUserByUsername("bsfbrgvesfvsdr");
        // 查询用户 By各种参数
        Map<String, Object> params = new HashMap<>();
        params.put("phone","15578352978");
        List<User> userList = userService.queryUserList(params);
        String json = JSON.toJSONString(userList);

        if(user5 == null){
            return JSON.toJSONString(user4) + " ## user5 is null ## " + json;
        }
        return json;
    }
}


