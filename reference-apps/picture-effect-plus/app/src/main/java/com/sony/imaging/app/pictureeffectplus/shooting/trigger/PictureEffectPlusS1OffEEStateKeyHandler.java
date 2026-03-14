package com.sony.imaging.app.pictureeffectplus.shooting.trigger;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler;
import com.sony.imaging.app.pictureeffectplus.shooting.PictureEffectEEState;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;

/* loaded from: classes.dex */
public class PictureEffectPlusS1OffEEStateKeyHandler extends S1OffEEStateKeyHandler {
    private static final String ITEM_ID = "ItemId";
    protected static final StringBuilder STRBUILD = new StringBuilder();
    private static final String TAG = "PictureEffectPlusS1OffEEStateKeyHandler";
    protected String FUNC_NAME = "";

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        PictureEffectEEState.setIsMenuStateAdd(true);
        Bundle bundle = new Bundle();
        bundle.putString("ItemId", PictureEffectPlusController.PICTUREEFFECTPLUS);
        openMenu(bundle);
        this.FUNC_NAME = "pushedCenterKey";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return 1;
    }
}
