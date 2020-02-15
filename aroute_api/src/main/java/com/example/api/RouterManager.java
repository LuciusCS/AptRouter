package com.example.api;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.example.annotation.model.RouterBean;
import com.example.api.core.ARouterLoadGroup;
import com.example.api.core.ARouterLoadPath;

public class RouterManager {

    //路由组名
    private String group;

    //路由Path路径
    private String path;

    private static RouterManager instance;

    //Lru缓存，key:组名，value:路由组Group加载借口
    private LruCache<String, ARouterLoadGroup> groupLruCache;
    //Lru缓存，key:路径名，value:路径path路径加载借口
    private LruCache<String, ARouterLoadPath> pathLruCache;
    //APT生成类文件后缀名(包名拼接)
    private static final String GROUP_FILE_PREFIX_MAME = ".ARouter$$Group$$";

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    private RouterManager() {
        groupLruCache = new LruCache<>(200);
        pathLruCache = new LruCache<>(200);
    }

    /**
     * @param path 传递路由的地址
     * @return
     */
    public BundleManager build(String path) {

        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("未按照规范配置：如 /order/OrderSecondActivity");
        }

        group = subFromPath2Group(path);

        //检查过了path和group
        this.path = path;
        return new BundleManager();

    }

    private String subFromPath2Group(String path) {
        //开发者写法：path= "/MainActivity"
        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("未按照规范配置：如 /order/OrderSecondActivity");
        }

        //从第一个 / 到第二个 / 中间截取
        String finalGroup = path.substring(1, path.indexOf("/", 1));

        if (TextUtils.isEmpty(finalGroup)) {
            //定义规范
            throw new IllegalArgumentException("未按照规范配置：如 /order/OrderSecondActivity");
        }

        return finalGroup;
    }


    /**
     * 开始跳转
     *
     * @param context       上下文
     * @param bundleManager 参数管理
     * @param code          可能是resultCode, 也可以是requestCode, 取决于 isResult
     * @return 普通跳转可以忽略，用于模块化Call接口
     */
    public Object navigation(Context context, BundleManager bundleManager, int code) {
        //ARouter$$Group$$order
        String groupClassName = context.getPackageName() + ".apt" + GROUP_FILE_PREFIX_MAME + group;
        Log.e("RouterManager", groupClassName);

        //读取路由组Group类文件（缓存，懒加载）
        try {
            ARouterLoadGroup groupLoad = groupLruCache.get(group);
            if (groupLoad == null) {
                //加载路由组APT路由组Group文件ARouter$$Group$$personal
                Class<?> clazz = Class.forName(groupClassName);
                //初始化类文件
                groupLoad = (ARouterLoadGroup) clazz.newInstance();
                groupLruCache.put(group, groupLoad);
            }

            //判断是否为空
            if (groupLoad.loadGroup().isEmpty()) {
                throw new RuntimeException("路由表Group加载失败！");
            }

            //读取路由Path路径类文件（懒加载）
            ARouterLoadPath pathLoad = pathLruCache.get(path);
            if (pathLoad == null) {

                //通过组Group记载借口，获取Path加载接口
                Class<? extends ARouterLoadPath> clazz = groupLoad.loadGroup().get(group);

                //初始化类文件，ARouter$$Path$$personal
                if (clazz != null) {
                   pathLoad = (ARouterLoadPath) clazz.newInstance();
                }
                if (pathLoad != null) {
                    pathLruCache.put(path, pathLoad);
                }
            }

            if (pathLoad != null) {  //增强健壮性
                if (pathLoad.loadPath().isEmpty()) {
                    throw new RuntimeException("路由表Path加载失败！");
                }
                RouterBean routerBean = pathLoad.loadPath().get(path);

                if (routerBean != null) {
                    //类型判断，方便跳转
                    switch (routerBean.getType()) {
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getClazz());
                            intent.putExtras(bundleManager.getBundle());

                            //注意： startActivityForResult -- > setResult
                            if (bundleManager.isResult()) {
                                ((Activity) context).setResult(code, intent);
                                ((Activity) context).finish();
                            }

                            if (code > 0) {
                                //跳转时需要进行回调
                                ((Activity) context).startActivityForResult(intent, code, bundleManager.getBundle());
                            } else {
                                ((Activity) context).startActivity(intent, bundleManager.getBundle());
                            }

                            break;

                        case CALL:
                            //返回接口实现类
                            return routerBean.getClazz().newInstance();
                    }
                }
            }


        } catch (Exception e) {
            Log.e("RouterManager", e.getMessage());
            e.printStackTrace();
        }


        return null;
    }
}
