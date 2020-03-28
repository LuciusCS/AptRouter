package com.example.glidemodule.cache;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.example.glidemodule.resources.Value;

/**
 * 内容缓存 --LRU算法
 *
 * LruCache 有多个
 */
public class MemoryCache extends LruCache <String, Value>{

    private boolean manualRemove;

    //TODO 手动移除 Manual
    public Value manualRemove(String key){
        manualRemove=true;
       Value value =remove(key);
        manualRemove=false;
       return value;
    }

    //put 父类中已经有了
    //get 父类中已经有了


    private MemoryCacheCallback memoryCacheCallback;

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }

    /**
     * 传入元素最大值给 LruCache
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(@NonNull String key, @NonNull Value value) {
//        return super.sizeOf(key, value);
        Bitmap bitmap=value.getBitmap();

        //最开始的的时候
//        int result=bitmap.getRowBytes()*bitmap.getHeight();
        //API 12  3.0
//        int result=bitmap.getByteCount();  //bitmap内存复用上有区别(所属的)
        //API 19 4.4
//        int result=bitmap.getAllocationByteCount();//bitmap内存复用上有区别(整个的)
        int sdkInt= Build.VERSION.SDK_INT;
        if (sdkInt>=Build.VERSION_CODES.KITKAT){
            return bitmap.getAllocationByteCount();
        }

        return bitmap.getByteCount();

    }

    /**
     * 1. 重复的key
     * 2. 最少使用的元素会被移除
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */

    @Override
    protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull Value oldValue, @Nullable Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (memoryCacheCallback!=null&&!manualRemove){  //被动删除
            memoryCacheCallback.entryRemoveMemoryCache(key,oldValue);
        }
    }
}
