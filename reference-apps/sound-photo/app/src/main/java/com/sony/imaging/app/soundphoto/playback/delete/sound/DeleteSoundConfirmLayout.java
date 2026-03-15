package com.sony.imaging.app.soundphoto.playback.delete.sound;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class DeleteSoundConfirmLayout extends EditorConfirmLayoutBase {
    TextView mCancel;
    TextView mOk;

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        if (SPUtil.getInstance().isSoundDataDeletePerforming()) {
            return R.layout.cmn_layout_dialog_string_with_icon_two_button_with_fotter;
        }
        return R.layout.pb_layout_deletor_single_confirmation;
    }

    protected EditService getEditService() {
        return DeleteService.getInstance();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.cmn_footer_guide;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        boolean isOk = BackupReader.getDeleteConfirmationDisplay() == BackupReader.ConfirmationCusorPosition.OK;
        this.mOk = (TextView) view.findViewById(R.id.button_upper);
        if (this.mOk != null) {
            this.mOk.setText(android.R.string.permlab_foregroundService);
            this.mOk.setSelected(isOk);
        }
        this.mCancel = (TextView) view.findViewById(R.id.button_lower);
        if (this.mCancel != null) {
            this.mCancel.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
            this.mCancel.setSelected(isOk ? false : true);
        }
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        setFooterGuideData(null);
        this.mOk = null;
        this.mCancel = null;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        TextView label;
        super.onResume();
        setFooterGuideData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        if (SPUtil.getInstance().isSoundDataDeletePerforming() && (label = (TextView) getView().findViewById(R.id.dialog_message)) != null) {
            String text = getResources().getString(R.string.STRID_FUNC_SOUNDPHOTO_CONFIRM_DELETE_SOUND);
            label.setText(text);
        }
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

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        toggleSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        SPUtil.getInstance().setSoundDataDeleteCalled(false);
        return super.pushedPlayBackKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        toggleSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        EditorConfirmLayoutBase.OnConfirmListener listener = this.mOnConfirmListener;
        if (listener != null) {
            if (this.mOk != null && this.mOk.isSelected()) {
                DataBaseOperations.sTotalCount--;
                listener.onOk();
                return 1;
            }
            if (this.mCancel != null && this.mCancel.isSelected()) {
                listener.onCancel();
                return 1;
            }
            return 1;
        }
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
                case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
                case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
                case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
                case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
                case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
                case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                    return result;
                case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                    if (SPUtil.getInstance().isSoundDataDeletePerforming()) {
                        return -1;
                    }
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
}
