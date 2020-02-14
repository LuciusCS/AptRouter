package com.example.api.core;

/**
 *
 * 参数Parameter加载接口
 *
 */
public interface ParameterLoad {
    /**
     * 目标对象，属性名=getIntent().属性类型("注解值或属性名");完成赋值
     * @param target 目标对象，如MainActivity(中的某些属性)
     */
    void loadParameter(Object target);
}
