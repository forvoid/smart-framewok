package org.smart4j.framework.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by forvoid on 3/22/2017.
 * 流操作工具类
 */
public final class StreamUtil {
    private static final Logger LOGGER = Logger.getLogger(StreamUtil.class);
    /**
     * 从输入流中获取字符串
     * */
    public static String getString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }catch (Exception e) {
            LOGGER.error("get string failure", e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}
