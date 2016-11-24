package com.louisgeek.checkappupdatelib.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by louisgeek on 2016/11/23.
 */

public class PgyerBaseBean<T> {

    /**
     * code : 0
     * message :
     * data : {"list":[{"appKey":"58988c2ea816981accd6e141b816d908","appType":"2","appFileSize":"8806184","appName":"云种","appVersion":"2.0.5","appVersionNo":"7","appBuildVersion":"39","appIdentifier":"com.sunstar.cloudseeds","appIcon":"125fdf11e95afcf6a3a107c3c57f4599","appCreated":"2016-11-23 10:20:30"},{"appKey":"b3feb777bc1e19bf88a47f2d306c31d0","appType":"2","appFileSize":"8806199","appName":"云种","appVersion":"2.0.4","appVersionNo":"6","appBuildVersion":"38","appIdentifier":"com.sunstar.cloudseeds","appIcon":"291bbd06edc09bd180e5088f423f74f0","appCreated":"2016-11-23 10:14:11"},{"appKey":"4a8e9a4c035122f7218af526bd563d4d","appType":"2","appFileSize":"8018330","appName":"云种","appVersion":"2.0.4","appVersionNo":"6","appBuildVersion":"37","appIdentifier":"com.sunstar.cloudseeds","appIcon":"71708daa56c17c234ca908011863f206","appCreated":"2016-11-21 12:46:16"},{"appKey":"0278e522227d00a8766508727c0dc17e","appType":"2","appFileSize":"8801958","appName":"云种","appVersion":"2.0.4","appVersionNo":"6","appBuildVersion":"36","appIdentifier":"com.sunstar.cloudseeds","appIcon":"aca31b4a2dcec2329acfd2f175e9016d","appCreated":"2016-11-17 14:58:36"},{"appKey":"ec7b3ecbcbab2072ac8ed890ae4f9f1b","appType":"2","appFileSize":"8801965","appName":"云种","appVersion":"2.0.3","appVersionNo":"5","appBuildVersion":"35","appIdentifier":"com.sunstar.cloudseeds","appIcon":"d1cf6c1bc59e3f1d44f2b05dde36f69f","appCreated":"2016-11-17 14:48:34"},{"appKey":"bf7c462b4552d54633832da1caf434f6","appType":"2","appFileSize":"8053247","appName":"云种","appVersion":"2.0.2","appVersionNo":"4","appBuildVersion":"34","appIdentifier":"com.sunstar.cloudseeds","appIcon":"653b53bcd6ac5a758d8fbcc339fbd01c","appCreated":"2016-11-15 16:54:51"},{"appKey":"5b506ed31afa9f02f95fbd9cffef7f75","appType":"2","appFileSize":"8801912","appName":"云种","appVersion":"2.0.2","appVersionNo":"4","appBuildVersion":"33","appIdentifier":"com.sunstar.cloudseeds","appIcon":"a2c16b62526850a65bc146d53802c4d9","appCreated":"2016-11-09 16:34:04"},{"appKey":"5b4888b2eaea86e6ab4d6c45f22bd7b1","appType":"2","appFileSize":"8801910","appName":"云种","appVersion":"2.0.1","appVersionNo":"3","appBuildVersion":"32","appIdentifier":"com.sunstar.cloudseeds","appIcon":"fc9aeb87f248f637897bfdb22e497176","appCreated":"2016-11-09 16:31:00"},{"appKey":"b47c63589577c29bb9a0be918bdee2b7","appType":"2","appFileSize":"8801780","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"31","appIdentifier":"com.sunstar.cloudseeds","appIcon":"8ce1ba0b0885220f8b10cc2d1ec11810","appCreated":"2016-11-08 08:34:58"},{"appKey":"05d6ab7f1b63585bf399cd1a83e67118","appType":"2","appFileSize":"8018007","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"30","appIdentifier":"com.sunstar.cloudseeds","appIcon":"0c541bc92cb2f87ecded4eeff3287776","appCreated":"2016-11-03 14:23:07"},{"appKey":"dd1c0c4165d2b444507c92c97af49b2b","appType":"2","appFileSize":"8801073","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"29","appIdentifier":"com.sunstar.cloudseeds","appIcon":"1e5701668b38d9c37e16ddf832ed0246","appCreated":"2016-10-20 11:27:59"},{"appKey":"8f833d3e7c4ac544c96fb632f6915ea6","appType":"2","appFileSize":"8801158","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"28","appIdentifier":"com.sunstar.cloudseeds","appIcon":"8e97dfc9f85c1fe6920f943c2c99442d","appCreated":"2016-10-20 10:12:22"},{"appKey":"a190c43c6dc22666075eed110afcd21f","appType":"2","appFileSize":"8801030","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"27","appIdentifier":"com.sunstar.cloudseeds","appIcon":"7735f26460e12b892686bcecc9ddb07f","appCreated":"2016-10-18 10:09:14"},{"appKey":"17e32ad88423e5122f1c8b8bdcf3d43b","appType":"2","appFileSize":"8801054","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"26","appIdentifier":"com.sunstar.cloudseeds","appIcon":"26f6e4cbbe591174b4bd78241c2feb28","appCreated":"2016-10-18 09:52:35"},{"appKey":"55aa7f0bea344e77e1f4b32e988b87ae","appType":"2","appFileSize":"8801150","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"25","appIdentifier":"com.sunstar.cloudseeds","appIcon":"64f38dd6a32b455e1e74457624d03267","appCreated":"2016-10-18 09:45:38"},{"appKey":"8c4f4c651440a39c483164dad7e41e19","appType":"2","appFileSize":"8800477","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"24","appIdentifier":"com.sunstar.cloudseeds","appIcon":"386c8e46b006ed956c9b117d9d56c60e","appCreated":"2016-10-11 15:43:28"},{"appKey":"15e0fef3318b31714bdc6d1d4f8f7266","appType":"2","appFileSize":"8797490","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"23","appIdentifier":"com.sunstar.cloudseeds","appIcon":"be226e5dce090fa4a283906f2935b976","appCreated":"2016-10-10 13:22:41"},{"appKey":"34ad0324ac39822f13959ad7aae49b86","appType":"2","appFileSize":"8797194","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"22","appIdentifier":"com.sunstar.cloudseeds","appIcon":"f8bbf54b9efe2d1fdc06704edd902986","appCreated":"2016-09-30 14:41:00"},{"appKey":"aff6e56ab342eab8781488fe50567594","appType":"2","appFileSize":"8759749","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"21","appIdentifier":"com.sunstar.cloudseeds","appIcon":"6f55148108d33f9fd7ff07fc7e6f6f67","appCreated":"2016-09-29 14:11:30"},{"appKey":"0ef7d35e3afb28ce56449a0394734b8b","appType":"2","appFileSize":"8759582","appName":"云种","appVersion":"2.0.0","appVersionNo":"2","appBuildVersion":"20","appIdentifier":"com.sunstar.cloudseeds","appIcon":"481037d845a23a566d28c431f1c59b56","appCreated":"2016-09-29 12:55:36"}]}
     */

    private String code;
    private String message;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 推荐
     * <p>
     * 用法
     * TypeToken<BaseBean<OthersBean>> typeToken=new TypeToken<BaseBean<OthersBean>>(){};
     * BaseBean<OthersBean> baseBean=BaseBean.fromJsonOne(body,typeToken);
     *
     * @param json
     * @param token
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, TypeToken<T> token) {
        Gson gson = new Gson();
        Type objectType = token.getType();
        return gson.fromJson(json, objectType);
    }
}
