package com.example.glidemodule;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.glidemodule.fragment.ActivityFragmentManager;
import com.example.glidemodule.fragment.FragmentActivityFragmentManager;



/**
 * 管理RequestManager
 */
public class RequestManager {

    private final String TAG=RequestManager.class.getSimpleName();

    private final Context requestManagerContext;

    private final String FRAGMENT_ACTIVITY_NAME="Fragment_Activity_NAME";

    private final String ACTIVITY_NAME="activity_name";

    private final int NEXT_HANDLER_MSG= 9;

    private static RequestTargetEngine requestTargetEngine;

    //构造代码块，不用再在所有的构造方法里面进行实例化，统一去写
    {
        if (requestTargetEngine==null){
            requestTargetEngine=new RequestTargetEngine();
        }
    }

    /**
     * 可以管理生命周期 --FragmentActivity 可以进行管理生命周期
     * @param fragmentActivity
     */
   FragmentActivity fragmentActivity;

    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext=fragmentActivity;
        this.fragmentActivity=fragmentActivity;

        //拿到Fragment
       FragmentManager supportFragmentManager= fragmentActivity.getSupportFragmentManager();
        Fragment fragment=supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (null==fragment){
            //如果为null ，则需要创建Fragment
            fragment=new FragmentActivityFragmentManager(requestTargetEngine);  //Fragment的生命周期与requestTargetEngine关联
            //添加到 supportActivityManager
            supportFragmentManager.beginTransaction().add(fragment,FRAGMENT_ACTIVITY_NAME).commit();


        }
        Fragment fragment2 = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);   //如果不添加 fragmenr2 为null ,还在排队中，没有被消费
        Log.e(TAG, "RequestManager: fragment2:" + fragment2);

        //发送一次Handler
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

    }

    /**
     * 可以管理生命周期  --Activity可以进行管理
     * @param activity
     */
    public RequestManager(Activity activity) {
        this.requestManagerContext=activity;

        //拿到Fragemnt
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(ACTIVITY_NAME);

        if (null==fragment){
            fragment=new ActivityFragmentManager(requestTargetEngine);
            //添加到管理器  ---- fragmentManager.beginTransaction().add()  与 handler有关
            fragmentManager.beginTransaction().add(fragment,ACTIVITY_NAME).commitAllowingStateLoss(); //提交
        }

//        android.app.Fragment fragment2 = fragmentManager.findFragmentByTag(ACTIVITY_NAME);   //fragmenr2 为null ,还在排队中，没有被消费
//        Log.d(TAG,"RequestManager: fragment2:"+fragment2);

        //发送一次Handler，把排队中的进行消费(让fragment不在排队中，让下一次能取到), Android 基于 Handler消息的
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
    }

    private Handler mHandler=new Handler(new Handler.Callback(){

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Fragment fragment2 = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);   //fragmenr2 不为null ,不在排队中，被消费
            Log.e(TAG, "Handler: fragment2:" + fragment2);

            return false;
        }
    }) ;

    /**
     * 无法管理生命周期 因为Application无法管理
     * @param context
     */
    public RequestManager(Context context) {

        this.requestManagerContext=context;
    }

    /**
     * load 拿到显示图片的路径
     * @param s
     * @return
     */
    public RequestTargetEngine load(String path) {

        //移除handler
        mHandler.removeMessages(NEXT_HANDLER_MSG);

        //把值传递给资源加载引擎
        requestTargetEngine.loadValueInitAction(path,requestManagerContext);

        return requestTargetEngine;
    }
}
