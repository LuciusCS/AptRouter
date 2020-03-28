package com.example.glidemodule.pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.example.glidemodule.resources.Value;

import java.util.TreeMap;

public class BitmapPoolImpl extends LruCache<Integer,Bitmap> implements BitmapPool {


    String TAG=BitmapPoolImpl.class.getSimpleName();

    //为了筛选出合适的 Bitmap 容器
    private TreeMap<Integer,String>treeMap=new TreeMap<>();

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapPoolImpl(int maxSize) {
        super(maxSize);
    }

    @Override
    public void put(Bitmap bitmap) {
        //存入复用池
        bitmap.isMutable();
        //todo 条件一 bitmap.isMutable()==true;
        if (!bitmap.isMutable())
        {
            if (bitmap.isRecycled()==false){
                bitmap.recycle();
            }

            Log.e(TAG,"put: 条件一 bitmap.isMutable==true 不满足，不能存入复用池");
            return;
        }

        //todo 条件二 就计算bitmap的大小,
        int bitmapSize = getBitmapSize(bitmap);
        if (bitmapSize>maxSize()){
            if (bitmap.isRecycled()==false){
                bitmap.recycle();
            }
            Log.e(TAG,"put： 条件二 大于maxSize 不满足，不能存入复用池");
            return;
        }

        //todo bitmap存入 LruCache
        put(bitmapSize,bitmap);

        //存入筛选容器
        treeMap.put(bitmapSize,null);
    }

    /**
     * 获取可以复用Bitmap
     * @param w
     * @param h
     * @param config
     * @return
     */
    @Override
    public Bitmap get(int w, int h, Bitmap.Config config) {

        /**
         * ALPHA_8 理论上 实际上Android自动做处理的  只有透明度 8 位，一个字节
         * w*h*1
         *
         * RGB_565 理论上 实际上Android自动处理的  R 5位 G 6位 B 5位 没有透明度 两个字节
         * w*h*2
         *
         * ARGB_4444  理论上 实际上Android自动处理的 A 4位 R 4位 G 4位 B 4位  两个字节
         *
         *
         * 质量最高：
         * ARGB_8888  Android默认使用  A 8位 R 8位 G 8位 B 8位  四个字节
         *
         *
         * 常用的 ARGB_8888 RGB_565
         *
         */

        int getSize=w*h*(config==Bitmap.Config.ARGB_8888?4:2);  //只考虑两种，Glide所有的都考虑了

        Integer key = treeMap.ceilingKey(getSize);//可以查找到容器里面 和 getSize一样打的，也可以比getSize还要大的

        //如果treeMap还没有put,那么一定是null
        if (key==null){
            return null;   //没有找到合适的可复用的key
        }

        //查找容器取出来的key ,必须小于计算出来的 (getSize*2)
        if (key<=(getSize*2)){
            Bitmap remove=remove(key);  //复用池如果要取出来，肯定要取出来，不给其他地方用
            Log.e(TAG,"从复用池中取出复用元素 bitmap");
            return remove;
        }

        if (config==Bitmap.Config.ARGB_8888){

        }
        return null;
    }


    /**
     * 计算Bitmap的大小
     * @param bitmap
     * @return
     */
    private int getBitmapSize(Bitmap  bitmap){
        int sdkInt= Build.VERSION.SDK_INT;
        if (sdkInt>=Build.VERSION_CODES.KITKAT){
            return bitmap.getAllocationByteCount();
        }

        return bitmap.getByteCount();

    }

    //元素的大小
    @Override
    protected int sizeOf(@NonNull Integer key, @NonNull Bitmap value) {
//        return super.sizeOf(key, value);

        return getBitmapSize(value);
    }

    @Override
    protected void entryRemoved(boolean evicted, @NonNull Integer key, @NonNull Bitmap oldValue, @Nullable Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        //吧TreeMap里面的给移除
    }
}
