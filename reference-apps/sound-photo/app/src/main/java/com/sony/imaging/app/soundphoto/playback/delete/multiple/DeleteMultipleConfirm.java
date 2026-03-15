package com.sony.imaging.app.soundphoto.playback.delete.multiple;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.editor.Confirm;

/* loaded from: classes.dex */
public class DeleteMultipleConfirm extends Confirm {
    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        PlayRootContainer root = getRootContainer();
        root.changeApp(PlayRootContainer.ID_BROWSER);
        return 1;
    }
}
