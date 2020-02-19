package com.example.glidemodule;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.glidemodule.cache.ActiveCache;
import com.example.glidemodule.cache.MemoryCache;
import com.example.glidemodule.cache.MemoryCacheCallback;
import com.example.glidemodule.cache.disk.DiskLruCacheImpl;
import com.example.glidemodule.fragment.LifecycleCallback;
import com.example.glidemodule.load_data.LoadDataManager;
import com.example.glidemodule.load_data.ResponseListener;
import com.example.glidemodule.resources.Key;
import com.example.glidemodule.resources.Value;
import com.example.glidemodule.resources.ValueCallback;
import com.example.glidemodule.utils.Tool;

/**
 * 加载图片资源
 */
public class RequestTargetEngine implements LifecycleCallback , ValueCallback , MemoryCacheCallback , ResponseListener {

    private final String TAG=RequestTargetEngine.class.getSimpleName();

    @Override
    public void glideInitAction() {
        Log.d(TAG,"glideInitAction：Glide生命周期 已经开启 初始化");
    }

    @Override
    public void glideStopAction() {
        Log.d(TAG,"glideStopAction：Glide生命周期 已经停止");
    }

    @Override
    public void glideRecycleAction() {
        Log.d(TAG,"glideRecycleAction：Glide生命周期 进行释放操作 ");
        if (activeCache!=null){
            activeCache.closeThread();  //把活动缓存给释放掉
        }
    }

    //活动缓存
    private ActiveCache activeCache;
    //内存缓存
    private MemoryCache memoryCache;

    //磁盘缓存
    private DiskLruCacheImpl diskLruCache;

    //复用池 未写

    private final int MEMARY_MAX_SIZE=1024*1024*60;

    public RequestTargetEngine(){
        if (activeCache==null){
            activeCache=new ActiveCache(this);   //回调外界，Value 资源不再使用, 设置监听
        }
        if (memoryCache==null){
            memoryCache=new MemoryCache(MEMARY_MAX_SIZE);  //LRU最少使员工的元素会被移除， 设置监听
            memoryCache.setMemoryCacheCallback(this);
        }

        //初始化磁盘缓存
       diskLruCache=new DiskLruCacheImpl();

    }

    private String path;
    private Context glideContext;

    private String key;  //ac43474d52403e60fe21894520a67d6f417a6868994c145eeb26712472a78311

    private ImageView imageView; //显示的目标

    /**
     * RequestManager 传递的值
     */
    public void loadValueInitAction(String path, Context glideContext){
        this.path=path;
        this.glideContext=glideContext;
        this.key=new Key(path).getKey();  //ac43474d52403e60fe21894520a67d6f417a6868994c145eeb26712472a78311

    }

    public void into(ImageView imageView) {
        this.imageView=imageView;
        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();

        //TODO  加载资源 --- 》 缓存 ---》 网络/SD卡/ 加载资源 成功后 --》 资源保存到缓存中 》》
        Value value=cacheAction();
        if (null!=value){
            //使用完成了  减一
            value.nonUseAction();
            imageView.setImageBitmap(value.getBitmap());
        }
    }


    //TODO  加载资源 --- 》 缓存 ---》 网络/SD卡/ 加载资源 成功后 --》 资源保存到缓存中 》》
    private Value cacheAction(){

        //TODO 第一步 判断活动缓存是否有资源，如果有资源则 返回，否则继续往下找
        Value value=activeCache.get(key);
        if (null!=value){
            Log.d(TAG,"cacheAction: 本次加载是在活动缓存中共获取的资源》》");

            //返回 代表使用了 一次 Value
            value.useAction();
            return value;
        }

        //TODO 第二部， 从内存缓存中去找，如果找到了，内存缓存中的元素 “移动到” 活动缓存，然后再返回
        value=memoryCache.get(key);
        if (null!=value){
            //移动操作
            memoryCache.manualRemove(key); //移除内存缓存
            activeCache.put(key,value);     //把内存缓存中的元素，加入到活动缓存中
            Log.d(TAG,"cache Action：本次加载是在(内存缓存)中获取的资源>>>");

            //返回，代表使用了一次 Value
            value.useAction(); //使用了一次 加一
            return value;
        }

        //TODO 第三步，从磁盘缓存中去找，如果找到了，把磁盘缓存中的元素加入到活动缓存中
        value=diskLruCache.get(key);
        if (null!=value){
            //把磁盘缓存中的元素 --》 加入到活动缓存中
            activeCache.put(key,value);

            //把磁盘缓存中的元素　--> 加入到内存缓存中
//            memoryCache.put(key,value);
            Log.d(TAG,"cache Action：本次加载是在(磁盘缓存)中获取的资源>>>");
            value.useAction(); //使用了一次 加一
            return value;
        }

        //TODO 第四步， 真正地去加载外部资源 ，去网络上加载/去SD本地上加载
        value=new LoadDataManager().loadResource(path,this,glideContext);
        if (value!=null){
            return value;
        }

        return  null;

    }

    /**
     * 活动缓存间接调用Value发出
     * //回调外界，Value 资源不再使用
     * @param key
     * @param value
     */
    @Override
    public void valueNonUseAction(String key, Value value) {
        //把活动缓存操作的Value资源加到内存缓存
        if (key!=null&&value!=null){
            memoryCache.put(key,value);
        }

    }

    /**
     * 内存缓存发出的
     * 最少使用的元素会被移除
     * @param key
     * @param oldValue
     */
    @Override
    public void entryRemoveMemoryCache(String key, @NonNull Value oldValue) {
        //添加到复用池，空余功能点

    }

    //加载外部资源成功
    @Override
    public void responseSuccess(Value value) {
        if (value!=null){
            saveCache(key,value);
            imageView.setImageBitmap(value.getBitmap());
        }

    }

    //加载外部资源失败
    @Override
    public void responseException(Exception e) {

        Log.d(TAG,"responseException：加载外部资源失败 e: "+e.getMessage());
    }

    /**
     * 保存到缓存中
     * @param key
     * @param value
     */
    private void saveCache(String key,Value value){
        Log.d(TAG,"加载外部资源后，保存到缓存中 key:"+key+"  value:"+value);
        value.setKey(key);
        if (diskLruCache!=null){
            diskLruCache.put(key,value);  //保存到磁盘缓存中
        }
    }
}
