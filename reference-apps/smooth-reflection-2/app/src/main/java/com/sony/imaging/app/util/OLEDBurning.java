package com.sony.imaging.app.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.FrameLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.util.List;

/* loaded from: classes.dex */
public class OLEDBurning {
    public static final int BURN_IN_OLEAD_ARISE = 1;
    public static final int BURN_IN_OLEAD_CLEAR = 0;
    private static final String LOG_MSG_ERROR_STR1 = "unkown value is coming";
    private static final String TAG = "OLEDBuring";
    public static final String TAG_OLEADSCREEN_CHANGE = "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange";
    static String[] tags = {"com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};
    private DisplayManager mDisplayManager;
    private AppRoot mRoot;
    protected BlendDrawable mMode = null;
    protected String OLEDType = null;
    private int mOleadScreenStatus = 0;

    public OLEDBurning(Context context) {
        this.mRoot = null;
        this.mDisplayManager = null;
        this.mRoot = (AppRoot) context;
        this.mDisplayManager = new DisplayManager();
        if (Environment.getVersionOfHW() < 2) {
            DisplayManager.OnScreenDisplayListener mScreenDisplayListener = new OnOleadScreenDisplayListener();
            this.mDisplayManager.setOnScreenDisplayListener(mScreenDisplayListener);
            OnDisplayEventListener mDisplayEventListener = new OnDisplayEventListener();
            this.mDisplayManager.setDisplayStatusListener(mDisplayEventListener);
            refresh();
        }
    }

    public void terminate() {
        if (Environment.getVersionOfHW() < 2) {
            this.mDisplayManager.releaseOnScreenDisplayListener();
        }
        this.mDisplayManager.finish();
        this.mDisplayManager = null;
        this.mOleadScreenStatus = 0;
    }

    protected void refresh() {
        int burn = getOleadScreenStatus();
        FrameLayout v = (FrameLayout) this.mRoot.getView();
        int color = 0;
        String device = this.mDisplayManager.getActiveDevice();
        if ("DEVICE_ID_FINDER".equals(device)) {
            if (1 == burn) {
                Log.i("OLED", "Arise Finder");
                if ("SCREEN_GAIN_CONTROL_LUMINANCE_ONLY".equals(this.OLEDType)) {
                    color = getOLEDBurningBrightnessColor();
                } else if ("SCREEN_GAIN_CONTROL_LUMINANCE_ALPHA".equals(this.OLEDType)) {
                    color = getOLEDBurningAlphaBrightnessColor();
                } else if ("SCREEN_GAIN_NO_CONTROL".equals(this.OLEDType)) {
                    color = getFinderClearColor();
                }
                if (this.mMode == null || this.mMode.getColor() != color) {
                    this.mMode = new BlendDrawable(color);
                }
            } else {
                Log.i("OLED", "Clear Finder");
                int color2 = getFinderClearColor();
                this.mMode = new BlendDrawable(color2);
            }
        } else {
            Log.i("OLED", "Clear Panel");
            this.mMode = null;
        }
        v.setForeground(this.mMode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class BlendDrawable extends Drawable {
        int color;

        public BlendDrawable(int c) {
            this.color = c;
        }

        int getColor() {
            return this.color;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawColor(this.color, PorterDuff.Mode.MULTIPLY);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public int getOleadScreenStatus() {
        return this.mOleadScreenStatus;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOLEDType(String type) {
        if (Environment.getVersionOfHW() < 2) {
            this.OLEDType = type;
            return;
        }
        List<String> list = this.mDisplayManager.getSupportedScreenGainControlTypes();
        if (list.contains(type)) {
            this.mDisplayManager.setScreenGainControlType(type);
        } else if (list.contains("SCREEN_GAIN_CONTROL_LUMINANCE_ONLY")) {
            this.mDisplayManager.setScreenGainControlType("SCREEN_GAIN_CONTROL_LUMINANCE_ONLY");
        }
    }

    public int getFinderClearColor() {
        return -6381922;
    }

    public int getOLEDBurningAlphaBrightnessColor() {
        return -8947849;
    }

    public int getOLEDBurningBrightnessColor() {
        return -8947849;
    }

    /* loaded from: classes.dex */
    class OnOleadScreenDisplayListener implements DisplayManager.OnScreenDisplayListener {
        OnOleadScreenDisplayListener() {
        }

        public void onPreventBurnInOLED(int arise) {
            if (arise == 0 || arise == 1) {
                Log.i("OLED", "onPreventBurnInOLED");
                OLEDBurning.this.mOleadScreenStatus = arise;
                OLEDBurning.this.refresh();
                return;
            }
            Log.e(OLEDBurning.TAG, OLEDBurning.LOG_MSG_ERROR_STR1);
        }
    }

    /* loaded from: classes.dex */
    class OnDisplayEventListener implements DisplayManager.DisplayEventListener {
        OnDisplayEventListener() {
        }

        public void onDeviceStatusChanged(int eventId) {
            if (4096 == eventId) {
                Log.i("OLED", "onDeviceStatusChanged");
                OLEDBurning.this.refresh();
            }
        }
    }
}
