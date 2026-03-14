package com.sony.imaging.app.digitalfilter.menu.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.widget.ApertureView;
import com.sony.imaging.app.base.shooting.widget.ExposureCompensationIcon;
import com.sony.imaging.app.base.shooting.widget.ISOIconFn;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.common.SaUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFPositionLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShadingLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFFilterSetIcon;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFWhiteBalanceFnIcon;
import com.sony.imaging.app.digitalfilter.shooting.widget.GridLine;
import com.sony.imaging.app.digitalfilter.shooting.widget.ShadingLevel;
import com.sony.imaging.app.digitalfilter.shooting.widget.ShutterSpeedView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFSettingMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final int ADDON = 0;
    private static final int APERTURE = 4;
    private static final int EXPCOMP = 3;
    private static final int INVISIBLE_ALPHA = 128;
    private static final int ISO = 6;
    public static final String LAND_ID = "LandSettings";
    public static final String LAYER3_ID = "Layer3Settings";
    public static final String MENU_ID = "ID_GFSETTINGMENULAYOUT";
    private static final int POS = 1;
    private static final int SHADING = 2;
    public static final String SKY_ID = "SkySettings";
    private static final int SS = 5;
    private static final int VISIBLE_ALPHA = 255;
    private static final int WHITEBALANCE = 7;
    private static ApertureView mApertureView;
    private static ExposureCompensationIcon mExpCompIcon;
    private static ImageView mFilterSet;
    private static GFFilterSetIcon mFilterSetFn;
    private static View[] mFocus;
    private static ISOIconFn mIsoIcon;
    private static ImageView mPosIcon;
    private static ImageView mSetting15Zabuton0;
    private static ImageView mSetting15Zabuton1;
    private static ImageView mSetting15Zabuton2;
    private static ImageView mSetting15Zabuton2_4;
    private static ImageView mShadingIcon;
    private static ShadingLevel mShadingValue;
    private static ShutterSpeedView mShutterSpeedView1;
    private static ShutterSpeedView mShutterSpeedView2;
    private static ShutterSpeedView mShutterSpeedView3;
    private static GFWhiteBalanceFnIcon mWBIcon;
    private static View mCurrentView = null;
    private static GridLine mGridLine = null;
    private static int mCurrentFocus = 0;
    private static TextView mItemName = null;
    private static TextView mLinkTo = null;
    private static BorderView mBorderView = null;
    private static FooterGuide mFooterGuide = null;
    private static ImageView mAreaIcon = null;
    private static TextView mTitle = null;
    private static RelativeLayout mEvLink = null;
    private static RelativeLayout mApertureLink = null;
    private static RelativeLayout mSSLink = null;
    private static RelativeLayout mIsoLink = null;
    private static RelativeLayout mWBLink = null;
    private static ImageView mPositionLink = null;
    private static ImageView mShadingLink = null;
    private static ImageView mEvLink1 = null;
    private static ImageView mEvLink2 = null;
    private static ImageView mEvLink3 = null;
    private static ImageView mApertureLink1 = null;
    private static ImageView mApertureLink2 = null;
    private static ImageView mApertureLink3 = null;
    private static ImageView mSSLink1 = null;
    private static ImageView mSSLink2 = null;
    private static ImageView mSSLink3 = null;
    private static ImageView mIsoLink1 = null;
    private static ImageView mIsoLink2 = null;
    private static ImageView mIsoLink3 = null;
    private static ImageView mWBLink1 = null;
    private static ImageView mWBLink2 = null;
    private static ImageView mWBLink3 = null;
    private static RelativeLayout mSep0 = null;
    private static RelativeLayout mSep1 = null;
    private static RelativeLayout mSep2 = null;
    private static RelativeLayout mSep3 = null;
    private static RelativeLayout mSep4 = null;
    private static boolean isLand = false;
    private static boolean isSky = false;
    private static boolean isLayer3 = false;
    private static int mSettingLayer = 0;
    private static boolean mOpeningOtherLayout = false;
    private static boolean isAreaChanged = false;
    private static Handler mHandler = null;
    private static Runnable mRunnable = null;
    private static Runnable mTitleRunnable = null;
    private static Runnable mItemNameRunnable = null;
    private static RelativeLayout mAreaGuide = null;
    public static boolean requestWBAdjustment = false;
    private static boolean mDuringToggleLink = false;
    private static final String[] tags = {CameraNotificationManager.DEVICE_LENS_CHANGED, "ExposureCompensation", CameraNotificationManager.ISO_SENSITIVITY, GFConstants.CLOSE_GUIDE, GFConstants.TAG_GYROSCOPE, GFConstants.FILTER_SET_CHANGED, GFConstants.CLOSE_15LAYER_BOUNDARY_SETTING};
    private final String TAG = AppLog.getClassName();
    NotificationListener mListener = new ChangeYUVNotifier();
    NotificationListener mCameraListener = new ChangeCameraNotifier();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_setting);
        mAreaIcon = (ImageView) mCurrentView.findViewById(R.id.area_icon);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        mGridLine = (GridLine) mCurrentView.findViewById(R.id.gridline);
        mItemName = (TextView) mCurrentView.findViewById(R.id.item_name);
        mLinkTo = (TextView) mCurrentView.findViewById(R.id.link_to);
        mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        mTitle = (TextView) mCurrentView.findViewById(R.id.menu_screen_title);
        mSetting15Zabuton0 = (ImageView) mCurrentView.findViewById(R.id.zabuton0);
        mSetting15Zabuton1 = (ImageView) mCurrentView.findViewById(R.id.zabuton1);
        mSetting15Zabuton2 = (ImageView) mCurrentView.findViewById(R.id.zabuton2);
        mSetting15Zabuton2_4 = (ImageView) mCurrentView.findViewById(R.id.zabuton2_4);
        mSetting15Zabuton2_4.setVisibility(4);
        mFocus = new View[]{(ImageView) mCurrentView.findViewById(R.id.focus0), (ImageView) mCurrentView.findViewById(R.id.focus1), (ImageView) mCurrentView.findViewById(R.id.focus2), (ImageView) mCurrentView.findViewById(R.id.focus3), (ImageView) mCurrentView.findViewById(R.id.focus4), (ImageView) mCurrentView.findViewById(R.id.focus5), (ImageView) mCurrentView.findViewById(R.id.focus6), (ImageView) mCurrentView.findViewById(R.id.focus7)};
        mFilterSet = (ImageView) mCurrentView.findViewById(R.id.filter_set);
        mFilterSetFn = (GFFilterSetIcon) mCurrentView.findViewById(R.id.filter_set_fn);
        mPosIcon = (ImageView) mCurrentView.findViewById(R.id.pos_img);
        mShadingIcon = (ImageView) mCurrentView.findViewById(R.id.shading_img);
        mShadingValue = (ShadingLevel) mCurrentView.findViewById(R.id.shading_level);
        mFilterSet = (ImageView) mCurrentView.findViewById(R.id.filter_set);
        mExpCompIcon = (ExposureCompensationIcon) mCurrentView.findViewById(R.id.expcomp_icon);
        mExpCompIcon.setFnMode(true);
        mIsoIcon = (ISOIconFn) mCurrentView.findViewById(R.id.iso_icon);
        mIsoIcon.setFnMode(true);
        mWBIcon = (GFWhiteBalanceFnIcon) mCurrentView.findViewById(R.id.wb_icon);
        mWBIcon.setFnMode(true);
        mApertureView = (ApertureView) mCurrentView.findViewById(R.id.aperture_icon);
        mShutterSpeedView1 = (ShutterSpeedView) mCurrentView.findViewById(R.id.shutterspeed_icon);
        mShutterSpeedView2 = (ShutterSpeedView) mCurrentView.findViewById(R.id.shutterspeed2_icon);
        mShutterSpeedView3 = (ShutterSpeedView) mCurrentView.findViewById(R.id.shutterspeed3_icon);
        mApertureView.setTextColor(GFCommonUtil.getInstance().isAvailableAperture() ? -2236963 : -8947849);
        mShutterSpeedView1.setTextColor(GFCommonUtil.getInstance().isAvailableShutterSpeed() ? -2236963 : -8947849);
        mShutterSpeedView2.setTextColor(GFCommonUtil.getInstance().isAvailableShutterSpeed() ? -2236963 : -8947849);
        mShutterSpeedView3.setTextColor(GFCommonUtil.getInstance().isAvailableShutterSpeed() ? -2236963 : -8947849);
        mShutterSpeedView1.setFnMode(true);
        mShutterSpeedView2.setFnMode(true);
        mShutterSpeedView3.setFnMode(true);
        mEvLink = (RelativeLayout) mCurrentView.findViewById(R.id.exp_link_info);
        mApertureLink = (RelativeLayout) mCurrentView.findViewById(R.id.aperture_link_info);
        mSSLink = (RelativeLayout) mCurrentView.findViewById(R.id.ss_link_info);
        mIsoLink = (RelativeLayout) mCurrentView.findViewById(R.id.iso_link_info);
        mWBLink = (RelativeLayout) mCurrentView.findViewById(R.id.wb_link_info);
        mPositionLink = (ImageView) mCurrentView.findViewById(R.id.pos_link);
        mShadingLink = (ImageView) mCurrentView.findViewById(R.id.shading_link);
        mEvLink1 = (ImageView) mCurrentView.findViewById(R.id.exp_link_info).findViewById(R.id.link_land);
        mEvLink2 = (ImageView) mCurrentView.findViewById(R.id.exp_link_info).findViewById(R.id.link_sky);
        mEvLink3 = (ImageView) mCurrentView.findViewById(R.id.exp_link_info).findViewById(R.id.link_3rd);
        mApertureLink1 = (ImageView) mCurrentView.findViewById(R.id.aperture_link_info).findViewById(R.id.link_land);
        mApertureLink2 = (ImageView) mCurrentView.findViewById(R.id.aperture_link_info).findViewById(R.id.link_sky);
        mApertureLink3 = (ImageView) mCurrentView.findViewById(R.id.aperture_link_info).findViewById(R.id.link_3rd);
        mSSLink1 = (ImageView) mCurrentView.findViewById(R.id.ss_link_info).findViewById(R.id.link_land);
        mSSLink2 = (ImageView) mCurrentView.findViewById(R.id.ss_link_info).findViewById(R.id.link_sky);
        mSSLink3 = (ImageView) mCurrentView.findViewById(R.id.ss_link_info).findViewById(R.id.link_3rd);
        mIsoLink1 = (ImageView) mCurrentView.findViewById(R.id.iso_link_info).findViewById(R.id.link_land);
        mIsoLink2 = (ImageView) mCurrentView.findViewById(R.id.iso_link_info).findViewById(R.id.link_sky);
        mIsoLink3 = (ImageView) mCurrentView.findViewById(R.id.iso_link_info).findViewById(R.id.link_3rd);
        mWBLink1 = (ImageView) mCurrentView.findViewById(R.id.wb_link_info).findViewById(R.id.link_land);
        mWBLink2 = (ImageView) mCurrentView.findViewById(R.id.wb_link_info).findViewById(R.id.link_sky);
        mWBLink3 = (ImageView) mCurrentView.findViewById(R.id.wb_link_info).findViewById(R.id.link_3rd);
        mSep0 = (RelativeLayout) mCurrentView.findViewById(R.id.sep0);
        mSep1 = (RelativeLayout) mCurrentView.findViewById(R.id.sep1);
        mSep2 = (RelativeLayout) mCurrentView.findViewById(R.id.sep2);
        mSep3 = (RelativeLayout) mCurrentView.findViewById(R.id.sep3);
        mSep4 = (RelativeLayout) mCurrentView.findViewById(R.id.sep4);
        mAreaGuide = (RelativeLayout) mCurrentView.findViewById(R.id.area_guide);
        View[] arr$ = mFocus;
        for (View focus : arr$) {
            Drawable selector = getResources().getDrawable(R.drawable.menu_fn_selector);
            focus.setBackgroundDrawable(selector);
            selector.setCallback(null);
        }
        String prevArea = GFCommonUtil.getInstance().getPrevArea();
        if (prevArea.equals(LAND_ID)) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.LAND_APPSETTING);
            GFCommonUtil.getInstance().setBorderId(0);
        } else if (prevArea.equals("SkySettings")) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.SKY_APPSETTING);
            GFCommonUtil.getInstance().setBorderId(0);
        } else if (prevArea.equals("Layer3Settings")) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.LAYER3_APPSETTING);
            GFCommonUtil.getInstance().setBorderId(1);
        }
        isAreaChanged = false;
        disableAfMfHold();
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        GFCommonUtil.getInstance().startLayerSetting();
        String itemId = this.mService.getMenuItemId();
        isLand = itemId.equals(LAND_ID);
        isSky = itemId.equals("SkySettings");
        isLayer3 = itemId.equals("Layer3Settings");
        GFCommonUtil.getInstance().setLayerFlag(isLand, isSky, isLayer3);
        setMenuTitle();
        GFLinkAreaController.getInstance().stopToggleSetting();
        mDuringToggleLink = false;
        requestWBAdjustment = false;
        if (isAreaChanged && !itemId.equals(GFCommonUtil.getInstance().getPrevArea())) {
            if (isLand) {
                CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGED_SKY_TO_LAND);
                GFCommonUtil.getInstance().setBorderId(0);
            } else if (isSky) {
                if (GFCommonUtil.getInstance().getPrevArea().equals(LAND_ID)) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGED_LAND_TO_SKY);
                } else {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGED_THIRD_TO_SKY);
                }
                GFCommonUtil.getInstance().setBorderId(0);
            } else if (isLayer3) {
                CameraNotificationManager.getInstance().requestNotify(GFConstants.CHANGED_SKY_TO_THIRD);
                GFCommonUtil.getInstance().setBorderId(1);
            }
        }
        GFCommonUtil.getInstance().setPrevArea(itemId);
        mSettingLayer = GFCommonUtil.getInstance().getSettingLayer();
        GFCommonUtil.getInstance().setTransitionFromWBAdjustment(false);
        CameraNotificationManager.getInstance().setNotificationListener(this.mCameraListener);
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        updateFilterSetIcon();
        updateAreaIcon();
        updateLinkImage();
        updateFocus();
        updateExpIconAlpha();
        boolean isShownStep2Guide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_STEP2GUIDE, false);
        boolean isShown3rdAreaGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_3RD_AREA_GUIDE, false);
        if (!isShownStep2Guide) {
            Bundle bundle = new Bundle();
            bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "STEP2");
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
        if (isLayer3 && !isShown3rdAreaGuide && GFCommonUtil.getInstance().needShow3rdAreaSettingGuide() && isShownStep2Guide && isShownStep2Guide) {
            mAreaGuide.setVisibility(0);
            GFCommonUtil.getInstance().setShow3rdAreaSettingFlag(false);
            BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_3RD_AREA_GUIDE, true);
        } else {
            mAreaGuide.setVisibility(4);
        }
        setFooterGuide();
        mGridLine.setVisibility(4);
        mOpeningOtherLayout = false;
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SHUTTER_SPEED);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.SHADING_LEVEL_CHANGE);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.FILTER_SET_CHANGED);
        isAreaChanged = true;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        getFocusable();
        updateBoundaryPosLink();
        updateBoundaryShadingLink();
        mExpCompIcon.setAlpha(ExposureCompensationController.getInstance().isExposureCompensationAvailable() ? 255 : INVISIBLE_ALPHA);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (mDuringToggleLink) {
            copyLinkSetting();
            mDuringToggleLink = false;
            GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
            GFBackUpKey.getInstance().saveLastParameters(param.flatten());
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.removeCallbacks(mTitleRunnable);
            mHandler.removeCallbacks(mItemNameRunnable);
            mHandler = null;
        }
        mRunnable = null;
        mTitleRunnable = null;
        mItemNameRunnable = null;
        if (getLayout(GFGuideLayout.ID_GFGUIDELAYOUT) != null && getLayout(GFGuideLayout.ID_GFGUIDELAYOUT).getView() != null) {
            getLayout(GFGuideLayout.ID_GFGUIDELAYOUT).closeLayout();
        }
        CameraNotificationManager.getInstance().removeNotificationListener(this.mCameraListener);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mAreaGuide = null;
        mAreaIcon = null;
        mFooterGuide = null;
        mCurrentView = null;
        mGridLine = null;
        mSetting15Zabuton0 = null;
        mSetting15Zabuton1 = null;
        mSetting15Zabuton2 = null;
        mSetting15Zabuton2_4 = null;
        mItemName = null;
        mLinkTo = null;
        mBorderView = null;
        mFocus = null;
        mFilterSet = null;
        mFilterSetFn = null;
        mPosIcon = null;
        mShadingIcon = null;
        mShadingValue = null;
        mExpCompIcon = null;
        mIsoIcon = null;
        mWBIcon = null;
        mApertureView = null;
        mShutterSpeedView1 = null;
        mShutterSpeedView2 = null;
        mShutterSpeedView3 = null;
        mEvLink = null;
        mApertureLink = null;
        mSSLink = null;
        mIsoLink = null;
        mWBLink = null;
        mPositionLink = null;
        mShadingLink = null;
        mEvLink1 = null;
        mEvLink2 = null;
        mEvLink3 = null;
        mApertureLink1 = null;
        mApertureLink2 = null;
        mApertureLink3 = null;
        mSSLink1 = null;
        mSSLink2 = null;
        mSSLink3 = null;
        mIsoLink1 = null;
        mIsoLink2 = null;
        mIsoLink3 = null;
        mWBLink1 = null;
        mWBLink2 = null;
        mWBLink3 = null;
        mSep0 = null;
        mSep1 = null;
        mSep2 = null;
        mSep3 = null;
        mSep4 = null;
        mTitle = null;
        super.onDestroyView();
        System.gc();
    }

    private void setMenuTitle() {
        mAreaIcon.setVisibility(0);
        mTitle.setVisibility(0);
        if (isLand) {
            mTitle.setText(R.string.STRID_FUNC_DF_LAND_SETTINGS);
        } else if (isSky) {
            mTitle.setText(R.string.STRID_FUNC_DF_SKY_SETTINGS);
        } else if (isLayer3) {
            mTitle.setText(R.string.STRID_FUNC_DF_LAYER3_SETTINGS);
        }
        disappearTitle();
    }

    private void disappearTitle() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mTitleRunnable);
        }
        if (mTitleRunnable == null) {
            mTitleRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    GFSettingMenuLayout.mAreaIcon.setVisibility(4);
                    GFSettingMenuLayout.mTitle.setVisibility(4);
                }
            };
        }
        mHandler.postDelayed(mTitleRunnable, 3000L);
    }

    private void disappearItemName() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mItemNameRunnable);
        }
        if (mItemNameRunnable == null) {
            mItemNameRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout.2
                @Override // java.lang.Runnable
                public void run() {
                    GFSettingMenuLayout.mItemName.setVisibility(4);
                }
            };
        }
        mHandler.postDelayed(mItemNameRunnable, 3000L);
    }

    private void disableAfMfHold() {
        if (FocusModeController.getInstance().isAfMfControlHold()) {
            FocusModeController.getInstance().holdFocusControl(false);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    private void updateFilterSetIcon() {
        int resId = R.drawable.p_16_dd_parts_skyhdr_filter_set_2area_1st;
        if (GFFilterSetController.getInstance().need3rdShooting()) {
            if (isLand) {
                resId = R.drawable.p_16_dd_parts_skyhdr_filter_set_3area_1st;
            } else if (isSky) {
                resId = R.drawable.p_16_dd_parts_skyhdr_filter_set_3area_2nd;
            } else if (isLayer3) {
                resId = R.drawable.p_16_dd_parts_skyhdr_filter_set_3area_3rd;
            }
        } else if (isLand) {
            resId = R.drawable.p_16_dd_parts_skyhdr_filter_set_2area_1st;
        } else if (isSky) {
            resId = R.drawable.p_16_dd_parts_skyhdr_filter_set_2area_2nd;
        }
        mFilterSet.setImageResource(resId);
        mFilterSet.setVisibility(0);
        mFilterSetFn.setVisibility(4);
    }

    private void updateAreaIcon() {
        int resId = R.drawable.p_16_dd_parts_skyhdr_3rd_icon_3area;
        if (GFFilterSetController.getInstance().need3rdShooting()) {
            if (isLand) {
                resId = R.drawable.p_16_dd_parts_skyhdr_1st_icon_3area;
            } else if (isSky) {
                resId = R.drawable.p_16_dd_parts_skyhdr_2nd_icon_3area;
            }
        } else if (isLand) {
            resId = R.drawable.p_16_dd_parts_skyhdr_1st_icon_2area;
        } else if (isSky) {
            resId = R.drawable.p_16_dd_parts_skyhdr_2nd_icon_2area;
        }
        mAreaIcon.setImageResource(resId);
    }

    private void updateFocus() {
        setZabuton(0);
        updateButtons(0);
        View[] arr$ = mFocus;
        for (View focus : arr$) {
            focus.setSelected(false);
        }
        mFocus[mCurrentFocus].setSelected(true);
        mOpeningOtherLayout = false;
    }

    private void updateLinkImage() {
        updateBoundaryPosLink();
        updateBoundaryShadingLink();
        updateExpCompLink();
        updateApertureLink();
        updateSSLink();
        updateIsoLink();
        updateWBLink();
    }

    private void updateBoundaryPosLink() {
        String link = GFPositionLinkController.getInstance().getValue(GFPositionLinkController.RELATIVE_MODE);
        if (link.equals(GFPositionLinkController.OFF)) {
            mPositionLink.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_pos_link_normal);
        } else if (link.equals(GFPositionLinkController.ON)) {
            mPositionLink.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_pos_link_all);
        }
    }

    private void updateBoundaryShadingLink() {
        String link = GFShadingLinkController.getInstance().getValue(GFShadingLinkController.RELATIVE_MODE);
        if (link.equals(GFShadingLinkController.OFF)) {
            mShadingLink.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_pos_link_normal);
        } else if (link.equals(GFShadingLinkController.ON)) {
            mShadingLink.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_pos_link_all);
        }
    }

    private void updateLink(ImageView linkLand, ImageView linkSky, ImageView link3rd, boolean isLandLink, boolean isSkyLink, boolean isLayer3Link) {
        if (isLandLink) {
            linkLand.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_link_on);
        } else {
            linkLand.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_link_off);
        }
        if (isSkyLink) {
            linkSky.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_link_on);
        } else {
            linkSky.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_link_off);
        }
        if (isLayer3Link) {
            link3rd.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_link_on);
        } else {
            link3rd.setBackgroundResource(R.drawable.p_16_dd_parts_skyhdr_link_off);
        }
    }

    private void updateExpCompLink() {
        boolean isLandLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_EXPCOMP);
        boolean isSkyLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_EXPCOMP);
        boolean isLayer3Link = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_EXPCOMP);
        updateLink(mEvLink1, mEvLink2, mEvLink3, isLandLink, isSkyLink, isLayer3Link);
    }

    private void updateApertureLink() {
        boolean isLandLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_APERTURE);
        boolean isSkyLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_APERTURE);
        boolean isLayer3Link = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_APERTURE);
        updateLink(mApertureLink1, mApertureLink2, mApertureLink3, isLandLink, isSkyLink, isLayer3Link);
    }

    private void updateSSLink() {
        boolean isLandLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_SS);
        boolean isSkyLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_SS);
        boolean isLayer3Link = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_SS);
        updateLink(mSSLink1, mSSLink2, mSSLink3, isLandLink, isSkyLink, isLayer3Link);
    }

    private void updateIsoLink() {
        boolean isLandLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_ISO);
        boolean isSkyLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_ISO);
        boolean isLayer3Link = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_ISO);
        updateLink(mIsoLink1, mIsoLink2, mIsoLink3, isLandLink, isSkyLink, isLayer3Link);
    }

    private void updateWBLink() {
        boolean isLandLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAND_WB);
        boolean isSkyLink = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.SKY_WB);
        boolean isLayer3Link = GFLinkAreaController.getInstance().isLink(GFLinkAreaController.LAYER3_WB);
        updateLink(mWBLink1, mWBLink2, mWBLink3, isLandLink, isSkyLink, isLayer3Link);
    }

    private void setFilterSetItemsVisibility(int visibility) {
        mSetting15Zabuton0.setVisibility(visibility);
        mFilterSet.setVisibility(4);
        mFilterSetFn.setVisibility(visibility);
    }

    private void setBorderItemsVisibility(int visibility) {
        mSetting15Zabuton1.setVisibility(visibility);
        mPositionLink.setVisibility(visibility);
        mShadingLink.setVisibility(visibility);
        mSep0.setVisibility(visibility);
        mPosIcon.setVisibility(visibility);
        mShadingIcon.setVisibility(visibility);
        mShadingValue.setVisibility(visibility);
    }

    private void setExposureItemsVisibility(int visibility) {
        mEvLink.setVisibility(visibility);
        mApertureLink.setVisibility(visibility);
        mSSLink.setVisibility(visibility);
        mIsoLink.setVisibility(visibility);
        mSep1.setVisibility(visibility);
        mSep2.setVisibility(visibility);
        mSep3.setVisibility(visibility);
        mSep4.setVisibility(visibility);
        if (visibility == 0) {
            mExpCompIcon.setFnMode(true);
            mShutterSpeedView1.setFnMode(true);
            mShutterSpeedView2.setFnMode(true);
            mShutterSpeedView3.setFnMode(true);
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SHUTTER_SPEED);
            mIsoIcon.setFnMode(true);
        } else {
            mExpCompIcon.setFnMode(false);
            mShutterSpeedView1.setFnMode(false);
            mShutterSpeedView2.setFnMode(false);
            mShutterSpeedView3.setFnMode(false);
            mIsoIcon.setFnMode(false);
            mShutterSpeedView1.setVisibility(visibility);
            mShutterSpeedView2.setVisibility(visibility);
            mShutterSpeedView3.setVisibility(visibility);
        }
        mExpCompIcon.setVisibility(visibility);
        mApertureView.setVisibility(visibility);
        mIsoIcon.setVisibility(visibility);
    }

    private void setWBItemsVisibility(int visibility) {
        mWBLink.setVisibility(visibility);
        if (visibility == 0) {
            mWBIcon.setFnMode(true);
        } else {
            mWBIcon.setFnMode(false);
        }
        mWBIcon.setVisibility(visibility);
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.WB_MODE_CHANGE);
    }

    private void updateButtons(int visibility) {
        if (visibility == 0) {
            setFilterSetItemsVisibility(visibility);
            setBorderItemsVisibility(visibility);
            setExposureItemsVisibility(visibility);
            setWBItemsVisibility(visibility);
            mSetting15Zabuton2.setVisibility(visibility);
            mSetting15Zabuton2_4.setVisibility(4);
            return;
        }
        switch (mCurrentFocus) {
            case 0:
                setBorderItemsVisibility(visibility);
                setExposureItemsVisibility(visibility);
                setWBItemsVisibility(visibility);
                mSetting15Zabuton2.setVisibility(visibility);
                mSetting15Zabuton2_4.setVisibility(visibility);
                return;
            case 1:
            case 2:
                setFilterSetItemsVisibility(visibility);
                setExposureItemsVisibility(visibility);
                setWBItemsVisibility(visibility);
                mSetting15Zabuton2.setVisibility(visibility);
                mSetting15Zabuton2_4.setVisibility(visibility);
                return;
            case 3:
            case 4:
            case 5:
            case 6:
                setFilterSetItemsVisibility(visibility);
                setBorderItemsVisibility(visibility);
                setWBItemsVisibility(visibility);
                mSetting15Zabuton2.setVisibility(visibility);
                mSetting15Zabuton2_4.setVisibility(0);
                mSep4.setVisibility(8);
                return;
            case 7:
                setFilterSetItemsVisibility(visibility);
                setBorderItemsVisibility(visibility);
                setExposureItemsVisibility(visibility);
                mSetting15Zabuton2.setVisibility(visibility);
                mSetting15Zabuton2_4.setVisibility(visibility);
                return;
            default:
                return;
        }
    }

    private void setZabuton(int visibility) {
        int id = R.drawable.p_16_dd_parts_skyhdr_base;
        if (visibility != 0) {
            id = R.drawable.p_16_dd_parts_skyhdr_transparent_base;
        }
        mSetting15Zabuton0.setImageResource(id);
        mSetting15Zabuton1.setImageResource(id);
        mSetting15Zabuton2.setImageResource(id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateItemName() {
        mLinkTo.setVisibility(4);
        switch (mCurrentFocus) {
            case 0:
                mItemName.setText(R.string.STRID_FUNC_DF_FILTERSETS);
                mItemName.setVisibility(0);
                break;
            case 1:
                if (isLayer3) {
                    mItemName.setText(R.string.STRID_FUNC_SKYND_POSITION_LAYER3);
                } else {
                    mItemName.setText(R.string.STRID_FUNC_DF_POSITION_SKY);
                }
                mItemName.setVisibility(0);
                break;
            case 2:
                if (isLayer3) {
                    mItemName.setText(R.string.STRID_FUNC_SKYND_GRADATION_LAYER3);
                } else {
                    mItemName.setText(R.string.STRID_FUNC_DF_GRADATION_SKY);
                }
                mItemName.setVisibility(0);
                break;
            case 3:
                if (isLayer3) {
                    mItemName.setText(R.string.STRID_FUNC_DF_EXPOSURECOMP_LAYER3);
                } else if (isSky) {
                    mItemName.setText(R.string.STRID_FUNC_DF_EXPOSURECOMP_SKY);
                } else if (isLand) {
                    mItemName.setText(R.string.STRID_FUNC_DF_EXPOSURECOMP_LAND);
                }
                mItemName.setVisibility(0);
                break;
            case 4:
                if (isLayer3) {
                    mItemName.setText(R.string.STRID_FUNC_SKYND_APERTURE_LAYER3);
                } else if (isSky) {
                    mItemName.setText(R.string.STRID_FUNC_DF_APERTURE_SKY);
                } else if (isLand) {
                    mItemName.setText(R.string.STRID_FUNC_DF_APERTURE_LAND);
                }
                mItemName.setVisibility(0);
                break;
            case 5:
                if (isLayer3) {
                    mItemName.setText(R.string.STRID_FUNC_SKYND_SS_LAYER3);
                } else if (isSky) {
                    mItemName.setText(R.string.STRID_FUNC_DF_SS_SKY);
                } else if (isLand) {
                    mItemName.setText(R.string.STRID_FUNC_DF_SS_LAND);
                }
                mItemName.setVisibility(0);
                break;
            case 6:
                if (isLayer3) {
                    mItemName.setText(R.string.STRID_FUNC_DF_ISO_3RD_AREA);
                } else if (isSky) {
                    mItemName.setText(R.string.STRID_FUNC_DF_ISO_SKY);
                } else if (isLand) {
                    mItemName.setText(R.string.STRID_FUNC_DF_ISO_LAND);
                }
                mItemName.setVisibility(0);
                break;
            case 7:
                if (isLayer3) {
                    mItemName.setText(R.string.STRID_FUNC_DF_WB_3RD_AREA);
                } else if (isSky) {
                    mItemName.setText(R.string.STRID_FUNC_DF_WB_SKY);
                } else if (isLand) {
                    mItemName.setText(R.string.STRID_FUNC_DF_WB_LAND);
                }
                mItemName.setVisibility(0);
                break;
            default:
                mItemName.setVisibility(4);
                break;
        }
        disappearItemName();
    }

    private void moveFocusToLeft() {
        mCurrentFocus--;
        if (mCurrentFocus < 0) {
            mCurrentFocus = mFocus.length - 1;
        }
        updateFocus();
        setFooterGuide();
        mBorderView.invalidate();
    }

    private void moveFocusToRight() {
        mCurrentFocus++;
        if (mCurrentFocus > mFocus.length - 1) {
            mCurrentFocus = 0;
        }
        updateFocus();
        setFooterGuide();
        mBorderView.invalidate();
    }

    private void executeSetting() {
        openNextMenuLayout();
    }

    private void openNextMenuLayout() {
        String layoutID = null;
        String itemID = null;
        String prefix = null;
        if (isLand) {
            prefix = "land_";
        } else if (isSky) {
            prefix = "sky_";
        } else if (isLayer3) {
            prefix = "layer3_";
        }
        switch (mCurrentFocus) {
            case 0:
                layoutID = GFFilterSetMenuLayout.MENU_ID;
                itemID = GFFilterSetController.FILTER_SET;
                break;
            case 1:
                layoutID = GFBorderMenuLayout.MENU_ID;
                itemID = prefix + "border";
                break;
            case 2:
                layoutID = GFShadingMenuLayout.MENU_ID;
                itemID = prefix + "shading";
                break;
            case 3:
                if (ExposureModeController.MANUAL_MODE.equalsIgnoreCase(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE)) && !ISOSensitivityController.ISO_AUTO.equalsIgnoreCase(ISOSensitivityController.getInstance().getValue())) {
                    if (SaUtil.isAVIP()) {
                        CautionUtilityClass.getInstance().requestTrigger(1470);
                        break;
                    } else {
                        CautionUtilityClass.getInstance().requestTrigger(3083);
                        break;
                    }
                } else {
                    layoutID = GFExposureCompensationMenuLayout.MENU_ID;
                    if (isLand) {
                        itemID = "ExposureCompensation";
                        break;
                    } else {
                        itemID = prefix + "ExposureCompensation";
                        break;
                    }
                }
                break;
            case 4:
                if (GFCommonUtil.getInstance().isAvailableAperture()) {
                    layoutID = GFAvMenuLayout.MENU_ID;
                    itemID = prefix + "aperture";
                    break;
                } else if (!GFCommonUtil.getInstance().isAvailableModeforAperture()) {
                    CautionUtilityClass.getInstance().requestTrigger(33649);
                    break;
                }
                break;
            case 5:
                if (GFCommonUtil.getInstance().isAvailableShutterSpeed()) {
                    layoutID = GFTvMenuLayout.MENU_ID;
                    itemID = prefix + "ss";
                    break;
                } else {
                    CautionUtilityClass.getInstance().requestTrigger(33649);
                    break;
                }
            case 6:
                layoutID = GFISOMenuLayout.MENU_ID;
                if (isLand) {
                    itemID = ISOSensitivityController.MENU_ITEM_ID_ISO;
                    break;
                } else {
                    itemID = prefix + ISOSensitivityController.MENU_ITEM_ID_ISO;
                    break;
                }
            case 7:
                layoutID = GFWhiteBalanceMenuLayout.MENU_ID;
                if (isLand) {
                    itemID = WhiteBalanceController.WHITEBALANCE;
                    break;
                } else {
                    itemID = prefix + WhiteBalanceController.WHITEBALANCE;
                    break;
                }
        }
        if (itemID != null && layoutID != null) {
            closeLayout();
            openNextMenu(itemID, layoutID);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int itemNameId = 0;
        switch (mCurrentFocus) {
            case 0:
                setGuideResource(guideResources, R.string.STRID_FUNC_DF_FILTERSETS, R.string.STRID_FUNC_SKYND_FILTERADDON_GUIDE);
                return;
            case 1:
                if (isLayer3) {
                    setGuideResource(guideResources, R.string.STRID_FUNC_SKYND_POSITION_LAYER3, R.string.STRID_FUNC_SKYND_LAYER3_SETTINGS_POSITION);
                    return;
                } else {
                    setGuideResource(guideResources, R.string.STRID_FUNC_DF_POSITION_SKY, R.string.STRID_FUNC_DF_SKY_SETTINGS_POSITION);
                    return;
                }
            case 2:
                if (isLayer3) {
                    setGuideResource(guideResources, R.string.STRID_FUNC_SKYND_GRADATION_LAYER3, R.string.STRID_FUNC_SKYND_LAYER3_SETTINGS_GRADATION);
                    return;
                } else {
                    setGuideResource(guideResources, R.string.STRID_FUNC_DF_GRADATION_SKY, R.string.STRID_FUNC_DF_SKY_SETTINGS_GRADATION);
                    return;
                }
            case 3:
                if (isLayer3) {
                    itemNameId = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAYER3;
                } else if (isSky) {
                    itemNameId = R.string.STRID_FUNC_DF_EXPOSURECOMP_SKY;
                } else if (isLand) {
                    itemNameId = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAND;
                }
                setGuideResource(guideResources, itemNameId, android.R.string.ext_media_unmounting_notification_message);
                return;
            case 4:
                if (isLayer3) {
                    itemNameId = R.string.STRID_FUNC_SKYND_APERTURE_LAYER3;
                } else if (isSky) {
                    itemNameId = R.string.STRID_FUNC_DF_APERTURE_SKY;
                } else if (isLand) {
                    itemNameId = R.string.STRID_FUNC_DF_APERTURE_LAND;
                }
                setGuideResource(guideResources, itemNameId, R.string.STRID_FUNC_SKYND_APERTURE_GUIDE);
                return;
            case 5:
                if (isLayer3) {
                    itemNameId = R.string.STRID_FUNC_SKYND_SS_LAYER3;
                } else if (isSky) {
                    itemNameId = R.string.STRID_FUNC_DF_SS_SKY;
                } else if (isLand) {
                    itemNameId = R.string.STRID_FUNC_DF_SS_LAND;
                }
                setGuideResource(guideResources, itemNameId, R.string.STRID_FUNC_SKYND_SS_GUIDE);
                return;
            case 6:
                if (isLayer3) {
                    itemNameId = R.string.STRID_FUNC_DF_ISO_3RD_AREA;
                } else if (isSky) {
                    itemNameId = R.string.STRID_FUNC_DF_ISO_SKY;
                } else if (isLand) {
                    itemNameId = R.string.STRID_FUNC_DF_ISO_LAND;
                }
                setGuideResource(guideResources, itemNameId, android.R.string.ext_media_unmountable_notification_message);
                return;
            case 7:
                if (isLayer3) {
                    itemNameId = R.string.STRID_FUNC_DF_WB_3RD_AREA;
                } else if (isSky) {
                    itemNameId = R.string.STRID_FUNC_DF_WB_SKY;
                } else if (isLand) {
                    itemNameId = R.string.STRID_FUNC_DF_WB_LAND;
                }
                setGuideResource(guideResources, itemNameId, android.R.string.ext_media_move_failure_title);
                return;
            default:
                return;
        }
    }

    private void setGuideResource(ArrayList<Object> guideResources, int guideTitleID, int guideDefi) {
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFooterGuide() {
        int id = R.string.STRID_FUNC_DF_AREA_BTM_LINK_FOOTER;
        if (mCurrentFocus == 0 || mCurrentFocus == 2) {
        }
        boolean isNoLink = mCurrentFocus == 0;
        if (isLand) {
            if (isNoLink) {
                id = R.string.STRID_FUNC_DF_AREA_BTM_ADJ_FOOTER;
            } else {
                id = R.string.STRID_FUNC_DF_AREA_BTM_LINK_FOOTER;
            }
        } else if (isSky) {
            if (isNoLink) {
                if (GFFilterSetController.THREE_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
                    id = R.string.STRID_FUNC_DF_AREA_MID_ADJ_FOOTER;
                } else {
                    id = R.string.STRID_FUNC_DF_AREA_TOP_ADJ_FOOTER;
                }
            } else if (GFFilterSetController.THREE_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
                id = R.string.STRID_FUNC_DF_AREA_MID_LINK_FOOTER;
            } else {
                id = R.string.STRID_FUNC_DF_AREA_TOP_LINK_FOOTER;
            }
        } else if (isLayer3) {
            if (isNoLink) {
                id = R.string.STRID_FUNC_DF_AREA_TOP_ADJ_FOOTER;
            } else {
                id = R.string.STRID_FUNC_DF_AREA_TOP_LINK_FOOTER;
            }
        }
        if (mAreaGuide != null && mAreaGuide.getVisibility() == 0) {
            id = 17042422;
        }
        mFooterGuide.setData(new FooterGuideDataResId(getActivity(), id, id));
        updateItemName();
    }

    private int transitToPreviousMenu() {
        GFCommonUtil.getInstance().setLayerFlag(GFEEAreaController.getInstance().isLand(), GFEEAreaController.getInstance().isSky(), GFEEAreaController.getInstance().isLayer3());
        CameraNotificationManager.getInstance().requestNotify(GFConstants.STOP_APPSETTING);
        GFCommonUtil.getInstance().setEECameraSettings();
        GFCommonUtil.getInstance().setBorderId(0);
        if (GFCommonUtil.getInstance().getPrevMenuItemId() == null) {
            closeMenuLayout(null);
            return 1;
        }
        openNextMenu(GFCommonUtil.getInstance().getPrevMenuItemId(), GFPageMenuLayout.MENU_ID, false, null);
        return 1;
    }

    private void openFn15LayerLayout(Bundle bundle) {
        String itemId = null;
        int footerId = R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE;
        String layoutId = null;
        String prefix = null;
        if (isLand) {
            prefix = "land_";
        } else if (isSky) {
            prefix = "sky_";
        } else if (isLayer3) {
            prefix = "layer3_";
        }
        boolean valid = false;
        if (mCurrentFocus == 0) {
            itemId = GFFilterSetController.FILTER_SET;
            layoutId = GFSetting15LayerFilterSetLayout.MENU_ID;
            mFilterSet.setVisibility(4);
            mFilterSetFn.setVisibility(0);
            valid = true;
        } else if (mCurrentFocus == 1) {
            itemId = prefix + "border";
            layoutId = GFSetting15LayerBorderLayout.MENU_ID;
            valid = true;
            mGridLine.setVisibility(0);
        } else if (mCurrentFocus == 2) {
            itemId = prefix + "shading";
            layoutId = GFSetting15LayerShadingLayout.MENU_ID;
            valid = true;
            mGridLine.setVisibility(0);
        } else if (mCurrentFocus == 3) {
            if (isLand) {
                itemId = "ExposureCompensation";
            } else {
                itemId = prefix + "ExposureCompensation";
            }
            layoutId = GFSetting15LayerExposureCompensationLayout.MENU_ID;
            valid = !ExposureModeController.MANUAL_MODE.equalsIgnoreCase(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE)) || ISOSensitivityController.ISO_AUTO.equalsIgnoreCase(ISOSensitivityController.getInstance().getValue());
        } else if (mCurrentFocus == 6) {
            if (isLand) {
                itemId = ISOSensitivityController.MENU_ITEM_ID_ISO;
            } else {
                itemId = prefix + ISOSensitivityController.MENU_ITEM_ID_ISO;
            }
            layoutId = GFSetting15LayerISOLayout.MENU_ID;
            valid = true;
        } else if (mCurrentFocus == 4) {
            if (GFCommonUtil.getInstance().isAvailableAperture()) {
                itemId = prefix + "aperture";
                layoutId = GFSetting15LayerTvAvLayout.MENU_ID;
                valid = true;
                bundle.putString(GFConstants.BUNDLE_SETTING15_KEY, "aperture");
            } else if (!GFCommonUtil.getInstance().isAvailableModeforAperture()) {
                CautionUtilityClass.getInstance().requestTrigger(33649);
            }
        } else if (mCurrentFocus == 5) {
            if (GFCommonUtil.getInstance().isAvailableShutterSpeed()) {
                itemId = prefix + "ss";
                layoutId = GFSetting15LayerTvAvLayout.MENU_ID;
                valid = true;
                bundle.putString(GFConstants.BUNDLE_SETTING15_KEY, "ss");
            } else {
                CautionUtilityClass.getInstance().requestTrigger(33649);
            }
        } else if (mCurrentFocus == 7) {
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                footerId = R.string.STRID_FUNC_DF_WB_15LAYER_FOOTER_GUIDE;
            }
            if (isLand) {
                itemId = WhiteBalanceController.WHITEBALANCE;
            } else {
                itemId = prefix + WhiteBalanceController.WHITEBALANCE;
            }
            layoutId = GFSetting15LayerWhiteBalanceLayout.MENU_ID;
            valid = true;
        }
        if (valid) {
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
            openNextMenu(itemId, layoutId, false, bundle);
            setZabuton(4);
            updateButtons(8);
            mItemName.setVisibility(8);
            mLinkTo.setVisibility(8);
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), footerId, footerId));
            mOpeningOtherLayout = true;
            return;
        }
        if (mCurrentFocus == 3) {
            if (SaUtil.isAVIP()) {
                CautionUtilityClass.getInstance().requestTrigger(1470);
            } else {
                CautionUtilityClass.getInstance().requestTrigger(3083);
            }
        }
    }

    private void getFocusable() {
        if (mOpeningOtherLayout) {
            mOpeningOtherLayout = false;
            setZabuton(0);
            updateButtons(0);
            setFooterGuide();
        }
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SHUTTER_SPEED);
    }

    public static String getCurrentArea() {
        return GFCommonUtil.getInstance().getPrevArea();
    }

    public static void disable3rdArea() {
        if (GFCommonUtil.getInstance().getPrevArea().equals("Layer3Settings")) {
            GFCommonUtil.getInstance().setPrevArea("SkySettings");
        }
    }

    public static boolean isExpCompPos() {
        return mCurrentFocus == 3;
    }

    public static boolean isAvPos() {
        return mCurrentFocus == 4;
    }

    public static boolean isTvPos() {
        return mCurrentFocus == 5;
    }

    public static boolean isISOPos() {
        return mCurrentFocus == 6;
    }

    public static boolean isWBPos() {
        return mCurrentFocus == 7;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        switch (mCurrentFocus) {
            case 1:
                openPosLinkMenu();
                return 1;
            case 2:
                openShadingLinkMenu();
                return 1;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                openAreaLinkMenu();
                return 1;
            default:
                CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_LINK);
                return 1;
        }
    }

    private int openAreaLinkMenu() {
        openNextMenu("link-area", GFLinkAreaMenuLayout.MENU_ID);
        return 1;
    }

    private int openPosLinkMenu() {
        openNextMenu(GFPositionLinkController.RELATIVE_MODE, SetMenuLayout.MENU_ID);
        return 1;
    }

    private int openShadingLinkMenu() {
        openNextMenu(GFShadingLinkController.RELATIVE_MODE, SetMenuLayout.MENU_ID);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    private boolean isValidPosition() {
        if (mCurrentFocus != 7 || GFWhiteBalanceController.getInstance().getValue().equals(WhiteBalanceController.COLOR_TEMP)) {
            return true;
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        Bundle b = new Bundle();
        if (mCurrentFocus != 7) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 3);
        }
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        Bundle b = new Bundle();
        if (mCurrentFocus != 7) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 4);
        }
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        if (!isValidPosition()) {
            return -1;
        }
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 3);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        if (!isValidPosition()) {
            return -1;
        }
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 4);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        Bundle b = new Bundle();
        if (mCurrentFocus != 7) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 3);
        }
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        Bundle b = new Bundle();
        if (mCurrentFocus != 7) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 3);
        }
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret;
        int code = event.getScanCode();
        if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
            ret = super.onConvertedKeyDown(event, func);
        } else {
            if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
                return 0;
            }
            ret = -1;
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

    private void toggleLink() {
        mDuringToggleLink = true;
        GFLinkAreaController.getInstance().startToggleSetting();
        switch (mCurrentFocus) {
            case 0:
                mDuringToggleLink = false;
                CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_LINK);
                return;
            case 1:
                GFLinkAreaController.getInstance().stopToggleSetting();
                toggleBoundaryPosLink();
                updateBoundaryPosLink();
                GFKikiLogUtil.getInstance().countTogglePos();
                return;
            case 2:
                mDuringToggleLink = false;
                GFLinkAreaController.getInstance().stopToggleSetting();
                toggleBoundaryShadingLink();
                updateBoundaryShadingLink();
                return;
            case 3:
                if (isLand) {
                    toggleLinkValue(GFLinkAreaController.LAND_EXPCOMP);
                } else if (isSky) {
                    toggleLinkValue(GFLinkAreaController.SKY_EXPCOMP);
                } else if (isLayer3) {
                    toggleLinkValue(GFLinkAreaController.LAYER3_EXPCOMP);
                }
                updateExpCompLink();
                GFCommonUtil.getInstance().setEvComp(mSettingLayer);
                GFKikiLogUtil.getInstance().countToggleExpComp();
                return;
            case 4:
                if (isLand) {
                    toggleLinkValue(GFLinkAreaController.LAND_APERTURE);
                } else if (isSky) {
                    toggleLinkValue(GFLinkAreaController.SKY_APERTURE);
                } else if (isLayer3) {
                    toggleLinkValue(GFLinkAreaController.LAYER3_APERTURE);
                }
                updateApertureLink();
                GFCommonUtil.getInstance().setAperture(mSettingLayer, false);
                GFKikiLogUtil.getInstance().countToggleAv();
                return;
            case 5:
                if (isLand) {
                    toggleLinkValue(GFLinkAreaController.LAND_SS);
                } else if (isSky) {
                    toggleLinkValue(GFLinkAreaController.SKY_SS);
                } else if (isLayer3) {
                    toggleLinkValue(GFLinkAreaController.LAYER3_SS);
                }
                updateSSLink();
                GFCommonUtil.getInstance().setShutterSpeed(mSettingLayer);
                GFKikiLogUtil.getInstance().countToggleTv();
                return;
            case 6:
                if (isLand) {
                    toggleLinkValue(GFLinkAreaController.LAND_ISO);
                } else if (isSky) {
                    toggleLinkValue(GFLinkAreaController.SKY_ISO);
                } else if (isLayer3) {
                    toggleLinkValue(GFLinkAreaController.LAYER3_ISO);
                }
                updateIsoLink();
                GFCommonUtil.getInstance().setIso(mSettingLayer);
                GFKikiLogUtil.getInstance().countToggleISO();
                return;
            case 7:
                if (isLand) {
                    toggleLinkValue(GFLinkAreaController.LAND_WB);
                } else if (isSky) {
                    toggleLinkValue(GFLinkAreaController.SKY_WB);
                } else if (isLayer3) {
                    toggleLinkValue(GFLinkAreaController.LAYER3_WB);
                }
                updateWBLink();
                GFCommonUtil.getInstance().setWhiteBalance(mSettingLayer);
                GFKikiLogUtil.getInstance().countToggleWB();
                return;
            default:
                return;
        }
    }

    private void toggleBoundaryPosLink() {
        String value = GFPositionLinkController.getInstance().getValue(GFPositionLinkController.RELATIVE_MODE);
        if (value != null) {
            String nextValue = GFPositionLinkController.OFF;
            int textId = R.string.STRID_FUNC_SKYND_RELATIVE_BOUNDARY_OFF;
            if (GFPositionLinkController.OFF.equals(value)) {
                nextValue = GFPositionLinkController.ON;
                textId = R.string.STRID_FUNC_DF_RELATIVE_BOUNDARY_ON;
            }
            GFPositionLinkController.getInstance().setValue(GFPositionLinkController.RELATIVE_MODE, nextValue);
            switchItemName(textId);
        }
    }

    private void toggleBoundaryShadingLink() {
        String value = GFShadingLinkController.getInstance().getValue(GFShadingLinkController.RELATIVE_MODE);
        if (value != null) {
            String nextValue = GFShadingLinkController.OFF;
            int textId = R.string.STRID_FUNC_DF_DEFOCUS_LINK_OFF;
            if (GFShadingLinkController.OFF.equals(value)) {
                nextValue = GFShadingLinkController.ON;
                textId = R.string.STRID_FUNC_DF_DEFOCUS_LINK_ON;
            }
            GFShadingLinkController.getInstance().setValue(GFShadingLinkController.RELATIVE_MODE, nextValue);
            switchItemName(textId);
        }
    }

    private void toggleLinkValue(String tag) {
        String value = GFLinkAreaController.getInstance().getValue(tag);
        String nextValue = GFLinkAreaController.OFF;
        int textId = R.string.STRID_FUNC_SKYND_LINK_OFF;
        if (GFLinkAreaController.OFF.equals(value)) {
            nextValue = GFLinkAreaController.ON;
            textId = R.string.STRID_FUNC_SKYND_LINK_ON;
        }
        GFLinkAreaController.getInstance().setValue(tag, nextValue);
        switchItemName(textId);
    }

    private void switchItemName(int textId) {
        mLinkTo.setText(textId);
        mLinkTo.setVisibility(0);
        mItemName.setVisibility(8);
        writeBackItemName();
    }

    private void writeBackItemName() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mRunnable);
            mHandler.removeCallbacks(mTitleRunnable);
            mHandler.removeCallbacks(mItemNameRunnable);
        }
        if (mRunnable == null) {
            mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout.3
                @Override // java.lang.Runnable
                public void run() {
                    GFSettingMenuLayout.this.updateItemName();
                }
            };
        }
        mHandler.postDelayed(mRunnable, 3000L);
    }

    private void copyLinkSetting() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        switch (mCurrentFocus) {
            case 3:
                String value = params.getExposureComp(0);
                params.setExposureComp(0, value);
                String value2 = params.getExposureComp(1);
                params.setExposureComp(1, value2);
                String value3 = params.getExposureComp(2);
                params.setExposureComp(2, value3);
                return;
            case 4:
                int aperture = params.getAperture(0);
                params.setAperture(0, aperture);
                int aperture2 = params.getAperture(1);
                params.setAperture(1, aperture2);
                int aperture3 = params.getAperture(2);
                params.setAperture(2, aperture3);
                return;
            case 5:
                int numerator = params.getSSNumerator(0);
                int denominator = params.getSSDenominator(0);
                params.setSSNumerator(0, numerator);
                params.setSSDenominator(0, denominator);
                int numerator2 = params.getSSNumerator(1);
                int denominator2 = params.getSSDenominator(1);
                params.setSSNumerator(1, numerator2);
                params.setSSDenominator(1, denominator2);
                int numerator3 = params.getSSNumerator(2);
                int denominator3 = params.getSSDenominator(2);
                params.setSSNumerator(2, numerator3);
                params.setSSDenominator(2, denominator3);
                return;
            case 6:
                String iso = params.getISO(0);
                params.setISO(0, iso);
                String iso2 = params.getISO(1);
                params.setISO(1, iso2);
                String iso3 = params.getISO(2);
                params.setISO(2, iso3);
                return;
            case 7:
                String wbMode = params.getWBMode(0);
                String wbOption = params.getWBOption(0);
                params.setWBMode(0, wbMode);
                String theme = GFThemeController.getInstance().getValue();
                GFBackUpKey.getInstance().saveWBOption(wbMode, wbOption, 0, theme);
                String wbMode2 = params.getWBMode(1);
                String wbOption2 = params.getWBOption(1);
                params.setWBMode(1, wbMode2);
                GFBackUpKey.getInstance().saveWBOption(wbMode2, wbOption2, 1, theme);
                String wbMode3 = params.getWBMode(2);
                String wbOption3 = params.getWBOption(2);
                params.setWBMode(2, wbMode3);
                GFBackUpKey.getInstance().saveWBOption(wbMode3, wbOption3, 2, theme);
                return;
            default:
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int retVal = 1;
        int code = event.getScanCode();
        if (mDuringToggleLink && code != 595 && code != 513) {
            GFLinkAreaController.getInstance().stopToggleSetting();
            copyLinkSetting();
            mDuringToggleLink = false;
        }
        if (mAreaGuide.getVisibility() == 0) {
            mAreaGuide.setVisibility(4);
            setFooterGuide();
            if (232 == code) {
                return 1;
            }
        }
        switch (code) {
            case 103:
                if (!isFunctionGuideShown()) {
                    if (isLand) {
                        openNextMenu("SkySettings", MENU_ID, false, null);
                    } else if (isSky) {
                        if (GFFilterSetController.THREE_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
                            openNextMenu("Layer3Settings", MENU_ID, false, null);
                        } else if (mOpeningOtherLayout) {
                            getFocusable();
                        }
                    } else if (isLayer3) {
                        if (GFFilterSetController.TWO_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
                            openNextMenu("SkySettings", MENU_ID, false, null);
                        } else if (mOpeningOtherLayout) {
                            getFocusable();
                        }
                    }
                    mOpeningOtherLayout = false;
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                if (!isFunctionGuideShown()) {
                    if (isLayer3 && GFFilterSetController.TWO_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
                        openNextMenu("SkySettings", MENU_ID, false, null);
                    }
                    updateFilterSetIcon();
                    updateAreaIcon();
                    moveFocusToLeft();
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                if (!isFunctionGuideShown()) {
                    if (isLayer3 && GFFilterSetController.TWO_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
                        openNextMenu("SkySettings", MENU_ID, false, null);
                    }
                    updateFilterSetIcon();
                    updateAreaIcon();
                    moveFocusToRight();
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (!isFunctionGuideShown()) {
                    if (isLand) {
                        if (mOpeningOtherLayout) {
                            getFocusable();
                        }
                    } else if (isSky) {
                        openNextMenu(LAND_ID, MENU_ID, false, null);
                    } else if (isLayer3) {
                        openNextMenu("SkySettings", MENU_ID, false, null);
                    }
                    mOpeningOtherLayout = false;
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                if (!isFunctionGuideShown()) {
                    if (!mOpeningOtherLayout) {
                        transitToPreviousMenu();
                        break;
                    } else {
                        updateLinkImage();
                        getFocusable();
                        break;
                    }
                } else {
                    retVal = super.onKeyDown(keyCode, event);
                    break;
                }
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (!isFunctionGuideShown()) {
                    if (!mOpeningOtherLayout) {
                        executeSetting();
                        break;
                    } else if (isLayer3 && GFFilterSetController.TWO_AREAS.equals(GFFilterSetController.getInstance().getValue())) {
                        openNextMenu("SkySettings", MENU_ID, false, null);
                        break;
                    } else {
                        updateFilterSetIcon();
                        updateAreaIcon();
                        updateLinkImage();
                        getFocusable();
                        break;
                    }
                } else {
                    retVal = super.onKeyDown(keyCode, event);
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (!isFunctionGuideShown()) {
                    if (requestWBAdjustment) {
                        String itemId = "WhiteBalance_" + GFWhiteBalanceController.getInstance().getValue();
                        openNextMenu(itemId, GFWhiteBalanceAdjustmentMenuLayout.MENU_ID);
                        requestWBAdjustment = false;
                        break;
                    } else {
                        getFocusable();
                        toggleLink();
                        break;
                    }
                }
                break;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (!isFunctionGuideShown()) {
                    retVal = super.onKeyDown(keyCode, event);
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                retVal = -1;
                break;
            default:
                retVal = super.onKeyDown(keyCode, event);
                break;
        }
        return retVal;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (595 == code || 513 == code) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* loaded from: classes.dex */
    static class ChangeYUVNotifier implements NotificationListener {
        private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

        ChangeYUVNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            GFSettingMenuLayout.mBorderView.invalidate();
        }
    }

    /* loaded from: classes.dex */
    class ChangeCameraNotifier implements NotificationListener {
        ChangeCameraNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return GFSettingMenuLayout.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                GFSettingMenuLayout.mApertureView.setTextColor(GFCommonUtil.getInstance().isAvailableAperture() ? -2236963 : -8947849);
                return;
            }
            if ("ExposureCompensation".equals(tag) || CameraNotificationManager.ISO_SENSITIVITY.equals(tag)) {
                GFSettingMenuLayout.updateExpIconAlpha();
                return;
            }
            if (GFConstants.TAG_GYROSCOPE.equals(tag) || GFConstants.FILTER_SET_CHANGED.equals(tag)) {
                GFSettingMenuLayout.mBorderView.invalidate();
                return;
            }
            if (GFConstants.CLOSE_GUIDE.equals(tag)) {
                if (GFSettingMenuLayout.mAreaGuide != null && GFSettingMenuLayout.isLayer3 && GFCommonUtil.getInstance().needShow3rdAreaSettingGuide()) {
                    GFSettingMenuLayout.mAreaGuide.setVisibility(0);
                    GFCommonUtil.getInstance().setShow3rdAreaSettingFlag(false);
                    BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_3RD_AREA_GUIDE, true);
                    GFSettingMenuLayout.this.setFooterGuide();
                    return;
                }
                return;
            }
            if (GFConstants.CLOSE_15LAYER_BOUNDARY_SETTING.equals(tag)) {
                GFSettingMenuLayout.mGridLine.setVisibility(4);
            }
        }
    }

    public static BorderView getBorderView() {
        return mBorderView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateExpIconAlpha() {
        mExpCompIcon.setAlpha(ExposureCompensationController.getInstance().isExposureCompensationAvailable() ? 255 : INVISIBLE_ALPHA);
    }

    public static int getFocusPosition() {
        return mCurrentFocus;
    }
}
