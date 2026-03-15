package com.sony.imaging.app.timelapse.menu.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.databaseutil.AppContext;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.FrameRateController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.PTag;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TLThemeSelectionLayout extends SpecialScreenMenuLayout {
    private static final String BACK = "back";
    private static final String TAG = "TLThemeSelectionLayout";
    private int mAeStatus;
    private BackUpUtil mBackUtil;
    private ImageView mBlack;
    private int mCurrentSelectedState;
    private int mInterval;
    private int mLastStoredState;
    private String mRecordingMode;
    private int mShootingNum;
    ViewGroup mCurrentView = null;
    private ImageView mBackgroundImageView = null;
    private ImageView mRecMode = null;
    private ImageView mAEStatus = null;
    private TextView mGuideTextView = null;
    private TextView mThemeInterval = null;
    private TextView mThemeShootingNum = null;
    private TextView mThemeShootingTime = null;
    private TextView mThemeMenuItemName = null;
    private HistoryItem mLastItemId = null;
    private FooterGuide mFooterGuide = null;
    private TLCommonUtil mTlCommonUtil = null;
    private final int TIMELAPSE_LITE_THEME_POSITION = 0;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.tl_layout_menu_theme_selection);
        this.mTlCommonUtil = TLCommonUtil.getInstance();
        initializeView();
        if (this.mBlack == null) {
            this.mBlack = new ImageView(AppContext.getAppContext());
            this.mBlack.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.mBlack.setImageResource(android.R.color.black);
            this.mCurrentView.addView(this.mBlack, 0);
        }
        return this.mCurrentView;
    }

    private void initializeView() {
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mItemNameView = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mBackgroundImageView = (ImageView) this.mCurrentView.findViewById(R.id.sceneselection_icon);
        this.mGuideTextView = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        this.mThemeInterval = (TextView) this.mCurrentView.findViewById(R.id.interval_text);
        this.mThemeShootingNum = (TextView) this.mCurrentView.findViewById(R.id.theme_selection_shooting_num);
        this.mThemeShootingTime = (TextView) this.mCurrentView.findViewById(R.id.theme_selection_shooting_time);
        this.mThemeMenuItemName = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        if (this.mThemeMenuItemName != null) {
            this.mThemeMenuItemName.setGravity(17);
        }
        this.mRecMode = (ImageView) this.mCurrentView.findViewById(R.id.theme_selection_recording_mode);
        this.mAEStatus = (ImageView) this.mCurrentView.findViewById(R.id.theme_selection_al_status);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        getLastStoredValues();
        setFooterGuide();
        update(TLCommonUtil.getInstance().getCurrentState());
    }

    private void setFooterGuide() {
        if (this.mLastItemId == null) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_FOOTERGUIDE_OPTION_EXITAPP, R.string.STRID_FUNC_TIMELAPSE_FOOTERGUIDE_OPTION_EXITAPP_SK));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_FOOTERGUIDE_OPTION_RETURN, R.string.STRID_FUNC_TIMELAPSE_FOOTERGUIDE_OPTION_RETURN_SK));
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (TLCommonUtil.getInstance().isTimelapseLiteApplication()) {
            this.mTlCommonUtil.setCurrentState(6);
            this.mTlCommonUtil.setSelectedThemeName(String.valueOf(getResources().getString(R.string.STRID_AMC_STR_01016)));
        } else {
            this.mTlCommonUtil.setCurrentState(TimeLapseConstants.THEME_STATE_ARRAY[this.mCurrentSelectedState]);
            this.mTlCommonUtil.setSelectedThemeName(String.valueOf(this.mItemNameView.getText()));
        }
        TLCommonUtil.getInstance().setJpegDummyQuality();
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        if (bundle == null) {
            this.mTlCommonUtil.setBootFactor(2);
        }
        super.closeMenuLayout(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mSpecialScreenView = null;
        this.mService = null;
        this.mAdapter = null;
        this.mScreenTitleView = null;
        if (this.mBackgroundImageView != null) {
            this.mBackgroundImageView.setImageResource(0);
        }
        this.mBackgroundImageView = null;
        this.mGuideTextView = null;
        this.mBlack = null;
        super.onDestroyView();
    }

    protected void update(int position) {
        AppLog.enter(TAG, "started time for update theme selection view");
        this.mScreenTitleView.setText("");
        this.mCurrentSelectedState = position;
        BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIMELAPSE_CURRENT_STATE, Integer.valueOf(position));
        if (TLCommonUtil.getInstance().isTimelapseLiteApplication()) {
            position = 0;
        }
        String itemid = this.mAdapter.getItem(position);
        if (this.mItemNameView != null) {
            this.mItemNameView.setText(this.mService.getMenuItemText(itemid));
            this.mItemNameView.setVisibility(0);
        }
        if (this.mGuideTextView != null) {
            this.mGuideTextView.setText(this.mService.getMenuItemGuideText(itemid));
            this.mGuideTextView.setVisibility(0);
        }
        updateThemeProperty(TLShootModeSettingController.getInstance().mThemeList.indexOf(itemid));
        AppLog.exit(TAG, "started time for update theme selection view");
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void openThemeOptionMenu() {
        if (this.mSpecialScreenView != null) {
            String itemId = this.mAdapter.getItem(this.mSpecialScreenView.getSelectedItemPosition());
            doOnKeyProcessing(itemId);
        }
    }

    protected void doOnKeyProcessing(String itemid) {
        String nextFragmentID;
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (this.mService != null) {
            if (!this.mService.isMenuItemValid(itemid)) {
                requestCautionTrigger(itemid);
                return;
            }
            if (TimeLapseConstants.EXTENSTION_CUSTOM_THEME_SETTING) {
                nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
            } else {
                nextFragmentID = "ID_TLTHEMEOPTIONLAYOUT";
            }
            this.mService.execCurrentMenuItem(itemid);
            if (nextFragmentID != null) {
                PTag.start("Menu open next MenuLayout");
                openNextMenu(itemid, nextFragmentID);
            } else {
                Log.e(TAG, "Can't open the next MenuLayout");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.mLastItemId == null) {
            getActivity().finish();
            return 1;
        }
        int returnState = super.pushedMenuKey();
        return returnState;
    }

    private void setPreviousMenuID() {
        this.mLastItemId = this.mService.popMenuHistory();
        this.mService.pushMenuHistory(this.mLastItemId);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            this.mTlCommonUtil.setThemeChanged(true);
            openThemeOptionMenu();
            return 1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }

    private void updateThemeProperty(int selectedThemePos) {
        updateThemeFromBackup(selectedThemePos);
        this.mThemeShootingTime.setText(this.mTlCommonUtil.getRecordingTime(this.mInterval * (this.mShootingNum - 1)));
        this.mThemeShootingNum.setText("" + this.mShootingNum);
        this.mThemeInterval.setText(this.mTlCommonUtil.getTimeString(this.mInterval, this));
        updateRecModeIcon();
        updateAEStatusIcon();
    }

    private void updateThemeFromBackup(int mCurrentState) {
        int resID;
        if (this.mBackUtil == null) {
            this.mBackUtil = BackUpUtil.getInstance();
        }
        switch (mCurrentState) {
            case 0:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.CLOUDY_SKY_INTERVAL_KEY, 5);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.CLOUDY_SKY_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.CLOUDY_SKY_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.CLOUDY_SKY_AEL_STATUS_KEY, 0);
                resID = R.drawable.p_16_dd_parts_tm_image_cloudysky;
                break;
            case 1:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SKY_INTERVAL_KEY, 30);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SKY_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.NIGHTY_SKY_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SKY_AEL_STATUS_KEY, 0);
                resID = R.drawable.p_16_dd_parts_tm_image_nightysky;
                break;
            case 2:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SCENE_INTERVAL_KEY, 3);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SCENE_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.NIGHTY_SCENE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.NIGHTY_SCENE_AEL_STATUS_KEY, 0);
                resID = R.drawable.p_16_dd_parts_tm_image_nightscn;
                break;
            case 3:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.SUN_SET_INTERVAL_KEY, 10);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.SUN_SET_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.SUN_SET_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                if (this.mTlCommonUtil.isOlderPFVersion()) {
                    this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.SUN_SET_AEL_STATUS_KEY, 4);
                } else {
                    this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.SUN_SET_AEL_STATUS_KEY, 3);
                    if (4 == this.mAeStatus) {
                        this.mAeStatus = 3;
                        this.mBackUtil.setPreference(TimeLapseConstants.SUN_SET_AEL_STATUS_KEY, 3);
                    }
                }
                resID = R.drawable.p_16_dd_parts_tm_image_sunset;
                break;
            case 4:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.SUN_RISE_INTERVAL_KEY, 10);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.SUN_RISE_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.SUN_RISE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                if (this.mTlCommonUtil.isOlderPFVersion()) {
                    this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.SUN_RISE_AEL_STATUS_KEY, 4);
                } else {
                    this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.SUN_RISE_AEL_STATUS_KEY, 3);
                    if (4 == this.mAeStatus) {
                        this.mAeStatus = 3;
                        this.mBackUtil.setPreference(TimeLapseConstants.SUN_RISE_AEL_STATUS_KEY, 3);
                    }
                }
                resID = R.drawable.p_16_dd_parts_tm_image_sunrise;
                break;
            case 5:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.MINIATURE_INTERVAL_KEY, 2);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.MINIATURE_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.MINIATURE_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.MINIATURE_AEL_STATUS_KEY, 0);
                resID = R.drawable.p_16_dd_parts_tm_image_miniature;
                break;
            case 6:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.STANDARD_INTERVAL_KEY, 1);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.STANDARD_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.STANDARD_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.STANDARD_AEL_STATUS_KEY, 0);
                resID = R.drawable.p_16_dd_parts_tm_image_standard;
                break;
            case 7:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.CUSTOM_INTERVAL_KEY, 1);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.CUSTOM_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.CUSTOM_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.CUSTOM_AEL_STATUS_KEY, 0);
                if (!this.mTlCommonUtil.isOlderPFVersion() && 4 == this.mAeStatus) {
                    this.mAeStatus = 3;
                    this.mBackUtil.setPreference(TimeLapseConstants.CUSTOM_AEL_STATUS_KEY, 3);
                }
                resID = R.drawable.p_16_dd_parts_tm_image_custom;
                break;
            default:
                this.mInterval = this.mBackUtil.getPreferenceInt(TimeLapseConstants.STANDARD_INTERVAL_KEY, 1);
                this.mShootingNum = this.mBackUtil.getPreferenceInt(TimeLapseConstants.STANDARD_SHOOTING_NUM_KEY, 240);
                this.mRecordingMode = this.mBackUtil.getPreferenceString(TimeLapseConstants.STANDARD_FILE_FORMAT_KEY, "MOVIE_FORMAT");
                this.mAeStatus = this.mBackUtil.getPreferenceInt(TimeLapseConstants.STANDARD_AEL_STATUS_KEY, 0);
                resID = R.drawable.p_16_dd_parts_tm_image_standard;
                break;
        }
        this.mBackgroundImageView.setBackgroundResource(0);
        this.mBackgroundImageView.setImageResource(0);
        this.mBackgroundImageView.setImageDrawable(null);
        releaseImageViewDrawable(this.mBackgroundImageView);
        System.gc();
        this.mBackgroundImageView.setBackgroundResource(resID);
    }

    private void updateAEStatusIcon() {
        switch (this.mAeStatus) {
            case 1:
                this.mAEStatus.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_tracking_hi);
                return;
            case 2:
                this.mAEStatus.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_tracking_mid);
                return;
            case 3:
                this.mAEStatus.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_tracking_lo);
                return;
            case 4:
                this.mAEStatus.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_tracking);
                return;
            default:
                this.mAEStatus.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_ael);
                return;
        }
    }

    private void updateRecModeIcon() {
        if (this.mRecordingMode.equalsIgnoreCase("MOVIE_FORMAT")) {
            if ("framerate-24p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
                this.mRecMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
                return;
            } else {
                this.mRecMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
                return;
            }
        }
        if (this.mRecordingMode.equalsIgnoreCase(TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL)) {
            this.mRecMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_menu_normal);
        } else if (this.mRecordingMode.equalsIgnoreCase(TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE)) {
            this.mRecMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_normal);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        deinitializeView();
        TLCommonUtil.getInstance().setJpegDummyQuality();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public ArrayList<String> getMenuItemList() {
        ArrayList<String> mSortedDisplayMenuItems = super.getMenuItemList();
        if (TLCommonUtil.getInstance().isTimelapseLiteApplication()) {
            mSortedDisplayMenuItems.clear();
            mSortedDisplayMenuItems.add(TLShootModeSettingController.BACKUP_ID_STANDARD);
        }
        return mSortedDisplayMenuItems;
    }

    private void deinitializeView() {
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mBackgroundImageView.setBackgroundResource(0);
        this.mRecMode.setBackgroundResource(0);
        this.mAEStatus.setBackgroundResource(0);
        this.mBackgroundImageView.setImageBitmap(null);
        this.mRecMode.setImageBitmap(null);
        this.mAEStatus.setImageBitmap(null);
        releaseImageViewDrawable(this.mBackgroundImageView);
        this.mBackgroundImageView = null;
        releaseImageViewDrawable(this.mRecMode);
        releaseImageViewDrawable(this.mAEStatus);
        this.mFooterGuide = null;
        this.mGuideTextView = null;
        this.mTlCommonUtil = null;
        this.mThemeInterval = null;
        this.mThemeShootingNum = null;
        this.mThemeShootingTime = null;
        this.mThemeMenuItemName = null;
        this.mRecMode = null;
        this.mAEStatus = null;
        this.mLastItemId = null;
        this.mTlCommonUtil = null;
        this.mRecordingMode = null;
        this.mBackUtil = null;
        this.mCurrentView = null;
        this.mViewArea = null;
        releaseImageViewDrawable(this.mBlack);
        this.mBlack = null;
        System.gc();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    private void getLastStoredValues() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (!BACK.equals(parcelable.getItemId())) {
            this.mLastStoredState = TLCommonUtil.getInstance().getCurrentState();
        }
        setPreviousMenuID();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        update(position);
        TLCommonUtil.getInstance().setJpegDummyQuality();
        super.onItemSelected(adapterView, view, position, id);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    public void cancelValue() {
        update(this.mLastStoredState);
        super.cancelValue();
    }
}
