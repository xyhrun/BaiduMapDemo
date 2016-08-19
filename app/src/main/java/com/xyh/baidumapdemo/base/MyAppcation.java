package com.xyh.baidumapdemo.base;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by 向阳湖 on 2016/7/18.
 */
public class MyAppcation extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static void  MyToast(String string) {
        Toast.makeText(context, ""+ string, Toast.LENGTH_SHORT).show();
    }

    public static void  MyLongToast(String string) {
        Toast.makeText(context, ""+ string, Toast.LENGTH_LONG).show();
    }
}
