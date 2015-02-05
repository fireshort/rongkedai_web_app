package com.yuexiaohome.framework.http;

/**
 * Created by Administrator on 2014/8/4.
 */
public final class HttpException extends Exception {

    private int mCode = Integer.MIN_VALUE;


    public HttpException() {
        super("Unknown error");
    }


    public HttpException(int code) {
        super();
        mCode = code;
    }

    public HttpException(String msg) {
        super(msg);
    }


    public HttpException(String msg, Throwable throwable) {
        super(msg, throwable);
    }


    public HttpException(int code, String msg) {
        super(msg);
        mCode = code;
    }

    public HttpException(int code, String msg, Throwable throwable) {
        super(msg, throwable);
        mCode = code;
    }

    public int getErrorCode() {
        return mCode;
    }

}
