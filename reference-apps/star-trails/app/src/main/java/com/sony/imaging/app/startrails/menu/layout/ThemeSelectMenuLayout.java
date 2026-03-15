package com.sony.imaging.app.startrails.menu.layout;

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
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.startrails.AppContext;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeChangeListener;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class ThemeSelectMenuLayout extends SpecialScreenMenuLayout {
    public static final String APPTOP_MENU_ID = "ID_THEMESELECTMENULAYOUT";
    private static final String BACK = "back";
    private static final String TAG = "ThemeSelectMenuLayout";
    private ImageView mBlack;
    private int mCurrentSelectedState;
    private int mLastStoredState;
    private int mShootingNumValue;
    private ShutterSpeedChangeListener mShutterSpeedListener;
    private int mStreakLevelValue;
    ViewGroup mCurrentView = null;
    private ImageView mBackgroundImageView = null;
    private ImageView mRecModeImgView = null;
    private TextView mGuideTextView = null;
    private TextView mStreakLevelTxtView = null;
    private TextView mShotNumTxtView = null;
    private TextView mItemNameTxtView = null;
    private TextView mShootingTimeValue = null;
    private HistoryItem mLastItemId = null;
    private FooterGuide mFooterGuide = null;
    private int mRecordingModeValue = 0;
    private boolean isFromOptionTheme = false;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.st_layout_menu_theme_selection);
        initializeView();
        if (this.mBlack == null) {
            this.mBlack = new ImageView(AppContext.getAppContext());
            this.mBlack.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.mBlack.setImageResource(android.R.color.black);
            this.mCurrentView.addView(this.mBlack, 0);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    private void initializeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mItemNameView = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_theme_select);
        this.mBackgroundImageView = (ImageView) this.mCurrentView.findViewById(R.id.sceneselection_icon);
        this.mGuideTextView = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        this.mStreakLevelTxtView = (TextView) this.mCurrentView.findViewById(R.id.streaklevel);
        this.mShotNumTxtView = (TextView) this.mCurrentView.findViewById(R.id.theme_selection_shooting_num);
        this.mItemNameTxtView = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        this.mShootingTimeValue = (TextView) this.mCurrentView.findViewById(R.id.shooting_time_value);
        if (this.mItemNameTxtView != null) {
            this.mItemNameTxtView.setGravity(17);
        }
        this.mRecModeImgView = (ImageView) this.mCurrentView.findViewById(R.id.theme_selection_recording_mode);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        AppLog.enter(TAG, AppLog.getMethodName());
        STUtility.getInstance().setUpdateThemeProperty(true);
        getLastStoredValues();
        CameraNotificationManager.getInstance().setNotificationListener(getNotificationListener());
        update(this.mCurrentSelectedState);
        setFooterGuide();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setFooterGuide() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mLastItemId == null) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_FOOTERGUIDE_OPTION_EXITAPP, R.string.STRID_FUNC_TIMELAPSE_FOOTERGUIDE_OPTION_EXITAPP_SK));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_FOOTERGUIDE_OPTION_RETURN, R.string.STRID_FUNC_TIMELAPSE_FOOTERGUIDE_OPTION_RETURN_SK));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        AppLog.enter(TAG, AppLog.getMethodName());
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
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onDestroyView();
    }

    protected void update(int position) {
        AppLog.enter(TAG, "started time for update theme selection view  " + position);
        this.mScreenTitleView.setText("");
        this.mCurrentSelectedState = position;
        String itemid = this.mAdapter.getItem(position);
        if (this.mItemNameView != null) {
            this.mItemNameView.setText(this.mService.getMenuItemText(itemid));
            this.mItemNameView.setVisibility(0);
        }
        STUtility.getInstance().updateExposureMode();
        updateThemeFromBackup(position);
        AppLog.exit(TAG, "started time for update theme selection view");
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void openThemeOptionMenu() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSpecialScreenView != null) {
            String itemId = this.mAdapter.getItem(this.mSpecialScreenView.getSelectedItemPosition());
            doOnKeyProcessing(itemId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected void doOnKeyProcessing(String itemid) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (this.mService != null) {
            if (!this.mService.isMenuItemValid(itemid)) {
                requestCautionTrigger(itemid);
            } else {
                String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
                this.mService.execCurrentMenuItem(itemid);
                if (nextFragmentID != null) {
                    PTag.start("Menu open next MenuLayout");
                    openNextMenu(itemid, nextFragmentID);
                } else {
                    Log.e(TAG, "Can't open the next MenuLayout");
                }
            }
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToLeft(KeyEvent event) {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToRight(KeyEvent event) {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        closeMenuLayout(null);
        return 1;
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
        AppLog.enter(TAG, AppLog.getMethodName());
        int returnState = 1;
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            openThemeOptionMenu();
        } else {
            returnState = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    private void updateThemeProperty(int selectedThemePos) {
        this.mShotNumTxtView.setText("" + this.mShootingNumValue);
        CharSequence streakLevelValue = STUtility.getInstance().getStreakValue(this.mStreakLevelValue);
        this.mStreakLevelTxtView.setText(streakLevelValue);
        STUtility.getInstance().setCurrentTrail(selectedThemePos);
        this.mShootingTimeValue.setText(STUtility.getInstance().calculateShootingTime(this.mShootingNumValue));
        updateBackgroundImage();
        updateRecModeIcon();
    }

    private void updateBackgroundImage() {
        releaseImageViewDrawable(this.mBackgroundImageView);
        if (this.mBackgroundImageView == null) {
            this.mBackgroundImageView = (ImageView) this.mCurrentView.findViewById(R.id.sceneselection_icon);
        }
        switch (this.mCurrentSelectedState) {
            case 0:
                this.mBackgroundImageView.setImageResource(R.drawable.p_16_dd_parts_strl_theme_bright_night_image);
                if (this.mGuideTextView != null) {
                    this.mGuideTextView.setText(R.string.STRID_FUNC_STRLS_THEME_BRIGHT_NIGHT_GUIDE2);
                    this.mGuideTextView.setVisibility(0);
                    return;
                }
                return;
            case 1:
                this.mBackgroundImageView.setImageResource(R.drawable.p_16_dd_parts_strl_theme_dark_night_image);
                if (this.mGuideTextView != null) {
                    this.mGuideTextView.setText(R.string.STRID_FUNC_STRLS_THEME_DARK_NIGHT_GUIDE2);
                    this.mGuideTextView.setVisibility(0);
                    return;
                }
                return;
            default:
                this.mBackgroundImageView.setImageResource(R.drawable.p_16_dd_parts_tm_image_custom);
                if (this.mGuideTextView != null) {
                    this.mGuideTextView.setText(R.string.STRID_FUNC_SMOOTH_PHOTO_CUSTOM_GUIDE);
                    this.mGuideTextView.setVisibility(0);
                    return;
                }
                return;
        }
    }

    private void updateThemeFromBackup(int mCurrentState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (true == this.isFromOptionTheme) {
            this.mRecordingModeValue = ThemeParameterSettingUtility.getInstance().getRecordingMode();
            this.mShootingNumValue = ThemeParameterSettingUtility.getInstance().getNumberOfShot();
            this.mStreakLevelValue = ThemeParameterSettingUtility.getInstance().getStreakLevel();
        } else {
            updatedValue(mCurrentState);
        }
        updateThemeProperty(mCurrentState);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updatedValue(int mCurrentState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (mCurrentState) {
            case 1:
                this.mRecordingModeValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.DNIGHT_RECORDING_MODE_KEY, 0);
                this.mShootingNumValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.DNIGHT_SHOOTING_NUM_KEY, 480);
                this.mStreakLevelValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.DNIGHT_STREAK_LEVEL_KEY, 4);
                break;
            case 2:
                this.mRecordingModeValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.CUSTOM_RECORDING_MODE_KEY, 0);
                this.mShootingNumValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.CUSTOM_SHOOTING_NUM_KEY, 480);
                this.mStreakLevelValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.CUSTOM_STREAK_LEVEL_KEY, 4);
                break;
            default:
                this.mRecordingModeValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.BNIGHT_RECORDING_MODE_KEY, 0);
                this.mShootingNumValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.BNIGHT_SHOOTING_NUM_KEY, 480);
                this.mStreakLevelValue = BackUpUtil.getInstance().getPreferenceInt(STBackUpKey.BNIGHT_STREAK_LEVEL_KEY, 4);
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateRecModeIcon() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mRecordingModeValue == 0) {
            this.mRecModeImgView.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        } else {
            this.mRecModeImgView.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        deinitializeView();
        super.onPause();
    }

    private void deinitializeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mShutterSpeedListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mShutterSpeedListener);
            this.mShutterSpeedListener = null;
        }
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        releaseImageViewDrawable(this.mBackgroundImageView);
        releaseImageViewDrawable(this.mRecModeImgView);
        if (this.mFooterGuide != null) {
            this.mFooterGuide.setData(null);
            this.mFooterGuide = null;
        }
        this.mBackgroundImageView = null;
        this.mGuideTextView = null;
        this.mStreakLevelTxtView = null;
        this.mShotNumTxtView = null;
        this.mShootingTimeValue = null;
        this.mItemNameTxtView = null;
        this.mRecModeImgView = null;
        this.mLastItemId = null;
        this.mRecordingModeValue = 0;
        this.mCurrentView = null;
        releaseImageViewDrawable(this.mBlack);
        this.mBlack = null;
        System.gc();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (imageView != null) {
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(null);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void getLastStoredValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (!BACK.equals(parcelable.getItemId())) {
            this.isFromOptionTheme = false;
            AppLog.info(TAG, "getLastStoredValues");
            this.mLastStoredState = STUtility.getInstance().getCurrentTrail();
            STUtility.getInstance().setLastTrail(this.mLastStoredState);
            this.mCurrentSelectedState = this.mLastStoredState;
        } else if (this.mCurrentSelectedState == this.mLastStoredState) {
            this.isFromOptionTheme = false;
        } else {
            this.isFromOptionTheme = true;
        }
        this.mSpecialScreenView.setSelection(this.mCurrentSelectedState);
        setPreviousMenuID();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        super.onItemSelected(adapterView, view, position, id);
        AppLog.enter(TAG, "onItemSelected");
        this.mCurrentSelectedState = position;
        STUtility.getInstance().setCurrentTrail(this.mCurrentSelectedState);
        updateThemeParameter();
        update(position);
        AppLog.exit(TAG, "onItemSelected");
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    public void cancelValue() {
        this.mCurrentSelectedState = this.mLastStoredState;
        STUtility.getInstance().setCurrentTrail(this.mLastStoredState);
        STUtility.getInstance().setLastTrail(this.mLastStoredState);
        update(this.mLastStoredState);
        updateThemeParameter();
        STUtility.getInstance().setUpdateThemeProperty(false);
        super.cancelValue();
    }

    private void updateThemeParameter() {
        if (this.isFromOptionTheme) {
            STUtility.getInstance().setUpdateThemeProperty(true);
            ThemeChangeListener.getInstance().updateThemeProperty();
            this.isFromOptionTheme = false;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int returnState = 1;
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            AppLog.enter(TAG, "Delete key releases");
        } else {
            returnState = super.onKeyUp(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (STUtility.getInstance().getLastTrail() == STUtility.getInstance().getCurrentTrail() && !STUtility.getInstance().isEEStateBoot()) {
            STUtility.getInstance().setUpdateThemeProperty(false);
        }
        ThemeParameterSettingUtility.getInstance().initializeThemeParameters();
        deinitializeView();
        super.closeLayout();
    }

    protected NotificationListener getNotificationListener() {
        if (this.mShutterSpeedListener == null) {
            this.mShutterSpeedListener = new ShutterSpeedChangeListener();
        }
        return this.mShutterSpeedListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ShutterSpeedChangeListener implements NotificationListener {
        private String[] TAGS = {CameraNotificationManager.SHUTTER_SPEED};

        ShutterSpeedChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (2 == ThemeSelectMenuLayout.this.mCurrentSelectedState && ThemeSelectMenuLayout.this.mShootingTimeValue != null) {
                ThemeSelectMenuLayout.this.mShootingTimeValue.setText(STUtility.getInstance().calculateShootingTime(ThemeSelectMenuLayout.this.mShootingNumValue));
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedIrisDial() {
        return -1;
    }
}
