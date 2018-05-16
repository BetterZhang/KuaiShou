package com.anshi.kuaishou.http;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/05/16 下午 1:53
 * Desc   : description
 */
public class HttpResult<T> {

    private String m;
    private String e;
    private boolean success;
    private T d;

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getD() {
        return d;
    }

    public void setD(T d) {
        this.d = d;
    }
}
