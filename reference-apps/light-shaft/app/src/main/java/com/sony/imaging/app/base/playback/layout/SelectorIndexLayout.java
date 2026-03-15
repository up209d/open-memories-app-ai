package com.sony.imaging.app.base.playback.layout;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public abstract class SelectorIndexLayout extends IndexLayoutGridViewBase implements NotificationListener {
    private static final String MSG_ON_ITEM_SELECTED = "onItemSelected : ";
    private static final String MSG_SELECT = "select ";
    private static final String MSG_SELECT_GROUP = "select group : ";
    private static final String MSG_UPDATE_GP_CHECK = "SelectorIndexLayout invalidate update Group CheckBox";
    protected ISelectorTriggerFunction mSelectorTrigger;

    protected abstract EditService getEditService();

    protected abstract int getGroupCheckBoxResourceId();

    public void setSelectorTrigger(ISelectorTriggerFunction trigger) {
        this.mSelectorTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.IndexLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ContentsManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        ContentsManager.getInstance().removeNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    public void invalidate() {
        super.invalidate();
        upadteGroupCheckbox();
    }

    protected void upadteGroupCheckbox() {
        Log.d(this.TAG, MSG_UPDATE_GP_CHECK);
        CheckBox checkbox = (CheckBox) getView().findViewById(getGroupCheckBoxResourceId());
        if (checkbox != null) {
            if (getEditService().isGroupSelectable()) {
                checkbox.setVisibility(0);
                checkbox.setChecked(getEditService().isGroupChecked());
            } else {
                checkbox.setVisibility(4);
            }
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{ContentsManager.NOTIFICATION_TAG_CURRENT_FILE};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        upadteGroupCheckbox();
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.d(this.TAG, LogHelper.getScratchBuilder(MSG_ON_ITEM_SELECTED).append(arg2).append(LogHelper.MSG_COMMA).append(arg3).toString());
        String beep = BeepUtilityRsrcTable.BEEP_ID_NONE;
        if (-2 != arg2) {
            if (onContentEntered(arg2)) {
                beep = getEditService().isChecked(arg2) ? BeepUtilityRsrcTable.BEEP_ID_ON : BeepUtilityRsrcTable.BEEP_ID_OFF;
            }
        } else if (onGroupEntered()) {
            beep = getEditService().isGroupChecked() ? BeepUtilityRsrcTable.BEEP_ID_ON : BeepUtilityRsrcTable.BEEP_ID_OFF;
        }
        BeepUtility.getInstance().playBeep(beep);
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected boolean onContentEntered(int position) {
        int[] result = {0};
        return onContentEntered(position, result);
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected boolean onGroupEntered() {
        int[] result = {0};
        return onGroupEntered(result);
    }

    protected boolean onContentEntered(int position, int[] result) {
        EditService service = getEditService();
        result[0] = service.select(!service.isChecked(position), position);
        Log.d(this.TAG, LogHelper.getScratchBuilder(MSG_SELECT).append(position).append(LogHelper.MSG_COLON).append(result[0]).toString());
        invalidate();
        return result[0] == 0;
    }

    protected boolean onGroupEntered(int[] result) {
        EditService service = getEditService();
        result[0] = service.selectGroup(!service.isGroupChecked());
        Log.d(this.TAG, LogHelper.getScratchBuilder(MSG_SELECT_GROUP).append(result[0]).toString());
        invalidate();
        return result[0] == 0;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        thinZoomKey();
        transitionSinglePb();
        return 1;
    }
}
