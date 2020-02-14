package com.example.aptrouter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annotation.ARouter;
import com.example.annotation.Parameter;


@ARouter(path = "/app/Main3Activity")
public class Main3Activity extends AppCompatActivity {

    @Parameter
    String password;

    @Parameter
    int gender=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }
}
