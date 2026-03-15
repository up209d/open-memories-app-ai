package com.sony.imaging.app.base.common.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.util.Log;

/* loaded from: classes.dex */
public class WifiSubLCDIcon extends SubLcdIconView {
    private static final String TAG = "WifiSubLCDIcon";
    BroadcastReceiver mBroadcastReceiver;
    private WifiManager wifiManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdIconView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow()");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        getContext().registerReceiver(this.mBroadcastReceiver, filter);
        boolean visible = isVisible();
        setOwnVisible(visible);
        if (visible) {
            refresh();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdIconView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow()");
        getContext().unregisterReceiver(this.mBroadcastReceiver);
    }

    public WifiSubLCDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.wifiManager = null;
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.sony.imaging.app.base.common.widget.WifiSubLCDIcon.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.net.wifi.WIFI_STATE_CHANGED".equals(intent.getAction())) {
                    boolean visible = WifiSubLCDIcon.this.isVisible();
                    WifiSubLCDIcon.this.setOwnVisible(visible);
                    if (visible) {
                        WifiSubLCDIcon.this.refresh();
                    }
                }
            }
        };
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
    }

    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    protected void refresh() {
    }

    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    protected boolean isVisible() {
        boolean visible = false;
        try {
            if (this.wifiManager != null && this.wifiManager.isWifiEnabled()) {
                visible = true;
            }
            Log.d(TAG, "isVisible() : " + visible);
            return visible;
        } catch (SecurityException e) {
            return false;
        }
    }
}
