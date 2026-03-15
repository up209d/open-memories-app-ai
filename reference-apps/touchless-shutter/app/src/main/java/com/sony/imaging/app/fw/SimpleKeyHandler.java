package com.sony.imaging.app.fw;

import android.view.KeyEvent;

/* loaded from: classes.dex */
public abstract class SimpleKeyHandler implements IKeyHandler, IConvertibleKeyHandler {
    protected KeyConverter mKeyConverter;
    protected KeyReceiver target;

    protected abstract String getKeyConvCategory();

    protected KeyConverter getKeyConverter(IConvertibleKeyHandler handler) {
        return new KeyConverter(handler);
    }

    public SimpleKeyHandler() {
        this.mKeyConverter = null;
        this.mKeyConverter = getKeyConverter(this);
    }

    public SimpleKeyHandler(IConvertibleKeyHandler key) {
        this.mKeyConverter = null;
        this.mKeyConverter = getKeyConverter(key);
    }

    protected boolean canRepeat(int KeyCode) {
        return true;
    }

    @Override // com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        int repCount = event.getRepeatCount();
        if (repCount != 0 && !canRepeat(code)) {
            return 0;
        }
        int ret = dispatchKeyConversion(event);
        return ret;
    }

    @Override // com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    public final int dispatchKeyConversion(KeyEvent event) {
        return this.mKeyConverter.apply(event, getKeyConvCategory());
    }

    @Override // com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        return 0;
    }
}
