package com.sony.imaging.app.base.playback.delete.single;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.base.editor.Editor;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class DeleteSingle extends Editor {
    public static final String ID_FINALIZER = "ID_FINALIZER";
    private static final String INH_ID_PB_MODIFY = "INH_FEATURE_COMMON_PB_MODIFY";
    private static final String MSG_CAUTION_CALLBACK = "InhCautionCallback";
    private static final String MSG_MEDIA_ERR = "requestTrigger because of media error";
    private InhCautionCallback mCautionCallback = null;

    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected String getEntryState() {
        if (isMediaErr()) {
            return null;
        }
        return Editor.ID_TEST_THIS;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.editor.Editor
    public EditService getEditService() {
        return DeleteService.getInstance();
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 12;
    }

    /* loaded from: classes.dex */
    class InhCautionCallback implements CautionUtilityClass.triggerCautionCallback {
        InhCautionCallback() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionUtilityClass.triggerCautionCallback
        public void onCallback() {
            Log.i(DeleteSingle.this.getLogTag(), DeleteSingle.MSG_CAUTION_CALLBACK);
            CautionUtilityClass.getInstance().unregisterCallbackTriggerDisapper(this);
            DeleteSingle.this.mCautionCallback = null;
            DeleteSingle.this.removeState();
        }
    }

    protected boolean isMediaErr() {
        if (!MediaNotificationManager.getInstance().isMounted()) {
            return false;
        }
        String[] media = AvindexStore.getExternalMediaIds();
        AvailableInfo.update();
        return AvailableInfo.isInhibition(INH_ID_PB_MODIFY, media[0]);
    }

    @Override // com.sony.imaging.app.base.playback.base.editor.Editor, com.sony.imaging.app.base.playback.base.PlaySubApp, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (isMediaErr()) {
            Log.i(getLogTag(), MSG_MEDIA_ERR);
            CautionUtilityClass.getInstance().requestTrigger(32769);
            this.mCautionCallback = new InhCautionCallback();
            CautionUtilityClass.getInstance().registerCallbackTriggerDisapper(this.mCautionCallback);
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.editor.Editor, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mCautionCallback != null) {
            CautionUtilityClass.getInstance().unregisterCallbackTriggerDisapper(this.mCautionCallback);
            this.mCautionCallback = null;
        }
        super.onPause();
    }
}
