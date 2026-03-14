package com.sony.imaging.app.doubleexposure.menu.layout;

import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.doubleexposure.DoubleExposureApp;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class DoubleExposurePageMenuLayout extends PageMenuLayout {
    public static final String MENU_ID = "ID_DOUBLEEXPOSUREPAGEMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private boolean mIsTurnedEVDial = false;

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        DoubleExposureApp.sBootFactor = -1;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout
    public void updatePageViewItems(ArrayList<String> itemIds) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (itemIds != null) {
            itemIds.remove(itemIds.size() - 1);
        }
        super.updatePageViewItems(itemIds);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mIsTurnedEVDial) {
            DoubleExposureUtil.getInstance().updateExposureCompensation();
            this.mIsTurnedEVDial = false;
        }
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
        DoubleExposureUtil.getInstance().setSaveSettings(false);
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.detachedLens();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        DoubleExposureUtil.getInstance().setSaveSettings(false);
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.attachedLens();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mIsTurnedEVDial = true;
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.turnedEVDial();
    }
}
