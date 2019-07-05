package com.whut.pan.domain;

/**
 * 给前台返回的操作结果
 *
 * @author Sandeepin
 */
public class ResponseMsg {

    private boolean success = false;

    private String msg = "";

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
}
