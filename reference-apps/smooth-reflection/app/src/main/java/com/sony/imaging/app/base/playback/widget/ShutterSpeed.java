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
    private static final String MSG_ERR_EXPOSURE_TIME_IS_NULL = "###ERROR: exposureTime is null.";
    private static final String SEPARATOR_SLASH = "/";
    private static final String TAG = ShutterSpeed.class.getSimpleName();
    private static final int THRES_DEMON_PATTERN_A = 10000;
    private static final int THRES_INT_PATTERN_C = 1000;
    private static final int THRES_NUMER_PATTERN_A = 10;
    private static final int THRES_NUMER_PATTERN_B = 100000;
    private static final int THRES_POINT_PATTERN_A = 4;
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
                String[] exposureTimeValue = exposureTime.split(SEPARATOR_SLASH);
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
            int exposureTimeDeci_int = exposureTimeNumer / exposureTimeDenom;
            int exposureTimeDeci_point = ((exposureTimeNumer % exposureTimeDenom) * 10) / exposureTimeDenom;
            if (exposureTimeDeci_int == 0 && exposureTimeDeci_point < 4) {
                if (exposureTimeNumer < 10 && exposureTimeDenom < THRES_DEMON_PATTERN_A) {
                    shutterSpeed = String.format(getResources().getString(R.string.restr_pin_create_pin), Integer.valueOf(exposureTimeNumer), Integer.valueOf(exposureTimeDenom));
                }
            } else if (exposureTimeDenom == 1) {
                if (exposureTimeNumer < 100000) {
                    shutterSpeed = String.format(getResources().getString(17041718), String.valueOf(exposureTimeNumer));
                }
            } else if (exposureTimeDeci_int < THRES_INT_PATTERN_C) {
                shutterSpeed = String.format(getResources().getString(17041718), LogHelper.getScratchBuilder(String.valueOf(exposureTimeDeci_int)).append(DOT).append(exposureTimeDeci_point).toString());
            }
            isDisplay = true;
        }
        setText(shutterSpeed);
        setVisibility(isDisplay ? 0 : 4);
    }
}
