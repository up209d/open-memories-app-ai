package com.sony.imaging.app.base.playback.layout;

import android.util.Log;
import android.widget.CheckBox;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.EditService;

/* loaded from: classes.dex */
public abstract class SelectorSingleLayout extends SingleLayoutBase {
    private static final String MSG_SELECT_CONTENT = "selectContent : ";
    protected ISelectorTriggerFunction mSelectorTrigger;

    protected abstract int getCheckBoxResourceId();

    protected abstract EditService getEditService();

    public void setSelectorTrigger(ISelectorTriggerFunction trigger) {
        this.mSelectorTrigger = trigger;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateDisplay() {
        super.updateDisplay();
        ContentsManager mgr = ContentsManager.getInstance();
        EditService service = getEditService();
        CheckBox checkbox = (CheckBox) getView().findViewById(getCheckBoxResourceId());
        int file = mgr.getContentsPosition();
        if (service.isSelectable(file)) {
            checkbox.setVisibility(0);
            checkbox.setChecked(service.isChecked(file));
        } else {
            checkbox.setVisibility(4);
        }
    }

    public int selectContent() {
        ContentsManager mgr = ContentsManager.getInstance();
        EditService service = getEditService();
        int file = mgr.getContentsPosition();
        int result = service.select(!service.isChecked(file), file);
        Log.d(this.TAG, LogHelper.getScratchBuilder(MSG_SELECT_CONTENT).append(file).append(LogHelper.MSG_HYPHEN).append(result).toString());
        return result;
    }
}
