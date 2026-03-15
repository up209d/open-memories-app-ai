package com.sony.imaging.app.soundphoto.playback.state;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.soundphoto.menu.base.layout.SPPBPageMenuLayout;
import com.sony.imaging.app.soundphoto.playback.SPPlayRootContainer;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class SPPlayBackMenuState extends MenuState implements MenuTransitionTrigger {
    public static final String ID_DELETE_SELECTOR = "ID_SPDELETEMENUCONFIRMLAYOUT";
    private static final String TAG = SPPlayBackMenuState.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public void openLayout(String name, Bundle data) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.openLayout(name, data);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public void openLayout(String name) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.openLayout(name);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.MenuState
    public void openMenuLayout(String itemId, String layoutID) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.openMenuLayout(itemId, layoutID);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return ApoWrapper.APO_TYPE.SPECIAL;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SPPBPageMenuLayout layout = (SPPBPageMenuLayout) getLayout(PageMenuLayout.MENU_ID);
        if (layout != null) {
            layout.registerMenuTransitionTrigger(null);
        }
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        SPPBPageMenuLayout layout = (SPPBPageMenuLayout) getLayout(PageMenuLayout.MENU_ID);
        if (layout != null) {
            layout.registerMenuTransitionTrigger(this);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onDestroy();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected void finishMenuState(Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (bundle != null) {
            String nextState = bundle.getString(MenuTable.NEXT_STATE);
            if (SPPlayRootContainer.ID_DELETE_MULTIPLE.equals(nextState)) {
                transitionDeleteMultiple();
            } else if (SPPlayRootContainer.ID_DELETE_SOUND_DATA.equals(nextState)) {
                transitionDeleteSound();
            } else if (PlayRootContainer.ID_BROWSER.equals(nextState)) {
                transitionCancel();
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.soundphoto.playback.state.MenuTransitionTrigger
    public boolean transitionDeleteMultiple() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SPUtil.getInstance().releaseAudioTrackController();
        setNextState(SPPlayRootContainer.ID_DELETE_MULTIPLE, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.state.MenuTransitionTrigger
    public boolean transitionUploadMultiple() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SPUtil.getInstance().releaseAudioTrackController();
        setNextState(SPPlayRootContainer.ID_UPLOAD_MULTIPLE, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.fw.State
    public void removeState() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.removeState();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.soundphoto.playback.state.MenuTransitionTrigger
    public boolean transitionCancel() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setNextState(PlayRootContainer.ID_BROWSER, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.state.MenuTransitionTrigger
    public boolean transitionDeleteSound() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setNextState(PlayRootContainer.ID_BROWSER, null);
        CameraNotificationManager.getInstance().requestNotify(SPPlayRootContainer.ID_DELETE_SOUND_DATA);
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.state.MenuTransitionTrigger
    public boolean transition4KDisplay() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setNextState(SPPlayRootContainer.ID_TRANSIT_4K_DISPLAY, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.state.MenuTransitionTrigger
    public boolean transitionToVolumeSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setNextState(SPPlayRootContainer.ID_VOLUME_SETTING, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }
}
