package com.sony.imaging.app.pictureeffectplus.shooting.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusExposureModeController;

/* loaded from: classes.dex */
public class PictureEffectExposureModeSubMenuLayout extends ExposureModeMenuLayout {
    public static final String TAG = AppLog.getClassName();
    private boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.isCanceled = false;
        AppLog.exit(TAG, AppLog.getMethodName());
        return view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        displayCaution();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout
    protected int getCautionId() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 131202;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return ExposureModeMenuLayout.MENU_ID;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isCanceled = false;
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        updateExposureMode();
        super.closeMenuLayout(bundle);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        CautionUtilityClass.getInstance().executeTerminate();
        updateExposureMode();
        super.closeLayout();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isCanceled = true;
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedSK1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isCanceled = true;
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedMenuKey();
    }

    private void updateExposureMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!ModeDialDetector.hasModeDial() && !this.isCanceled) {
            String expoMode = PictureEffectPlusExposureModeController.getInstance().getValue(null);
            AppLog.enter("Applog", expoMode);
        }
        AppLog.exit("Applog", "out");
    }

    protected void displayCaution() {
        AppLog.enter(TAG, AppLog.getMethodName());
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.pictureeffectplus.shooting.layout.PictureEffectExposureModeSubMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int result = 1;
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        result = 0;
                        break;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        break;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        result = 0;
                        break;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        PictureEffectExposureModeSubMenuLayout.this.updateView();
                        CautionUtilityClass.getInstance().executeTerminate();
                        break;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        if (EVDialDetector.hasEVDial()) {
                            ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                            break;
                        }
                        break;
                }
                AppLog.exit(PictureEffectExposureModeSubMenuLayout.TAG, AppLog.getMethodName());
                return result;
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(131202, mKey);
        CautionUtilityClass.getInstance().requestTrigger(131202);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(PictureEffectPlusController.PICTUREEFFECTPLUS);
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, PictureEffectPlusController.PICTUREEFFECTPLUS);
        bundle.putString(MenuState.LAYOUT_ID, layoutID);
        bundle.putBoolean(MenuState.BOOLEAN_FINISH_MENU_STATE, true);
        closeMenuLayout(bundle);
    }
}
