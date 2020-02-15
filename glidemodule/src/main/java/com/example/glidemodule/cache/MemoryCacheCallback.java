package com.example.glidemodule.cache;

import androidx.annotation.NonNull;

import com.example.glidemodule.resources.Value;

/**
 * 内存缓存中，元素被移除的接口回调
 */
public interface MemoryCacheCallback {

    /**
     * 内存缓存中移除的key
     * @param key
     * @param oldValue
     */
    public void entryRemoveMemoryCache(String key,@NonNull Value oldValue);
}
