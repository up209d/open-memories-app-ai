package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.graduatedfilter.shooting.widget.GFWhiteBalanceIcon;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFSettingMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final int BORDER = 0;
    private static final int EXPOSURE = 2;
    public static final String MENU_ID = "ID_GFSETTINGMENULAYOUT";
    private static final int SHADING = 1;
    private static final int WHITEBALANCE = 3;
    private static View mCurrentView = null;
    private static int mCurrentFocus = 0;
    private static ImageView mFocus00 = null;
    private static ImageView mFocus01 = null;
    private static ImageView mFocus02 = null;
    private static ImageView mFocus03 = null;
    private static ImageView[] mFocus = null;
    private static TextView mItemName = null;
    private static TextView mShadingLevel = null;
    private static GFWhiteBalanceIcon mWhiteBalanceIcon = null;
    private static BorderView mBorderView = null;
    private static GFEffectParameters.Parameters mParams = null;
    private final String TAG = AppLog.getClassName();
    private FooterGuide mFooterGuide = null;
    private boolean isLayoutClosed = false;
    NotificationListener mListener = new ChangeYUVNotifier();
    NotificationListener mCameraListener = new ChangeCameraNotifier();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_setting);
        mFocus00 = (ImageView) mCurrentView.findViewById(R.id.focus00);
        mFocus01 = (ImageView) mCurrentView.findViewById(R.id.focus01);
        mFocus02 = (ImageView) mCurrentView.findViewById(R.id.focus02);
        mFocus03 = (ImageView) mCurrentView.findViewById(R.id.focus03);
        mFocus = new ImageView[]{mFocus00, mFocus01, mFocus02, mFocus03};
        mWhiteBalanceIcon = (GFWhiteBalanceIcon) mCurrentView.findViewById(R.id.WhiteBalanceIcon);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        mShadingLevel = (TextView) mCurrentView.findViewById(R.id.option01Value);
        mItemName = (TextView) mCurrentView.findViewById(R.id.item_name);
        this.mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        GFCommonUtil.getInstance().startFilterSetting();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.START_APPSETTING);
        this.isLayoutClosed = false;
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        GFCommonUtil.getInstance().setTransitionFromWBAdjustment(false);
        GFCommonUtil.getInstance().needCTempSetting();
        CameraNotificationManager.getInstance().setNotificationListener(this.mCameraListener);
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        mParams = GFEffectParameters.getInstance().getParameters();
        ImageView[] arr$ = mFocus;
        for (ImageView focus : arr$) {
            focus.setBackgroundResource(17306069);
        }
        updateFocus();
        updateFilter();
        setFooterGuide();
        boolean isShownStep2Guide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_STEP2GUIDE, false);
        if (!isShownStep2Guide) {
            Bundle bundle = new Bundle();
            bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "STEP2");
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (getLayout(GFGuideLayout.ID_GFGUIDELAYOUT) != null && getLayout(GFGuideLayout.ID_GFGUIDELAYOUT).getView() != null) {
            getLayout(GFGuideLayout.ID_GFGUIDELAYOUT).closeLayout();
        }
        mShadingLevel = null;
        this.mFooterGuide = null;
        mCurrentView = null;
        mWhiteBalanceIcon = null;
        mBorderView = null;
        mFocus00 = null;
        mFocus01 = null;
        mFocus02 = null;
        mFocus03 = null;
        mFocus = null;
        CameraNotificationManager.getInstance().removeNotificationListener(this.mCameraListener);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    private static void updateFocus() {
        ImageView[] arr$ = mFocus;
        for (ImageView focus : arr$) {
            focus.setVisibility(4);
        }
        mFocus[mCurrentFocus].setVisibility(0);
    }

    private void updateItemName() {
        switch (mCurrentFocus) {
            case 0:
                mItemName.setText(R.string.STRID_FUNC_SKYND_SKYSETTING_POSITION);
                mItemName.setVisibility(0);
                return;
            case 1:
                mItemName.setText(R.string.STRID_FUNC_SKYND_SKYSETTING_GRADATION);
                mItemName.setVisibility(0);
                return;
            case 2:
                mItemName.setText(R.string.STRID_FUNC_SKYND_EXPOSURE_SKY);
                mItemName.setVisibility(0);
                return;
            case 3:
                mItemName.setText(android.R.string.policydesc_setGlobalProxy);
                mItemName.setVisibility(0);
                return;
            default:
                mItemName.setVisibility(4);
                return;
        }
    }

    private void moveFocusToLeft() {
        mCurrentFocus--;
        if (mCurrentFocus < 0) {
            mCurrentFocus = mFocus.length - 1;
        }
        updateFocus();
        setFooterGuide();
    }

    private void moveFocusToRight() {
        mCurrentFocus++;
        if (mCurrentFocus > mFocus.length - 1) {
            mCurrentFocus = 0;
        }
        updateFocus();
        setFooterGuide();
    }

    private void updateFilter() {
        if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            mShadingLevel.setText(GFCommonUtil.getInstance().getSignedInteger((mParams.getStrength() + 1) - 9));
        } else {
            mShadingLevel.setText(GFCommonUtil.getInstance().getSignedInteger(mParams.getStrength() + 1));
        }
    }

    private void executeSetting() {
        openNextMenuLayout();
    }

    private void openNextMenuLayout() {
        String layoutID = null;
        String itemID = null;
        switch (mCurrentFocus) {
            case 0:
                layoutID = GFBorderMenuLayout.MENU_ID;
                itemID = "gf_border";
                break;
            case 1:
                layoutID = GFShadingMenuLayout.MENU_ID;
                itemID = "gf_shading";
                break;
            case 2:
                layoutID = GFExposureMenuLayout.MENU_ID;
                itemID = "gf_exposure";
                break;
            case 3:
                layoutID = GFWhiteBalanceMenuLayout.MENU_ID;
                itemID = WhiteBalanceController.WHITEBALANCE;
                break;
        }
        if (itemID != null && layoutID != null) {
            closeLayout();
            openNextMenu(itemID, layoutID);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        switch (mCurrentFocus) {
            case 0:
                setGuideResource(guideResources, R.string.STRID_FUNC_SKYND_SKYSETTING_POSITION, R.string.STRID_FUNC_SKYND_SKYSETTINGS_POSITION);
                return;
            case 1:
                setGuideResource(guideResources, R.string.STRID_FUNC_SKYND_SKYSETTING_GRADATION, R.string.STRID_FUNC_SKYND_SKYSETTINGS_GRADATION);
                return;
            case 2:
                setGuideResource(guideResources, R.string.STRID_FUNC_SKYND_EXPOSURE_SKY, R.string.STRID_FUNC_SKYND_SKYSETTING_EXPOSURE_GUIDE);
                return;
            case 3:
                setGuideResource(guideResources, android.R.string.policydesc_setGlobalProxy, R.string.STRID_FUNC_SKYND_SKYSETTING_WHITEBALANCE_GUIDE);
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

    private void setFooterGuide() {
        int id = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_01;
        int idSK = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_01_SK;
        switch (mCurrentFocus) {
            case 0:
            case 1:
            case 2:
            case 3:
                id = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_01;
                idSK = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_01_SK;
                break;
        }
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), id, idSK));
        updateItemName();
    }

    private int transitToPreviousMenu() {
        CameraNotificationManager.getInstance().requestNotify(GFConstants.STOP_APPSETTING);
        GFCommonUtil.getInstance().stopFilterSetting();
        GFCommonUtil.getInstance().setBaseCameraSettings();
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.NoAssign.equals(func) && 595 == event.getScanCode()) {
            transitToPreviousMenu();
            return 1;
        }
        if (CustomizableFunction.Unchanged.equals(func) || CustomizableFunction.Guide.equals(func) || CustomizableFunction.MainPrev.equals(func) || CustomizableFunction.MainNext.equals(func) || CustomizableFunction.SubPrev.equals(func) || CustomizableFunction.SubNext.equals(func) || CustomizableFunction.ThirdPrev.equals(func) || CustomizableFunction.ThirdNext.equals(func) || CustomizableFunction.MfAssist.equals(func)) {
            int ret = super.onConvertedKeyDown(event, func);
            return ret;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (GFCommonUtil.getInstance().isFilterSetting() && (CustomizableFunction.AelHold.equals(func) || CustomizableFunction.AfMfHold.equals(func))) {
            return -1;
        }
        return super.onConvertedKeyUp(event, func);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (this.isLayoutClosed) {
            return -1;
        }
        if (!isFunctionGuideShown()) {
            switch (code) {
                case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    moveFocusToLeft();
                    return 1;
                case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    moveFocusToRight();
                    return 1;
                case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                case AppRoot.USER_KEYCODE.MENU /* 514 */:
                    transitToPreviousMenu();
                    return 1;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    executeSetting();
                    return 1;
                case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
                case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                case 645:
                    return -1;
                default:
                    int retVal = super.onKeyDown(keyCode, event);
                    return retVal;
            }
        }
        int retVal2 = super.onKeyDown(keyCode, event);
        return retVal2;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (595 == code || 513 == code) {
            return -1;
        }
        if ((code == 530 || code == 655) && !GFCommonUtil.getInstance().hasIrisRing() && !GFCommonUtil.getInstance().isFixedAperture() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
            GFCommonUtil.getInstance().setAperture(false);
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
            GFSettingMenuLayout.mWhiteBalanceIcon.refreshIcon();
        }
    }

    /* loaded from: classes.dex */
    static class ChangeCameraNotifier implements NotificationListener {
        private static final String[] tags = {CameraNotificationManager.DEVICE_LENS_CHANGED};

        ChangeCameraNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (!GFCommonUtil.getInstance().hasIrisRing() && !GFCommonUtil.getInstance().isFixedAperture()) {
                if (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual()) {
                    GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
                    int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), params.getAperture(false));
                    if (step != 0) {
                        GFCommonUtil.getInstance().setAperture(false);
                    }
                }
            }
        }
    }
}
