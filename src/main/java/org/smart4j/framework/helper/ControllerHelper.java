package org.smart4j.framework.helper;

import org.smart4j.framework.annotation.Action;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Request;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by forvoid on 3/22/2017.
 * 控制器助手类
 */

public class ControllerHelper {
    /**
     * 存放请求与处理器映射关系
     * */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            //遍历这些ControllerClass类
            for (Class<?> controllerClass : controllerClassSet) {
                //获取method定义的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(Action.class)) {
                            //从注解中获取url映射规则
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();
                            //验证url映射规则
                            if (mapping.matches("\\w+:/\\w*")) {

                                String[] array = mapping.split(":");
                                if (array != null && array.length == 2) {
                                    Request request = new Request(array[0], array[1]);
                                    Handler handler = new Handler(controllerClass, method);
                                    //初始化ActionMap
                                    ACTION_MAP.put(request, handler);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 获得handler
     * */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
