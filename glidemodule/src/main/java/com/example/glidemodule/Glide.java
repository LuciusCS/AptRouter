package com.example.glidemodule;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

public class Glide {

    RequestManagerRetriver retriver;

    public Glide(RequestManagerRetriver retriver) {
        this.retriver = retriver;
    }

    public static RequestManager with(FragmentActivity fragmentActivity) {

        return getRetriver(fragmentActivity).get(fragmentActivity);
    }

    public static RequestManager with(Activity activity) {
        return getRetriver(activity).get(activity);
    }

    public static RequestManager with(Context context) {
        return getRetriver(context).get(context);
    }

    /**
     *
     * RequestManager由 RequestManagerRetriver创建
     * @return
     */

   public static RequestManagerRetriver getRetriver(Context context){
       return Glide.get(context).getRetriver();

    }

    /**
     * Glide 是new 出来的，做了转变
     * @param context
     * @return
     */
    public static  Glide get(Context context){
       return new GlideBuilder().build();
    }

    public RequestManagerRetriver getRetriver() {
        return retriver;
    }
}
