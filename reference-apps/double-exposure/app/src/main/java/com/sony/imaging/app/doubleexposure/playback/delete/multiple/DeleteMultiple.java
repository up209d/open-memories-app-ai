package com.sony.imaging.app.doubleexposure.playback.delete.multiple;

import com.sony.imaging.app.base.playback.base.editor.Editor;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.doubleexposure.common.AppLog;

/* loaded from: classes.dex */
public class DeleteMultiple extends Editor {
    private final String TAG = AppLog.getClassName();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.editor.Editor
    public EditService getEditService() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return DeleteService.getInstance();
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 12;
    }
}
