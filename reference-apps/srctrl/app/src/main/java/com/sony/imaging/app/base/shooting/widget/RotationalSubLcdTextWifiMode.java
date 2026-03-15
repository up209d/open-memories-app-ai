package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class RotationalSubLcdTextWifiMode extends RotationalSubLcdTextView {
    private static final String KEY_WIFI_MODE_SUPPORTED = "wifi-mode-supported";
    private static final String TAG = RotationalSubLcdTextWifiMode.class.getSimpleName();
    private static Boolean mIsWifiMultiSupported = null;

    public RotationalSubLcdTextWifiMode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "constructor");
        if (mIsWifiMultiSupported == null) {
            mIsWifiMultiSupported = Boolean.valueOf(isWifiMultiSupported(context));
        }
    }

    public RotationalSubLcdTextWifiMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "constructor");
        if (mIsWifiMultiSupported == null) {
            mIsWifiMultiSupported = Boolean.valueOf(isWifiMultiSupported(context));
        }
    }

    public RotationalSubLcdTextWifiMode(Context context) {
        super(context);
        Log.d(TAG, "constructor");
        if (mIsWifiMultiSupported == null) {
            mIsWifiMultiSupported = Boolean.valueOf(isWifiMultiSupported(context));
        }
    }

    private boolean isWifiMultiSupported(Context context) {
        String metaData;
        if (!(context instanceof Activity)) {
            return false;
        }
        Activity activity = (Activity) context;
        PackageManager pm = activity.getPackageManager();
        try {
            ActivityInfo info = pm.getActivityInfo(activity.getComponentName(), LiveviewCommon.PAYLOAD_HEADER_SIZE);
            if (info.metaData == null || (metaData = info.metaData.getString(KEY_WIFI_MODE_SUPPORTED)) == null) {
                return false;
            }
            String[] supportedWifiModes = metaData.split(",");
            for (String supportedWifiMode : supportedWifiModes) {
                if (FocusAreaController.MULTI.equals(supportedWifiMode)) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        if (!mIsWifiMultiSupported.booleanValue() || isAirPlaneModeOn()) {
            return false;
        }
        int wifiMode = ScalarProperties.getInt("wifi.setting");
        return wifiMode == 2;
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        int wifiMode = ScalarProperties.getInt("wifi.setting");
        return wifiMode == 2 ? getResources().getString(R.string.megabyteShort) : "";
    }

    private boolean isAirPlaneModeOn() {
        return Settings.System.getInt(getContext().getContentResolver(), "airplane_mode_on", 0) == 1;
    }
}
