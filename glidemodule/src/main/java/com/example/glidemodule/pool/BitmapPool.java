package com.example.glidemodule.pool;

import android.graphics.Bitmap;

/**
 * 复用池标准
 */
public interface BitmapPool {
    /**
     * 存入到Bitmap
     * @param bitmap
     */
    void put(Bitmap bitmap);

    /**
     * 获取匹配 可用复用 Bitmap
     * @param w
     * @param h
     * @return
     */
    Bitmap get(int w,int h,Bitmap.Config config);
}
