package com.sony.imaging.app.srctrl.webapi.v1_3;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.srctrl.playback.contents.PrepareTransferList;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.definition.StatusCode;
import com.sony.imaging.app.srctrl.webapi.util.ApiCallLog;
import com.sony.mexi.orb.service.avcontent.v1_3.SmartRemoteControlAvContentServiceBase;
import com.sony.scalar.webapi.service.avcontent.v1_3.GetContentListCallback;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.Content;
import com.sony.scalar.webapi.service.avcontent.v1_3.common.struct.ContentListSource;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class SRCtrlAvContentServlet extends SmartRemoteControlAvContentServiceBase {
    private static final String TAG = SRCtrlAvContentServlet.class.getSimpleName();
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.service.avcontent.v1_3.SmartRemoteControlAvContentServiceBase
    public void getContentList(ContentListSource conListSource, GetContentListCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    boolean DEBUGSUCCEEDED = false;
                    Log.v(TAG, "---> getContentList()");
                    Content[] contentList = null;
                    if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition()) || PrepareTransferList.getInstance().isCancelState()) {
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    } else {
                        contentList = PrepareTransferList.getInstance().getContentList(conListSource.uri, conListSource.stIdx.intValue(), conListSource.cnt.intValue(), conListSource.type, conListSource.target, conListSource.view, conListSource.sort);
                        if (contentList != null) {
                            returnCb.returnCb(contentList);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Cannot obtain content list.");
                        }
                    }
                    Log.v(TAG, "<=== getContentList(count=" + ((!DEBUGSUCCEEDED || contentList == null) ? "error!!" : Integer.valueOf(contentList.length)) + LogHelper.MSG_CLOSE_BRACKET);
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
