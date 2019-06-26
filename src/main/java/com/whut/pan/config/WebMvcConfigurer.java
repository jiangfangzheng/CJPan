package com.whut.pan.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

import static com.whut.pan.util.SystemUtil.isWindows;


/**
 * Web配置
 *
 * @author Sandeepin
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {


    /**
     * 配置静态访问资源
     *
     * @param registry 注册
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // TODO Linxu下目录要改
        String downloadFilePath = "/root/pan/";
        if (isWindows()) {
            downloadFilePath = "D:\\logs\\";
        }
        registry.addResourceHandler("/data/**").addResourceLocations("file:" + downloadFilePath);
        super.addResourceHandlers(registry);
    }

    /**
     * 跳转页面
     *
     * @param registry 注册
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/toLogin").setViewName("login");
        super.addViewControllers(registry);
    }

    /**
     * 拦截器
     *
     * @param registry 拦截器注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(new WebInterceptor()).addPathPatterns("/**").excludePathPatterns("/toLogin", "/login",
                "/signin", "/data", "/test", "/upload", "/test1", "/share", "/sharefile");
        super.addInterceptors(registry);
    }

    /**
     * 配置fastJson
     *
     * @param converters converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastConverter);
        super.configureMessageConverters(converters);
    }

}
