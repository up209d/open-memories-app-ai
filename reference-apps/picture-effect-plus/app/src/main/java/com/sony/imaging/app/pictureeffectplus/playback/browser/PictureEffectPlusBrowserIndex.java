package com.sony.imaging.app.pictureeffectplus.playback.browser;

import android.util.Log;
import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.pictureeffectplus.playback.layout.IPictureEffectPlusPlaybackTriggerFunction;
import com.sony.imaging.app.pictureeffectplus.playback.layout.PictureEffectPlusBrowserIndexLayout;

/* loaded from: classes.dex */
public class PictureEffectPlusBrowserIndex extends BrowserIndex implements IPictureEffectPlusPlaybackTriggerFunction {
    private static final String TAG = "PictureEffectPlusBrowserIndex";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        Log.i(TAG, "layoutTag:" + layoutTag);
        if (layoutTag.equals("NORMAL_PB")) {
            PictureEffectPlusBrowserIndexLayout layout = (PictureEffectPlusBrowserIndexLayout) getLayout(layoutTag);
            layout.setEachPbTrigger(this);
        }
    }

    @Override // com.sony.imaging.app.pictureeffectplus.playback.layout.IPictureEffectPlusPlaybackTriggerFunction
    public boolean transitionDeleteMultiple() {
        getRootContainer().changeApp("ID_DELETE_MULTIPLE");
        return true;
    }
}
