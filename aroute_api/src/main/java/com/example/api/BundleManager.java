package com.example.api;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 参数管理
 */
public class BundleManager {

    private Bundle bundle = new Bundle();

    //是否回调serResult();

    private boolean isResult;

    public Bundle getBundle() {
        return bundle;
    }

    public boolean isResult() {
        return isResult;
    }

    //对外提供传参方法
    //@NonNull不允许传null, @Nullable 可以传null
    public BundleManager withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    //示例代码，需要根据情况进行拓展
    public BundleManager withResultString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        isResult = true;
        return this;
    }

    public BundleManager withBoolean(@NonNull String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(@NonNull String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(@NonNull Bundle bundle){
        bundle=bundle;
        return this;
    }

    //直接跳转Activity
    public Object navigation(Context context){
        return navigation(context,-1);

    }


    //forResult

    /**
     *
     * @param context
     * @param code  可能是resultCode, 也可以是requestCode, 取决于 isResult
     * @return
     */
    public Object navigation(Context context,int code){
        return RouterManager.getInstance().navigation(context,this,code);

    }

}