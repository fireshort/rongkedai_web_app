package com.yuexiaohome.framework.util;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import com.yuexiaohome.framework.lib.AsyncTaskEx;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils
{

    private Utils() {
    }

    public static boolean isEmailValid(String email) {
        Pattern emailPattern =
                Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        return matcher.find();
    }

    public static boolean hasBluetoothLE(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) && pm.hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static boolean isBluetoothEnabled(Context context) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    public static String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }


    public static File compressImage(File imageFile, long maxSize) {

        return null;

    }

    /**
     * @param imageFile original file
     * @param level     1-10, higher level,higher compress ratio
     * @return compressed file
     */
    public static File compressImage(File imageFile, int level) {
        if (!imageFile.exists()) {
            return null;
        }
        level = Math.max(level, 0);
        level = Math.min(level, 10);

        return null;
    }


    public static void phoneCall(Context context, String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return;
        }
        Uri uri = Uri.parse("tel:" + phoneNum);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        context.startActivity(intent);
    }

    public static String getAppVersionName(Context context) {
        String verName = null;
        PackageManager pm = context.getPackageManager();
        String pkgName = context.getPackageName();
        try {
            verName = pm.getPackageInfo(pkgName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            L.e("Fail to get package info for " + pkgName, e);
        }
        return verName;
    }


    public static int getAppVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        String pkgName = context.getPackageName();
        try {
            return pm.getPackageInfo(pkgName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            L.e("Fail to get package info for " + pkgName, e);
        }
        return 0;
    }

    public static String encodeUrl(Map<String, String> param) {
        if (param == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        Set<String> keys = param.keySet();
        boolean first = true;

        for (String key : keys) {
            String value = param.get(key);
            // if (key.equals("description") || key.equals("url")) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            try {
                sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
                        .append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                L.e("unsupported encoding", e);
                return "";
            }
            // }
        }
        return sb.toString();
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                try {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"),
                            URLDecoder.decode(v[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }
            }
        }
        return params;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {

            }
        }
    }


    public static int length(String paramString) {
        int i = 0;
        for (int j = 0; j < paramString.length(); j++) {
            if (paramString.substring(j, j + 1).matches("[Α-￥]")) {
                i += 2;
            } else {
                i++;
            }
        }

        if (i % 2 > 0) {
            i = 1 + i / 2;
        } else {
            i = i / 2;
        }

        return i;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public static int getNetType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return networkInfo.getType();
        }
        return -1;
    }

    public static boolean isIntentSafe(Activity activity, Uri uri) {
        Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public static boolean isIntentSafe(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }

    public static boolean isGooglePlaySafe(Activity activity) {
        Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms");
        Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
        mapCall.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        mapCall.setPackage("com.android.vending");
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public static boolean isTaskStopped(AsyncTaskEx<?, ?, ?> task) {
        return task == null || task.getStatus() == AsyncTaskEx.Status.FINISHED;
    }

    public static void tryCancelTask(AsyncTaskEx<?, ?, ?> task) {
        if (task != null && task.getStatus() != AsyncTaskEx.Status.FINISHED) {
            task.cancel(true);
        }
    }

    public static Rect locateView(View v) {
        int[] location = new int[2];
        if (v == null) {
            return null;
        }
        try {
            v.getLocationOnScreen(location);
        } catch (NullPointerException npe) {
            // Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect locationRect = new Rect();
        locationRect.left = location[0];
        locationRect.top = location[1];
        locationRect.right = locationRect.left + v.getWidth();
        locationRect.bottom = locationRect.top + v.getHeight();
        return locationRect;
    }

    public static int countWord(String content, String word, int preCount) {
        int count = preCount;
        int index = content.indexOf(word);
        if (index == -1) {
            return count;
        } else {
            count++;
            return countWord(content.substring(index + word.length()), word, count);
        }
    }


}
