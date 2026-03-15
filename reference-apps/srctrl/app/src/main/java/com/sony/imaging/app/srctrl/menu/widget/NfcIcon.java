package com.sony.imaging.app.srctrl.menu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.srctrl.util.NfcCtrlManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class NfcIcon extends ImageView implements NotificationListener {
    public NfcIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        String[] tags = {NfcCtrlManager.TAG_NFC_TOUCH_ENABLE};
        return tags;
    }

    private void drawNfcIcon() {
        if (NfcCtrlManager.getInstance().isNfcTouchEnable()) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        drawNfcIcon();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NfcCtrlManager.getInstance().setNotificationListener(this);
        drawNfcIcon();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        NfcCtrlManager.getInstance().removeNotificationListener(this);
        super.onDetachedFromWindow();
    }
}
