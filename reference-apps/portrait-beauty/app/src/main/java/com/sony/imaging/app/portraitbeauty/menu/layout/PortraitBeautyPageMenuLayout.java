package com.sony.imaging.app.portraitbeauty.menu.layout;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.controller.AdjustVisuallyStartupController;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class PortraitBeautyPageMenuLayout extends PageMenuLayout {
    public static final String MENU_ID = "ID_PORTRAITBEAUTYPAGEMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private final String ADJUST_MODE_ITEM_ID = "AdjustMode";

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout
    public void updatePageViewItems(ArrayList<String> itemIds) {
        ArrayList<String> list;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (itemIds != null) {
            itemIds.remove(6);
            itemIds.remove(5);
        }
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        new ArrayList();
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String pageId = i$.next();
            if (pageId.equals("Page5") && ((list = this.mService.getSupportedItemList(pageId)) == null || list.size() == 0)) {
                if (pageId.equals("Page5")) {
                    itemIds.remove(4);
                }
            }
        }
        super.updatePageViewItems(itemIds);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (itemid != null && "AdjustMode".equals(itemid)) {
            PortraitBeautyDisplayModeObserver observer = PortraitBeautyDisplayModeObserver.getInstance();
            observer.setDisplayMode(0, 3);
            AdjustVisuallyStartupController.getInstance().setAdjustVisullySelectedInMenu(true);
        }
        super.doItemClickProcessing(itemid);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.turnedEVDial();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        displayCaution(PortraitBeautyInfo.CAUTION_ID_DLAPP_MODE_DIAL_INVALID);
        return 1;
    }

    private void displayCaution(final int cautionId) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.portraitbeauty.menu.layout.PortraitBeautyPageMenuLayout.1
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        closeLayout();
        return super.pushedS2Key();
    }
}
