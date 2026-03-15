package com.sony.imaging.app.base.playback.base.editor;

import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.EditService;

/* loaded from: classes.dex */
public abstract class TestThis extends PlayStateBase {
    protected abstract boolean testContents(int i);

    protected EditService getEditService() {
        return ((Editor) getContainer()).getEditService();
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ContentsManager mgr = ContentsManager.getInstance();
        EditService service = getEditService();
        int result = service.select(true, mgr.getContentsPosition());
        if (testContents(result)) {
            setNextState(Editor.ID_CONFIRM, null);
        } else {
            getContainer().removeState();
        }
    }
}
