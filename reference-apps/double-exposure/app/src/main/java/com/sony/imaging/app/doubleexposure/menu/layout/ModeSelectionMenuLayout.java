package com.sony.imaging.app.doubleexposure.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppContext;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DESA;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureBackUpKey;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ModeSelectionController;
import com.sony.imaging.app.doubleexposure.shooting.state.DoubleExposureShootingMenuState;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ModeSelectionMenuLayout extends SpecialScreenMenuLayout implements SpecialScreenView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {
    private final String TAG = AppLog.getClassName();
    private final String DEFAULT_BLEND_MODE_VALUE = ModeSelectionController.ADD;
    private final int ITEM_COUNT_5 = 5;
    private ViewGroup mCurrentView = null;
    private ImageView mImgVwShadeArea = null;
    private FooterGuide mFooterGuide = null;
    private RelativeLayout mLayoutWeightSelection = null;
    private TextView mTxtVwScreenTitle = null;
    private TextView mTxtVwItemName = null;
    private SeekBar mSeekBarWeight = null;
    private int mWeightedAvgValue = 5;
    private int mLastWeightedAvgValue = 5;
    private int mSelectedBlendModeValue = 0;
    private String mSelectedBlendMode = ModeSelectionController.ADD;
    private String mLastSelectedBlendMode = ModeSelectionController.ADD;
    private ModeSelectionController mModeSelectionController = null;
    private DoubleExposureUtil mDoubleExposureUtil = null;
    private MediaNotificationManager mMediaNotificationManager = null;
    private MediaMountEventListener mMediaMountEventListener = null;
    private boolean mIsTurnedEVDial = false;
    private ArrayList<String> mList = null;
    protected Layout mLayoutStatus = Layout.SUB;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public enum Layout {
        SUB,
        OPTION
    }

    protected void setLayoutStatus(Layout layout) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mLayoutStatus = layout;
        if (this.mImgVwShadeArea != null) {
            this.mImgVwShadeArea.setVisibility(Layout.SUB == layout ? 4 : 0);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.mode_selection_layout);
        Context context = getActivity().getApplicationContext();
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mService = new BaseMenuService(context);
        this.mOptionService = new BaseMenuService(context);
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mService);
        this.mOptionAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mOptionService);
        this.mImgVwShadeArea = (ImageView) this.mCurrentView.findViewById(R.id.specialscreen_shade_area);
        if (this.mImgVwShadeArea != null) {
            this.mImgVwShadeArea.setVisibility(Layout.SUB == this.mLayoutStatus ? 4 : 0);
        }
        this.mSeekBarWeight = (SeekBar) this.mCurrentView.findViewById(R.id.progress_bar);
        this.mSeekBarWeight.setOnSeekBarChangeListener(this);
        this.mSeekBarWeight.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.doubleexposure.menu.layout.ModeSelectionMenuLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.mTxtVwScreenTitle = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mTxtVwItemName = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        this.mLayoutWeightSelection = (RelativeLayout) this.mCurrentView.findViewById(R.id.weight_progress);
        this.mLayoutWeightSelection.setVisibility(4);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        this.mDoubleExposureUtil = DoubleExposureUtil.getInstance();
        this.mDoubleExposureUtil.setCurrentMenuLayout(DoubleExposureShootingMenuState.ID_MODESELECTIONMENULAYOUT);
        this.mLastSelectedBlendMode = BackUpUtil.getInstance().getPreferenceString(DoubleExposureBackUpKey.KEY_SELECTED_MODE, ModeSelectionController.ADD);
        this.mSelectedBlendMode = this.mLastSelectedBlendMode;
        int position = this.mService.getMenuItemList().indexOf("ModeSelection_" + this.mSelectedBlendMode);
        int mBlock = getMenuItemList().size();
        if (mBlock > 5) {
            mBlock = 5;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, position);
        this.mLastWeightedAvgValue = BackUpUtil.getInstance().getPreferenceInt(DoubleExposureBackUpKey.KEY_WEIGHTED_AVERAGE_VALUE, 5);
        this.mSeekBarWeight.setProgress(this.mLastWeightedAvgValue - 1);
        this.mWeightedAvgValue = this.mLastWeightedAvgValue;
        this.mModeSelectionController = ModeSelectionController.getInstance();
        ModeSelectionController.getInstance().setValue(ModeSelectionController.MODESELECTION, this.mSelectedBlendMode);
        if (this.mFooterGuide != null) {
            if (1 == Environment.getVersionOfHW()) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FOOTERGUIDE_GUIDE_RETURN_FOR_SK));
            } else {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FOOTERGUIDE_GUIDE_RETURN));
            }
        }
        setLayoutStatus(Layout.SUB);
        this.mList = this.mService.getSupportedItemList();
        updateGuideView(position);
        this.mMediaNotificationManager = MediaNotificationManager.getInstance();
        if (this.mMediaMountEventListener == null) {
            this.mMediaMountEventListener = new MediaMountEventListener();
        }
        this.mMediaNotificationManager.setNotificationListener(this.mMediaMountEventListener);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int retVal = 1;
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            pushedGuideFuncKey();
        } else {
            retVal = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            return 1;
        }
        int returnState = super.onKeyUp(keyCode, event);
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mIsTurnedEVDial) {
            DoubleExposureUtil.getInstance().updateExposureCompensation();
            this.mIsTurnedEVDial = false;
        }
        if (this.mMediaNotificationManager != null) {
            this.mMediaNotificationManager.removeNotificationListener(this.mMediaMountEventListener);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        saveCurrentModeSettings();
        this.mMediaNotificationManager = null;
        this.mMediaMountEventListener = null;
        this.mDoubleExposureUtil = null;
        this.mSpecialScreenView = null;
        this.mService = null;
        this.mOptionService = null;
        this.mAdapter = null;
        this.mOptionAdapter = null;
        this.mImgVwShadeArea = null;
        this.mTxtVwScreenTitle = null;
        this.mTxtVwItemName = null;
        this.mFooterGuide = null;
        this.mSelectedBlendMode = null;
        this.mLayoutWeightSelection = null;
        this.mSeekBarWeight = null;
        this.mCurrentView = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, "Menu Item Postion: " + position);
        String itemId = this.mList.get(position);
        this.mSelectedBlendMode = this.mService.getMenuItemValue(itemId);
        this.mSelectedBlendModeValue = position;
        DESA.getInstance().setBlendMode(this.mSelectedBlendModeValue);
        DESA.getInstance().updateLiveViewEffect();
        super.onItemSelected(parent, view, position, id);
        updateLayoutItems();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onOptionItemSelected(SpecialScreenView parent, View view, int position, long id) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        updateGuideView(position);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mSelectedBlendMode.equalsIgnoreCase(ModeSelectionController.WEIGHTED)) {
            this.mWeightedAvgValue = this.mLastWeightedAvgValue;
            this.mSeekBarWeight.setProgress(this.mWeightedAvgValue - 1);
        }
        this.mSelectedBlendMode = this.mLastSelectedBlendMode;
        this.mModeSelectionController.setValue(ModeSelectionController.MODESELECTION, this.mSelectedBlendMode);
        if (!this.mDoubleExposureUtil.IsTransitionFlag()) {
            this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
        } else {
            this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.SECOND_SHOOTING);
            int mLastSelectedBlendModeValue = ModeSelectionController.getInstance().getSupportedValue(ModeSelectionController.MODESELECTION).indexOf(this.mSelectedBlendMode) - 1;
            DESA.getInstance().setBlendMode(mLastSelectedBlendModeValue);
            if (this.mSelectedBlendMode.equalsIgnoreCase(ModeSelectionController.WEIGHTED)) {
                DESA.getInstance().setWeightMeanValue((int) (this.mWeightedAvgValue * 25.5f));
            }
            DESA.getInstance().updateLiveViewEffect();
            this.mDoubleExposureUtil.setTransitionFlag(false);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.SECOND_SHOOTING);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int retVal = -1;
        if (this.mSelectedBlendMode.equalsIgnoreCase(ModeSelectionController.WEIGHTED)) {
            this.mSeekBarWeight.setFocusable(true);
            this.mSeekBarWeight.requestFocus();
            updatePosition(true);
            retVal = 1;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int retVal = -1;
        if (this.mSelectedBlendMode.equalsIgnoreCase(ModeSelectionController.WEIGHTED)) {
            this.mSeekBarWeight.setFocusable(true);
            this.mSeekBarWeight.requestFocus();
            updatePosition(false);
            retVal = 1;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.closeMenuLayout(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.SECOND_SHOOTING);
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return DoubleExposureShootingMenuState.ID_THEMESELECTIONMENULAYOUT;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int position = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(position);
        guideResources.add(this.mService.getMenuItemText(itemId));
        guideResources.add(this.mService.getMenuItemGuideText(itemId));
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mIsTurnedEVDial = true;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.turnedEVDial();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mWeightedAvgValue = arg1 + 1;
        AppLog.info(this.TAG, "Weight Mean Value: " + this.mWeightedAvgValue);
        DESA.getInstance().setWeightMeanValue((int) (this.mWeightedAvgValue * 25.5f));
        DESA.getInstance().updateLiveViewEffect();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar arg0) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar arg0) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void updateLayoutItems() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        this.mTxtVwItemName.setText(this.mService.getMenuItemText(itemId));
        this.mTxtVwItemName.setVisibility(0);
        this.mModeSelectionController.setValue(ModeSelectionController.MODESELECTION, this.mSelectedBlendMode);
        if (this.mLayoutWeightSelection != null) {
            if (value.equalsIgnoreCase(ModeSelectionController.WEIGHTED)) {
                this.mLayoutWeightSelection.setVisibility(0);
                this.mSeekBarWeight.setFocusable(true);
                this.mSeekBarWeight.requestFocus();
            } else {
                this.mLayoutWeightSelection.setVisibility(4);
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void updateGuideView(int position) {
        if (this.mTxtVwScreenTitle != null) {
            this.mTxtVwScreenTitle.setText(R.string.STRID_FUNC_MLE_BLEND);
        }
        this.mSelectedBlendModeValue = position;
        if (DESA.getInstance() != null) {
            DESA.getInstance().setBlendMode(this.mSelectedBlendModeValue);
            DESA.getInstance().updateLiveViewEffect();
        }
        if (this.mViewArea != null) {
            this.mViewArea.update();
        }
        AppLog.info(this.TAG, "Menu Item Postion: " + position);
        updateLayoutItems();
    }

    private void saveCurrentModeSettings() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, "mSelectedLevel = " + this.mSelectedBlendMode);
        BackUpUtil.getInstance().setPreference(DoubleExposureBackUpKey.KEY_SELECTED_MODE, this.mSelectedBlendMode);
        BackUpUtil.getInstance().setPreference(DoubleExposureBackUpKey.KEY_WEIGHTED_AVERAGE_VALUE, Integer.valueOf(this.mWeightedAvgValue));
        setKikiLog(this.mSelectedBlendMode);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void setKikiLog(String blendMode) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int kikilogSubId = 0;
        if (ModeSelectionController.ADD.equals(blendMode)) {
            kikilogSubId = 4198;
        } else if (ModeSelectionController.WEIGHTED.equals(blendMode)) {
            kikilogSubId = 4199;
        } else if (ModeSelectionController.LIGHTEN.equals(blendMode)) {
            kikilogSubId = 4200;
        } else if (ModeSelectionController.DARKEN.equals(blendMode)) {
            kikilogSubId = 4201;
        } else if (ModeSelectionController.SCREEN.equals(blendMode)) {
            kikilogSubId = 4202;
        } else if (ModeSelectionController.MULTIPLY.equals(blendMode)) {
            kikilogSubId = 4203;
        }
        this.mDoubleExposureUtil.startKikiLog(kikilogSubId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void updatePosition(boolean isRight) {
        int currentPos;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int currentPos2 = this.mSeekBarWeight.getProgress();
        if (isRight) {
            currentPos = currentPos2 + 1;
        } else {
            currentPos = currentPos2 - 1;
        }
        if (currentPos >= 0 && currentPos <= 8) {
            this.mSeekBarWeight.setProgress(currentPos);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    private class MediaMountEventListener implements NotificationListener {
        private final String[] tags;

        private MediaMountEventListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(ModeSelectionMenuLayout.this.TAG, tag);
            if (MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE.equals(tag) && ModeSelectionMenuLayout.this.mMediaNotificationManager != null && !ModeSelectionMenuLayout.this.mMediaNotificationManager.isMounted()) {
                ModeSelectionMenuLayout.this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
            }
        }
    }
}
