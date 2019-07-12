package com.whut.pan.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述
 *
 * @author Sandeepin
 * @date 2019/7/10 0010
 */
class PrintStream extends Thread {

    private static Logger logger = LoggerFactory.getLogger(PrintStream.class);

    java.io.InputStream __is = null;

    StringBuilder stringBuilder = new StringBuilder();

    public PrintStream(java.io.InputStream is) {
        __is = is;
    }

    @Override
    public void run() {
        try {
            while (this != null) {
                int _ch = __is.read();
                if (_ch == -1) {
                    break;
                } else {
                    stringBuilder.append((char) _ch);
                }
                if (stringBuilder.toString().contains("\r")) {
                    logger.warn("FFmpeg: {}", stringBuilder);
                    stringBuilder.delete(0, stringBuilder.length());
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        }
    }
}