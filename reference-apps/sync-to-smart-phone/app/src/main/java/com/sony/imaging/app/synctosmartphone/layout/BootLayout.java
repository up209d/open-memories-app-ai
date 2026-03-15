package com.sony.imaging.app.synctosmartphone.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;

/* loaded from: classes.dex */
public class BootLayout extends Layout {
    private static final String TAG = BootLayout.class.getSimpleName();
    protected View mCurrentView;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.boot_layout_panel);
        }
        return this.mCurrentView;
    }

    private void createView() {
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume = onResume");
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        super.onDestroyView();
    }

    private void deInitializeView() {
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }
}
