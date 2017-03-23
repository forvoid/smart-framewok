package org.smart4j.framework.helper;

import org.apache.commons.lang3.ArrayUtils;
import org.smart4j.framework.annotation.Inject;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by forvoid on 3/22/2017.
 * 依赖注入助手
 */
public class IocHelper {
    static {
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                    Class<?> beanClass = beanEntry.getKey();
                    Object beanInstance = beanEntry.getValue();
                //获得class的所有成员变量
                Field[]  beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    //遍历bean field
                    for (Field beanField : beanFields) {
                        //判断当前的bean field事否带有inject注释
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            //在bean map 中获取bean field 对应的实例
                            Class<?> beanFieldClass = beanField.getType();
                            Object beanFiledInstance = beanMap.get(beanFieldClass);
                            if (beanFiledInstance != null) {
                                //通过反射初始化beanField的值
                                ReflectionUtil.setField(beanInstance, beanField, beanFiledInstance);

                            }
                        }
                    }
                }
            }
        }
    }
}
