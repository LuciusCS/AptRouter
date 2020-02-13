package com.example.api.core;

import com.example.annotation.model.RouterBean;

import java.util.Map;

/**
 *
 * 路由Group对应的详细Path加载数据接口
 * 比如 app分组对应有哪些类需要进行加载
 *
 */
public interface ARouterLoadPath {

    /**
     * 加载路由组Group中Path详细数据
     * 比如：“app“ 分组下有这些信息
     *
     * @return key:"/app/MainActivity"  value:MainActivity信息封装到RouterBean中
     *
     */
    Map<String, RouterBean>loadPath();

}
