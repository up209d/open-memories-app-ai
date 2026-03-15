package com.sony.imaging.app.startrails.menu.base.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class LastBastionMenuLayout extends BaseMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return new View(getActivity());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
        STExposureModeController emc = STExposureModeController.getInstance();
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), getKeyHandler());
        CautionUtilityClass.getInstance().requestTrigger(emc.getCautionId());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        STExposureModeController emc = STExposureModeController.getInstance();
        CautionUtilityClass.getInstance().disapperTrigger(emc.getCautionId());
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int modeDialProcessing() {
        return super.turnedModeDial();
    }

    protected IkeyDispatchEach getKeyHandler() {
        return new IkeyDispatchEach(null, 0 == true ? 1 : 0) { // from class: com.sony.imaging.app.startrails.menu.base.layout.LastBastionMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedPlayBackKey() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMovieRecKey() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedUmRemoteRecKey() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedIRRecKey() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedShootingModeKey() {
                return -1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedModeDial() {
                if (ModeDialDetector.getModeDialPosition() == -1) {
                    return 1;
                }
                CautionUtilityClass.getInstance().executeTerminate();
                return LastBastionMenuLayout.this.modeDialProcessing();
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMenuKey() {
                LastBastionMenuLayout.this.getActivity().finish();
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                return 1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedS1Key() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int releasedS1Key() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedEVDial() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedFocusModeDial() {
                return 0;
            }
        };
    }
}
