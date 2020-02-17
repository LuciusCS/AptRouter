package com.example.glidemodule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView1,imageView2,imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView1=findViewById(R.id.image1);
        imageView2=findViewById(R.id.image2);
        imageView3=findViewById(R.id.image3);
    }

    public void t1(View view) {
        Glide.with(this)
                .load("https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy90cm01Vk1lRnA5blVESWdHaWJkVnlFSnk3MmlieVdXOElDWGlhamZDNlliNTc3YnVRTUdtYmUxR1hxSmhwVjNvUzhyWktENTIzODNVZDFpYkh6VTJDakxrUUEvNjQw?x-oss-process=image/format,png")
              .into(imageView1);
    }

    public void t2(View view) {
        Glide.with(this).load("https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy90cm01Vk1lRnA5blVESWdHaWJkVnlFSnk3MmlieVdXOElDWGlhamZDNlliNTc3YnVRTUdtYmUxR1hxSmhwVjNvUzhyWktENTIzODNVZDFpYkh6VTJDakxrUUEvNjQw?x-oss-process=image/format,png")
                .into(imageView2);
    }

    public void t3(View view) {
        Glide.with(this).load("https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy90cm01Vk1lRnA5blVESWdHaWJkVnlFSnk3MmlieVdXOElDWGlhamZDNlliNTc3YnVRTUdtYmUxR1hxSmhwVjNvUzhyWktENTIzODNVZDFpYkh6VTJDakxrUUEvNjQw?x-oss-process=image/format,png")
                .into(imageView3);
    }
}
