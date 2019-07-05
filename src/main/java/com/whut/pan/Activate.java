package com.whut.pan;

import com.whut.pan.service.IPanRestV1Service;
import hprose.client.HproseHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 描述
 *
 * @author Sandeepin
 * @date 2019/6/30 0030
 */
@Component
public class Activate implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activate.class);

    private static final HproseHttpClient CLIENT = new HproseHttpClient();

    public static IPanRestV1Service panRestV1Service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("========== init project start ===========");
        LOGGER.warn("rpc调用，健康检查返回：{}", activatePanRestV1Service());
        LOGGER.info("==========  init project end  ===========");
    }

    private static boolean activatePanRestV1Service() {
        CLIENT.useService("http://localhost:32018/api/v1/old");
        try {
            // 通过接口调用
            panRestV1Service = CLIENT.useService(IPanRestV1Service.class);
            return panRestV1Service.healthCheck();
        } catch (Exception e) {
            LOGGER.error("HproseHttpClient call Exception.", e);
        }
        return false;
    }
}
