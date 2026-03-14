package com.sony.imaging.app.photoretouch.playback.layout.softskin;

import android.graphics.Rect;
import android.util.Log;
import com.sony.imaging.app.photoretouch.PhotoRetouchKikiLog;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchBrowserSingleLayout;
import com.sony.scalar.graphics.OptimizedImage;
import java.util.Vector;

/* loaded from: classes.dex */
public class SoftSkinSavingTask extends ImageEditor.ResultReflection {
    private static final String TAG = SoftSkinSavingTask.class.getSimpleName();
    private Runnable executeHandler = new Runnable() { // from class: com.sony.imaging.app.photoretouch.playback.layout.softskin.SoftSkinSavingTask.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(SoftSkinSavingTask.TAG, "======doInBackground +..is mem available= " + ImageEditor.isSpaceAvailableInMemoryCard());
            OptimizedImage output = ImageEditor.applySkinLevel(ImageEditor.optimizedImages[0], SoftSkinSavingTask.this.mFaceList, SoftSkinSavingTask.this.mIso, SoftSkinSavingTask.this.mLevel);
            Log.d(SoftSkinSavingTask.TAG, "======doInBackground -");
            PhotoRetouchBrowserSingleLayout.updateImage(output);
            PhotoRetouchKikiLog.photoRetouchSavingLog(PhotoRetouchKikiLog.PHOTO_RETOUCH_KIKILOG_BEAUTI_EFFECT);
            ImageEditor.optimizedImages = null;
        }
    };
    private Vector<Rect> mFaceList;
    private int mIso;
    private int mLevel;

    public SoftSkinSavingTask(Vector<Rect> faceList, int iso, int level) {
        this.mLevel = -1;
        this.mLevel = level;
        this.mFaceList = faceList;
        this.mIso = iso;
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.ResultReflection
    protected Runnable getExecutor() {
        return this.executeHandler;
    }
}
