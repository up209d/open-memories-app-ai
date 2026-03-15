package com.sony.mexi.orb.service.guide.v1_0;

import com.sony.imaging.app.srctrl.webapi.definition.ResponseType;
import com.sony.mexi.orb.service.OrbAbstractClient;
import com.sony.mexi.orb.service.OrbAbstractClientCallbacks;
import com.sony.mexi.orb.service.OrbAbstractMethod;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.guide.v1_0.ApiInfoHandler;
import com.sony.mexi.webapi.guide.v1_0.ProtocolHandler;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiInfoRequest;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceInfo;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceProtocol;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
import java.util.List;
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class GuideServiceBase extends OrbAbstractVersion {
    private static final String SERVICE_VERSION = "1.0";

    public abstract int getServiceProtocols(ServiceProtocol[] serviceProtocolArr, ProtocolHandler protocolHandler);

    public abstract int getSupportedApiInfo(ServiceInfo[] serviceInfoArr, ApiInfoRequest apiInfoRequest, ApiInfoHandler apiInfoHandler);

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final String getServiceVersion() {
        return SERVICE_VERSION;
    }

    /* loaded from: classes.dex */
    private static class ApiInfoHandlerImpl extends OrbAbstractClientCallbacks implements ApiInfoHandler {
        OrbAbstractClient client;

        ApiInfoHandlerImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.mexi.webapi.guide.v1_0.ApiInfoHandler
        public void handleApiInfo(ServiceInfo[] service) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(service, ServiceInfoCustomConverter.REF));
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
    static class ProtocolHandlerImpl extends OrbAbstractClientCallbacks implements ProtocolHandler {
        OrbAbstractClient client;

        ProtocolHandlerImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.mexi.webapi.guide.v1_0.ProtocolHandler
        public void handleProtocols(String serviceName, String[] protocols) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, serviceName);
            JsonUtil.put(orbResponse, protocols);
            this.client.appendResults(orbResponse.toString());
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
            this.client.appendResults(result);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetServiceProtocols extends OrbAbstractMethod {
        private MethodGetServiceProtocols() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                ProtocolHandlerImpl callbacks = new ProtocolHandlerImpl(client);
                try {
                    List<ServiceProtocol> list = ServiceProtocolCustomConverter.REF.fromInfoJsonArray(JsonUtil.getArray(params, 0));
                    GuideServiceBase.this.getServiceProtocols((ServiceProtocol[]) list.toArray(new ServiceProtocol[list.size()]), callbacks);
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
    private class MethodGetSupportedApiInfo extends OrbAbstractMethod {
        private MethodGetSupportedApiInfo() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"service\":\"string\", \"protocols\":\"string*\", \"apis\":\"ApiInfo[]\"}*"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"services\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 2) {
                ApiInfoHandlerImpl callbacks = new ApiInfoHandlerImpl(client);
                try {
                    List<ServiceInfo> tmpList = JsonUtil.fromJsonArray(JsonUtil.getArray(params, 1), ServiceInfo.Converter.REF);
                    ServiceInfo[] serviceInfoArray = tmpList == null ? null : (ServiceInfo[]) tmpList.toArray(new ServiceInfo[tmpList.size()]);
                    GuideServiceBase.this.getSupportedApiInfo(serviceInfoArray, ApiInfoRequest.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
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
        addMethod("getServiceProtocols", new MethodGetServiceProtocols());
        addMethod("getSupportedApiInfo", new MethodGetSupportedApiInfo());
    }
}
