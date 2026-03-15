package com.sony.imaging.app.timelapse.angleshift.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFaderController;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftThemeSelectionController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.PTag;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class AngleShiftThemeSelectionLayout extends SpecialScreenMenuLayout {
    private static final String BACK = "back";
    private static final String TAG = "TLThemeSelectionLayout";
    private int mCurrentSelectedState;
    private int mLastStoredState;
    ViewGroup mCurrentView = null;
    private ImageView mBackgroundImageView = null;
    private ImageView mFader = null;
    private ImageView mSize = null;
    private ImageView mRecMode = null;
    private TextView mGuideTextView = null;
    private TextView mThemeShootingNum = null;
    private TextView mPlayBackTextTime = null;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.gc();
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.as_layout_menu_theme_selection);
        initializeView();
        return this.mCurrentView;
    }

    private void initializeView() {
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mItemNameView = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        if (this.mItemNameView != null) {
            this.mItemNameView.setGravity(17);
        }
        this.mBackgroundImageView = (ImageView) this.mCurrentView.findViewById(R.id.sceneselection_icon);
        this.mGuideTextView = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        this.mThemeShootingNum = (TextView) this.mCurrentView.findViewById(R.id.theme_selection_shooting_num);
        this.mPlayBackTextTime = (TextView) this.mCurrentView.findViewById(R.id.playback_value);
        this.mFader = (ImageView) this.mCurrentView.findViewById(R.id.theme_selection_fader);
        if (!AngleShiftFaderController.getInstance().isAvailable(null)) {
            this.mFader.setVisibility(4);
        }
        this.mSize = (ImageView) this.mCurrentView.findViewById(R.id.theme_selection_moviesize);
        this.mRecMode = (ImageView) this.mCurrentView.findViewById(R.id.theme_selection_recording_mode);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (!TLCommonUtil.getInstance().isAngleShiftBootDone()) {
            TLCommonUtil.getInstance().setAngleShiftBoot(true);
            CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_BOOT_DONE);
        }
        super.onResume();
        getLastStoredValues();
        update(BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0));
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        BackUpUtil.getInstance().setPreference(AngleShiftConstants.AS_CURRENT_THEME, Integer.valueOf(this.mCurrentSelectedState));
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        if (getLayout("ID_ANGLESHIFTCONFIRMEXITLAYOUT").getView() != null) {
            getLayout("ID_ANGLESHIFTCONFIRMEXITLAYOUT").closeLayout();
        }
        if (getLayout("ID_ANGLESHIFTBOOTLOGOLAYOUT").getView() != null) {
            getLayout("ID_ANGLESHIFTBOOTLOGOLAYOUT").closeLayout();
        }
        this.mSpecialScreenView = null;
        this.mService = null;
        this.mAdapter = null;
        this.mScreenTitleView = null;
        if (this.mBackgroundImageView != null) {
            this.mBackgroundImageView.setImageResource(0);
        }
        this.mBackgroundImageView = null;
        this.mGuideTextView = null;
        super.onDestroyView();
        clear();
        System.gc();
    }

    protected void update(int position) {
        AppLog.enter(TAG, "started time for update theme selection view : " + position);
        this.mScreenTitleView.setText("");
        this.mCurrentSelectedState = position;
        BackUpUtil.getInstance().setPreference(AngleShiftConstants.AS_CURRENT_THEME, Integer.valueOf(position));
        String itemid = this.mAdapter.getItem(position);
        if (this.mItemNameView != null) {
            this.mItemNameView.setText(this.mService.getMenuItemText(itemid));
            this.mItemNameView.setVisibility(0);
        }
        if (this.mGuideTextView != null) {
            this.mGuideTextView.setText(this.mService.getMenuItemGuideText(itemid));
            this.mGuideTextView.setVisibility(0);
        }
        updateThemeProperty(AngleShiftThemeSelectionController.getInstance().mThemeList.indexOf(itemid));
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
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (this.mService != null) {
            if (!this.mService.isMenuItemValid(itemid)) {
                requestCautionTrigger(itemid);
                return;
            }
            String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
            this.mService.execCurrentMenuItem(itemid);
            if (nextFragmentID != null) {
                PTag.start("Menu open next MenuLayout");
                CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_OPTION_LAYOUT);
                openNextMenu(itemid, nextFragmentID);
                return;
            }
            AppLog.error(TAG, "Can't open the next MenuLayout");
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_EXIT);
        return 1;
    }

    private void setPreviousMenuID() {
        this.mService.pushMenuHistory(this.mService.popMenuHistory());
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

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (getLayout("ID_ANGLESHIFTBOOTLOGOLAYOUT").getView() != null) {
            return 1;
        }
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT);
        return super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            openThemeOptionMenu();
            return 1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }

    private void updateThemeProperty(int selectedThemePos) {
        updateThemeFromBackup(selectedThemePos);
        this.mThemeShootingNum.setText("" + AngleShiftSetting.getInstance().getTargetNumber());
        this.mPlayBackTextTime.setText(TLCommonUtil.getInstance().getTimeString(AngleShiftSetting.getInstance().getPlackBackDuration(), this));
        AngleShiftSetting.getInstance().updateFaderIcon(this.mFader);
        AngleShiftSetting.getInstance().updateMovieSizeIcon(this.mSize);
        AngleShiftSetting.getInstance().updateRecModeIcon(this.mRecMode);
    }

    private void updateThemeFromBackup(int mCurrentState) {
        int resID;
        switch (mCurrentState) {
            case 0:
                resID = R.drawable.p_16_dd_parts_tm_image_as_pan;
                break;
            case 1:
                resID = R.drawable.p_16_dd_parts_tm_image_as_tilt;
                break;
            case 2:
                resID = R.drawable.p_16_dd_parts_tm_image_as_zoom;
                break;
            case 3:
                resID = R.drawable.p_16_dd_parts_tm_image_as_no_effect;
                break;
            case 4:
                resID = R.drawable.p_16_dd_parts_tm_image_as_custom;
                break;
            default:
                resID = R.drawable.p_16_dd_parts_tm_image_as_pan;
                break;
        }
        this.mBackgroundImageView.setBackgroundResource(0);
        this.mBackgroundImageView.setImageResource(0);
        this.mBackgroundImageView.setImageDrawable(null);
        releaseImageViewDrawable(this.mBackgroundImageView);
        System.gc();
        this.mBackgroundImageView.setBackgroundResource(resID);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        deinitializeView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public ArrayList<String> getMenuItemList() {
        ArrayList<String> mSortedDisplayMenuItems = super.getMenuItemList();
        return mSortedDisplayMenuItems;
    }

    private void deinitializeView() {
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mBackgroundImageView.setBackgroundResource(0);
        this.mFader.setBackgroundResource(0);
        this.mSize.setBackgroundResource(0);
        this.mRecMode.setBackgroundResource(0);
        this.mBackgroundImageView.setImageBitmap(null);
        this.mFader.setImageBitmap(null);
        this.mSize.setImageBitmap(null);
        this.mRecMode.setImageBitmap(null);
        releaseImageViewDrawable(this.mBackgroundImageView);
        this.mBackgroundImageView = null;
        releaseImageViewDrawable(this.mFader);
        releaseImageViewDrawable(this.mSize);
        releaseImageViewDrawable(this.mRecMode);
        this.mPlayBackTextTime = null;
        this.mGuideTextView = null;
        this.mThemeShootingNum = null;
        this.mFader = null;
        this.mSize = null;
        this.mRecMode = null;
        this.mCurrentView = null;
        this.mViewArea = null;
        System.gc();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    private void getLastStoredValues() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (!BACK.equals(parcelable.getItemId())) {
            this.mLastStoredState = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        }
        setPreviousMenuID();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        update(position);
        super.onItemSelected(adapterView, view, position, id);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            int result = super.onConvertedKeyDown(event, func);
            return result;
        }
        switch (event.getScanCode()) {
            case 103:
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                int result2 = super.onConvertedKeyDown(event, func);
                return result2;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            default:
                return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    public void cancelValue() {
        update(this.mLastStoredState);
        super.cancelValue();
    }
}
