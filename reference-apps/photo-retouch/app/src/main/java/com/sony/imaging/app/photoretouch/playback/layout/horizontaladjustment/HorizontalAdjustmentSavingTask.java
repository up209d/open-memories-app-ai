package com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment;

import android.util.Log;
import com.sony.imaging.app.photoretouch.PhotoRetouchKikiLog;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchBrowserSingleLayout;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class HorizontalAdjustmentSavingTask extends ImageEditor.ResultReflection {
    private final String TAG = HorizontalAdjustmentSavingTask.class.getSimpleName();
    private Runnable executeHandler = new Runnable() { // from class: com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment.HorizontalAdjustmentSavingTask.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(HorizontalAdjustmentSavingTask.this.TAG, "======doInBackground +");
            PhotoRetouchBrowserSingleLayout.updateImage(HorizontalAdjustmentSavingTask.this.mNewImage);
            PhotoRetouchKikiLog.photoRetouchSavingLog(PhotoRetouchKikiLog.PHOTO_RETOUCH_KIKILOG_HORIZONTAL);
            ImageEditor.optimizedImages = null;
        }
    };
    private OptimizedImage mNewImage;

    public HorizontalAdjustmentSavingTask(OptimizedImage newImage) {
        this.mNewImage = null;
        this.mNewImage = newImage;
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.ResultReflection
    protected Runnable getExecutor() {
        return this.executeHandler;
    }
}
