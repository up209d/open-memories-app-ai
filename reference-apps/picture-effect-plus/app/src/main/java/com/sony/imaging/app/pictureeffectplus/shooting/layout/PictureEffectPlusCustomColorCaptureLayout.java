package com.sony.imaging.app.pictureeffectplus.shooting.layout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.playback.contents.EditServiceImpl;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.R;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.pictureeffectplus.shooting.widget.VerticalSeekBar;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class PictureEffectPlusCustomColorCaptureLayout extends DisplayMenuItemsMenuLayout implements View.OnTouchListener {
    public static final int CAUTION_ID_DLAPP_COLOR_CAPTURE_ERROR = 131277;
    private static DisplayManager.VideoRect EE_Rect = null;
    private static final String TAG = "PictureEffectPlusCustomColorCaptureLayout";
    private boolean captureFlag;
    private PictureEffectPlusController mController;
    private boolean mIsTouchPanelModel;
    protected static final StringBuilder STRBUILD = new StringBuilder();
    public static boolean mSetS2PushedStatus = false;
    protected String FUNC_NAME = "";
    protected View mCurrentView = null;
    int[] touchView_origin = new int[2];
    private final boolean COLOR_CAPTURE_TRUE = true;
    private final boolean COLOR_CAPTURE_FALSE = false;
    private FooterGuide mFooterGuide = null;
    private NotificationListener mDeviceChangeListener = new NotificationListener() { // from class: com.sony.imaging.app.pictureeffectplus.shooting.layout.PictureEffectPlusCustomColorCaptureLayout.2
        private String[] TAGS = {DisplayModeObserver.TAG_DEVICE_CHANGE};

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            PictureEffectPlusCustomColorCaptureLayout.this.updateView();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }
    };

    protected void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.pictureeffectplus.shooting.layout.PictureEffectPlusCustomColorCaptureLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        PictureEffectPlusCustomColorCaptureLayout.setIsS2PushedStatus(false);
                        return 0;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        if (!EVDialDetector.hasEVDial()) {
                            return 1;
                        }
                        ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                        return 1;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CAUTION_ID_DLAPP_COLOR_CAPTURE_ERROR, mKey);
        CautionUtilityClass.getInstance().requestTrigger(CAUTION_ID_DLAPP_COLOR_CAPTURE_ERROR);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setIsS2PushedStatus(false);
        DisplayModeObserver.getInstance().setNotificationListener(this.mDeviceChangeListener);
        Log.i(TAG, "onResume");
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mDeviceChangeListener);
        this.mCurrentView.setOnTouchListener(null);
        CautionUtilityClass.getInstance().executeTerminate();
        Log.i(TAG, "onPause");
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DisplayManager dmgr = new DisplayManager();
        EE_Rect = dmgr.getDisplayedVideoRect();
        dmgr.finish();
        createView();
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.mCurrentView.setOnTouchListener(this);
        this.mCurrentView.getLocationOnScreen(this.touchView_origin);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mFooterGuide.setData(new FooterGuideDataResId(context, android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        this.mController = PictureEffectPlusController.getInstance();
        this.mController.startOVFpartColorCondition();
        this.captureFlag = false;
        this.mController.setColorCaptureStatus(PictureEffectPlusController.COLOR_CAPTURE_STATUS_YES);
        this.FUNC_NAME = "onCreateView";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return this.mCurrentView;
    }

    private void createView() {
        int isSupporeted = ScalarProperties.getInt("input.tp.supported");
        this.mIsTouchPanelModel = isSupporeted == 1;
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (device == 1) {
            this.mCurrentView = obtainViewFromPool(R.layout.pictureeffectplus_colorcapture_evf);
        } else if (this.mIsTouchPanelModel) {
            this.mCurrentView = obtainViewFromPool(R.layout.pictureeffectplus_colorcapture_with_tp);
        } else {
            this.mCurrentView = obtainViewFromPool(R.layout.pictureeffectplus_colorcapture);
        }
        this.FUNC_NAME = "createView";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        cancelSetValue();
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.FUNC_NAME = "pushedS1On";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        this.mController.setColorCaptureStatus(true);
        setIsS2PushedStatus(true);
        int androidEE_x = convertDiadem2AndroidPreviewCoordX(320, this.touchView_origin[0]);
        int androidEE_y = convertDiadem2AndroidPreviewCoordY(148, this.touchView_origin[1]);
        if (this.mController.captureColorFromEE(androidEE_x, androidEE_y) != null) {
            this.captureFlag = true;
            this.mService = new BaseMenuService(getActivity().getApplicationContext());
            String nextMenuId = this.mService.getMenuItemNextMenuID(PictureEffectPlusController.PART_COLOR_CUSTOM);
            if (nextMenuId != null && this.mService.isMenuItemValid(PictureEffectPlusController.PART_COLOR_CUSTOM)) {
                openNextMenu(PictureEffectPlusController.PART_COLOR_CUSTOM, nextMenuId);
            }
        } else {
            displayCaution();
        }
        Log.i(TAG, "pushedS2On");
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (this.captureFlag) {
            this.mController.setColorCaptureStatus(PictureEffectPlusController.COLOR_CAPTURE_STATUS_YES);
            this.mController.changeTargetChannelOnlyPartColor();
            this.mController.setCapturedColorToEE();
        } else if (!this.captureFlag) {
            this.mController.setColorCaptureStatus(PictureEffectPlusController.COLOR_CAPTURE_STATUS_NO);
            this.mController.setOriginalColor();
            this.mController.changeEEDoublePartColor();
        }
        this.mController.setCh0ExifInfo();
        this.mController.setCustomSet2CustomOnBackupOptionValue();
        this.mController.endOVFpartColorCondition();
        this.mController.setComeFromColorIndication(true);
        super.closeLayout();
        Log.i(TAG, "closeLayout");
    }

    private int convertDiadem2AndroidPreviewCoordX(float touchView_x, int touchView_org_x) {
        float xx = ((((touchView_org_x + touchView_x) - EE_Rect.pxLeft) / (EE_Rect.pxRight - EE_Rect.pxLeft)) * 2000.0f) - 1000.0f;
        int androidEE_x = (int) xx;
        if (androidEE_x < -1000 || androidEE_x > 1000) {
            androidEE_x = EditServiceImpl.ERR_UNKNOWN;
        }
        this.FUNC_NAME = "convertDiadem2AndroidPreviewCoordX";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return androidEE_x;
    }

    private int convertDiadem2AndroidPreviewCoordY(float touchView_y, int touchView_org_y) {
        float yy = ((((touchView_org_y + touchView_y) - EE_Rect.pxTop) / (EE_Rect.pxBottom - EE_Rect.pxTop)) * 2000.0f) - 1000.0f;
        int androidEE_y = (int) yy;
        if (androidEE_y < -1000 || androidEE_y > 1000) {
            androidEE_y = EditServiceImpl.ERR_UNKNOWN;
        }
        this.FUNC_NAME = "convertDiadem2AndroidPreviewCoordY";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return androidEE_y;
    }

    private int convertAndroidPreview2DiademCoordX(int androidEE_x, int touchView_org_x) {
        float xx = ((((androidEE_x + 1000.0f) * (EE_Rect.pxRight - EE_Rect.pxLeft)) / 2000.0f) - touchView_org_x) + EE_Rect.pxLeft;
        int touchView_x = (int) xx;
        this.FUNC_NAME = "convertAndroidPreview2DiademCoordX";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return touchView_x;
    }

    private int convertAndroidPreview2DiademCoordY(int androidEE_y, int touchView_org_y) {
        float yy = ((((androidEE_y + 1000.0f) * (EE_Rect.pxBottom - EE_Rect.pxTop)) / 2000.0f) - touchView_org_y) + EE_Rect.pxTop;
        int touchView_y = (int) yy;
        this.FUNC_NAME = "convertAndroidPreview2DiademCoordY";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return touchView_y;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setIsS2PushedStatus(boolean isS2PushedStatus) {
        mSetS2PushedStatus = isS2PushedStatus;
        Log.i(TAG, "setIsS2PushedStatus");
    }

    public static boolean getS2PushedStatus() {
        Log.i(TAG, "getS2PushedStatus");
        return mSetS2PushedStatus;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0005. Please report as an issue. */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int retValue;
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                if (EVDialDetector.hasEVDial()) {
                    ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                }
                return -1;
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                retValue = pushedMenuKey();
                return retValue;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                retValue = pushedS1Key();
                return retValue;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                retValue = pushedS2Key();
                return retValue;
            default:
                retValue = super.onKeyDown(keyCode, event);
                return retValue;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                if (EVDialDetector.hasEVDial()) {
                    ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                }
                return -1;
            default:
                int retValue = super.onKeyUp(keyCode, event);
                return retValue;
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        VerticalSeekBar.sISTouchEnable = true;
        if (!getS2PushedStatus()) {
            switch (event.getAction()) {
                case 0:
                    Log.i(TAG, "DOWN");
                    break;
                case 1:
                    Log.i(TAG, "touched x right in");
                    int androidEE_x = convertDiadem2AndroidPreviewCoordX(event.getX(), this.touchView_origin[0]);
                    int androidEE_y = convertDiadem2AndroidPreviewCoordY(event.getY(), this.touchView_origin[1]);
                    if (this.mController.captureColorFromEE(androidEE_x, androidEE_y) != null) {
                        this.captureFlag = true;
                        this.mService = new BaseMenuService(getActivity().getApplicationContext());
                        String nextMenuId = this.mService.getMenuItemNextMenuID(PictureEffectPlusController.PART_COLOR_CUSTOM);
                        if (nextMenuId != null && this.mService.isMenuItemValid(PictureEffectPlusController.PART_COLOR_CUSTOM)) {
                            openNextMenu(PictureEffectPlusController.PART_COLOR_CUSTOM, nextMenuId);
                        }
                    } else {
                        displayCaution();
                    }
                    Log.i(TAG, "UP");
                    break;
                case 2:
                    Log.i(TAG, "MOVE");
                    break;
                case 3:
                    Log.i(TAG, "CANCEL");
                    break;
            }
        }
        this.FUNC_NAME = "onTouch";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return true;
    }
}
