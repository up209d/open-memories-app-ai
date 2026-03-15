package com.sony.imaging.app.timelapse.playback.delete.multiple;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.editor.Executor;

/* loaded from: classes.dex */
public class DeleteMultipleExecutor extends Executor {
    @Override // com.sony.imaging.app.base.playback.contents.EditService.ExecutionListener
    public void onEnd(int status) {
        PlayRootContainer root = getRootContainer();
        root.changeApp(PlayRootContainer.ID_BROWSER);
    }
}
