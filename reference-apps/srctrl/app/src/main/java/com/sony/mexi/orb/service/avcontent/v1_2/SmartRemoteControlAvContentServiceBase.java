package com.sony.mexi.orb.service.avcontent.v1_2;

import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.mexi.orb.service.OrbAbstractClient;
import com.sony.mexi.orb.service.OrbAbstractClientCallbacks;
import com.sony.mexi.orb.service.OrbAbstractMethod;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.avcontent.v1_2.GetContentCountCallback;
import com.sony.scalar.webapi.service.avcontent.v1_2.common.struct.ContentCount;
import com.sony.scalar.webapi.service.avcontent.v1_2.common.struct.ContentCountSource;
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class SmartRemoteControlAvContentServiceBase extends OrbAbstractVersion {
    private static final String SERVICE_VERSION = "1.2";

    public abstract void getContentCount(ContentCountSource contentCountSource, GetContentCountCallback getContentCountCallback);

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final String getServiceVersion() {
        return SERVICE_VERSION;
    }

    /* loaded from: classes.dex */
    private static class GetContentCountCallbackImpl extends OrbAbstractClientCallbacks implements GetContentCountCallback {
        OrbAbstractClient client;

        GetContentCountCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_2.GetContentCountCallback
        public void returnCb(ContentCount count) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ContentCount.Converter.REF.toJson(count));
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
    private class MethodGetContentCount extends OrbAbstractMethod {
        private MethodGetContentCount() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"count\":\"int\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"uri\":\"string\", \"type\":\"string*\", \"target\":\"string\", \"view\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                GetContentCountCallbackImpl callbacks = new GetContentCountCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.getContentCount(ContentCountSource.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
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
            return "private";
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final void init(OrbServiceProvider service) {
        addMethod(Name.GET_CONTENT_COUNT, new MethodGetContentCount());
    }
}
