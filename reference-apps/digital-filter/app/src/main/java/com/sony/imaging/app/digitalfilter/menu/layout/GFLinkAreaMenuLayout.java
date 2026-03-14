package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.RunStatus;
import java.lang.reflect.Array;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFLinkAreaMenuLayout extends DisplayMenuItemsMenuLayout {
    public static final String MENU_ID = "ID_GFLINKAREAMENULAYOUT";
    private static boolean isAllBtn;
    private static boolean isCancel;
    private static boolean isCancelBtn;
    private static boolean isOKBtn;
    private static TextView[] mAllButton;
    private static TextView mAvTitle;
    private static TextView mCancelButton;
    private static ImageView mExpCompTitle;
    private static TextView[][] mFocusView;
    private static TextView mISOTitle;
    private static ImageView[][] mLinkParent;
    private static ImageView[][] mLinkStatusView;
    private static String[][] mLinkTag;
    private static TextView mOKButton;
    private static String[] mOriginalParents;
    private static String[][] mOriginalValue;
    private static String[] mSettingParents;
    private static String[][] mSettingValue;
    private static TextView mTvTitle;
    private static TextView mWBTitle;
    private final String TAG = AppLog.getClassName();
    private static int EXP = 0;
    private static int AV = 1;
    private static int TV = 2;
    private static int ISO = 3;
    private static int WB = 4;
    private static int LAYER3 = 0;
    private static int SKY = 1;
    private static int LAND = 2;
    private static int mFocusX = 0;
    private static int mFocusY = 0;
    private static GFLinkAreaController mLinkCtrl = null;
    private static GFBackUpKey mGFBackUpKey = null;
    private static String mCommonExpComp = null;
    private static int mCommonAperture = 0;
    private static int mCommonSsNumerator = 0;
    private static int mCommonSsDenominator = 0;
    private static String mCommonISO = null;
    private static String mCommonWB = null;
    private static String mCommonWBOption = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mLinkCtrl = GFLinkAreaController.getInstance();
        mGFBackUpKey = GFBackUpKey.getInstance();
        View currentView = (ViewGroup) obtainViewFromPool(R.layout.menu_link);
        mOriginalValue = (String[][]) Array.newInstance((Class<?>) String.class, 3, 5);
        mSettingValue = (String[][]) Array.newInstance((Class<?>) String.class, 3, 5);
        mOriginalParents = new String[5];
        mSettingParents = new String[5];
        mLinkTag = new String[3];
        String[][] strArr = mLinkTag;
        int i = LAYER3;
        String[] strArr2 = new String[5];
        strArr2[0] = GFLinkAreaController.LAYER3_EXPCOMP;
        strArr2[1] = GFLinkAreaController.LAYER3_APERTURE;
        strArr2[2] = GFLinkAreaController.LAYER3_SS;
        strArr2[3] = GFLinkAreaController.LAYER3_ISO;
        strArr2[4] = GFLinkAreaController.LAYER3_WB;
        strArr[i] = strArr2;
        String[][] strArr3 = mLinkTag;
        int i2 = SKY;
        String[] strArr4 = new String[5];
        strArr4[0] = GFLinkAreaController.SKY_EXPCOMP;
        strArr4[1] = GFLinkAreaController.SKY_APERTURE;
        strArr4[2] = GFLinkAreaController.SKY_SS;
        strArr4[3] = GFLinkAreaController.SKY_ISO;
        strArr4[4] = GFLinkAreaController.SKY_WB;
        strArr3[i2] = strArr4;
        String[][] strArr5 = mLinkTag;
        int i3 = LAND;
        String[] strArr6 = new String[5];
        strArr6[0] = GFLinkAreaController.LAND_EXPCOMP;
        strArr6[1] = GFLinkAreaController.LAND_APERTURE;
        strArr6[2] = GFLinkAreaController.LAND_SS;
        strArr6[3] = GFLinkAreaController.LAND_ISO;
        strArr6[4] = GFLinkAreaController.LAND_WB;
        strArr5[i3] = strArr6;
        mFocusView = new TextView[3];
        TextView[][] textViewArr = mFocusView;
        int i4 = LAYER3;
        TextView[] textViewArr2 = new TextView[5];
        textViewArr2[0] = (TextView) currentView.findViewById(R.id.exp).findViewById(R.id.focus0);
        textViewArr2[1] = (TextView) currentView.findViewById(R.id.av).findViewById(R.id.focus0);
        textViewArr2[2] = (TextView) currentView.findViewById(R.id.tv).findViewById(R.id.focus0);
        textViewArr2[3] = (TextView) currentView.findViewById(R.id.iso).findViewById(R.id.focus0);
        textViewArr2[4] = (TextView) currentView.findViewById(R.id.wb).findViewById(R.id.focus0);
        textViewArr[i4] = textViewArr2;
        TextView[][] textViewArr3 = mFocusView;
        int i5 = SKY;
        TextView[] textViewArr4 = new TextView[5];
        textViewArr4[0] = (TextView) currentView.findViewById(R.id.exp).findViewById(R.id.focus1);
        textViewArr4[1] = (TextView) currentView.findViewById(R.id.av).findViewById(R.id.focus1);
        textViewArr4[2] = (TextView) currentView.findViewById(R.id.tv).findViewById(R.id.focus1);
        textViewArr4[3] = (TextView) currentView.findViewById(R.id.iso).findViewById(R.id.focus1);
        textViewArr4[4] = (TextView) currentView.findViewById(R.id.wb).findViewById(R.id.focus1);
        textViewArr3[i5] = textViewArr4;
        TextView[][] textViewArr5 = mFocusView;
        int i6 = LAND;
        TextView[] textViewArr6 = new TextView[5];
        textViewArr6[0] = (TextView) currentView.findViewById(R.id.exp).findViewById(R.id.focus2);
        textViewArr6[1] = (TextView) currentView.findViewById(R.id.av).findViewById(R.id.focus2);
        textViewArr6[2] = (TextView) currentView.findViewById(R.id.tv).findViewById(R.id.focus2);
        textViewArr6[3] = (TextView) currentView.findViewById(R.id.iso).findViewById(R.id.focus2);
        textViewArr6[4] = (TextView) currentView.findViewById(R.id.wb).findViewById(R.id.focus2);
        textViewArr5[i6] = textViewArr6;
        mLinkStatusView = new ImageView[3];
        ImageView[][] imageViewArr = mLinkStatusView;
        int i7 = LAYER3;
        ImageView[] imageViewArr2 = new ImageView[5];
        imageViewArr2[0] = (ImageView) currentView.findViewById(R.id.exp).findViewById(R.id.check_box0);
        imageViewArr2[1] = (ImageView) currentView.findViewById(R.id.av).findViewById(R.id.check_box0);
        imageViewArr2[2] = (ImageView) currentView.findViewById(R.id.tv).findViewById(R.id.check_box0);
        imageViewArr2[3] = (ImageView) currentView.findViewById(R.id.iso).findViewById(R.id.check_box0);
        imageViewArr2[4] = (ImageView) currentView.findViewById(R.id.wb).findViewById(R.id.check_box0);
        imageViewArr[i7] = imageViewArr2;
        ImageView[][] imageViewArr3 = mLinkStatusView;
        int i8 = SKY;
        ImageView[] imageViewArr4 = new ImageView[5];
        imageViewArr4[0] = (ImageView) currentView.findViewById(R.id.exp).findViewById(R.id.check_box1);
        imageViewArr4[1] = (ImageView) currentView.findViewById(R.id.av).findViewById(R.id.check_box1);
        imageViewArr4[2] = (ImageView) currentView.findViewById(R.id.tv).findViewById(R.id.check_box1);
        imageViewArr4[3] = (ImageView) currentView.findViewById(R.id.iso).findViewById(R.id.check_box1);
        imageViewArr4[4] = (ImageView) currentView.findViewById(R.id.wb).findViewById(R.id.check_box1);
        imageViewArr3[i8] = imageViewArr4;
        ImageView[][] imageViewArr5 = mLinkStatusView;
        int i9 = LAND;
        ImageView[] imageViewArr6 = new ImageView[5];
        imageViewArr6[0] = (ImageView) currentView.findViewById(R.id.exp).findViewById(R.id.check_box2);
        imageViewArr6[1] = (ImageView) currentView.findViewById(R.id.av).findViewById(R.id.check_box2);
        imageViewArr6[2] = (ImageView) currentView.findViewById(R.id.tv).findViewById(R.id.check_box2);
        imageViewArr6[3] = (ImageView) currentView.findViewById(R.id.iso).findViewById(R.id.check_box2);
        imageViewArr6[4] = (ImageView) currentView.findViewById(R.id.wb).findViewById(R.id.check_box2);
        imageViewArr5[i9] = imageViewArr6;
        mLinkParent = new ImageView[3];
        ImageView[][] imageViewArr7 = mLinkParent;
        int i10 = LAYER3;
        ImageView[] imageViewArr8 = new ImageView[5];
        imageViewArr8[0] = (ImageView) currentView.findViewById(R.id.exp).findViewById(R.id.parent0);
        imageViewArr8[1] = (ImageView) currentView.findViewById(R.id.av).findViewById(R.id.parent0);
        imageViewArr8[2] = (ImageView) currentView.findViewById(R.id.tv).findViewById(R.id.parent0);
        imageViewArr8[3] = (ImageView) currentView.findViewById(R.id.iso).findViewById(R.id.parent0);
        imageViewArr8[4] = (ImageView) currentView.findViewById(R.id.wb).findViewById(R.id.parent0);
        imageViewArr7[i10] = imageViewArr8;
        ImageView[][] imageViewArr9 = mLinkParent;
        int i11 = SKY;
        ImageView[] imageViewArr10 = new ImageView[5];
        imageViewArr10[0] = (ImageView) currentView.findViewById(R.id.exp).findViewById(R.id.parent1);
        imageViewArr10[1] = (ImageView) currentView.findViewById(R.id.av).findViewById(R.id.parent1);
        imageViewArr10[2] = (ImageView) currentView.findViewById(R.id.tv).findViewById(R.id.parent1);
        imageViewArr10[3] = (ImageView) currentView.findViewById(R.id.iso).findViewById(R.id.parent1);
        imageViewArr10[4] = (ImageView) currentView.findViewById(R.id.wb).findViewById(R.id.parent1);
        imageViewArr9[i11] = imageViewArr10;
        ImageView[][] imageViewArr11 = mLinkParent;
        int i12 = LAND;
        ImageView[] imageViewArr12 = new ImageView[5];
        imageViewArr12[0] = (ImageView) currentView.findViewById(R.id.exp).findViewById(R.id.parent2);
        imageViewArr12[1] = (ImageView) currentView.findViewById(R.id.av).findViewById(R.id.parent2);
        imageViewArr12[2] = (ImageView) currentView.findViewById(R.id.tv).findViewById(R.id.parent2);
        imageViewArr12[3] = (ImageView) currentView.findViewById(R.id.iso).findViewById(R.id.parent2);
        imageViewArr12[4] = (ImageView) currentView.findViewById(R.id.wb).findViewById(R.id.parent2);
        imageViewArr11[i12] = imageViewArr12;
        mExpCompTitle = (ImageView) currentView.findViewById(R.id.exp).findViewById(R.id.item_icon);
        mAvTitle = (TextView) currentView.findViewById(R.id.av).findViewById(R.id.item_name);
        mTvTitle = (TextView) currentView.findViewById(R.id.tv).findViewById(R.id.item_name);
        mISOTitle = (TextView) currentView.findViewById(R.id.iso).findViewById(R.id.item_name);
        mWBTitle = (TextView) currentView.findViewById(R.id.wb).findViewById(R.id.item_name);
        mExpCompTitle.setImageResource(R.drawable.p_16_dd_parts_hgf_exposurecomp_normal);
        mAvTitle.setText("Av");
        mTvTitle.setText("Tv");
        mISOTitle.setText("ISO");
        mWBTitle.setText("WB");
        mAllButton = new TextView[]{(TextView) currentView.findViewById(R.id.exp).findViewById(R.id.toggle_all), (TextView) currentView.findViewById(R.id.av).findViewById(R.id.toggle_all), (TextView) currentView.findViewById(R.id.tv).findViewById(R.id.toggle_all), (TextView) currentView.findViewById(R.id.iso).findViewById(R.id.toggle_all), (TextView) currentView.findViewById(R.id.wb).findViewById(R.id.toggle_all)};
        mOKButton = (TextView) currentView.findViewById(R.id.button_ok);
        mCancelButton = (TextView) currentView.findViewById(R.id.button_cancel);
        if (GFSettingMenuLayout.isExpCompPos()) {
            mFocusX = 0;
        } else if (GFSettingMenuLayout.isAvPos()) {
            mFocusX = 1;
        } else if (GFSettingMenuLayout.isTvPos()) {
            mFocusX = 2;
        } else if (GFSettingMenuLayout.isISOPos()) {
            mFocusX = 3;
        } else if (GFSettingMenuLayout.isWBPos()) {
            mFocusX = 4;
        }
        if (GFSettingMenuLayout.getCurrentArea().equals("Layer3Settings")) {
            mFocusY = 0;
        } else if (GFSettingMenuLayout.getCurrentArea().equals("SkySettings")) {
            mFocusY = 1;
        } else if (GFSettingMenuLayout.getCurrentArea().equals(GFSettingMenuLayout.LAND_ID)) {
            mFocusY = 2;
        }
        checkStatus();
        isOKBtn = false;
        isCancelBtn = false;
        isCancel = true;
        return currentView;
    }

    private void checkStatus() {
        for (int layer = 0; layer < 3; layer++) {
            for (int item = 0; item < 5; item++) {
                String tag = mLinkTag[layer][item];
                mOriginalValue[layer][item] = mLinkCtrl.getValue(tag);
            }
        }
        String theme = GFThemeController.getInstance().getValue();
        mOriginalParents[EXP] = mGFBackUpKey.getParentExpComp(theme);
        mOriginalParents[AV] = mGFBackUpKey.getParentAperture(theme);
        mOriginalParents[TV] = mGFBackUpKey.getParentSS(theme);
        mOriginalParents[ISO] = mGFBackUpKey.getParentISO(theme);
        mOriginalParents[WB] = mGFBackUpKey.getParentWB(theme);
        mCommonExpComp = mGFBackUpKey.getCommonExpComp(theme);
        mCommonAperture = mGFBackUpKey.getCommonAperture(theme);
        mCommonSsNumerator = mGFBackUpKey.getCommonSsNumerator(theme);
        mCommonSsDenominator = mGFBackUpKey.getCommonSsDenominator(theme);
        mCommonISO = mGFBackUpKey.getCommonISO(theme);
        mCommonWB = mGFBackUpKey.getCommonWB(theme);
        mCommonWBOption = mGFBackUpKey.getCommonWBOption(theme);
    }

    private void storeStatus() {
        for (int layer = 0; layer < 3; layer++) {
            for (int item = 0; item < 5; item++) {
                String tag = mLinkTag[layer][item];
                mSettingValue[layer][item] = mLinkCtrl.getValue(tag);
            }
        }
        String theme = GFThemeController.getInstance().getValue();
        mSettingParents[EXP] = mGFBackUpKey.getParentExpComp(theme);
        mSettingParents[AV] = mGFBackUpKey.getParentAperture(theme);
        mSettingParents[TV] = mGFBackUpKey.getParentSS(theme);
        mSettingParents[ISO] = mGFBackUpKey.getParentISO(theme);
        mSettingParents[WB] = mGFBackUpKey.getParentWB(theme);
    }

    private void updateValues() {
        String[] arr$ = GFLinkAreaController.getInstance().getLinkTags();
        for (String tag : arr$) {
            GFLinkAreaController.getInstance().setValue(tag, GFLinkAreaController.OFF);
        }
        for (int item = 0; item < 5; item++) {
            String tag2 = mSettingParents[item];
            if (!tag2.equals("none")) {
                GFLinkAreaController.getInstance().setValue(tag2, GFLinkAreaController.ON);
            }
        }
        for (int layer = 0; layer < 3; layer++) {
            for (int item2 = 0; item2 < 5; item2++) {
                GFLinkAreaController.getInstance().setValue(mLinkTag[layer][item2], mSettingValue[layer][item2]);
            }
        }
        copyLinkSetting();
    }

    private void copyLinkSetting() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        String value = params.getExposureComp(0);
        params.setExposureComp(0, value);
        String value2 = params.getExposureComp(1);
        params.setExposureComp(1, value2);
        String value3 = params.getExposureComp(2);
        params.setExposureComp(2, value3);
        String iso = params.getISO(0);
        params.setISO(0, iso);
        String iso2 = params.getISO(1);
        params.setISO(1, iso2);
        String iso3 = params.getISO(2);
        params.setISO(2, iso3);
        int aperture = params.getAperture(0);
        params.setAperture(0, aperture);
        int aperture2 = params.getAperture(1);
        params.setAperture(1, aperture2);
        int aperture3 = params.getAperture(2);
        params.setAperture(2, aperture3);
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
    }

    private void toggleStatus(int layer, int item) {
        String tag = mLinkTag[layer][item];
        String value = mLinkCtrl.getValue(tag);
        if (value.equals(GFLinkAreaController.ON)) {
            mLinkCtrl.setValue(tag, GFLinkAreaController.OFF);
            mLinkStatusView[layer][item].setBackgroundResource(17306786);
        } else {
            mLinkCtrl.setValue(tag, GFLinkAreaController.ON);
            mLinkStatusView[layer][item].setBackgroundResource(17306383);
        }
    }

    private void changeAllStatus(int item) {
        String tag = mLinkTag[LAYER3][item];
        String layer3Value = mLinkCtrl.getValue(tag);
        String tag2 = mLinkTag[SKY][item];
        String skyValue = mLinkCtrl.getValue(tag2);
        String tag3 = mLinkTag[LAND][item];
        String landValue = mLinkCtrl.getValue(tag3);
        if (landValue.equals(GFLinkAreaController.ON) && skyValue.equals(GFLinkAreaController.ON) && layer3Value.equals(GFLinkAreaController.ON)) {
            toggleStatus(LAND, item);
            toggleStatus(SKY, item);
            toggleStatus(LAYER3, item);
        } else {
            if (landValue.equals(GFLinkAreaController.OFF)) {
                toggleStatus(LAND, item);
            }
            if (skyValue.equals(GFLinkAreaController.OFF)) {
                toggleStatus(SKY, item);
            }
            if (layer3Value.equals(GFLinkAreaController.OFF)) {
                toggleStatus(LAYER3, item);
            }
        }
        updateFocus();
    }

    private void updateCheckBox() {
        for (int layer = 0; layer < 3; layer++) {
            for (int item = 0; item < 5; item++) {
                String tag = mLinkTag[layer][item];
                String value = mLinkCtrl.getValue(tag);
                if (value.equals(GFLinkAreaController.ON)) {
                    mLinkStatusView[layer][item].setBackgroundResource(17306383);
                } else {
                    mLinkStatusView[layer][item].setBackgroundResource(17306786);
                }
            }
        }
    }

    private void updateParents() {
        String area = null;
        for (int layer = 0; layer < 3; layer++) {
            if (layer == 0) {
                area = GFLinkAreaController.LAYER3;
            } else if (layer == 1) {
                area = GFLinkAreaController.SKY;
            } else if (layer == 2) {
                area = GFLinkAreaController.LAND;
            }
            String theme = GFThemeController.getInstance().getValue();
            if (GFLinkAreaController.getInstance().isExpCompParent(area, theme)) {
                mLinkParent[layer][EXP].setVisibility(0);
            } else {
                mLinkParent[layer][EXP].setVisibility(4);
            }
            if (GFLinkAreaController.getInstance().isApertureParent(area, theme)) {
                mLinkParent[layer][AV].setVisibility(0);
            } else {
                mLinkParent[layer][AV].setVisibility(4);
            }
            if (GFLinkAreaController.getInstance().isSSParent(area, theme)) {
                mLinkParent[layer][TV].setVisibility(0);
            } else {
                mLinkParent[layer][TV].setVisibility(4);
            }
            if (GFLinkAreaController.getInstance().isISOParent(area, theme)) {
                mLinkParent[layer][ISO].setVisibility(0);
            } else {
                mLinkParent[layer][ISO].setVisibility(4);
            }
            if (GFLinkAreaController.getInstance().isWBParent(area, theme)) {
                mLinkParent[layer][WB].setVisibility(0);
            } else {
                mLinkParent[layer][WB].setVisibility(4);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        GFKikiLogUtil.getInstance().countExpWBLinkSetting();
        GFLinkAreaController.getInstance().startToggleSetting();
        super.onResume();
        updateCheckBox();
        updateParents();
        updateFocus();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        GFLinkAreaController.getInstance().stopToggleSetting();
        if (!isCancel) {
            storeStatus();
            updateValues();
            int runStatus = RunStatus.getStatus();
            if (2 != runStatus) {
                reloadImages();
            }
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        for (int layer = 0; layer < 3; layer++) {
            for (int item = 0; item < 5; item++) {
                mFocusView[layer][item] = null;
                mLinkStatusView[layer][item] = null;
                mLinkParent[layer][item] = null;
                mOriginalValue[layer][item] = null;
                mSettingValue[layer][item] = null;
                mLinkTag[layer][item] = null;
            }
            mFocusView[layer] = null;
            mLinkStatusView[layer] = null;
            mLinkParent[layer] = null;
            mOriginalValue[layer] = null;
            mSettingValue[layer] = null;
            mLinkTag[layer] = null;
        }
        mFocusView = (TextView[][]) null;
        mLinkStatusView = (ImageView[][]) null;
        mLinkParent = (ImageView[][]) null;
        mOriginalValue = (String[][]) null;
        mSettingValue = (String[][]) null;
        mLinkTag = (String[][]) null;
        for (int item2 = 0; item2 < 5; item2++) {
            mAllButton[item2] = null;
            mOriginalParents[item2] = null;
            mSettingParents[item2] = null;
        }
        mAllButton = null;
        mOriginalParents = null;
        mSettingParents = null;
        mOKButton = null;
        mCancelButton = null;
        mExpCompTitle = null;
        mAvTitle = null;
        mTvTitle = null;
        mISOTitle = null;
        mWBTitle = null;
        mOKButton = null;
        mLinkCtrl = null;
        mGFBackUpKey = null;
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        setGuideResource(guideResources, R.string.STRID_FUNC_DF_EXP_WB_LINK, R.string.STRID_FUNC_DF_EXP_WB_LINK_GUIDE);
    }

    private void setGuideResource(ArrayList<Object> guideResources, int guideTitleID, int guideDefi) {
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(true);
    }

    private boolean isLayerChangedToLink(int layer) {
        for (int item = 0; item < 5; item++) {
            String tag = mLinkTag[layer][item];
            String value = mLinkCtrl.getValue(tag);
            if (value.equals(GFLinkAreaController.ON) && !value.equals(mOriginalValue[layer][item])) {
                return true;
            }
        }
        return false;
    }

    private void reloadImages() {
        if (GFCommonUtil.getInstance().isLand()) {
            if (isLayerChangedToLink(SKY)) {
                CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_SKY_IMAGE);
                return;
            } else {
                if (isLayerChangedToLink(LAND)) {
                    GFCommonUtil.getInstance().setCameraSettings(GFCommonUtil.getInstance().getSettingLayer(), false);
                    return;
                }
                return;
            }
        }
        if (GFCommonUtil.getInstance().isSky()) {
            if (isLayerChangedToLink(LAND)) {
                CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_LAND_IMAGE);
                return;
            } else {
                if (isLayerChangedToLink(SKY)) {
                    GFCommonUtil.getInstance().setCameraSettings(GFCommonUtil.getInstance().getSettingLayer(), false);
                    return;
                }
                return;
            }
        }
        if (GFCommonUtil.getInstance().isLayer3()) {
            if (isLayerChangedToLink(LAND) && isLayerChangedToLink(SKY)) {
                CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
            } else if (isLayerChangedToLink(LAYER3)) {
                GFCommonUtil.getInstance().setCameraSettings(GFCommonUtil.getInstance().getSettingLayer(), false);
            }
        }
    }

    private void moveToTop() {
        if (isCancelBtn) {
            isCancelBtn = false;
            isOKBtn = true;
        } else if (isOKBtn) {
            isOKBtn = false;
            isAllBtn = true;
        } else if (isAllBtn) {
            isAllBtn = false;
            mFocusY = LAND;
        } else if (mFocusY > LAYER3) {
            mFocusY--;
        } else {
            isCancelBtn = true;
        }
        updateFocus();
    }

    private void moveToBottom() {
        if (isAllBtn) {
            isAllBtn = false;
            isOKBtn = true;
        } else if (isOKBtn) {
            isOKBtn = false;
            isCancelBtn = true;
        } else if (isCancelBtn) {
            isCancelBtn = false;
            mFocusY = LAYER3;
        } else if (mFocusY < LAND) {
            mFocusY++;
        } else {
            isAllBtn = true;
        }
        updateFocus();
    }

    private void moveToRight() {
        if (!isOKBtn && !isCancelBtn) {
            if (mFocusX < WB) {
                mFocusX++;
            } else {
                mFocusX = EXP;
            }
        }
        updateFocus();
    }

    private void moveToLeft() {
        if (!isOKBtn && !isCancelBtn) {
            if (mFocusX > EXP) {
                mFocusX--;
            } else {
                mFocusX = WB;
            }
        }
        updateFocus();
    }

    private void updateFocus() {
        mOKButton.setSelected(isOKBtn);
        mCancelButton.setSelected(isCancelBtn);
        for (int item = 0; item < 5; item++) {
            mAllButton[item].setSelected(false);
        }
        if (isAllBtn) {
            mAllButton[mFocusX].setSelected(isAllBtn);
        }
        for (int layer = 0; layer < 3; layer++) {
            for (int item2 = 0; item2 < 5; item2++) {
                mFocusView[layer][item2].setSelected(false);
                mFocusView[layer][item2].setVisibility(4);
            }
        }
        if (!isAllBtn && !isOKBtn && !isCancelBtn) {
            mFocusView[mFocusY][mFocusX].setSelected(true);
            mFocusView[mFocusY][mFocusX].setVisibility(0);
        }
    }

    private void handleCenterKey() {
        if (isCancelBtn) {
            cancelValue();
            openPreviousMenu();
        } else if (isOKBtn) {
            isCancel = false;
            openPreviousMenu();
        } else if (isAllBtn) {
            changeAllStatus(mFocusX);
            updateParents();
        } else {
            toggleStatus(mFocusY, mFocusX);
            updateParents();
        }
    }

    private void cancelValue() {
        for (int layer = 0; layer < 3; layer++) {
            for (int item = 0; item < 5; item++) {
                String tag = mLinkTag[layer][item];
                mLinkCtrl.setValue(tag, mOriginalValue[layer][item]);
            }
        }
        String theme = GFThemeController.getInstance().getValue();
        mGFBackUpKey.setCommonExpComp(mCommonExpComp, theme);
        mGFBackUpKey.setCommonAperture(mCommonAperture, theme);
        mGFBackUpKey.setCommonSsNumerator(mCommonSsNumerator, theme);
        mGFBackUpKey.setCommonSsDenominator(mCommonSsDenominator, theme);
        mGFBackUpKey.setCommonISO(mCommonISO, theme);
        mGFBackUpKey.setCommonWB(mCommonWB, theme);
        mGFBackUpKey.setCommonWBOption(mCommonWBOption, theme);
        mGFBackUpKey.setParentExpComp(mOriginalParents[EXP], theme);
        mGFBackUpKey.setParentAperture(mOriginalParents[AV], theme);
        mGFBackUpKey.setParentSS(mOriginalParents[TV], theme);
        mGFBackUpKey.setParentISO(mOriginalParents[ISO], theme);
        mGFBackUpKey.setParentWB(mOriginalParents[WB], theme);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        isCancel = false;
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCancel = true;
        cancelValue();
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret;
        int code = event.getScanCode();
        if (GFConstants.SettingLayerCustomizableFunction.contains(func)) {
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        isCancel = true;
        switch (code) {
            case 103:
                if (!isFunctionGuideShown()) {
                    moveToTop();
                }
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                if (!isFunctionGuideShown()) {
                    moveToLeft();
                }
                return 1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                if (!isFunctionGuideShown()) {
                    moveToRight();
                }
                return 1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (!isFunctionGuideShown()) {
                    moveToBottom();
                }
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (!isFunctionGuideShown()) {
                    handleCenterKey();
                    return 1;
                }
                int result = super.onKeyDown(keyCode, event);
                return result;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                return -1;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                isCancel = isCancelBtn;
                int result2 = super.onKeyDown(keyCode, event);
                return result2;
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                if (!isFunctionGuideShown()) {
                    if (GFCommonUtil.getInstance().isOneDial()) {
                        moveToBottom();
                    } else {
                        moveToRight();
                    }
                }
                return 1;
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                if (!isFunctionGuideShown()) {
                    if (GFCommonUtil.getInstance().isOneDial()) {
                        moveToTop();
                    } else {
                        moveToLeft();
                    }
                }
                return 1;
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                if (!isFunctionGuideShown()) {
                    if (GFCommonUtil.getInstance().isTwoDial()) {
                        moveToBottom();
                    } else {
                        moveToRight();
                    }
                }
                return 1;
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                if (!isFunctionGuideShown()) {
                    if (GFCommonUtil.getInstance().isTwoDial()) {
                        moveToTop();
                    } else {
                        moveToLeft();
                    }
                }
                return 1;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                return -1;
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (!isFunctionGuideShown()) {
                    moveToTop();
                }
                return 1;
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (!isFunctionGuideShown()) {
                    moveToBottom();
                }
                return 1;
            case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                return -1;
            default:
                int result3 = super.onKeyDown(keyCode, event);
                return result3;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 653) {
            return -1;
        }
        if (code == 232 || code == 595) {
            return 1;
        }
        return super.onKeyUp(keyCode, event);
    }
}
