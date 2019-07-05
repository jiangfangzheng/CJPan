package com.whut.pan.controller;

import com.whut.pan.domain.ResponseMsg;
import com.whut.pan.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.whut.pan.Activate.panRestV1Service;
import static com.whut.pan.util.SystemUtil.getUserBySession;
import static com.whut.pan.util.SystemUtil.getUserNameBySession;

/**
 * 鉴权接口, 设定权限0(最高)
 *
 * @author Sandeepin
 * @date 2019/7/2 0002
 */
@Controller
public class AuthController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 登录
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request) {
        Map<String, Object> map = panRestV1Service.login(request.getParameter("userName"), request.getParameter("password"));
        if ("1".equals(map.get("result"))) {
            String user = (String) map.get("userObj");
            request.getSession().setAttribute("user", user);
            map.remove("userObj");
        }
        return map;
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/quit")
    public String loginOut(HttpServletRequest request) {
        // 清除session
        request.getSession().invalidate();
        logger.warn("退出登录成功！");
        return "login";
    }

    /**
     * 注册
     *
     * @param alias
     * @param userName
     * @param password
     * @param regcode
     * @param email
     * @param phone
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> signin(@RequestParam(required = false) String alias,
                                      @RequestParam(required = true) String userName,
                                      @RequestParam(required = true) String password,
                                      @RequestParam(required = false) String regcode,
                                      @RequestParam(required = false) String email,
                                      @RequestParam(required = false) String phone, HttpServletRequest request,
                                      HttpServletResponse response) {
        return panRestV1Service.signin(alias, userName, password, regcode, email, phone);
    }

    @RequestMapping(value = "/username", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg fileRename(HttpServletRequest request) {
        ResponseMsg j = new ResponseMsg();
        String userName = getUserNameBySession(request);
        if (userName == null) {
            userName = "null";
        }
        j.setMsg(userName);
        j.setSuccess(true);
        return j;
    }

    @RequestMapping(value = "/getUserByUserName")
    @ResponseBody
    public User getUserByUserName(HttpServletRequest request) {
        return getUserBySession(request);
    }

    /**
     * 更新当前用户信息
     *
     * @param username
     * @param alias
     * @param password
     * @param email
     * @param phone
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateUserByUserName")
    @ResponseBody
    public ResponseMsg updateUserByUserName(@RequestParam(required = false) String username,
                                            @RequestParam(required = false) String alias,
                                            @RequestParam(required = false) String password,
                                            @RequestParam(required = false) String email,
                                            @RequestParam(required = false) String phone,
                                            HttpServletRequest request) {
        return panRestV1Service.updateUserByUserName(username, alias, password, email, phone, getUserBySession(request));
    }

    /**
     * 检查是否是登录状态
     *
     * @param savePath
     * @param request
     * @return
     */
    @RequestMapping(value = "/islogin")
    @ResponseBody
    public ResponseMsg isLogin(@RequestParam(required = false) String savePath, HttpServletRequest request) {
        ResponseMsg j = new ResponseMsg();
        // 获取用户名
        User user = getUserBySession(request);
        if (user == null) {
            //未登录，跳转到登录界面，登录之后默认保存到网盘连接的地址<a href="wut://pan">链接到app</a>
            j.setMsg("未登录");
            j.setSuccess(false);
        } else {
            j.setMsg("已登录");
            j.setSuccess(true);
        }
        return j;
    }

    /**
     * 用户修改密码的接口，可以直接访问(管理员维护的接口 )
     *
     * @param userName
     * @param password
     */
    @RequestMapping("/alterPassword")
    @ResponseBody
    public void alterSecret(@RequestParam String userName, @RequestParam String password) {
        panRestV1Service.alterSecret(userName, password);
    }

    /**
     * 根据用户名删除用户，可以直接访问
     *
     * @param userName
     */
    @RequestMapping("/deleteUser")
    @ResponseBody
    public void deleteUser(@RequestParam String[] userName) {
        panRestV1Service.deleteUser(userName);
    }


    /**
     * 用户产生验证码的接口，只有特定用户可以访问
     */
    @RequestMapping(value = "/registerCode")
    public ModelAndView registerCode(ModelAndView modelAndView, HttpServletRequest request) {
        String username = getUserNameBySession(request);
        if (username.equals("fjc") || username.equals("jfz")) {
            modelAndView.setViewName("registerCode");
            return modelAndView;
        } else {
            modelAndView.setViewName("errorPage");
            modelAndView.addObject("message", "没有权限生成验证码！");
            return modelAndView;
        }
    }

    /**
     * 根据操作人的名字和要验证码人的名字来生成注册码
     *
     * @param customName
     * @param request
     * @return
     */
    @RequestMapping(value = "proRegisterCode", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseMsg proRegisterCode(@RequestParam String customName, HttpServletRequest request) {
        return panRestV1Service.proRegisterCode(customName, getUserNameBySession(request));
    }
}
