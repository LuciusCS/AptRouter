package com.example.modular.personal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annotation.ARouter;
import com.example.annotation.Parameter;
import com.example.api.ParameterManager;
import com.example.api.RouterManager;
/**
 * 在反复跳转时，Activity 需要设置为singleTask
 */
@ARouter(path = "/personal/PersonalSecondActivity")
public class PersonalSecondActivity extends AppCompatActivity {


    @Parameter
    String name;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_second);

        ParameterManager.getInstance().loadParameter(this);
        Log.e("PersonalSecondActivity", "name>>>" + name + "++++");

    }

    public void jumpApp(View view) {
        RouterManager.getInstance().build("/app/MainActivity")
                .withResultString("call","callback")
                .navigation(this);

    }

    public void Order(View view) {
        RouterManager.getInstance().build("/order/OrderSecondActivity")
                .navigation(this,200);

    }
}
