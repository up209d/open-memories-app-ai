package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.manuallenscompensation.OpticalCompensation;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.menu.controller.OCExposureModeController;
import com.sony.imaging.app.manuallenscompensation.widget.OCAppNameFocalValue;

/* loaded from: classes.dex */
public class OCExposureModeSubMenuLayout extends ExposureModeMenuLayout {
    private static final String TAG = "ID_EXPOSUREMODEMENULAYOUT";
    private boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.isCanceled = false;
        if (!this.data.containsKey("ItemId")) {
            this.data.putString("ItemId", ExposureModeController.EXPOSURE_MODE);
        }
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        return view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        displayCaution();
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout
    protected int getCautionId() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        return OCInfo.CAUTION_ID_DLAPP_PASMSCN_OK;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        return "ID_EXPOSUREMODEMENULAYOUT";
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        this.isCanceled = false;
        super.onPause();
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        CautionUtilityClass.getInstance().executeTerminate();
        super.closeLayout();
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        this.isCanceled = true;
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        return super.pushedSK1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        AppNameView.setText((String) getResources().getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION));
        resetExifForMovie();
        OCAppNameFocalValue.setText(null);
        OCAppNameFocalValue.show(true);
        this.isCanceled = true;
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        return super.pushedMenuKey();
    }

    private void resetExifForMovie() {
        if (ModeDialDetector.hasModeDial() && ModeDialDetector.getModeDialPosition() == 544) {
            OCUtil.getInstance().resetExifDataOff();
        }
    }

    protected void displayCaution() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.manuallenscompensation.menu.layout.OCExposureModeSubMenuLayout.1
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
                        OCExposureModeSubMenuLayout.this.updateView();
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(OCInfo.CAUTION_ID_DLAPP_PASMSCN_OK, mKey);
        CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_PASMSCN_OK);
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        if (isAppTopBooted()) {
            openAppTopScreen();
        } else {
            super.postSetValue();
        }
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        if (bundle == null) {
            resetExifForMovie();
        }
        super.closeMenuLayout(bundle);
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
    }

    private boolean isAppTopBooted() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        boolean isAppTopBooted = false;
        if (OpticalCompensation.sIsFirstTimeLaunched && ModeDialDetector.hasModeDial() && this.data.containsKey("ItemId") && OCExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
            isAppTopBooted = true;
        }
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        return isAppTopBooted;
    }

    private void openAppTopScreen() {
        AppLog.enter("ID_EXPOSUREMODEMENULAYOUT", AppLog.getMethodName());
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(OCConstants.MENU_KIND);
        openNextMenu(OCConstants.MENU_KIND, layoutID, false, this.data);
        AppLog.exit("ID_EXPOSUREMODEMENULAYOUT", "postSetValue  ExposureMode check fail");
    }
}
