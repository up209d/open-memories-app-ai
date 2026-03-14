package com.sony.imaging.app.photoretouch.playback;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class PhotoRetouchPlay extends PlayRootContainer {
    public static boolean sIsPhotoRetouchAppStarted = false;

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.ContainerState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sIsPhotoRetouchAppStarted = true;
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 9;
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public void onMediaError() {
        ((BaseApp) getActivity()).finish(false);
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                return -1;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                Log.e("", "MOVIE KEY");
                showMovieRecCaution();
                return 1;
            default:
                int result = super.onKeyDown(keyCode, event);
                return result;
        }
    }

    private void showMovieRecCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.photoretouch.playback.PhotoRetouchPlay.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                return -1;
            }
        };
        CautionUtilityClass.getInstance().requestTrigger(131077);
        CautionUtilityClass.getInstance().setDispatchKeyEvent(131077, mKey);
    }
}
