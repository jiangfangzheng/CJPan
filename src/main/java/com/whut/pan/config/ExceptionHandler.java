package com.whut.pan.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zc on 2018/10/20.
 */
@ControllerAdvice
public class ExceptionHandler {

    public static final String ERROR_VIEW = "errorPage";
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public Object errorHandler(HttpServletRequest reqest, HttpServletResponse response, Exception e) throws Exception {

        e.printStackTrace();

        if (isAjax(reqest)) {
            return e.getMessage();
        } else {
            ModelAndView mav = new ModelAndView();
            mav.addObject("exception", e);
            mav.addObject("url", reqest.getRequestURL());
            mav.setViewName(ERROR_VIEW);
            return mav;
        }
    }

    /**
     * 判断是否是Adjx请求
     * @param httpRequest
     * @return
     */
    public static boolean isAjax(HttpServletRequest httpRequest){
        return  (httpRequest.getHeader("X-Requested-With") != null
                && "XMLHttpRequest"
                .equals( httpRequest.getHeader("X-Requested-With").toString()) );
    }
}
