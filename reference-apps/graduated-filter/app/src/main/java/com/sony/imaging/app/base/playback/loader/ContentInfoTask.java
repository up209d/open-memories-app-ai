package com.sony.imaging.app.base.playback.loader;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;

/* loaded from: classes.dex */
public class ContentInfoTask extends Task {
    private static final String MSG_GROUP_INVALID = "getData group changed";
    private static final String TAG = ContentInfoTask.class.getSimpleName();
    private int mGroup;

    public ContentInfoTask(int position) {
        super(position);
        this.mGroup = ContentsManager.getInstance().getGroupPosition();
    }

    @Override // com.sony.imaging.app.base.playback.loader.Task
    public ContentInfo getData() {
        Log.d(TAG, LogHelper.getScratchBuilder("getData : ").append(getPos()).toString());
        ContentsManager mgr = ContentsManager.getInstance();
        if (this.mGroup != mgr.getGroupPosition()) {
            Log.w(TAG, MSG_GROUP_INVALID);
            return null;
        }
        ContentInfo info = mgr.getContentInfo(mgr.getContentsIdAt(getPos()));
        if (info != null) {
            info.getInt("FILE_SYSTEMProtectInfo");
            return info;
        }
        return info;
    }
}
