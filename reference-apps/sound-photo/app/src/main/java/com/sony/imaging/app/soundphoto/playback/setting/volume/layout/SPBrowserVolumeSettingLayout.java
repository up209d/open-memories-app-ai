package com.sony.imaging.app.soundphoto.playback.setting.volume.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.playback.menu.layout.IVolumeSettingTransitHandler;
import com.sony.imaging.app.soundphoto.util.AppLog;
import java.util.List;

/* loaded from: classes.dex */
public class SPBrowserVolumeSettingLayout extends BrowserSingleLayout {
    private static final String TAG = "SPBrowserVolumeSettingLayout";
    protected AudioVolumeController mController = AudioVolumeController.getInstance();
    IVolumeSettingTransitHandler mEachPbTrigger;
    protected int mMax;
    protected int mOriginalValue;

    public void setEachPbTrigger(IVolumeSettingTransitHandler trigger) {
        this.mEachPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mController = AudioVolumeController.getInstance();
        List<String> supported = this.mController.getSupportedValue(AudioVolumeController.TAG_AUDIO_VOLUME);
        this.mMax = Integer.parseInt(supported.get(supported.size() - 1));
        this.mOriginalValue = this.mController.getInt(AudioVolumeController.TAG_AUDIO_VOLUME);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mController = null;
        this.mOriginalValue = 0;
        super.onDestroy();
    }

    protected void turnUp() {
        int value = this.mController.getInt(AudioVolumeController.TAG_AUDIO_VOLUME) + 1;
        if (value <= this.mMax) {
            this.mController.setInt(AudioVolumeController.TAG_AUDIO_VOLUME, value);
        }
    }

    protected void turnDown() {
        int value = this.mController.getInt(AudioVolumeController.TAG_AUDIO_VOLUME) - 1;
        if (value >= 0) {
            this.mController.setInt(AudioVolumeController.TAG_AUDIO_VOLUME, value);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return this.mEachPbTrigger.transitionToNextState() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        turnUp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        turnDown();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        turnUp();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        turnDown();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        turnUp();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        turnDown();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        turnDown();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        turnUp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeLayout();
        return this.mEachPbTrigger.transitionToNextState() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mController.setInt(AudioVolumeController.TAG_AUDIO_VOLUME, this.mOriginalValue);
        return this.mEachPbTrigger.transitionToMenuState() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getLayoutResource() {
        return R.layout.pb_volume_setting_layout;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case 103:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.SLIDE_AF_MF /* 596 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            default:
                int returnStatus = super.onKeyDown(keyCode, event);
                return returnStatus;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToLeft(KeyEvent event) {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToRight(KeyEvent event) {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public boolean transitionIndexPb() {
        return false;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_TRANSPARENT;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        AppLog.info(TAG, "onLayoutModeChanged");
    }
}
