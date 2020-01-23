package com.example.aptrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.annotation.ARouter;


@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        BuildConfig.APPLICATION_ID
    }



    public void jump(View view) {

        Class<?> targetClass=SecondActivity$$ARouter.findTargetClass("/app/SecondActivity");
        startActivity(new Intent(this,targetClass));
    }
}
