package com.sony.imaging.app.fw;

import android.view.KeyEvent;

/* loaded from: classes.dex */
public class InnerViewKeyHandler {
    protected String mCategory;
    protected KeyConverter mKeyConverter;

    public InnerViewKeyHandler(IConvertibleKeyHandler key, String category) {
        this.mKeyConverter = null;
        this.mKeyConverter = getKeyConverter(key);
        this.mCategory = category;
    }

    protected KeyConverter getKeyConverter(IConvertibleKeyHandler handler) {
        return new KeyConverter(handler);
    }

    protected boolean canRepeat(int KeyCode) {
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int ret = 0;
        int code = event.getScanCode();
        int repCount = event.getRepeatCount();
        if (repCount == 0 || canRepeat(code)) {
            ret = dispatchKeyConversion(event);
        }
        return 1 == ret;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int ret = dispatchKeyConversion(event);
        return 1 == ret;
    }

    public final int dispatchKeyConversion(KeyEvent event) {
        return this.mKeyConverter.apply(event, getKeyConvCategory());
    }

    protected String getKeyConvCategory() {
        return this.mCategory;
    }

    public void setKeyConvCategory(String category) {
        this.mCategory = category;
    }
}
