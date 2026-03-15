package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGAppTopController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGSelfTimerController;
import com.sony.imaging.app.lightgraffiti.shooting.timer.LGSelfTimerThread;
import com.sony.imaging.app.lightgraffiti.shooting.timer.LGTimerThread;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class LGNormalCaptureLayout extends Layout implements LGStateHolder.ValueChangedListener {
    private static final int BEEP_SLEEP_MS = 125;
    private static final int PROGRESS_MAX_05 = 5;
    private static final int PROGRESS_MAX_10 = 10;
    private static final int PROGRESS_MAX_20 = 20;
    private static final int PROGRESS_MAX_30 = 30;
    private View mCurrentView = null;
    private Handler mHandler = new Handler();
    private static final String TAG = LGNormalCaptureLayout.class.getSimpleName();
    private static ImageView mCountdounNumImageView = null;
    private static TextView mGuideTextView = null;
    private static ProgressBar mProgeressBar = null;
    private static View mProgeressBarBase = null;
    private static String mExposingTime = "";
    private static int[] mImageList = null;
    private static BeepPattern[] mBeepList = null;
    private static boolean isPULLINGBACK = false;
    private static final int[] IMAGE_LIST_FOR_05 = {R.drawable.p_16_dd_parts_lightgraffiti_countdown_4_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_3_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_2_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_1_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_0_icon};
    private static final int[] IMAGE_LIST_FOR_10 = {R.drawable.p_16_dd_parts_lightgraffiti_countdown_9_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_8_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_7_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_6_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_5_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_4_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_3_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_2_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_1_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_0_icon};
    private static final int[] IMAGE_LIST_FOR_20 = {R.drawable.p_16_dd_parts_lightgraffiti_countdown_19_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_18_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_17_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_16_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_15_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_14_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_13_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_12_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_11_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_10_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_9_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_8_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_7_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_6_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_5_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_4_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_3_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_2_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_1_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_0_icon};
    private static final int[] IMAGE_LIST_FOR_30 = {R.drawable.p_16_dd_parts_lightgraffiti_countdown_30_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_30_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_29_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_28_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_27_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_26_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_25_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_24_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_23_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_22_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_21_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_20_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_19_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_18_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_17_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_16_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_15_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_14_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_13_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_12_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_11_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_10_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_9_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_8_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_7_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_6_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_5_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_4_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_3_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_2_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_1_icon, R.drawable.p_16_dd_parts_lightgraffiti_countdown_0_icon};
    private static BeepPattern beepPattern_30_21 = new BeepPattern_30_21();
    private static BeepPattern beepPattern_20_11 = new BeepPattern_20_11();
    private static BeepPattern beepPattern_10_02 = new BeepPattern_10_02();
    private static BeepPattern beepPatternSilent = new BeepPatternSilent();
    private static final BeepPattern[] BEEP_LIST_FOR_30 = {beepPatternSilent, beepPattern_30_21, beepPatternSilent, beepPattern_30_21, beepPatternSilent, beepPattern_30_21, beepPatternSilent, beepPattern_30_21, beepPatternSilent, beepPattern_30_21, beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_10_02, beepPatternSilent, beepPattern_10_02, beepPatternSilent, beepPattern_10_02, beepPatternSilent, beepPattern_10_02, beepPatternSilent};
    private static final BeepPattern[] BEEP_LIST_FOR_20 = {beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_20_11, beepPatternSilent, beepPattern_10_02, beepPatternSilent, beepPattern_10_02, beepPatternSilent, beepPattern_10_02, beepPatternSilent, beepPattern_10_02, beepPatternSilent};
    private static final BeepPattern[] BEEP_LIST_FOR_10 = {beepPatternSilent, beepPattern_10_02, beepPatternSilent, beepPattern_10_02, beepPatternSilent, beepPattern_10_02, beepPatternSilent};
    private static final BeepPattern[] BEEP_LIST_FOR_05 = {beepPatternSilent, beepPattern_10_02};
    private static LocalTimerCallback localTimerCallback = new LocalTimerCallback();

    /* loaded from: classes.dex */
    public static abstract class BeepPattern {
        abstract void playBeep();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.lightgraffiti_layout_exposing_guide);
        mCountdounNumImageView = (ImageView) this.mCurrentView.findViewById(R.id.exposing_count_num);
        mGuideTextView = (TextView) this.mCurrentView.findViewById(R.id.exposing_guide);
        mProgeressBar = (ProgressBar) this.mCurrentView.findViewById(R.id.exposing_progress);
        mProgeressBarBase = this.mCurrentView.findViewById(R.id.exposing_progress_base);
        isPULLINGBACK = false;
        LGTimerThread.getInstance().setHandler();
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        try {
            mExposingTime = LGAppTopController.getInstance().getValue(LGAppTopController.DURATION_SELECTION);
        } catch (IController.NotSupportedException e) {
            Log.e(TAG, "NotSupportedException LGAppTopController.getValue()");
            e.printStackTrace();
        }
        if (mExposingTime.equals(LGAppTopController.DURATION_TIME_10)) {
            mCountdounNumImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_countdown_10_icon);
            mImageList = IMAGE_LIST_FOR_10;
            mBeepList = BEEP_LIST_FOR_10;
            mProgeressBar.setMax(10);
        } else if (mExposingTime.equals(LGAppTopController.DURATION_TIME_20)) {
            mCountdounNumImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_countdown_20_icon);
            mImageList = IMAGE_LIST_FOR_20;
            mBeepList = BEEP_LIST_FOR_20;
            mProgeressBar.setMax(20);
        } else if (mExposingTime.equals(LGAppTopController.DURATION_TIME_30)) {
            mCountdounNumImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_countdown_30_icon);
            mImageList = IMAGE_LIST_FOR_30;
            mBeepList = BEEP_LIST_FOR_30;
            mProgeressBar.setMax(30);
            invisibleParts();
        } else {
            mCountdounNumImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_countdown_5_icon);
            mImageList = IMAGE_LIST_FOR_05;
            mBeepList = BEEP_LIST_FOR_05;
            mProgeressBar.setMax(5);
        }
        mProgeressBar.setProgress(0);
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (stage.equals(LGStateHolder.EXPOSING_1ST) || stage.equals(LGStateHolder.EXPOSING_2ND)) {
            if (stage.equals(LGStateHolder.EXPOSING_1ST)) {
                mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_SHOOTING_1ST);
            } else if (stage.equals(LGStateHolder.EXPOSING_2ND)) {
                mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_SHOOTING_2ND);
            }
        } else {
            invisibleParts();
        }
        LGStateHolder.getInstance().setValueChangedListener(this);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        LGTimerThread.getInstance().timerStop();
        LGSelfTimerThread.getInstance().stopSelftimer();
        super.onDestroyView();
        this.mCurrentView = null;
        mCountdounNumImageView = null;
        mGuideTextView = null;
        mProgeressBar = null;
        mProgeressBarBase = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void visibleParts() {
        if (mCountdounNumImageView != null) {
            mCountdounNumImageView.setVisibility(0);
            mGuideTextView.setVisibility(0);
            mProgeressBar.setVisibility(0);
            mProgeressBarBase.setVisibility(0);
            return;
        }
        Log.d(TAG, "Already released!!!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invisibleParts() {
        if (mCountdounNumImageView != null) {
            mCountdounNumImageView.setVisibility(4);
            mGuideTextView.setVisibility(4);
            mProgeressBar.setVisibility(4);
            mProgeressBarBase.setVisibility(4);
            return;
        }
        Log.d(TAG, "Already released!!!");
    }

    /* loaded from: classes.dex */
    public static class LocalTimerCallback implements LGTimerThread.ILGTimerCallback {
        private int mCount = 0;

        @Override // com.sony.imaging.app.lightgraffiti.shooting.timer.LGTimerThread.ILGTimerCallback
        public void onPeriodic(int count) {
            Log.d(LGNormalCaptureLayout.TAG, AppLog.getMethodName());
            this.mCount = count;
            if (!LGNormalCaptureLayout.isPULLINGBACK) {
                if (!LGNormalCaptureLayout.mExposingTime.equals(LGAppTopController.DURATION_TIME_30)) {
                    playBeep();
                    updateParts(false);
                    return;
                }
                switch (this.mCount) {
                    case 0:
                        return;
                    case 1:
                        LGNormalCaptureLayout.visibleParts();
                        playBeep();
                        updateParts(true);
                        return;
                    default:
                        playBeep();
                        updateParts(true);
                        return;
                }
            }
        }

        private void updateParts(boolean isDurationTime30) {
            try {
                if (this.mCount < LGNormalCaptureLayout.mImageList.length) {
                    if (LGNormalCaptureLayout.mCountdounNumImageView != null && LGNormalCaptureLayout.mProgeressBar != null) {
                        LGNormalCaptureLayout.mCountdounNumImageView.setImageResource(LGNormalCaptureLayout.mImageList[this.mCount]);
                        if (isDurationTime30) {
                            LGNormalCaptureLayout.mProgeressBar.setProgress(this.mCount - 1);
                        } else {
                            LGNormalCaptureLayout.mProgeressBar.setProgress(this.mCount + 1);
                        }
                    }
                } else {
                    Log.d(LGNormalCaptureLayout.TAG, AppLog.getMethodName() + "Over run. Count = " + this.mCount + ", mImageList.length=" + LGNormalCaptureLayout.mImageList.length);
                }
            } catch (Exception e) {
                Log.d(LGNormalCaptureLayout.TAG, "        mCountdounNumImageView = " + LGNormalCaptureLayout.mCountdounNumImageView);
                Log.d(LGNormalCaptureLayout.TAG, "        mImageList = " + LGNormalCaptureLayout.mImageList);
                Log.d(LGNormalCaptureLayout.TAG, "        mCount = " + this.mCount);
                e.printStackTrace();
            }
        }

        private void playBeep() {
            if (this.mCount >= LGNormalCaptureLayout.mBeepList.length) {
                if (this.mCount == LGNormalCaptureLayout.mBeepList.length) {
                    LGSelfTimerThread.getInstance().startSelftimer(LGSelfTimerController.SELC_TIMER_EXPOSING_FINISH);
                }
            } else {
                PalyBeepThread palyBeepRunnable = new PalyBeepThread(LGNormalCaptureLayout.mBeepList, this.mCount);
                palyBeepRunnable.start();
            }
        }
    }

    public static LGTimerThread.ILGTimerCallback getLocalTimerCallback() {
        return localTimerCallback;
    }

    /* loaded from: classes.dex */
    public static class BeepPattern_30_21 extends BeepPattern {
        @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGNormalCaptureLayout.BeepPattern
        void playBeep() {
            try {
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                Thread.sleep(125L);
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                Thread.sleep(125L);
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
            } catch (InterruptedException e) {
                Log.e(LGNormalCaptureLayout.TAG, AppLog.getMethodName() + " : InterruptedException");
                e.printStackTrace();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class BeepPattern_20_11 extends BeepPattern {
        @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGNormalCaptureLayout.BeepPattern
        void playBeep() {
            try {
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
                Thread.sleep(125L);
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
            } catch (InterruptedException e) {
                Log.e(LGNormalCaptureLayout.TAG, AppLog.getMethodName() + " : InterruptedException");
                e.printStackTrace();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class BeepPattern_10_02 extends BeepPattern {
        @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGNormalCaptureLayout.BeepPattern
        void playBeep() {
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
        }
    }

    /* loaded from: classes.dex */
    public static class BeepPatternSilent extends BeepPattern {
        @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGNormalCaptureLayout.BeepPattern
        void playBeep() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PalyBeepThread extends Thread {
        BeepPattern[] mBeepList;
        int mCount;

        public PalyBeepThread(BeepPattern[] beepList, int count) {
            this.mBeepList = beepList;
            this.mCount = count;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            this.mBeepList[this.mCount].playBeep();
        }
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder.ValueChangedListener
    public void onValueChanged(String tag) {
        this.mHandler.post(new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.layout.LGNormalCaptureLayout.1
            @Override // java.lang.Runnable
            public void run() {
                String stage = LGStateHolder.getInstance().getShootingStage();
                Log.d(LGNormalCaptureLayout.TAG, "Shooting Stage = " + stage);
                if (stage.equals(LGStateHolder.EXPOSING_1ST) || stage.equals(LGStateHolder.EXPOSING_2ND)) {
                    if (stage.equals(LGStateHolder.EXPOSING_1ST)) {
                        LGNormalCaptureLayout.mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_SHOOTING_1ST);
                        return;
                    } else {
                        if (stage.equals(LGStateHolder.EXPOSING_2ND)) {
                            LGNormalCaptureLayout.mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_EASYGUIDE_CREATIVE_SHOOTING_2ND);
                            return;
                        }
                        return;
                    }
                }
                if (stage.equals(LGStateHolder.SHOOTING_3RD_AFTER_SHOOT) || stage.equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT)) {
                    LGNormalCaptureLayout.this.invisibleParts();
                } else {
                    Log.d(LGNormalCaptureLayout.TAG, "Shooting stage not match!");
                }
            }
        });
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(TAG, "onPause() called and removed ValueChangedListener.");
        LGStateHolder.getInstance().removeValueChangedListener(this);
        LGTimerThread.getInstance().timerStop();
        int runStatus = RunStatus.getStatus();
        if (2 == runStatus) {
            isPULLINGBACK = true;
        }
        LGUtility.getInstance().releaseImageViewDrawable(mCountdounNumImageView);
        mCountdounNumImageView = null;
        super.onPause();
    }
}
