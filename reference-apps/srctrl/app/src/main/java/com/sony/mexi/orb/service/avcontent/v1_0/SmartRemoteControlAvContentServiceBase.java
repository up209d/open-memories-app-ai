package com.sony.mexi.orb.service.avcontent.v1_0;

import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.mexi.orb.service.OrbAbstractClient;
import com.sony.mexi.orb.service.OrbAbstractClientCallbacks;
import com.sony.mexi.orb.service.OrbAbstractMethod;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
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
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class SmartRemoteControlAvContentServiceBase extends OrbAbstractVersion {
    private static final String SERVICE_VERSION = "1.0";

    public abstract void getSchemeList(GetSchemeListCallback getSchemeListCallback);

    public abstract void getSourceList(ContentScheme contentScheme, GetSourceListCallback getSourceListCallback);

    public abstract void pauseStreaming(PauseStreamingCallback pauseStreamingCallback);

    public abstract void requestToNotifyStreamingStatus(Polling polling, RequestToNotifyStreamingStatusCallback requestToNotifyStreamingStatusCallback);

    public abstract void seekStreamingPosition(SeekPositionMsec seekPositionMsec, SeekStreamingPositionCallback seekStreamingPositionCallback);

    public abstract void setStreamingContent(StreamingContentInfo streamingContentInfo, SetStreamingContentCallback setStreamingContentCallback);

    public abstract void startStreaming(StartStreamingCallback startStreamingCallback);

    public abstract void stopStreaming(StopStreamingCallback stopStreamingCallback);

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final String getServiceVersion() {
        return SERVICE_VERSION;
    }

    /* loaded from: classes.dex */
    private static class GetSchemeListCallbackImpl extends OrbAbstractClientCallbacks implements GetSchemeListCallback {
        OrbAbstractClient client;

        GetSchemeListCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_0.GetSchemeListCallback
        public void returnCb(ContentScheme[] schemeList) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(schemeList, ContentScheme.Converter.REF));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSourceListCallbackImpl extends OrbAbstractClientCallbacks implements GetSourceListCallback {
        OrbAbstractClient client;

        GetSourceListCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_0.GetSourceListCallback
        public void returnCb(ContentSource[] sourceList) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(sourceList, ContentSource.Converter.REF));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class PauseStreamingCallbackImpl extends OrbAbstractClientCallbacks implements PauseStreamingCallback {
        OrbAbstractClient client;

        PauseStreamingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_0.PauseStreamingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class RequestToNotifyStreamingStatusCallbackImpl extends OrbAbstractClientCallbacks implements RequestToNotifyStreamingStatusCallback {
        OrbAbstractClient client;

        RequestToNotifyStreamingStatusCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_0.RequestToNotifyStreamingStatusCallback
        public void returnCb(StreamingStatus streamingStatus) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, StreamingStatus.Converter.REF.toJson(streamingStatus));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SeekStreamingPositionCallbackImpl extends OrbAbstractClientCallbacks implements SeekStreamingPositionCallback {
        OrbAbstractClient client;

        SeekStreamingPositionCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_0.SeekStreamingPositionCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetStreamingContentCallbackImpl extends OrbAbstractClientCallbacks implements SetStreamingContentCallback {
        OrbAbstractClient client;

        SetStreamingContentCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_0.SetStreamingContentCallback
        public void returnCb(StreamingURL url) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, StreamingURL.Converter.REF.toJson(url));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StartStreamingCallbackImpl extends OrbAbstractClientCallbacks implements StartStreamingCallback {
        OrbAbstractClient client;

        StartStreamingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_0.StartStreamingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StopStreamingCallbackImpl extends OrbAbstractClientCallbacks implements StopStreamingCallback {
        OrbAbstractClient client;

        StopStreamingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_0.StopStreamingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSchemeList extends OrbAbstractMethod {
        private MethodGetSchemeList() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"scheme\":\"string\"}*"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSchemeListCallbackImpl callbacks = new GetSchemeListCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.getSchemeList(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSourceList extends OrbAbstractMethod {
        private MethodGetSourceList() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"source\":\"string\"}*"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"scheme\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                GetSourceListCallbackImpl callbacks = new GetSourceListCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.getSourceList(ContentScheme.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodPauseStreaming extends OrbAbstractMethod {
        private MethodPauseStreaming() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                PauseStreamingCallbackImpl callbacks = new PauseStreamingCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.pauseStreaming(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public String getAccessLevel() {
            return "generic";
        }
    }

    /* loaded from: classes.dex */
    private class MethodRequestToNotifyStreamingStatus extends OrbAbstractMethod {
        private MethodRequestToNotifyStreamingStatus() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"status\":\"string\", \"factor\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"polling\":\"bool\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                RequestToNotifyStreamingStatusCallbackImpl callbacks = new RequestToNotifyStreamingStatusCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.requestToNotifyStreamingStatus(Polling.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSeekStreamingPosition extends OrbAbstractMethod {
        private MethodSeekStreamingPosition() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"positionMsec\":\"int\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SeekStreamingPositionCallbackImpl callbacks = new SeekStreamingPositionCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.seekStreamingPosition(SeekPositionMsec.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public String getAccessLevel() {
            return "generic";
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetStreamingContent extends OrbAbstractMethod {
        private MethodSetStreamingContent() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"playbackUrl\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"uri\":\"string\", \"remotePlayType\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetStreamingContentCallbackImpl callbacks = new SetStreamingContentCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.setStreamingContent(StreamingContentInfo.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public String getAccessLevel() {
            return "generic";
        }
    }

    /* loaded from: classes.dex */
    private class MethodStartStreaming extends OrbAbstractMethod {
        private MethodStartStreaming() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StartStreamingCallbackImpl callbacks = new StartStreamingCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.startStreaming(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public String getAccessLevel() {
            return "generic";
        }
    }

    /* loaded from: classes.dex */
    private class MethodStopStreaming extends OrbAbstractMethod {
        private MethodStopStreaming() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StopStreamingCallbackImpl callbacks = new StopStreamingCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.stopStreaming(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public String getAccessLevel() {
            return "generic";
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final void init(OrbServiceProvider service) {
        addMethod(Name.GET_SCHEME_LIST, new MethodGetSchemeList());
        addMethod(Name.GET_SOURCE_LIST, new MethodGetSourceList());
        addMethod(Name.PAUSE_STREAMING, new MethodPauseStreaming());
        addMethod(Name.REQUEST_TO_NOTIFY_STREAMING_STATUS, new MethodRequestToNotifyStreamingStatus());
        addMethod(Name.SEEK_STREAMING_POSITION, new MethodSeekStreamingPosition());
        addMethod(Name.SET_STREAMING_CONTENT, new MethodSetStreamingContent());
        addMethod(Name.START_STREAMING, new MethodStartStreaming());
        addMethod(Name.STOP_STREAMING, new MethodStopStreaming());
    }
}
