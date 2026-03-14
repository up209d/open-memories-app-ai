package com.sony.imaging.app.digitalfilter.shooting.trigger;

import android.view.KeyEvent;
import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class GFMfAssistKeyHandler extends MfAssistKeyHandler {
    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        return isFocusSetKey(code) ? pushedMfAssistCustomKey() : super.onConvertedKeyDown(event, func);
    }

    private boolean isFocusSetKey(int code) {
        int[] keys = {code};
        int[] functions = Settings.getKeyFunction(keys);
        return functions != null && 61 == functions[0];
    }
}
