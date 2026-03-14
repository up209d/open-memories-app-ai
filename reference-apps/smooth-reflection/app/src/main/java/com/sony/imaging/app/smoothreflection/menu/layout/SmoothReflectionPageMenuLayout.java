package com.sony.imaging.app.smoothreflection.menu.layout;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.smoothreflection.SmoothReflectionApp;
import com.sony.imaging.app.smoothreflection.caution.SmoothReflectionInfo;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SmoothReflectionPageMenuLayout extends PageMenuLayout {
    public static final String MENU_ID = "ID_SMOOTHREFLECTIONPAGEMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    MyRecordingMediaChangeCallback sMyRecordingMediaChangeCallback = new MyRecordingMediaChangeCallback();

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppNameView.show(false);
        setActualMediaIds();
        super.onResume();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setActualMediaIds() {
        ExecutorCreator creator = ExecutorCreator.getInstance();
        creator.stableSequence();
        ShootingExecutor executor = (ShootingExecutor) creator.getSequence();
        String[] ids = AvindexStore.getExternalMediaIds();
        if (ids != null && ids[0] != null) {
            executor.setRecordingMedia(ids[0], this.sMyRecordingMediaChangeCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyRecordingMediaChangeCallback implements CameraEx.RecordingMediaChangeCallback {
        MyRecordingMediaChangeCallback() {
        }

        public void onRecordingMediaChange(CameraEx cameraEx) {
            ArrayList<String> menuItemIds = SmoothReflectionPageMenuLayout.this.mService.getMenuItemList();
            ArrayList<String> supportedItemIds = new ArrayList<>();
            Iterator i$ = menuItemIds.iterator();
            while (i$.hasNext()) {
                String pageId = i$.next();
                ArrayList<String> list = SmoothReflectionPageMenuLayout.this.mService.getSupportedItemList(pageId);
                if (list != null && list.size() != 0) {
                    supportedItemIds.add(pageId);
                }
            }
            SmoothReflectionPageMenuLayout.this.updatePageViewItems(supportedItemIds);
            SmoothReflectionPageMenuLayout.this.mPageListView.invalidateViews();
            SmoothReflectionApp.sBootFactor = -1;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onReopened();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        ArrayList<String> supportedItemIds = new ArrayList<>();
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String pageId = i$.next();
            ArrayList<String> list = this.mService.getSupportedItemList(pageId);
            if (list != null && list.size() != 0) {
                supportedItemIds.add(pageId);
            }
        }
        updatePageViewItems(supportedItemIds);
        this.mPageListView.invalidateViews();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onDestroy();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.detachedLens();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.attachedLens();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.turnedEVDial();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (SmoothReflectionConstants.SELECTED_MENU_ITEM_THEME.contains(itemId) && !ThemeController.CUSTOM.equals(getSelectedTheme())) {
            displayCaution(SmoothReflectionInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_WHEN_INVALID_THEME);
        } else {
            super.requestCautionTrigger(itemId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void displayCaution(final int cautionId) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.smoothreflection.menu.layout.SmoothReflectionPageMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    default:
                        return -1;
                }
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                return -1;
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getSelectedTheme() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return selectedTheme;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (itemid.equals("ApplicationSettings")) {
            SmoothReflectionUtil.getInstance().setComeFromPageMenu(true);
        }
        super.doItemClickProcessing(itemid);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }
}
