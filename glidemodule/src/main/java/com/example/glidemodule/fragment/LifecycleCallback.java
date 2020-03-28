package com.example.glidemodule.fragment;

public interface LifecycleCallback {

    //生命周期初始化
    public void glideInitAction();

    public void glideStopAction();

    //生命周期回收
    public void glideRecycleAction();

}
