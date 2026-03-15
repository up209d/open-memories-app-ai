package com.sony.imaging.app.synctosmartphone.state;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class MenuStateSync extends MenuState {
    private static final String TAG = MenuStateSync.class.getSimpleName();
    private static final String TRANSLATION_MODE = "NormalTransition";
    private int bootMode;
    private boolean doneFinishMenuState = false;

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle data = this.data;
        this.bootMode = data.getInt(ConstantsSync.BOOT_MODE);
        this.doneFinishMenuState = false;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "pushedS1Key");
        finishApp();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "pushedS2Key");
        finishApp();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "pushedMenuKey");
        Bundle data = new Bundle();
        data.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
        data.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
        setNextState(ConstantsSync.MAIN_STATE, data);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.d(TAG, "pushedMovieRecKey");
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected void finishMenuState(Bundle bundle) {
        if (!this.doneFinishMenuState) {
            this.doneFinishMenuState = true;
            Handler handler = getHandler();
            Message msg = Message.obtain(handler);
            String nextState = null;
            if (bundle != null) {
                nextState = bundle.getString(MenuTable.NEXT_STATE);
            } else {
                bundle = new Bundle();
            }
            if (nextState == null || nextState.isEmpty()) {
                bundle.putString(MenuTable.NEXT_STATE, TRANSLATION_MODE);
            }
            bundle.putInt(ConstantsSync.BOOT_MODE, this.bootMode);
            bundle.putInt(ConstantsSync.BOOT_FIRST_TIME, 0);
            msg.obj = bundle;
            handler.sendMessageAtFrontOfQueue(msg);
            BaseMenuService.flushAllRunnable();
            removeState();
        }
        Log.d(TAG, "finishMenuState = ");
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    private void finishApp() {
        BaseApp baseApp = (BaseApp) getActivity();
        baseApp.finish(false);
    }
}
