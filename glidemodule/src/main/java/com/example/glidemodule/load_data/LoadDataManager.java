package com.example.glidemodule.load_data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.glidemodule.resources.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoadDataManager implements ILoadData,Runnable {

    private final String TAG=LoadDataManager.class.getSimpleName();

    //使用线程池的方式进行加载
    private String path;
    private ResponseListener responseListener;
    private Context context;

    @Override
    public Value loadResource(String path, ResponseListener responseListener, Context context) {

        this.path=path;
        this.responseListener=responseListener;
        this.context=context;

        //加载 网络图片/
        Uri uri=Uri.parse(path);

        if ("HTTP".equalsIgnoreCase(uri.getScheme())||"HTTPS".equalsIgnoreCase(uri.getScheme())){
            new ThreadPoolExecutor(0,Integer.MAX_VALUE,
                    60,
                    TimeUnit.SECONDS,new SynchronousQueue<Runnable>()).execute(this);
        }

        //SD本地图片加载，会返回Value

        //......

        return null;
    }

    @Override
    public void run() {

        InputStream inputStream=null;

        HttpURLConnection httpURLConnection=null;

        try {
            URL url=new URL(path);
            URLConnection urlConnection=url.openConnection();

            httpURLConnection =(HttpURLConnection)urlConnection;
            httpURLConnection.setConnectTimeout(5000);       //设置连接超时时间
            final int responseCode=httpURLConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK==responseCode){
               inputStream= httpURLConnection.getInputStream();

               final  Bitmap bitmap= BitmapFactory.decodeStream(inputStream);

                //切换主线程
               new Handler(Looper.getMainLooper()).post(new Runnable() {
                   @Override
                   public void run() {

                       Value value=Value.getInstance();
                       value.setBitmap(bitmap);

                       //回调成功
                       responseListener.responseSuccess(value);
                   }
               });

            }else {
                //切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        responseListener.responseException(new IllegalArgumentException("请求失败 请求码："+responseCode));
                    }
                });
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"run : 关闭 inputStream.close(); e: "+e.getMessage());
                }
            }

            if (httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }

    }
}
