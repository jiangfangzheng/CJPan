package com.whut.pan;

import com.whut.pan.service.IPanRestV1Service;
import hprose.client.HproseHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${panRestV1Host0}")
    public String panRestV1Host0;

    @Value("${panRestV1Host1}")
    public String panRestV1Host1;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("========== init project start ===========");
        LOGGER.warn("panRestV1Host0:{} Host1:{}", panRestV1Host0, panRestV1Host1);
        LOGGER.warn("rpc hprose chealthCheck():{}", activatePanRestV1Service(panRestV1Host0, panRestV1Host1));
        LOGGER.info("==========  init project end  ===========");
    }

    private static boolean activatePanRestV1Service(String host0, String host1) {
        String[] panRestV1Hosts;
        if (StringUtils.isEmpty(host0) && StringUtils.isNotEmpty(host1)) {
            panRestV1Hosts = new String[]{host1};
        } else if (StringUtils.isNotEmpty(host0) && StringUtils.isEmpty(host1)) {
            panRestV1Hosts = new String[]{host0};
        } else {
            panRestV1Hosts = new String[]{host0, host1};
        }
        CLIENT.useService(panRestV1Hosts);
        // 超时时间
        CLIENT.setTimeout(30000);
        // 自动切换服务地址
        CLIENT.setFailswitch(true);
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
