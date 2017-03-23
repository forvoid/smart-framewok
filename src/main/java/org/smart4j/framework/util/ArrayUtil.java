package org.smart4j.framework.util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by forvoid on 3/22/2017.
 */
public class ArrayUtil {
    public static boolean isEmpty(Object[] obj) {
        return ArrayUtils.isEmpty(obj);
    }
    public static boolean isNotEmpty(Object[] obj) {
        return !isEmpty(obj);
    }
}
