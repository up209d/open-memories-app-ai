package com.sony.imaging.app.portraitbeauty.playback;

import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.adjusteffect.layout.AdjustEffectChangeListener;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class PortraitBeautyAdjustEffectState extends PlayStateBase {
    public static final String ID_ADJUSTEFFECTLAYOUT = "ID_ADJUSTEFFECTLAYOUT";
    public static final String ID_ADJUST_EFFECT_STATE = "ID_ADJUST_EFFECT_STATE";
    public static final String ID_ADJUST_EFF_PREVIEW_LAYOUT = "ID_ADJUST_EFF_PREVIEW_LAYOUT";
    public static final int TRANSIT_TO_SHOOTING_FROM_ADJUSTEFFECT = 109;
    public static OptimizedImage sOptimizedImage = null;
    public static AdjustEffectChangeListener sAdjustEffectChangeListener = null;

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        openLayout(ID_ADJUST_EFF_PREVIEW_LAYOUT);
        openAdjustEffectMenu();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.SPECIAL;
    }

    private void openAdjustEffectMenu() {
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, "AdjustEffect");
        addChildState(ICustomKey.CATEGORY_MENU, bundle);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        closeLayout(ID_ADJUST_EFF_PREVIEW_LAYOUT);
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (sOptimizedImage != null) {
            sOptimizedImage.release();
            sOptimizedImage = null;
        }
        sAdjustEffectChangeListener = null;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        switch (msg.what) {
            case TRANSIT_TO_SHOOTING_FROM_ADJUSTEFFECT /* 109 */:
                PortraitBeautyUtil.bIsAdjustModeGuide = false;
                transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
                break;
        }
        return super.handleMessage(msg);
    }
}
