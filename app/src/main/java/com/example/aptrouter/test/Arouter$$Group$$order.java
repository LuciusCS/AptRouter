package com.example.aptrouter.test;

import com.example.api.core.ARouterLoadPath;
import com.example.api.core.ARouterLoadGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟Arouter路由的组文件
 */
public class Arouter$$Group$$order implements ARouterLoadGroup {
    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        Map<String, Class<? extends ARouterLoadPath>> groupMap=new HashMap<>();
        groupMap.put("order",Arouter$$Path$$order.class);
        return groupMap;
    }
}
