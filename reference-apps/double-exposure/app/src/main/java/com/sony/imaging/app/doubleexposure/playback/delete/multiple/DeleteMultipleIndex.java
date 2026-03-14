package com.sony.imaging.app.doubleexposure.playback.delete.multiple;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.editor.SelectorIndex;
import com.sony.imaging.app.doubleexposure.common.AppLog;

/* loaded from: classes.dex */
public class DeleteMultipleIndex extends SelectorIndex {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.playback.layout.ISelectorTriggerFunction
    public boolean onReturn() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        getRootContainer().changeApp(PlayRootContainer.ID_BROWSER);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return true;
    }
}
