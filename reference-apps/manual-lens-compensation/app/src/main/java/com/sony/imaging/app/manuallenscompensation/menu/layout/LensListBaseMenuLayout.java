package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.menu.layout.MenuLayoutListener;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.ByteDataAnalyser;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;
import com.sony.imaging.app.manuallenscompensation.widget.LevelGauge;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public abstract class LensListBaseMenuLayout extends DisplayMenuItemsMenuLayout implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, MenuLayoutListener, NotificationListener {
    protected LevelGauge mChromaticBlueGauge;
    protected LevelGauge mChromaticRedGauge;
    protected View mCurrentView;
    protected LevelGauge mDistortionGauge;
    protected TextView mEmptyText;
    protected FooterGuide mGuide;
    protected ImageView mMinusIconLevel1;
    protected ImageView mMinusIconLevel2;
    protected ImageView mMinusIconLevel3;
    protected ImageView mPlusIconLevel1;
    protected ImageView mPlusIconLevel2;
    protected ImageView mPlusIconLevel3;
    protected LevelGauge mShadingBlueGauge;
    protected LevelGauge mShadingLightGauge;
    protected LevelGauge mShadingRedGauge;
    protected Cursor mCursor = null;
    protected String TAG = "LensListBaseMenuLayout";
    protected ImageView mLensListScreen = null;
    protected ImageView mLensListScreen_create = null;
    protected View mFooterCreateView = null;
    protected View mFooterImportView = null;
    protected View mOptionView = null;
    protected ListView mListView = null;
    protected TextView mEditButton = null;
    protected TextView mDeleteButton = null;
    protected TextView mExportButton = null;
    protected TextView mFocalValues = null;
    protected TextView mTitle = null;
    protected String mPreviousMenuLayoutID = null;
    protected ImageView mHeader = null;
    protected int mLastSelectedID = 0;
    protected int mRestoreOldValue = 0;
    public HistoryItem mLastItemId = null;
    protected String mActionSelected = null;

    public abstract BaseAdapter getAdapter(Context context);

    public abstract BaseAdapter getAdapter(Context context, Cursor cursor);

    protected abstract void initializeView();

    public abstract void onItemClick(AdapterView<?> adapterView, View view, int i, long j);

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_lens_list);
        }
        this.mLensListScreen = (ImageView) this.mCurrentView.findViewById(R.id.middle_part);
        this.mLensListScreen_create = (ImageView) this.mCurrentView.findViewById(R.id.middle_part_create);
        this.mListView = (ListView) this.mCurrentView.findViewById(R.id.list_view);
        this.mGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        initializeRightPaneComponents();
        this.mOptionView = this.mCurrentView.findViewById(R.id.menu_lens_list_option);
        this.mOptionView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.mHeader = (ImageView) this.mCurrentView.findViewById(R.id.header);
        this.mEditButton = (TextView) this.mCurrentView.findViewById(R.id.edittext);
        this.mDeleteButton = (TextView) this.mCurrentView.findViewById(R.id.deletetext);
        this.mExportButton = (TextView) this.mCurrentView.findViewById(R.id.exporttext);
        this.mTitle = (TextView) this.mCurrentView.findViewById(R.id.title);
        this.mEmptyText = (TextView) this.mCurrentView.findViewById(R.id.empty_text);
        this.mEmptyText.setVisibility(8);
        this.mOptionView.setVisibility(8);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        OCUtil.getInstance().setCloseMenuLayoutExecuted(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HistoryItem getMenuHistoryItem() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        BaseMenuService service = new BaseMenuService(parcelable.getMenuService());
        HistoryItem item = service.popMenuHistory();
        service.pushMenuHistory(item);
        return item;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTitleForSceeen(int screenType, String titleString) {
        TextView title = (TextView) getView().findViewById(R.id.title);
        ImageView header = (ImageView) getView().findViewById(R.id.header);
        if (header != null) {
            if (screenType == 1) {
                header.setBackgroundResource(R.drawable.p_16_dd_parts_oc_external_list_header);
            } else {
                header.setBackgroundResource(R.drawable.p_16_dd_parts_oc_list_header);
            }
        }
        if (title != null) {
            title.setText(titleString);
        }
    }

    public void showFooterGuideDrawable(int screenType) {
        if (screenType == 1) {
            this.mGuide.setBackgroundResource(R.drawable.p_16_dd_parts_oc_external_ist_footer);
        } else {
            this.mGuide.setBackgroundResource(R.drawable.p_16_dd_parts_oc_ist_footer);
        }
    }

    public void showFooterGuide(int pOneExitGuide, int pOneReturnGuide, int sk2ExitGuide, int skReturnGuide) {
        if (this.mGuide != null) {
            if (this.mPreviousMenuLayoutID == null && this.mLastItemId == null) {
                this.mGuide.setData(new FooterGuideDataResId(getActivity(), pOneExitGuide, sk2ExitGuide));
            } else {
                this.mGuide.setData(new FooterGuideDataResId(getActivity(), pOneReturnGuide, skReturnGuide));
            }
        }
    }

    protected void initializeRightPaneComponents() {
        this.mFocalValues = (TextView) this.mCurrentView.findViewById(R.id.focal_values);
        this.mShadingLightGauge = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_gauge1);
        this.mShadingRedGauge = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_gauge2);
        this.mShadingBlueGauge = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_gauge3);
        this.mChromaticRedGauge = (LevelGauge) this.mCurrentView.findViewById(R.id.chromatic_gauge1);
        this.mChromaticBlueGauge = (LevelGauge) this.mCurrentView.findViewById(R.id.chromatic_gauge2);
        this.mDistortionGauge = (LevelGauge) this.mCurrentView.findViewById(R.id.distortion_gauge);
        this.mPlusIconLevel1 = (ImageView) this.mCurrentView.findViewById(R.id.plus_icon_level1);
        this.mPlusIconLevel2 = (ImageView) this.mCurrentView.findViewById(R.id.plus_icon_level2);
        this.mPlusIconLevel3 = (ImageView) this.mCurrentView.findViewById(R.id.plus_icon_level3);
        this.mMinusIconLevel1 = (ImageView) this.mCurrentView.findViewById(R.id.minus_icon_level1);
        this.mMinusIconLevel2 = (ImageView) this.mCurrentView.findViewById(R.id.minus_icon_level2);
        this.mMinusIconLevel3 = (ImageView) this.mCurrentView.findViewById(R.id.minus_icon_level3);
        clearRightPane(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initializeListener() {
        this.mListView.setOnItemClickListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mCursor != null) {
            this.mCursor.close();
            this.mCursor = null;
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deinitializeView();
        super.onDestroyView();
    }

    protected void deinitializeView() {
        removeFooterView();
        if (this.mListView != null) {
            this.mListView.setOnItemClickListener(null);
            this.mListView = null;
        }
        if (this.mOptionView != null) {
            this.mOptionView.setOnClickListener(null);
            this.mOptionView = null;
        }
        this.mCurrentView = null;
        if (this.mEditButton != null) {
            this.mEditButton.setOnClickListener(null);
            this.mEditButton = null;
        }
        if (this.mDeleteButton != null) {
            this.mDeleteButton.setOnClickListener(null);
            this.mDeleteButton = null;
        }
        if (this.mExportButton != null) {
            this.mExportButton.setOnClickListener(null);
            this.mExportButton = null;
        }
        if (this.mLensListScreen_create != null) {
            releaseImageViewDrawable(this.mLensListScreen_create);
            this.mLensListScreen_create = null;
        }
        if (this.mLensListScreen != null) {
            releaseImageViewDrawable(this.mLensListScreen);
            this.mLensListScreen = null;
        }
        this.mTitle = null;
        this.mFocalValues = null;
        this.mCurrentView = null;
        this.mShadingLightGauge = null;
        this.mShadingRedGauge = null;
        this.mShadingBlueGauge = null;
        this.mChromaticRedGauge = null;
        this.mChromaticBlueGauge = null;
        this.mDistortionGauge = null;
        this.mPlusIconLevel1 = null;
        this.mMinusIconLevel1 = null;
        this.mPlusIconLevel2 = null;
        this.mMinusIconLevel2 = null;
        this.mPlusIconLevel3 = null;
        this.mMinusIconLevel3 = null;
        this.mPreviousMenuLayoutID = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_UP);
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_DOWN);
        return 0;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> parent) {
        AppLog.info(this.TAG, "INSIDE onNothingSelected");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateRightPane(int position, BaseAdapter baseAdapter) {
        int light_vignetting;
        int red_vignetting;
        int blue_vegnitting;
        int red_aberration;
        int blue_aberration;
        int distortion;
        String f_value;
        String focal_length;
        Object param_object = baseAdapter.getItem(position);
        if (param_object.getClass() == LensCompensationParameter.class) {
            LensCompensationParameter params = (LensCompensationParameter) param_object;
            light_vignetting = params.getLevel(OCController.LIGHT_VIGNETTING);
            red_vignetting = params.getLevel(OCController.RED_COLOR_VIGNETTING);
            blue_vegnitting = params.getLevel(OCController.BLUE_COLOR_VIGNETTING);
            red_aberration = params.getLevel(OCController.RED_CHROMATIC_ABERRATION);
            blue_aberration = params.getLevel(OCController.BLUE_CHROMATIC_ABERRATION);
            distortion = params.getLevel(OCController.DISTORTION);
            f_value = getFValueForDisplay(params.mFValue);
            focal_length = params.mFocalLength;
        } else {
            ByteDataAnalyser params2 = (ByteDataAnalyser) param_object;
            light_vignetting = params2.getFMinLightVignettingCorrection();
            red_vignetting = params2.getFMinRedColorVignettingCorrection();
            blue_vegnitting = params2.getFMinBlueColorVignettingCorrection();
            red_aberration = params2.getFMinRedChromaticAberationCorrection();
            blue_aberration = params2.getFMinBlueChromaticAberationCorrection();
            distortion = params2.getFMinDistortionCorrection();
            f_value = getFValueForDisplay(params2.getMinFNumber());
            focal_length = String.valueOf(params2.getFocalLength());
        }
        AppLog.info(this.TAG, "INSIDE updateRightPanelight_vig = " + light_vignetting + " red_vig = " + red_vignetting + " blue_vig = " + blue_vegnitting + " red_chrom = " + red_aberration + " blue_chrom = " + blue_aberration + " distor = " + distortion);
        if ("".equals(focal_length)) {
            focal_length = "0";
        }
        if (f_value != null) {
            this.mFocalValues.setText(focal_length + "mm / F" + f_value);
        } else {
            this.mFocalValues.setText(focal_length + "mm / F0");
        }
        this.mShadingLightGauge.setProgress(light_vignetting + 16);
        this.mShadingRedGauge.setProgress(red_vignetting + 16);
        this.mShadingBlueGauge.setProgress(blue_vegnitting + 16);
        this.mChromaticRedGauge.setProgress(red_aberration + 16);
        this.mChromaticBlueGauge.setProgress(blue_aberration + 16);
        this.mDistortionGauge.setProgress(distortion + 16);
    }

    private String getFValueForDisplay(String fValueFromDB) {
        try {
            int dot_index = fValueFromDB.indexOf(".");
            String f_value_int_part = fValueFromDB.substring(0, dot_index);
            String f_value_dec_part = fValueFromDB.substring(dot_index + 1);
            boolean isIntPartBlank = " ".equals(f_value_int_part);
            boolean isDecPartBlank = " ".equals(f_value_dec_part);
            if (isIntPartBlank && isDecPartBlank) {
                return "0";
            }
            if (!isIntPartBlank && isDecPartBlank) {
                return f_value_int_part;
            }
            if (!isIntPartBlank || isDecPartBlank) {
                return fValueFromDB;
            }
            String f_value_param = "0".concat(".").concat(f_value_dec_part);
            return f_value_param;
        } catch (Exception exp) {
            AppLog.error(this.TAG, AppLog.getMethodName() + ",Excpetion Message: " + exp.getMessage());
            return fValueFromDB;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setBackGroundImageDrawable(int screen) {
        if (screen == 1) {
            this.mLensListScreen_create.setBackgroundResource(R.drawable.p_16_dd_parts_oc_external_list_background_create);
            this.mLensListScreen.setBackgroundResource(R.drawable.p_16_dd_parts_oc_external_list_background);
        } else {
            this.mLensListScreen_create.setBackgroundResource(R.drawable.p_16_dd_parts_oc_list_background_create);
            this.mLensListScreen.setBackgroundResource(R.drawable.p_16_dd_parts_oc_list_background);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearRightPane(int screen) {
        this.mFocalValues.setText("");
        this.mLensListScreen_create.setVisibility(0);
        this.mLensListScreen.setVisibility(4);
        this.mShadingLightGauge.setVisibility(4);
        this.mShadingRedGauge.setVisibility(4);
        this.mShadingBlueGauge.setVisibility(4);
        this.mChromaticRedGauge.setVisibility(4);
        this.mChromaticBlueGauge.setVisibility(4);
        this.mDistortionGauge.setVisibility(4);
        this.mPlusIconLevel1.setVisibility(4);
        this.mPlusIconLevel2.setVisibility(4);
        this.mPlusIconLevel3.setVisibility(4);
        this.mMinusIconLevel1.setVisibility(4);
        this.mMinusIconLevel2.setVisibility(4);
        this.mMinusIconLevel3.setVisibility(4);
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (imageView != null) {
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(null);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showRightPane(int profileAction) {
        if (this.mShadingLightGauge.getVisibility() == 4) {
            AppLog.info("", "INSIDE showRightPane");
            this.mLensListScreen_create.setVisibility(4);
            this.mLensListScreen.setVisibility(0);
            this.mShadingLightGauge.setVisibility(0);
            this.mShadingRedGauge.setVisibility(0);
            this.mShadingBlueGauge.setVisibility(0);
            this.mChromaticRedGauge.setVisibility(0);
            this.mChromaticBlueGauge.setVisibility(0);
            this.mDistortionGauge.setVisibility(0);
            this.mPlusIconLevel1.setVisibility(0);
            this.mPlusIconLevel2.setVisibility(0);
            this.mPlusIconLevel3.setVisibility(0);
            this.mMinusIconLevel1.setVisibility(0);
            this.mMinusIconLevel2.setVisibility(0);
            this.mMinusIconLevel3.setVisibility(0);
        }
    }

    protected void removeFooterView() {
        if (this.mFooterCreateView != null) {
            this.mListView.removeFooterView(this.mFooterCreateView);
            this.mFooterCreateView = null;
        }
        if (this.mFooterImportView != null) {
            this.mListView.removeFooterView(this.mFooterImportView);
            this.mFooterImportView = null;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.MenuLayoutListener
    public MenuState getState() {
        if (getMenuLayoutListener() != null) {
            return getMenuLayoutListener().getState();
        }
        return null;
    }

    protected void setFooterguide() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        BaseMenuService service = new BaseMenuService(parcelable.getMenuService());
        HistoryItem item = service.popMenuHistory();
        String prevLayoutId = null;
        if (item != null) {
            prevLayoutId = item.layoutId;
        }
        if ("ID_PAGEMENULAYOUT".equalsIgnoreCase(prevLayoutId)) {
            showFooterGuide(R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_BY_MENU, R.string.STRID_FOOTERGUIDE_OPTION_RETURN_BY_MENU, R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_FOR_SK, R.string.STRID_FOOTERGUIDE_OPTION_RETURN_FOR_SK);
        } else {
            showFooterGuide(android.R.string.hour, android.R.string.zen_mode_feature_name, android.R.string.hour_picker_description, R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_BY_MENU);
        }
    }
}
