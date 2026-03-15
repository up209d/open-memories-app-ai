package com.sony.imaging.app.soundphoto.playback.delete.sound;

import com.sony.imaging.app.base.playback.base.editor.Confirm;

/* loaded from: classes.dex */
public class DeleteSoundConfirm extends Confirm {
    @Override // com.sony.imaging.app.base.playback.base.editor.Confirm, com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase.OnConfirmListener
    public void onCancel() {
        removeState();
    }
}
