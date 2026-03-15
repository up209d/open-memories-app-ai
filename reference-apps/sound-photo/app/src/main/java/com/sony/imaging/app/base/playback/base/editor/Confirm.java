package com.sony.imaging.app.base.playback.base.editor;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase;

/* loaded from: classes.dex */
public class Confirm extends PlayStateWithLayoutBase implements EditorConfirmLayoutBase.OnConfirmListener {
    /* JADX INFO: Access modifiers changed from: protected */
    public EditService getEditService() {
        return ((Editor) getContainer()).getEditService();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        ((EditorConfirmLayoutBase) getLayout(layoutTag)).setOnConfirmListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutClosing(String layoutTag) {
        ((EditorConfirmLayoutBase) getLayout(layoutTag)).setOnConfirmListener(null);
        super.onPlayLayoutClosing(layoutTag);
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase.OnConfirmListener
    public void onOk() {
        setNextState(Editor.ID_EXECUTOR, null);
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase.OnConfirmListener
    public void onCancel() {
        PlayRootContainer root = getRootContainer();
        PlayRootContainer.PB_MODE mode = (PlayRootContainer.PB_MODE) root.getData(PlayRootContainer.PROP_ID_PB_MODE);
        if (mode == PlayRootContainer.PB_MODE.INDEX) {
            setNextState(PlaySubApp.ID_INDEX_PB, null);
        } else {
            setNextState(PlaySubApp.ID_SINGLE_PB, null);
        }
    }
}
