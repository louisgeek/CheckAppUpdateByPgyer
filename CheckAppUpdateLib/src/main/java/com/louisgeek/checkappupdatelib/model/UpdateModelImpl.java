package com.louisgeek.checkappupdatelib.model;

import com.google.gson.reflect.TypeToken;
import com.louisgeek.checkappupdatelib.bean.PgyerBaseListBean;
import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.contract.UpdateContract;
import com.louisgeek.checkappupdatelib.tool.HttpTool;

/**
 * Created by louisgeek on 2016/11/23
 */

public class UpdateModelImpl implements UpdateContract.Model {

    @Override
    public void loadUpdateInfo(final UpdateContract.OnLoadDataListener onLoadDataListener) {

        HttpTool.postUrlBackString(UpdateContract.postGroupInfoUrl,
                UpdateContract.postGroupInfoParamStr, new HttpTool.OnUrlBackStringCallBack() {
                    @Override
                    public void onSuccess(String backStr) {
                        //
                        TypeToken<PgyerBaseListBean<PgyerGroupBean>> typeToken = new TypeToken<PgyerBaseListBean<PgyerGroupBean>>() {
                        };

                        PgyerBaseListBean<PgyerGroupBean> pgyerBaseListBean = PgyerBaseListBean.fromJson(backStr, typeToken);
                        //Log.d("sdf", "onSuccess: pgyerBaseListBean:"+pgyerBaseListBean);
                        onLoadDataListener.onSuccess(pgyerBaseListBean);
                    }

                    @Override
                    public void onError(String errorMsg) {
                        onLoadDataListener.onError(errorMsg);
                    }
                });
    }

}