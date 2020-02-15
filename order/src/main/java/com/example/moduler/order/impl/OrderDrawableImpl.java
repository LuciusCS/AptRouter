package com.example.moduler.order.impl;

import com.example.annotation.ARouter;
import com.example.common.order.OrderDrawable;
import com.example.moduler.order.R;

@ARouter(path = "/order/getDrawable")
public class OrderDrawableImpl implements OrderDrawable {
    @Override
    public int getDrawable() {
        return R.drawable.ic_brightness_4_black_24dp;
    }
}
