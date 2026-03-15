package com.sony.imaging.app.graduatedfilter.playback.delete.multiple;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.editor.SelectorIndex;

/* loaded from: classes.dex */
public class DeleteMultipleIndex extends SelectorIndex {
    @Override // com.sony.imaging.app.base.playback.layout.ISelectorTriggerFunction
    public boolean onReturn() {
        getRootContainer().changeApp(PlayRootContainer.ID_BROWSER);
        return true;
    }
}
