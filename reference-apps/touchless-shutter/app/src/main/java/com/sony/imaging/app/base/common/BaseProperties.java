package com.sony.imaging.app.base.common;

import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class BaseProperties {
    protected static final int API_VER_SUPPORT_DOWN_KEY_ASSIGN = 4;

    public static boolean isDownKeyAssignedToIndexTransition() {
        return 4 <= Environment.getVersionPfAPI() && ScalarProperties.getInt("ui.down.key.assign.in.playback") == 0;
    }
}
