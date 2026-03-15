package com.sony.imaging.app.lightgraffiti.menu.layout;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.lightgraffiti.caution.LGInfo;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;

/* loaded from: classes.dex */
public class LGExposureModeMenuLayout extends ExposureModeMenuLayout {
    private String TAG = LGExposureModeMenuLayout.class.getSimpleName();

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected boolean isParentItemAvailable() {
        Log.d(this.TAG, AppLog.getMethodName());
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void checkCaution() {
        Log.d(this.TAG, AppLog.getMethodName());
        if (this.mService == null) {
            Log.e(this.TAG, AppLog.getMethodName() + " : mService is null.");
            return;
        }
        String itemid = this.mService.getMenuItemId();
        if (itemid == null) {
            Log.e(this.TAG, AppLog.getMethodName() + " : itemid is null.");
        } else {
            requestCautionTrigger(itemid);
            closeMenuLayout(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        Log.d(this.TAG, AppLog.getMethodName());
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (LGStateHolder.SHOOTING_1ST.equals(stage)) {
            CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_FUNC_INV_APP);
        } else {
            CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_FUNC_INV_APP_APO_NONE);
        }
    }
}
