package com.sony.imaging.app.photoretouch.playback.layout.manualframing;

import android.util.Log;
import com.sony.imaging.app.photoretouch.PhotoRetouchKikiLog;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchBrowserSingleLayout;
import com.sony.imaging.app.photoretouch.playback.layout.manualframing.ManualFramingLayout;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class ManualSavingTask extends ImageEditor.ResultReflection {
    private static final String TAG = ManualSavingTask.class.getSimpleName();
    private Runnable executeHandler = new Runnable() { // from class: com.sony.imaging.app.photoretouch.playback.layout.manualframing.ManualSavingTask.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(ManualSavingTask.TAG, "======doInBackground +");
            ImageEditor.setOrientationInfo(ManualSavingTask.this.mFrameInfo.orientation);
            PhotoRetouchBrowserSingleLayout.updateImage(ManualSavingTask.this.mNewImage);
            PhotoRetouchKikiLog.photoRetouchSavingLog(PhotoRetouchKikiLog.PHOTO_RETOUCH_KIKILOG_FRAMING);
            ManualSavingTask.this.mFrameInfo = null;
            ImageEditor.optimizedImages = null;
            Log.d(ManualSavingTask.TAG, "======doInBackground -");
        }
    };
    private ManualFramingLayout.FrameInfo mFrameInfo;
    private OptimizedImage mNewImage;

    public ManualSavingTask(ManualFramingLayout.FrameInfo frameInfo, OptimizedImage newImage) {
        this.mNewImage = null;
        this.mFrameInfo = null;
        this.mFrameInfo = frameInfo;
        this.mNewImage = newImage;
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.ResultReflection
    protected Runnable getExecutor() {
        return this.executeHandler;
    }
}
