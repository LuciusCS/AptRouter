package com.example.aptrouter.test;

import com.example.annotation.model.RouterBean;
import com.example.api.core.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟Arouter路由的组文件,对应的路径
 */
public class Arouter$$Path$$order implements ARouterLoadPath {
    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap=new HashMap<>();
//        pathMap.put("/order/Order_MainActivity",
//                RouterBean.create(RouterBean.Type.ACTIVITY,
//                Order_MainActivity.class,"/order/Order_MainActivity",
//                "order"));
//        pathMap.put("/order/Order_MainActivity",
//                RouterBean.create(RouterBean.Type.ACTIVITY,
//                        Order_MainActivity.class,"/order/Order_MainActivity",
//                        "order"));
        return pathMap;
    }
}
