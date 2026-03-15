package com.sony.imaging.app.soundphoto.playback.delete.single.executor;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleConfirm;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class SPDeleteSingleConfirm extends DeleteSingleConfirm {
    @Override // com.sony.imaging.app.base.playback.delete.single.DeleteSingleConfirm, com.sony.imaging.app.base.playback.base.editor.Confirm, com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase.OnConfirmListener
    public void onCancel() {
        if (SPUtil.getInstance().isSoundDataDeletePerforming()) {
            handleTransition();
        } else {
            super.onCancel();
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        SPUtil.getInstance().setDeleteScreenVisible(true);
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        SPUtil.getInstance().setDeleteScreenVisible(false);
        super.onPause();
    }

    private void handleTransition() {
        SPUtil.getInstance().setMenuBootrequired(true);
        setNextState(PlayRootContainer.ID_BROWSER, null);
    }
}
