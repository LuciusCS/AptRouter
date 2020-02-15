package com.example.glidemodule.cache;

import com.example.glidemodule.resources.Value;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 活动缓存 -- 真正正在使用的资源
 */
public class ActiveCache {


    //容器
    private Map<String, WeakReference<Value>>mapList=new HashMap<>();

    private ReferenceQueue<Value>queue;    //目的：为了监听弱引用是否被回收
    private boolean isCloseThread;

    private Thread thread;

    //用于判断是主动还是被动移除
    private boolean isAutoRemove=true;

    /**
     * TODO 添加活动缓存
     * @param key
     * @param value
     */
    public void put(String key,Value value){
        //存储 --》 容器
        mapList.put(key,new CustomWeakReference(value,getQueue(),key));
    }


    /**
     * TODO 给外界获取value
     * @param key
     * @return
     */
    public Value get(String key){
        WeakReference<Value>valueWeakReference=mapList.get(key);
        if (null!=valueWeakReference){
            return valueWeakReference.get();   //返回value
        }
        return null;
    }

    /**
     * TODO 手动删除
     * @param key
     * @return
     */
    public Value remove(String key){
        isAutoRemove=false;
        WeakReference<Value>valueWeakReference= mapList.remove(key);
        isAutoRemove=true;  //还原，目的让GC 自动移除继续工作，
        if (null!=valueWeakReference){
            return valueWeakReference.get();

        }
        return null;
    }

    //监听弱引用，成为弱引用的子类，为了监听弱引用是否被回收
    //使用弱引用子类，是为了把构造方法
    public class CustomWeakReference extends WeakReference< Value>{

        private String key;

        public CustomWeakReference(Value referent, ReferenceQueue<? super Value> q,String key) {
            super(referent, q);
            this.key=key;
        }
    }

    /**
     * 释放 关闭线程
     */

    public void closeThread(){
        isCloseThread=true;
        if (null!=thread){
            thread.interrupt();  //终端线程
            try {
                thread.join(TimeUnit.SECONDS.toMillis(5));  //线程稳定，安全停止下来
                if (thread.isAlive()){  //证明线程还是没有结束
                    throw  new IllegalArgumentException("活动缓存中 关闭线程 线程没有停止下来。。。");

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 为了监听弱引用回收,被动移除的
     * @return
     */

    private ReferenceQueue<Value>getQueue(){

        if (queue==null){
            queue=new ReferenceQueue<>();

           thread= new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (!isCloseThread){

                        try {

                            //阻塞式方法
                            Reference<? extends Value> remove=queue.remove();  //如果被回收，则会执行该方法

                            CustomWeakReference weakReference=(CustomWeakReference)remove;

                            //移除容器  //isAutoRemove 区分手动移除还是被动移除
                            if (mapList!=null&&!mapList.isEmpty()&&isAutoRemove){
                                mapList.remove(weakReference.key);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
           thread.start();
        }
        return queue;

    }
}
