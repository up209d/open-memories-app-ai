package com.sony.imaging.app.digitalfilter.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFIntervalController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFVerticalLinkController;
import com.sony.imaging.app.digitalfilter.shooting.state.GFEEState;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class GFThemeSelectionLayout extends SpecialScreenMenuLayout {
    private static final boolean isAvailableDebugMode = false;
    private ViewGroup mCurrentView = null;
    private static final String TAG = AppLog.getClassName();
    private static ImageView mBackgroundImageView = null;
    private static TextView mItemGuideView = null;
    private static ImageView mBlack = null;
    private static FooterGuide mFooterGuide = null;
    private static String mThemeForCancel = null;
    private static boolean isBootedByLuncher = false;
    private static boolean isToggleEnable = true;
    private static ImageView mLogIcon = null;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mItemNameView = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        this.mItemNameView.setGravity(17);
        mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        mBackgroundImageView = (ImageView) this.mCurrentView.findViewById(R.id.theme_image);
        mItemGuideView = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        mLogIcon = (ImageView) this.mCurrentView.findViewById(R.id.logout_icon);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mService);
        isBootedByLuncher = GFCommonUtil.getInstance().getBootFactor() == 0;
        if (!isBootedByLuncher) {
            GFEffectParameters.Parameters parameter = GFEffectParameters.getInstance().getParameters();
            GFBackUpKey.getInstance().saveLastParameters(parameter.flatten());
        }
        if (mBlack == null) {
            mBlack = new ImageView(AppContext.getAppContext());
            mBlack.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            mBlack.setImageResource(android.R.color.black);
            this.mCurrentView.addView(mBlack, 0);
        }
        if (GFVerticalLinkController.getInstance().hasDigitalLevel() && GFEEState.mGyroListener != null) {
            GyroscopeObserver.getInstance().removeNotificationListener(GFEEState.mGyroListener);
        }
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setFooterGuide();
        update(this.mSpecialScreenView.getSelectedItemPosition());
        mThemeForCancel = GFThemeController.getInstance().getValue(GFThemeController.THEME);
        if (GFCommonUtil.getInstance().hasIrisRing() && GFCommonUtil.getInstance().needShowIrisLensMessage()) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_IRIS_RING);
            GFCommonUtil.getInstance().setShowIrisLensMessage(false);
        }
        isToggleEnable = true;
        GFCommonUtil.getInstance().updateLogIcon(mLogIcon);
        GFCommonUtil.getInstance().inValidFlashCheck();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mCurrentView = null;
        if (mBackgroundImageView != null) {
            mBackgroundImageView.setBackgroundResource(0);
            mBackgroundImageView = null;
        }
        this.mItemNameView = null;
        mItemGuideView = null;
        mBlack = null;
        mThemeForCancel = null;
        mFooterGuide = null;
        mLogIcon = null;
        GFCommonUtil.getInstance().validFlashCheck();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        if (GFVerticalLinkController.getInstance().hasDigitalLevel() && GFEEState.mGyroListener != null) {
            GyroscopeObserver.getInstance().setNotificationListener(GFEEState.mGyroListener);
        }
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_theme_selection;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        super.onItemSelected(adapterView, view, position, id);
        this.mService.execCurrentMenuItem((String) this.mSpecialScreenView.getItemAtPosition(position), false);
        update(this.mSpecialScreenView.getSelectedItemPosition());
    }

    private void update(int position) {
        String itemid = (String) this.mSpecialScreenView.getItemAtPosition(position);
        this.mScreenTitleView.setText("");
        this.mItemNameView.setText(this.mService.getMenuItemText(itemid));
        this.mItemNameView.setVisibility(0);
        mItemGuideView.setText(getGuideText(position));
        mItemGuideView.setVisibility(0);
        mBackgroundImageView.setImageResource(getBackGroundResourceID(position));
    }

    private String getGuideText(int position) {
        String itemId = (String) this.mSpecialScreenView.getItemAtPosition(position);
        String guideText = (String) this.mService.getMenuItemGuideText(itemId);
        return guideText;
    }

    private int getBackGroundResourceID(int position) {
        String itemId = (String) this.mSpecialScreenView.getItemAtPosition(position);
        if (itemId.equals("standard")) {
            return R.drawable.p_16_dd_parts_hgf_graduatednd_image;
        }
        if (itemId.equals(GFThemeController.REVERSE)) {
            return R.drawable.p_16_dd_parts_hgf_reverse_image;
        }
        if (itemId.equals(GFThemeController.STRIPE)) {
            return R.drawable.p_16_dd_parts_hgf_colorstripe_image;
        }
        if (itemId.equals(GFThemeController.BLUESKY)) {
            return R.drawable.p_16_dd_parts_hgf_bluesky_image;
        }
        if (itemId.equals("sunset")) {
            return R.drawable.p_16_dd_parts_hgf_sunset_image;
        }
        if (itemId.equals("custom1")) {
            return R.drawable.p_16_dd_parts_hgf_custom_image;
        }
        if (!itemId.equals("custom2")) {
            return R.drawable.p_16_dd_parts_hgf_graduatednd_image;
        }
        return R.drawable.p_16_dd_parts_hgf_custom_image;
    }

    private void setFooterGuide() {
        if (isBootedByLuncher) {
            if (GFIntervalController.getInstance().isSupportedInterval()) {
                mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_INTERVAL_EXITAPP_BY_MENU, R.string.STRID_FOOTERGUIDE_INTERVAL_EXITAPP_BY_MENU));
                return;
            } else {
                mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.hour, android.R.string.hour_picker_description));
                return;
            }
        }
        if (GFIntervalController.getInstance().isSupportedInterval()) {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_INTERVAL_RETURN_BY_MENU, R.string.STRID_FOOTERGUIDE_INTERVAL_RETURN_BY_MENU));
        } else {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (isBootedByLuncher) {
            cancelValue();
            getActivity().finish();
            return 1;
        }
        int returnState = super.pushedMenuKey();
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        GFCommonUtil.getInstance().setInvalidShutter(true);
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (GFIntervalController.getInstance().isSupportedInterval() && (event.getScanCode() == 595 || event.getScanCode() == 513)) {
            openThemeOptionMenu();
            return 1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        isToggleEnable = true;
        int debugStage = BackUpUtil.getInstance().getPreferenceInt(GFBackUpKey.KEY_DEBUG_LOG, 0);
        if (debugStage == 1) {
            BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_DEBUG_LOG, 2);
            GFCommonUtil.getInstance().updateLogIcon(mLogIcon);
        }
        return super.onKeyUp(keyCode, event);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void openThemeOptionMenu() {
        if (this.mSpecialScreenView != null) {
            String itemId = this.mAdapter.getItem(this.mSpecialScreenView.getSelectedItemPosition());
            doOnKeyProcessing(itemId);
        }
    }

    protected void doOnKeyProcessing(String itemid) {
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (this.mService != null) {
            if (!this.mService.isMenuItemValid(itemid)) {
                requestCautionTrigger(itemid);
                return;
            }
            String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
            this.mService.execCurrentMenuItem(itemid);
            if (nextFragmentID != null) {
                PTag.start("Menu open next MenuLayout");
                openNextMenu(itemid, nextFragmentID);
            } else {
                Log.e(TAG, "Can't open the next MenuLayout");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    public void cancelValue() {
        GFThemeController.getInstance().setValue(GFThemeController.THEME, mThemeForCancel);
        super.cancelValue();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        GFCommonUtil.getInstance().setThemeSelectedFlag(true);
        GFCommonUtil.getInstance().setCommonCameraSettings();
        GFCommonUtil.getInstance().setEECameraSettings();
        String theme = GFThemeController.getInstance().getValue();
        GFLinkAreaController.getInstance().checkParent(theme);
        super.closeLayout();
        this.mCurrentView = null;
        if (mBackgroundImageView != null) {
            mBackgroundImageView.setBackgroundResource(0);
            mBackgroundImageView = null;
        }
        this.mItemNameView = null;
        mItemGuideView = null;
        mBlack = null;
        mThemeForCancel = null;
        mFooterGuide = null;
        mLogIcon = null;
        System.gc();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void onDeviceStatusChanged() {
    }
}
