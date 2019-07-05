package com.whut.pan.config;

import com.whut.pan.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static com.whut.pan.util.SystemUtil.getUserBySession;


/**
 * 拦截配置
 *
 * @author Sandeepin
 */
public class WebInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("requestStartTime", System.currentTimeMillis());
        Method method = ((HandlerMethod) handler).getMethod();
        String logMsg = "用户ip:" + request.getRemoteAddr() + ",访问目标:" + method.getDeclaringClass().getName() + ":" + method.getName();
        logger.warn(logMsg);

        User user = getUserBySession(request);
        boolean flag = false;
        if (null == user) {
            logger.warn("未登录");
            response.sendRedirect("toLogin");
        } else {
            logger.warn("登录的账号:{}", user.getUserName());
            flag = true;
        }
        return flag;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        long startTime = (Long) request.getAttribute("requestStartTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        // 打印方法执行时间
        String logMsg;
        if (executeTime > 1000) {
            logMsg = "[" + method.getDeclaringClass().getName() + "." + method.getName() + "] 执行耗时 : " + executeTime + "ms";
            logger.warn(logMsg);
        } else {
            logMsg = "[" + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "] 执行耗时 : " + executeTime + "ms";
            logger.warn(logMsg);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
