package com.sony.imaging.app.smoothreflection.menu.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.sony.imaging.app.base.menu.layout.BaseMenuAdapter;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.smoothreflection.R;
import com.sony.imaging.app.smoothreflection.SmoothReflectionApp;
import com.sony.imaging.app.smoothreflection.common.AppContext;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SaUtil;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionCreativeStyleController;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionPictureEffectController;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ThemeMenuLayout extends SpecialScreenMenuLayout {
    public static final String MENU_ID = "ID_THEMEMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    public static String sPreviousSelectedTheme = null;
    public static String sCurrentSelectedTheme = null;
    public static boolean sbISDeleteKeyPushed = false;
    public static boolean sbISCenterKeyPushed = false;
    private final String DEFAULT_THEME = ThemeController.TWILIGHTREFLECTION;
    private ViewGroup mCurrentView = null;
    private ImageView mImgVwBackground = null;
    private TextView mTxtVwScreenTitle = null;
    private TextView mTxtVwGuide = null;
    private FooterGuide mFooterGuide = null;
    private boolean mIsMenuKeyPressed = false;
    private String mLastSelectedTheme = ThemeController.TWILIGHTREFLECTION;
    private String mSelectedTheme = ThemeController.TWILIGHTREFLECTION;
    private ThemeController mThemeController = null;

    /* loaded from: classes.dex */
    protected class ThemeAdapter extends SpecialScreenMenuLayout.SpecialBaseMenuAdapter {
        public ThemeAdapter(Context context, int ResId, BaseMenuService service) {
            super(context, ResId, service);
        }

        @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout.SpecialBaseMenuAdapter, com.sony.imaging.app.base.menu.layout.BaseMenuAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            BaseMenuAdapter.ViewHolder holder = (BaseMenuAdapter.ViewHolder) view.getTag();
            holder.iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_special_button));
            return view;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout.SpecialBaseMenuAdapter, com.sony.imaging.app.base.menu.layout.BaseMenuAdapter
        public Drawable getMenuItemDrawable(int position) {
            BaseMenuService baseMenuService = this.mService;
            String itemId = this.mItems.get(position);
            Drawable drawable = this.mService.getMenuItemDrawable(itemId);
            return drawable;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mImgVwBackground = (ImageView) this.mCurrentView.findViewById(R.id.sample_image);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mAdapter = new ThemeAdapter(getActivity(), R.layout.menu_sub_adapter, this.mService);
        this.mTxtVwGuide = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        this.mTxtVwScreenTitle = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mCameraNotifier = null;
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return R.layout.menu_theme;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        setFooterGuide();
        this.mLastSelectedTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        this.mSelectedTheme = this.mLastSelectedTheme;
        sPreviousSelectedTheme = this.mLastSelectedTheme;
        sCurrentSelectedTheme = this.mSelectedTheme;
        this.mThemeController = (ThemeController) ThemeController.getInstance();
        this.mThemeController.setValue(ThemeController.THEMESELECTION, this.mSelectedTheme);
        String itemId = (String) this.mSpecialScreenView.getSelectedItem();
        this.mSelectedTheme = this.mService.getMenuItemValue(itemId);
        if (this.mTxtVwScreenTitle != null) {
            if ("ApplicationTop".equals(this.mService.getParentItemId(itemId))) {
                this.mScreenTitleView.setText(R.string.STRID_FUNC_TIMELAPSE_THEME);
            } else {
                this.mScreenTitleView.setText(this.mService.getParentItemId(itemId));
            }
        }
        update(this.mSpecialScreenView.getSelectedItemPosition());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!this.mIsMenuKeyPressed) {
            SmoothReflectionUtil.getInstance().updateApplicationTiltle(this.mSelectedTheme);
        }
        saveCurrentSetting();
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mIsMenuKeyPressed = false;
        this.mTxtVwScreenTitle = null;
        this.mTxtVwGuide = null;
        this.mFooterGuide = null;
        this.mImgVwBackground = null;
        this.mCurrentView = null;
        super.onDestroy();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void saveCurrentSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        BackUpUtil.getInstance().setPreference(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, this.mSelectedTheme);
        this.mThemeController.setValue(ThemeController.THEMESELECTION, this.mSelectedTheme);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void update(int position) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String itemid = (String) this.mSpecialScreenView.getItemAtPosition(position);
        this.mImgVwBackground.setImageDrawable(this.mService.getDrawable(itemid, "SampleImageRes"));
        if (this.mItemNameView != null) {
            this.mItemNameView.setText(this.mService.getMenuItemText(itemid));
            this.mItemNameView.setVisibility(0);
        }
        if (this.mTxtVwGuide != null) {
            this.mTxtVwGuide.setText(this.mService.getMenuItemGuideText(itemid));
            this.mTxtVwGuide.setVisibility(0);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int retVal = 1;
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            saveCurrentSetting();
            resetCameraSetting();
            openNextMenu("ApplicationSettings", SmoothOptionMenuLayout.MENU_ID);
            sbISDeleteKeyPushed = true;
        } else if (event.getScanCode() == 532) {
            retVal = -1;
        } else {
            retVal = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int returnState = 1;
        if (event.getScanCode() != 595 && event.getScanCode() != 513) {
            returnState = super.onKeyUp(keyCode, event);
        } else if (event.getScanCode() == 532) {
            returnState = -1;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String itemId = (String) this.mSpecialScreenView.getSelectedItem();
        saveCurrentSetting();
        resetCameraSetting();
        doItemClickProcessing(itemId);
        sbISCenterKeyPushed = true;
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedTheme = this.mLastSelectedTheme;
        sCurrentSelectedTheme = this.mSelectedTheme;
        if (SmoothReflectionApp.sBootFactor == 0) {
            this.mIsMenuKeyPressed = true;
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish();
            AppLog.exit(TAG, AppLog.getMethodName());
            return 1;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected ArrayList<String> getOptionMenuItemList(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return null;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        openNextMenu("ApplicationSettings", SmoothOptionMenuLayout.MENU_ID);
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return super.pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onItemSelected(parent, view, position, id);
        this.mService.execCurrentMenuItem((String) this.mSpecialScreenView.getItemAtPosition(position));
        String itemId = (String) this.mSpecialScreenView.getSelectedItem();
        this.mSelectedTheme = this.mService.getMenuItemValue(itemId);
        sCurrentSelectedTheme = this.mSelectedTheme;
        update(this.mSpecialScreenView.getSelectedItemPosition());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setFooterGuide() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (SmoothReflectionApp.sBootFactor == 0) {
            if (SaUtil.isAVIP()) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_FOOTERGUIDE_SMOOTHING_EXITAPP_SK));
            } else {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_FOOTERGUIDE_SMOOTHING_EXITAPP));
            }
        } else if (SaUtil.isAVIP()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_FOOTERGUIDE_SMOOTHING_RETURN_SK));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_FOOTERGUIDE_SMOOTHING_RETURN));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onAFMFSwitchChanged() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return -1;
    }

    private void resetCameraSetting() {
        if (ThemeController.CUSTOM.equals(this.mSelectedTheme)) {
            String creativeStyle = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_CREATIVE_STYLE, "standard");
            String pictureEffect = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_PICTURE_EFFECT, "off");
            CreativeStyleController.CreativeStyleOptions creativeStyleOptions = getCreativeStyleOptionFromBackup();
            SmoothReflectionCreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, creativeStyle);
            SmoothReflectionCreativeStyleController.getInstance().setDetailValue(creativeStyleOptions);
            SmoothReflectionPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, pictureEffect);
            return;
        }
        SmoothReflectionCreativeStyleController.getInstance().setValue(CreativeStyleController.CREATIVESTYLE, "standard");
        if (ThemeController.MONOTONE.equals(this.mSelectedTheme)) {
            CreativeStyleController.CreativeStyleOptions creativeStyleOptions2 = getCreativeStyleOptionFromBackup();
            creativeStyleOptions2.contrast = 3;
            creativeStyleOptions2.saturation = 0;
            creativeStyleOptions2.sharpness = 0;
            SmoothReflectionCreativeStyleController.getInstance().setDetailValue(creativeStyleOptions2);
        }
        SmoothReflectionPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, "off");
    }

    private CreativeStyleController.CreativeStyleOptions getCreativeStyleOptionFromBackup() {
        CreativeStyleController.CreativeStyleOptions creativeStyleOptions = (CreativeStyleController.CreativeStyleOptions) SmoothReflectionCreativeStyleController.getInstance().getDetailValue();
        AppLog.enter(TAG, AppLog.getMethodName());
        String optValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_CUSTOM_THEME_CREATIVE_STYLE_DETAIL, "0/0/0");
        String[] optValueArray = optValue.split("/");
        creativeStyleOptions.contrast = Integer.parseInt(optValueArray[0]);
        creativeStyleOptions.saturation = Integer.parseInt(optValueArray[1]);
        creativeStyleOptions.sharpness = Integer.parseInt(optValueArray[2]);
        AppLog.exit(TAG, AppLog.getMethodName());
        return creativeStyleOptions;
    }
}
