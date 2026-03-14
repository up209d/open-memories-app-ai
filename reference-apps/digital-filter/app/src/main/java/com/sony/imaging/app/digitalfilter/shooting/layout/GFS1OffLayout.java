package com.sony.imaging.app.digitalfilter.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.shooting.base.GFDisplayModeObserver;

/* loaded from: classes.dex */
public class GFS1OffLayout extends S1OffLayout {
    Handler myHandler = null;
    Runnable myRunnable = null;
    private static final String TAG = AppLog.getClassName();
    private static ImageView mLeftButton = null;
    private static ImageView mLeftButtonEv = null;
    private static ImageView mRightButton = null;
    private static ImageView mRightButtonIso = null;
    private static ImageView mDeleteButton = null;
    private static ImageView mDeleteButtonSetting = null;
    public static int mPrevS1OffDispMode = -1;

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected int getLayout(int device, int dispmode) {
        return R.layout.gf_shooting_main_skguide_dispon_for_eview_pone;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.gc();
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        mLeftButton = (ImageView) currentView.findViewById(R.id.left_button);
        mLeftButtonEv = (ImageView) currentView.findViewById(R.id.left_button_ev);
        if (ExposureModeController.MANUAL_MODE.equalsIgnoreCase(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE)) && !ISOSensitivityController.ISO_AUTO.equalsIgnoreCase(ISOSensitivityController.getInstance().getValue())) {
            mLeftButtonEv.setImageResource(R.drawable.p_16_dd_parts_hgf_parts_assigned_func_ev_disable);
        } else {
            mLeftButtonEv.setImageResource(R.drawable.p_16_dd_parts_hgf_parts_assigned_func_ev);
        }
        mRightButton = (ImageView) currentView.findViewById(R.id.right_button);
        mRightButtonIso = (ImageView) currentView.findViewById(R.id.right_button_iso);
        mDeleteButton = (ImageView) currentView.findViewById(R.id.delete_button);
        mDeleteButtonSetting = (ImageView) currentView.findViewById(R.id.delete_button_setting);
        int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (mPrevS1OffDispMode != displayMode || 1 == displayMode) {
            setButtonVisibility(0);
            mPrevS1OffDispMode = displayMode;
        }
        if (1 != displayMode) {
            disappearButtonView();
        }
        return currentView;
    }

    private void disappearButtonView() {
        if (this.myHandler == null) {
            this.myHandler = new Handler();
        }
        if (this.myRunnable == null) {
            this.myRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.layout.GFS1OffLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
                    if (1 != displayMode) {
                        GFS1OffLayout.this.setButtonVisibility(4);
                    }
                }
            };
        }
        this.myHandler.postDelayed(this.myRunnable, 3000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setButtonVisibility(int visiblity) {
        mLeftButton.setVisibility(visiblity);
        mLeftButtonEv.setVisibility(visiblity);
        mRightButton.setVisibility(visiblity);
        mRightButtonIso.setVisibility(visiblity);
        mDeleteButton.setVisibility(visiblity);
        mDeleteButtonSetting.setVisibility(visiblity);
    }

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.myHandler != null) {
            this.myHandler.removeCallbacks(this.myRunnable);
            this.myHandler = null;
        }
        this.myRunnable = null;
        mLeftButton = null;
        mLeftButtonEv = null;
        mRightButton = null;
        mRightButtonIso = null;
        mDeleteButton = null;
        mDeleteButtonSetting = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected void createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (this.mMainView != null) {
            if (this.mCurrentLayout != null) {
                this.mCurrentLayout.removeView(this.mMainView);
            }
            this.mMainView = null;
        }
        this.mCurrentLayout = null;
        this.mCurrentLayout = new RelativeLayout(getActivity());
        int viewId = getLayout(device, displayMode);
        if (-1 != viewId) {
            this.mMainView = obtainViewFromPool(viewId);
            this.mCurrentLayout.addView(this.mMainView);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected void detachView() {
        createView();
        mLeftButton = (ImageView) this.mMainView.findViewById(R.id.left_button);
        mLeftButtonEv = (ImageView) this.mMainView.findViewById(R.id.left_button_ev);
        mRightButton = (ImageView) this.mMainView.findViewById(R.id.right_button);
        mRightButtonIso = (ImageView) this.mMainView.findViewById(R.id.right_button_iso);
        mDeleteButton = (ImageView) this.mMainView.findViewById(R.id.delete_button);
        mDeleteButtonSetting = (ImageView) this.mMainView.findViewById(R.id.delete_button_setting);
        int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (1 != displayMode) {
            setButtonVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        return super.pushedS1Key();
    }
}
