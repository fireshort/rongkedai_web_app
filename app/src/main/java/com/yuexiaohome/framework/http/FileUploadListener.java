package com.yuexiaohome.framework.http;

public interface FileUploadListener {

    public void transferred(float progress);

    public void waitServerResponse();

    public void completed();
}
