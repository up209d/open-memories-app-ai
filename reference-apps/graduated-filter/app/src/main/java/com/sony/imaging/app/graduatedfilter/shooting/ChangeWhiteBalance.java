package com.sony.imaging.app.graduatedfilter.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ChangeWhiteBalance {
    private static String TAG = AppLog.getClassName();
    private ChangeListener mChangeNotification;
    private Callback mCallback = null;
    private String mWb = null;
    private String mWbOption = null;

    /* loaded from: classes.dex */
    public interface Callback {
        void cbFunction();
    }

    public ChangeWhiteBalance() {
        if (this.mChangeNotification == null) {
            this.mChangeNotification = new ChangeListener();
        }
    }

    public void execute(String wb, String wbOption, Callback callBack) {
        String cWb = GFWhiteBalanceController.getInstance().getValue();
        WhiteBalanceController.WhiteBalanceParam detailValue = (WhiteBalanceController.WhiteBalanceParam) GFWhiteBalanceController.getInstance().getDetailValue();
        String cWboption = "" + detailValue.getLightBalance() + "/" + detailValue.getColorComp() + "/" + detailValue.getColorTemp();
        Log.d(TAG, "execute. target wb:" + wb + "  current wb:" + cWb);
        Log.d(TAG, "execute. target option:" + wbOption + "  current option:" + cWboption);
        if (wb.equals(cWb) && wbOption.equals(cWboption)) {
            Log.i(TAG, "no change needed. ");
            if (this.mChangeNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeNotification);
            }
            callBack.cbFunction();
            return;
        }
        if (wb.equals(cWb)) {
            Log.i(TAG, "Only option value should be changed. ");
            if (this.mChangeNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeNotification);
            }
            this.mWbOption = wbOption;
            setDetailValue();
            callBack.cbFunction();
            return;
        }
        this.mCallback = callBack;
        this.mWb = wb;
        this.mWbOption = wbOption;
        if (this.mChangeNotification != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeNotification);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this.mChangeNotification);
        if (!wb.equals(cWb)) {
            GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, wb);
        }
        setDetailValue();
    }

    private void setDetailValue() {
        WhiteBalanceController.WhiteBalanceParam options = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        String[] optArray = this.mWbOption.split("/");
        int light = Integer.parseInt(optArray[0]);
        int comp = Integer.parseInt(optArray[1]);
        int temp = Integer.parseInt(optArray[2]);
        options.setLightBalance(light);
        options.setColorComp(comp);
        options.setColorTemp(temp);
        GFWhiteBalanceController.getInstance().setDetailValue(options);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ChangeListener implements NotificationListener {
        private String[] TAGS;

        private ChangeListener() {
            this.TAGS = new String[]{CameraNotificationManager.WB_MODE_CHANGE, CameraNotificationManager.WB_DETAIL_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(ChangeWhiteBalance.TAG, "ChangeListener onNotify: " + tag);
            try {
                removeNotificationListener();
                if (ChangeWhiteBalance.this.mCallback != null) {
                    ChangeWhiteBalance.this.mCallback.cbFunction();
                }
            } catch (Exception e) {
                Log.e(ChangeWhiteBalance.TAG, "catch exception");
            }
        }

        private void removeNotificationListener() {
            if (ChangeWhiteBalance.this.mChangeNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(ChangeWhiteBalance.this.mChangeNotification);
            }
        }
    }
}
