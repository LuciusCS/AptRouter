package com.example.glidemodule.cache.disk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.example.glidemodule.resources.Value;
import com.example.glidemodule.utils.Tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 对磁盘缓存的封装
 */
public class DiskLruCacheImpl {

    //SD/disk_lru_cache_dir/ac43474d52403e60fe21894520a67d6f417a6868994c145eeb26712472a78311
    private final String DISKLRU_CACHE_DIR="disk_lru_cache_dir";  //磁盘缓存的目录

    private final int APP_VERSION=1;   //我们的版本号，一旦修改这个版本号，之前的缓存失效

    private final int VALUE_COUNT=1; //通常情况下是1

    private final long MAX_SIZE=1024*1024*10l;

    private DiskLruCache diskLruCache;

    public DiskLruCacheImpl() {

        File file=new File(Environment.getExternalStorageDirectory()+File.separator+DISKLRU_CACHE_DIR);


        try {
            diskLruCache= DiskLruCache.open(file,APP_VERSION,VALUE_COUNT,MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //TODO put
    public void put(String key, Value value){

        Tool.checkNotEmpty(key);

        DiskLruCache.Editor editor=null;

        OutputStream outputStream=null;

        try {
            editor=diskLruCache.edit(key);

           outputStream=editor.newOutputStream(0);  //index不能大于 VALUE_COUNT

            Bitmap bitmap=value.getBitmap();

            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);  //吧把BitMap 写入到outputStream

            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();

            //失败
            try {
                editor.abort();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }finally {
            try {
                editor.commit();
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (outputStream!=null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //TODO get
    public Value get(String key){

        Tool.checkNotEmpty(key);

        InputStream inputStream=null;



        try {
            DiskLruCache.Snapshot snapshot=diskLruCache.get(key);
            //判断快照不为null的情况下，再去读操作
            if (null!=snapshot){
                Value value=Value.getInstance();
                inputStream=snapshot.getInputStream(0);  //index不嗯给你大于VALUE_COUNT;
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                value.setBitmap(bitmap);
                //保存key唯一标识
                value.setKey(key);
                return value;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null!=inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
