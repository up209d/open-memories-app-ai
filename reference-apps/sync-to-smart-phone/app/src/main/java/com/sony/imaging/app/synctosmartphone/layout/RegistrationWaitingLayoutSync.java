package com.sony.imaging.app.synctosmartphone.layout;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.WifiParameters;

/* loaded from: classes.dex */
public class RegistrationWaitingLayoutSync extends Layout {
    private static final String TAG = RegistrationWaitingLayoutSync.class.getSimpleName();
    private static AnimationDrawable aDrawable;
    private int bootMode = 0;
    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sony.imaging.app.synctosmartphone.layout.RegistrationWaitingLayoutSync.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            if (RegistrationWaitingLayoutSync.this.bootMode == 1) {
                RegistrationWaitingLayoutSync.aDrawable.start();
            }
        }
    };
    TextView mAutotTansfer;
    protected View mCurrentView;
    TextView mRegistration;
    protected AppNameConstantView mScreenTitleView;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.bootMode = this.data.getInt(ConstantsSync.BOOT_MODE);
        if (this.mCurrentView == null) {
            if (this.bootMode == 1) {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.registration_waiting_layout_panel);
            } else {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.registration_waiting_layout_panel_pairing);
            }
        }
        createView();
        this.mScreenTitleView = (AppNameConstantView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        Log.d(TAG, "onCreateView = " + (this.mCurrentView != null));
        return this.mCurrentView;
    }

    private void createView() {
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        WifiParameters wifiParameters = WifiParameters.getInstance();
        Log.d(TAG, "wifiParameters = " + wifiParameters);
        RelativeLayout r2 = (RelativeLayout) getView().findViewById(R.id.nw_parts_wait_2_txt);
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_1_txt)).setText(getString(R.string.STRID_CMN_SSID));
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_2_txt)).setText(wifiParameters.getSSID());
        RelativeLayout r3 = (RelativeLayout) getView().findViewById(R.id.nw_parts_wait_3_txt);
        ((TextView) r3.findViewById(R.id.nw_parts_wait_special_1_txt)).setText(getString(R.string.STRID_CMN_PASSWORD));
        ((TextView) r3.findViewById(R.id.nw_parts_wait_special_2_txt)).setText(wifiParameters.getPASSWD());
        TextView cancelButton = (TextView) getView().findViewById(R.id.button_cancel_text);
        if (this.bootMode == 1) {
            ImageView progressDot = (ImageView) getView().findViewById(R.id.img_standby);
            aDrawable = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.progress_dot_animation);
            progressDot.setImageDrawable(aDrawable);
            ViewTreeObserver vtObserver = getView().getViewTreeObserver();
            vtObserver.addOnGlobalLayoutListener(this.listener);
            if (cancelButton != null) {
                cancelButton.setText(android.R.string.config_customAdbPublicKeyConfirmationSecondaryUserComponent);
                cancelButton.setSelected(true);
            }
        } else {
            RelativeLayout r4 = (RelativeLayout) getView().findViewById(R.id.nw_parts_wait_4_txt);
            ((TextView) r4.findViewById(R.id.nw_parts_wait_special_1_txt)).setText(getString(R.string.STRID_CMN_WIFI_DEVICENAME_DSLR));
            ((TextView) r4.findViewById(R.id.nw_parts_wait_special_2_txt)).setText(wifiParameters.getDeviceName());
            if (cancelButton != null) {
                cancelButton.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
                cancelButton.setSelected(true);
            }
        }
        this.mScreenTitleView.setVisibility(0);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.bootMode == 1) {
            getView().getViewTreeObserver().removeGlobalOnLayoutListener(this.listener);
            aDrawable.stop();
            aDrawable = null;
        }
        this.mScreenTitleView.setVisibility(4);
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
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey");
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
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        Log.d(TAG, "turnedDial1ToLeft");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        Log.d(TAG, "turnedDial2ToRight");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        Log.d(TAG, "turnedDial2ToLeft");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        Log.d(TAG, "turnedDial3ToRight");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        Log.d(TAG, "turnedDial3ToLeft");
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

    protected int getResumeKeyBeepPattern() {
        return -1;
    }
}
