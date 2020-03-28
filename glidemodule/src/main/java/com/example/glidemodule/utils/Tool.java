package com.example.glidemodule.utils;

import android.graphics.Bitmap;
import android.os.Looper;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

public class Tool {

    public static void assertMainThread(){
        if (!isOnMainThread())
        {
            throw new IllegalArgumentException("You must call this method on the main thread");
        }
    }


    public static void assertBackgroundThread(){
        if (isOnBackgroundThread()){
            throw new IllegalArgumentException("You must call this method on a background thread");
        }
    }

    public static boolean isOnMainThread(){
        return Looper.myLooper()==Looper.getMainLooper();
    }

    public static boolean isOnBackgroundThread(){
        return !isOnMainThread();
    }


    @NonNull
    public static void checkNotEmpty(@Nullable String string){
        if (null==string){
            throw new IllegalArgumentException("Must not be empty ");
        }
    }

    @NonNull
    public static <T extends Collection<Y>,Y> T checkNotEmpty(@Nullable T collection){
        if (collection.isEmpty()){
            throw new IllegalArgumentException("Must not be empty 传递进来的值collection："+collection+"是null");
        }


        return collection;
    }


    public static void checkNotEmpty(Bitmap bitmap){
        if (null==bitmap){
            throw new IllegalArgumentException("Must not be empty 传递进来的值bitmap："+bitmap+"是null");
        }
    }



    /**
     * 利用Java原生的摘要事项SHA256加密
     * @param str 加密后的保温
     * @return
     */
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr="";
        try {
            messageDigest=MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr=byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encodeStr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer=new StringBuffer();
        String tmp=null;
        for (int i=0;i<bytes.length;i++){
            tmp=Integer.toHexString(bytes[i]&0xff);
            if (tmp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(tmp);
        }

        return stringBuffer.toString();

    }

    public static void checkNotEmpty(ImageView imageView){
        if (imageView==null){
            throw new IllegalArgumentException("Must not be empty 传进来的imageView："+imageView+"是null");
        }
    }


}
