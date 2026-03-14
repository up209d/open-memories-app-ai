package com.sony.imaging.app.portraitbeauty.menu.adjusteffect.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.AppContext;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.controller.PortraitBeautySoftSkinController;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyAdjustEffectState;
import com.sony.imaging.app.portraitbeauty.shooting.widget.PortraitBeautyVerticalSeekBar;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class AdjustEffectLayout extends SpecialScreenMenuLayout implements SeekBar.OnSeekBarChangeListener, SpecialScreenView.OnItemSelectedListener {
    private static final int GUIDE_DISPLAY_TIME = 3000;
    private static final int KEYCODE_ZOOM = 645;
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private ImageView mImgVwAdjustGuideBackground = null;
    private TextView mTxtVwAdjustGuide = null;
    private boolean mISGideInvisible = false;
    private boolean mISAdjustFinishGuideVisible = false;
    private ImageView mImgVwWhiteSkinEffect = null;
    private ImageView mImgVwSoftSkinEffect = null;
    private ImageView mImgVwSeekBarbg = null;
    private TextView mTxtVwPlusSign = null;
    private TextView mTxtVwMinusSign = null;
    private TextView mTxtVwEnterGuide = null;
    private TextView mTxtVwOff = null;
    private TextView adjustFinishOk = null;
    private FooterGuide mFooterGuide = null;
    private RelativeLayout mFinishAdjustVisuallyGuide = null;
    private TextView mTxtVwWhiteEffectGuide = null;
    private TextView mTxtVwSoftSkinGuide = null;
    private ImageView mImgVwWhiteEffectGuideArrow = null;
    private ImageView mImgVwSoftSkinGuideArrow = null;
    private ImageView mImgWhiteSkinOffIcon = null;
    private String mSelectedBeautyEffect = null;
    private final int DEFAULT_WHITE_SKIN_VALUE = 4;
    private final int ITEM_COUNT_4 = 4;
    private int mCautionID = 0;
    private String mDefocusValue = null;
    private int mWhiteSkinValue = 4;
    private PortraitBeautyVerticalSeekBar mPortraitBeautyVerticalSeekBar = null;
    private PortraitBeautySoftSkinController mPortraitBeautySoftSkinController = null;
    private NotificationListener mMediaNotificationListener = null;
    private NotificationListener mDisplayModeListener = null;
    private String mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_MID;
    private String mSelectedSoftSkinLevel = PortraitBeautySoftSkinController.SOFTSKIN_MID;
    private ArrayList<String> mMenuItemList = null;
    private Handler mHandlerBalloontGuide = null;
    private RunnableTask mGuideTask = null;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.adjust_effect_menu_layout);
        initializeView();
        this.mService = new BaseMenuService(AppContext.getAppContext());
        this.mService.setMenuItemId("AdjustEffect");
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(AppContext.getAppContext(), R.layout.menu_sub_adapter, this.mService);
        this.mAdapter.setMenuItemList(this.mService.getMenuItemList());
        this.mSpecialScreenView.setAdapter(this.mAdapter);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mTxtVwAdjustGuide.setVisibility(0);
        this.mImgVwAdjustGuideBackground.setVisibility(0);
        this.mTxtVwEnterGuide.setVisibility(0);
        this.mISAdjustFinishGuideVisible = false;
        this.adjustFinishOk.setSelected(true);
        this.mFooterGuide.setVisibility(4);
        this.mFinishAdjustVisuallyGuide.setVisibility(4);
        this.mISGideInvisible = true;
        this.mViewArea.intialize(AppContext.getAppContext(), this.mSpecialScreenView);
        this.mViewArea.setVisibility(4);
        this.mSelectedSoftSkinLevel = this.mSoftSkinValue;
        if (this.mSelectedBeautyEffect.equals(PortraitBeautyConstants.WHITE_SKIN_EFFECT)) {
            this.mImgVwWhiteSkinEffect.setSelected(true);
            this.mImgVwSoftSkinEffect.setSelected(false);
        } else if (this.mSelectedBeautyEffect.equals(PortraitBeautyConstants.SOFT_SKIN_EFFECT)) {
            this.mImgVwWhiteSkinEffect.setSelected(false);
            this.mImgVwSoftSkinEffect.setSelected(true);
        } else if (this.mSelectedBeautyEffect.equals(PortraitBeautyConstants.DEFOCUS_EFFECT)) {
        }
        updateSoftSkinIcon(this.mSoftSkinValue);
        updateWhiteSkinIcon(this.mWhiteSkinValue);
        initializeTask();
        setGuideVisibility(this.mSelectedBeautyEffect);
        handleVisibilityOfWhiteSkinGuide();
        this.mMenuItemList = this.mService.getMenuItemList();
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
        if (this.mMediaNotificationListener == null) {
            this.mMediaNotificationListener = new MediaNotificationListener();
        }
        MediaNotificationManager.getInstance().setNotificationListener(this.mMediaNotificationListener);
        if (this.mDisplayModeListener == null) {
            this.mDisplayModeListener = new DisplayModeListener();
        }
        DisplayModeObserver.getInstance().setNotificationListener(this.mDisplayModeListener);
        AppLog.enter(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        deInitializeTask();
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, Integer.valueOf(this.mWhiteSkinValue));
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_LEVEL_OF_SOFT_SKIN, this.mSoftSkinValue);
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_EFFECT, this.mSelectedBeautyEffect);
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        MediaNotificationManager.getInstance().removeNotificationListener(this.mMediaNotificationListener);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayModeListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        closeLayout();
        deinitializeView();
        this.mMediaNotificationListener = null;
        this.mDisplayModeListener = null;
        this.mCautionID = 0;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    private void deinitializeView() {
        this.mImgVwAdjustGuideBackground = null;
        this.mFooterGuide = null;
        this.mFinishAdjustVisuallyGuide = null;
        this.mTxtVwEnterGuide = null;
        this.mTxtVwAdjustGuide = null;
        this.mISGideInvisible = false;
        this.mImgVwWhiteSkinEffect = null;
        this.mImgVwSoftSkinEffect = null;
        this.mImgVwSeekBarbg = null;
        this.mTxtVwPlusSign = null;
        this.mTxtVwMinusSign = null;
        this.mTxtVwOff = null;
        this.mImgWhiteSkinOffIcon = null;
        this.adjustFinishOk = null;
        this.mTxtVwWhiteEffectGuide = null;
        this.mImgVwWhiteEffectGuideArrow = null;
        this.mTxtVwSoftSkinGuide = null;
        this.mImgVwSoftSkinGuideArrow = null;
        this.mSelectedBeautyEffect = null;
        this.mDefocusValue = null;
        this.mWhiteSkinValue = 4;
        this.mPortraitBeautyVerticalSeekBar = null;
        this.mPortraitBeautySoftSkinController = null;
        this.mSoftSkinValue = PortraitBeautySoftSkinController.SOFTSKIN_MID;
        this.mSelectedSoftSkinLevel = PortraitBeautySoftSkinController.SOFTSKIN_MID;
        this.mMenuItemList = null;
        this.mHandlerBalloontGuide = null;
        if (this.mGuideTask != null) {
            this.mGuideTask.removeCallbacks();
            this.mGuideTask = null;
        }
        this.mCurrentView = null;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar arg0) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar arg0) {
    }

    private void initializeTask() {
        if (this.mHandlerBalloontGuide == null) {
            this.mHandlerBalloontGuide = new Handler();
        }
        if (this.mGuideTask == null) {
            this.mGuideTask = new RunnableTask(this.mHandlerBalloontGuide);
        }
    }

    private void deInitializeTask() {
        if (this.mGuideTask != null) {
            this.mGuideTask.removeCallbacks();
        }
        this.mHandlerBalloontGuide = null;
    }

    private void initializeView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mSelectedBeautyEffect = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_EFFECT, PortraitBeautyConstants.WHITE_SKIN_EFFECT);
        this.mSoftSkinValue = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_LEVEL_OF_SOFT_SKIN, this.mSoftSkinValue);
        this.mWhiteSkinValue = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, this.mWhiteSkinValue);
        this.mImgVwAdjustGuideBackground = (ImageView) this.mCurrentView.findViewById(R.id.adjust_effect_guide_background);
        this.mTxtVwAdjustGuide = (TextView) this.mCurrentView.findViewById(R.id.adjust_effect_guide_txt);
        this.mImgVwWhiteSkinEffect = (ImageView) this.mCurrentView.findViewById(R.id.whitening_effect);
        this.mImgVwSoftSkinEffect = (ImageView) this.mCurrentView.findViewById(R.id.softskin_effect);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mFinishAdjustVisuallyGuide = (RelativeLayout) this.mCurrentView.findViewById(R.id.finish_adjust_visually);
        this.mImgVwSeekBarbg = (ImageView) this.mCurrentView.findViewById(R.id.seek_bar_bg);
        this.mTxtVwPlusSign = (TextView) this.mCurrentView.findViewById(R.id.vsb_plus);
        this.mTxtVwEnterGuide = (TextView) this.mCurrentView.findViewById(R.id.enter_guide_txt);
        this.mTxtVwMinusSign = (TextView) this.mCurrentView.findViewById(R.id.vsb_minus);
        this.mTxtVwOff = (TextView) this.mCurrentView.findViewById(R.id.vsb_off);
        this.mImgWhiteSkinOffIcon = (ImageView) this.mCurrentView.findViewById(R.id.vsb_off_icon);
        this.adjustFinishOk = (TextView) this.mCurrentView.findViewById(R.id.finish_adjust_visually_ok);
        this.adjustFinishOk.setSelected(true);
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mSpecialScreenView.setOnSpecialScreenItemSelectedListener(this);
        this.mPortraitBeautySoftSkinController = PortraitBeautySoftSkinController.getInstance();
        this.mSpecialScreenView.setVisibility(4);
        this.mTxtVwWhiteEffectGuide = (TextView) this.mCurrentView.findViewById(R.id.whitening_effect_guide);
        this.mImgVwWhiteEffectGuideArrow = (ImageView) this.mCurrentView.findViewById(R.id.whitening_effect_guide_arrow);
        this.mTxtVwSoftSkinGuide = (TextView) this.mCurrentView.findViewById(R.id.softskin_effect_guide);
        this.mImgVwSoftSkinGuideArrow = (ImageView) this.mCurrentView.findViewById(R.id.softskin_effect_guide_arrow);
        this.mPortraitBeautyVerticalSeekBar = (PortraitBeautyVerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_seekbar);
        this.mPortraitBeautyVerticalSeekBar.setMax(7);
        this.mPortraitBeautyVerticalSeekBar.setProgress(this.mWhiteSkinValue);
        updateBeautyEffectView(this.mSelectedBeautyEffect);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        disapperAllGuideOnKeyDown();
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        disapperAllGuideOnKeyDown();
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        disapperAllGuideOnKeyDown();
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        if (true == this.mISGideInvisible || this.mFinishAdjustVisuallyGuide.getVisibility() == 0) {
            return -1;
        }
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(this.mSelectedBeautyEffect)) {
            this.mSelectedBeautyEffect = PortraitBeautyConstants.SOFT_SKIN_EFFECT;
            setGaugeBarVisibility();
            updateBeautyEffectView(this.mSelectedBeautyEffect);
        } else if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.mSelectedBeautyEffect)) {
            this.mSelectedBeautyEffect = PortraitBeautyConstants.WHITE_SKIN_EFFECT;
            updateBeautyEffectView(this.mSelectedBeautyEffect);
        }
        this.mGuideTask.removeCallbacks();
        setGuideVisibility(this.mSelectedBeautyEffect);
        handleVisibilityOfWhiteSkinGuide();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    private void setGaugeBarVisibility() {
        this.mTxtVwPlusSign.setVisibility(0);
        this.mTxtVwMinusSign.setVisibility(0);
        this.mTxtVwOff.setVisibility(0);
        this.mImgWhiteSkinOffIcon.setVisibility(0);
        this.mPortraitBeautyVerticalSeekBar.setVisibility(0);
        this.mImgVwSeekBarbg.setVisibility(0);
    }

    private void setGaugeBarInvisibilty() {
        this.mTxtVwPlusSign.setVisibility(4);
        this.mTxtVwMinusSign.setVisibility(4);
        this.mTxtVwOff.setVisibility(4);
        this.mImgWhiteSkinOffIcon.setVisibility(4);
        this.mPortraitBeautyVerticalSeekBar.setVisibility(4);
        this.mImgVwSeekBarbg.setVisibility(4);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        if (true == this.mISGideInvisible || this.mFinishAdjustVisuallyGuide.getVisibility() == 0) {
            return -1;
        }
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.mSelectedBeautyEffect)) {
            this.mSelectedBeautyEffect = PortraitBeautyConstants.WHITE_SKIN_EFFECT;
            this.mPortraitBeautyVerticalSeekBar.setMax(7);
            this.mPortraitBeautyVerticalSeekBar.setProgress(this.mWhiteSkinValue);
            updateBeautyEffectView(this.mSelectedBeautyEffect);
        } else if (PortraitBeautyConstants.WHITE_SKIN_EFFECT.equals(this.mSelectedBeautyEffect)) {
            this.mSelectedBeautyEffect = PortraitBeautyConstants.SOFT_SKIN_EFFECT;
            setGaugeBarVisibility();
            updateBeautyEffectView(this.mSelectedBeautyEffect);
        }
        this.mGuideTask.removeCallbacks();
        setGuideVisibility(this.mSelectedBeautyEffect);
        handleVisibilityOfWhiteSkinGuide();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
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
        if (this.mSoftSkinValue.equalsIgnoreCase("Off")) {
            this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_off_menu);
            return;
        }
        if (this.mSoftSkinValue.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_LOW)) {
            this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_low_menu);
        } else if (this.mSoftSkinValue.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_MID)) {
            this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_mid_menu);
        } else if (this.mSoftSkinValue.equalsIgnoreCase(PortraitBeautySoftSkinController.SOFTSKIN_HIGH)) {
            this.mImgVwSoftSkinEffect.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_softskin_high_menu);
        }
    }

    private void updateBeautyEffectView(String effect) {
        if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(effect)) {
            this.mImgVwWhiteSkinEffect.setSelected(false);
            this.mImgVwSoftSkinEffect.setSelected(true);
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
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (PortraitBeautyConstants.SOFT_SKIN_EFFECT.equals(this.mSelectedBeautyEffect)) {
            String itemId = this.mMenuItemList.get(position);
            this.mSoftSkinValue = this.mService.getMenuItemValue(itemId);
            this.mService.getMenuItemList().indexOf(this.mSoftSkinValue);
            update(position);
            int softskinLevel = this.mPortraitBeautySoftSkinController.convValue_String2Int(this.mSoftSkinValue);
            if (PortraitBeautyAdjustEffectState.sAdjustEffectChangeListener != null) {
                BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, Integer.valueOf(this.mWhiteSkinValue));
                BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_LEVEL_OF_SOFT_SKIN, this.mSoftSkinValue);
                PortraitBeautyAdjustEffectState.sAdjustEffectChangeListener.onUpdateEffect(this.mWhiteSkinValue, softskinLevel);
            }
            super.onItemSelected(parent, view, position, id);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void finishAdjustVisuallyTransition() {
        this.mFinishAdjustVisuallyGuide.setVisibility(4);
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        closeLayout();
        getHandler().sendEmptyMessage(PortraitBeautyAdjustEffectState.TRANSIT_TO_SHOOTING_FROM_ADJUSTEFFECT);
    }

    private void update(int position) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String itemId = this.mMenuItemList.get(position);
        this.mSoftSkinValue = this.mService.getMenuItemValue(itemId);
        this.mPortraitBeautySoftSkinController.setValue(PortraitBeautySoftSkinController.SOFTSKIN_CHANGE, this.mSoftSkinValue);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x000c. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:4:0x0011 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:6:0x03d5  */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int onKeyDown(int r9, android.view.KeyEvent r10) {
        /*
            Method dump skipped, instructions count: 1078
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.portraitbeauty.menu.adjusteffect.layout.AdjustEffectLayout.onKeyDown(int, android.view.KeyEvent):int");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class RunnableTask {
        private Handler handler;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.menu.adjusteffect.layout.AdjustEffectLayout.RunnableTask.1
            @Override // java.lang.Runnable
            public void run() {
                AdjustEffectLayout.this.mTxtVwWhiteEffectGuide.setVisibility(4);
                AdjustEffectLayout.this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
                AdjustEffectLayout.this.mTxtVwSoftSkinGuide.setVisibility(4);
                AdjustEffectLayout.this.mImgVwSoftSkinGuideArrow.setVisibility(4);
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
        this.mGuideTask.execute(3000);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        if (!this.mISAdjustFinishGuideVisible) {
            return 0;
        }
        finishAdjustVisuallyTransition();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        if (!this.mISAdjustFinishGuideVisible) {
            return 0;
        }
        finishAdjustVisuallyTransition();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        if (!this.mISAdjustFinishGuideVisible) {
            return -1;
        }
        finishAdjustVisuallyTransition();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayCaution(final int cautionId) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.portraitbeauty.menu.adjusteffect.layout.AdjustEffectLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case 103:
                    case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                    case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                    case AppRoot.USER_KEYCODE.FN /* 520 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                    case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                    case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                    case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                    case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                    case AppRoot.USER_KEYCODE.AEL /* 532 */:
                    case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                    case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
                    case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                    case AppRoot.USER_KEYCODE.SLIDE_AF_MF /* 596 */:
                    case AppRoot.USER_KEYCODE.DISP /* 608 */:
                    case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
                    case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                    case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
                    case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
                    case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
                    case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                    case AdjustEffectLayout.KEYCODE_ZOOM /* 645 */:
                        return -1;
                    case 232:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        AdjustEffectLayout.this.mCautionID = 0;
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        AdjustEffectLayout.this.mCautionID = 0;
                        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
                        return 1;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        AdjustEffectLayout.this.mCautionID = 0;
                        AdjustEffectLayout.this.getHandler().sendEmptyMessage(PortraitBeautyAdjustEffectState.TRANSIT_TO_SHOOTING_FROM_ADJUSTEFFECT);
                        return 1;
                    default:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        AdjustEffectLayout.this.mCautionID = 0;
                        return 0;
                }
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        AdjustEffectLayout.this.getHandler().sendEmptyMessage(PortraitBeautyAdjustEffectState.TRANSIT_TO_SHOOTING_FROM_ADJUSTEFFECT);
                        AdjustEffectLayout.this.mCautionID = 0;
                        return 1;
                    default:
                        return -1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onAFMFSwitchChanged() {
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfAelKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAFMFKey() {
        return -1;
    }

    public void disapperAllGuideOnKeyDown() {
        this.mTxtVwWhiteEffectGuide.setVisibility(4);
        this.mImgVwWhiteEffectGuideArrow.setVisibility(4);
        this.mTxtVwSoftSkinGuide.setVisibility(4);
        this.mImgVwSoftSkinGuideArrow.setVisibility(4);
    }

    /* loaded from: classes.dex */
    private class MediaNotificationListener implements NotificationListener {
        private String[] tags;

        private MediaNotificationListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            int state = MediaNotificationManager.getInstance().getMediaState();
            if (AdjustEffectLayout.this.mCautionID == 131228 && 1 == state) {
                AdjustEffectLayout.this.displayCaution(PortraitBeautyInfo.CAUTION_ID_DLAPP_DEFOCUS_INVALID);
            }
        }
    }

    /* loaded from: classes.dex */
    private class DisplayModeListener implements NotificationListener {
        private DisplayModeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (PortraitBeautyAdjustEffectState.sAdjustEffectChangeListener != null && AdjustEffectLayout.this.mSoftSkinValue != null) {
                PortraitBeautyAdjustEffectState.sAdjustEffectChangeListener.onUpdateEffect(AdjustEffectLayout.this.mWhiteSkinValue, AdjustEffectLayout.this.mPortraitBeautySoftSkinController.convValue_String2Int(AdjustEffectLayout.this.mSoftSkinValue));
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return new String[]{DisplayModeObserver.TAG_DEVICE_CHANGE};
        }
    }
}
