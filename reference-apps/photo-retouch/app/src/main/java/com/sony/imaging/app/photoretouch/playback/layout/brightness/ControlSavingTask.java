package com.sony.imaging.app.photoretouch.playback.layout.brightness;

import android.util.Log;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.photoretouch.PhotoRetouchKikiLog;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchBrowserSingleLayout;
import com.sony.imaging.app.photoretouch.playback.layout.contrast.ContrastControlLayout;
import com.sony.imaging.app.photoretouch.playback.layout.saturation.SaturationControlLayout;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class ControlSavingTask extends ImageEditor.ResultReflection {
    private Layout mLayout;
    private OptimizedImage mNewImage;
    private final String TAG = ControlSavingTask.class.getSimpleName();
    private int mControlKit = 0;
    private Runnable executeHandler = new Runnable() { // from class: com.sony.imaging.app.photoretouch.playback.layout.brightness.ControlSavingTask.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(ControlSavingTask.this.TAG, "======doInBackground +");
            if (ControlSavingTask.this.mLayout instanceof ContrastControlLayout) {
                ControlSavingTask.this.mControlKit = 1;
            } else if (ControlSavingTask.this.mLayout instanceof BrightnessControlLayout) {
                ControlSavingTask.this.mControlKit = 2;
            } else if (ControlSavingTask.this.mLayout instanceof SaturationControlLayout) {
                ControlSavingTask.this.mControlKit = 3;
            }
            ControlSavingTask.this.getKikiLog(ControlSavingTask.this.mControlKit);
            PhotoRetouchBrowserSingleLayout.updateImage(ControlSavingTask.this.mNewImage);
            ImageEditor.optimizedImages = null;
            ControlSavingTask.this.mControlKit = 0;
            Log.d(ControlSavingTask.this.TAG, "======doInBackground -");
        }
    };

    public ControlSavingTask(Layout layout, OptimizedImage newImage) {
        this.mNewImage = null;
        this.mLayout = null;
        this.mLayout = layout;
        this.mNewImage = newImage;
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.ResultReflection
    protected Runnable getExecutor() {
        return this.executeHandler;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getKikiLog(int controlKit) {
        switch (controlKit) {
            case 1:
                PhotoRetouchKikiLog.photoRetouchSavingLog(PhotoRetouchKikiLog.PHOTO_RETOUCH_KIKILOG_COLOR);
                return;
            case 2:
                PhotoRetouchKikiLog.photoRetouchSavingLog(PhotoRetouchKikiLog.PHOTO_RETOUCH_KIKILOG_BRIGHTNESS);
                return;
            case 3:
                PhotoRetouchKikiLog.photoRetouchSavingLog(PhotoRetouchKikiLog.PHOTO_RETOUCH_KIKILOG_SATURATION);
                return;
            default:
                Log.d(this.TAG, "No instance is found for control kit");
                return;
        }
    }
}
