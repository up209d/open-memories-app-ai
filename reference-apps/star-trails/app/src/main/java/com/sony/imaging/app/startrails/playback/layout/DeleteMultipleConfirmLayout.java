package com.sony.imaging.app.startrails.playback.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.database.DBConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class DeleteMultipleConfirmLayout extends EditorConfirmLayoutBase {
    TextView mCancel;
    TextView mOk;

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

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        String text;
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.CENTER, 3);
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
        super.onResume();
        TextView label = (TextView) getView().findViewById(R.id.dialog_message);
        Bundle bundle = this.data;
        if (label != null) {
            String shootMode = (String) bundle.get(DBConstants.COL_SHOOTING_MODE);
            if (shootMode.equalsIgnoreCase("STILL")) {
                text = getResources().getString(android.R.string.description_target_unlock_tablet, Integer.valueOf(ListViewLayout.mSelectedDeleteImageSize));
            } else {
                String path = bundle.getString(DBConstants.COL_FILE_PATH);
                text = getResources().getString(android.R.string.deprecated_target_sdk_message) + "\n" + STUtility.getInstance().getAviFilename(path);
            }
            label.setText(text);
        }
        FooterGuide guide = (FooterGuide) getView().findViewById(getFooterGuideResource());
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 12;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mOk != null && this.mOk.isSelected()) {
            openLayout(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT);
            closeLayout();
            return 1;
        }
        if (this.mCancel != null && this.mCancel.isSelected()) {
            closeLayout();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        closeLayout();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_FOLLOW;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.CENTER, 3);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mOk = null;
        this.mCancel = null;
        setFooterGuideData(null);
    }

    private void toggleSelection() {
        if (this.mOk != null && this.mCancel != null) {
            this.mOk.setSelected(!this.mOk.isSelected());
            this.mCancel.setSelected(this.mCancel.isSelected() ? false : true);
        } else if (this.mOk != null) {
            this.mOk.setSelected(true);
            this.mOk.requestFocus();
        } else if (this.mCancel != null) {
            this.mCancel.setSelected(true);
            this.mCancel.requestFocus();
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        toggleSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        toggleSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        closeLayout();
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.cmn_layout_dialog_string_with_icon_two_button_with_fotter;
    }

    protected EditService getEditService() {
        return DeleteService.getInstance();
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
                case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
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

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.cmn_footer_guide;
    }
}
