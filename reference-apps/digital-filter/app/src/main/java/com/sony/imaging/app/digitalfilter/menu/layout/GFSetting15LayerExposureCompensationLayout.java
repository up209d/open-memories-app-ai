package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFExposureCompensationController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFSetting15LayerExposureCompensationLayout extends Fn15LayerAbsLayout {
    public static final String MENU_ID = "ID_GFSETTING15LAYEREXPOSURECOMPENSATIONLAYOUT";
    private static final String STR_ZERO = "0";
    private static ExposureCompensationController mController;
    private static int mMaxIndex;
    private static int mMinIndex;
    private static int mZeroIndex;
    private static boolean isCanceled = false;
    private static int mSetting = 0;
    private static ImageView mRightArrow = null;
    private static ImageView mLeftArrow = null;
    private static TextView mExpComp = null;
    private static ImageView mIcon = null;
    private static int mCurrentIndex = 0;
    private static List<String> mSupport = null;
    private static boolean isLand = false;
    private static boolean isSky = false;
    private static boolean isLayer3 = false;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean isHistgramView = false;
    private static Handler mHandler = null;
    private static Runnable mRunnable = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.menu_setting15_exposure_comp);
        mRightArrow = (ImageView) currentView.findViewById(R.id.right_arrow);
        mLeftArrow = (ImageView) currentView.findViewById(R.id.left_arrow);
        mExpComp = (TextView) currentView.findViewById(R.id.level);
        mIcon = (ImageView) currentView.findViewById(R.id.expcomp_icon);
        mController = GFExposureCompensationController.getInstance();
        mSupport = mController.getSupportedValueInMode(1);
        if (mSupport != null && mSupport.size() != 0) {
            mZeroIndex = mSupport.indexOf("0");
            mMaxIndex = mSupport.size() - 1;
            mMinIndex = 0;
            mCurrentIndex = mController.getExposureCompensationIndex() + mZeroIndex;
        }
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
        GFHistgramTask.getInstance().startHistgramUpdating();
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        isLand = GFCommonUtil.getInstance().isLand();
        isSky = GFCommonUtil.getInstance().isSky();
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        isCanceled = false;
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        if (!BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_EXPCOMP_LINK_MSG, false)) {
            showLinkMsg();
        }
        update();
    }

    private void showLinkMsg() {
        boolean isValidLink = false;
        if (GFLinkAreaController.getInstance().isExpCompLink(mSetting)) {
            if (isLand) {
                if (GFLinkAreaController.getInstance().isExpCompLink(1) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isExpCompLink(2))) {
                    isValidLink = true;
                }
            } else if (isSky) {
                if (GFLinkAreaController.getInstance().isExpCompLink(0) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isExpCompLink(2))) {
                    isValidLink = true;
                }
            } else if (GFLinkAreaController.getInstance().isExpCompLink(0) || GFLinkAreaController.getInstance().isExpCompLink(1)) {
                isValidLink = true;
            }
        }
        if (isValidLink) {
            Bundle bundle = new Bundle();
            bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_EXPCOMP");
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        if (!isCanceled) {
            try {
                String value = mController.getValue("ExposureCompensation");
                params.setExposureComp(mSetting, value);
                String reloadImage = GFLinkUtil.getInstance().saveLinkedExpComp(value, mSetting, isLand, isSky, isLayer3);
                if (reloadImage != null) {
                    CameraNotificationManager.getInstance().requestNotify(reloadImage);
                }
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
            }
        } else {
            mController.setValue("ExposureCompensation", params.getExposureComp(mSetting));
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int guideTitleID = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAND;
        if (isLayer3) {
            guideTitleID = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAYER3;
        } else if (isSky) {
            guideTitleID = R.string.STRID_FUNC_DF_EXPOSURECOMP_SKY;
        }
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(android.R.string.ext_media_unmounting_notification_message));
        guideResources.add(true);
    }

    private void update() {
        if (mCurrentIndex == mMaxIndex) {
            mRightArrow.setVisibility(4);
        } else {
            mRightArrow.setVisibility(0);
        }
        if (mCurrentIndex == mMinIndex) {
            mLeftArrow.setVisibility(4);
        } else {
            mLeftArrow.setVisibility(0);
        }
        mExpComp.setText(getInformationText());
        mExpComp.setVisibility(0);
        mIcon.setVisibility(0);
        disappearInfo();
    }

    private void disappearInfo() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mRunnable);
        }
        if (mRunnable == null) {
            mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerExposureCompensationLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    GFSetting15LayerExposureCompensationLayout.this.setInfoVisibility(4);
                }
            };
        }
        mHandler.postDelayed(mRunnable, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setInfoVisibility(int visibility) {
        mIcon.setVisibility(visibility);
        mLeftArrow.setVisibility(visibility);
        mRightArrow.setVisibility(visibility);
        mExpComp.setVisibility(visibility);
    }

    private String getInformationText() {
        String expComp = getResources().getString(android.R.string.restr_pin_enter_new_pin);
        if (mCurrentIndex != mZeroIndex) {
            float value = mController.calcExposureCompensationValue(mCurrentIndex - mZeroIndex);
            if (value > 0.0f) {
                String expComp2 = String.format(getResources().getString(17041720), Integer.valueOf(((int) value) / 1), Integer.valueOf(((int) (value * 10.0f)) % 10));
                return expComp2;
            }
            float value2 = Math.abs(value);
            String expComp3 = String.format(getResources().getString(17041719), Integer.valueOf(((int) value2) / 1), Integer.valueOf(((int) (value2 * 10.0f)) % 10));
            return expComp3;
        }
        return expComp;
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
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mLeftArrow = null;
        mRightArrow = null;
        mExpComp = null;
        mSupport = null;
        mController = null;
        mIcon = null;
        super.onDestroyView();
        System.gc();
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

    private void valueUp() {
        if (mCurrentIndex < mMaxIndex) {
            mCurrentIndex++;
            mController.incrementExposureCompensation();
        }
        update();
    }

    private void valueDown() {
        if (mCurrentIndex > mMinIndex) {
            mCurrentIndex--;
            mController.decrementExposureCompensation();
        }
        update();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        valueUp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        valueDown();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        valueUp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        valueDown();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        valueUp();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        valueDown();
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
