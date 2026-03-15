package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextAEShift extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextAEShift.class.getSimpleName();
    public static SparseIntArray pf2app = null;
    private final int APP_AE_MINUS_ONE;
    private final int APP_AE_MINUS_ONE_SEVEN;
    private final int APP_AE_MINUS_ONE_THREE;
    private final int APP_AE_MINUS_POINT_SEVEN;
    private final int APP_AE_MINUS_POINT_THREE;
    private final int APP_AE_MINUS_TWO;
    private final int APP_AE_ONE;
    private final int APP_AE_ONE_SEVEN;
    private final int APP_AE_ONE_THREE;
    private final int APP_AE_POINT_SEVEN;
    private final int APP_AE_POINT_THREE;
    private final int APP_AE_TWO;
    private final int APP_AE_ZERO;
    private final int PF_AE_0;
    private final int PF_AE_1;
    private final int PF_AE_2;
    private final int PF_AE_3;
    private final int PF_AE_4;
    private final int PF_AE_5;
    private final int PF_AE_6;
    private final int PF_AE_MINUS_1;
    private final int PF_AE_MINUS_2;
    private final int PF_AE_MINUS_3;
    private final int PF_AE_MINUS_4;
    private final int PF_AE_MINUS_5;
    private final int PF_AE_MINUS_6;

    private void setAEShiftDictionary() {
        pf2app = new SparseIntArray();
        pf2app.put(-6, R.string.permgrouplab_storage);
        pf2app.put(-5, R.string.permission_request_notification_title);
        pf2app.put(-4, R.string.permission_request_notification_with_subtitle);
        pf2app.put(-3, R.string.permlab_acceptHandover);
        pf2app.put(-2, R.string.permlab_accessBackgroundLocation);
        pf2app.put(-1, R.string.permlab_accessDrmCertificates);
        pf2app.put(0, R.string.permlab_accessLocationExtraCommands);
        pf2app.put(1, R.string.permlab_accessNetworkConditions);
        pf2app.put(2, R.string.permlab_accessNetworkState);
        pf2app.put(3, R.string.permlab_accessNotifications);
        pf2app.put(4, R.string.permlab_accessWifiState);
        pf2app.put(5, R.string.permlab_accessWimaxState);
        pf2app.put(6, R.string.permlab_access_notification_policy);
    }

    public RotationalSubLcdTextAEShift(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.PF_AE_MINUS_6 = -6;
        this.PF_AE_MINUS_5 = -5;
        this.PF_AE_MINUS_4 = -4;
        this.PF_AE_MINUS_3 = -3;
        this.PF_AE_MINUS_2 = -2;
        this.PF_AE_MINUS_1 = -1;
        this.PF_AE_0 = 0;
        this.PF_AE_1 = 1;
        this.PF_AE_2 = 2;
        this.PF_AE_3 = 3;
        this.PF_AE_4 = 4;
        this.PF_AE_5 = 5;
        this.PF_AE_6 = 6;
        this.APP_AE_MINUS_TWO = R.string.permgrouplab_storage;
        this.APP_AE_MINUS_ONE_SEVEN = R.string.permission_request_notification_title;
        this.APP_AE_MINUS_ONE_THREE = R.string.permission_request_notification_with_subtitle;
        this.APP_AE_MINUS_ONE = R.string.permlab_acceptHandover;
        this.APP_AE_MINUS_POINT_SEVEN = R.string.permlab_accessBackgroundLocation;
        this.APP_AE_MINUS_POINT_THREE = R.string.permlab_accessDrmCertificates;
        this.APP_AE_ZERO = R.string.permlab_accessLocationExtraCommands;
        this.APP_AE_POINT_THREE = R.string.permlab_accessNetworkConditions;
        this.APP_AE_POINT_SEVEN = R.string.permlab_accessNetworkState;
        this.APP_AE_ONE = R.string.permlab_accessNotifications;
        this.APP_AE_ONE_THREE = R.string.permlab_accessWifiState;
        this.APP_AE_ONE_SEVEN = R.string.permlab_accessWimaxState;
        this.APP_AE_TWO = R.string.permlab_access_notification_policy;
        setAEShiftDictionary();
    }

    public RotationalSubLcdTextAEShift(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.PF_AE_MINUS_6 = -6;
        this.PF_AE_MINUS_5 = -5;
        this.PF_AE_MINUS_4 = -4;
        this.PF_AE_MINUS_3 = -3;
        this.PF_AE_MINUS_2 = -2;
        this.PF_AE_MINUS_1 = -1;
        this.PF_AE_0 = 0;
        this.PF_AE_1 = 1;
        this.PF_AE_2 = 2;
        this.PF_AE_3 = 3;
        this.PF_AE_4 = 4;
        this.PF_AE_5 = 5;
        this.PF_AE_6 = 6;
        this.APP_AE_MINUS_TWO = R.string.permgrouplab_storage;
        this.APP_AE_MINUS_ONE_SEVEN = R.string.permission_request_notification_title;
        this.APP_AE_MINUS_ONE_THREE = R.string.permission_request_notification_with_subtitle;
        this.APP_AE_MINUS_ONE = R.string.permlab_acceptHandover;
        this.APP_AE_MINUS_POINT_SEVEN = R.string.permlab_accessBackgroundLocation;
        this.APP_AE_MINUS_POINT_THREE = R.string.permlab_accessDrmCertificates;
        this.APP_AE_ZERO = R.string.permlab_accessLocationExtraCommands;
        this.APP_AE_POINT_THREE = R.string.permlab_accessNetworkConditions;
        this.APP_AE_POINT_SEVEN = R.string.permlab_accessNetworkState;
        this.APP_AE_ONE = R.string.permlab_accessNotifications;
        this.APP_AE_ONE_THREE = R.string.permlab_accessWifiState;
        this.APP_AE_ONE_SEVEN = R.string.permlab_accessWimaxState;
        this.APP_AE_TWO = R.string.permlab_access_notification_policy;
        setAEShiftDictionary();
    }

    public RotationalSubLcdTextAEShift(Context context) {
        super(context);
        this.PF_AE_MINUS_6 = -6;
        this.PF_AE_MINUS_5 = -5;
        this.PF_AE_MINUS_4 = -4;
        this.PF_AE_MINUS_3 = -3;
        this.PF_AE_MINUS_2 = -2;
        this.PF_AE_MINUS_1 = -1;
        this.PF_AE_0 = 0;
        this.PF_AE_1 = 1;
        this.PF_AE_2 = 2;
        this.PF_AE_3 = 3;
        this.PF_AE_4 = 4;
        this.PF_AE_5 = 5;
        this.PF_AE_6 = 6;
        this.APP_AE_MINUS_TWO = R.string.permgrouplab_storage;
        this.APP_AE_MINUS_ONE_SEVEN = R.string.permission_request_notification_title;
        this.APP_AE_MINUS_ONE_THREE = R.string.permission_request_notification_with_subtitle;
        this.APP_AE_MINUS_ONE = R.string.permlab_acceptHandover;
        this.APP_AE_MINUS_POINT_SEVEN = R.string.permlab_accessBackgroundLocation;
        this.APP_AE_MINUS_POINT_THREE = R.string.permlab_accessDrmCertificates;
        this.APP_AE_ZERO = R.string.permlab_accessLocationExtraCommands;
        this.APP_AE_POINT_THREE = R.string.permlab_accessNetworkConditions;
        this.APP_AE_POINT_SEVEN = R.string.permlab_accessNetworkState;
        this.APP_AE_ONE = R.string.permlab_accessNotifications;
        this.APP_AE_ONE_THREE = R.string.permlab_accessWifiState;
        this.APP_AE_ONE_SEVEN = R.string.permlab_accessWimaxState;
        this.APP_AE_TWO = R.string.permlab_access_notification_policy;
        setAEShiftDictionary();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        int value = ExposureCompensationController.getInstance().getExposureCompensationIndex();
        if (value != 0) {
            return true;
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        int value = ExposureCompensationController.getInstance().getExposureCompensationIndex();
        float step = ExposureCompensationController.getInstance().getExposureCompensationStep();
        if (Math.abs(step - 0.33333f) >= 1.0E-5d) {
            return "";
        }
        int resId = pf2app.get(value);
        if (resId != 0) {
            String ret = getResources().getString(resId);
            return ret;
        }
        Log.w(TAG, "AEShift value is not supported");
        return "";
    }
}
