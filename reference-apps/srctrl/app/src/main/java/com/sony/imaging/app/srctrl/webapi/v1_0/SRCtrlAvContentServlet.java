package com.sony.imaging.app.srctrl.webapi.v1_0;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.srctrl.playback.contents.GetContentProxy;
import com.sony.imaging.app.srctrl.playback.contents.PrepareTransferList;
import com.sony.imaging.app.srctrl.streaming.StreamingLoader;
import com.sony.imaging.app.srctrl.streaming.StreamingProxy;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.definition.StatusCode;
import com.sony.imaging.app.srctrl.webapi.specific.StreamingStatusEventHandler;
import com.sony.imaging.app.srctrl.webapi.util.ApiCallLog;
import com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase;
import com.sony.mexi.webapi.mandatory.v1_0.MethodTypeHandler;
import com.sony.scalar.webapi.service.avcontent.v1_0.GetSchemeListCallback;
import com.sony.scalar.webapi.service.avcontent.v1_0.GetSourceListCallback;
import com.sony.scalar.webapi.service.avcontent.v1_0.PauseStreamingCallback;
import com.sony.scalar.webapi.service.avcontent.v1_0.RequestToNotifyStreamingStatusCallback;
import com.sony.scalar.webapi.service.avcontent.v1_0.SeekStreamingPositionCallback;
import com.sony.scalar.webapi.service.avcontent.v1_0.SetStreamingContentCallback;
import com.sony.scalar.webapi.service.avcontent.v1_0.StartStreamingCallback;
import com.sony.scalar.webapi.service.avcontent.v1_0.StopStreamingCallback;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.ContentScheme;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.ContentSource;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.Polling;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.SeekPositionMsec;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.StreamingContentInfo;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.StreamingStatus;
import com.sony.scalar.webapi.service.avcontent.v1_0.common.struct.StreamingURL;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class SRCtrlAvContentServlet extends SmartRemoteControlAvContentServiceBase {
    private static final String REMOTE_PLAYTYPE = "simpleStreaming";
    private static final String TAG = SRCtrlAvContentServlet.class.getSimpleName();
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public void getMethodTypes(String arg0, MethodTypeHandler arg1) {
        if (SRCtrlEnvironment.getInstance().isStreamingPlaybackAPISupported()) {
            super.getMethodTypes(arg0, arg1);
        } else {
            arg1.sendRaw("[\"getSchemeList\",[],[\"{\\\"scheme\\\":\\\"string\\\"}*\"],\"1.0\"],[\"getSourceList\",[\"{\\\"scheme\\\":\\\"string\\\"}\"],[\"{\\\"source\\\":\\\"string\\\"}*\"],\"1.0\"]");
        }
    }

    @Override // com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase
    public void getSchemeList(GetSchemeListCallback returnCb) {
        Log.e(TAG, "<=== getSchemeList");
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    boolean DEBUGSUCCEEDED = false;
                    ContentScheme[] contentScheme = null;
                    if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition()) || PrepareTransferList.getInstance().isCancelState()) {
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    } else {
                        String[] schemeList = GetContentProxy.getSchemeList();
                        if (schemeList != null) {
                            contentScheme = new ContentScheme[schemeList.length];
                            for (int index = 0; index < schemeList.length; index++) {
                                contentScheme[index] = new ContentScheme();
                                contentScheme[index].scheme = schemeList[index];
                            }
                            returnCb.returnCb(contentScheme);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Cannot obtain scheme lists.");
                        }
                    }
                    Log.e(TAG, "<=== getSchemeList(Scheme=" + ((!DEBUGSUCCEEDED || contentScheme == null) ? "error!!" : contentScheme[0].scheme) + LogHelper.MSG_CLOSE_BRACKET);
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

    @Override // com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase
    public void getSourceList(ContentScheme contentScheme, GetSourceListCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    boolean DEBUGSUCCEEDED = false;
                    ContentSource[] contentSource = null;
                    if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition()) || PrepareTransferList.getInstance().isCancelState()) {
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    } else {
                        String[] sourceList = GetContentProxy.getSourceList(contentScheme.scheme);
                        if (sourceList != null) {
                            contentSource = new ContentSource[sourceList.length];
                            for (int index = 0; index < sourceList.length; index++) {
                                contentSource[index] = new ContentSource();
                                contentSource[index].source = sourceList[index];
                            }
                            returnCb.returnCb(contentSource);
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Cannot obtain source lists.");
                        }
                    }
                    Log.e(TAG, "<=== getSourceList(Scheme=" + ((!DEBUGSUCCEEDED || contentScheme == null) ? "error!!" : contentSource[0].source) + LogHelper.MSG_CLOSE_BRACKET);
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

    @Override // com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase
    public void setStreamingContent(StreamingContentInfo streamingContentInfo, SetStreamingContentCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.v(TAG, "---> setStreamingContent(uri=" + ((streamingContentInfo == null || streamingContentInfo.uri == null) ? "n/a" : streamingContentInfo.uri) + ", remotePlayType=" + (streamingContentInfo.remotePlayType != null ? streamingContentInfo.remotePlayType : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    boolean DEBUGSUCCEEDED = false;
                    StreamingURL streamingURL = new StreamingURL();
                    if (!SRCtrlEnvironment.getInstance().isStreamingPlaybackAPISupported()) {
                        returnCb.handleStatus(StatusCode.NO_SUCH_METHOD.toInt(), "No Such Method");
                    } else if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition()) && !StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    } else if (REMOTE_PLAYTYPE.equals(streamingContentInfo.remotePlayType)) {
                        StateController.getInstance().setAppCondition(StateController.AppCondition.PLAYBACK_STREAMING);
                        boolean result = StreamingProxy.setStreamContent(streamingContentInfo.uri);
                        if (result) {
                            streamingURL.playbackUrl = SRCtrlEnvironment.getInstance().getStreamingURL();
                            returnCb.returnCb(streamingURL);
                            DEBUGSUCCEEDED = true;
                        } else {
                            if (StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
                                StateController.getInstance().setAppCondition(StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER);
                            }
                            returnCb.handleStatus(StatusCode.ILLEGAL_DATA_FORMAT.toInt(), "Cannot setStreamingContent.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ILLEGAL_DATA_FORMAT.toInt(), "Not Available RemotePlayType");
                    }
                    Log.v(TAG, "<=== setStreamingContent() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
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

    @Override // com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase
    public void pauseStreaming(PauseStreamingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    boolean DEBUGSUCCEEDED = false;
                    if (!SRCtrlEnvironment.getInstance().isStreamingPlaybackAPISupported()) {
                        returnCb.handleStatus(StatusCode.NO_SUCH_METHOD.toInt(), "No Such Method");
                    } else if (StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
                        boolean result = StreamingProxy.pauseStreaming();
                        if (result) {
                            returnCb.returnCb();
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ILLEGAL_DATA_FORMAT.toInt(), "Cannot pauseStreaming.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== pauseStreaming() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
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

    @Override // com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase
    public void startStreaming(StartStreamingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    boolean DEBUGSUCCEEDED = false;
                    if (!SRCtrlEnvironment.getInstance().isStreamingPlaybackAPISupported()) {
                        returnCb.handleStatus(StatusCode.NO_SUCH_METHOD.toInt(), "No Such Method");
                    } else if (StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
                        boolean result = StreamingProxy.startStreaming();
                        if (result) {
                            returnCb.returnCb();
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ILLEGAL_DATA_FORMAT.toInt(), "Cannot startStreaming.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== startStreaming() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
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

    @Override // com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase
    public void stopStreaming(StopStreamingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    boolean DEBUGSUCCEEDED = false;
                    if (!SRCtrlEnvironment.getInstance().isStreamingPlaybackAPISupported()) {
                        returnCb.handleStatus(StatusCode.NO_SUCH_METHOD.toInt(), "No Such Method");
                    } else if (StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition()) || StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
                        StreamingLoader.stopObtainingImages();
                        boolean result = StreamingProxy.stopStreaming();
                        if (result) {
                            StateController stateController = StateController.getInstance();
                            stateController.setAppCondition(StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER);
                            returnCb.returnCb();
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ILLEGAL_DATA_FORMAT.toInt(), "Cannot stopStreaming.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== stopStreaming() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
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

    @Override // com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase
    public void requestToNotifyStreamingStatus(Polling polling, RequestToNotifyStreamingStatusCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init(false);
                    boolean DEBUGSUCCEEDED = false;
                    StreamingStatus streamingStatus = null;
                    Log.v(TAG, "---> requestToNotifyStreamingStatus(isPolling=" + polling.polling + LogHelper.MSG_CLOSE_BRACKET);
                    if (SRCtrlEnvironment.getInstance().isStreamingPlaybackAPISupported()) {
                        RequestToNotifyStreamingStatusAdapter adapter = RequestToNotifyStreamingStatusAdapter.getInstance();
                        StreamingStatusEventHandler.Status status = StreamingStatusEventHandler.Status.SUCCESS;
                        if (polling.polling.booleanValue()) {
                            StreamingStatusEventHandler eventHandler = StreamingStatusEventHandler.getInstance();
                            if (!ParamsGenerator.getUpdateStreamingStatus()) {
                                status = eventHandler.startWaiting();
                            }
                        }
                        if (StreamingStatusEventHandler.Status.SUCCESS.equals(status)) {
                            streamingStatus = adapter.getStreamingStatus(polling.polling.booleanValue());
                            if (streamingStatus != null) {
                                returnCb.returnCb(streamingStatus);
                                DEBUGSUCCEEDED = true;
                            } else {
                                returnCb.handleStatus(StatusCode.TIMEOUT.toInt(), "Timed out");
                            }
                        } else if (StreamingStatusEventHandler.Status.CANCELED.equals(status)) {
                            returnCb.handleStatus(StatusCode.ALREADY_RUNNING_POLLING.toInt(), "DoublePolling");
                        } else {
                            returnCb.handleStatus(StatusCode.ANY.toInt(), "Unknown");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.NO_SUCH_METHOD.toInt(), "No Such Method");
                    }
                    Log.v(TAG, "<=== requestToNotifyStreamingStatus(status=" + (streamingStatus != null ? streamingStatus.status : "") + ", factor=" + (streamingStatus != null ? streamingStatus.factor : "") + LogHelper.MSG_CLOSE_BRACKET + "DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
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

    @Override // com.sony.mexi.orb.service.avcontent.v1_0.SmartRemoteControlAvContentServiceBase
    public void seekStreamingPosition(SeekPositionMsec seekPositionMsec, SeekStreamingPositionCallback returnCb) {
        ApiCallLog apiCallLog = null;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    boolean DEBUGSUCCEEDED = false;
                    Log.v(TAG, "---> seekStreamingPosition(seekPositionMsec=" + (seekPositionMsec != null ? seekPositionMsec.positionMsec : "n/a") + LogHelper.MSG_CLOSE_BRACKET);
                    if (!SRCtrlEnvironment.getInstance().isStreamingPlaybackAPISupported()) {
                        returnCb.handleStatus(StatusCode.NO_SUCH_METHOD.toInt(), "No Such Method");
                    } else if (StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
                        boolean result = StreamingProxy.seekStreamingPosition(seekPositionMsec.positionMsec.intValue());
                        if (result) {
                            returnCb.returnCb();
                            DEBUGSUCCEEDED = true;
                        } else {
                            returnCb.handleStatus(StatusCode.ILLEGAL_DATA_FORMAT.toInt(), "Cannot seekStreamingPosition.");
                        }
                    } else {
                        returnCb.handleStatus(StatusCode.ILLEGAL_STATE.toInt(), "Not Available Now");
                    }
                    Log.v(TAG, "<=== seekStreamingPosition() : DEBUGSUCCEEDED=" + DEBUGSUCCEEDED);
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
