package com.sony.imaging.app.srctrl.playback;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.player.MediaPlayerManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.srctrl.playback.contents.PrepareTransferList;
import com.sony.imaging.app.srctrl.streaming.StreamingLoader;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.servlet.ContentsTransfer;
import com.sony.imaging.app.srctrl.webapi.specific.DeletingHandler;
import com.sony.imaging.app.srctrl.webapi.specific.StreamingStatusEventHandler;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class PlayRootContainerEx extends PlayRootContainer {
    public static final String DEFAULT_LAYOUT = "DefaultLayout";
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";
    private static final String TAG = PlayRootContainerEx.class.getName();

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ParamsGenerator.startContentsTransferListener();
        ParamsGenerator.updateCameraFunctionParams(SRCtrlConstants.CAMERA_FUNCTION_CONTENTS_TRANSFER, SRCtrlConstants.CAMERA_FUNCTION_CANDIDATES);
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        stateController.setIsClosingShootingState(false);
        stateController.setAppCondition(StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER);
        ContentsTransfer.getInstance().initialize();
        initializeViewMode();
        PrepareTransferList.getInstance().initState();
        openLayout("DefaultLayout");
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    protected void initializeViewMode() {
        PrepareTransferList.getInstance().setViewMode("neutral");
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout("DefaultLayout");
        ContentsTransfer.getInstance().terminate();
        ParamsGenerator.stopContentsTransferListener();
        StreamingLoader.clean();
        MediaPlayerManager.getInstance().terminate();
        StreamingStatusEventHandler.getInstance().onPauseCalled();
        DeletingHandler.getInstance().onPauseCalled();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (!CustomizableFunction.Unchanged.equals(func)) {
            return 0;
        }
        switch (code) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case 624:
            case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
            case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
            case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
            case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                return -1;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                StateController.getInstance().changeToShootingState();
                return 1;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public void onMediaChanged() {
        initializeViewMode();
        changeApp(PlayRootContainer.ID_BROWSER);
        Log.i(TAG, " *** onMediaChanged");
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public void onMediaError() {
        initializeViewMode();
        changeApp(PlayRootContainer.ID_BROWSER);
        Log.i(TAG, " *** onMediaError");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase
    public int getResumeKeyBeepPattern() {
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
            return 0;
        }
        return super.getResumeKeyBeepPattern();
    }
}
