package com.sony.imaging.app.base.playback.delete.single;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.playback.base.editor.TestThis;

/* loaded from: classes.dex */
public class DeleteSingleTestContents extends TestThis {
    @Override // com.sony.imaging.app.base.playback.base.editor.TestThis
    protected boolean testContents(int result) {
        if (result == -3) {
            CautionUtilityClass.getInstance().requestTrigger(1740);
        }
        return result == 0;
    }
}
