package com.sony.imaging.app.base.common;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.TreeMap;

/* loaded from: classes.dex */
public class KikilogUtil {
    private static final TreeMap<String, Integer> PICTURE_EFFECTS_KIKILOG;
    private static final TreeMap<String, Integer> PICTURE_QUALITIES_KIKILOG;
    private static final int START_APP_TAG = 1;
    private static final int STOP_APP_TAG = 2;
    private byte[] mAppId = null;
    protected static final KikilogUtil M_INSTANCE = new KikilogUtil();
    private static final TreeMap<String, Integer> EXPOSURES_KIKILOG = new TreeMap<>();

    static {
        EXPOSURES_KIKILOG.put(ExposureModeController.PROGRAM_AUTO_MODE, 769);
        EXPOSURES_KIKILOG.put("Aperture", 770);
        EXPOSURES_KIKILOG.put(ExposureModeController.SHUTTER_MODE, 771);
        EXPOSURES_KIKILOG.put(ExposureModeController.MANUAL_MODE, Integer.valueOf(AppRoot.USER_KEYCODE.S1_OFF));
        EXPOSURES_KIKILOG.put(ExposureModeController.INTELLIGENT_AUTO_MODE, 773);
        EXPOSURES_KIKILOG.put(ExposureModeController.HAND_HELD_TWILIGHT, Integer.valueOf(AppRoot.USER_KEYCODE.S2_OFF));
        EXPOSURES_KIKILOG.put(ExposureModeController.ANTI_MOTION_BLUR, 775);
        EXPOSURES_KIKILOG.put("portrait", 776);
        EXPOSURES_KIKILOG.put(ExposureModeController.SPORTS, 777);
        EXPOSURES_KIKILOG.put(ExposureModeController.MACRO, 778);
        EXPOSURES_KIKILOG.put("landscape", 779);
        EXPOSURES_KIKILOG.put("sunset", 780);
        EXPOSURES_KIKILOG.put("twilight", 781);
        EXPOSURES_KIKILOG.put(ExposureModeController.TWILIGHT_PORTRAIT, 782);
        EXPOSURES_KIKILOG.put(ExposureModeController.ADVANCE_SPORTS, 783);
        EXPOSURES_KIKILOG.put(ExposureModeController.PET, 784);
        EXPOSURES_KIKILOG.put(ExposureModeController.GOURMET, 785);
        EXPOSURES_KIKILOG.put(ExposureModeController.BEACH, Integer.valueOf(AppRoot.USER_KEYCODE.LENS_DETACH));
        EXPOSURES_KIKILOG.put(ExposureModeController.SNOW, 787);
        EXPOSURES_KIKILOG.put(ExposureModeController.FIREWORKS, 788);
        EXPOSURES_KIKILOG.put(ExposureModeController.SOFT_SKIN, 789);
        EXPOSURES_KIKILOG.put(ExposureModeController.HIGH_SENSITIVITY, 790);
        PICTURE_EFFECTS_KIKILOG = new TreeMap<>();
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_TOY_CAMERA, 833);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_POP_COLOR, 834);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.POSTERIZATION_COLOR, 835);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.POSTERIZATION_MONO, 836);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_RETRO_PHOTO, 837);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_SOFT_HIGH_KEY, 838);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.PART_COLOR_RED, 839);
        PICTURE_EFFECTS_KIKILOG.put("green", 840);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.PART_COLOR_BLUE, 841);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.PART_COLOR_YELLOW, 842);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_SOFT_FOCUS, 843);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_HDR_ART, 844);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_RICH_TONE_MONOCHROME, 845);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_MINIATURE, 846);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.WATERCOLOR, 847);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_ILLUST, 848);
        PICTURE_EFFECTS_KIKILOG.put(PictureEffectController.MODE_ROUGH_MONO, 849);
        PICTURE_QUALITIES_KIKILOG = new TreeMap<>();
        PICTURE_QUALITIES_KIKILOG.put(PictureQualityController.PICTURE_QUALITY_RAW, 897);
        PICTURE_QUALITIES_KIKILOG.put(PictureQualityController.PICTURE_QUALITY_RAWJPEG, 898);
    }

    public static KikilogUtil getInstance() {
        return M_INSTANCE;
    }

    public void setAppName(String appName) {
        int id = 0;
        if (appName != null) {
            id = appName.hashCode();
        }
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bout);
            try {
                out.writeInt(id);
                this.mAppId = bout.toByteArray();
            } catch (Exception e) {
                e = e;
                e.printStackTrace();
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public void startApp() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 16;
        Kikilog.setUserLog(1, options, this.mAppId);
    }

    public void stopApp() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 16;
        Kikilog.setUserLog(2, options, this.mAppId);
    }

    public static void incrementShootingCount() {
        Integer kikilogId;
        Integer kikilogId2;
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        ExposureModeController exposureMode = ExposureModeController.getInstance();
        String mode = exposureMode.getValue(null);
        if (mode != null && (kikilogId2 = EXPOSURES_KIKILOG.get(mode)) != null) {
            Kikilog.setUserLog(kikilogId2.intValue(), options);
        }
        PictureEffectController pictureEffect = PictureEffectController.getInstance();
        String mode2 = pictureEffect.getValue(PictureEffectController.PICTUREEFFECT);
        if (mode2 != null) {
            Integer kikilogId3 = PICTURE_EFFECTS_KIKILOG.get(mode2);
            if (kikilogId3 == null && (mode2 = pictureEffect.getValue()) != null) {
                kikilogId3 = PICTURE_EFFECTS_KIKILOG.get(mode2);
            }
            if (kikilogId3 != null) {
                Kikilog.setUserLog(kikilogId3.intValue(), options);
            }
        }
        PictureQualityController pictureQuality = PictureQualityController.getInstance();
        try {
            mode2 = pictureQuality.getValue(PictureQualityController.PICTURE_QUALITY_FINE);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        if (mode2 != null && (kikilogId = PICTURE_QUALITIES_KIKILOG.get(mode2)) != null) {
            Kikilog.setUserLog(kikilogId.intValue(), options);
        }
    }
}
