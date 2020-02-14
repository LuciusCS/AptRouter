package com.example.aptrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.annotation.ARouter;
import com.example.annotation.Parameter;
import com.example.annotation.model.RouterBean;
import com.example.api.core.ARouterLoadGroup;
import com.example.api.core.ARouterLoadPath;

import com.example.aptrouter.apt.ARouter$$Group$$order;
import com.example.aptrouter.apt.ARouter$$Group$$personal;

import com.example.modular.personal.PersonalSecondActivity;
import com.example.moduler.order.OrderSecondActivity;

import java.util.Map;


@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {


    @Parameter
    String name;

    @Parameter(name = "agex")
    int age=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        BuildConfig.APPLICATION_ID

        name=getIntent().getStringExtra("name");
        age=getIntent().getIntExtra("agex",age);

    }


    public void jump(View view) {

//        Class<?> targetClass=SecondActivity$$ARouter.findTargetClass("/app/SecondActivity");
//        startActivity(new Intent(this,targetClass));
//        最终集成化模式，所有子模块APT生成为类文件都会打包到apk中
        ARouterLoadGroup loadGroup =new ARouter$$Group$$personal();
        Map<String,Class<?extends ARouterLoadPath>>groupMap=loadGroup.loadGroup();
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

    public void jumporder(View view) {

//        Class<?> targetClass=SecondActivity$$ARouter.findTargetClass("/app/SecondActivity");
//        startActivity(new Intent(this,targetClass));
//        最终集成化模式，所有子模块APT生成为类文件都会打包到apk中
        ARouterLoadGroup loadGroup =new ARouter$$Group$$order();
        Map<String,Class<?extends ARouterLoadPath>>groupMap=loadGroup.loadGroup();
        //app - personal
        Class<? extends ARouterLoadPath>clazz=groupMap.get("order");
//        类加载技术
        try {
            ARouterLoadPath aRouterLoadPath=clazz.newInstance();
            Map<String, RouterBean> pathMap = aRouterLoadPath.loadPath();
            //获取personal/Personal_MainActivity
            RouterBean routerBean=pathMap.get("/order/OrderSecondActivity");
            if (routerBean!=null){
                Intent intent=new Intent(this, OrderSecondActivity.class);
                intent.putExtra("name","ccerf");
                intent.putExtra("agex",25);

                startActivity(intent);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

}
