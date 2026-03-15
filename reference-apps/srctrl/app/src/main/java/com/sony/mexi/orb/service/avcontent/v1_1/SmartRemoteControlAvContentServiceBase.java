package com.sony.mexi.orb.service.avcontent.v1_1;

import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.mexi.orb.service.OrbAbstractClient;
import com.sony.mexi.orb.service.OrbAbstractClientCallbacks;
import com.sony.mexi.orb.service.OrbAbstractMethod;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.avcontent.v1_1.DeleteContentCallback;
import com.sony.scalar.webapi.service.avcontent.v1_1.common.struct.ContentURIs;
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class SmartRemoteControlAvContentServiceBase extends OrbAbstractVersion {
    private static final String SERVICE_VERSION = "1.1";

    public abstract void deleteContent(ContentURIs contentURIs, DeleteContentCallback deleteContentCallback);

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final String getServiceVersion() {
        return SERVICE_VERSION;
    }

    /* loaded from: classes.dex */
    private static class DeleteContentCallbackImpl extends OrbAbstractClientCallbacks implements DeleteContentCallback {
        OrbAbstractClient client;

        DeleteContentCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.avcontent.v1_1.DeleteContentCallback
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
    private class MethodDeleteContent extends OrbAbstractMethod {
        private MethodDeleteContent() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"uri\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                DeleteContentCallbackImpl callbacks = new DeleteContentCallbackImpl(client);
                try {
                    SmartRemoteControlAvContentServiceBase.this.deleteContent(ContentURIs.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
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
        addMethod(Name.DELETE_CONTENT, new MethodDeleteContent());
    }
}
