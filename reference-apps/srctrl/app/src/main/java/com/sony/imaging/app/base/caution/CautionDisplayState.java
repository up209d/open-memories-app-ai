package com.sony.imaging.app.base.caution;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.OLEDWrapper;

/* loaded from: classes.dex */
public class CautionDisplayState extends State {
    private static final String CAUTION_ID = "CAUTION_ID";
    private static final String COLUMN_NAME = "cautionid";
    private static final String LAYOUT_ID_CAUTION = "cautionLayout";
    private static final String LOG_EXECUTE_TERMINATE_DISPLAY_STATE = "executeTerminateDisplayState";
    private static final String LOG_TERMINAME_LAYOUT = "onTerminateLayout";
    private static final String LOG_TERMINATE_DISPLAY_STATE = "setTerminateDisplayState: ";
    private static final String TABLE_NAME = "APOType";
    private static final String TAG = "CautionDisplayState";
    private static ITerminateDisplayState mTerminateDisplayState;
    private CautionUtilityClass cautionUtil = CautionUtilityClass.getInstance();
    private ApoWrapper.APO_TYPE mApoType = ApoWrapper.APO_TYPE.UNKNOWN;
    private CautionLayout.ITerminateLayout terminate = new CautionLayout.ITerminateLayout() { // from class: com.sony.imaging.app.base.caution.CautionDisplayState.1
        @Override // com.sony.imaging.app.base.caution.CautionLayout.ITerminateLayout
        public void onTerminateLayout() {
            CautionDisplayState.this.executeTerminateDisplayState();
            Log.i(CautionDisplayState.TAG, CautionDisplayState.LOG_TERMINAME_LAYOUT);
        }
    };

    /* loaded from: classes.dex */
    public interface ITerminateDisplayState {
        void onTerminateDisplayState();
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle data = this.data;
        ((CautionLayout) getLayout(LAYOUT_ID_CAUTION)).setTerminateLayout(this.terminate);
        int[] cautionId = this.data.getIntArray(CAUTION_ID);
        int maxPriorityId = CautionProcessingFunction.getCautionId(cautionId);
        this.mApoType = judgeApoType(maxPriorityId);
        new CautionProcessingFunction(cautionId, getActivity());
        CautionProcessingFunction.priorityData mPriData = CautionProcessingFunction.getMaxPriorityId(cautionId);
        if (this.cautionUtil.getResourceInfo(mPriData.maxPriorityId, mPriData.type)[0] != null) {
            openLayout(LAYOUT_ID_CAUTION, data);
        } else {
            executeTerminateDisplayState();
            this.cautionUtil.disapperTrigger(mPriData.maxPriorityId);
        }
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        closeLayout(LAYOUT_ID_CAUTION);
    }

    private ApoWrapper.APO_TYPE judgeApoType(int cautionId) {
        ApoWrapper.APO_TYPE apoType;
        ApoWrapper.APO_TYPE apo_type = ApoWrapper.APO_TYPE.UNKNOWN;
        int kind = this.cautionUtil.judgeIdKind(cautionId);
        if (kind != 262144) {
            try {
                Cursor c = this.cautionUtil.dbManager.searchData(COLUMN_NAME, Integer.toString(cautionId), TABLE_NAME, kind);
                if (c != null) {
                    String str = c.getString(c.getColumnIndex(TABLE_NAME));
                    c.close();
                    if (str != null) {
                        if (str.equals("A")) {
                            apoType = ApoWrapper.APO_TYPE.SPECIAL;
                        } else if (str.equals("C")) {
                            apoType = ApoWrapper.APO_TYPE.NONE;
                        } else {
                            apoType = ApoWrapper.APO_TYPE.UNKNOWN;
                        }
                    } else {
                        apoType = ApoWrapper.APO_TYPE.UNKNOWN;
                    }
                } else {
                    apoType = ApoWrapper.APO_TYPE.UNKNOWN;
                }
            } catch (CursorIndexOutOfBoundsException e) {
                ApoWrapper.APO_TYPE apoType2 = ApoWrapper.APO_TYPE.UNKNOWN;
                return apoType2;
            }
        } else {
            apoType = ApoWrapper.APO_TYPE.SPECIAL;
        }
        return apoType;
    }

    public void setTerminateDisplayState(ITerminateDisplayState td) {
        mTerminateDisplayState = td;
        Log.i(TAG, LOG_TERMINATE_DISPLAY_STATE + td);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeTerminateDisplayState() {
        if (mTerminateDisplayState != null) {
            mTerminateDisplayState.onTerminateDisplayState();
        }
        Log.i(TAG, LOG_EXECUTE_TERMINATE_DISPLAY_STATE);
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return this.mApoType;
    }

    @Override // com.sony.imaging.app.fw.State
    public OLEDWrapper.OLED_TYPE getOledType() {
        return OLEDWrapper.OLED_TYPE.NO_CONTROL;
    }
}
