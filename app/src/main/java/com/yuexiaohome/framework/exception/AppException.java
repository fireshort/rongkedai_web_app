package com.yuexiaohome.framework.exception;

/**
 * Created by Administrator on 2014/8/4.
 */
public final class AppException extends Exception {

    private int mCode = -1;

    public AppException() {
        super("Unknown error");
    }


    public AppException(int code) {
        super();
        mCode = code;
    }

    public AppException(String msg) {
        super(msg);
    }


    public AppException(String msg,Throwable throwable) {
        super(msg, throwable);
    }


    public AppException(int code,String msg) {
        super(msg);
        mCode = code;
    }

    public AppException(int code,String msg,Throwable throwable) {
        super(msg, throwable);
        mCode = code;
    }

    public int getErrorCode() {
        return mCode;
    }

}
