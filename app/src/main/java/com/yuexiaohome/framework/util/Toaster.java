/*
 * Copyright 2012 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuexiaohome.framework.util;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.MessageFormat;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Helper to show {@link android.widget.Toast} notifications
 */
public class Toaster {

    private static void show(final Context context, final String message,
            final int duration) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(context, message, duration).show();
        }
    }

    private static void show(final Context context, final int resId,
            final int duration) {
        if (context == null) {
            return;
        }
        show(context, context.getString(resId), duration);
    }


    public static void showLong(final Context context, int resId) {
        show(context, resId, LENGTH_LONG);
    }

    public static void showShort(final Context context, final int resId) {
        show(context, resId, LENGTH_SHORT);
    }


    public static void showLong(final Context context, final String message) {
        show(context, message, LENGTH_LONG);
    }


    public static void showShort(final Context context, final String message) {
        show(context, message, LENGTH_SHORT);
    }


    public static void showLong(final Context context, final String message,
            final Object... args) {
        String formatted = MessageFormat.format(message, args);
        show(context, formatted, LENGTH_LONG);
    }


    public static void showShort(final Context context, final String message,
            final Object... args) {
        String formatted = MessageFormat.format(message, args);
        show(context, formatted, LENGTH_SHORT);
    }


    public static void showLong(final Context context, final int resId,
            final Object... args) {
        if (context == null) {
            return;
        }

        String message = context.getString(resId);
        showLong(context, message, args);
    }


    public static void showShort(final Context context, final int resId,
            final Object... args) {
        if (context == null) {
            return;
        }

        String message = context.getString(resId);
        showShort(context, message, args);
    }
}
