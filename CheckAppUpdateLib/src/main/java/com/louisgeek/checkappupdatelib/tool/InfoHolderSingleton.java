package com.louisgeek.checkappupdatelib.tool;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例模式持参
 * Created by louisgeek on 2016/7/7.
 */
public class InfoHolderSingleton {
    //
    private static final String TAG = "InfoHolderSingleton";

    //懒汉式(线程安全，同步方法)，性能略低
    //单例模式实例
    private static InfoHolderSingleton instance = null;

    final Map<String, Object> mMap;

    private InfoHolderSingleton() {
        mMap = new HashMap<>();
    }

    //synchronized 用于线程安全，防止多线程同时创建实例
    public synchronized static InfoHolderSingleton getInstance() {
        if (instance == null) {
            instance = new InfoHolderSingleton();
        }
        return instance;
    }

    public void putMapObj(String key, Object value) {
        mMap.put(key, value);
    }

    public Object getMapObj(String key) {
        return mMap.get(key);
    }

    public void showAllMapObj() {
        for (String key : mMap.keySet()) {
            Log.d(TAG, "showAllMapObj: key:" + key);
            Log.d(TAG, "showAllMapObj: value:" + mMap.get(key));
        }
    }
}