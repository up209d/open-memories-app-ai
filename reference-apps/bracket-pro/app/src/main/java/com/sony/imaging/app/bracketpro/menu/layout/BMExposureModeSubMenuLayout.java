package com.sony.imaging.app.bracketpro.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.BracketMasterMain;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMExposureModeController;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.shooting.state.BMEEState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class BMExposureModeSubMenuLayout extends ExposureModeMenuLayout {
    private static final String TAG = AppLog.getClassName();
    MediaeNotificationListener _medianotification;
    public boolean mbMounted;

    /* loaded from: classes.dex */
    class MediaeNotificationListener implements NotificationListener {
        private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};

        MediaeNotificationListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag != null && tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE) && BMExposureModeSubMenuLayout.this._medianotification != null && MediaNotificationManager.getInstance().isMounted() != BMExposureModeSubMenuLayout.this.mbMounted) {
                BMExposureModeSubMenuLayout.this.openShootingLayout();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openShootingLayout() {
        super.closeMenuLayout(null);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        BMEEState.isBMCautionStateBooted = true;
        super.closeMenuLayout(null);
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        super.closeMenuLayout(null);
        return super.pushedS2Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        BMMenuController.getInstance().setRangeStatus(false);
        String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        String focusMode = FocusModeController.getInstance().getValue();
        if (currentBracket != null && focusMode != null && currentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            FocusModeController.getInstance().setValue("af-s");
        }
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        super.closeMenuLayout(null);
        return super.onLensAttached(event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        super.closeMenuLayout(null);
        return super.onLensDetached(event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this._medianotification == null) {
            this._medianotification = new MediaeNotificationListener();
            MediaNotificationManager.getInstance().setNotificationListener(this._medianotification);
        }
        this.mbMounted = MediaNotificationManager.getInstance().isMounted();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this._medianotification != null) {
            MediaNotificationManager.getInstance().removeNotificationListener(this._medianotification);
        }
        this._medianotification = null;
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        displayCaution();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        return "ID_EXPOSUREMODESUBMENULAYOUT";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public ArrayList<String> getMenuItemList() {
        ArrayList<String> mSortedDisplayMenuItems = super.getMenuItemList();
        if (!ModeDialDetector.hasModeDial()) {
            if (mSortedDisplayMenuItems.contains(ExposureModeController.INTELLIGENT_AUTO_MODE)) {
                mSortedDisplayMenuItems.remove(ExposureModeController.INTELLIGENT_AUTO_MODE);
            }
            if (mSortedDisplayMenuItems.contains(ExposureModeController.SUPERIOR_AUTO_MODE)) {
                mSortedDisplayMenuItems.remove(ExposureModeController.SUPERIOR_AUTO_MODE);
            }
            if (mSortedDisplayMenuItems.contains(ExposureModeController.SWEEPSHOOTING_MODE)) {
                mSortedDisplayMenuItems.remove(ExposureModeController.SWEEPSHOOTING_MODE);
            }
            if (mSortedDisplayMenuItems.contains("SceneSelectionMode")) {
                mSortedDisplayMenuItems.remove("SceneSelectionMode");
            }
        }
        return mSortedDisplayMenuItems;
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        CautionUtilityClass.getInstance().executeTerminate();
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (BracketMasterUtil.getCurrentBracketType().equalsIgnoreCase(BMMenuController.FlashBracket)) {
            BMMenuController.getInstance().setRangeStatus(false);
        }
        return super.pushedCenterKey();
    }

    private void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMExposureModeSubMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        BMExposureModeSubMenuLayout.this.updateView();
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        return 0;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(getCautionId(), mKey);
        CautionUtilityClass.getInstance().requestTrigger(getCautionId());
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout
    public int getCautionId() {
        String mCurrentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            return CautionInfo.CAUTION_ID_DLAPP_PASM_OK;
        }
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ApertureBracket)) {
            return CautionInfo.CAUTION_ID_DLAPP_A_OK;
        }
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket)) {
            return CautionInfo.CAUTION_ID_DLAPP_S_OK;
        }
        if (!mCurrentBracket.equalsIgnoreCase(BMMenuController.FlashBracket)) {
            return 0;
        }
        return CautionInfo.CAUTION_ID_DLAPP_P_OK;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        if (isAppTopBooted()) {
            openAppTopScreen(BMMenuController.BRACKETPRO);
        } else {
            super.postSetValue();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        if (isAppTopBooted()) {
            openAppTopScreen(BMMenuController.BRACKETPRO);
        }
        if (BMMenuController.getInstance().isRangeSet() && ModeDialDetector.hasModeDial() && BMExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
            openAppTopScreen(BMMenuController.getInstance().getSelectedBracket());
        } else {
            super.closeMenuLayout(bundle);
        }
    }

    private boolean isAppTopBooted() {
        if (!BracketMasterMain.getIsLauncherBoot() || !ModeDialDetector.hasModeDial() || this.data.getString(ExposureModeController.EXPOSURE_MODE) == null || !BMExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        BMMenuController.getInstance().setRangeStatus(false);
        return super.pushedMovieRecKey();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void openRootMenu() {
        if (isAppTopBooted()) {
            openAppTopScreen(BMMenuController.BRACKETPRO);
        } else {
            super.openRootMenu();
        }
    }

    private void openAppTopScreen(String itemID) {
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(itemID);
        openNextMenu(itemID, layoutID, true, this.data);
        AppLog.exit("ID_EXPOSUREMODESUBMENULAYOUT", "postSetValue  ExposureMode check fail");
    }
}
