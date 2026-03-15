package com.sony.imaging.app.timelapse.angleshift.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;

/* loaded from: classes.dex */
public class AngleShiftBootLogoLayout extends Layout {
    private Handler mHandler = null;
    private Runnable mRunnable = null;
    private final int TIME_OUT = 2000;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.as_boot_logo);
        this.mHandler = new Handler();
        this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftBootLogoLayout.1
            @Override // java.lang.Runnable
            public void run() {
                AngleShiftBootLogoLayout.this.closeLayout();
            }
        };
        return currentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mHandler.postDelayed(this.mRunnable, 2000L);
        getView().setFocusable(false);
        getView().setFocusableInTouchMode(false);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                return 0;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            default:
                return 0;
        }
    }

    private void reinitializeAllValues() {
        if (this.mHandler != null && this.mRunnable != null) {
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mHandler = null;
            this.mRunnable = null;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        TLCommonUtil.getInstance().setAngleShiftBoot(true);
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_BOOT_DONE);
        reinitializeAllValues();
        super.onDestroyView();
    }
}
