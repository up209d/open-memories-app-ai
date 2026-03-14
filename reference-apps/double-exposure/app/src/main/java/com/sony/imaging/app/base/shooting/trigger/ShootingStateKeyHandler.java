package com.sony.imaging.app.base.shooting.trigger;

import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class ShootingStateKeyHandler extends ShootingKeyHandlerBase {
    private static final String PTAG_RELEASE_LAG_END = "start shutter lag";
    private static final String PTAG_START_AUTOFOCUS = "StartAutoFocus";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        PTag.start("StartAutoFocus");
        ExecutorCreator executorCreator = ExecutorCreator.getInstance();
        if (!executorCreator.isAElockedOnAutoFocus()) {
            String behavior = null;
            String focusMode = AutoFocusModeController.getInstance().getValue();
            if ("af-s".equals(focusMode)) {
                behavior = "af_woaf";
            } else if ("af-c".equals(focusMode)) {
                behavior = "afc_woaf";
            }
            executorCreator.getSequence().autoFocus(null, behavior);
            return 1;
        }
        executorCreator.getSequence().autoFocus(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        PTag.start(PTAG_RELEASE_LAG_END);
        ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        ExecutorCreator.getInstance().getSequence().cancelTakePicture();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        AELController.getInstance().holdAELock(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        FocusModeController.getInstance().holdFocusControl(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AELController.getInstance().cancelAELock();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        AELController.getInstance().cancelAELock();
        return 1;
    }
}
