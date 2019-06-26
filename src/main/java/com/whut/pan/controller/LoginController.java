package com.whut.pan.controller;

import com.whut.pan.domain.ResponseMsg;
import com.whut.pan.domain.User;
import com.whut.pan.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.whut.pan.service.impl.FileServiceImpl.fileRootPath;
import static com.whut.pan.util.FileUtil.deleteDir;
import static com.whut.pan.util.WebUtil.getSessionUserName;


/**
 * 登录
 * 管理员admin 密码123 权限0(最高)
 *
 * @author Sandeepin
 */
@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IUserService userService;

    private List<String> codeLsit = new ArrayList<String>() {{
        add("娄平");add("韩屏");add("张小梅");add("江雪梅");add("魏勤");add("方艺霖");
        add("严俊伟");add("胡建民");add("冯嘉诚");add("李炆峰");add("蒋方正");
        add("陶媛媛");add("姜静");add("杨柳");add("徐雅");add("魏世杰");add("董晓东");
        add("杨赟");add("祁衍");add("葛锐嘉");add("汤君逸");add("高根源");add("陈婷灵");
        add("朱盼盼");add("李依侬");add("于士玉");add("王笑");add("徐澜瑛");add("刘一凡");
        add("易拓");
    }};
    private String nameManager1 = "CFlower is best!";
    private String nameManager2 = "sandeepin poi";

    // 登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        User dataBaseUser = userService.queryUserByUsername(userName);
        if (dataBaseUser != null && password.equals(dataBaseUser.getPassWord())) {
            User user = new User(userName, password, dataBaseUser.getLevelType(), dataBaseUser.getEmail(), dataBaseUser.getPhone());
            request.getSession().setAttribute("user", user);
            logger.info("用户登录成功！");
            map.put("result", "1");
        } else if (dataBaseUser != null && !password.equals(dataBaseUser.getPassWord())) {
            logger.info("密码错误！");
            map.put("result", "2");
        } else {
            logger.info("用户不存在！");
            map.put("result", "0");
        }
        return map;
    }

    // 退出登录
    @RequestMapping(value = "/quit", method = RequestMethod.GET)
    public String loginOut(HttpServletRequest request) {
        // 删除用户文件
        deleteDir(fileRootPath + getSessionUserName(request));
        // 清除session
        request.getSession().invalidate();
        logger.info("退出登录成功！");
        return "login";
    }

    // 注册
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> signin(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String regcode = request.getParameter("regcode");
        User dataBaseUser = userService.queryUserByUsername(userName);

        if(!regcode.contains("娄老师项目组@") && !nameManager1.equals(regcode) && !nameManager2.equals(regcode)) {
            logger.info("注册失败，激活码不正确！");
            map.put("result", "2");
            return map;
        }

        if(nameManager1.equals(regcode) || nameManager2.equals(regcode) || codeLsit.contains(regcode.split("@",-1)[1])){
            if (dataBaseUser == null) {
                User user = new User(userName, password, "0", email, phone);
                userService.add(user);
//            request.getSession().setAttribute("user", user);
                logger.info("账号注册成功！");
                map.put("result", "1");
            } else {
                logger.info("用户已经存在，请登录或换一个用户名！");
                map.put("result", "0");
            }
        }
        else {
            logger.info("注册失败，激活码不正确！");
            map.put("result", "2");
        }
        return map;
    }

    @RequestMapping(value = "/username", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg fileRename(HttpServletRequest request) {
        ResponseMsg j = new ResponseMsg();
        // 获取用户名
        User user = (User) request.getSession().getAttribute("user");
        String userName = user.getUserName();
        if (userName == null) {
            userName = "null";
        }
        j.setMsg(userName);
        j.setSuccess(true);
        return j;
    }

}
