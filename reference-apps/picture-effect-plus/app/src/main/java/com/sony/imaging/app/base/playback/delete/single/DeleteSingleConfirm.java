package com.sony.imaging.app.base.playback.delete.single;

import com.sony.imaging.app.base.playback.base.editor.Confirm;

/* loaded from: classes.dex */
public class DeleteSingleConfirm extends Confirm {
    @Override // com.sony.imaging.app.base.playback.base.editor.Confirm, com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase.OnConfirmListener
    public void onCancel() {
        getContainer().removeState();
    }
}
