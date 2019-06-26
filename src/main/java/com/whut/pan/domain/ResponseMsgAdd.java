package com.whut.pan.domain;

/**
 * Created by zc on 2018/11/2.
 */
public class ResponseMsgAdd {
    private boolean success = false;

    private String msg = "";
    private Object obj;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
