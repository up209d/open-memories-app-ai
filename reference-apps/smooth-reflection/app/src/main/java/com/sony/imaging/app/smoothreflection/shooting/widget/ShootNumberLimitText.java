package com.sony.imaging.app.smoothreflection.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.widget.ActiveText;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.smoothreflection.shooting.layout.SmoothReflectionStableLayout;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.math.BigDecimal;

/* loaded from: classes.dex */
public class ShootNumberLimitText extends ActiveText {
    private final int MAX_SHUTTER_SPEED;
    private final int MIN_SHUTTER_SPEED;
    private final String TAG;
    private ShutterSpeedChangeListener mShutterSpeedListener;
    public static int sTotalNumberOfShot = 0;
    public static int sShotNumber = 0;

    public ShootNumberLimitText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        this.MAX_SHUTTER_SPEED = 30;
        this.MIN_SHUTTER_SPEED = 8000;
        this.mShutterSpeedListener = null;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (SmoothReflectionStableLayout.sLayoutID == 1) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
        if (!SmoothReflectionCompositProcess.sbISShootingFinish) {
            String value = null;
            String selectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
            if (!ThemeController.CUSTOM.equals(selectedTheme)) {
                CameraEx.ShutterSpeedInfo infoSS = CameraSetting.getInstance().getShutterSpeedInfo();
                if (infoSS != null) {
                    int denominator = infoSS.currentShutterSpeed_d;
                    int numerator = infoSS.currentShutterSpeed_n;
                    if (denominator == 0 || denominator == 1) {
                        if (numerator > 30) {
                            value = "30\"";
                        } else {
                            value = "" + numerator + "\"";
                        }
                        AppLog.info(this.TAG, "Shutter Speed Value 3: " + numerator);
                    } else {
                        value = speedval(numerator, denominator);
                    }
                    AppLog.info(this.TAG, "dem : " + denominator + "   num : = " + numerator + "   value : = " + value);
                }
                int SmoothingLevel = 2;
                String smoothingLevel = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SMOOTH_LEVEL, ThemeController.MID);
                if (ThemeController.LOW.equals(smoothingLevel)) {
                    SmoothingLevel = 3;
                } else if (ThemeController.MID.equals(smoothingLevel)) {
                    SmoothingLevel = 2;
                } else if (ThemeController.HIGH.equals(smoothingLevel)) {
                    SmoothingLevel = 1;
                }
                if (ThemeController.WATERFLOW.equals(selectedTheme)) {
                    int index = SmoothReflectionConstants.SHUTTER_VALUE_LIST.indexOf(value);
                    if (-1 == index) {
                        if (value.length() >= SmoothReflectionConstants.SHUTTER_VALUE_LIST.get(0).length()) {
                            index = 0;
                        } else {
                            index = SmoothReflectionConstants.waterflow_shot.length - 1;
                        }
                    }
                    AppLog.info(this.TAG, "ShotNumber value: " + value);
                    AppLog.info(this.TAG, "ShotNumber index: " + index);
                    sTotalNumberOfShot = SmoothReflectionConstants.waterflow_shot[index][SmoothingLevel - 1];
                    setText("" + sTotalNumberOfShot);
                } else if (ThemeController.SMOKEHAZE.equals(selectedTheme)) {
                    int index2 = SmoothReflectionConstants.SHUTTER_VALUE_LIST.indexOf(value);
                    if (-1 == index2) {
                        if (value.length() >= SmoothReflectionConstants.SHUTTER_VALUE_LIST.get(0).length()) {
                            index2 = 0;
                        } else {
                            index2 = SmoothReflectionConstants.smokehazre_shot.length - 1;
                        }
                    }
                    AppLog.info(this.TAG, "ShotNumber value: " + value);
                    AppLog.info(this.TAG, "ShotNumber index: " + index2);
                    sTotalNumberOfShot = SmoothReflectionConstants.smokehazre_shot[index2][SmoothingLevel - 1];
                    setText("" + sTotalNumberOfShot);
                } else {
                    int index3 = SmoothReflectionConstants.SHUTTER_VALUE_LIST.indexOf(value);
                    if (-1 == index3) {
                        if (value.length() >= SmoothReflectionConstants.SHUTTER_VALUE_LIST.get(0).length()) {
                            index3 = 0;
                        } else {
                            index3 = SmoothReflectionConstants.slientrefmono_shot.length - 1;
                        }
                    }
                    AppLog.info(this.TAG, "ShotNumber value: " + value);
                    AppLog.info(this.TAG, "ShotNumber index: " + index3);
                    sTotalNumberOfShot = SmoothReflectionConstants.slientrefmono_shot[index3][SmoothingLevel - 1];
                    setText("" + sTotalNumberOfShot);
                }
            } else {
                setShotNumberForCustomTheme();
                setText("" + sTotalNumberOfShot);
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void setShotNumberForCustomTheme() {
        String selectedShot = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SHOTS, ThemeController.SUPPORTED_SHOTS_ARRAY[7]);
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[0].equals(selectedShot)) {
            sTotalNumberOfShot = 2;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[1].equals(selectedShot)) {
            sTotalNumberOfShot = 4;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[2].equals(selectedShot)) {
            sTotalNumberOfShot = 6;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[3].equals(selectedShot)) {
            sTotalNumberOfShot = 8;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[4].equals(selectedShot)) {
            sTotalNumberOfShot = 16;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[5].equals(selectedShot)) {
            sTotalNumberOfShot = 32;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[6].equals(selectedShot)) {
            sTotalNumberOfShot = 48;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[7].equals(selectedShot)) {
            sTotalNumberOfShot = 64;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[8].equals(selectedShot)) {
            sTotalNumberOfShot = 96;
            return;
        }
        if (ThemeController.SUPPORTED_SHOTS_ARRAY[9].equals(selectedShot)) {
            sTotalNumberOfShot = 128;
        } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[10].equals(selectedShot)) {
            sTotalNumberOfShot = 192;
        } else if (ThemeController.SUPPORTED_SHOTS_ARRAY[11].equals(selectedShot)) {
            sTotalNumberOfShot = 256;
        }
    }

    private int MAX(int i, float min) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (i > min) {
            AppLog.exit(this.TAG, AppLog.getMethodName());
            return i;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return (int) min;
    }

    private float MIN(int i, float inverse) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (i > inverse) {
            AppLog.exit(this.TAG, AppLog.getMethodName());
            return inverse;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return i;
    }

    private float INVERSE(Float shutterSpeed) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        float value = 1.0f / shutterSpeed.floatValue();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return value;
    }

    private Float setValue(Pair<Integer, Integer> ss) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        float value = SmoothReflectionConstants.INVALID_APERTURE_VALUE;
        if (((Integer) ss.second).intValue() != 0) {
            float value2 = ((Integer) ss.first).intValue() / ((Integer) ss.second).intValue();
            BigDecimal bi = new BigDecimal(String.valueOf(value2));
            value = bi.setScale(1, 4).floatValue();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return Float.valueOf(value);
    }

    /* loaded from: classes.dex */
    class ShutterSpeedChangeListener implements NotificationListener {
        private String[] TAGS = {CameraNotificationManager.SHUTTER_SPEED, SmoothReflectionCompositProcess.CAPTUREDIMAGETAG, SmoothReflectionCompositProcess.UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG};

        ShutterSpeedChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(CameraNotificationManager.SHUTTER_SPEED)) {
                ShootNumberLimitText.this.refresh();
                return;
            }
            if (tag.equals(SmoothReflectionCompositProcess.CAPTUREDIMAGETAG)) {
                int shootNumber = SmoothReflectionUtil.getInstance().getCurrentShootNumber();
                ShootNumberLimitText.this.setText("" + shootNumber + "/" + ShootNumberLimitText.sShotNumber);
            } else if (tag.equals(SmoothReflectionCompositProcess.UPDATESHOOTNUBERAFTERCANELSHOOTINGTAG)) {
                ShootNumberLimitText.this.refresh();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mShutterSpeedListener == null) {
            this.mShutterSpeedListener = new ShutterSpeedChangeListener();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mShutterSpeedListener;
    }

    public String speedval(int num, int den) {
        float Val = num / den;
        AppLog.info(this.TAG, "Shutter Speed Value 1: " + Val);
        float Val2 = Math.round(Val * 10.0f) / 10.0f;
        AppLog.info(this.TAG, "Shutter Speed Value 2: " + Val2);
        int Value = (int) (100.0f * Val2);
        switch (Value) {
            case 40:
                return "0.4\"";
            case PictureQualityController.QUALITY_FINE /* 50 */:
                return "0.5\"";
            case 60:
                return "0.6\"";
            case 80:
                return "0.8\"";
            case 130:
                return "1.3\"";
            case 160:
                return "1.6\"";
            case 250:
                return "2.5\"";
            case 320:
                return "3.2\"";
            default:
                if (den > 8000) {
                    String ret = "" + num + "/8000";
                    return ret;
                }
                String ret2 = "" + num + "/" + den;
                return ret2;
        }
    }
}
