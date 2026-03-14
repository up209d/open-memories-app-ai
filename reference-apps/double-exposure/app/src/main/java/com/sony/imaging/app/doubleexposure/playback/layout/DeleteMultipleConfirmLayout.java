package com.sony.imaging.app.doubleexposure.playback.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;

/* loaded from: classes.dex */
public class DeleteMultipleConfirmLayout extends EditorConfirmLayoutBase {
    private final String TAG = AppLog.getClassName();
    private TextView mTxtVwCancel;
    private TextView mTxtVwOk;

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return R.layout.cmn_layout_dialog_string_with_icon_two_button_with_fotter;
    }

    protected EditService getEditService() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return DeleteService.getInstance();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return R.id.cmn_footer_guide;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        View view = super.onCreateView(inflater, container, savedInstanceState);
        boolean isOk = BackupReader.getDeleteConfirmationDisplay() == BackupReader.ConfirmationCusorPosition.OK;
        this.mTxtVwOk = (TextView) view.findViewById(R.id.button_upper);
        if (this.mTxtVwOk != null) {
            this.mTxtVwOk.setText(android.R.string.permlab_foregroundService);
            this.mTxtVwOk.setSelected(isOk);
        }
        this.mTxtVwCancel = (TextView) view.findViewById(R.id.button_lower);
        if (this.mTxtVwCancel != null) {
            this.mTxtVwCancel.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
            this.mTxtVwCancel.setSelected(isOk ? false : true);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroyView();
        setFooterGuideData(null);
        this.mTxtVwOk = null;
        this.mTxtVwCancel = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        TextView label = (TextView) getView().findViewById(R.id.dialog_message);
        if (label != null) {
            String text = getResources().getString(android.R.string.description_target_unlock_tablet, Integer.valueOf(getEditService().countSelected()));
            label.setText(text);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        toggleSelection();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        toggleSelection();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        EditorConfirmLayoutBase.OnConfirmListener listener = this.mOnConfirmListener;
        if (listener != null) {
            if (this.mTxtVwOk != null && this.mTxtVwOk.isSelected()) {
                listener.onOk();
            } else if (this.mTxtVwCancel != null && this.mTxtVwCancel.isSelected()) {
                listener.onCancel();
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
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

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    private void toggleSelection() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mTxtVwOk != null && this.mTxtVwCancel != null) {
            this.mTxtVwOk.setSelected(!this.mTxtVwOk.isSelected());
            this.mTxtVwCancel.setSelected(this.mTxtVwCancel.isSelected() ? false : true);
        } else if (this.mTxtVwOk != null) {
            this.mTxtVwOk.setSelected(true);
        } else if (this.mTxtVwCancel != null) {
            this.mTxtVwCancel.setSelected(true);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
