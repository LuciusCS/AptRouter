package com.example.aptrouter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annotation.ARouter;
import com.example.annotation.Parameter;


@ARouter(path = "/app/Main2Activity")
public class Main2Activity extends AppCompatActivity {

    @Parameter
    String username;

    @Parameter
    boolean success;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
