package com.sony.imaging.app.digitalfilter.shooting.trigger;

import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.fw.BaseInvalidKeyHandler;

/* loaded from: classes.dex */
public class GFAdjustmentStateKeyHandler extends BaseInvalidKeyHandler {
    private static final String NEXT_STATE = "Processing";
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        return "Menu";
    }

    private void transiteToSaving() {
        GFImageAdjustmentUtil.getInstance().terminateAdjustmentPreview();
        StateBase state = (StateBase) this.target;
        state.setNextState(NEXT_STATE, null);
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        transiteToSaving();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        transiteToSaving();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        GFImageAdjustmentUtil.getInstance().terminateAdjustmentPreview();
        GFCompositProcess.storeCompositImage();
        StateBase state = (StateBase) this.target;
        state.setNextState("Development", null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        GFImageAdjustmentUtil.getInstance().terminateAdjustmentPreview();
        GFCompositProcess.storeCompositImage();
        StateBase state = (StateBase) this.target;
        state.setNextState("Development", null);
        return 1;
    }
}
