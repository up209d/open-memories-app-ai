package com.sony.imaging.app.portraitbeauty.menu.layout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.PortraitBeautyApp;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.AppContext;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.controller.PortraitBeautySoftSkinController;
import com.sony.imaging.app.portraitbeauty.menu.controller.PortraitBeautyWhiteSkinController;
import com.sony.imaging.app.portraitbeauty.shooting.widget.PortraitBeautyVerticalSeekBar;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SelectEffectMenuLayout extends SpecialScreenMenuLayout implements SeekBar.OnSeekBarChangeListener, SpecialScreenView.OnItemSelectedListener {
    private static final int BACKGROUND_IMAGE_HEIGHT = 226;
    private static final int BACKGROUND_IMAGE_TOP_MARGIN = 88;
    private static final int GUIDE_DISPLAY_TIME = 3000;
    private static final int NUMBER_OF_BACKGROUND__ICONS = 10;
    private static String prefixImageName = "p_16_dd_parts_portraitbeauty_bkgrounddefocus_level";
    int currentProgress;
    private RunnableTask guideTask;
    private Handler mHandlerBalloontGuide;
    private ArrayList<String> mList;
    private String mSelectedSoftSkinLevel;
    private String mSoftSkinValue;
    private final String TAG = "SelectEffectMenuLayout";
    private View mCurrentView = null;
    private ImageView mImgVwWhiteSkinEffect = null;
    private ImageView mImgVwSoftSkinEffect = null;
    private ImageView mImgVwSeekBarbg = null;
    private TextView mTxtVwPlusSign = null;
    private TextView mTxtVwMinusSign = null;
    private TextView mTxtVwOff = null;
    private boolean isMenuKeyPressed = false;
    private TextView mTxtVwWhiteEffectGuide = null;
    private ImageView mImgVwWhiteEffectGuideArrow = null;
    private TextView mTxtVwSoftSkinGuide = null;
    private ImageView mImgVwSoftSkinGuideArrow = null;
    private ImageView mImgWhiteSkinOffIcon = null;
    private String selectedBeautyEffect = null;
    private final int DEFAULT_WHITE_SKIN_VALUE = 4;
    private final int DEFAULT_DEFOCUS_ICON_INDEX = -1;
    private int mSelectedItemPosition = -1;
    private final int ITEM_COUNT_4 = 4;
    private String softSkinValue = null;
    private String defocusValue = null;
    private String defocusAperturelValue = null;
    private int mWhiteSkinValue = 4;
    private PortraitBeautyVerticalSeekBar mPortraitBeautyVerticalSeekBar = null;
    private PortraitBeautyWhiteSkinController mPortraitBeautyWhiteSkinController = null;
    private PortraitBeautySoftSkinController mPortraitBeautySoftSkinController = null;

    public SelectEffectMenuLayout() {
        PortraitBeautySoftSkinController portraitBeautySoftSkinController = this.mPortraitBeautySoftSkinController;
        this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_MID;
        this.mSelectedSoftSkinLevel = PortraitBeautySoftSkinController.SOFTSKIN_MID;
        this.mList = null;
        this.mHandlerBalloontGuide = null;
        this.guideTask = null;
        this.currentProgress = 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.pb_select_effect);
        this.selectedBeautyEffect = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_EFFECT, PortraitBeautyConstants.WHITE_SKIN_EFFECT);
        this.mWhiteSkinValue = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, 4);
        this.mSoftSkinValue = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_LEVEL_OF_SOFT_SKIN, PortraitBeautySoftSkinController.SOFTSKIN_MID);
        initializeView();
        this.mService = new BaseMenuService(AppContext.getAppContext());
        this.mService.setMenuItemId("ApplicationSettings");
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(AppContext.getAppContext(), R.layout.menu_sub_adapter, this.mService);
        this.mAdapter.setMenuItemList(this.mService.getMenuItemList());
        this.mSpecialScreenView.setAdapter(this.mAdapter);
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        this.mViewArea.intialize(AppContext.getAppContext(), this.mSpecialScreenView);
        this.mViewArea.setVisibility(4);
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        if (this.mSpecialScreenView.getAdapter() == null) {
            this.mService = null;
            this.mAdapter = null;
            this.mService = new BaseMenuService(AppContext.getAppContext());
            this.mService.setMenuItemId("ApplicationSettings");
            this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(AppContext.getAppContext(), R.layout.menu_sub_adapter, this.mService);
            this.mAdapter.setMenuItemList(this.mService.getMenuItemList());
            this.mSpecialScreenView.setAdapter(this.mAdapter);
        }
        this.mSelectedSoftSkinLevel = this.mSoftSkinValue;
        if (this.selectedBeautyEffect.equals(PortraitBeautyConstants.WHITE_SKIN_EFFECT)) {
            this.mImgVwWhiteSkinEffect.setSelected(true);
            this.mImgVwSoftSkinEffect.setSelected(false);
        } else if (this.selectedBeautyEffect.equals(PortraitBeautyConstants.SOFT_SKIN_EFFECT)) {
            this.mImgVwSoftSkinEffect.setSelected(true);
            this.mImgVwWhiteSkinEffect.setSelected(false);
        } else if (this.selectedBeautyEffect.equals(PortraitBeautyConstants.DEFOCUS_EFFECT)) {
        }
        updateSoftSkinIcon(this.mSoftSkinValue);
        updateWhiteSkinIcon(this.mWhiteSkinValue);
        initializeTask();
        setGuideVisibility(this.selectedBeautyEffect);
        handleVisibilityOfWhiteSkinGuide();
        this.mList = this.mService.getMenuItemList();
        int position = this.mService.getMenuItemList().indexOf(this.mSoftSkinValue);
        int mBlock = getMenuItemList().size();
        if (mBlock > 4) {
            mBlock = 4;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, position);
        this.mPortraitBeautySoftSkinController.setValue(PortraitBeautySoftSkinController.SOFTSKIN, this.mSelectedSoftSkinLevel);
        update(position);
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
    }

    private void initializeTask() {
        if (this.mHandlerBalloontGuide == null) {
            this.mHandlerBalloontGuide = new Handler();
        }
        if (this.guideTask == null) {
            this.guideTask = new RunnableTask(this.mHandlerBalloontGuide);
        }
    }

    private void deInitializeTask() {
        if (this.guideTask != null) {
            this.guideTask.removeCallbacks();
        }
        this.mHandlerBalloontGuide = null;
    }

    private void initializeView() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        this.mImgVwWhiteSkinEffect = (ImageView) this.mCurrentView.findViewById(R.id.whitening_effect);
        this.mImgVwSoftSkinEffect = (ImageView) this.mCurrentView.findViewById(R.id.softskin_effect);
        this.mImgVwSeekBarbg = (ImageView) this.mCurrentView.findViewById(R.id.seek_bar_bg_ws);
        this.mTxtVwPlusSign = (TextView) this.mCurrentView.findViewById(R.id.vsb_plus);
        this.mTxtVwMinusSign = (TextView) this.mCurrentView.findViewById(R.id.vsb_minus);
        this.mTxtVwOff = (TextView) this.mCurrentView.findViewById(R.id.vsb_off);
        this.mImgWhiteSkinOffIcon = (ImageView) this.mCurrentView.findViewById(R.id.vsb_off_icon);
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mSpecialScreenView.setOnSpecialScreenItemSelectedListener(this);
        PortraitBeautySoftSkinController portraitBeautySoftSkinController = this.mPortraitBeautySoftSkinController;
        this.mPortraitBeautySoftSkinController = PortraitBeautySoftSkinController.getInstance();
        this.mSpecialScreenView.setVisibility(4);
        this.mTxtVwWhiteEffectGuide = (TextView) this.mCurrentView.findViewById(R.id.whitening_effect_guide);
        this.mImgVwWhiteEffectGuideArrow = (ImageView) this.mCurrentView.findViewById(R.id.whitening_effect_guide_arrow);
        this.mTxtVwSoftSkinGuide = (TextView) this.mCurrentView.findViewById(R.id.softskin_effect_guide);
        this.mImgVwSoftSkinGuideArrow = (ImageView) this.mCurrentView.findViewById(R.id.softskin_effect_guide_arrow);
        this.mPortraitBeautyVerticalSeekBar = (PortraitBeautyVerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_seekbar_ws);
        this.mPortraitBeautyVerticalSeekBar.setMax(7);
        this.mPortraitBeautyVerticalSeekBar.setProgress(this.mWhiteSkinValue);
        updateBeautyEffectView(this.selectedBeautyEffect);
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        deInitializeTask();
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        this.mImgVwWhiteSkinEffect = null;
        this.mImgVwSoftSkinEffect = null;
        this.mImgVwSeekBarbg = null;
        this.mTxtVwPlusSign = null;
        this.mTxtVwMinusSign = null;
        this.mHandlerBalloontGuide = null;
        this.mWhiteSkinValue = 4;
        this.mSoftSkinValue = null;
        this.isMenuKeyPressed = false;
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekbar, int progress, boolean arg2) {
        Log.i("SelectEffectMenuLayout", "currentProgress = " + this.currentProgress);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar arg0) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar arg0) {
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        this.isMenuKeyPressed = true;
        this.mService = new BaseMenuService(AppContext.getAppContext());
        String nextMenuId = this.mService.getMenuItemNextMenuID("Main1");
        if (nextMenuId != null && this.mService.isMenuItemValid("Main1")) {
            openNextMenu("Main1", nextMenuId);
        }
        if (this.data != null && this.data.getString(MenuState.ITEM_ID) != null && this.data.getString(MenuState.ITEM_ID).equals("ApplicationSettings")) {
            super.pushedS1Key();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        setBootFactor();
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        return super.pushedS1Key();
    }

    private void saveCurrentEffectValues() {
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, Integer.valueOf(this.mWhiteSkinValue));
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_LEVEL_OF_SOFT_SKIN, this.mSoftSkinValue);
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_LEVEL_OF_DEFOCUS, this.defocusAperturelValue);
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_EFFECT, this.selectedBeautyEffect);
    }

    private void setDefaultValues() {
        this.selectedBeautyEffect = PortraitBeautyConstants.WHITE_SKIN_EFFECT;
        this.mWhiteSkinValue = 4;
        this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_MID;
        this.mImgVwWhiteSkinEffect.setSelected(true);
        this.mImgVwSoftSkinEffect.setSelected(false);
        this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level4);
        this.mPortraitBeautyVerticalSeekBar.setProgress(this.mWhiteSkinValue);
        this.mTxtVwSoftSkinGuide.setVisibility(4);
        this.mImgVwSoftSkinGuideArrow.setVisibility(4);
        this.mTxtVwWhiteEffectGuide.setVisibility(0);
        this.mImgVwWhiteEffectGuideArrow.setVisibility(0);
        int position = this.mService.getMenuItemList().indexOf(this.mSoftSkinValue);
        this.mSpecialScreenView.setSelection(position);
        this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_mid_menu);
        updateBeautyEffectView(this.selectedBeautyEffect);
        ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
        handleVisibilityOfWhiteSkinGuide();
    }

    private List<String> getSupportedApertureList() {
        List<String> supportedlist = new ArrayList<>();
        List<String> aperturevaluelist = PortraitBeautyConstants.APERTURE_VALUE_LIST;
        CameraEx.LensInfo lensinfo = CameraSetting.getInstance().getLensInfo();
        Log.i("SelectEffectMenuLayout", "lensinfo.MaxFValue.tele = " + lensinfo.MaxFValue.tele + " lensinfo.MinFValue.wide = " + lensinfo.MinFValue.wide);
        float maxAperture = lensinfo.MaxFValue.tele / 10.0f;
        float minAperture = lensinfo.MinFValue.wide / 10.0f;
        Log.i("SelectEffectMenuLayout", "maxAperture = " + maxAperture + " minAperture = " + minAperture);
        for (int i = 0; i < aperturevaluelist.size(); i++) {
            String value = PortraitBeautyConstants.APERTURE_VALUE_LIST.get(i).replace("F", "");
            float intValue = Float.parseFloat(value);
            if (minAperture <= intValue && intValue <= maxAperture) {
                supportedlist.add(aperturevaluelist.get(i));
            }
        }
        return supportedlist;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
            this.selectedBeautyEffect = PortraitBeautyConstants.SOFT_SKIN_EFFECT;
            updateBeautyEffectView(this.selectedBeautyEffect);
        } else if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
            this.selectedBeautyEffect = PortraitBeautyConstants.WHITE_SKIN_EFFECT;
            updateBeautyEffectView(this.selectedBeautyEffect);
        }
        this.guideTask.removeCallbacks();
        setGuideVisibility(this.selectedBeautyEffect);
        handleVisibilityOfWhiteSkinGuide();
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
            this.selectedBeautyEffect = PortraitBeautyConstants.WHITE_SKIN_EFFECT;
            this.mPortraitBeautyVerticalSeekBar.setMax(7);
            this.mPortraitBeautyVerticalSeekBar.setProgress(this.mWhiteSkinValue);
            updateBeautyEffectView(this.selectedBeautyEffect);
        } else if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
            this.selectedBeautyEffect = PortraitBeautyConstants.SOFT_SKIN_EFFECT;
            updateBeautyEffectView(this.selectedBeautyEffect);
        }
        this.guideTask.removeCallbacks();
        setGuideVisibility(this.selectedBeautyEffect);
        handleVisibilityOfWhiteSkinGuide();
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        return 1;
    }

    private void setBootFactor() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        if (PortraitBeautyApp.sBootFactor == 0) {
            PortraitBeautyApp.sBootFactor = -1;
        }
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        if (bundle == null) {
            setBootFactor();
        }
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        super.closeMenuLayout(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!this.isMenuKeyPressed) {
            saveCurrentEffectValues();
            this.isMenuKeyPressed = false;
        }
        PortraitBeautyUtil.getInstance().setLensAttachedOnShootingScreen(false);
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        setBootFactor();
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecCustomKey() {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        setBootFactor();
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
        return super.pushedMovieRecCustomKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        CameraSetting.getInstance();
        int scanCode = event.getScanCode();
        PortraitBeautyUtil.getInstance().setLensAttachedOnShootingScreen(false);
        boolean handle = false;
        switch (scanCode) {
            case 103:
                if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
                    if (this.mWhiteSkinValue < 7) {
                        this.mPortraitBeautyVerticalSeekBar.moveUp();
                        this.mWhiteSkinValue++;
                        updateWhiteSkinIcon(this.mWhiteSkinValue);
                        this.mTxtVwWhiteEffectGuide.setVisibility(4);
                        this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
                    }
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.DEFOCUS_EFFECT.equals(this.selectedBeautyEffect)) {
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
                    String str = this.mSoftSkinValue;
                    PortraitBeautySoftSkinController portraitBeautySoftSkinController = this.mPortraitBeautySoftSkinController;
                    if (str.equalsIgnoreCase("Off")) {
                        PortraitBeautySoftSkinController portraitBeautySoftSkinController2 = this.mPortraitBeautySoftSkinController;
                        this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_LOW;
                    } else {
                        String str2 = this.mSoftSkinValue;
                        PortraitBeautySoftSkinController portraitBeautySoftSkinController3 = this.mPortraitBeautySoftSkinController;
                        if (str2.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_LOW)) {
                            PortraitBeautySoftSkinController portraitBeautySoftSkinController4 = this.mPortraitBeautySoftSkinController;
                            this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_MID;
                        } else {
                            String str3 = this.mSoftSkinValue;
                            PortraitBeautySoftSkinController portraitBeautySoftSkinController5 = this.mPortraitBeautySoftSkinController;
                            if (str3.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_MID)) {
                                PortraitBeautySoftSkinController portraitBeautySoftSkinController6 = this.mPortraitBeautySoftSkinController;
                                this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_HIGH;
                            } else {
                                String str4 = this.mSoftSkinValue;
                                PortraitBeautySoftSkinController portraitBeautySoftSkinController7 = this.mPortraitBeautySoftSkinController;
                                if (str4.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_HIGH)) {
                                    PortraitBeautySoftSkinController portraitBeautySoftSkinController8 = this.mPortraitBeautySoftSkinController;
                                    this.mSoftSkinValue = "Off";
                                }
                            }
                        }
                    }
                    this.mTxtVwSoftSkinGuide.setVisibility(4);
                    this.mImgVwSoftSkinGuideArrow.setVisibility(4);
                    updateSoftSkinIcon(this.mSoftSkinValue);
                    handle = false;
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
                    if (this.mWhiteSkinValue > 0) {
                        this.mPortraitBeautyVerticalSeekBar.moveDown();
                        this.mWhiteSkinValue--;
                        updateWhiteSkinIcon(this.mWhiteSkinValue);
                        this.mTxtVwWhiteEffectGuide.setVisibility(4);
                        this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
                    }
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.DEFOCUS_EFFECT.equals(this.selectedBeautyEffect)) {
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
                    String str5 = this.mSoftSkinValue;
                    PortraitBeautySoftSkinController portraitBeautySoftSkinController9 = this.mPortraitBeautySoftSkinController;
                    if (str5.equalsIgnoreCase("Off")) {
                        PortraitBeautySoftSkinController portraitBeautySoftSkinController10 = this.mPortraitBeautySoftSkinController;
                        this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_HIGH;
                    } else {
                        String str6 = this.mSoftSkinValue;
                        PortraitBeautySoftSkinController portraitBeautySoftSkinController11 = this.mPortraitBeautySoftSkinController;
                        if (str6.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_LOW)) {
                            PortraitBeautySoftSkinController portraitBeautySoftSkinController12 = this.mPortraitBeautySoftSkinController;
                            this.mSoftSkinValue = "Off";
                        } else {
                            String str7 = this.mSoftSkinValue;
                            PortraitBeautySoftSkinController portraitBeautySoftSkinController13 = this.mPortraitBeautySoftSkinController;
                            if (str7.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_MID)) {
                                PortraitBeautySoftSkinController portraitBeautySoftSkinController14 = this.mPortraitBeautySoftSkinController;
                                this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_LOW;
                            } else {
                                String str8 = this.mSoftSkinValue;
                                PortraitBeautySoftSkinController portraitBeautySoftSkinController15 = this.mPortraitBeautySoftSkinController;
                                if (str8.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_HIGH)) {
                                    PortraitBeautySoftSkinController portraitBeautySoftSkinController16 = this.mPortraitBeautySoftSkinController;
                                    this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_MID;
                                }
                            }
                        }
                    }
                    this.mTxtVwSoftSkinGuide.setVisibility(4);
                    this.mImgVwSoftSkinGuideArrow.setVisibility(4);
                    updateSoftSkinIcon(this.mSoftSkinValue);
                    handle = false;
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                setDefaultValues();
                handle = true;
                break;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
                    if (this.mWhiteSkinValue < 7) {
                        this.mPortraitBeautyVerticalSeekBar.moveUp();
                        this.mWhiteSkinValue++;
                        updateWhiteSkinIcon(this.mWhiteSkinValue);
                        this.mTxtVwWhiteEffectGuide.setVisibility(4);
                        this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
                    }
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.DEFOCUS_EFFECT.equals(this.selectedBeautyEffect)) {
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
                    String str9 = this.mSoftSkinValue;
                    PortraitBeautySoftSkinController portraitBeautySoftSkinController17 = this.mPortraitBeautySoftSkinController;
                    if (str9.equalsIgnoreCase("Off")) {
                        PortraitBeautySoftSkinController portraitBeautySoftSkinController18 = this.mPortraitBeautySoftSkinController;
                        this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_HIGH;
                    } else {
                        String str10 = this.mSoftSkinValue;
                        PortraitBeautySoftSkinController portraitBeautySoftSkinController19 = this.mPortraitBeautySoftSkinController;
                        if (str10.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_LOW)) {
                            PortraitBeautySoftSkinController portraitBeautySoftSkinController20 = this.mPortraitBeautySoftSkinController;
                            this.mSoftSkinValue = "Off";
                        } else {
                            String str11 = this.mSoftSkinValue;
                            PortraitBeautySoftSkinController portraitBeautySoftSkinController21 = this.mPortraitBeautySoftSkinController;
                            if (str11.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_MID)) {
                                PortraitBeautySoftSkinController portraitBeautySoftSkinController22 = this.mPortraitBeautySoftSkinController;
                                this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_LOW;
                            } else {
                                String str12 = this.mSoftSkinValue;
                                PortraitBeautySoftSkinController portraitBeautySoftSkinController23 = this.mPortraitBeautySoftSkinController;
                                if (str12.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_HIGH)) {
                                    PortraitBeautySoftSkinController portraitBeautySoftSkinController24 = this.mPortraitBeautySoftSkinController;
                                    this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_MID;
                                }
                            }
                        }
                    }
                    this.mTxtVwSoftSkinGuide.setVisibility(4);
                    this.mImgVwSoftSkinGuideArrow.setVisibility(4);
                    updateSoftSkinIcon(this.mSoftSkinValue);
                    handle = false;
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
                    if (this.mWhiteSkinValue > 0) {
                        this.mPortraitBeautyVerticalSeekBar.moveDown();
                        this.mWhiteSkinValue--;
                        updateWhiteSkinIcon(this.mWhiteSkinValue);
                        this.mTxtVwWhiteEffectGuide.setVisibility(4);
                        this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
                    }
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.DEFOCUS_EFFECT.equals(this.selectedBeautyEffect)) {
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
                    String str13 = this.mSoftSkinValue;
                    PortraitBeautySoftSkinController portraitBeautySoftSkinController25 = this.mPortraitBeautySoftSkinController;
                    if (str13.equalsIgnoreCase("Off")) {
                        PortraitBeautySoftSkinController portraitBeautySoftSkinController26 = this.mPortraitBeautySoftSkinController;
                        this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_LOW;
                    } else {
                        String str14 = this.mSoftSkinValue;
                        PortraitBeautySoftSkinController portraitBeautySoftSkinController27 = this.mPortraitBeautySoftSkinController;
                        if (str14.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_LOW)) {
                            PortraitBeautySoftSkinController portraitBeautySoftSkinController28 = this.mPortraitBeautySoftSkinController;
                            this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_MID;
                        } else {
                            String str15 = this.mSoftSkinValue;
                            PortraitBeautySoftSkinController portraitBeautySoftSkinController29 = this.mPortraitBeautySoftSkinController;
                            if (str15.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_MID)) {
                                PortraitBeautySoftSkinController portraitBeautySoftSkinController30 = this.mPortraitBeautySoftSkinController;
                                this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_HIGH;
                            } else {
                                String str16 = this.mSoftSkinValue;
                                PortraitBeautySoftSkinController portraitBeautySoftSkinController31 = this.mPortraitBeautySoftSkinController;
                                if (str16.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_HIGH)) {
                                    PortraitBeautySoftSkinController portraitBeautySoftSkinController32 = this.mPortraitBeautySoftSkinController;
                                    this.mSoftSkinValue = "Off";
                                }
                            }
                        }
                    }
                    this.mTxtVwSoftSkinGuide.setVisibility(4);
                    this.mImgVwSoftSkinGuideArrow.setVisibility(4);
                    updateSoftSkinIcon(this.mSoftSkinValue);
                    handle = false;
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                pushedRightKey();
                handle = true;
                break;
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                pushedLeftKey();
                handle = true;
                break;
        }
        if (handle) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            return 1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }

    private void updateWhiteSkinIcon(int whiteSkinUpdate) {
        if (whiteSkinUpdate == 7) {
            this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level7);
            return;
        }
        if (whiteSkinUpdate == 6) {
            this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level6);
            return;
        }
        if (whiteSkinUpdate == 5) {
            this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level5);
            return;
        }
        if (whiteSkinUpdate == 4) {
            this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level4);
            return;
        }
        if (whiteSkinUpdate == 3) {
            this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level3);
            return;
        }
        if (whiteSkinUpdate == 2) {
            this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level2);
        } else if (whiteSkinUpdate == 1) {
            this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level1);
        } else if (whiteSkinUpdate == 0) {
            this.mImgVwWhiteSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_off);
        }
    }

    private void updateSoftSkinIcon(String softSkinUpdate) {
        String str = this.mSoftSkinValue;
        PortraitBeautySoftSkinController portraitBeautySoftSkinController = this.mPortraitBeautySoftSkinController;
        if (str.equalsIgnoreCase("Off")) {
            this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_off_menu);
            return;
        }
        String str2 = this.mSoftSkinValue;
        PortraitBeautySoftSkinController portraitBeautySoftSkinController2 = this.mPortraitBeautySoftSkinController;
        if (str2.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_LOW)) {
            this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_low_menu);
            return;
        }
        String str3 = this.mSoftSkinValue;
        PortraitBeautySoftSkinController portraitBeautySoftSkinController3 = this.mPortraitBeautySoftSkinController;
        if (str3.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_MID)) {
            this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_mid_menu);
            return;
        }
        String str4 = this.mSoftSkinValue;
        PortraitBeautySoftSkinController portraitBeautySoftSkinController4 = this.mPortraitBeautySoftSkinController;
        if (str4.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_HIGH)) {
            this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_high_menu);
        }
    }

    private void updateBeautyEffectView(String effect) {
        if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(effect)) {
            this.mImgVwSoftSkinEffect.setSelected(true);
            this.mImgVwWhiteSkinEffect.setSelected(false);
            this.mSpecialScreenView.setVisibility(0);
            this.mPortraitBeautyVerticalSeekBar.setVisibility(4);
            this.mImgVwSeekBarbg.setVisibility(4);
            this.mTxtVwPlusSign.setVisibility(4);
            this.mTxtVwMinusSign.setVisibility(4);
            this.mTxtVwOff.setVisibility(4);
            this.mImgWhiteSkinOffIcon.setVisibility(4);
            return;
        }
        if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(effect)) {
            this.mImgVwWhiteSkinEffect.setSelected(true);
            this.mImgVwSoftSkinEffect.setSelected(false);
            this.mSpecialScreenView.setVisibility(4);
            this.mPortraitBeautyVerticalSeekBar.setVisibility(0);
            this.mImgVwSeekBarbg.setVisibility(0);
            this.mTxtVwPlusSign.setVisibility(0);
            this.mTxtVwMinusSign.setVisibility(0);
            this.mTxtVwOff.setVisibility(0);
            this.mImgWhiteSkinOffIcon.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.selectedBeautyEffect)) {
            String itemId = this.mList.get(position);
            this.mSoftSkinValue = this.mService.getMenuItemValue(itemId);
            this.mService.getMenuItemList().indexOf(this.mSoftSkinValue);
            this.mSelectedItemPosition = this.mPortraitBeautySoftSkinController.getSupportedValue(PortraitBeautySoftSkinController.SOFTSKIN_CHANGE).indexOf(this.mSoftSkinValue) - 1;
            update(position);
            super.onItemSelected(parent, view, position, id);
        }
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
    }

    private void update(int position) {
        AppLog.enter("SelectEffectMenuLayout", AppLog.getMethodName());
        String itemId = this.mList.get(position);
        this.mSoftSkinValue = this.mService.getMenuItemValue(itemId);
        PortraitBeautySoftSkinController portraitBeautySoftSkinController = this.mPortraitBeautySoftSkinController;
        PortraitBeautySoftSkinController portraitBeautySoftSkinController2 = this.mPortraitBeautySoftSkinController;
        portraitBeautySoftSkinController.setValue(PortraitBeautySoftSkinController.SOFTSKIN_CHANGE, this.mSoftSkinValue);
        AppLog.exit("SelectEffectMenuLayout", AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class RunnableTask {
        private Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.menu.layout.SelectEffectMenuLayout.RunnableTask.1
            @Override // java.lang.Runnable
            public void run() {
                SelectEffectMenuLayout.this.mTxtVwWhiteEffectGuide.setVisibility(4);
                SelectEffectMenuLayout.this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
                SelectEffectMenuLayout.this.mTxtVwSoftSkinGuide.setVisibility(4);
                SelectEffectMenuLayout.this.mImgVwSoftSkinGuideArrow.setVisibility(4);
            }
        };

        public RunnableTask(Handler hnd) {
            this.handler = null;
            this.handler = hnd;
        }

        public void execute(int time) {
            this.handler.postDelayed(this.r, time);
        }

        public void removeCallbacks() {
            this.handler.removeCallbacks(this.r);
        }
    }

    private void setGuideVisibility(String effect) {
        if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(effect)) {
            this.mTxtVwWhiteEffectGuide.setVisibility(0);
            this.mImgVwWhiteEffectGuideArrow.setVisibility(0);
            this.mTxtVwSoftSkinGuide.setVisibility(4);
            this.mImgVwSoftSkinGuideArrow.setVisibility(4);
            return;
        }
        if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(effect)) {
            this.mTxtVwWhiteEffectGuide.setVisibility(4);
            this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
            this.mTxtVwSoftSkinGuide.setVisibility(0);
            this.mImgVwSoftSkinGuideArrow.setVisibility(0);
            return;
        }
        if (PortraitBeautyConstants.DEFOCUS_EFFECT.equals(effect)) {
            this.mTxtVwWhiteEffectGuide.setVisibility(4);
            this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
            this.mTxtVwSoftSkinGuide.setVisibility(4);
            this.mImgVwSoftSkinGuideArrow.setVisibility(4);
        }
    }

    private void handleVisibilityOfWhiteSkinGuide() {
        this.guideTask.execute(3000);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        this.mTxtVwWhiteEffectGuide.setVisibility(4);
        this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
        this.mTxtVwSoftSkinGuide.setVisibility(4);
        this.mImgVwSoftSkinGuideArrow.setVisibility(4);
        displayCaution(PortraitBeautyInfo.CAUTION_ID_DLAPP_MODE_DIAL_INVALID);
        return 1;
    }

    private void displayCaution(final int cautionId) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.portraitbeauty.menu.layout.SelectEffectMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case 103:
                    case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                    case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case 232:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 1;
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                    case AppRoot.USER_KEYCODE.FN /* 520 */:
                    case AppRoot.USER_KEYCODE.AEL /* 532 */:
                    case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                    case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
                    case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                    case AppRoot.USER_KEYCODE.SLIDE_AF_MF /* 596 */:
                    case AppRoot.USER_KEYCODE.DISP /* 608 */:
                    case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
                    case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
                    case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
                    case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                        return -1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
                        return 1;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    default:
                        return -1;
                }
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    default:
                        return -1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        pushedMenuKey();
        return 1;
    }
}
