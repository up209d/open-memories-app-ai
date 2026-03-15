package com.sony.imaging.app.srctrl.shooting.camera;

import com.sony.imaging.app.base.common.TemperatureManager;
import com.sony.imaging.app.base.common.TemperatureNotificationManager;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.definition.ResponseParams;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SRCtrlTemperatureManager {
    private static SRCtrlTemperatureManager mInstance;
    private NotificationListener mTempListener = null;

    public static SRCtrlTemperatureManager getInstance() {
        if (mInstance == null) {
            mInstance = new SRCtrlTemperatureManager();
        }
        return mInstance;
    }

    public void update() {
        boolean update;
        if (TemperatureManager.getInstance().isOverheating()) {
            update = ParamsGenerator.addContinuousErrorParams(ResponseParams.ERROR_STRING_OVERHEATING_WARNING);
        } else {
            update = ParamsGenerator.removeContinuousErrorParams(ResponseParams.ERROR_STRING_OVERHEATING_WARNING);
        }
        if (update) {
            ServerEventHandler.getInstance().onServerStatusChanged();
        }
    }

    public void init() {
        if (this.mTempListener == null) {
            this.mTempListener = new TemperatureChangedListener();
        }
        TemperatureNotificationManager.getInstance().setNotificationListener(this.mTempListener);
        update();
    }

    public void release() {
        if (this.mTempListener != null) {
            TemperatureNotificationManager.getInstance().removeNotificationListener(this.mTempListener);
            this.mTempListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TemperatureChangedListener implements NotificationListener {
        private final String[] tags;

        private TemperatureChangedListener() {
            this.tags = new String[]{TemperatureNotificationManager.TEMP_STATUS_CHANGED, TemperatureNotificationManager.TEMP_COUNTDOWN_INFO_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            SRCtrlTemperatureManager.this.update();
        }
    }
}
