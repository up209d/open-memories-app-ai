package com.sony.imaging.app.each.playback.delete.multiple;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.editor.SelectorSingle;

/* loaded from: classes.dex */
public class DeleteMultipleSingle extends SelectorSingle {
    @Override // com.sony.imaging.app.base.playback.layout.ISelectorTriggerFunction
    public boolean onReturn() {
        getRootContainer().changeApp(PlayRootContainer.ID_BROWSER);
        return true;
    }
}
