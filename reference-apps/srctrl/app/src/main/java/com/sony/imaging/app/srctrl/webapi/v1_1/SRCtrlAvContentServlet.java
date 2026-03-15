package com.sony.imaging.app.srctrl.webapi.v1_1;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.definition.StatusCode;
import com.sony.imaging.app.srctrl.webapi.specific.DeletingHandler;
import com.sony.imaging.app.srctrl.webapi.util.ApiCallLog;
import com.sony.mexi.orb.service.avcontent.v1_1.SmartRemoteControlAvContentServiceBase;
import com.sony.scalar.webapi.service.avcontent.v1_1.DeleteContentCallback;
import com.sony.scalar.webapi.service.avcontent.v1_1.common.struct.ContentURIs;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class SRCtrlAvContentServlet extends SmartRemoteControlAvContentServiceBase {
    private static final String TAG = SRCtrlAvContentServlet.class.getSimpleName();
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.service.avcontent.v1_1.SmartRemoteControlAvContentServiceBase
    public void deleteContent(ContentURIs contents, DeleteContentCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.e(TAG, "---> deleteContent()");
                    StateController stateController = StateController.getInstance();
                    if (!stateController.isWaitingDeletingStateChange() && StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition())) {
                        DeletingHandler.DeletingStatus status = DeletingHandler.getInstance().goToDeletingState(contents.uri);
                        switch (status) {
                            case SUCCESS:
                                returnCb.returnCb();
                                break;
                            default:
                                returnCb.handleStatus(StatusCode.SOME_CONTENT_COULD_NOT_BE_DELETED.toInt(), "Some contents could not be deleted.");
                                break;
                        }
                    } else {
                        Log.v(TAG, "Failed to delete content because of current application state");
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    }
                    Log.e(TAG, "<=== deleteContent()");
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    returnCb.handleStatus(StatusCode.ANY.toInt(), "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                        apiCallLog = null;
                    }
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }
}
