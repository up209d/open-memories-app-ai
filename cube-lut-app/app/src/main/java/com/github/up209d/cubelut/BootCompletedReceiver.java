package com.github.up209d.cubelut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.i("CubeLUT", "Boot completed");
        } catch (Exception e) {
            // Must never crash here - camera OS boot depends on all receivers completing
        }
    }
}
