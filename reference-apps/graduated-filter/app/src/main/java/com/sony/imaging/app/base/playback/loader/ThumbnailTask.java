package com.sony.imaging.app.base.playback.loader;

import android.graphics.Bitmap;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;

/* loaded from: classes.dex */
public class ThumbnailTask extends Task {
    private static final String MSG_GROUP_INVALID = "getData group changed";
    private static final String TAG = ThumbnailTask.class.getSimpleName();
    private int mGroup;
    private ContentsManager.ThumbnailOption mOption;
    private boolean mRotateIt;

    public ThumbnailTask(int position, boolean rotateIt) {
        super(position);
        this.mGroup = ContentsManager.getInstance().getGroupPosition();
        this.mRotateIt = rotateIt;
    }

    public ThumbnailTask(int position, ContentsManager.ThumbnailOption option) {
        super(position);
        this.mGroup = ContentsManager.getInstance().getGroupPosition();
        this.mOption = option;
    }

    @Override // com.sony.imaging.app.base.playback.loader.Task
    public Object getData() {
        Log.d(TAG, LogHelper.getScratchBuilder("getData : ").append(getPos()).toString());
        ContentsManager mgr = ContentsManager.getInstance();
        if (this.mGroup != mgr.getGroupPosition()) {
            Log.w(TAG, MSG_GROUP_INVALID);
            return null;
        }
        if (this.mOption != null) {
            Bitmap bmp = mgr.getThumbnail(mgr.getContentsIdAt(getPos()), this.mOption);
            return bmp;
        }
        Bitmap bmp2 = mgr.getThumbnail(mgr.getContentsIdAt(getPos()), this.mRotateIt);
        return bmp2;
    }
}
