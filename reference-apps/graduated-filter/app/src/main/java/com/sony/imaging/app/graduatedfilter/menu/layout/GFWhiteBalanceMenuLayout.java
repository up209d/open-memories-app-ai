package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFWhiteBalanceMenuLayout extends WhiteBalanceMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFWHITEBALANCEMENULAYOUT";
    NotificationListener mChangeDispListener = new ChangeDispNotifier();
    private static final String TAG = AppLog.getClassName();
    private static ViewGroup mCurrentView = null;
    private static ImageView mArrowView = null;
    private static ImageView mAdjustTempArrowRightView = null;
    private static TextView mLightTextView = null;
    private static TextView mCompTextView = null;
    private static String mMenuItemSubGuideText = null;
    private static String mMenuItemOptionGuideText = null;
    private static BorderView mBorderView = null;
    private static boolean isBaseSetting = false;
    private static TextView mTitle = null;
    private static boolean isCanceled = false;
    public static boolean isCTempOpened = false;
    private static final String[] TAGS = {GFConstants.CHANGE_BASE_AWB_BY_FLASH, GFConstants.CHANGE_FILTER_AWB_BY_FLASH};

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isBaseSetting = !GFCommonUtil.getInstance().isFilterSetting();
        GFWhiteBalanceController.getInstance().setByFilterMenu(isBaseSetting ? false : true);
        mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        mArrowView = (ImageView) mCurrentView.findViewById(R.id.arrow_right);
        mAdjustTempArrowRightView = (ImageView) mCurrentView.findViewById(R.id.temp_arrow_right);
        mLightTextView = (TextView) mCurrentView.findViewById(R.id.light_text);
        mCompTextView = (TextView) mCurrentView.findViewById(R.id.comp_text);
        mTitle = (TextView) mCurrentView.findViewById(R.id.menu_screen_title);
        this.mItemNameView = (TextView) mCurrentView.findViewById(R.id.menu_item_name);
        this.mItemNameView.setGravity(17);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        isCTempOpened = false;
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        isCanceled = false;
        CameraNotificationManager.getInstance().setNotificationListener(this);
        DisplayModeObserver.getInstance().setNotificationListener(this.mChangeDispListener);
        if (GFCommonUtil.getInstance().isTransitionFromWBAdjustment()) {
            openPreviousMenu();
            GFCommonUtil.getInstance().setTransitionFromWBAdjustment(false);
        }
        if (isBaseSetting) {
            mTitle.setText(R.string.STRID_FUNC_SKYND_WB_EARTH);
        } else {
            mTitle.setText(R.string.STRID_FUNC_SKYND_WB_SKY);
        }
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemid = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemid);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            this.mItemNameView.setText(android.R.string.mmcc_illegal_ms);
        }
        if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            mMenuItemSubGuideText = getResources().getString(android.R.string.ext_media_status_bad_removal);
        } else {
            mMenuItemSubGuideText = getResources().getString(android.R.string.ext_media_status_checking);
        }
        mMenuItemOptionGuideText = getResources().getString(android.R.string.ext_media_status_checking);
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mChangeDispListener);
        mBorderView = null;
        mLightTextView = null;
        mCompTextView = null;
        mArrowView = null;
        mAdjustTempArrowRightView = null;
        mTitle = null;
        this.mItemNameView = null;
        mMenuItemSubGuideText = null;
        mMenuItemOptionGuideText = null;
        mCurrentView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.COLOR_TEMP.equals(value)) {
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                guideResources.add(getResources().getString(android.R.string.mmcc_illegal_ms));
            } else {
                guideResources.add(this.mService.getMenuItemText(itemId));
            }
            if (this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB) {
                guideResources.add(mMenuItemSubGuideText);
                return;
            } else {
                guideResources.add(mMenuItemOptionGuideText);
                return;
            }
        }
        guideResources.add(this.mService.getMenuItemText(itemId));
        guideResources.add(this.mService.getMenuItemGuideText(itemId));
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_wb_sub;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void doItemClickProcessing(String itemid) {
        String value = this.mService.getMenuItemValue(itemid);
        if (GFCommonUtil.getInstance().isFilterSetting()) {
            AppLog.info(TAG, "WB: Filter Setting");
            if (WhiteBalanceController.CUSTOM_SET.equalsIgnoreCase(value) || (!GFWhiteBalanceController.getInstance().isSupportedABGM() && GFCommonUtil.getInstance().isEnableFlash() && ("auto".equalsIgnoreCase(value) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(value)))) {
                super.doItemClickProcessing(itemid);
                return;
            } else {
                openPreviousMenu();
                return;
            }
        }
        AppLog.info(TAG, "WB: Base Setting");
        super.doItemClickProcessing(itemid);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (!GFCommonUtil.getInstance().isFilterSetting()) {
            return super.pushedFnKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM() || this.mLayoutStatus != WhiteBalanceMenuLayout.Layout.SUB) {
            int pos = this.mSpecialScreenView.getSelectedItemPosition();
            String itemId = this.mService.getSupportedItemList().get(pos);
            String value = this.mService.getMenuItemValue(itemId);
            boolean isOptionOfColorTemp = WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.OPTION;
            if (isOptionOfColorTemp) {
                WhiteBalanceController.WhiteBalanceParam option = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
                String sOption = "" + option.getLightBalance() + "/" + option.getColorComp() + "/" + option.getColorTemp();
                GFBackUpKey.getInstance().saveCTempFilterWBOption(sOption, isBaseSetting, GFEffectParameters.Parameters.getEffect());
                GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, sOption, isBaseSetting, GFEffectParameters.Parameters.getEffect());
            }
            super.pushedLeftKey();
            if (isOptionOfColorTemp && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                this.mItemNameView.setText(android.R.string.mmcc_illegal_ms);
            }
            isCTempOpened = false;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB) {
            setBackupOptions();
            isCTempOpened = true;
        }
        if (GFWhiteBalanceController.getInstance().isSupportedABGM() && !GFWhiteBalanceController.SAME.equalsIgnoreCase(value)) {
            if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB) {
                this.mItemNameView.setText(android.R.string.config_ntpServer);
            }
            return super.pushedRightKey();
        }
        WhiteBalanceController wbc = WhiteBalanceController.getInstance();
        if (!WhiteBalanceController.CUSTOM_SET.equals(value) && wbc.getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE) != null && wbc.getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION) != null && WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB) {
            this.mItemNameView.setText(android.R.string.config_ntpServer);
            return super.pushedRightKey();
        }
        return -1;
    }

    private void setBackupOptions() {
        WhiteBalanceController.WhiteBalanceParam options = (WhiteBalanceController.WhiteBalanceParam) GFWhiteBalanceController.getInstance().getDetailValue();
        WhiteBalanceController.getInstance().setDetailValue(options);
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        super.pushedDownKey();
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            this.mItemNameView.setText(android.R.string.mmcc_illegal_ms);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        super.pushedUpKey();
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            this.mItemNameView.setText(android.R.string.mmcc_illegal_ms);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        boolean isOptionOfColorTemp = false;
        isCTempOpened = false;
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.OPTION) {
            isOptionOfColorTemp = true;
        }
        super.pushedMenuKey();
        if (isOptionOfColorTemp && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            this.mItemNameView.setText(android.R.string.mmcc_illegal_ms);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (!isCanceled) {
            if (!GFWhiteBalanceController.getInstance().isSupportedABGM() && GFCommonUtil.getInstance().isEnableFlash() && ("auto".equalsIgnoreCase(value) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(value))) {
                GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
            } else if (!WhiteBalanceController.CUSTOM_SET.equalsIgnoreCase(value)) {
                GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
                params.setWBMode(isBaseSetting, WhiteBalanceController.getInstance().getValue());
                if (!GFWhiteBalanceController.SAME.equalsIgnoreCase(value) && WhiteBalanceController.COLOR_TEMP.equalsIgnoreCase(value)) {
                    WhiteBalanceController.WhiteBalanceParam option = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
                    String sOption = "" + option.getLightBalance() + "/" + option.getColorComp() + "/" + option.getColorTemp();
                    GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, sOption, isBaseSetting, GFEffectParameters.Parameters.getEffect());
                    GFBackUpKey.getInstance().saveCTempFilterWBOption(sOption, isBaseSetting, GFEffectParameters.Parameters.getEffect());
                }
            }
        }
        GFWhiteBalanceController.getInstance().setByFilterMenu(false);
        super.closeLayout();
        isCTempOpened = false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout
    protected void updateText() {
        super.updateText();
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (GFWhiteBalanceController.SAME.equals(value)) {
            mCompTextView.setVisibility(4);
            mLightTextView.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout
    protected void updateRightArrow() {
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            super.updateRightArrow();
            if (GFWhiteBalanceController.SAME.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB) {
                mArrowView.setVisibility(4);
                return;
            }
            return;
        }
        super.updateRightArrow();
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB) {
            mArrowView.setVisibility(0);
        } else {
            mArrowView.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout
    protected void appearCTempAndTempArrow() {
        super.appearCTempAndTempArrow();
        if (!GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            mAdjustTempArrowRightView.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (GFCommonUtil.getInstance().isFilterSetting()) {
            if (CustomizableFunction.Unchanged.equals(func) || CustomizableFunction.Guide.equals(func) || CustomizableFunction.MainPrev.equals(func) || CustomizableFunction.MainNext.equals(func) || CustomizableFunction.SubPrev.equals(func) || CustomizableFunction.SubNext.equals(func) || CustomizableFunction.ThirdPrev.equals(func) || CustomizableFunction.ThirdNext.equals(func) || CustomizableFunction.MfAssist.equals(func)) {
                int ret = super.onConvertedKeyDown(event, func);
                return ret;
            }
            return -1;
        }
        int ret2 = super.onConvertedKeyDown(event, func);
        return ret2;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (GFCommonUtil.getInstance().isFilterSetting() && (CustomizableFunction.AelHold.equals(func) || CustomizableFunction.AfMfHold.equals(func))) {
            return -1;
        }
        return super.onConvertedKeyUp(event, func);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0005. Please report as an issue. */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                if (!isCTempOpened) {
                    isCanceled = true;
                }
                int result = super.onKeyDown(keyCode, event);
                return result;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                return -1;
            default:
                int result2 = super.onKeyDown(keyCode, event);
                return result2;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (GFCommonUtil.getInstance().isFilterSetting() && (code == 232 || code == 595)) {
            return 1;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (GFConstants.CHANGE_BASE_AWB_BY_FLASH.equalsIgnoreCase(tag)) {
            if (isBaseSetting) {
                openPreviousMenu();
            }
        } else if (GFConstants.CHANGE_FILTER_AWB_BY_FLASH.equalsIgnoreCase(tag) && !isBaseSetting) {
            openPreviousMenu();
        }
    }

    /* loaded from: classes.dex */
    static class ChangeDispNotifier implements NotificationListener {
        private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

        ChangeDispNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            GFWhiteBalanceMenuLayout.mBorderView.invalidate();
        }
    }
}
