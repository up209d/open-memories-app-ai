package com.sony.imaging.app.synctosmartphone.layout;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;

/* loaded from: classes.dex */
public class ConnectingLayoutSync extends Layout {
    private static final String TAG = ConnectingLayoutSync.class.getSimpleName();
    private static AnimationDrawable aDrawable;
    private int bootMode = 0;
    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sony.imaging.app.synctosmartphone.layout.ConnectingLayoutSync.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            ConnectingLayoutSync.aDrawable.start();
        }
    };
    TextView mAutotTansfer;
    protected View mCurrentView;
    TextView mRegistration;
    protected AppNameConstantView mScreenTitleView;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView = start");
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.connecting_layout_panel);
        }
        this.bootMode = this.data.getInt(ConstantsSync.BOOT_MODE);
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
        ImageView progressDot = (ImageView) getView().findViewById(R.id.img_standby);
        aDrawable = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.progress_dot_animation);
        progressDot.setImageDrawable(aDrawable);
        TextView cancelButton = (TextView) getView().findViewById(R.id.button_cancel_text);
        if (cancelButton != null) {
            if (this.bootMode == 1) {
                cancelButton.setText(android.R.string.config_customAdbPublicKeyConfirmationSecondaryUserComponent);
            } else {
                cancelButton.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
            }
            cancelButton.setSelected(true);
        }
        ViewTreeObserver vtObserver = getView().getViewTreeObserver();
        vtObserver.addOnGlobalLayoutListener(this.listener);
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
        deInitializeView();
        this.mCurrentView = null;
        this.mScreenTitleView = null;
        super.onDestroyView();
    }

    private void deInitializeView() {
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }
}
