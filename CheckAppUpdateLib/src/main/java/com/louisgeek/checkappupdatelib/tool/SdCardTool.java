package com.louisgeek.checkappupdatelib.tool;

import android.os.Environment;

/**
 * Created by louisgeek on 2016/9/27.
 */
public class SdCardTool {
    /**
     * 判断sd卡可用
     * @return
     */
    public static boolean hasSDCardMounted() {
        String state = Environment.getExternalStorageState();
        if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
