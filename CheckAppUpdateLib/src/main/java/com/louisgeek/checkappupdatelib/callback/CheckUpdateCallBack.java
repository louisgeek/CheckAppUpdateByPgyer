package com.louisgeek.checkappupdatelib.callback;

import com.louisgeek.checkappupdatelib.bean.FirImBean;

/**
 * Created by louisgeek on 2016/9/18.
 */
public abstract class CheckUpdateCallBack {
    public  abstract void OnSuccess(String result, int statusCode, FirImBean firImBean, boolean silentDownload);

    public  abstract void OnSuccessNotifyUI(String result,int statusCode,FirImBean firImBean, boolean silentDownload);

    public  abstract void OnError(String errorMsg,int statusCode);

    public  abstract void OnErrorNotifyUI(String errorMsg,int statusCode);
}
