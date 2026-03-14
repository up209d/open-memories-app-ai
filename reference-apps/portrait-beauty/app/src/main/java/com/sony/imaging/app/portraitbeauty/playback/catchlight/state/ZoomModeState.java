package com.sony.imaging.app.portraitbeauty.playback.catchlight.state;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.playback.browser.PortraitBeautyCatchLightState;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.ImageAnalyzer;

/* loaded from: classes.dex */
public class ZoomModeState extends PlayStateBase {
    public static final String ID_CATCHLIGHT_PLAYBACK_LAYOUT = "ID_CATCHLIGHT_PLAYBACK_LAYOUT";
    public static CatchLightEffectChangeListener mChangeListener = null;
    private NotificationListener mMediaNotificationListener = null;
    private ImageAnalyzer.AnalyzedFace[] mFaces = null;

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        if (this.mMediaNotificationListener == null) {
            this.mMediaNotificationListener = new MediaNotificationListener();
        }
        MediaNotificationManager.getInstance().setNotificationListener(this.mMediaNotificationListener);
        Bundle bundle = new Bundle();
        if (this.data != null) {
            bundle.putBoolean("menu_pressed", this.data.getBoolean("menu_pressed"));
        }
        openLayout(ID_CATCHLIGHT_PLAYBACK_LAYOUT, bundle);
        openAdjustZoomModeMenu();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void openAdjustZoomModeMenu() {
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, "ZoomMode");
        if (this.data != null) {
            bundle.putBoolean("menu_pressed", this.data.getBoolean("menu_pressed"));
            bundle.putBoolean("cancel_pressed", this.data.getBoolean("cancel_pressed"));
        }
        addChildState(ICustomKey.CATEGORY_MENU, bundle);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode1, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case 232:
                if (CatchLightPlayBackLayout.bTransitToShooting) {
                    CatchLightPlayBackLayout.bTransitToShooting = false;
                    transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
                } else {
                    setNextState(PortraitBeautyCatchLightState.ID_PREVIEW_AFTER_PB, null);
                }
                return 1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        MediaNotificationManager.getInstance().removeNotificationListener(this.mMediaNotificationListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        closeLayout(ID_CATCHLIGHT_PLAYBACK_LAYOUT);
        CatchLightPlayBackLayout.isHDMIShown = false;
        this.mMediaNotificationListener = null;
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.SPECIAL;
    }

    /* loaded from: classes.dex */
    private class MediaNotificationListener implements NotificationListener {
        private String[] tags;

        private MediaNotificationListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AppLog.enter(ZoomModeState.this.TAG, AppLog.getMethodName());
            int state = MediaNotificationManager.getInstance().getMediaState();
            if (state == 0) {
                ZoomModeState.this.closeLayout(ZoomModeState.ID_CATCHLIGHT_PLAYBACK_LAYOUT);
                ZoomModeState.this.closeLayout(PortraitBeautyCatchLightState.ID_ZOOMMODEMENULAYOUT);
                ZoomModeState.this.getHandler().sendEmptyMessage(101);
            }
            AppLog.exit(ZoomModeState.this.TAG, AppLog.getMethodName());
        }
    }
}
