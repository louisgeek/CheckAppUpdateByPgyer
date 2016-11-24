package com.louisgeek.checkappupdatelib.tool;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by louisgeek on 2016/7/12.
 */
public class ThreadUtil {
    public static final void runOnUiThread(Runnable runnable) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        //通过查看Thread类的当前线程
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    public static final void runOnUiThreadTwo(Runnable runnable) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        //使用Looper类判断
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    public static boolean isMainThread() {
        //使用Looper类判断
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return true;
        } else {
            return false;
        }
    }
}
