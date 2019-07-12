package com.whut.pan.config;

import static com.whut.pan.util.SystemUtil.isWindows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import javax.servlet.MultipartConfigElement;

/**
 * 链接：https://blog.csdn.net/llibin1024530411/article/details/79474953
 * 注意：springboot项目，部署到服务器后，运行一段时间后，处理一些文件上传接口时后报异常
 * Caused by: java.io.IOException: The temporary upload location [/tmp/tomcat.33230                                                                                                             11741980485887.8080/work/Tomcat/localhost/ROOT] is not valid
 * at org.apache.catalina.connector.Request.parseParts(Request.java:2844)
 * Created by zc on 2019/3/6.
 */
@Configuration
public class MultipartConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 文件上传临时路径
     */
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        if (!isWindows()) {
            String location = System.getProperty("user.dir") + "/data/tmp";
            // 目录为：/root/pan/data/tmp
            logger.warn("临时文件的目录更改于2019-3-6：" + location);
            File tmpFile = new File(location);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            factory.setLocation(location);
        }
        return factory.createMultipartConfig();
    }
}
