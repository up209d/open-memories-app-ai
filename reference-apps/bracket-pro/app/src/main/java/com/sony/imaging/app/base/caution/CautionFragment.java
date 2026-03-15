package com.sony.imaging.app.base.caution;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.fw.StateHandle;

/* loaded from: classes.dex */
public class CautionFragment extends ContainerState {
    private static final String LOG_ADD_CHILD_STATE = "addChildState mHandle:";
    private static final String LOG_ON_TERMINATE_DISPLAY_STATE = "onTerminateDisplayState";
    private static final String LOG_REPLACE_CHILD_STATE = "replaceChildState mHandle:";
    private static final String STATE_NAME = "CautionDisplayState";
    private static final String TAG = "CautionFragment";
    private CautionUtilityClass cautionUtil = CautionUtilityClass.getInstance();
    private StateHandle mHandle = null;
    private CautionDisplayState.ITerminateDisplayState terminate = new CautionDisplayState.ITerminateDisplayState() { // from class: com.sony.imaging.app.base.caution.CautionFragment.1
        @Override // com.sony.imaging.app.base.caution.CautionDisplayState.ITerminateDisplayState
        public void onTerminateDisplayState() {
            Log.i(CautionFragment.TAG, CautionFragment.LOG_ON_TERMINATE_DISPLAY_STATE);
            boolean isRemoveState = false;
            CautionUtilityClass.cautionData cData = CautionFragment.this.cautionUtil.getCurrentCautionData();
            if (cData == null) {
                isRemoveState = true;
            } else if (cData.kind == 131072 || cData.kind == 262144) {
                isRemoveState = true;
            }
            if (isRemoveState) {
                CautionFragment.this.removeState();
                CautionFragment.this.clearStatehandle();
            }
            if (cData != null) {
                CautionFragment.this.cautionUtil.disapperTrigger(cData.maxPriorityId, cData.type);
            }
        }
    };

    @Override // com.sony.imaging.app.fw.ContainerState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cautionStateChange(this.data);
        ((CautionDisplayState) getState(STATE_NAME)).setTerminateDisplayState(this.terminate);
    }

    private void cautionStateChange(Bundle bundle) {
        if (this.mHandle == null) {
            this.mHandle = addChildState(STATE_NAME, bundle);
            Log.i(TAG, LOG_ADD_CHILD_STATE + this.mHandle);
        } else {
            replaceChildState(this.mHandle, STATE_NAME, bundle);
            Log.i(TAG, LOG_REPLACE_CHILD_STATE + this.mHandle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearStatehandle() {
        BaseApp.mCautionHandle = null;
    }
}
