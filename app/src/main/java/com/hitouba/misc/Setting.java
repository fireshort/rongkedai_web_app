package com.hitouba.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Setting {


    public static final int ITEMS_PER_PAGE = 10;

    public static final String KEY_NAME = "name";
    public static final String KEY_UID = "uid";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PWD = "pwd";
    public static final String KEY_LANG = "lang";
    public static final String KEY_REMEMBER_ME = "remember_me";
    public static final String KEY_LOGIN = "login";

    private static SharedPreferences mSP;

    public static void init(Context context) {
        mSP = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getLang() {
        return mSP.getString(KEY_LANG, "en_US");
    }

    public static void setLang(String lang) {
        mSP.edit().putString(KEY_LANG, lang).apply();
    }

    public static boolean getRememberMe() {
        return mSP.getBoolean(KEY_REMEMBER_ME, false);
    }

    public static void setRememberMe(boolean rememberMe) {
        mSP.edit().putBoolean(KEY_REMEMBER_ME, rememberMe).apply();
    }

    public static boolean getLogin() {
        return mSP.getBoolean(KEY_LOGIN, false);
    }

    public static void setLogin(boolean login) {
        mSP.edit().putBoolean(KEY_LOGIN, login).apply();
    }


}
