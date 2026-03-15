package com.sony.imaging.app.base.playback.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public abstract class PlayLayoutBase extends Layout {
    private static final String MSG_CLEAR_HOLD_KEY = "clear held zoom key";
    private static final String MSG_THIN_ZOOM_KEY = "thinZoomKey";
    private static final String MY_TAG = "PlayLayoutBase";
    protected static final int THINOUT_DURATION = 800;
    protected static Runnable sClearHoldRunnable = new Runnable() { // from class: com.sony.imaging.app.base.playback.layout.PlayLayoutBase.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(PlayLayoutBase.MY_TAG, PlayLayoutBase.MSG_CLEAR_HOLD_KEY);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE, 3);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE, 3);
        }
    };
    protected final String TAG = getClass().getSimpleName();
    protected IPlaybackTriggerFunction mPbTrigger;
    protected View mView;

    protected abstract int getLayoutResource();

    public void setPlaybackTrigger(IPlaybackTriggerFunction trigger) {
        this.mPbTrigger = trigger;
    }

    protected int getFooterGuideResource() {
        return 0;
    }

    public void setFooterGuideData(IFooterGuideData data) {
        FooterGuide guide;
        if (this.mView != null && (guide = (FooterGuide) this.mView.findViewById(getFooterGuideResource())) != null) {
            guide.setData(data);
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mView = obtainViewFromPool(getLayoutResource());
        return this.mView;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mView = null;
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setKeyBeepPattern(getResumeKeyBeepPattern());
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (4 != RunStatus.getStatus()) {
            Log.d(MY_TAG, "clearHeldZoomKey onPause");
            clearHeldZoomKey();
        }
    }

    protected String getLogTag() {
        return this.TAG;
    }

    protected PlayRootContainer getRootContainer() {
        return (PlayRootContainer) getData(PlayRootContainer.PROP_ID_APP_ROOT);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Single";
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightUpKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftUpKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedUmRemoteS1Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedUmRemoteS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedCenterKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedRightKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedLeftKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedUpKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedRightUpKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedRightDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedLeftUpKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedLeftDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedPbZoomFuncMinus() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedPbZoomFuncPlus() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    @Deprecated
    public int pushedDeleteKey() {
        return pushedDeleteFuncKey();
    }

    public boolean transitionBrowser() {
        if (this.mPbTrigger != null) {
            return this.mPbTrigger.transitionBrowser();
        }
        return false;
    }

    public boolean transitionSinglePb() {
        if (this.mPbTrigger != null) {
            return this.mPbTrigger.transitionSinglePb();
        }
        return false;
    }

    public boolean transitionIndexPb() {
        if (this.mPbTrigger != null) {
            return this.mPbTrigger.transitionIndexPb();
        }
        return false;
    }

    public boolean transitionZoom(EventParcel event) {
        if (this.mPbTrigger != null) {
            return this.mPbTrigger.transitionZoom(event);
        }
        return false;
    }

    public boolean transitionDeleteThis() {
        if (this.mPbTrigger != null) {
            return this.mPbTrigger.transitionDeleteThis();
        }
        return false;
    }

    public boolean transitionShooting(EventParcel event) {
        if (this.mPbTrigger != null) {
            return this.mPbTrigger.transitionShooting(event);
        }
        return false;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_BLACK;
    }

    public void thinZoomKey() {
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE);
        if (status != null && status.valid == 1) {
            Log.d(MY_TAG, MSG_THIN_ZOOM_KEY);
            clearHeldZoomKey();
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE, 2);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE, 2);
            getHandler().postDelayed(sClearHoldRunnable, 800L);
        }
    }

    public void clearHeldZoomKey() {
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE);
        if (status != null && status.valid == 1) {
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE, 3);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE, 3);
            getHandler().removeCallbacks(sClearHoldRunnable);
        }
    }
}
