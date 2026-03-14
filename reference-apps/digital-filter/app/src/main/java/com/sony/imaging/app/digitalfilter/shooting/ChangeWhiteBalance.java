package com.sony.imaging.app.digitalfilter.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

/* loaded from: classes.dex */
public class ChangeWhiteBalance {
    private static String TAG = AppLog.getClassName();
    private ChangeListener mChangeNotification;
    private Callback mCallback = null;
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

    public void execute(String targetWb, String targetWbOption, Callback callBack) {
        String currentWb = GFWhiteBalanceController.getInstance().getValue();
        WhiteBalanceController.WhiteBalanceParam detailValue = (WhiteBalanceController.WhiteBalanceParam) GFWhiteBalanceController.getInstance().getDetailValue();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), "target wb:").append(targetWb);
        Log.d(TAG, builder.toString());
        builder.replace(0, builder.length(), "target option:").append(targetWbOption);
        Log.d(TAG, builder.toString());
        builder.replace(0, builder.length(), "").append(detailValue.getLightBalance()).append("/").append(detailValue.getColorComp()).append("/").append(detailValue.getColorTemp());
        String currentWbOption = builder.toString();
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        if (this.mChangeNotification != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeNotification);
        }
        if (targetWb.equals(currentWb)) {
            if (targetWbOption.equals(currentWbOption)) {
                Log.i(TAG, "no change needed. ");
            } else {
                this.mWbOption = targetWbOption;
                setDetailValue();
            }
            callBack.cbFunction();
            return;
        }
        this.mCallback = callBack;
        this.mWbOption = targetWbOption;
        CameraNotificationManager.getInstance().setNotificationListener(this.mChangeNotification);
        GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, targetWb);
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
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), "onNotify:").append(tag);
            Log.d(ChangeWhiteBalance.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
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
