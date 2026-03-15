package com.sony.scalar.lib.ssdpdevice.v1_1;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/* loaded from: classes.dex */
public final class DevLog {
    private static final String APPLICATION_PREFIX = "[Server] ";
    private static final String DEFAULT_TAG = "DD Server";
    private static final String LIFE_CYCLE_PREFIX = "[LifeCycle] ";
    private static boolean sLogEnabled = false;

    private DevLog() {
    }

    public static void enable(boolean en) {
        sLogEnabled = en;
    }

    public static void toast(Context context, String str) {
        if (sLogEnabled) {
            Toast.makeText(context, "DD Server: " + str, 1).show();
        }
    }

    public static void toast(Context context, View view) {
        if (sLogEnabled) {
            Toast toast = new Toast(context);
            LinearLayout layout = new LinearLayout(context);
            TextView text = new TextView(context);
            text.setText("DevToast: ");
            layout.addView(text);
            layout.addView(view);
            layout.setVerticalGravity(16);
            layout.setBackgroundColor(-12303292);
            layout.setPadding(7, 3, 7, 3);
            toast.setView(layout);
            toast.show();
        }
    }

    public static void v(String str) {
        if (sLogEnabled) {
            v(DEFAULT_TAG, str);
        }
    }

    public static void v(String tag, String str) {
        if (sLogEnabled) {
            Log.v(tag, APPLICATION_PREFIX + str);
        }
    }

    public static void d(String str) {
        if (sLogEnabled) {
            d(DEFAULT_TAG, str);
        }
    }

    public static void d(String tag, String str) {
        if (sLogEnabled) {
            Log.d(tag, APPLICATION_PREFIX + str);
        }
    }

    public static void i(String str) {
        if (sLogEnabled) {
            i(DEFAULT_TAG, str);
        }
    }

    public static void i(String tag, String str) {
        if (sLogEnabled) {
            Log.i(tag, APPLICATION_PREFIX + str);
        }
    }

    public static void w(String str) {
        if (sLogEnabled) {
            w(DEFAULT_TAG, str);
        }
    }

    public static void w(String tag, String str) {
        if (sLogEnabled) {
            Log.w(tag, APPLICATION_PREFIX + str);
        }
    }

    public static void e(String str) {
        if (sLogEnabled) {
            e(DEFAULT_TAG, str);
        }
    }

    public static void e(String tag, String str) {
        if (sLogEnabled) {
            Log.e(tag, APPLICATION_PREFIX + str);
        }
    }

    public static void l(String str) {
        if (sLogEnabled) {
            l(DEFAULT_TAG, str);
        }
    }

    public static void l(String tag, String str) {
        if (sLogEnabled) {
            d(tag, LIFE_CYCLE_PREFIX + str);
        }
    }
}
