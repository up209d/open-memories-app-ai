package com.sony.imaging.app.base.caution;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class CautionLayout extends Layout {
    private static final String CAUTION_ID = "CAUTION_ID";
    private static final String LOG_CREATE_CAUTION_VIEW = "createCautionView:";
    private static final String LOG_EXECUTE_TERMINATE = "executeTerminate";
    private static final String LOG_ON_TERMINATE = "onTerminate";
    private static final String LOG_SET_TERMINATE_LAYOUT = "setTerminateLayout ITerminateLayout:";
    private static final String TAG = "CautionLayout";
    private ITerminateLayout mTerminateLayout;
    public CautionProcessingFunction cautionfunc = null;
    private CautionUtilityClass cautionUtil = CautionUtilityClass.getInstance();
    protected retractLensCautionCallback mCallback = null;
    private CautionProcessingFunction.terminateCaution terminate = new CautionProcessingFunction.terminateCaution() { // from class: com.sony.imaging.app.base.caution.CautionLayout.1
        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.terminateCaution
        public void onTerminate() {
            Log.i(CautionLayout.TAG, CautionLayout.LOG_ON_TERMINATE);
            CautionLayout.this.executeTerminate();
        }
    };

    /* loaded from: classes.dex */
    public interface ITerminateLayout {
        void onTerminateLayout();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return createCautionView();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        updateView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.cautionUtil.setTerminateCaution(null);
    }

    private View createCautionView() {
        int[] cautionId = this.data.getIntArray(CAUTION_ID);
        this.cautionfunc = new CautionProcessingFunction(cautionId, getActivity());
        this.cautionUtil.setTerminateCaution(this.terminate);
        View mView = this.cautionfunc.getView();
        if (mView == null) {
            executeTerminate();
            return null;
        }
        Log.i(TAG, LOG_CREATE_CAUTION_VIEW + mView);
        int dialogType = this.cautionfunc.getDialogType();
        if (-1 == dialogType) {
            mView.setBackgroundDrawable(BACKGROUND_TRANSPARENT);
        } else {
            mView.setBackgroundDrawable(null);
        }
        if (this.cautionUtil.getCurrentCautionData() != null) {
            int currentId = this.cautionUtil.getCurrentCautionData().maxPriorityId;
            if (currentId == 2393) {
                this.mCallback = new retractLensCautionCallback();
                this.cautionUtil.registerCallbackTriggerDisapper(this.mCallback);
                return mView;
            }
            return mView;
        }
        return mView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class retractLensCautionCallback implements CautionUtilityClass.triggerCautionCallback {
        retractLensCautionCallback() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionUtilityClass.triggerCautionCallback
        public void onCallback() {
            CautionLayout.this.cautionUtil.unregisterCallbackTriggerDisapper(CautionLayout.this.mCallback);
            ExecutorCreator.getInstance().reupdateSequence();
            CautionLayout.this.mCallback = null;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (this.cautionfunc == null) {
            return 1;
        }
        int ret = this.cautionfunc.onKeyDown(keyCode, event);
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (this.cautionfunc == null) {
            return 1;
        }
        int ret = this.cautionfunc.onKeyUp(keyCode, event);
        return ret;
    }

    public void setTerminateLayout(ITerminateLayout tl) {
        this.mTerminateLayout = tl;
        Log.i(TAG, LOG_SET_TERMINATE_LAYOUT + tl);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeTerminate() {
        this.mTerminateLayout.onTerminateLayout();
        Log.i(TAG, LOG_EXECUTE_TERMINATE);
    }
}
