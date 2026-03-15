package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppContext;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFThemeController;

/* loaded from: classes.dex */
public class GFThemeSelectionLayout extends SpecialScreenMenuLayout {
    private static final String TAG = AppLog.getClassName();
    private static final int[] BACKGROUND_RESOURCES = {R.drawable.p_16_dd_parts_hgf_bluesky_image, R.drawable.p_16_dd_parts_hgf_sunset_image, R.drawable.p_16_dd_parts_hgf_graduatednd_image, R.drawable.p_16_dd_parts_hgf_custom_image, R.drawable.p_16_dd_parts_hgf_custom_image};
    private static final int[] ADDITIONAL_GUIDE = {R.string.STRID_FUNC_SKYND_RECOMMENDEDEXP_A_GUIDE, R.string.STRID_FUNC_SKYND_RECOMMENDEDEXP_A_GUIDE, R.string.STRID_FUNC_SKYND_RECOMMENDEDEXP_A_GUIDE, R.string.STRID_FUNC_SKYND_RECOMMENDEDEXP_A_GUIDE, R.string.STRID_FUNC_SKYND_RECOMMENDEDEXP_A_GUIDE};
    private ViewGroup mCurrentView = null;
    private ImageView mBackgroundImageView = null;
    private TextView mItemGuideView = null;
    private ImageView mBlack = null;
    private FooterGuide mFooterGuide = null;
    private String mThemeForCancel = null;
    private boolean isBootedByLuncher = false;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mItemNameView = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        this.mItemNameView.setGravity(17);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mBackgroundImageView = (ImageView) this.mCurrentView.findViewById(R.id.theme_image);
        this.mItemGuideView = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mService);
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        this.isBootedByLuncher = parcelable.getPreviousMenuLayoutId() == null;
        if (this.mBlack == null) {
            this.mBlack = new ImageView(AppContext.getAppContext());
            this.mBlack.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.mBlack.setImageResource(android.R.color.black);
            this.mCurrentView.addView(this.mBlack, 0);
        }
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setFooterGuide();
        update(this.mSpecialScreenView.getSelectedItemPosition());
        this.mThemeForCancel = GFThemeController.getInstance().getValue(GFThemeController.THEME);
        if (GFCommonUtil.getInstance().hasIrisRing() && GFCommonUtil.getInstance().needShowIrisLensMessage()) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_IRIS_RING);
            GFCommonUtil.getInstance().setShowIrisLensMessage(false);
        }
        GFCommonUtil.getInstance().needCTempSetting();
        GFCommonUtil.getInstance().inValidFlashCheck();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mCurrentView = null;
        this.mBackgroundImageView.setBackgroundResource(0);
        this.mBackgroundImageView = null;
        this.mItemNameView = null;
        this.mItemGuideView = null;
        this.mBlack = null;
        this.mThemeForCancel = null;
        this.mFooterGuide = null;
        GFCommonUtil.getInstance().validFlashCheck();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
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
        this.mItemGuideView.setText(getGuideText(position));
        this.mItemGuideView.setVisibility(0);
        this.mBackgroundImageView.setImageResource(getBackGroundResourceID(position));
    }

    private String getGuideText(int position) {
        String itemid = (String) this.mSpecialScreenView.getItemAtPosition(position);
        String guideText = (String) this.mService.getMenuItemGuideText(itemid);
        String additionalguide = "\n" + getResources().getString(ADDITIONAL_GUIDE[position]);
        if (position < 2) {
            return guideText + additionalguide;
        }
        return guideText;
    }

    private int getBackGroundResourceID(int position) {
        return BACKGROUND_RESOURCES[position];
    }

    private void setFooterGuide() {
        if (this.isBootedByLuncher) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.hour, android.R.string.hour_picker_description));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.isBootedByLuncher) {
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    public void cancelValue() {
        GFThemeController.getInstance().setValue(GFThemeController.THEME, this.mThemeForCancel);
        super.cancelValue();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        GFCommonUtil.getInstance().setCommonCameraSettings();
        GFCommonUtil.getInstance().setBaseCameraSettings();
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDeviceStatusChanged() {
    }
}
