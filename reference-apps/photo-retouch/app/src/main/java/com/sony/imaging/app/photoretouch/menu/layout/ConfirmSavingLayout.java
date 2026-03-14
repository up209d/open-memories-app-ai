package com.sony.imaging.app.photoretouch.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.ImageEditor;

/* loaded from: classes.dex */
public class ConfirmSavingLayout extends Layout implements ImageEditor.SaveCallback {
    TextView mCancel;
    TextView mOk;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.confirm_saving);
        boolean isOk = BackupReader.getDeleteConfirmationDisplay() == BackupReader.ConfirmationCusorPosition.OK;
        this.mOk = (TextView) currentView.findViewById(R.id.pr_button_upper);
        if (this.mOk != null) {
            this.mOk.setText("OK");
            this.mOk.setSelected(isOk);
        }
        this.mCancel = (TextView) currentView.findViewById(R.id.pr_button_lower);
        if (this.mCancel != null) {
            this.mCancel.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
            this.mCancel.setSelected(isOk ? false : true);
        }
        return currentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setKeyBeepPattern(0);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
        this.mOk = null;
        this.mCancel = null;
    }

    private void toggleSelection() {
        if (this.mOk != null && this.mCancel != null) {
            this.mOk.setSelected(!this.mOk.isSelected());
            this.mCancel.setSelected(this.mCancel.isSelected() ? false : true);
        } else if (this.mOk != null) {
            this.mOk.setSelected(true);
        } else if (this.mCancel != null) {
            this.mCancel.setSelected(true);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                toggleSelection();
                return 1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                closeLayout();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (this.mOk != null && this.mOk.isSelected()) {
                    saveImage();
                } else if (this.mCancel != null && this.mCancel.isSelected()) {
                    getHandler().sendEmptyMessage(Constant.TRANSITION_INDEXPB);
                    closeLayout();
                }
                return 1;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result = super.onConvertedKeyDown(event, func);
        if (result == 0) {
            if (!CustomizableFunction.Unchanged.equals(func)) {
                return -1;
            }
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
                case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
                case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
                case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
                case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
                case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                    return result;
                default:
                    return -1;
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.SaveCallback
    public void onSuccess() {
        ImageEditor.refreshIndexView();
        closeLayout();
        getLayout(Constant.ID_SAVINGLAYOUT).closeLayout();
        getHandler().sendEmptyMessage(Constant.TRANSITION_INDEXPB);
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.SaveCallback
    public void onFail() {
        getLayout(Constant.ID_SAVINGLAYOUT).closeLayout();
        String message = getResources().getString(R.string.STRID_CAU_FILE_MAX_CHANGE_CARD);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ALERT_MESSAGE, message);
        bundle.putBoolean(Constant.ALERT_SWITCHTOINDEXVIEW, true);
        openLayout(Constant.ID_MESSAGEALERT, bundle);
    }

    private void saveImage() {
        openLayout(Constant.ID_SAVINGLAYOUT);
        ImageEditor.saveEditedImage(ImageEditor.getOrientationInfo(), this);
    }
}
