package com.example.moduler.order;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annotation.ARouter;
import com.example.annotation.Parameter;
import com.example.annotation.model.RouterBean;
import com.example.api.ParameterManager;
import com.example.api.RouterManager;
import com.example.api.core.ARouterLoadGroup;
import com.example.api.core.ARouterLoadPath;
import com.example.api.core.ParameterLoad;
//import com.example.aptrouter.apt.ARouter$$Group$$personal;
//import com.example.modular.personal.PersonalSecondActivity;

import java.util.Map;

/**
 * 在反复跳转时，Activity 需要设置为singleTask
 */

@ARouter(path = "/order/OrderSecondActivity")
public class OrderSecondActivity extends AppCompatActivity {


    @Parameter
    String name;

    @Parameter(name = "agex")
    int age = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_second);

//        ParameterLoad parameterLoad=new OrderSecondActivity$$Parameter();
//        parameterLoad.loadParameter(this);
//        if (getIntent()!=null){
//            Log.e("aaaaaaa","name>>>"+name+"++++"+age);
//        }
        //懒加载方式，跳转到哪加载哪个类
        ParameterManager.getInstance().loadParameter(this);
        Log.e("OrderSecondActivity", "name>>>" + name + "++++" + age);

    }

    /*
        public void jump(View view) {

            Class<?> targetClass=SecondActivity$$ARouter.findTargetClass("/app/SecondActivity");
            startActivity(new Intent(this,targetClass));
    //        最终集成化模式，所有子模块APT生成为类文件都会打包到apk中
            ARouterLoadGroup loadGroup =new ARouter$$Group$$personal();
            Map<String,Class<?extends ARouterLoadPath>> groupMap=loadGroup.loadGroup();
            //app - personal
            Class<? extends ARouterLoadPath>clazz=groupMap.get("personal");
    //        类加载技术
            try {
                ARouterLoadPath aRouterLoadPath=clazz.newInstance();
                Map<String, RouterBean> pathMap = aRouterLoadPath.loadPath();
                //获取personal/Personal_MainActivity
                RouterBean routerBean=pathMap.get("/personal/PersonalSecondActivity");
                if (routerBean!=null){
                    Intent intent=new Intent(this, PersonalSecondActivity.class);
                    startActivity(intent);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }
        */
    public void jumpApp(View view) {
        RouterManager.getInstance().build("/app/MainActivity")
                .withResultString("call","callback")
                .navigation(this);

    }


    public void jumpPersonl(View view) {
        RouterManager.getInstance().build("/personal/PersonalSecondActivity")
                .withString("name", "12355666")
                .navigation(this);

    }

    /**
     * 在反复跳转时，Activity 需要设置为singleTask
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.e("OrderSecondActivity", "回调" + data.getStringExtra("call"));
        }
    }
}
