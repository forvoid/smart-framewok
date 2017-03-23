package org.smart4j.framework.bean;

import org.smart4j.framework.util.CastUtil;

import java.util.Map;

/**
 * Created by forvoid on 3/22/2017.
 * 请求参数对象
 */
public class Param {
    private Map<String, Object> paramMap;
    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 根据参数名获取long型参数值
     * */
    public long getLong(String name) {
        return CastUtil.castLong(paramMap.get(name));
    }

    /**
     * 根据参数名获取所有字段信息
     * */
    public Map<String, Object> getMap() {
        return paramMap;
    }
}
