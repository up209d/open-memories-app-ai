package com.sony.imaging.app.liveviewgrading.shooting;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingMovieModeMenuLayout;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class ColorGradingEEState extends EEState {
    private static final String DATA_TAG_FIRSTBOOT = "FirstBoot";
    private static final String DATA_TAG_ITEMID = "ItemId";
    private static final String MOVIE_REC_STANDBY_STATE = "MovieRecStandby";
    private static final String NEXT_STATE_EXPOSUREMODECHECK = "ExposureModeCheck";
    private static final String NEXT_STATE_MENU = "Menu";
    private static final String TAG = AppLog.getClassName();
    public static boolean mMenuStateAdd = true;
    private int keycode;
    private Bundle mBundle = null;
    private boolean mIsFirstBoot;
    private String mItemId;

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.data == null) {
            this.mIsFirstBoot = true;
            this.mItemId = ColorGradingController.COLORGRADING;
        } else {
            AppLog.enter(TAG, "Bundle is not null");
            Boolean b = Boolean.valueOf(this.data.getBoolean(DATA_TAG_FIRSTBOOT));
            this.mIsFirstBoot = b != null && b.booleanValue();
            this.mItemId = this.data.getString("ItemId");
            this.keycode = this.data.getInt("keyCodeModeDial");
        }
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId)) {
            Log.e("", "EEState Case 1");
            AppLog.info(TAG, "Mode Dial  screen opened: " + this.mItemId);
            this.mBundle = new Bundle();
            this.mBundle.putString("ItemId", this.mItemId);
            this.mItemId = null;
            AppLog.exit(TAG, AppLog.getMethodName());
            return "Menu";
        }
        if (mMenuStateAdd) {
            Log.e("", "EEState Case 2");
            Log.e("", "mItemId = " + this.mItemId);
            setIsMenuStateAdd(false);
            if (this.mItemId != null || ColorGradingMovieModeMenuLayout.sPushedCenterKey) {
                ColorGradingMovieModeMenuLayout.sPushedCenterKey = false;
                AppLog.info(TAG, "Menu  screen opened ");
                this.mBundle = new Bundle();
                this.mItemId = ColorGradingController.COLORGRADING;
                BaseMenuService service = new BaseMenuService(getActivity());
                String layoutID = service.getMenuItemCustomStartLayoutID(this.mItemId);
                this.mBundle.putString("ItemId", this.mItemId);
                this.mBundle.putString(MenuState.LAYOUT_ID, layoutID);
                this.mItemId = null;
                return "Menu";
            }
            AppLog.info(TAG, "EE screen opened ");
            return MOVIE_REC_STANDBY_STATE;
        }
        Log.e("", "EEState Case 3");
        Log.e("", "mItemId = " + this.mItemId);
        if (this.mItemId != null || this.keycode == 572) {
            AppLog.exit(TAG, AppLog.getMethodName());
            String nextstate = super.getNextChildState();
            return nextstate;
        }
        ColorGradingController.getInstance().setShootingScreenOpened(true);
        AppLog.info(TAG, "EE screen opened ");
        return MOVIE_REC_STANDBY_STATE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public Bundle setStateBundle() {
        if (this.mBundle == null) {
            return super.setStateBundle();
        }
        AppLog.enter(TAG, AppLog.getMethodName() + ":" + this.mBundle);
        AppLog.exit(TAG, AppLog.getMethodName() + ":" + this.mBundle);
        return this.mBundle;
    }

    public static void setIsMenuStateAdd(boolean isAdd) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mMenuStateAdd = isAdd;
        AppLog.exit(TAG, AppLog.getMethodName());
        Log.i(TAG, "setIsMenuStateAdd:" + mMenuStateAdd);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.NORMAL;
        if (DriveModeController.getInstance().isRemoteControl()) {
            ApoWrapper.APO_TYPE type2 = ApoWrapper.APO_TYPE.NONE;
            return type2;
        }
        return type;
    }
}
