package com.example.aptrouter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.annotation.ARouter;
import com.example.annotation.Parameter;
import com.example.annotation.model.RouterBean;
import com.example.api.RouterManager;
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


    public void jumpPersonal(View view) {
        RouterManager.getInstance().build("/personal/PersonalSecondActivity")
                .withString("name", "12355666")
                .navigation(this,200);

    }

    public void jumporder(View view) {

        RouterManager.getInstance().build("/order/OrderSecondActivity")
                .withString("name", "123")
                .navigation(this,200);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.e("MainActivity", "回调" + data.getStringExtra("call"));
        }
    }

}
