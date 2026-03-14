package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class FocusModeSpecialScreenMenuLayout extends SpecialScreenMenuLayout {
    public static final String MENU_ID = "ID_FOCUSMODESPECIALSCREENMENULAYOUT";
    private String mFocusModeForCancel;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mFocusModeForCancel = FocusModeController.getInstance().getValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        String execType = this.mService.getMenuItemExecType(itemid);
        String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
        if (this.mService.isMenuItemValid(itemid) && MenuTable.NEXT_STATE.equals(execType)) {
            if (isMovieMode()) {
                this.mService.execCurrentMenuItem(itemid, false);
                postSetValue();
                return;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(FocusModeController.CANCEL_FOCUS_MODE, this.mFocusModeForCancel);
                openNextState(nextFragmentID, bundle);
                return;
            }
        }
        super.doItemClickProcessing(itemid);
    }

    protected boolean isMovieMode() {
        return Environment.isMovieAPISupported() && 2 == ExecutorCreator.getInstance().getRecordingMode();
    }
}
