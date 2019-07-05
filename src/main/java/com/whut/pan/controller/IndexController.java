package com.whut.pan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static com.whut.pan.util.SystemUtil.getUserNameBySession;


/**
 * 主要页面映射
 *
 * @author Sandeepin
 */
@Controller
public class IndexController {

    /**
     * 管理页面
     *
     * @return 页面
     */
    @RequestMapping("/")
    public ModelAndView admin(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("author", getUserNameBySession(request));
        return modelAndView;
    }

    /**
     * 测试页面
     *
     * @return 页面
     */
    @RequestMapping("/test1")
    public String test() {
        return "test";
    }

    /**
     * 在线播放视频
     *
     * @return 页面
     */
    @RequestMapping("/onlineplayer")
    public ModelAndView onlineplayer(HttpServletRequest request, String fileName, String filePath) {
        ModelAndView modelAndView = new ModelAndView("onlineplayer");
        modelAndView.addObject("author", getUserNameBySession(request));
        modelAndView.addObject("fileName", fileName);
        modelAndView.addObject("filePath", filePath);
        return modelAndView;
    }

}
