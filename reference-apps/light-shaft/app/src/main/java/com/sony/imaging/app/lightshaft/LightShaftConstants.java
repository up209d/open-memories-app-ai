package com.sony.imaging.app.lightshaft;

/* loaded from: classes.dex */
public class LightShaftConstants {
    private boolean isEVDialRotated = false;
    private static LightShaftConstants sLightShaftConstant = null;
    public static int sLastExposureCompesationIndex = 0;

    private LightShaftConstants() {
    }

    public static LightShaftConstants getInstance() {
        if (sLightShaftConstant == null) {
            sLightShaftConstant = new LightShaftConstants();
        }
        return sLightShaftConstant;
    }

    public boolean isEVDialRotated() {
        return this.isEVDialRotated;
    }

    public void setEVDialRotated(boolean isEVDialRotated) {
        this.isEVDialRotated = isEVDialRotated;
    }
}
