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
public class RegisteredLayout extends Layout {
    private static final String TAG = RegisteredLayout.class.getSimpleName();
    protected View mCurrentView;
    protected AppNameConstantView mScreenTitleView;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.id6_dialog_layout_panel);
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
        TextView okButton = (TextView) getView().findViewById(R.id.button_lower);
        if (okButton != null) {
            okButton.setSelected(true);
        }
        String smartPhone = SyncBackUpUtil.getInstance().getSmartphone(null);
        TextView registeredDevice = (TextView) this.mCurrentView.findViewById(R.id.text_registered_device);
        if (registeredDevice != null) {
            registeredDevice.setText(getString(R.string.STRID_FUNC_AUTOSYNC_REGISTERED_DEVICE_COLON) + smartPhone);
        }
        this.mScreenTitleView.setVisibility(0);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        this.mScreenTitleView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
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
}
