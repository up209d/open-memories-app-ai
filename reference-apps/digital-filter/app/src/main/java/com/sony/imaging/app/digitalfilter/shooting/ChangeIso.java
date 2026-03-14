package com.sony.imaging.app.digitalfilter.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

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

    public void execute(String targetIso, Callback callBack) {
        try {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), "target iso:").append(targetIso);
            Log.d(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if (this.mChangeNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeNotification);
            }
            if (targetIso.equals(ISOSensitivityController.getInstance().getValue())) {
                Log.i(TAG, "no change needed. ");
                callBack.cbFunction();
            } else {
                this.mCallback = callBack;
                CameraNotificationManager.getInstance().setNotificationListener(this.mChangeNotification);
                ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, targetIso);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
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
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), "onNotify:").append(tag);
            Log.d(ChangeIso.TAG, builder.toString());
            builder.replace(0, builder.length(), "current iso:").append(ISOSensitivityController.getInstance().getValue());
            Log.d(ChangeIso.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if (ChangeIso.this.mChangeNotification != null) {
                CameraNotificationManager.getInstance().removeNotificationListener(ChangeIso.this.mChangeNotification);
            }
            ChangeIso.this.mCallback.cbFunction();
        }
    }
}
