package com.sony.imaging.app.base.common;

import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class BaseProperties {
    protected static final int API_VER_MOVIE_REC_QUICK_RESTART_SUPPORTED = 6;
    protected static final int API_VER_SUPPORT_DOWN_KEY_ASSIGN = 4;
    protected static final int API_VER_SUPPORT_TRANSITION_MODE = 12;
    protected static boolean isRequestingPowerOff = false;

    public static boolean isDownKeyAssignedToIndexTransition() {
        return 4 <= Environment.getVersionPfAPI() && ScalarProperties.getInt("ui.down.key.assign.in.playback") == 0;
    }

    public static int getD2ATransitionMode() {
        if (12 <= Environment.getVersionPfAPI()) {
            return ScalarProperties.getInt("diadem.2.scalar.transition.mode", 0);
        }
        return 0;
    }

    public static void setD2ATransitionMode(int mode) {
        if (12 <= Environment.getVersionPfAPI()) {
            ScalarProperties.setInt("diadem.2.scalar.transition.mode", mode);
        }
    }

    public static void requestingPowerOff(boolean isRequesting) {
        isRequestingPowerOff = isRequesting;
    }

    public static boolean isPowerOffRequested() {
        return isRequestingPowerOff;
    }

    public static boolean isRecDisabledOnStreamWriting() {
        int category = ScalarProperties.getInt("model.category");
        return 6 <= Environment.getVersionPfAPI() ? 1 != ScalarProperties.getInt("ui.movie.rec.quick.restart.supported") : 2 == category || 2 == category;
    }
}
