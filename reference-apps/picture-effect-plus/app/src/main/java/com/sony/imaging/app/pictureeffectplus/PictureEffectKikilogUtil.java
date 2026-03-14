package com.sony.imaging.app.pictureeffectplus;

import android.util.Log;
import com.sony.imaging.app.base.common.KikilogUtil;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.util.TreeMap;

/* loaded from: classes.dex */
public class PictureEffectKikilogUtil extends KikilogUtil {
    private static final TreeMap<String, Integer> EFFECTS_KIKILOG = new TreeMap<>();
    public static final int PICTURE_EFFECT_PLUS_APP_START = 257;
    private static final String TAG = "PictureEffectKikilogUtil";

    static {
        EFFECTS_KIKILOG.put(PictureEffectPlusController.MODE_PART_COLOR_PLUS, 4097);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.MODE_MINIATURE_PLUS, 4098);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS, 4099);
        EFFECTS_KIKILOG.put("watercolor", 4100);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.ILLUST_HIGH, 4103);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.ILLUST_MID, 4104);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.ILLUST_LOW, 4105);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.SOFT_HIGH_KEY_BLUE, 4106);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.SOFT_HIGH_KEY_GREEN, 4107);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.SOFT_HIGH_KEY_BLUE, 4106);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.SOFT_HIGH_KEY_GREEN, 4107);
        EFFECTS_KIKILOG.put(PictureEffectPlusController.SOFT_HIGH_KEY_PINK, 4108);
    }

    public static void incrementShootingPictureEffectPlusCount() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        PictureEffectPlusController mController = PictureEffectPlusController.getInstance();
        String mode = mController.getValue();
        Integer kikilogId = EFFECTS_KIKILOG.get(mode);
        if (kikilogId == null) {
            String optionValue = mController.getValue(mode);
            kikilogId = EFFECTS_KIKILOG.get(optionValue);
        }
        if (kikilogId != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
            Log.i(TAG, "kikilogId:" + Integer.toString(kikilogId.intValue()));
        }
    }

    public static void PictureEffectPlusStart() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Kikilog.setUserLog(PICTURE_EFFECT_PLUS_APP_START, options);
        Log.i(TAG, "PictureEffectPlusStart");
    }
}
