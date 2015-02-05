package com.yuexiaohome.framework.http;

public interface FileDownloadListener {

    public void pushProgress(int progress,int max);

    public void onCompleted();


}
