package com.example.glidemodule.resources;

/**
 * 专门给 Value 不再施工的回调借口
 */
public interface ValueCallback {

    /**
     * 监听的方法（Value不再使用了）
     * @param key
     * @param value
     */
    public void valueNonUseAction(String key,Value value);
}
