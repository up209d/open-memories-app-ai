package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFWhiteBalanceController;

/* loaded from: classes.dex */
public class GFWhiteBalanceAdjustmentMenuLayout extends WhiteBalanceAdjustmentMenuLayout {
    public static final String MENU_ID = "ID_GFWHITEBALANCEADJUSTMENTMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private static boolean isBaseSetting = false;
    private static TextView mTitle = null;
    private static TextView mItemNameView = null;
    public static boolean isLayoutOpened = false;
    private static boolean isCanceled = false;
    private static boolean isMounted = true;

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup currentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        mTitle = (TextView) currentView.findViewById(R.id.menu_screen_title);
        mItemNameView = (TextView) currentView.findViewById(R.id.wb_option_text_mode);
        isBaseSetting = !GFCommonUtil.getInstance().isFilterSetting();
        isCanceled = false;
        isMounted = MediaNotificationManager.getInstance().isMounted();
        setBackupOptions();
        return currentView;
    }

    private void setBackupOptions() {
        WhiteBalanceController.WhiteBalanceParam options = (WhiteBalanceController.WhiteBalanceParam) GFWhiteBalanceController.getInstance().getDetailValue();
        WhiteBalanceController.getInstance().setDetailValue(options);
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isLayoutOpened = true;
        super.onResume();
        if (isBaseSetting) {
            mTitle.setText(R.string.STRID_FUNC_SKYND_WB_ADJUSTMENT_EARTH);
        } else {
            mTitle.setText(R.string.STRID_FUNC_SKYND_WB_ADJUSTMENT_SKY);
        }
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        String itemId = parcelable.getItemId();
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && GFWhiteBalanceController.getInstance().isSupportedABGM()) {
            mItemNameView.setText(android.R.string.mmcc_illegal_ms);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        View view = getView();
        if (view != null && (view instanceof ViewGroup)) {
            ViewGroup layoutView = (ViewGroup) view;
            layoutView.removeView(getFunctionGuideView());
        }
        mTitle = null;
        mItemNameView = null;
        super.onPause();
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (GFCommonUtil.getInstance().isFilterSetting()) {
            GFCommonUtil.getInstance().setTransitionFromWBAdjustment(true);
            openPreviousMenu();
        } else {
            super.doItemClickProcessing(itemid);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (MediaNotificationManager.getInstance().isMounted() != isMounted) {
            isCanceled = true;
        }
        if (!isCanceled) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            WhiteBalanceController.WhiteBalanceParam option = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
            String value = "" + option.getLightBalance() + "/" + option.getColorComp() + "/" + option.getColorTemp();
            GFBackUpKey.getInstance().saveWBOption(WhiteBalanceController.getInstance().getValue(), value, isBaseSetting, GFEffectParameters.Parameters.getEffect());
            params.setWBMode(isBaseSetting, WhiteBalanceController.getInstance().getValue());
            GFBackUpKey.getInstance().saveLastParameters(params.flatten());
        }
        super.closeLayout();
        isLayoutOpened = false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
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

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                if (getFunctionGuideView().isShown()) {
                    int result = super.onKeyDown(keyCode, event);
                    updateView();
                    return result;
                }
                int result2 = super.onKeyDown(keyCode, event);
                return result2;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                return -1;
            default:
                int result3 = super.onKeyDown(keyCode, event);
                return result3;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        if (!getFunctionGuideView().isShown()) {
            super.onLayoutModeChanged(device, displayMode);
        }
    }
}
