package com.sony.imaging.app.synctosmartphone.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;

/* loaded from: classes.dex */
public class RegistrationSettingLayoutSync extends Layout {
    private static final int CHANGE_REGISTER = 2131296315;
    private static final int REGISTER = 2131296273;
    private static final int REGISTRATION_DELETE = 2131296274;
    private static final int REGISTRATION_DEVICE = 2131296277;
    private static final int SMART_PHONE = 2131296278;
    private static final String TAG = RegistrationSettingLayoutSync.class.getSimpleName();
    protected View mCurrentView;
    TextView mDeleteRegist;
    TextView mRegister;
    protected AppNameConstantView mScreenTitleView;
    private boolean flg = false;
    boolean bRegist = true;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.registration_setting_layout_panel);
        }
        createView();
        this.mScreenTitleView = (AppNameConstantView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mRegister = (TextView) this.mCurrentView.findViewById(R.id.button_register_text);
        this.mDeleteRegist = (TextView) this.mCurrentView.findViewById(R.id.jadx_deobf_0x00000514);
        Log.d(TAG, "onCreateView = " + (this.mCurrentView != null));
        return this.mCurrentView;
    }

    private void createView() {
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mScreenTitleView.setVisibility(0);
        TextView registrationDevice = (TextView) getView().findViewById(R.id.registration_device);
        if (registrationDevice != null) {
            registrationDevice.setText(R.string.STRID_FUNC_AUTOSYNC_REGISTERED_DEVICE);
        }
        TextView smartphoneState = (TextView) getView().findViewById(R.id.smartphone_state_text);
        this.bRegist = true;
        if (smartphoneState != null) {
            this.flg = SyncBackUpUtil.getInstance().getRegister(this.flg);
            String smartPhone = this.flg ? SyncBackUpUtil.getInstance().getSmartphone(null) : null;
            if (this.flg) {
                smartphoneState.setText(smartPhone);
            } else {
                smartphoneState.setText(R.string.STRID_FUNC_AUTOSYNC_SMART_PHONE);
            }
            if (this.mRegister != null) {
                if (this.flg) {
                    this.mRegister.setText(R.string.STRID_FUNC_AUTOSYNC_CHANGE_REGISTER);
                } else {
                    this.mRegister.setText(R.string.STRID_FUNC_AUTOSYNC_REGISTER);
                }
                this.mRegister.setSelected(this.bRegist);
            }
            if (this.mDeleteRegist != null) {
                this.mDeleteRegist.setText(R.string.STRID_FUNC_AUTOSYNC_DELETE_REGISTER);
                this.mDeleteRegist.setSelected(this.bRegist ? false : true);
                if (this.flg) {
                    this.mDeleteRegist.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
                } else {
                    this.mDeleteRegist.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_DISABLE));
                }
            }
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mScreenTitleView.setVisibility(4);
        this.flg = false;
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        this.mScreenTitleView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "pushedS1Key");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "pushedS2Key");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "pushedMenuKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.d(TAG, "pushedMovieRecKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, "pushedCenterKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.d(TAG, "pushedUpKey");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.d(TAG, "pushedRightKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.d(TAG, "pushedLeftKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        Log.d(TAG, "turnedDial1ToRight");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        Log.d(TAG, "turnedDial1ToLeft");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        Log.d(TAG, "turnedDial2ToRight");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        Log.d(TAG, "turnedDial2ToLeft");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        Log.d(TAG, "turnedDial3ToRight");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        Log.d(TAG, "turnedDial3ToLeft");
        setSlectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        Log.d(TAG, "pushedDeleteKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Log.d(TAG, "pushedPlayBackKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        Log.d(TAG, "pushedFnKey");
        return 0;
    }

    private void deInitializeView() {
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }

    private void setSlectedButton() {
        if (this.flg) {
            this.bRegist = !this.bRegist;
            this.mRegister.setSelected(this.bRegist);
            this.mDeleteRegist.setSelected(this.bRegist ? false : true);
        }
        Log.d(TAG, "setSelctedButton bRegist = " + this.bRegist);
    }
}
