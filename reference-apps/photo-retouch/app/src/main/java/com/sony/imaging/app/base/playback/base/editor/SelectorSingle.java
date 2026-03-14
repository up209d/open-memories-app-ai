package com.sony.imaging.app.base.playback.base.editor;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.ISelectorTriggerFunction;
import com.sony.imaging.app.base.playback.layout.SelectorSingleLayout;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public abstract class SelectorSingle extends SingleStateBase implements ISelectorTriggerFunction {
    protected EditService getEditService() {
        return ((Editor) getContainer()).getEditService();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        Layout layout = getLayout(layoutTag);
        if (SelectorSingleLayout.class.isInstance(layout)) {
            ((SelectorSingleLayout) layout).setSelectorTrigger(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutClosing(String layoutTag) {
        Layout layout = getLayout(layoutTag);
        if (SelectorSingleLayout.class.isInstance(layout)) {
            ((SelectorSingleLayout) layout).setSelectorTrigger(null);
        }
        super.onPlayLayoutClosing(layoutTag);
    }

    @Override // com.sony.imaging.app.base.playback.layout.ISelectorTriggerFunction
    public boolean onOk() {
        setNextState(Editor.ID_CONFIRM, null);
        return true;
    }
}
