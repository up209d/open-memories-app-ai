package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class LGEEGuideEasyLayout extends Layout {
    private static final String TAG = LGEEGuideEasyLayout.class.getSimpleName();
    private static ImageView mGuideBackgroundkgView = null;
    private static TextView mGuideTextView = null;
    private static ImageView mSk1KeyIconView = null;
    private static ImageView mSk1KeyStringView = null;
    private static ImageView mOsdKeyIconView = null;
    private static ImageView mOsdKeyStringView = null;
    private static ImageView mDeleteKeyIconView = null;
    private static ImageView mDeleteKeyStringView = null;
    private static int SAVED_DISPLAY_TIME = 2000;
    protected View mCurrentView = null;
    private Handler handler = new Handler();

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        return this.mCurrentView;
    }

    private void createView() {
        this.mCurrentView = obtainViewFromPool(R.layout.lightgraffiti_layout_ee_guide_easy);
        mGuideBackgroundkgView = (ImageView) this.mCurrentView.findViewById(R.id.easy_guide_background);
        mGuideTextView = (TextView) this.mCurrentView.findViewById(R.id.easy_guide_string);
        mSk1KeyIconView = (ImageView) this.mCurrentView.findViewById(R.id.sk1_key_icon);
        mSk1KeyStringView = (ImageView) this.mCurrentView.findViewById(R.id.sk1_string_icon);
        mOsdKeyIconView = (ImageView) this.mCurrentView.findViewById(R.id.osd_key_icon);
        mOsdKeyStringView = (ImageView) this.mCurrentView.findViewById(R.id.osd_string_icon);
        mDeleteKeyIconView = (ImageView) this.mCurrentView.findViewById(R.id.delete_key_icon);
        mDeleteKeyStringView = (ImageView) this.mCurrentView.findViewById(R.id.delete_string_icon);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        visibleParts();
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (stage.equals(LGStateHolder.SHOOTING_1ST)) {
            mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_THROUGH_1ST);
            setSk1View();
            mOsdKeyIconView.setVisibility(4);
            mOsdKeyStringView.setVisibility(4);
            setDeleteKeyView();
            mDeleteKeyStringView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_sk2_guide_osd_icon);
            LGUtility.getInstance().setAfterProgress(false);
            return;
        }
        if (stage.equals(LGStateHolder.SHOOTING_2ND)) {
            mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_THROUGH_2ND);
            mSk1KeyIconView.setVisibility(4);
            mSk1KeyStringView.setVisibility(4);
            setOsdView();
            setDeleteKeyView();
            mDeleteKeyStringView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_sk2_guide_osd_icon);
            return;
        }
        if (stage.equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT)) {
            mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_THROUGH_3RD);
            setSk1View();
            setOsdView();
            setDeleteKeyView();
            mDeleteKeyStringView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_sk2_guide_osd_icon);
            if (FocusModeController.isFocusShiftByControlWheel()) {
                mOsdKeyIconView.setVisibility(4);
                mOsdKeyStringView.setVisibility(4);
                return;
            }
            return;
        }
        if (stage.equals(LGStateHolder.SHOOTING_3RD_AFTER_SHOOT)) {
            if (LGUtility.getInstance().isAfterProgress()) {
                mGuideTextView.setText(R.string.STRID_image_saved);
                Thread displaySavedIn3rdAfterShootingThread = new DisplaySavedIn3rdAfterShootingThread();
                displaySavedIn3rdAfterShootingThread.start();
                LGUtility.getInstance().setAfterProgress(false);
            } else {
                mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_THROUGH_3RD);
            }
            setSk1View();
            setOsdView();
            setDeleteKeyView();
            mDeleteKeyStringView.setImageResource(R.drawable.p_16_dd_parts_stpm_option_center_key_review_osd_icon);
            if (FocusModeController.isFocusShiftByControlWheel()) {
                mOsdKeyIconView.setVisibility(4);
                mOsdKeyStringView.setVisibility(4);
                return;
            }
            return;
        }
        Log.e(TAG, AppLog.getMethodName() + " Unexpected! stage=" + stage);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (stage.equals(LGStateHolder.SHOOTING_1ST)) {
            mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_THROUGH_1ST);
            setSk1View();
            mOsdKeyIconView.setVisibility(4);
            mOsdKeyStringView.setVisibility(4);
            setDeleteKeyView();
            mDeleteKeyStringView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_sk2_guide_osd_icon);
            LGUtility.getInstance().setAfterProgress(false);
        }
        super.onReopened();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        releaseResources();
        super.onPause();
    }

    private void releaseResources() {
        LGUtility.getInstance().releaseImageViewDrawable(mSk1KeyIconView);
        LGUtility.getInstance().releaseImageViewDrawable(mSk1KeyStringView);
        LGUtility.getInstance().releaseImageViewDrawable(mOsdKeyIconView);
        LGUtility.getInstance().releaseImageViewDrawable(mOsdKeyStringView);
        LGUtility.getInstance().releaseImageViewDrawable(mDeleteKeyIconView);
        LGUtility.getInstance().releaseImageViewDrawable(mDeleteKeyStringView);
        mSk1KeyIconView = null;
        mSk1KeyStringView = null;
        mOsdKeyIconView = null;
        mOsdKeyStringView = null;
        mDeleteKeyIconView = null;
        mDeleteKeyStringView = null;
    }

    private void visibleParts() {
        mGuideBackgroundkgView.setVisibility(0);
        mGuideTextView.setVisibility(0);
        mSk1KeyIconView.setVisibility(0);
        mSk1KeyStringView.setVisibility(0);
        mOsdKeyIconView.setVisibility(0);
        mOsdKeyStringView.setVisibility(0);
        mDeleteKeyIconView.setVisibility(0);
        mDeleteKeyStringView.setVisibility(0);
    }

    private void setSk1View() {
        if (Environment.getVersionOfHW() == 1) {
            mSk1KeyIconView.setImageResource(R.drawable.p_16_dd_parts_key_sk1);
            mSk1KeyStringView.setImageResource(R.drawable.p_16_dd_parts_guide_menu_ey);
        }
    }

    private void setOsdView() {
        mOsdKeyIconView.setImageResource(R.drawable.p_16_dd_parts_ee_option_center_key_osd_icon);
        mOsdKeyStringView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_center_key_back_to_first_osd_icon);
    }

    private void setDeleteKeyView() {
        if (Environment.getVersionOfHW() == 1) {
            mDeleteKeyIconView.setImageResource(R.drawable.p_16_dd_parts_key_sk2);
        } else {
            mDeleteKeyIconView.setImageResource(R.drawable.p_16_dd_parts_key_delete);
        }
    }

    /* loaded from: classes.dex */
    private class RedrawRunnable implements Runnable {
        private RedrawRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            LGEEGuideEasyLayout.mGuideTextView.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RedrawRunnableFor2ndS1OffOn extends RedrawRunnable {
        private RedrawRunnableFor2ndS1OffOn() {
            super();
        }

        @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGEEGuideEasyLayout.RedrawRunnable, java.lang.Runnable
        public void run() {
            super.run();
        }
    }

    /* loaded from: classes.dex */
    private class RedrawRunnableFor3rdAfter extends RedrawRunnable {
        private RedrawRunnableFor3rdAfter() {
            super();
        }

        @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGEEGuideEasyLayout.RedrawRunnable, java.lang.Runnable
        public void run() {
            String stage = LGStateHolder.getInstance().getShootingStage();
            if (!stage.equals(LGStateHolder.SHOOTING_1ST)) {
                LGEEGuideEasyLayout.mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_THROUGH_3RD);
            }
            super.run();
        }
    }

    /* loaded from: classes.dex */
    private class DisplaySavedIn3rdAfterShootingThread extends Thread {
        private DisplaySavedIn3rdAfterShootingThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                Thread.sleep(LGEEGuideEasyLayout.SAVED_DISPLAY_TIME);
                RedrawRunnable redrawRunnable = new RedrawRunnableFor3rdAfter();
                LGEEGuideEasyLayout.this.handler.post(redrawRunnable);
            } catch (InterruptedException e) {
                Log.e(LGEEGuideEasyLayout.TAG, "Thread.sleep() is Exception!");
                e.printStackTrace();
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean handled;
        int scanCode = event.getScanCode();
        if (scanCode == 516) {
            Log.d(TAG, "S1_ON: " + scanCode);
            handled = lgPushedInvalidS1On();
        } else {
            Log.d(TAG, "Other Key: " + scanCode);
            handled = false;
        }
        if (handled) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        boolean handled;
        int scanCode = event.getScanCode();
        if (scanCode == 516) {
            Log.d(TAG, "S1_OFF: " + scanCode);
            handled = lgPushedInvalidS1Off();
        } else {
            Log.d(TAG, "Other Key: " + scanCode);
            handled = false;
        }
        if (handled) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean lgPushedInvalidS1On() {
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (!stage.equals(LGStateHolder.SHOOTING_2ND)) {
            return false;
        }
        mGuideBackgroundkgView.setVisibility(4);
        mGuideTextView.setVisibility(4);
        RedrawRunnable redrawRunnable = new RedrawRunnableFor2ndS1OffOn();
        this.handler.post(redrawRunnable);
        return true;
    }

    public boolean lgPushedInvalidS1Off() {
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (!stage.equals(LGStateHolder.SHOOTING_2ND)) {
            return false;
        }
        mGuideBackgroundkgView.setVisibility(0);
        mGuideTextView.setVisibility(0);
        RedrawRunnable redrawRunnable = new RedrawRunnableFor2ndS1OffOn();
        this.handler.post(redrawRunnable);
        return true;
    }
}
