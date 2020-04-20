package com.jd.platform.hotkey.dashboard.common.eunm;


public enum ResultEnum {

    SUCCESS(200, "操作成功！"),

    NO_LOGIN(1000, "未登录"),

    NO_CHANGE(1001, "操作无影响"),

    PWD_ERROR(1002, "账户/密码错误"),

    NO_PERMISSION(1003, "没有操作权限"),

    NO_RESOURCE(1004, "没有资源"),

    PARAM_ERROR(1005, "参数错误"),

    BIZ_ERROR(1006, "业务异常");

    private int code;

    private String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
