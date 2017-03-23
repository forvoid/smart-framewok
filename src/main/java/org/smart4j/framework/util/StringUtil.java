package org.smart4j.framework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by forvoid on 3/17/2017.
 */
public final class StringUtil {
    /**
     * 判断字符串是否为空
     * */
    public static boolean isEmpty(String str) {
        if (str != null) {
            str = str.trim();
        }
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    public static String[] splitString(String str, String split) {
        return StringUtils.splitByWholeSeparator(str, split);
    }

}
