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
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;

/* loaded from: classes.dex */
public class RegistratingLayoutSync extends Layout {
    private static final String TAG = RegistratingLayoutSync.class.getSimpleName();
    private static AnimationDrawable aDrawable;
    private int bootMode = 0;
    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sony.imaging.app.synctosmartphone.layout.RegistratingLayoutSync.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            RegistratingLayoutSync.aDrawable.start();
        }
    };
    TextView mAutotTansfer;
    protected View mCurrentView;
    protected FooterGuide mFooterGuide;
    TextView mRegistration;
    protected AppNameConstantView mScreenTitleView;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.bootMode = this.data.getInt(ConstantsSync.BOOT_MODE);
        if (this.mCurrentView == null) {
            if (this.bootMode == 1) {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.registrating_layout_trans_now_panel);
            } else {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.registrating_layout_panel);
            }
        }
        createView();
        this.mScreenTitleView = (AppNameConstantView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        Log.d(TAG, "onCreateView = " + (this.mCurrentView != null));
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        Log.d(TAG, "onCreateView = " + (this.mFooterGuide != null));
        return this.mCurrentView;
    }

    private void createView() {
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.bootMode == 1) {
            ImageView progressDot = (ImageView) getView().findViewById(R.id.img_standby);
            aDrawable = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.progress_dot_animation);
            progressDot.setImageDrawable(aDrawable);
            TextView cancelButton = (TextView) getView().findViewById(R.id.button_cancel_text);
            if (cancelButton != null) {
                cancelButton.setText(android.R.string.config_customAdbPublicKeyConfirmationSecondaryUserComponent);
                cancelButton.setSelected(true);
            }
            ViewTreeObserver vtObserver = getView().getViewTreeObserver();
            vtObserver.addOnGlobalLayoutListener(this.listener);
        } else {
            RelativeLayout r1 = (RelativeLayout) getView().findViewById(R.id.nw_parts_standby_1);
            ((TextView) r1.findViewById(R.id.nw_parts_standby_special_2_txt)).setText(getString(R.string.STRID_FUNC_NETWORK_SETTINGS_MSG_WIFI_STANDBY));
            ImageView progressDot2 = (ImageView) r1.findViewById(R.id.nw_parts_standby_special_1_img);
            aDrawable = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.progress_dot_animation);
            progressDot2.setImageDrawable(aDrawable);
            ViewTreeObserver vtObserver2 = getView().getViewTreeObserver();
            vtObserver2.addOnGlobalLayoutListener(this.listener);
            IFooterGuideData data = new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.autofill_save_no, android.R.string.autofill_save_no);
            this.mFooterGuide.setData(data);
        }
        this.mScreenTitleView.setVisibility(0);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getView().getViewTreeObserver().removeGlobalOnLayoutListener(this.listener);
        aDrawable.stop();
        aDrawable = null;
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

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }
}
