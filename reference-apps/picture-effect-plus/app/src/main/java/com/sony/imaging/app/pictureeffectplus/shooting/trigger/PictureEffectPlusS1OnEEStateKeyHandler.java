package com.sony.imaging.app.pictureeffectplus.shooting.trigger;

import android.util.Log;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.pictureeffectplus.shooting.PictureEffectEEState;

/* loaded from: classes.dex */
public class PictureEffectPlusS1OnEEStateKeyHandler extends S1OnEEStateKeyHandler {
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final String TAG = "PictureEffectPlusS1OnEEStateKeyHandler";
    private String FUNC_NAME = "";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        PictureEffectEEState.setIsMenuStateAdd(false);
        this.FUNC_NAME = "onCreateView";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return super.pushedS2Key();
    }
}
