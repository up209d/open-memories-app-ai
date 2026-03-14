package com.sony.imaging.app.portraitbeauty.playback.catchlight.effect;

import android.util.Log;
import com.sony.imaging.app.portraitbeauty.common.ImageEditor;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.scalar.graphics.OptimizedImage;

/* loaded from: classes.dex */
public class CatchLightSavingTask extends ImageEditor.ResultReflection {
    private static final String TAG = CatchLightSavingTask.class.getSimpleName();
    private Runnable executeHandler = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.playback.catchlight.effect.CatchLightSavingTask.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(CatchLightSavingTask.TAG, "======onPreExecute...catchlightLayout ==== ");
            Log.d(CatchLightSavingTask.TAG, "======doInBackground +");
            Log.d(CatchLightSavingTask.TAG, "======doInBackground -");
        }
    };
    private CatchLightPlayBackLayout mCatchLightLayout;
    private OptimizedImage mNewImage;

    public CatchLightSavingTask(CatchLightPlayBackLayout catchlightLayout, OptimizedImage newImage) {
        this.mNewImage = null;
        this.mCatchLightLayout = null;
        this.mNewImage = newImage;
        this.mCatchLightLayout = catchlightLayout;
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.ImageEditor.ResultReflection
    protected Runnable getExecutor() {
        return this.executeHandler;
    }
}
