package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShadingLinkController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFSetting15LayerShadingLayout extends Fn15LayerAbsLayout {
    public static final String MENU_ID = "ID_GFSETTING15LAYERSHADINGLAYOUT";
    private static final String TAG = AppLog.getClassName();
    private static boolean isCanceled = false;
    private static int mSetting = 0;
    private static boolean isLayer3 = false;
    private static BorderView mBorderView = null;
    private static TextView mShadingLevel = null;
    private static ImageView mUpArrow = null;
    private static ImageView mDownArrow = null;
    private static int mOldLevel = 0;
    private static int mCurrentLevel = 0;
    private static int mCompValue = 0;
    private static int mOtherBoundaryId = 0;
    private static int mOtherLevel = 0;
    private static boolean isLayerSetting = false;
    public static boolean isShadingLink = false;
    private static final int MAX_VALUE = GFEffectParameters.Parameters.mSAStrength.length - 1;
    private static int mBorder = 0;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean isHistgramView = false;
    private static Handler mHandler = null;
    private static Runnable mRunnable = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.menu_setting15_shading);
        mBorderView = GFSettingMenuLayout.getBorderView();
        mShadingLevel = (TextView) currentView.findViewById(R.id.shading_level);
        mUpArrow = (ImageView) currentView.findViewById(R.id.up_arrow);
        mDownArrow = (ImageView) currentView.findViewById(R.id.down_arrow);
        mGFGraphViewFilter = (GFGraphView) currentView.findViewById(R.id.filter_histgram);
        mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.base_histgram);
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (!isHistgramView) {
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        isLayerSetting = GFCommonUtil.getInstance().isLayerSetting();
        isShadingLink = GFShadingLinkController.getInstance().isLink() && isLayerSetting;
        if (GFCommonUtil.getInstance().isLayer3()) {
            mOtherBoundaryId = 0;
        } else {
            mOtherBoundaryId = 1;
        }
        if (isShadingLink) {
            mOtherLevel = GFCommonUtil.getInstance().getStrength(mOtherBoundaryId);
        }
        GFHistgramTask.getInstance().startHistgramUpdating();
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        mBorder = 0;
        if (mSetting == 2) {
            mBorder = 1;
        }
        int strength = GFCommonUtil.getInstance().getStrength(mBorder);
        mOldLevel = strength;
        mCurrentLevel = strength;
        if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            mCompValue = 9;
        }
        updateValue();
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
        mRunnable = null;
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        GFHistgramTask.getInstance().stopHistgramUpdating();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CLOSE_15LAYER_BOUNDARY_SETTING);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (isCanceled) {
            GFCommonUtil.getInstance().setStrength(mOldLevel, mBorder);
            if (isShadingLink) {
                GFCommonUtil.getInstance().setStrength(mOtherLevel, mOtherBoundaryId);
            }
            CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        } else if (isShadingLink && isLayer3) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mBorderView = null;
        mShadingLevel = null;
        mUpArrow = null;
        mDownArrow = null;
        isShadingLink = false;
        super.onDestroyView();
        System.gc();
    }

    private void disappearInfo() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mRunnable);
        }
        if (mRunnable == null) {
            mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerShadingLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    GFSetting15LayerShadingLayout.this.setInfoVisibility(4);
                }
            };
        }
        mHandler.postDelayed(mRunnable, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setInfoVisibility(int visibility) {
        mShadingLevel.setVisibility(visibility);
        mUpArrow.setVisibility(visibility);
        mDownArrow.setVisibility(visibility);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int guideTitleID = R.string.STRID_FUNC_DF_GRADATION_SKY;
        int guideDefi = R.string.STRID_FUNC_DF_SKY_SETTINGS_GRADATION;
        if (GFCommonUtil.getInstance().isLayer3()) {
            guideTitleID = R.string.STRID_FUNC_SKYND_GRADATION_LAYER3;
            guideDefi = R.string.STRID_FUNC_SKYND_LAYER3_SETTINGS_GRADATION;
        }
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(true);
    }

    private void incrementShading() {
        if (mCurrentLevel < MAX_VALUE) {
            mCurrentLevel++;
        }
        if (isShadingLink) {
            int otherLevel = GFCommonUtil.getInstance().getStrength(mOtherBoundaryId);
            if (otherLevel < MAX_VALUE) {
                otherLevel++;
            }
            GFCommonUtil.getInstance().setStrength(otherLevel, mOtherBoundaryId);
        }
        updateValue();
    }

    private void decrementShading() {
        if (mCurrentLevel - mCompValue > 0) {
            mCurrentLevel--;
        }
        if (isShadingLink) {
            int otherLevel = GFCommonUtil.getInstance().getStrength(mOtherBoundaryId);
            if (otherLevel > mCompValue) {
                otherLevel--;
            }
            GFCommonUtil.getInstance().setStrength(otherLevel, mOtherBoundaryId);
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

    private void toggleDispMode() {
        if (!isHistgramView) {
            isHistgramView = true;
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        } else {
            isHistgramView = false;
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        }
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        incrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        decrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        incrementShading();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                closeMenuLayout(new Bundle());
                return 0;
            default:
                int ret = super.onKeyDown(keyCode, event);
                return ret;
        }
    }
}
