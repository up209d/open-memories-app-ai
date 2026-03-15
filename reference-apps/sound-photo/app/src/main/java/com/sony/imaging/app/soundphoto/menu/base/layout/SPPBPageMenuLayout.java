package com.sony.imaging.app.soundphoto.menu.base.layout;

import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.soundphoto.common.caution.SPInfo;
import com.sony.imaging.app.soundphoto.menu.layout.controller.DeleteOptionController;
import com.sony.imaging.app.soundphoto.playback.state.MenuTransitionTrigger;
import com.sony.imaging.app.soundphoto.playback.upload.util.DirectUploadUtilManager;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPBackUpKey;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SPPBPageMenuLayout extends PageMenuLayout {
    private static final int DELETE_OPTION = 1;
    private static final String INH_ID_PB_MODIFY = "INH_FEATURE_COMMON_PB_MODIFY";
    private static final String TAG = "SPPBPageMenuLayout";
    private static final String[] TAGS_OBSERVE_DISPLAY = {DisplayModeObserver.TAG_DEVICE_CHANGE};
    private static final int TRANSIT_4K = 3;
    private static final int TRANSIT_TO_VOLUME = 4;
    private static final int UPLOAD_MULTIPLE = 2;
    private int mBackupShootingListPos;
    private int mBackupShootingPagePos;
    protected NotificationListener mDisplayChangedListener;
    ArrayList<String> mSupportedItemlist = null;
    private MenuTransitionTrigger mMenuActiontrigger = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return PageMenuLayout.MENU_ID;
    }

    public void registerMenuTransitionTrigger(MenuTransitionTrigger trigger) {
        this.mMenuActiontrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isMediaErr();
        AppNameView.show(false);
        this.mBackupShootingPagePos = BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_PAGE, 0);
        this.mBackupShootingListPos = BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_LIST, 0);
        int pagePosPlay = BackUpUtil.getInstance().getPreferenceInt(SPBackUpKey.SP_ID_MENU_POSITION_GLOBAL_MENU_PAGE, 0);
        int listPosPlay = BackUpUtil.getInstance().getPreferenceInt(SPBackUpKey.SP_ID_MENU_POSITION_GLOBAL_MENU_LIST, 0);
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_PAGE, Integer.valueOf(pagePosPlay));
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_LIST, Integer.valueOf(listPosPlay));
        super.onResume();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        updatePageViewItems(menuItemIds);
        this.mPageListView.setSelection(pagePosPlay);
        this.mItemListView.setSelection(listPosPlay);
        if (menuItemIds != null && menuItemIds.size() > 5) {
            menuItemIds.subList(0, 5).clear();
        }
        ArrayList<String> supportedItemIds = new ArrayList<>();
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String pageId = i$.next();
            this.mSupportedItemlist = this.mService.getSupportedItemList(pageId);
            if (this.mSupportedItemlist != null && this.mSupportedItemlist.size() != 0) {
                supportedItemIds.add(pageId);
            }
        }
        updatePageViewItems(supportedItemIds);
        updateListViewItems(this.mSupportedItemlist);
        this.mPageListView.invalidateViews();
        this.mPageListView.setSelection(pagePosPlay);
        this.mItemListView.setSelection(listPosPlay);
        registerDisplayChangedListener();
        if (SPUtil.getInstance().isMenuBootrequired()) {
            SPUtil.getInstance().setMenuBootrequired(false);
            if (listPosPlay == 1) {
                doItemClickProcessing(DeleteOptionController.DETELE_SELECTOR);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        AppNameView.show(false);
        int pagePosPlay = BackUpUtil.getInstance().getPreferenceInt(SPBackUpKey.SP_ID_MENU_POSITION_GLOBAL_MENU_PAGE, 0);
        int listPosPlay = BackUpUtil.getInstance().getPreferenceInt(SPBackUpKey.SP_ID_MENU_POSITION_GLOBAL_MENU_LIST, 0);
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_PAGE, Integer.valueOf(pagePosPlay));
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_LIST, Integer.valueOf(listPosPlay));
        super.onReopened();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        updatePageViewItems(menuItemIds);
        this.mPageListView.setSelection(pagePosPlay);
        this.mItemListView.setSelection(listPosPlay);
        if (menuItemIds != null && menuItemIds.size() > 5) {
            menuItemIds.subList(0, 5).clear();
        }
        ArrayList<String> supportedItemIds = new ArrayList<>();
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String pageId = i$.next();
            this.mSupportedItemlist = this.mService.getSupportedItemList(pageId);
            if (this.mSupportedItemlist != null && this.mSupportedItemlist.size() != 0) {
                supportedItemIds.add(pageId);
            }
        }
        updatePageViewItems(supportedItemIds);
        updateListViewItems(this.mSupportedItemlist);
        this.mPageListView.invalidateViews();
        this.mPageListView.setSelection(pagePosPlay);
        this.mItemListView.setSelection(listPosPlay);
        registerDisplayChangedListener();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int returnStatus;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (func.equals(CustomizableFunction.Guide) || func.equals(CustomizableFunction.Unchanged) || func.equals(CustomizableFunction.MainNext) || func.equals(CustomizableFunction.MainPrev) || func.equals(CustomizableFunction.SubNext) || func.equals(CustomizableFunction.SubPrev)) {
            AppLog.trace("Custom Key", "func.equals(CustomizableFunction.Guide)");
            returnStatus = super.onConvertedKeyDown(event, func);
        } else {
            AppLog.trace("Custom Key", "!!func.equals(CustomizableFunction.Guide)");
            returnStatus = 1;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnStatus;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        closeMenuLayout(null);
        if (this.mMenuActiontrigger != null) {
            this.mMenuActiontrigger.transitionCancel();
            return 1;
        }
        return 1;
    }

    protected void registerDisplayChangedListener() {
        if (((BaseApp) getActivity()).is4kPlaybackSupported()) {
            this.mDisplayChangedListener = new DisplayChangedListener();
            DisplayModeObserver.getInstance().setNotificationListener(this.mDisplayChangedListener);
        }
    }

    protected void unregisterDisplayChangedListener() {
        if (this.mDisplayChangedListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayChangedListener);
            this.mDisplayChangedListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        updateBackupValues();
        switch (this.mItemListView.getSelectedItemPosition()) {
            case 2:
                if (this.mMenuActiontrigger != null) {
                    this.mMenuActiontrigger.transitionUploadMultiple();
                    return;
                }
                return;
            case 3:
                if (this.mMenuActiontrigger != null) {
                    this.mMenuActiontrigger.transition4KDisplay();
                    return;
                }
                return;
            case 4:
                if (this.mMenuActiontrigger != null) {
                    this.mMenuActiontrigger.transitionToVolumeSetting();
                    return;
                }
                return;
            default:
                super.doItemClickProcessing(itemid);
                return;
        }
    }

    protected boolean isMediaErr() {
        if (!MediaNotificationManager.getInstance().isMounted()) {
            return false;
        }
        String[] media = AvindexStore.getExternalMediaIds();
        AvailableInfo.update();
        return AvailableInfo.isInhibition(INH_ID_PB_MODIFY, media[0]);
    }

    private void showExternalMediaCardCautions() {
        if (isMediaErr()) {
            CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_PROTECT_MS_SLOT1);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        switch (this.mItemListView.getSelectedItemPosition()) {
            case 1:
                showExternalMediaCardCautions();
                return;
            case 2:
                Boolean isSupportedUpload = DirectUploadUtilManager.getInstance().isSupported();
                Boolean isInstalledUpload = DirectUploadUtilManager.getInstance().isInstalled();
                if (!isInstalledUpload.booleanValue()) {
                    CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_DLAPP_ERROR_DIRECTUPLOAD);
                    return;
                } else {
                    if (!isSupportedUpload.booleanValue()) {
                        CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_DLAPP_UPDATE_DIRECTUPLOAD);
                        return;
                    }
                    return;
                }
            case 3:
                CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
                return;
            default:
                super.requestCautionTrigger(itemId);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout
    public void updateListViewItems(ArrayList<String> itemIds) {
        if (this.mSupportedItemlist != null) {
            itemIds = this.mSupportedItemlist;
        }
        super.updateListViewItems(itemIds);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout
    public ArrayList<String> getDisplayedItemList() {
        ArrayList<String> itemIds = super.getDisplayedItemList();
        if (this.mSupportedItemlist != null) {
            ArrayList<String> itemIds2 = this.mSupportedItemlist;
            return itemIds2;
        }
        return itemIds;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CautionUtilityClass.getInstance().executeTerminate();
        unregisterDisplayChangedListener();
        if (this.mSupportedItemlist != null) {
            this.mSupportedItemlist.clear();
            this.mSupportedItemlist = null;
        }
        super.onPause();
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_PAGE, Integer.valueOf(this.mBackupShootingPagePos));
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_LIST, Integer.valueOf(this.mBackupShootingListPos));
        updateBackupValues();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class DisplayChangedListener implements NotificationListener {
        protected DisplayChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return SPPBPageMenuLayout.TAGS_OBSERVE_DISPLAY;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            SPPBPageMenuLayout.this.updateLayout(1);
        }
    }

    private void updateBackupValues() {
        int pagePos = this.mPageListView.getSelectedItemPosition();
        int listPos = this.mItemListView.getSelectedItemPosition();
        BackUpUtil.getInstance().setPreference(SPBackUpKey.SP_ID_MENU_POSITION_GLOBAL_MENU_PAGE, Integer.valueOf(pagePos));
        BackUpUtil.getInstance().setPreference(SPBackUpKey.SP_ID_MENU_POSITION_GLOBAL_MENU_LIST, Integer.valueOf(listPos));
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (this.mMenuActiontrigger != null) {
            this.mMenuActiontrigger.transitionCancel();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        boolean handle = false;
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                handle = true;
                break;
        }
        if (handle) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedPbZoomFuncPlus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAFMFKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfAelKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }
}
