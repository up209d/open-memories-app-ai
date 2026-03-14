package com.sony.imaging.app.portraitbeauty.shooting.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class AdjustModeEntryLayout extends Layout {
    public static String ADJUST_MODE_ENTRY_LAYOUT = "AdjustModeEntryLayout";
    TextView mSkip;
    TextView mYes;
    private View mCurrentLayout = null;
    private ScrollView mScollView = null;
    private boolean isTouched = false;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentLayout = obtainViewFromPool(R.layout.adjust_mode_entry);
        this.mScollView = (ScrollView) this.mCurrentLayout.findViewById(R.id.scrollView);
        this.mYes = (TextView) this.mCurrentLayout.findViewById(R.id.adjust_yes);
        if (this.mYes != null) {
            this.mYes.setSelected(true);
        }
        this.mSkip = (TextView) this.mCurrentLayout.findViewById(R.id.adjust_skip);
        if (this.mSkip != null) {
            this.mSkip.setSelected(1 == 0);
        }
        return this.mCurrentLayout;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (this.mScollView != null) {
            this.mScollView.scrollTo(0, 0);
        }
        this.mScollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.portraitbeauty.shooting.layout.AdjustModeEntryLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                AdjustModeEntryLayout.this.isTouched = true;
                return true;
            }
        });
        super.onResume();
    }

    private void toggleSelection() {
        if (this.mYes != null && this.mSkip != null) {
            this.mYes.setSelected(!this.mYes.isSelected());
            this.mSkip.setSelected(this.mSkip.isSelected() ? false : true);
        } else if (this.mYes != null) {
            this.mYes.setSelected(true);
            this.mSkip.setSelected(false);
        } else if (this.mSkip != null) {
            this.mSkip.setSelected(true);
            this.mYes.setSelected(false);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        if (!this.isTouched || (scanCode != 106 && scanCode != 105)) {
            return 0;
        }
        toggleSelection();
        this.isTouched = false;
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        PortraitBeautyDisplayModeObserver observer = PortraitBeautyDisplayModeObserver.getInstance();
        switch (keyCode2) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                this.mScollView.smoothScrollBy(0, -20);
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                toggleSelection();
                this.isTouched = false;
                return 0;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                toggleSelection();
                this.isTouched = false;
                return 0;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                this.mScollView.smoothScrollBy(0, 20);
                return 1;
            case 232:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                if (this.mYes != null && true == this.mYes.isSelected()) {
                    observer.setDisplayMode(0, 3);
                    PortraitBeautyUtil.bIsAdjustModeGuide = true;
                } else if (this.mSkip != null && true == this.mSkip.isSelected()) {
                    int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
                    if (device == 0) {
                        String backupDispMode = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode));
                    }
                    if (device == 1) {
                        String backupDispMode2 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_EVF, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode2));
                    }
                    if (device == 2) {
                        String backupDispMode3 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_HDMI, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode3));
                    }
                    PortraitBeautyUtil.bIsAdjustModeGuide = false;
                }
                return 0;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
                return -1;
            case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
                observer.setDisplayMode(0, 3);
                PortraitBeautyUtil.bIsAdjustModeGuide = false;
                return 0;
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
                    FocusModeController focusModeController = FocusModeController.getInstance();
                    focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
                }
                return 0;
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                PortraitBeautyConstants.sISEVDIALTURN = true;
                PortraitBeautyConstants.slastStateExposureCompansasion = 0;
                if (this.mYes != null && true == this.mYes.isSelected()) {
                    observer.setDisplayMode(0, 3);
                    PortraitBeautyUtil.bIsAdjustModeGuide = true;
                } else if (this.mSkip != null && true == this.mSkip.isSelected()) {
                    int device2 = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
                    if (device2 == 0) {
                        String backupDispMode4 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode4));
                    }
                    if (device2 == 1) {
                        String backupDispMode5 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_EVF, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode5));
                    }
                    if (device2 == 2) {
                        String backupDispMode6 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_HDMI, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode6));
                    }
                    PortraitBeautyUtil.bIsAdjustModeGuide = false;
                }
                return 0;
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                if (this.mYes != null && true == this.mYes.isSelected()) {
                    observer.setDisplayMode(0, 3);
                    PortraitBeautyUtil.bIsAdjustModeGuide = true;
                } else if (this.mSkip != null && true == this.mSkip.isSelected()) {
                    int device3 = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
                    if (device3 == 0) {
                        String backupDispMode7 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode7));
                    }
                    if (device3 == 1) {
                        String backupDispMode8 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_EVF, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode8));
                    }
                    if (device3 == 2) {
                        String backupDispMode9 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_HDMI, "1");
                        observer.setDisplayMode(0, Integer.parseInt(backupDispMode9));
                    }
                    PortraitBeautyUtil.bIsAdjustModeGuide = false;
                }
                return 0;
            default:
                return 1;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentLayout = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        PortraitBeautyConstants.sISEVDIALTURN = true;
        PortraitBeautyConstants.slastStateExposureCompansasion = 0;
        return 1;
    }
}
