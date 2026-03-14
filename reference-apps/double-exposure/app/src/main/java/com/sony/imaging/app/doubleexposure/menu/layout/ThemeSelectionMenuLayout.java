package com.sony.imaging.app.doubleexposure.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.doubleexposure.DoubleExposureApp;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppContext;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DESA;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureBackUpKey;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.doubleexposure.shooting.state.DoubleExposureShootingMenuState;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ThemeSelectionMenuLayout extends SpecialScreenMenuLayout implements SpecialScreenView.OnItemSelectedListener {
    private final String TAG = AppLog.getClassName();
    private final String DEFAULT_THEME = "Manual";
    private final int ITEM_COUNT_5 = 5;
    private ViewGroup mCurrentView = null;
    private ImageView mImgVwShadeArea = null;
    private ImageView mImgVwGuideBackground = null;
    private TextView mTxtVwGuide = null;
    private FooterGuide mFooterGuide = null;
    protected TextView mTxtVwScreenTitle = null;
    protected TextView mTxtVwItemName = null;
    private ImageView mImgVwBlendingImage = null;
    private TextView mTxtVwBlendingMethod = null;
    private String mLastSelectedTheme = "Manual";
    private String mSelectedTheme = "Manual";
    private ThemeSelectionController mThemeSelectionController = null;
    private DoubleExposureUtil mDoubleExposureUtil = null;
    private ArrayList<String> mList = null;
    private boolean mIsUpdateTitle = false;
    private boolean mIsTurnedEVDial = false;
    protected Layout mLayoutStatus = Layout.SUB;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public enum Layout {
        SUB,
        OPTION
    }

    protected void setLayoutStatus(Layout layout) {
        this.mLayoutStatus = layout;
        if (this.mImgVwShadeArea != null) {
            this.mImgVwShadeArea.setVisibility(Layout.SUB == layout ? 4 : 0);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.theme_selection_layout);
        Context context = getActivity().getApplicationContext();
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mService = new BaseMenuService(context);
        this.mOptionService = new BaseMenuService(context);
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mService);
        this.mOptionAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mOptionService);
        this.mImgVwShadeArea = (ImageView) this.mCurrentView.findViewById(R.id.specialscreen_shade_area);
        if (this.mImgVwShadeArea != null) {
            this.mImgVwShadeArea.setVisibility(Layout.SUB == this.mLayoutStatus ? 4 : 0);
        }
        this.mTxtVwScreenTitle = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mTxtVwItemName = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        this.mImgVwGuideBackground = (ImageView) this.mCurrentView.findViewById(R.id.guide_image);
        this.mTxtVwGuide = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        this.mImgVwBlendingImage = (ImageView) this.mCurrentView.findViewById(R.id.blending_icon);
        this.mTxtVwBlendingMethod = (TextView) this.mCurrentView.findViewById(R.id.blending_method);
        this.mImgVwBlendingImage.setVisibility(4);
        this.mTxtVwBlendingMethod.setVisibility(4);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        DESA.getInstance().terminate();
        this.mDoubleExposureUtil = DoubleExposureUtil.getInstance();
        this.mLastSelectedTheme = BackUpUtil.getInstance().getPreferenceString(DoubleExposureBackUpKey.KEY_SELECTED_THEME, ThemeSelectionController.SKY);
        this.mSelectedTheme = this.mLastSelectedTheme;
        this.mThemeSelectionController = ThemeSelectionController.getInstance();
        AppLog.info(this.TAG, "mSelectedLevel = " + this.mSelectedTheme);
        int position = this.mService.getMenuItemList().indexOf("ApplicationTop_" + this.mSelectedTheme);
        int mBlock = getMenuItemList().size();
        if (mBlock > 5) {
            mBlock = 5;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, position);
        this.mThemeSelectionController.setValue(ThemeSelectionController.THEMESELECTION, this.mSelectedTheme);
        if (DoubleExposureApp.sBootFactor != 0) {
            DoubleExposureUtil.getInstance().setLastSelectedTheme(this.mLastSelectedTheme);
        }
        this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
        this.mDoubleExposureUtil.setCurrentMenuLayout(DoubleExposureShootingMenuState.ID_THEMESELECTIONMENULAYOUT);
        if (this.mSelectedTheme != null && this.mSelectedTheme.equalsIgnoreCase("Manual") && DoubleExposureApp.sBootFactor != 0) {
            this.mDoubleExposureUtil.getRecommendedValues();
        }
        setLayoutStatus(Layout.SUB);
        if (this.mTxtVwScreenTitle != null) {
            this.mTxtVwScreenTitle.setText(R.string.STRID_FUNC_TIMELAPSE_THEME);
        }
        this.mList = this.mService.getSupportedItemList();
        update(position);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mIsTurnedEVDial) {
            this.mDoubleExposureUtil.updateExposureCompensation();
            this.mIsTurnedEVDial = false;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        saveCurrentThemeSettings();
        if (!this.mIsUpdateTitle) {
            updateApplicationTiltle(this.mSelectedTheme);
        }
        this.mIsUpdateTitle = false;
        this.mDoubleExposureUtil = null;
        this.mSpecialScreenView = null;
        this.mService = null;
        this.mOptionService = null;
        this.mAdapter = null;
        this.mOptionAdapter = null;
        this.mImgVwBlendingImage = null;
        this.mTxtVwBlendingMethod = null;
        this.mImgVwShadeArea = null;
        this.mTxtVwScreenTitle = null;
        this.mTxtVwItemName = null;
        this.mFooterGuide = null;
        this.mSelectedTheme = null;
        this.mImgVwGuideBackground = null;
        this.mTxtVwGuide = null;
        this.mList = null;
        this.mCurrentView = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String itemId = this.mList.get(position);
        this.mSelectedTheme = this.mService.getMenuItemValue(itemId);
        super.onItemSelected(parent, view, position, id);
        update(position);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onOptionItemSelected(SpecialScreenView parent, View view, int position, long id) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mTxtVwScreenTitle != null) {
            this.mTxtVwScreenTitle.setText(R.string.STRID_FUNC_TIMELAPSE_THEME);
        }
        if (this.mViewArea != null) {
            this.mViewArea.update();
        }
        update(position);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.closeMenuLayout(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (DoubleExposureApp.sBootFactor == 0) {
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish();
        } else {
            this.mSelectedTheme = this.mLastSelectedTheme;
            this.mThemeSelectionController.setValue(ThemeSelectionController.THEMESELECTION, this.mSelectedTheme);
            this.mIsUpdateTitle = true;
            this.mService = new BaseMenuService(AppContext.getAppContext());
            String nextMenuId = this.mService.getMenuItemNextMenuID("Main1");
            if (nextMenuId != null && this.mService.isMenuItemValid("Main1")) {
                openNextMenu("Main1", nextMenuId);
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getScanCode() == 595 || event.getScanCode() == 513) && !"Manual".equalsIgnoreCase(this.mSelectedTheme)) {
            openMenuLayout(DoubleExposureShootingMenuState.ID_THEMEGUIDELAYOUT, this.data);
            return 1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            return 1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return DoubleExposureShootingMenuState.ID_THEMESELECTIONMENULAYOUT;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int position = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(position);
        guideResources.add(this.mService.getMenuItemText(itemId));
        guideResources.add(this.mService.getMenuItemGuideText(itemId));
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mIsTurnedEVDial = true;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.turnedEVDial();
    }

    private void update(int position) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String itemId = this.mList.get(position);
        String value = this.mService.getMenuItemValue(itemId);
        this.mSelectedTheme = value;
        if (this.mTxtVwItemName != null) {
            this.mTxtVwItemName.setText(this.mService.getMenuItemText(itemId));
            this.mTxtVwItemName.setVisibility(0);
        }
        if (this.mTxtVwGuide != null) {
            this.mTxtVwGuide.setText(this.mService.getMenuItemGuideText(itemId));
            this.mTxtVwGuide.setVisibility(0);
        }
        if (this.mImgVwGuideBackground != null) {
            updateBackGroundImage(value);
            this.mImgVwGuideBackground.setVisibility(0);
        }
        if (!value.equalsIgnoreCase("Manual")) {
            if (this.mImgVwBlendingImage != null || this.mTxtVwBlendingMethod != null) {
                this.mImgVwBlendingImage.setVisibility(0);
                this.mTxtVwBlendingMethod.setVisibility(0);
                updateBlendingImageAndText(value);
            }
        } else {
            this.mImgVwBlendingImage.setVisibility(4);
            this.mTxtVwBlendingMethod.setVisibility(4);
        }
        if (this.mViewArea != null) {
            this.mViewArea.update();
        }
        this.mThemeSelectionController.setValue(ThemeSelectionController.THEMESELECTION, this.mSelectedTheme);
        setFooterGuide();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void setFooterGuide() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (DoubleExposureApp.sBootFactor == 0) {
            this.mDoubleExposureUtil.updateDefaultValues();
            if (1 == Environment.getVersionOfHW()) {
                if (!"Manual".equalsIgnoreCase(this.mSelectedTheme)) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FOOTERGUIDE_GUIDE_EXIT_SK));
                } else {
                    this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FOOTERGUIDE_EXITAPP_BY_SK1));
                }
            } else if (!"Manual".equalsIgnoreCase(this.mSelectedTheme)) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FOOTERGUIDE_GUIDE_EXIT));
            } else {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FOOTERGUIDE_EXITAPP_BY_MENU));
            }
        } else if (1 == Environment.getVersionOfHW()) {
            if (!"Manual".equalsIgnoreCase(this.mSelectedTheme)) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FOOTERGUIDE_GUIDE_RETURN_FOR_SK));
            } else {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_MLE_AMC_STR_06562_SK));
            }
        } else if (!"Manual".equalsIgnoreCase(this.mSelectedTheme)) {
            this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FOOTERGUIDE_GUIDE_RETURN));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), android.R.string.zen_mode_feature_name));
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void updateBlendingImageAndText(String value) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int blendingTxtResId = -1;
        int blendingImageResId = -1;
        if (value.equalsIgnoreCase(ThemeSelectionController.SIL)) {
            blendingTxtResId = R.string.STRID_FUNC_MLE_LIGHTEN;
            blendingImageResId = R.drawable.p_16_dd_parts_mle_theme_lighten;
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SKY)) {
            blendingTxtResId = R.string.STRID_FUNC_MLE_WEIGHT;
            blendingImageResId = R.drawable.p_16_dd_parts_mle_theme_weighted_ave;
        } else if (value.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
            blendingTxtResId = R.string.STRID_FUNC_MLE_MULTIPLY;
            blendingImageResId = R.drawable.p_16_dd_parts_mle_theme_multiply;
        } else if (value.equalsIgnoreCase("Rotation") || value.equalsIgnoreCase(ThemeSelectionController.MIRROR) || value.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
            blendingTxtResId = R.string.STRID_FUNC_MLE_SCREEN;
            blendingImageResId = R.drawable.p_16_dd_parts_mle_theme_screen;
        }
        this.mImgVwBlendingImage.setImageResource(blendingImageResId);
        this.mTxtVwBlendingMethod.setText(blendingTxtResId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void updateBackGroundImage(String value) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (value.equalsIgnoreCase("Manual")) {
            this.mImgVwGuideBackground.setImageResource(R.drawable.p_16_dd_parts_mle_theme_manual_img);
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SIL)) {
            this.mImgVwGuideBackground.setImageResource(R.drawable.p_16_dd_parts_mle_theme_easysilhouette_img);
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SKY)) {
            this.mImgVwGuideBackground.setImageResource(R.drawable.p_16_dd_parts_mle_theme_sky_img);
        } else if (value.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
            this.mImgVwGuideBackground.setImageResource(R.drawable.p_16_dd_parts_mle_theme_texture_img);
        } else if (value.equalsIgnoreCase("Rotation")) {
            this.mImgVwGuideBackground.setImageResource(R.drawable.p_16_dd_parts_mle_theme_rotation_img);
        } else if (value.equalsIgnoreCase(ThemeSelectionController.MIRROR)) {
            this.mImgVwGuideBackground.setImageResource(R.drawable.p_16_dd_parts_mle_theme_mirror_img);
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
            this.mImgVwGuideBackground.setImageResource(R.drawable.p_16_dd_parts_mle_theme_softfilter_img);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void updateApplicationTiltle(String value) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int kikilogSubId = 0;
        if (value.equalsIgnoreCase("Manual")) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_MANUAL));
            kikilogSubId = 4191;
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SIL)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_SIL));
            kikilogSubId = 4192;
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SKY)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_SKY));
            kikilogSubId = 4193;
        } else if (value.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_TEXTURE));
            kikilogSubId = 4194;
        } else if (value.equalsIgnoreCase("Rotation")) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_ROTATE));
            kikilogSubId = 4195;
        } else if (value.equalsIgnoreCase(ThemeSelectionController.MIRROR)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_MIRROR));
            kikilogSubId = 4196;
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_SOFTFILTER));
            kikilogSubId = 4197;
        }
        this.mDoubleExposureUtil.startKikiLog(kikilogSubId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void saveCurrentThemeSettings() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, "mSelectedLevel = " + this.mSelectedTheme);
        BackUpUtil.getInstance().setPreference(DoubleExposureBackUpKey.KEY_SELECTED_THEME, this.mSelectedTheme);
        this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
        if (!this.mSelectedTheme.equalsIgnoreCase(this.mLastSelectedTheme)) {
            DoubleExposureUtil.getInstance().updateDefaultValues();
        }
        if (!this.mSelectedTheme.equalsIgnoreCase("Manual")) {
            DoubleExposureUtil.getInstance().setInitFocusMode(true);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
