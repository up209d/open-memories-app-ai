package com.sony.imaging.app.photoretouch.playback.layout.resize;

import android.util.Log;
import com.sony.imaging.app.photoretouch.PhotoRetouchKikiLog;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchBrowserSingleLayout;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class ResizeAndSaveTask extends ImageEditor.ResultReflection {
    private final String TAG = ResizeAndSaveTask.class.getSimpleName();
    private Runnable executeHandler = new Runnable() { // from class: com.sony.imaging.app.photoretouch.playback.layout.resize.ResizeAndSaveTask.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(ResizeAndSaveTask.this.TAG, "======onPreExecute...resizeLayout = " + ResizeAndSaveTask.this.mResizeLayout);
            Log.d(ResizeAndSaveTask.this.TAG, "======doInBackground +");
            PhotoRetouchBrowserSingleLayout.updateImage(ResizeAndSaveTask.this.mNewImage);
            PhotoRetouchKikiLog.photoRetouchSavingLog(PhotoRetouchKikiLog.PHOTO_RETOUCH_KIKILOG_RESIZE);
            Log.d(ResizeAndSaveTask.this.TAG, "======doInBackground -");
        }
    };
    private OptimizedImage mNewImage;
    private ResizeLayout mResizeLayout;

    public ResizeAndSaveTask(ResizeLayout resizeLayout, OptimizedImage newImage) {
        this.mNewImage = null;
        this.mResizeLayout = null;
        this.mNewImage = newImage;
        this.mResizeLayout = resizeLayout;
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.ResultReflection
    protected Runnable getExecutor() {
        return this.executeHandler;
    }
}
