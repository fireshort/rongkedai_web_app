package com.yuexiaohome.framework.http;

import android.support.v4.BuildConfig;
import android.text.TextUtils;
import com.yuexiaohome.framework.file.FileManager;
import com.yuexiaohome.framework.util.L;
import com.yuexiaohome.framework.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class JavaHttpUtility
{

    private static final int CONNECT_TIMEOUT = 20 * 1000;

    private static final int READ_TIMEOUT = 20 * 1000;

    private static final int DOWNLOAD_CONNECT_TIMEOUT = 15 * 1000;

    private static final int DOWNLOAD_READ_TIMEOUT = 60 * 1000;

    private static final int UPLOAD_CONNECT_TIMEOUT = 15 * 1000;

    private static final int UPLOAD_READ_TIMEOUT = 60 * 1000;

    public class NullHostNameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                String authType) {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                String authType) {
        }
    }};

    public JavaHttpUtility() {

        // allow Android to use an untrusted certificate for SSL/HTTPS
        // connection
        // so that when you debug app, you can use Fiddler http://fiddler2.com
        // to logs all HTTPS traffic
        try {
            if (BuildConfig.DEBUG) {
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Proxy getProxy() {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort)) {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost,
                    Integer.valueOf(proxyPort)));
        } else {
            return null;
        }
    }

    public String executeNormalTask(HttpMethod httpMethod, String url, Map<String, String> param)
            throws HttpException {

        switch (httpMethod) {
            case Post:
                return doPost(url, param);
            case Get:
                return doGet(url, param);
        }
        return "";
    }

    public String doGet(String urlStr, Map<String, String> param) throws HttpException {
        String errorStr = "";
        try {

            StringBuilder urlBuilder = new StringBuilder(urlStr);
            urlBuilder.append("?").append(Utils.encodeUrl(param));
            URL url = new URL(urlBuilder.toString());
            Proxy proxy = getProxy();
            HttpURLConnection urlConnection;
            if (proxy != null) {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();

            return handleResponse(urlConnection);
        } catch (IOException e) {
//            e.printStackTrace();
            throw new HttpException(errorStr, e);
        }
    }


    public String doPost(String urlAddress, Map<String, String> param) throws HttpException {
        String errorStr = "";

        try {
            URL url = new URL(urlAddress);
            Proxy proxy = getProxy();
            HttpURLConnection uRLConnection;

            if (proxy != null) {
                uRLConnection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                uRLConnection = (HttpURLConnection) url.openConnection();
            }

            uRLConnection.setDoInput(true);
            uRLConnection.setDoOutput(true);
            uRLConnection.setRequestMethod("POST");
            uRLConnection.setUseCaches(false);
            uRLConnection.setConnectTimeout(CONNECT_TIMEOUT);
            uRLConnection.setReadTimeout(READ_TIMEOUT);
            uRLConnection.setInstanceFollowRedirects(false);
            uRLConnection.setRequestProperty("Connection", "Keep-Alive");
            uRLConnection.setRequestProperty("Charset", "UTF-8");
            uRLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            uRLConnection.connect();

            DataOutputStream out = new DataOutputStream(uRLConnection.getOutputStream());
            out.write(Utils.encodeUrl(param).getBytes());
            out.flush();
            out.close();
            return handleResponse(uRLConnection);
        } catch (IOException e) {
//            e.printStackTrace();
            throw new HttpException(errorStr, e);
        }
    }

    private String handleResponse(HttpURLConnection httpURLConnection) throws HttpException {
        String errorStr = "Error: " + httpURLConnection.getURL();
        int status = 0;
        try {
            status = httpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            httpURLConnection.disconnect();
            throw new HttpException(status, errorStr, e);
        }

        if (status != HttpURLConnection.HTTP_OK) {
            return handleError(httpURLConnection);
        }

        return readResult(httpURLConnection);
    }

    private String handleError(HttpURLConnection urlConnection) throws HttpException {

        String result = readError(urlConnection);
        String err = null;
        int errCode = 0;
        try {
            JSONObject json = new JSONObject(result).getJSONObject("status");
            err = json.optString("message", "");
            if (TextUtils.isEmpty(err)) {
                err = "No error return";
            }
            errCode = json.getInt("code");
            throw new HttpException(errCode, err);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String readResult(HttpURLConnection urlConnection) throws HttpException {
        InputStream is = null;
        BufferedReader buffer = null;
        String errorStr = "";
        try {
            is = urlConnection.getInputStream();

            String content_encode = urlConnection.getContentEncoding();

            if (null != content_encode && !"".equals(content_encode)
                    && content_encode.equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            L.v("============  "+urlConnection.getURL()+"  ============\n"+
                    strBuilder.toString());
            return strBuilder.toString();
        } catch (IOException e) {
//            e.printStackTrace();
            throw new HttpException(errorStr, e);
        } finally {
            Utils.closeSilently(is);
            Utils.closeSilently(buffer);
            urlConnection.disconnect();
        }

    }

    private String readError(HttpURLConnection urlConnection) throws HttpException {
        InputStream is = null;
        BufferedReader buffer = null;
        String errorStr = "";

        try {
            is = urlConnection.getErrorStream();

            if (is == null) {
                throw new HttpException(errorStr);
            }

            String content_encode = urlConnection.getContentEncoding();

            if (null != content_encode && !"".equals(content_encode)
                    && content_encode.equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            L.v("============  " + urlConnection.getURL() + "  ============\n" +
                    strBuilder.toString());
            return strBuilder.toString();
        } catch (IOException e) {
//            e.printStackTrace();
            throw new HttpException(errorStr, e);
        } finally {
            Utils.closeSilently(is);
            Utils.closeSilently(buffer);
            urlConnection.disconnect();
        }

    }

    public boolean doGetSaveFile(String urlStr, String path,
            FileDownloadListener downloadListener) {

        File file = FileManager.createNewFileInSDCard(path);
        if (file == null) {
            return false;
        }

        BufferedOutputStream out = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(urlStr);
            Proxy proxy = getProxy();
            if (proxy != null) {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(DOWNLOAD_CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(DOWNLOAD_READ_TIMEOUT);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();

            int status = urlConnection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK) {
                return false;
            }

            int bytetotal = (int) urlConnection.getContentLength();
            int bytesum = 0;
            int byteread = 0;
            out = new BufferedOutputStream(new FileOutputStream(file));
            in = new BufferedInputStream(urlConnection.getInputStream());

            final Thread thread = Thread.currentThread();
            byte[] buffer = new byte[1444];
            while ((byteread = in.read(buffer)) != -1) {
                if (thread.isInterrupted()) {
                    file.delete();
                    throw new InterruptedIOException();
                }

                bytesum += byteread;
                out.write(buffer, 0, byteread);
                if (downloadListener != null && bytetotal > 0) {
                    downloadListener.pushProgress(bytesum, bytetotal);
                }
            }
            if (downloadListener != null) {
                downloadListener.onCompleted();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
        } finally {
            Utils.closeSilently(in);
            Utils.closeSilently(out);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return false;
    }

    private static String getBoundary() {
        StringBuffer _sb = new StringBuffer();
        for (int t = 1; t < 12; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3 == 0) {
                _sb.append((char) time % 9);
            } else if (time % 3 == 1) {
                _sb.append((char) (65 + time % 26));
            } else {
                _sb.append((char) (97 + time % 26));
            }
        }
        return _sb.toString();
    }

    private String getBoundaryMessage(String boundary, Map<String, String> params,
            String fileField, String fileName, String fileType) {
        StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n");

        Iterator<String> keys = params.keySet().iterator();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = (String) params.get(key);
            res.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n")
                    .append("\r\n").append(value).append("\r\n").append("--").append(boundary)
                    .append("\r\n");
        }
        res.append("Content-Disposition: form-data; name=\"").append(fileField)
                .append("\"; filename=\"").append(fileName).append("\"\r\n")
                .append("Content-Type: ").append(fileType).append("\r\n\r\n");

        return res.toString();
    }

    public String doUploadFile(String urlStr, Map<String, String> param, String path,
            String imageParamName,
            final FileUploadListener listener) throws HttpException {
        String BOUNDARYSTR = getBoundary();

        File targetFile = new File(path);

        byte[] barry = null;
        int contentLength = 0;
        String sendStr = "";
        try {
            barry = ("--" + BOUNDARYSTR + "--\r\n").getBytes("UTF-8");

            sendStr = getBoundaryMessage(BOUNDARYSTR, param, imageParamName,
                    new File(path).getName(), "image/png");
            contentLength = sendStr.getBytes("UTF-8").length + (int) targetFile.length() + 2
                    * barry.length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        FileInputStream fis = null;
        String errorStr = "";
        try {
            URL url = new URL(urlStr);

            Proxy proxy = getProxy();
            if (proxy != null) {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }

            urlConnection.setConnectTimeout(UPLOAD_CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(UPLOAD_READ_TIMEOUT);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-type",
                    "multipart/form-data;boundary=" + BOUNDARYSTR);
            urlConnection.setRequestProperty("Content-Length", Integer.toString(contentLength));
            urlConnection.setFixedLengthStreamingMode(contentLength);
            urlConnection.connect();

            out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(sendStr.getBytes("UTF-8"));

            fis = new FileInputStream(targetFile);

            int totalSent = sendStr.getBytes("UTF-8").length;
            int bytesRead;
            int bytesAvailable;
            int bufferSize;
            byte[] buffer;
            int maxBufferSize = 512;

            bytesAvailable = fis.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fis.read(buffer, 0, bufferSize);
            long transferred = 0;
            final Thread thread = Thread.currentThread();
            while (bytesRead > 0) {
                if (thread.isInterrupted()) {
                    throw new InterruptedIOException();
                }
                out.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
                totalSent += bytesRead;
                if (transferred % 50 == 0)
                    out.flush();
                if (listener != null) {
                    listener.transferred(totalSent / (float) contentLength);
                }
            }

            out.write(barry);
            totalSent += barry.length;
            out.write(barry);
            totalSent += barry.length;
            out.flush();
            out.close();
            if (listener != null) {
                listener.transferred(totalSent / (float) contentLength);
//                listener.waitServerResponse();
            }
            return handleResponse(urlConnection);
        } catch (IOException e) {
//            e.printStackTrace();
            throw new HttpException(errorStr, e);
        } finally {
            Utils.closeSilently(fis);
            Utils.closeSilently(out);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

    }

}
