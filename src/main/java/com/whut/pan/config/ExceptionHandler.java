package com.whut.pan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zc
 * @date 2018/10/20
 */
@ControllerAdvice
public class ExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String ERROR_VIEW = "errorPage";

    /**
     * 错误页处理
     *
     * @param request
     * @param response
     * @param e
     * @return
     * @throws Exception
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public Object errorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        logger.info("errorHandler():{}", e.getMessage());
        if (isAjax(request)) {
            return e.getMessage();
        } else {
            ModelAndView mav = new ModelAndView();
            mav.addObject("exception", e);
            mav.addObject("url", request.getRequestURL());
            mav.setViewName(ERROR_VIEW);
            return mav;
        }
    }

    /**
     * 判断是否是Adjx请求
     *
     * @param httpRequest
     * @return
     */
    private static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-Requested-With") != null &&
                "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With")));
    }
}
