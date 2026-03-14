package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.layout.BeltView;
import com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFFn15LayerWhiteBalanceLayout extends Fn15LayerWhiteBalanceLayout {
    public static final String MENU_ID = "ID_GFFN15LAYERWHITEBALANCELAYOUT";
    private static final int TEMP_STEP = 100;
    protected static boolean isCanceled = false;
    protected static int mSetting = 0;
    protected static boolean isLayerSetting = false;
    protected static boolean isLand = false;
    protected static boolean isSky = false;
    protected static boolean isLayer3 = false;
    private static int MIN_TEMP = -1;
    private static int MAX_TEMP = -1;
    private static GFWhiteBalanceController mWBc = null;
    private static WhiteBalanceController.WhiteBalanceParam mWBParam = null;
    private static String mOldWBParam = null;
    private static String mOriginalWBOption = null;
    private static ImageView mBeltLeftArrow = null;
    private static ImageView mBeltRightArrow = null;
    private static ImageView mLeftArrow = null;
    private static ImageView mRightArrow = null;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup currentView = (ViewGroup) obtainViewFromPool(R.layout.menu_fn15layer_whitebalance);
        mBeltLeftArrow = (ImageView) currentView.findViewById(R.id.menu_fn15layer).findViewById(R.id.left_arrow);
        mBeltRightArrow = (ImageView) currentView.findViewById(R.id.menu_fn15layer).findViewById(R.id.right_arrow);
        mLeftArrow = (ImageView) currentView.findViewById(R.id.wb_fn_text_color_left_arrow);
        mRightArrow = (ImageView) currentView.findViewById(R.id.wb_fn_text_color_right_arrow);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        isLand = GFCommonUtil.getInstance().isLand();
        isSky = GFCommonUtil.getInstance().isSky();
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        if (!GFCommonUtil.getInstance().isLayerSetting()) {
            if (GFEEAreaController.getInstance().isLand()) {
                isLand = true;
                isSky = false;
                isLayer3 = false;
                mSetting = 0;
            } else if (GFEEAreaController.getInstance().isSky()) {
                isLand = false;
                isSky = true;
                isLayer3 = false;
                mSetting = 1;
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                isLand = false;
                isSky = false;
                isLayer3 = true;
                mSetting = 2;
            }
        }
        isLayerSetting = GFCommonUtil.getInstance().isLayerSetting();
        mWBc = GFWhiteBalanceController.getInstance();
        mWBParam = getDetailValue();
        MIN_TEMP = mWBc.getMinColorTemoerature();
        MAX_TEMP = mWBc.getMaxColorTemoerature();
        String theme = GFThemeController.getInstance().getValue();
        mOriginalWBOption = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.COLOR_TEMP, mSetting, theme);
        boolean isCTempOnly = GFWhiteBalanceLimitController.CTEMP.equals(GFWhiteBalanceLimitController.getInstance().getValue(GFWhiteBalanceLimitController.WB_LIMIT));
        if (isCTempOnly) {
            mBeltLeftArrow.setVisibility(4);
            mBeltRightArrow.setVisibility(4);
        } else {
            mBeltLeftArrow.setVisibility(0);
            mBeltRightArrow.setVisibility(0);
        }
        super.onResume();
        postSetValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout
    public void onMainBeltItemSelected(BeltView arg0, View arg1, int arg2, long arg3) {
        super.onMainBeltItemSelected(arg0, arg1, arg2, arg3);
        hideUndesirableText();
    }

    private void hideUndesirableText() {
        if (this.mItemTextView.getText().equals(getText(android.R.string.config_ntpServer))) {
            this.mItemTextView.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        super.postSetValue();
        String value = WhiteBalanceController.getInstance().getValue();
        boolean isCTemp = WhiteBalanceController.COLOR_TEMP.equals(value);
        if (isCTemp && isCtempChangeable()) {
            mLeftArrow.setVisibility(0);
            mRightArrow.setVisibility(0);
        } else {
            mLeftArrow.setVisibility(4);
            mRightArrow.setVisibility(4);
        }
        if (isCTemp) {
            WhiteBalanceController.WhiteBalanceParam wbParam = (WhiteBalanceController.WhiteBalanceParam) GFWhiteBalanceController.getInstance().getDetailValueFromPF();
            int cTemp = wbParam.getColorTemp();
            String text = getString(android.R.string.config_ntpServer) + AbstractSupportedChecker.COLON + cTemp + WhiteBalanceController.DISP_TEXT_K;
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                text = getString(android.R.string.mmcc_illegal_ms) + AbstractSupportedChecker.COLON + cTemp + WhiteBalanceController.DISP_TEXT_K;
            }
            this.mItemTextView.setText(text);
        }
        this.mItemTextView.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void setFunctionGuideResources(ArrayList<Object> guideResources) {
        super.setFunctionGuideResources(guideResources);
        String title = (String) guideResources.get(0);
        if (getString(android.R.string.config_ntpServer).equals(title) && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            String title2 = getResources().getString(android.R.string.mmcc_illegal_ms);
            guideResources.set(0, title2);
        }
    }

    private boolean isCtempChangeable() {
        if (!GFCommonUtil.getInstance().isOneDial()) {
            return true;
        }
        boolean isCTempOnly = GFWhiteBalanceLimitController.CTEMP.equals(GFWhiteBalanceLimitController.getInstance().getValue(GFWhiteBalanceLimitController.WB_LIMIT));
        if (isCTempOnly) {
            return true;
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout
    public boolean isKeyHandled(int key) {
        String value = mWBc.getValue();
        if (!WhiteBalanceController.COLOR_TEMP.equals(value)) {
            super.isKeyHandled(key);
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!isCanceled) {
            if (GFCommonUtil.getInstance().isEnableFlash() && !GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                String value = mWBc.getValue();
                if ("auto".equals(value) || WhiteBalanceController.UNDERWATER_AUTO.equals(value)) {
                    GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
                    CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_AWB_WITH_FLASH);
                }
            }
            GFLinkUtil.getInstance().saveLinkedWB(mSetting, isLand, isSky, isLayer3);
        } else {
            String theme = GFThemeController.getInstance().getValue();
            GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, mOriginalWBOption, mSetting, theme);
        }
        mWBParam = null;
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mWBc = null;
        mWBParam = null;
        mOldWBParam = null;
        mOriginalWBOption = null;
        mBeltLeftArrow = null;
        mBeltRightArrow = null;
        mLeftArrow = null;
        mRightArrow = null;
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout
    public int getMinItemListSize() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (!isLayerSetting) {
            return super.pushedFnKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        if (isLayerSetting) {
            return -1;
        }
        return super.pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        if (isLayerSetting) {
            return -1;
        }
        return super.pushedAFMFKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        if (isLayerSetting) {
            return -1;
        }
        return super.turnedEVDial();
    }

    private WhiteBalanceController.WhiteBalanceParam getDetailValue() {
        WhiteBalanceController.WhiteBalanceParam wbParam = (WhiteBalanceController.WhiteBalanceParam) mWBc.getDetailValue();
        String theme = GFThemeController.getInstance().getValue();
        mOldWBParam = GFBackUpKey.getInstance().getWBOption(WhiteBalanceController.COLOR_TEMP, mSetting, theme);
        String[] optArray = mOldWBParam.split("/");
        int light = Integer.parseInt(optArray[0]);
        int comp = Integer.parseInt(optArray[1]);
        int temp = Integer.parseInt(optArray[2]);
        wbParam.setLightBalance(light);
        wbParam.setColorComp(comp);
        wbParam.setColorTemp(temp);
        return wbParam;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            int ret = turnedSubDialNext();
            return ret;
        }
        int ret2 = super.turnedMainDialNext();
        mWBParam = getDetailValue();
        return ret2;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            int ret = turnedSubDialPrev();
            return ret;
        }
        int ret2 = super.turnedMainDialPrev();
        mWBParam = getDetailValue();
        return ret2;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            int ret = turnedSubDialNext();
            return ret;
        }
        int ret2 = super.turnedThirdDialNext();
        mWBParam = getDetailValue();
        return ret2;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            int ret = turnedSubDialPrev();
            return ret;
        }
        int ret2 = super.turnedThirdDialPrev();
        mWBParam = getDetailValue();
        return ret2;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        String value = mWBc.getValue();
        return WhiteBalanceController.COLOR_TEMP.equals(value) ? decrementTemp() : super.turnedSubDialPrev();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        String value = mWBc.getValue();
        return WhiteBalanceController.COLOR_TEMP.equals(value) ? incrementTemp() : super.turnedSubDialNext();
    }

    private int incrementTemp() {
        int temp;
        int temp2 = mWBParam.getColorTemp();
        if (-1 == MAX_TEMP) {
            return -1;
        }
        if (temp2 != MAX_TEMP) {
            temp = temp2 + 100;
        } else {
            temp = MIN_TEMP;
        }
        mWBParam.setColorTemp(temp);
        mWBc.setDetailValue(mWBParam);
        String sOption = "" + mWBParam.getLightBalance() + "/" + mWBParam.getColorComp() + "/" + mWBParam.getColorTemp();
        String theme = GFThemeController.getInstance().getValue();
        GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, sOption, mSetting, theme);
        postSetValue();
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.WB_DETAIL_CHANGE);
        return 1;
    }

    private int decrementTemp() {
        int temp;
        int temp2 = mWBParam.getColorTemp();
        if (-1 == MIN_TEMP) {
            return -1;
        }
        if (temp2 != MIN_TEMP) {
            temp = temp2 - 100;
        } else {
            temp = MAX_TEMP;
        }
        mWBParam.setColorTemp(temp);
        mWBc.setDetailValue(mWBParam);
        String sOption = "" + mWBParam.getLightBalance() + "/" + mWBParam.getColorComp() + "/" + mWBParam.getColorTemp();
        String theme = GFThemeController.getInstance().getValue();
        GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, sOption, mSetting, theme);
        postSetValue();
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.WB_DETAIL_CHANGE);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        if (isLayerSetting) {
            return -1;
        }
        return super.movedAelFocusSlideKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (!isLayerSetting || GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
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
        if (!isLayerSetting || CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }
}
