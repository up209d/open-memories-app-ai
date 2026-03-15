package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.os.Handler;
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
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.base.menu.layout.ISOSpecialScreenMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppContext;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.SaUtil;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.sa.SFRSA;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.graduatedfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFISOSpecialScreenMenuLayout extends ISOSpecialScreenMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFISOSPECIALSCREENMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private static ImageView mDeleteButton = null;
    private static ImageView mDeleteButtonDisp = null;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static FooterGuide mFooterGuide = null;
    private static BorderView mBorderView = null;
    private static boolean isHistgramView = false;
    private static boolean isBaseSetting = false;
    private static TextView mTitle = null;
    private static Handler myHandler = null;
    private static Runnable myRunnable = null;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup currentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        isBaseSetting = !GFCommonUtil.getInstance().isFilterSetting();
        mTitle = (TextView) currentView.findViewById(R.id.menu_screen_title);
        mFooterGuide = (FooterGuide) currentView.findViewById(R.id.footer_guide);
        mGFGraphViewFilter = (GFGraphView) currentView.findViewById(R.id.filter_histgram);
        if (isBaseSetting) {
            mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.earth_histgram);
        } else {
            mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.base_histgram);
        }
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (isBaseSetting) {
            startLiveveiwEffect();
            CameraNotificationManager.getInstance().requestNotify(GFConstants.START_BASESETTING);
        }
        mDeleteButton = (ImageView) currentView.findViewById(R.id.delete_button);
        if (SaUtil.isAVIP()) {
            mDeleteButton.setBackgroundResource(R.drawable.p_16_dd_parts_key_sk2);
        } else {
            mDeleteButton.setBackgroundResource(R.drawable.p_16_dd_parts_key_delete);
        }
        mDeleteButtonDisp = (ImageView) currentView.findViewById(R.id.delete_button_disp);
        setButtonVisibility(0);
        GFHistgramTask.getInstance().startHistgramUpdating();
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        if (!isHistgramView) {
            disappearButtonView();
            mGFGraphViewFilter.setVisibility(8);
            mGFGraphViewBase.setVisibility(8);
        } else if (isBaseSetting) {
            mGFGraphViewFilter.setVisibility(8);
            mGFGraphViewBase.setVisibility(0);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        IFooterGuideData data = new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.autofill_phone_suffix_separator_re, android.R.string.hardware);
        mFooterGuide.setData(data);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (isBaseSetting) {
            mTitle.setText(R.string.STRID_FUNC_SKYND_ISO_EARTH);
        } else {
            mTitle.setText(R.string.STRID_FUNC_SKYND_ISO_SKY);
        }
        DisplayModeObserver.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        mBorderView = null;
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler = null;
        }
        myRunnable = null;
        mGFGraphViewFilter.setVisibility(8);
        mGFGraphViewBase.setVisibility(8);
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mFooterGuide = null;
        mDeleteButton.setVisibility(8);
        mDeleteButtonDisp.setVisibility(8);
        mDeleteButton = null;
        mDeleteButtonDisp = null;
        mTitle = null;
        super.onPause();
    }

    private void startLiveveiwEffect() {
        if (!SFRSA.getInstance().isAvailableHistgram()) {
            SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
            SFRSA.getInstance().initialize();
            SFRSA.getInstance().setCommand(16);
            SFRSA.getInstance().startLiveViewEffect(false);
        }
    }

    private void toggleDispMode() {
        if (!isHistgramView) {
            isHistgramView = true;
            setButtonVisibility(0);
            mGFGraphViewBase.setVisibility(0);
            if (!isBaseSetting) {
                mGFGraphViewFilter.setVisibility(0);
            }
        } else {
            isHistgramView = false;
            disappearButtonView();
            mGFGraphViewFilter.setVisibility(8);
            mGFGraphViewBase.setVisibility(8);
        }
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setButtonVisibility(int visibility) {
        mDeleteButton.setVisibility(visibility);
        mDeleteButtonDisp.setVisibility(visibility);
    }

    private void disappearButtonView() {
        if (myHandler == null) {
            myHandler = new Handler();
        }
        if (myRunnable == null) {
            myRunnable = new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.menu.layout.GFISOSpecialScreenMenuLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!GFISOSpecialScreenMenuLayout.isHistgramView) {
                        GFISOSpecialScreenMenuLayout.this.setButtonVisibility(4);
                    }
                }
            };
        }
        myHandler.postDelayed(myRunnable, 3000L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (SaUtil.isAVIP() && itemid.equalsIgnoreCase("Iso_0") && ExposureModeController.MANUAL_MODE.equalsIgnoreCase(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE))) {
            CautionUtilityClass.getInstance().requestTrigger(1470);
        } else if (GFCommonUtil.getInstance().isFilterSetting()) {
            AppLog.info(TAG, "ISO: Filter Setting");
            openPreviousMenu();
        } else {
            AppLog.info(TAG, "ISO: Base Setting");
            super.doItemClickProcessing(itemid);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setISO(isBaseSetting, ISOSensitivityController.getInstance().getValue());
        super.closeLayout();
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret;
        if (CustomizableFunction.DispChange.equals(func)) {
            toggleDispMode();
            return 1;
        }
        if (GFCommonUtil.getInstance().isFilterSetting()) {
            if (CustomizableFunction.Unchanged.equals(func) || CustomizableFunction.Guide.equals(func) || CustomizableFunction.MainPrev.equals(func) || CustomizableFunction.MainNext.equals(func) || CustomizableFunction.SubPrev.equals(func) || CustomizableFunction.SubNext.equals(func) || CustomizableFunction.ThirdPrev.equals(func) || CustomizableFunction.ThirdNext.equals(func) || CustomizableFunction.MfAssist.equals(func)) {
                ret = super.onConvertedKeyDown(event, func);
            } else {
                ret = -1;
            }
        } else {
            ret = super.onConvertedKeyDown(event, func);
        }
        return ret;
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
        switch (code) {
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                toggleDispMode();
                return 1;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                return -1;
            default:
                int result = super.onKeyDown(keyCode, event);
                return result;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 232 || code == 595) {
            return 1;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        mBorderView.invalidate();
    }
}
