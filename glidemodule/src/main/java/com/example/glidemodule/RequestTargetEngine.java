package com.example.glidemodule;

import android.util.Log;
import android.widget.ImageView;

import com.example.glidemodule.fragment.LifecycleCallback;

/**
 * 加载图片资源
 */
public class RequestTargetEngine implements LifecycleCallback {

    private final String TAG=RequestTargetEngine.class.getSimpleName();

    @Override
    public void glideInitAction() {
        Log.d(TAG,"glideInitAction：Glide生命周期 已经开启 初始化");
    }

    @Override
    public void glideStopAction() {
        Log.d(TAG,"glideStopAction：Glide生命周期 已经停止");
    }

    @Override
    public void glideRecycleAction() {
        Log.d(TAG,"glideStopAction：Glide生命周期 进行释放操作 ");
    }

    public void into(ImageView imageView) {
    }
}
