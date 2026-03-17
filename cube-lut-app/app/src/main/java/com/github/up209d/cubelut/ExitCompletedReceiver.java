package com.github.up209d.cubelut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ExitCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.i("CubeLUT", "Exit completed");
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            // Must never crash here
        }
    }
}
