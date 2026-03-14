package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.widget.EachImageView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;

/* loaded from: classes.dex */
public class GFFilterSetMenuLayout extends SpecialScreenMenuLayout {
    public static final String MENU_ID = "ID_GFFILTERSETMENULAYOUT";
    private TextView mItemGuideView = null;
    private EachImageView mItemImageView = null;
    private static String mOrigAddon = null;
    private static boolean isLayerSetting = false;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mItemGuideView = (TextView) currentView.findViewById(R.id.guide_view);
        this.mItemImageView = (EachImageView) currentView.findViewById(R.id.guide_image);
        isLayerSetting = GFCommonUtil.getInstance().isLayerSetting();
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        mOrigAddon = getValueFromPosition();
        update();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mItemGuideView = null;
        this.mItemImageView = null;
        mOrigAddon = null;
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    private void update() {
        this.mItemGuideView.setText(getGuideText());
        this.mItemImageView.setImageResource(getGuideImage());
    }

    private int getGuideImage() {
        int position = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = (String) this.mSpecialScreenView.getItemAtPosition(position);
        if (!itemId.equals(GFFilterSetController.THREE_AREAS)) {
            return R.drawable.p_16_dd_parts_skyhdr_2area_bg;
        }
        return R.drawable.p_16_dd_parts_skyhdr_3area_bg;
    }

    private String getGuideText() {
        int position = this.mSpecialScreenView.getSelectedItemPosition();
        String itemid = (String) this.mSpecialScreenView.getItemAtPosition(position);
        String guideText = (String) this.mService.getMenuItemGuideText(itemid);
        return guideText;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        super.pushedDownKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        super.pushedUpKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        if (this.mService.execCurrentMenuItem(getValueFromPosition(), false)) {
            this.mIsChangedValue = true;
        }
        update();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_filterset;
    }

    private String getValueFromPosition() {
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        return this.mService.getSupportedItemList().get(pos);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            if (itemid.equalsIgnoreCase(GFFilterSetController.TWO_AREAS)) {
                if (GFCommonUtil.getInstance().isLayer3()) {
                    if (GFEEAreaController.getInstance().isLayer3()) {
                        GFEEAreaController.getInstance().setValue(GFEEAreaController.EE, GFEEAreaController.FIRST);
                    }
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGED_THIRD_TO_SKY);
                    GFCommonUtil.getInstance().setBorderId(0);
                    openNextMenu("SkySettings", GFSettingMenuLayout.MENU_ID, false, null);
                    return;
                }
                openPreviousMenu();
                return;
            }
            if (itemid.equalsIgnoreCase(GFFilterSetController.THREE_AREAS)) {
                if (itemid.equals(mOrigAddon)) {
                    openPreviousMenu();
                    return;
                }
                if (GFCommonUtil.getInstance().isLand()) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
                } else {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGED_SKY_TO_THIRD);
                }
                GFCommonUtil.getInstance().setBorderId(1);
                openNextMenu("Layer3Settings", GFSettingMenuLayout.MENU_ID, false, null);
                return;
            }
            super.doItemClickProcessing(itemid);
            return;
        }
        super.doItemClickProcessing(itemid);
        if (itemid.equalsIgnoreCase(GFFilterSetController.TWO_AREAS) && GFSettingMenuLayout.getCurrentArea().equals("Layer3Settings")) {
            GFSettingMenuLayout.disable3rdArea();
        }
        if (GFEEAreaController.getInstance().isLand()) {
            GFCommonUtil.getInstance().setCameraSettings(0, false);
        } else if (GFEEAreaController.getInstance().isSky()) {
            GFCommonUtil.getInstance().setCameraSettings(1, false);
        } else if (GFEEAreaController.getInstance().isLayer3()) {
            GFCommonUtil.getInstance().setCameraSettings(2, false);
        }
    }

    private void openOptionMenu() {
        if (this.mSpecialScreenView != null) {
            Bundle bundle = new Bundle();
            bundle.putString("PrevMenu", "FilterSet");
            openNextMenu("FilterShootingSet", GFFilterShootingMenuLayout.MENU_ID, true, bundle);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return MENU_ID;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (!isLayerSetting) {
            return super.pushedFnKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret;
        if (!GFCommonUtil.getInstance().isLayerSetting()) {
            if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
                ret = super.onConvertedKeyDown(event, func);
            } else if (!CustomizableFunction.Unchanged.equals(func)) {
                ret = -1;
            } else {
                ret = super.onConvertedKeyDown(event, func);
            }
        } else {
            int code = event.getScanCode();
            if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
                ret = super.onConvertedKeyDown(event, func);
            } else {
                if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
                    return 0;
                }
                ret = -1;
            }
        }
        return ret;
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
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (!isFunctionGuideShown()) {
                    openOptionMenu();
                }
                return 1;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
            case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                return -1;
            default:
                int result = super.onKeyDown(keyCode, event);
                return result;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 653) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
    }
}
