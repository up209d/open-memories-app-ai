package com.sony.imaging.app.base.shooting.trigger;

import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class ShootingStateKeyHandler extends ShootingKeyHandlerBase {
    private static final String PTAG_RELEASE_LAG_END = "start shutter lag";
    private static final String PTAG_START_AUTOFOCUS = "StartAutoFocus";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        if (isBulbOpenedBy(1)) {
            return -1;
        }
        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        if (isBulbOpenedBy(1)) {
            return -1;
        }
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
        if (isBulbOpenedBy(1)) {
            return -1;
        }
        PTag.start(PTAG_RELEASE_LAG_END);
        ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        if (isBulbOpenedBy(1)) {
            return -1;
        }
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

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isBulbOpenedBy(int shutterType) {
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        return executor.isBulbOpened() && executor.getShutterType() == shutterType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopBulbShooting() {
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        if (executor.isBulbOpened()) {
            executor.cancelTakePicture();
            DriveModeController cntl = DriveModeController.getInstance();
            if (cntl.isRemoteControl()) {
                cntl.setTempSelfTimer(0);
            }
        }
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        if (isBulbOpenedBy(1)) {
            stopBulbShooting();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        if (isBulbOpenedBy(1)) {
            stopBulbShooting();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        if (isBulbOpenedBy(1)) {
            stopBulbShooting();
        }
        return 1;
    }
}
