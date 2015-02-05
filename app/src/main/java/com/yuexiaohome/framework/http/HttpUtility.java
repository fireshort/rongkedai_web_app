package com.yuexiaohome.framework.http;



import com.yuexiaohome.framework.util.L;

import java.util.Iterator;
import java.util.Map;

public class HttpUtility {

    private HttpUtility() {
    }

    public static String executeNormalTask(HttpMethod httpMethod, String url,
            Map<String, String> param) throws HttpException {
        dumpParamsMap(url, param);
        return new JavaHttpUtility().executeNormalTask(httpMethod, url, param);
    }

    public static boolean executeDownloadTask(String url, String path,
            FileDownloadListener downloadListener) {
        L.d("============  Download  ============\n"+url+"\n-->"+path);
        return !Thread.currentThread().isInterrupted()
                && new JavaHttpUtility().doGetSaveFile(url, path, downloadListener);
    }

    public static String executeUploadTask(String url, Map<String, String> param, String path,
            String imageParamName,
            FileUploadListener listener) throws HttpException {
        dumpParamsMap(url, param);
        return Thread.currentThread().isInterrupted() ? "" : new JavaHttpUtility().doUploadFile(
                url, param, path, imageParamName, listener);
    }

    private static void dumpParamsMap(String url, Map<String, String> params) {
        L.d("============  " + url + "  ============");
        Iterator<String> k = params.keySet().iterator();
        while (k.hasNext()) {
            String str = k.next();
            L.d(str + " = " + params.get(str));
        }
        L.d("============        Dump finished      ============");
    }
}
