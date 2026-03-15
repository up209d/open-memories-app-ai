package com.sony.imaging.app.soundphoto.playback.upload.multiple;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.editor.SelectorSingle;

/* loaded from: classes.dex */
public class UploadMultipleSingle extends SelectorSingle {
    @Override // com.sony.imaging.app.base.playback.layout.ISelectorTriggerFunction
    public boolean onReturn() {
        getRootContainer().changeApp(PlayRootContainer.ID_BROWSER);
        return true;
    }
}
