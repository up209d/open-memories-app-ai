package com.sony.imaging.app.bracketpro.menu.layout;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.R;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMExposureModeController;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.menu.controller.BracketMasterController;
import com.sony.imaging.app.bracketpro.shooting.state.BMEEState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class BracketMasterSubMenu extends SpecialScreenMenuLayout {
    public static final String APERTURE_PRIORITY = "aperture-priority";
    public static final String APERTURE_PRIORITY_NORMAL = "Aperture Priority";
    public static final String AUTO = "auto";
    public static final String DEVICE_MODEL_MSS_4G = "DSCSC_MUSASHI4Gb";
    public static final String DEVICE_MODEL_MSS_8G = "DSCSC_MUSASHI8Gb";
    public static final String DEVICE_MODEL_ZNG = "DSCSC_KOJIRO8Gb";
    public static final String DEVICE_MODEL_ZNS = "DSCSC_KOJIRO4Gb";
    public static final String MANUAL_EXPOSURE = "manual-exposure";
    public static final String MANUAL_EXPOSURE_NORMAL = "Manual Exposure";
    public static final String PROGRAM_AUTO = "program-auto";
    public static final String PTAG_BRACKET_MASTER = "Bracket Master";
    public static final String SHUTTER_SPEED = "shutter-speed";
    public static final String SHUTTER_SPEED_NORMAL = "Shutter Priority";
    public static String sLastSelectedItem;
    View currentView;
    private BracketTypeNotificationListner mNotify;
    private boolean mbMounted;
    private String mlastSelectedItem;
    public static boolean isBracketTypeScrOpened = false;
    private static ArrayList<String> mList = new ArrayList<>();
    public static boolean sValidExpModeForTakePic = true;
    public static boolean sOpenRangeScreenInTouchCase = false;
    private String TAG = AppLog.getClassName();
    private BMMenuController mController = null;
    private TextView mGuideTxtVw = null;
    private ImageView mBracketTypeInfo = null;
    private int mSelectedPosition = 0;
    protected CameraEx mCameraEx = null;
    protected Camera mCamera = null;
    private ImageView mBackgroundImageView = null;
    private FooterGuide mFooterGuide = null;
    private MediaNotificationManager mMediaNotifier = null;
    Boolean isMenuKeySelected = false;

    /* loaded from: classes.dex */
    public class BracketProAdapter extends SpecialScreenMenuLayout.SpecialBaseMenuAdapter {
        public BracketProAdapter(Context context, int ResId, BaseMenuService service) {
            super(context, ResId, service);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout.SpecialBaseMenuAdapter, com.sony.imaging.app.base.menu.layout.BaseMenuAdapter
        public Drawable getMenuItemDrawable(int position) {
            return getMenuItemDrawable(this.mItems.get(position));
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.currentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mAdapter = new BracketProAdapter(getActivity(), R.layout.menu_sub_adapter, this.mService);
        this.mController = BMMenuController.getInstance();
        initializeView(this.currentView);
        if (!this.mController.isShootingScreenOpened()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.hour, android.R.string.hour_picker_description));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        if (this.mNotify == null) {
            this.mNotify = new BracketTypeNotificationListner();
            CameraNotificationManager.getInstance().setNotificationListener(this.mNotify);
        }
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mbMounted = this.mMediaNotifier.isMounted();
        return this.currentView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (ModeDialDetector.hasModeDial()) {
            BMExposureModeController emc = BMExposureModeController.getInstance();
            emc.initializeAgain();
            if (!emc.isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
                openNextMenu(itemid, "LastBastionLayout");
                return;
            } else {
                super.doItemClickProcessing(itemid);
                return;
            }
        }
        super.doItemClickProcessing(itemid);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.bm_menu_main;
    }

    private void initializeView(View currentView) {
        this.mGuideTxtVw = (TextView) currentView.findViewById(R.id.guide_view);
        this.mBackgroundImageView = (ImageView) currentView.findViewById(R.id.backgroundImgVw);
        this.mFooterGuide = (FooterGuide) currentView.findViewById(R.id.footer_guide);
        this.mBracketTypeInfo = (ImageView) currentView.findViewById(R.id.BracketTypeInfo);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.currentView.setVisibility(0);
        this.mlastSelectedItem = BMMenuController.getInstance().getSelectedBracket();
        this.mScreenTitleView.setPadding(78, 0, 0, 0);
        this.mScreenTitleView.setText(R.string.STRID_FUNC_BRKMASTER);
        this.mSelectedPosition = getLastSelectedPosition();
        this.mGuideTxtVw.setText(this.mService.getMenuItemGuideText(this.mAdapter.getItem(getLastSelectedPosition())));
        this.mBackgroundImageView.setBackgroundResource(getBackgroundDrawable(this.mAdapter.getItem(this.mSelectedPosition)));
        isBracketTypeScrOpened = true;
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mbMounted = this.mMediaNotifier.isMounted();
        BMMenuController.getInstance().setShowFlashCaution(true);
    }

    private int getLastSelectedPosition() {
        if (this.mlastSelectedItem.equalsIgnoreCase(BMMenuController.ApertureBracket)) {
            return 1;
        }
        if (this.mlastSelectedItem.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket)) {
            return 2;
        }
        if (!this.mlastSelectedItem.equalsIgnoreCase(BMMenuController.FlashBracket)) {
            return 0;
        }
        return 3;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        super.onItemSelected(adapterView, view, position, id);
        this.mGuideTxtVw.setText(this.mService.getMenuItemGuideText(this.mAdapter.getItem(position)));
        this.mSelectedPosition = position;
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, this.mAdapter.getItem(this.mSelectedPosition));
        this.mBackgroundImageView.setBackgroundResource(getBackgroundDrawable(this.mAdapter.getItem(this.mSelectedPosition)));
        BMMenuController.getInstance().setRangeStatus(true);
        setFlashModeOnNotForcely();
    }

    private void setFlashModeOnNotForcely() {
        boolean isFlase = false;
        if (this.mAdapter.getItem(this.mSelectedPosition).equalsIgnoreCase(BMMenuController.FlashBracket)) {
            isFlase = true;
            BMMenuController.getInstance().setRangeStatus(false);
        } else {
            BMMenuController.getInstance().setRangeStatus(true);
        }
        CameraEx mCameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx();
        android.hardware.Camera mCamera = mCameraEx.getNormalCamera();
        if (isFlase) {
            Camera.Parameters params = mCameraEx.createEmptyParameters();
            params.setFlashMode("on");
            mCamera.setParameters(params);
        } else {
            Camera.Parameters params2 = mCameraEx.createEmptyParameters();
            params2.setFlashMode("on");
            mCamera.setParameters(params2);
            Camera.Parameters params3 = mCameraEx.createEmptyParameters();
            params3.setFlashMode("off");
            mCamera.setParameters(params3);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class BracketTypeNotificationListner implements NotificationListener {
        private final String[] tags = {"Aperture", CameraNotificationManager.SHUTTER_SPEED};

        BracketTypeNotificationListner() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            BracketMasterController.getInstance().refreshApertureValues();
            BracketMasterController.getInstance().refreshShutterSpeedValues();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        freeResource();
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected ArrayList<String> getMenuItemList() {
        ArrayList<String> items = this.mService.getSupportedItemList();
        String deviceModel = ScalarProperties.getString("device.memory");
        if (deviceModel.equalsIgnoreCase(DEVICE_MODEL_ZNS) || deviceModel.equalsIgnoreCase(DEVICE_MODEL_ZNG) || deviceModel.equalsIgnoreCase(DEVICE_MODEL_MSS_4G) || deviceModel.equalsIgnoreCase(DEVICE_MODEL_MSS_8G)) {
            items.remove(BMMenuController.FlashBracket);
        }
        this.mService.updateValueItemsAvailable(items);
        return items;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected ArrayList<String> getOptionMenuItemList(String itemId) {
        return mList;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (!this.mController.isShootingScreenOpened()) {
            getActivity().finish();
            AppLog.exit(this.TAG, AppLog.getMethodName());
            return 1;
        }
        this.isMenuKeySelected = true;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return super.turnedMainDialPrev();
    }

    private int getBackgroundDrawable(String itemId) {
        int drawable = -1;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (itemId.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            drawable = R.drawable.p_16_dd_parts_bm_image_focusbracket;
            this.mBracketTypeInfo.setImageResource(R.drawable.p_16_dd_parts_bm_info_focusbracket);
        } else if (itemId.equalsIgnoreCase(BMMenuController.ApertureBracket)) {
            drawable = R.drawable.p_16_dd_parts_bm_image_apbracket;
            this.mBracketTypeInfo.setImageResource(R.drawable.p_16_dd_parts_bm_info_apbracket);
        } else if (itemId.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket)) {
            drawable = R.drawable.p_16_dd_parts_bm_image_ssbracket;
            this.mBracketTypeInfo.setImageResource(R.drawable.p_16_dd_parts_bm_info_ssbracket);
        } else if (itemId.equalsIgnoreCase(BMMenuController.FlashBracket)) {
            drawable = R.drawable.p_16_dd_parts_bm_image_flashbracket;
            this.mBracketTypeInfo.setImageResource(R.drawable.p_16_dd_parts_bm_info_flashbracket);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return drawable;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        isBracketTypeScrOpened = false;
        if (!this.isMenuKeySelected.booleanValue()) {
            BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, this.mAdapter.getItem(this.mSelectedPosition));
        } else {
            this.mSelectedPosition = getLastSelectedPosition();
            BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, this.mlastSelectedItem);
            setFlashModeOnNotForcely();
            this.isMenuKeySelected = false;
        }
        AppNameView.show(true);
        if (this.mNotify != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mNotify);
            this.mNotify = null;
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        if (bundle == null) {
            BMMenuController.getInstance().setRangeStatus(false);
        }
        if (this.mAdapter != null && FocusModeDialDetector.hasFocusModeDial() && !this.mAdapter.getItem(this.mSelectedPosition).equalsIgnoreCase(BMMenuController.FocusBracket)) {
            FocusModeController.getInstance().setValue(FocusModeController.getInstance().getFocusModeFromFocusModeDial());
        }
        super.closeMenuLayout(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (this.mAdapter != null && FocusModeDialDetector.hasFocusModeDial() && !this.mAdapter.getItem(this.mSelectedPosition).equalsIgnoreCase(BMMenuController.FocusBracket)) {
            FocusModeController.getInstance().setValue(FocusModeController.getInstance().getFocusModeFromFocusModeDial());
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onAFMFSwitchChanged() {
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        return -1;
    }

    private void freeResource() {
        this.mBackgroundImageView = null;
        this.mGuideTxtVw = null;
        isBracketTypeScrOpened = false;
        this.mController = null;
        this.mGuideTxtVw = null;
        this.mBracketTypeInfo = null;
        this.mCameraEx = null;
        this.mCamera = null;
        this.mBackgroundImageView = null;
        this.mFooterGuide = null;
        this.mNotify = null;
        this.mMediaNotifier = null;
        this.mlastSelectedItem = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
                if (BMMenuController.getInstance().getSelectedBracket().equalsIgnoreCase(BMMenuController.FocusBracket)) {
                    isValidLens();
                    if (info == null) {
                        displayCaution();
                        return 1;
                    }
                    if (info.LensType.equalsIgnoreCase("A-mount")) {
                        displayAMountLensCaution();
                        return 1;
                    }
                    int retValue = super.onKeyDown(keyCode, event);
                    return retValue;
                }
                int retValue2 = super.onKeyDown(keyCode, event);
                return retValue2;
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                if (!BracketMasterUtil.getCurrentBracketType().equalsIgnoreCase(BMMenuController.FlashBracket)) {
                    BMMenuController.getInstance().setRangeStatus(true);
                }
                return 0;
            default:
                int retValue3 = super.onKeyDown(keyCode, event);
                return retValue3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BracketMasterSubMenu.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        BracketMasterSubMenu.this.currentView.setVisibility(0);
                        BracketMasterSubMenu.this.updateView();
                        return -1;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        BracketMasterSubMenu.this.transitToNextState();
                        return -1;
                    default:
                        return -1;
                }
            }
        };
        if (this.currentView != null) {
            this.currentView.setVisibility(4);
        }
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CautionInfo.CAUTION_ID_INH_FACTOR_DETACH_EMOUNT_LENS, mKey);
        CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_ID_INH_FACTOR_DETACH_EMOUNT_LENS);
    }

    protected void transitToNextState() {
        try {
            CautionUtilityClass.cautionData cdData = CautionUtilityClass.getInstance().getCurrentCautionData();
            if (cdData != null) {
                CautionUtilityClass.getInstance().executeTerminate();
                try {
                    Thread.sleep(500L);
                } catch (Exception e) {
                }
                CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
                if (info.LensType.equalsIgnoreCase("A-mount")) {
                    displayAMountLensCaution();
                } else {
                    clickCenterKey();
                }
            }
        } catch (Exception e2) {
            AppLog.info(this.TAG, "transitToNextState() of BracketMasterSubMenu");
        }
    }

    private void displayAMountLensCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BracketMasterSubMenu.2
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        CautionUtilityClass.getInstance().disapperTrigger(CautionInfo.CAUTION_ID_INH_FACTOR_INVALID_WITH_MOUNT_ADAPTER);
                        BracketMasterSubMenu.this.displayCaution();
                        break;
                }
                return super.onKeyUp(keyCode, event);
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        BracketMasterSubMenu.this.currentView.setVisibility(0);
                        BracketMasterSubMenu.this.updateView();
                        return -1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        BracketMasterSubMenu.this.currentView.setVisibility(0);
                        return -1;
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    default:
                        return -1;
                }
            }
        };
        if (this.currentView != null) {
            this.currentView.setVisibility(4);
        }
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CautionInfo.CAUTION_ID_INH_FACTOR_INVALID_WITH_MOUNT_ADAPTER, mKey);
        CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_ID_INH_FACTOR_INVALID_WITH_MOUNT_ADAPTER);
    }

    private void clickCenterKey() {
        String opItemId;
        String itemId = (String) this.mSpecialScreenView.getSelectedItem();
        if (this.mService.hasSubArray(itemId) && (opItemId = this.mSpecialScreenView.getSelectedOptionItem()) != null) {
            itemId = opItemId;
        }
        doItemClickProcessing(itemId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void onFocusModeChanged() {
        super.onFocusModeChanged();
        transitToNextState();
    }

    private void isValidLens() {
        CameraSetting mCamSet = CameraSetting.getInstance();
        CameraEx.LensInfo info = mCamSet.getLensInfo();
        if (info == null) {
            BMEEState.isBMCautionStateBooted = true;
            return;
        }
        if (info.LensType.equalsIgnoreCase("A-mount")) {
            BMEEState.isBMCautionStateBooted = true;
        } else if (isMFModeSet()) {
            BMEEState.isBMCautionStateBooted = true;
        } else {
            BMEEState.isBMCautionStateBooted = false;
        }
    }

    private boolean isMFModeSet() {
        if (!FocusModeDialDetector.hasFocusModeDial() && !"af-s".equals(FocusModeController.getInstance().getValue())) {
            return true;
        }
        if (!FocusModeDialDetector.hasFocusModeDial() || "af-s".equals(FocusModeController.getInstance().getValue())) {
            return false;
        }
        return true;
    }
}
