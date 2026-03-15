package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SceneSelectionMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.manuallenscompensation.OpticalCompensation;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.menu.controller.OCExposureModeController;
import com.sony.imaging.app.manuallenscompensation.widget.OCAppNameFocalValue;

/* loaded from: classes.dex */
public class OCSceneSelectionMenuLayout extends SceneSelectionMenuLayout {
    private static final String TAG = "ID_SCENESELECTIONMENULAYOUT";
    private boolean isCanceled = false;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return TAG;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SceneSelectionMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.isCanceled = false;
        if (!this.data.containsKey("ItemId")) {
            this.data.putString("ItemId", ExposureModeController.EXPOSURE_MODE);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return view;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isCanceled = false;
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        CautionUtilityClass.getInstance().executeTerminate();
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

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppNameView.setText((String) getResources().getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION));
        OCAppNameFocalValue.setText(null);
        OCAppNameFocalValue.show(true);
        this.isCanceled = true;
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedMenuKey();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isAppTopBooted()) {
            this.isCanceled = true;
            openRootMenu();
        } else {
            super.postSetValue();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (bundle != null && isAppTopBooted() && !this.isCanceled) {
            openAppTopScreen();
        } else {
            super.closeMenuLayout(bundle);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean isAppTopBooted() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isAppTopBooted = false;
        if (OpticalCompensation.sIsFirstTimeLaunched && ModeDialDetector.hasModeDial() && this.data.containsKey("ItemId") && OCExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
            isAppTopBooted = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isAppTopBooted;
    }

    private void openAppTopScreen() {
        AppLog.enter(TAG, AppLog.getMethodName());
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(OCConstants.MENU_KIND);
        openNextMenu(OCConstants.MENU_KIND, layoutID, false, this.data);
        AppLog.exit(TAG, "postSetValue  ExposureMode check fail");
    }
}
