package com.sony.imaging.app.lightshaft.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.LightShaft;
import com.sony.imaging.app.lightshaft.LightShaftBackUpKey;
import com.sony.imaging.app.lightshaft.LightShaftConstants;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.widget.ShaftView;

/* loaded from: classes.dex */
public class LightShaftStableLayout extends StableLayout {
    private static final String TAG = "LightShaftStableLayout";
    Handler mShaftViewHandler = null;
    ShaftView mShaftView = null;

    public LightShaftStableLayout() {
        this.idleHandler = new ShaftViewDelayHandler();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mShaftView = (ShaftView) this.mCurrentView.findViewById(R.id.light_shaft);
        if (this.mShaftView != null && LightShaft.isEEStateBoot()) {
            LightShaft.setIsEEStateBoot(false);
            this.mShaftView.setVisibility(4);
        }
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        updateShaftParams();
        handleDelayedView();
        EffectSelectController.getInstance().setShootingEnable(true);
        updateExposureCompensationValue();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        handleDelayedView();
    }

    private void updateExposureCompensationValue() {
        if (Boolean.TRUE.booleanValue() == LightShaftConstants.getInstance().isEVDialRotated() && EVDialDetector.hasEVDial() && EVDialDetector.getEVDialPosition() != 100000) {
            LightShaftConstants.getInstance().setEVDialRotated(false);
            ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    public void setUserChanging(int whatUserChanging) {
        super.setUserChanging(whatUserChanging);
        if (2 == whatUserChanging) {
            LightShaftConstants.sLastExposureCompesationIndex = ExposureCompensationController.getInstance().getExposureCompensationIndex();
            ExposureCompensationController.getInstance().setEvDialValue(LightShaftConstants.sLastExposureCompesationIndex);
        }
    }

    public void updateShaftParams() {
        String lastSavedOptionParams = LightShaftBackUpKey.getInstance().getLastSavedOptionSettings();
        AppLog.info(TAG, "INSIDE Shooting Screen Options backup:" + lastSavedOptionParams);
        ShaftsEffect.Parameters params = ShaftsEffect.getInstance().getParameters();
        AppLog.info(TAG, "INSIDE Shooting Screen mParamValue1(before unflatten):" + params.flatten());
        params.unflatten(lastSavedOptionParams);
        AppLog.info(TAG, "INSIDE Shooting Screen mParamValue2(after unflatten):" + params.flatten());
        this.mShaftView.setParameters(params);
        ShaftsEffect.getInstance().setParameters(params);
    }

    private void handleDelayedView() {
        if (this.mShaftView != null && this.mShaftView.getVisibility() == 4) {
            if (this.mShaftViewHandler == null) {
                this.mShaftViewHandler = getHandler();
            }
            Runnable delayedShaftViewRunnable = new Runnable() { // from class: com.sony.imaging.app.lightshaft.shooting.layout.LightShaftStableLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    if (LightShaftStableLayout.this.mShaftView != null) {
                        LightShaftStableLayout.this.mShaftView.setVisibility(0);
                    }
                }
            };
            this.mShaftViewHandler.postDelayed(delayedShaftViewRunnable, 500);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mShaftViewHandler = null;
        this.mShaftView = null;
        this.mCurrentView = null;
        super.onDestroyView();
    }

    /* loaded from: classes.dex */
    protected class ShaftViewDelayHandler extends StableLayout.DelayAttachView {
        protected ShaftViewDelayHandler() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.DelayAttachView, android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            View mainView = LightShaftStableLayout.this.mCurrentMainView = LightShaftStableLayout.this.obtainViewFromPool(LightShaftStableLayout.this.mCurrentMainViewId);
            LightShaftStableLayout.this.mCurrentBaseView.addView(mainView, 1);
            return false;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        boolean isS2CautionShown = false;
        int[] cur = CautionUtilityClass.getInstance().CurrentCautionId();
        for (int i : cur) {
            if (i != 2058) {
                if (i != 65535) {
                    isS2CautionShown = true;
                }
            } else {
                isS2CautionShown = true;
            }
        }
        if (!isS2CautionShown) {
            return super.onDeleteKeyPushed(event);
        }
        CautionUtilityClass.getInstance().executeTerminate();
        return 1;
    }
}
