package com.sony.imaging.app.photoretouch.playback.layout.framing;

import android.util.Log;
import com.sony.imaging.app.photoretouch.PhotoRetouchKikiLog;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchBrowserSingleLayout;
import com.sony.imaging.app.photoretouch.playback.layout.framing.FramingLayout;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class FramingSavingTask extends ImageEditor.ResultReflection {
    private static final String TAG = FramingSavingTask.class.getSimpleName();
    private Runnable executeHandler = new Runnable() { // from class: com.sony.imaging.app.photoretouch.playback.layout.framing.FramingSavingTask.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(FramingSavingTask.TAG, "======doInBackground +");
            ImageEditor.setOrientationInfo(FramingSavingTask.this.mFrameInfo.orientation);
            PhotoRetouchBrowserSingleLayout.updateImage(FramingSavingTask.this.mNewImage);
            PhotoRetouchKikiLog.photoRetouchSavingLog(PhotoRetouchKikiLog.PHOTO_RETOUCH_KIKILOG_FRAMING);
            FramingSavingTask.this.mFrameInfo = null;
            ImageEditor.optimizedImages = null;
            Log.d(FramingSavingTask.TAG, "======doInBackground -");
        }
    };
    private FramingLayout.FrameInfo mFrameInfo;
    private OptimizedImage mNewImage;

    public FramingSavingTask(FramingLayout.FrameInfo frameInfo, OptimizedImage newImage) {
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
