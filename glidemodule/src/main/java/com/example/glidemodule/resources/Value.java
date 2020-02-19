package com.example.glidemodule.resources;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.glidemodule.utils.Tool;

/**
 * 对Bitmap的封装
 */
public class Value {

    private final String TAG=Value.class.getSimpleName();


    //使用单利模式
    private static Value value;

    public static Value getInstance(){

        if (null==value){
            synchronized (Value.class){
                if (value==null){
                    value=new Value();
                }
            }
        }

        return value;

    }

    private Bitmap bitmap;

    //使用计数 +1 -1
    private int count;

    //监听
    private ValueCallback valueCallback;

    //定义key
    private String key;

    /**
     * 使用一次 +1
     */
    public void useAction(){

        Tool.checkNotEmpty(bitmap);

        if (bitmap.isRecycled()){   //已经被回收
            Log.d(TAG,"UseAction:加一 count："+count);

            return;
        }
        count++;

    }

    /**
     * 不使用或者使用完毕 -1
     * count--<=0 ，不再使用了
     */
    public void nonUseAction(){
        count--;
        if (count<=0){
            //回调给外界，，告诉不在使用了
            valueCallback.valueNonUseAction(key,this);

        };

    }

    /**
     * 释放
     */
    public void recycleBitma(){
        if (count>0){
            Log.d(TAG,"recycleBitmap: 引用计数大于0，证明还在使用，不能释放");
            return;
        }

        if (bitmap.isRecycled()){   //被回收了
            Log.d(TAG,"Bitmap被回收了");
            return;

        }


        bitmap.recycle();

        value=null;

        System.gc();
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ValueCallback getValueCallback() {
        return valueCallback;
    }

    public void setValueCallback(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
