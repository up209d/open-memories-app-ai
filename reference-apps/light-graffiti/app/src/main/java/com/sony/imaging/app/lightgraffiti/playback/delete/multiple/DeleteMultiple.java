package com.sony.imaging.app.lightgraffiti.playback.delete.multiple;

import com.sony.imaging.app.base.playback.base.editor.Editor;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;

/* loaded from: classes.dex */
public class DeleteMultiple extends Editor {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.editor.Editor
    public EditService getEditService() {
        return DeleteService.getInstance();
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 12;
    }
}
