package com.sony.imaging.app.soundphoto.playback.delete.single.executor;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleExecutor;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControl;
import com.sony.imaging.app.soundphoto.playback.delete.single.layout.SPDeleteSingleProcessingLayout;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.DatabaseUtil;

/* loaded from: classes.dex */
public class SPDeleteSingleExecutor extends DeleteSingleExecutor implements IFinishListener {
    @Override // com.sony.imaging.app.base.playback.delete.single.DeleteSingleExecutor, com.sony.imaging.app.base.playback.base.editor.Executor, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        removeDataFromLocalDB();
        if (SPUtil.getInstance().isSoundDataDeletePerforming()) {
            this.mCurrentLayout = getResumeLayout();
            if (PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT.equals(this.mCurrentLayout)) {
                SPDeleteSingleProcessingLayout layout = (SPDeleteSingleProcessingLayout) getLayout(this.mCurrentLayout);
                openPlayLayout(this.mCurrentLayout);
                layout.setSoundPhotoDeleteFinishTrigger(this);
            }
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 1);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 1);
            SPUtil.getInstance().printKikiLog(SPConstants.SOUND_DELETE_KIKILOG_ID);
            updatePosition();
            return;
        }
        super.onResume();
    }

    private void updatePosition() {
        ContentsManager mgr = ContentsManager.getInstance();
        boolean isQueryDone = mgr.isInitialQueryDone();
        if (isQueryDone) {
            if (mgr.isFirst()) {
                mgr.moveToLast();
            } else {
                mgr.moveToPrevious();
            }
        }
    }

    @Override // com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        boolean isSound = SPUtil.getInstance().isSoundDataDeletePerforming();
        if (isSound) {
            SPUtil.getInstance().setSoundDataDeleteCalled(false);
        }
        super.setNextState(name, bundle);
    }

    @Override // com.sony.imaging.app.base.playback.delete.single.DeleteSingleExecutor, com.sony.imaging.app.base.playback.base.editor.Executor, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DatabaseUtil.setIsDatabaseChecked(false);
        DataBaseOperations.getInstance().databaseRecovery();
        CameraNotificationManager.getInstance().requestNotify(ContentsManager.NOTIFICATION_TAG_CURRENT_FILE);
        boolean isSound = SPUtil.getInstance().isSoundDataDeletePerforming();
        if (isSound) {
            if (this.mCurrentLayout != null) {
                SPDeleteSingleProcessingLayout layout = (SPDeleteSingleProcessingLayout) getLayout(this.mCurrentLayout);
                layout.setSoundPhotoDeleteFinishTrigger(null);
            }
            SPUtil.getInstance().setSoundDataDeleteCalled(false);
        }
        super.onPause();
    }

    public void removeDataFromLocalDB() {
        ContentsManager mgr = ContentsManager.getInstance();
        SPConstants.CURRENT_SELECTED_ID_POSITION = mgr.getContentsTotalPosition();
        ContentInfo info = mgr.getContentInfo(mgr.getContentsId());
        String dirName = info.getString("DCF_TBLDirName");
        String fileName = info.getString("DCF_TBLFileName");
        String filePath = SPConstants.ROOT_FOLDER_PATH + dirName + SPConstants.FILE_SEPARATER + fileName;
        if (SPUtil.getInstance().isSoundDataDeletePerforming()) {
            if (extractSoundData(filePath)) {
                DataBaseOperations.getInstance().deleteRowFromDB(filePath);
                return;
            }
            return;
        }
        DataBaseOperations.getInstance().deleteRowFromDB(filePath);
    }

    public boolean extractSoundData(String filePath) {
        AppLog.trace(this.TAG, "deletedAudio filePath: " + filePath);
        boolean result = new AudioTrackControl().deletedAudio(filePath);
        if (result) {
            AppLog.trace(this.TAG, "Delete Sound Data is succeed");
        } else {
            AppLog.trace(this.TAG, "Delete Sound Data is failed");
        }
        return result;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.delete.single.executor.IFinishListener
    public boolean finishTransition() {
        CameraNotificationManager.getInstance().requestNotify(SPConstants.DELETE_IMAGE_DATABASE_UPDATE);
        if (((AppRoot) getActivity()) != null) {
            onEnd(0);
        }
        return false;
    }
}
