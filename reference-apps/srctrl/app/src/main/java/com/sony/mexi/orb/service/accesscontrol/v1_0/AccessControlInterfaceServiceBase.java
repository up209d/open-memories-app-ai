package com.sony.mexi.orb.service.accesscontrol.v1_0;

import com.sony.mexi.orb.service.OrbAbstractClient;
import com.sony.mexi.orb.service.OrbAbstractClientCallbacks;
import com.sony.mexi.orb.service.OrbAbstractMethod;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.ActEnableMethodsCallback;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.DeveloperInfo;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.DeveloperKey;
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class AccessControlInterfaceServiceBase extends OrbAbstractVersion {
    private static final String SERVICE_VERSION = "1.0";

    public abstract void actEnableMethods(DeveloperInfo developerInfo, ActEnableMethodsCallback actEnableMethodsCallback);

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final String getServiceVersion() {
        return SERVICE_VERSION;
    }

    /* loaded from: classes.dex */
    private static class ActEnableMethodsCallbackImpl extends OrbAbstractClientCallbacks implements ActEnableMethodsCallback {
        OrbAbstractClient client;

        ActEnableMethodsCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.accesscontrol.v1_0.ActEnableMethodsCallback
        public void returnCb(DeveloperKey developerKey) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, DeveloperKey.Converter.REF.toJson(developerKey));
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
    private class MethodActEnableMethods extends OrbAbstractMethod {
        private MethodActEnableMethods() {
        }

        /* synthetic */ MethodActEnableMethods(AccessControlInterfaceServiceBase accessControlInterfaceServiceBase, MethodActEnableMethods methodActEnableMethods) {
            this();
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"dg\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"developerName\":\"string\", \"developerID\":\"string\", \"sg\":\"string\", \"methods\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                ActEnableMethodsCallbackImpl callbacks = new ActEnableMethodsCallbackImpl(client);
                try {
                    AccessControlInterfaceServiceBase.this.actEnableMethods(DeveloperInfo.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final void init(OrbServiceProvider service) {
        addMethod("actEnableMethods", new MethodActEnableMethods(this, null));
    }
}
