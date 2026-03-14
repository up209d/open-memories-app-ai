package com.sony.imaging.app.digitalfilter.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFAdjustment15LayerShadingLayout extends ShootingLayout {
    public static final String MENU_ID = "Adjustment15ShadingLayout";
    NotificationListener mListener = new ChangeYUVNotifier();
    private static final String TAG = AppLog.getClassName();
    private static boolean isCanceled = false;
    private static BorderView mBorderView = null;
    private static TextView mShadingLevel = null;
    private static ImageView mUpArrow = null;
    private static ImageView mDownArrow = null;
    private static int mOldLevel = 0;
    private static int mCurrentLevel = 0;
    private static int mCompValue = 0;
    private static final int MAX_VALUE = GFEffectParameters.Parameters.mSAStrength.length - 1;
    private static int mBorder = 0;
    private static Handler mHandler = null;
    private static Runnable mDialRunnable = null;
    private static Handler mHandler2 = null;
    private static Runnable mRunnable = null;

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.menu_adjustment15_shading);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        mShadingLevel = (TextView) currentView.findViewById(R.id.shading_level);
        mUpArrow = (ImageView) currentView.findViewById(R.id.up_arrow);
        mDownArrow = (ImageView) currentView.findViewById(R.id.down_arrow);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        isCanceled = false;
        mBorder = GFCommonUtil.getInstance().getBorderId();
        int strength = GFCommonUtil.getInstance().getStrength(mBorder);
        mOldLevel = strength;
        mCurrentLevel = strength;
        if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            mCompValue = 9;
        }
        updateValue();
        final int action = this.data.getInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION);
        mHandler = new Handler();
        mDialRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustment15LayerShadingLayout.1
            @Override // java.lang.Runnable
            public void run() {
                switch (action) {
                    case 1:
                        GFAdjustment15LayerShadingLayout.this.turnedMainDialNext();
                        return;
                    case 2:
                        GFAdjustment15LayerShadingLayout.this.turnedMainDialPrev();
                        return;
                    case 3:
                        GFAdjustment15LayerShadingLayout.this.turnedSubDialNext();
                        return;
                    case 4:
                        GFAdjustment15LayerShadingLayout.this.turnedSubDialPrev();
                        return;
                    default:
                        return;
                }
            }
        };
        mHandler.post(mDialRunnable);
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        if (mHandler != null) {
            mHandler.removeCallbacks(mDialRunnable);
            mHandler = null;
        }
        mDialRunnable = null;
        if (mHandler2 != null) {
            mHandler2.removeCallbacks(mRunnable);
            mHandler2 = null;
        }
        mRunnable = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (isCanceled) {
            GFCommonUtil.getInstance().setStrength(mOldLevel, mBorder);
            CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mBorderView = null;
        mShadingLevel = null;
        mUpArrow = null;
        mDownArrow = null;
        super.onDestroyView();
        System.gc();
    }

    private void incrementShading() {
        if (mCurrentLevel < MAX_VALUE) {
            mCurrentLevel++;
        }
        updateValue();
    }

    private void decrementShading() {
        if (mCurrentLevel - mCompValue > 0) {
            mCurrentLevel--;
        }
        updateValue();
    }

    private void updateValue() {
        GFCommonUtil.getInstance().setStrength(mCurrentLevel, mBorder);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        int level = (mCurrentLevel - mCompValue) + 1;
        if (level > 0) {
            mShadingLevel.setText(GFCommonUtil.getInstance().getSignedInteger(level));
        } else {
            mShadingLevel.setText("" + level);
        }
        mShadingLevel.setVisibility(0);
        if (level == 1) {
            mUpArrow.setVisibility(0);
            mDownArrow.setVisibility(4);
        } else if (level == (MAX_VALUE - mCompValue) + 1) {
            mUpArrow.setVisibility(4);
            mDownArrow.setVisibility(0);
        } else {
            mUpArrow.setVisibility(0);
            mDownArrow.setVisibility(0);
        }
        mBorderView.invalidate();
        disappearInfo();
    }

    private void disappearInfo() {
        if (mHandler2 == null) {
            mHandler2 = new Handler();
        } else {
            mHandler2.removeCallbacks(mRunnable);
        }
        if (mRunnable == null) {
            mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustment15LayerShadingLayout.2
                @Override // java.lang.Runnable
                public void run() {
                    GFAdjustment15LayerShadingLayout.this.setInfoVisibility(4);
                }
            };
        }
        mHandler2.postDelayed(mRunnable, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setInfoVisibility(int visibility) {
        mShadingLevel.setVisibility(visibility);
        mUpArrow.setVisibility(visibility);
        mDownArrow.setVisibility(visibility);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        incrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        decrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        incrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        decrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        incrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        decrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
            int ret = super.onConvertedKeyDown(event, func);
            return ret;
        }
        if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
            return 0;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

    /* loaded from: classes.dex */
    static class ChangeYUVNotifier implements NotificationListener {
        private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

        ChangeYUVNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            GFAdjustment15LayerShadingLayout.mBorderView.invalidate();
        }
    }
}
