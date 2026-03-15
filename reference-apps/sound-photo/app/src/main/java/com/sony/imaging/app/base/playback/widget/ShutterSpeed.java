package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class ShutterSpeed extends LabelFixedFontFileInfo {
    private static final int DECIMAL_FACTOR = 10;
    private static final String DOT = ".";
    private static final int INDEX_DENOM = 1;
    private static final int INDEX_LENGTH = 2;
    private static final int INDEX_NUMER = 0;
    private static final int INIT_ONE = 1;
    private static final int INIT_ZERO = 0;
    private static final int MAX = 100;
    private static final int MAX_DECIMAL = 1000;
    private static final int MAX_DENOMINATOR = 1000000;
    private static final int MAX_INT = 100000;
    private static final int MIN_SPEED = 40;
    private static final String MSG_ERR_EXPOSURE_TIME_IS_NULL = "###ERROR: exposureTime is null.";
    private static final String SEPARATOR_SLASH = "/";
    private static final String TAG = ShutterSpeed.class.getSimpleName();
    private String mExposureTime;

    public ShutterSpeed(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mExposureTime = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mExposureTime = null;
        super.onAttachedToWindow();
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        String exposureTime = null;
        if (info != null) {
            exposureTime = info.getString("ExposureTime");
        }
        setValue(exposureTime);
    }

    public void setValue(String exposureTime) {
        if (exposureTime != null) {
            if (!exposureTime.equals(this.mExposureTime)) {
                int exposureTimeNumer = -1;
                int exposureTimeDenom = -1;
                String[] exposureTimeValue = exposureTime.split("/");
                if (exposureTimeValue != null && exposureTimeValue.length == 2) {
                    exposureTimeNumer = Integer.parseInt(exposureTimeValue[0]);
                    exposureTimeDenom = Integer.parseInt(exposureTimeValue[1]);
                }
                setValue(exposureTimeNumer, exposureTimeDenom);
            }
        } else {
            setVisibility(4);
            Log.w(TAG, MSG_ERR_EXPOSURE_TIME_IS_NULL);
        }
        this.mExposureTime = exposureTime;
    }

    public void setValue(int exposureTimeNumer, int exposureTimeDenom) {
        boolean isDisplay = false;
        String shutterSpeed = "";
        if (exposureTimeNumer > 0 && exposureTimeDenom > 0) {
            float exposureTime4Disp = exposureTimeNumer / exposureTimeDenom;
            int exposureTimeDeci_int = exposureTimeNumer / exposureTimeDenom;
            int exposureTimeDeci_point = ((exposureTimeNumer % exposureTimeDenom) * 10) / exposureTimeDenom;
            if (exposureTime4Disp * 100.0f >= 40.0f) {
                if (exposureTimeDeci_point == 0) {
                    if (100000 > exposureTimeDeci_int && exposureTimeDeci_int > 0) {
                        shutterSpeed = String.format(getResources().getString(17041718), String.valueOf(exposureTimeDeci_int));
                        isDisplay = true;
                    }
                } else if (MAX_DECIMAL > exposureTimeDeci_int && exposureTimeDeci_int >= 0 && 10 > exposureTimeDeci_point && exposureTimeDeci_point >= 0) {
                    shutterSpeed = String.format(getResources().getString(17041718), LogHelper.getScratchBuilder(String.valueOf(exposureTimeDeci_int)).append(".").append(exposureTimeDeci_point).toString());
                    isDisplay = true;
                }
            } else if (0.0f < exposureTime4Disp * 100.0f && exposureTime4Disp * 100.0f < 40.0f) {
                if (exposureTimeNumer == 0) {
                    Log.w(TAG, "exposureTimeNumer error in shutter speed info");
                } else {
                    int denominator = exposureTimeDenom / exposureTimeNumer;
                    if (denominator < MAX_DENOMINATOR) {
                        shutterSpeed = String.format(getResources().getString(R.string.restr_pin_create_pin), String.valueOf(1), String.valueOf(denominator));
                        isDisplay = true;
                    }
                }
            } else {
                Log.w(TAG, "invalid value " + exposureTime4Disp);
            }
        }
        setText(shutterSpeed);
        setVisibility(isDisplay ? 0 : 4);
    }
}
