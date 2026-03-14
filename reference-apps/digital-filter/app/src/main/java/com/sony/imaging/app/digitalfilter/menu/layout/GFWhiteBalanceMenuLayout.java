package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFWhiteBalanceMenuLayout extends WhiteBalanceMenuLayout {
    public static final String MENU_ID = "ID_GFWHITEBALANCEMENULAYOUT";
    NotificationListener mChangeListener = new ChangeDispNotifier();
    private static final String TAG = AppLog.getClassName();
    private static ViewGroup mCurrentView = null;
    private static ImageView mArrowView = null;
    private static ImageView mAdjustTempArrowRightView = null;
    private static TextView mLightTextView = null;
    private static TextView mCompTextView = null;
    private static String mMenuItemSubGuideText = null;
    private static String mMenuItemOptionGuideText = null;
    private static BorderView mBorderView = null;
    private static TextView mTitle = null;
    private static FooterGuide mFooterGuide = null;
    private static boolean isCanceled = false;
    public static boolean isCTempOpened = false;
    private static boolean isWBLimitChanged = false;
    private static boolean closedByABGM = false;
    private static boolean isLand = false;
    private static boolean isSky = false;
    private static boolean isLayer3 = false;
    private static int mSetting = 0;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange", GFConstants.TAG_GYROSCOPE, GFConstants.WB_LIMIT_CHANGED};

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        mArrowView = (ImageView) mCurrentView.findViewById(R.id.arrow_right);
        mAdjustTempArrowRightView = (ImageView) mCurrentView.findViewById(R.id.temp_arrow_right);
        mLightTextView = (TextView) mCurrentView.findViewById(R.id.light_text);
        mCompTextView = (TextView) mCurrentView.findViewById(R.id.comp_text);
        mTitle = (TextView) mCurrentView.findViewById(R.id.menu_screen_title);
        mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        this.mItemNameView = (TextView) mCurrentView.findViewById(R.id.menu_item_name);
        this.mItemNameView.setGravity(17);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        isCTempOpened = false;
        isWBLimitChanged = false;
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        isCanceled = false;
        closedByABGM = false;
        DisplayModeObserver.getInstance().setNotificationListener(this.mChangeListener);
        CameraNotificationManager.getInstance().setNotificationListener(this.mChangeListener);
        if (GFCommonUtil.getInstance().isTransitionFromWBAdjustment()) {
            openPreviousMenu();
            GFCommonUtil.getInstance().setTransitionFromWBAdjustment(false);
        }
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            GFCommonUtil.getInstance().setMenuTitleText(mTitle, this.mService);
            if (!BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_WB_LINK_MSG, false)) {
                showLinkMsg();
            }
        } else {
            int id = R.string.STRID_FUNC_DF_WB_1ST_AREA_S;
            if (GFEEAreaController.getInstance().isSky()) {
                id = R.string.STRID_FUNC_DF_WB_2ND_AREA_S;
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                id = R.string.STRID_FUNC_DF_WB_3RD_AREA_S;
            }
            mTitle.setText(id);
        }
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemid = this.mService.getSupportedItemList().get(pos);
        String value = this.mService.getMenuItemValue(itemid);
        if (WhiteBalanceController.COLOR_TEMP.equals(value)) {
            WhiteBalanceController.WhiteBalanceParam option = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
            WhiteBalanceController.getInstance().setDetailValue(option);
            String sOption = "" + option.getLightBalance() + "/" + option.getColorComp() + "/" + option.getColorTemp();
            String theme = GFThemeController.getInstance().getValue();
            GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, sOption, mSetting, theme);
            AppLog.info("kamata", "detail:" + sOption);
            if (GFLinkAreaController.getInstance().isWBLink(mSetting)) {
                GFBackUpKey.getInstance().setCommonWBOption(sOption, theme);
            }
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                this.mItemNameView.setText(android.R.string.mmcc_illegal_ms);
            }
        }
        if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            mMenuItemSubGuideText = getResources().getString(android.R.string.ext_media_status_bad_removal);
        } else {
            mMenuItemSubGuideText = getResources().getString(android.R.string.ext_media_status_checking);
        }
        mMenuItemOptionGuideText = getResources().getString(android.R.string.ext_media_status_checking);
        setFooterGuide();
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this.mChangeListener);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mChangeListener);
        mBorderView = null;
        mLightTextView = null;
        mCompTextView = null;
        mArrowView = null;
        mAdjustTempArrowRightView = null;
        mTitle = null;
        mFooterGuide = null;
        this.mItemNameView = null;
        mMenuItemSubGuideText = null;
        mMenuItemOptionGuideText = null;
        mCurrentView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    private void showLinkMsg() {
        boolean isValidLink = false;
        if (GFLinkAreaController.getInstance().isWBLink(mSetting)) {
            if (isLand) {
                if (GFLinkAreaController.getInstance().isWBLink(1) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isWBLink(2))) {
                    isValidLink = true;
                }
            } else if (isSky) {
                if (GFLinkAreaController.getInstance().isWBLink(0) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isWBLink(2))) {
                    isValidLink = true;
                }
            } else if (GFLinkAreaController.getInstance().isWBLink(0) || GFLinkAreaController.getInstance().isWBLink(1)) {
                isValidLink = true;
            }
        }
        if (isValidLink) {
            Bundle bundle = new Bundle();
            bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_WB");
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
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

    private void setFooterGuide() {
        int id = R.string.STRID_FUNC_DF_WB_FOOTER;
        if (this.mLayoutStatus != WhiteBalanceMenuLayout.Layout.SUB) {
            id = android.R.string.autofill_phone_suffix_separator_re;
        }
        mFooterGuide.setData(new FooterGuideDataResId(getActivity(), id, id));
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_wb_sub;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void doItemClickProcessing(String itemid) {
        String value = this.mService.getMenuItemValue(itemid);
        if (GFCommonUtil.getInstance().isLayerSetting()) {
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
        if (!GFCommonUtil.getInstance().isLayerSetting()) {
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
                String theme = GFThemeController.getInstance().getValue();
                GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, sOption, mSetting, theme);
                GFLinkUtil.getInstance().saveLinkedWB(mSetting, isLand, isSky, isLayer3);
            }
            super.pushedLeftKey();
            if (isOptionOfColorTemp && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                this.mItemNameView.setText(android.R.string.mmcc_illegal_ms);
            }
            isCTempOpened = false;
            setFooterGuide();
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
            } else if (!WhiteBalanceController.CUSTOM_SET.equals(value) && GFWhiteBalanceController.getInstance().getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE) != null && GFWhiteBalanceController.getInstance().getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION) != null) {
                String nextMenu = this.mService.getMenuItemNextMenuID(itemId);
                if (nextMenu != null && this.mService.isMenuItemValid(itemId)) {
                    closedByABGM = true;
                }
            }
            int res = super.pushedRightKey();
            setFooterGuide();
            return res;
        }
        WhiteBalanceController wbc = WhiteBalanceController.getInstance();
        if (!WhiteBalanceController.CUSTOM_SET.equals(value) && wbc.getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE) != null && wbc.getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION) != null && WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB) {
            this.mItemNameView.setText(android.R.string.config_ntpServer);
            int res2 = super.pushedRightKey();
            setFooterGuide();
            return res2;
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
        setFooterGuide();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        String itemId;
        if (closedByABGM) {
            super.closeLayout();
            return;
        }
        if (isWBLimitChanged) {
            itemId = GFWhiteBalanceController.getInstance().getValue();
        } else {
            int pos = this.mSpecialScreenView.getSelectedItemPosition();
            String itemId2 = this.mService.getSupportedItemList().get(pos);
            itemId = itemId2;
        }
        String value = this.mService.getMenuItemValue(itemId);
        if (!isCanceled) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            if (!GFWhiteBalanceController.getInstance().isSupportedABGM() && GFCommonUtil.getInstance().isEnableFlash() && ("auto".equalsIgnoreCase(value) || WhiteBalanceController.UNDERWATER_AUTO.equalsIgnoreCase(value))) {
                GFWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.FLASH);
            } else if (!WhiteBalanceController.CUSTOM_SET.equalsIgnoreCase(value)) {
                params.setWBMode(mSetting, WhiteBalanceController.getInstance().getValue());
                if (!GFWhiteBalanceController.SAME.equalsIgnoreCase(value) && WhiteBalanceController.COLOR_TEMP.equalsIgnoreCase(value)) {
                    WhiteBalanceController.WhiteBalanceParam option = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
                    String sOption = "" + option.getLightBalance() + "/" + option.getColorComp() + "/" + option.getColorTemp();
                    String theme = GFThemeController.getInstance().getValue();
                    GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.COLOR_TEMP, sOption, mSetting, theme);
                }
            }
            if (!WhiteBalanceController.CUSTOM_SET.equalsIgnoreCase(value)) {
                GFLinkUtil.getInstance().saveLinkedWB(mSetting, isLand, isSky, isLayer3);
            }
        }
        super.closeLayout();
        isCTempOpened = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void onDeviceStatusChanged() {
        super.onDeviceStatusChanged();
        if (isParentItemAvailable()) {
            String currentItemId = this.mService.getMenuItemId();
            String itemId = this.mService.getCurrentValue(currentItemId);
            String value = this.mService.getMenuItemValue(itemId);
            if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                this.mItemNameView.setText(android.R.string.mmcc_illegal_ms);
            }
        }
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
        if (GFCommonUtil.getInstance().isLayerSetting()) {
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
        if (!GFCommonUtil.getInstance().isLayerSetting() || CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

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
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (!isFunctionGuideShown() && this.mLayoutStatus == WhiteBalanceMenuLayout.Layout.SUB) {
                    openNextMenu(GFWhiteBalanceLimitController.WB_LIMIT, GFWhiteBalanceLimitSetMenuLayout.MENU_ID);
                }
                return 1;
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
        if (GFCommonUtil.getInstance().isLayerSetting() && (code == 232 || code == 595)) {
            return 1;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* loaded from: classes.dex */
    class ChangeDispNotifier implements NotificationListener {
        ChangeDispNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return GFWhiteBalanceMenuLayout.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (GFConstants.WB_LIMIT_CHANGED.equals(tag)) {
                boolean unused = GFWhiteBalanceMenuLayout.isWBLimitChanged = true;
                GFWhiteBalanceMenuLayout.this.openPreviousMenu();
            } else {
                GFWhiteBalanceMenuLayout.mBorderView.invalidate();
            }
        }
    }
}
