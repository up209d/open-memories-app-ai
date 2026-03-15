package com.sony.imaging.app.graduatedfilter.shooting;

import android.util.Log;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class ChangeIso {
    private static String TAG = AppLog.getClassName();
    private Callback mCallback = null;
    private ChangeListener mChangeNotification;

    /* loaded from: classes.dex */
    public interface Callback {
        void cbFunction();
    }

    public ChangeIso() {
        if (this.mChangeNotification == null) {
            this.mChangeNotification = new ChangeListener();
        }
    }

    public void execute(String iso, Callback callBack) {
        try {
            Log.d(TAG, "execute. target iso:" + iso + "  current iso:" + ISOSensitivityController.getInstance().getValue());
            if (iso.equals(ISOSensitivityController.getInstance().getValue())) {
                if (this.mChangeNotification != null) {
                    CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeNotification);
                }
                Log.i(TAG, "no change needed. ");
                callBack.cbFunction();
                return;
            }
            this.mCallback = callBack;
            if (this.mChangeNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeNotification);
            }
            CameraNotificationManager.getInstance().setNotificationListener(this.mChangeNotification);
            Log.d(TAG, "call change.  ");
            ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, iso);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static String getStandardIso() {
        List<Integer> isoList = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).getSupportedISOSensitivities();
        int smallestIso = EVDialDetector.INVALID_EV_CODE;
        int n = isoList.size();
        for (int i = 0; i < n; i++) {
            if (!isThisExpandedISO(isoList.get(i).intValue()) && isoList.get(i).intValue() != 0 && smallestIso >= isoList.get(i).intValue()) {
                smallestIso = isoList.get(i).intValue();
            }
        }
        Log.i(TAG, "Lowest normal ISO(Standard ISO):" + smallestIso);
        return String.valueOf(smallestIso);
    }

    public static boolean isThisExpandedISO(int iso) {
        List<Integer> expIsoList = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).getSupportedExpandedISOSensitivities();
        if (expIsoList != null) {
            int n = expIsoList.size();
            for (int i = 0; i < n; i++) {
                if (expIsoList.get(i).intValue() == iso) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ChangeListener implements NotificationListener {
        private String[] TAGS;

        private ChangeListener() {
            this.TAGS = new String[]{CameraNotificationManager.ISO_SENSITIVITY};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(ChangeIso.TAG, "ChangeListener onNotify");
            if (ChangeIso.this.mChangeNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(ChangeIso.this.mChangeNotification);
            }
            Log.i(ChangeIso.TAG, "current setting:" + ISOSensitivityController.getInstance().getValue());
            ChangeIso.this.mCallback.cbFunction();
        }
    }
}
