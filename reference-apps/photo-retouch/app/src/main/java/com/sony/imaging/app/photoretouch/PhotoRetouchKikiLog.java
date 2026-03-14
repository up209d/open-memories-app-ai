package com.sony.imaging.app.photoretouch;

import com.sony.imaging.app.base.common.KikilogUtil;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class PhotoRetouchKikiLog extends KikilogUtil {
    public static final int PHOTO_RETOUCH_KIKILOG_APP_START = 260;
    public static final int PHOTO_RETOUCH_KIKILOG_BEAUTI_EFFECT = 4132;
    public static final int PHOTO_RETOUCH_KIKILOG_BRIGHTNESS = 4135;
    public static final int PHOTO_RETOUCH_KIKILOG_COLOR = 4134;
    public static final int PHOTO_RETOUCH_KIKILOG_FRAMING = 4131;
    public static final int PHOTO_RETOUCH_KIKILOG_HORIZONTAL = 4130;
    public static final int PHOTO_RETOUCH_KIKILOG_RESIZE = 4133;
    public static final int PHOTO_RETOUCH_KIKILOG_SATURATION = 4136;

    public static void photoRetouchSavingLog(int kitID) {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 512;
        Integer kikilogId = Integer.valueOf(kitID);
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
    }

    public static void photoRetouchStartLog() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 16;
        Integer kikilogId = Integer.valueOf(PHOTO_RETOUCH_KIKILOG_APP_START);
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
    }
}
