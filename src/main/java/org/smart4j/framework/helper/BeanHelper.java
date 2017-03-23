package org.smart4j.framework.helper;

import org.smart4j.framework.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by forvoid on 3/22/2017.
 * bean助手
 */
public final class BeanHelper {
    public static int count = 0;
    /**
     * 定义bean映射（bean与bean的映射关系）
     * */
    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<Class<?>, Object>();

    static {
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for (Class<?> cls : beanClassSet) {
            Object obj = ReflectionUtil.newInstance(cls);
            BEAN_MAP.put(cls,obj);

        }
    }

    /**
     * 获取bean映射
     * */
    public static Map<Class<?>, Object> getBeanMap() {
        return BEAN_MAP;
    }

    /**
     * 获取bean实例
     * */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> cls) {
        if (!BEAN_MAP.containsKey(cls)) {
            throw new RuntimeException("can not get bean by class : " +cls);
        }
        return (T) BEAN_MAP.get(cls);

    }


}
