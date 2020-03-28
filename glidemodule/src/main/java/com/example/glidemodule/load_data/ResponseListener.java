package com.example.glidemodule.load_data;

import android.content.Context;

import com.example.glidemodule.resources.Value;

/**
 * 加载外部资源成功与失败的 回调
 */
public interface ResponseListener {

    public void responseSuccess(Value value);

    public void responseException(Exception e);

}
