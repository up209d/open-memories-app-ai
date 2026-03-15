package com.sony.imaging.app.srctrl.menu.layout;

import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.srctrl.caution.InfoEx;
import com.sony.imaging.app.srctrl.shooting.ExposureModeControllerEx;

/* loaded from: classes.dex */
public class ExposureModeMenuLayoutEx extends ExposureModeMenuLayout {
    public static final String MENU_ID = "ID_EXPOSUREMODEMENULAYOUT";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout
    public int getCautionId() {
        return ExposureModeControllerEx.isExposureModeMovieSelected() ? InfoEx.CAUTION_ID_SMART_REMOTE_INVALID_MOVIE_OK : super.getCautionId();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return "ID_EXPOSUREMODEMENULAYOUT";
    }
}
