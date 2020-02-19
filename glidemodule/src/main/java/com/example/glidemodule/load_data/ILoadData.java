package com.example.glidemodule.load_data;

import android.content.Context;

import com.example.glidemodule.resources.Value;

/**
 * 加载外部资源 标准
 */
public interface ILoadData {
    //加载外部资源的方法
    public Value loadResource(String path, ResponseListener responseListener, Context context);
}
