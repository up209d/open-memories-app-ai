package com.sony.imaging.app.soundphoto.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.soundphoto.common.caution.SPInfo;
import com.sony.imaging.app.soundphoto.database.DataBaseAdapter;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SPEEState extends EEState {
    private static final String TAG = "SPEEState";
    protected NotificationListener mediaStatusChangedListener = new MediaNotificationListener();

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        MediaNotificationManager.getInstance().setNotificationListener(this.mediaStatusChangedListener);
        displayDBErrorCaution();
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        disapearDisplayingCaution(SPInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
        disapearDisplayingCaution(SPInfo.CAUTION_ID_DLAPP_DB_ERROR);
        MediaNotificationManager.getInstance().removeNotificationListener(this.mediaStatusChangedListener);
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        int cautionId = 0;
        if (518 == msg.what && (cautionId = SPUtil.getInstance().getCautionId()) != 0) {
            AppLog.trace(TAG, "handleMessage() sCautionId" + cautionId);
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
        }
        if (cautionId != 0) {
            return false;
        }
        AppLog.trace(TAG, "handleMessage() sCautionId" + cautionId);
        boolean ret = super.handleMessage(msg);
        return ret;
    }

    /* loaded from: classes.dex */
    private static class MediaNotificationListener implements NotificationListener {
        private final String CAUTION_NOT_EXIST;
        private final String INDEX_NOT_EXIST;
        private String[] tags;

        private MediaNotificationListener() {
            this.CAUTION_NOT_EXIST = "onNotify called and caution CautionUtilityClass.getInstance().getCurrentCautionData() not exist";
            this.INDEX_NOT_EXIST = "onNotify called and caution not exist  CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId index  not in range";
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            DataBaseAdapter.getInstance().setDBCorruptStatus(false);
            int caution_ID = 0;
            try {
                caution_ID = CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId;
            } catch (IndexOutOfBoundsException e) {
                AppLog.info(SPEEState.TAG, "onNotify called and caution not exist  CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId index  not in range");
            } catch (NullPointerException e2) {
                AppLog.info(SPEEState.TAG, "onNotify called and caution CautionUtilityClass.getInstance().getCurrentCautionData() not exist");
            }
            switch (caution_ID) {
                case SPInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING /* 131201 */:
                case SPInfo.CAUTION_ID_DLAPP_DB_ERROR /* 131203 */:
                    int state = MediaNotificationManager.getInstance().getMediaState();
                    if (2 == state) {
                        SPEEState.disapearDisplayingCaution(SPInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
                    }
                    SPEEState.disapearDisplayingCaution(caution_ID);
                    break;
            }
            DatabaseUtil.DbResult result = DataBaseOperations.getInstance().importDatabase();
            if (result == DatabaseUtil.DbResult.DB_ERROR) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                SPEEState.displayDBErrorCaution();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayDBErrorCaution() {
        if (DataBaseAdapter.getInstance().isDBStatusCorrupt()) {
            CautionUtilityClass.getInstance().setDispatchKeyEvent(SPInfo.CAUTION_ID_DLAPP_DB_ERROR, null);
            CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_DLAPP_DB_ERROR);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void disapearDisplayingCaution(int caution_ID) {
        CautionUtilityClass.getInstance().disapperTrigger(caution_ID);
    }
}
