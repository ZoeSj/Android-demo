package com.baidumap.demo;

import android.annotation.SuppressLint;
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Administrator on 2016/9/5.
 */
public class MyApp extends Application {
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        SDKInitializer.initialize(getApplicationContext());
    }
}
