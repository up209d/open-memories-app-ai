package com.sony.imaging.app.manuallenscompensation.shooting.state;

import android.os.Bundle;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.manuallenscompensation.OpticalCompensation;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

/* loaded from: classes.dex */
public class OCEEState extends EEState {
    private static final String KEY_CODE_MODEDIAL = "keyCodeModeDial";
    private static final String MOVIE_REC_STANDBY_STATE = "MovieRecStandby";
    private static final String TAG = "MLCEEState";
    private String mItemId;
    private Bundle mOCBundle = null;

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (this.data != null) {
            this.mItemId = this.data.getString("ItemId");
        }
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        String ret;
        this.mOCBundle = null;
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId) || isModeDialEvent()) {
            ret = super.getNextChildState();
        } else if (OpticalCompensation.sIsFirstTimeLaunched) {
            BaseMenuService service = new BaseMenuService(getActivity());
            String layoutID = service.getMenuItemCustomStartLayoutID(OCConstants.MENU_KIND);
            this.mOCBundle = new Bundle();
            this.mOCBundle.putString("ItemId", OCConstants.MENU_KIND);
            this.mOCBundle.putString(MenuState.LAYOUT_ID, layoutID);
            ret = "Menu";
            OpticalCompensation.sIsFirstTimeLaunched = false;
        } else {
            try {
                Thread.sleep(50L);
                OCUtil.getInstance().setCameraPreviewMode("iris_ss_iso_aeunlock", OCUtil.getInstance().isDiademOVfMode());
            } catch (InterruptedException e) {
                AppLog.error(TAG, e.getMessage());
            }
            ret = super.getNextChildState();
            if (!MOVIE_REC_STANDBY_STATE.equalsIgnoreCase(ret)) {
                OCUtil.getInstance().setMovieRecStarted(false);
            }
            OCUtil.getInstance().setExifData();
        }
        if (this.data != null && this.data.containsKey("ItemId")) {
            this.data.remove("ItemId");
            this.mItemId = null;
        }
        return ret;
    }

    private boolean isModeDialEvent() {
        if (this.data == null || 572 != this.data.getInt(KEY_CODE_MODEDIAL)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public Bundle setStateBundle() {
        return this.mOCBundle == null ? super.setStateBundle() : this.mOCBundle;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mOCBundle = null;
        super.onDestroy();
    }
}
