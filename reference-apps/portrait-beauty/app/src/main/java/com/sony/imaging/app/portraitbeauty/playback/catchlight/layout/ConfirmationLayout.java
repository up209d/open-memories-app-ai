package com.sony.imaging.app.portraitbeauty.playback.catchlight.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.playback.browser.PortraitBeautyCatchLightState;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class ConfirmationLayout extends BaseMenuLayout {
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    TextView mYes = null;
    TextView mSkip = null;
    TextView mFaceSelectionText = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.confirmation_layout);
        this.mFaceSelectionText = (TextView) this.mCurrentView.findViewById(R.id.confirm_text);
        this.mYes = (TextView) this.mCurrentView.findViewById(R.id.ok_yes);
        this.mSkip = (TextView) this.mCurrentView.findViewById(R.id.cancel_text);
        if (this.mFaceSelectionText != null) {
            if (true == PortraitBeautyConstants.FACE_MORE_THAN_ONE) {
                this.mFaceSelectionText.setText(R.string.STRID_FUNC_BEAUTYEFFECT_MSG_COMFIRMRESET);
            } else {
                this.mFaceSelectionText.setText(R.string.STRID_FUNC_SELFIE_CL_ADJUST_CANCEL);
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mYes.setSelected(true);
        this.mSkip.setSelected(false);
        super.onResume();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                toggleSelection();
                return 1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                toggleSelection();
                return 1;
            case 232:
                if (this.mYes.isSelected()) {
                    BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CURRENT_SIZE, Integer.valueOf(CatchLightPlayBackLayout.mGauzeCurlevel));
                    if (true == PortraitBeautyConstants.FACE_MORE_THAN_ONE) {
                        getHandler().sendEmptyMessage(100);
                    } else {
                        getHandler().sendEmptyMessage(101);
                    }
                } else if (this.mSkip.isSelected()) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("cancel_pressed", true);
                    openMenuLayout(PortraitBeautyCatchLightState.ID_ZOOMMODEMENULAYOUT, bundle);
                }
                return 1;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CURRENT_SIZE, Integer.valueOf(CatchLightPlayBackLayout.mGauzeCurlevel));
                return 0;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                return -1;
            case AppRoot.USER_KEYCODE.DIGITAL_ZOOM /* 609 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                return 0;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int result;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int scanCode = event.getScanCode();
        if (scanCode == 0) {
            scanCode = event.getKeyCode();
        }
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                result = -1;
                break;
            default:
                result = -1;
                break;
        }
        AppLog.info(this.TAG, "KeyCode: " + keyCode + "Result: " + result);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return result;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mYes = null;
        this.mSkip = null;
        this.mFaceSelectionText = null;
        this.mCurrentView = null;
        closeLayout();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    private void toggleSelection() {
        if (this.mYes != null && this.mSkip != null) {
            this.mYes.setSelected(!this.mYes.isSelected());
            this.mSkip.setSelected(this.mSkip.isSelected() ? false : true);
        } else if (this.mYes != null) {
            this.mYes.setSelected(true);
        } else if (this.mSkip != null) {
            this.mSkip.setSelected(true);
        }
    }
}
