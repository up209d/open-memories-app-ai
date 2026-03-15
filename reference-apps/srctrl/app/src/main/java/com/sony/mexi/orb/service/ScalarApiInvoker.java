package com.sony.mexi.orb.service;

import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.imaging.app.srctrl.webapi.definition.ResponseType;
import com.sony.mexi.orb.server.DebugLogger;
import com.sony.mexi.orb.server.OrbServiceGroup;
import com.sony.mexi.orb.server.OrbServiceInformation;
import com.sony.mexi.orb.server.PathValidator;
import com.sony.mexi.orb.server.SupportedServiceGpHolder;
import com.sony.mexi.orb.service.Authenticator;
import com.sony.mexi.orb.service.http.HttpEndPoint;
import com.sony.mexi.orb.service.websocket.WebSocketEndPoint;
import com.sony.mexi.webapi.ApiIdentity;
import com.sony.mexi.webapi.NotificationStatusRequest;
import com.sony.mexi.webapi.NotificationStatusResponse;
import com.sony.mexi.webapi.Protocol;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.WebSocketCloseCode;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiAttribute;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiInfo;
import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationAttribute;
import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationInfo;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ServiceInfo;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.mexi.webapi.mandatory.v1_0.MethodTypeHandler;
import com.sony.mexi.webapi.mandatory.v1_0.NotificationStatusHandler;
import com.sony.mexi.webapi.mandatory.v1_0.VersionHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ScalarApiInvoker {
    private static final String MSG_ILLEGAL_JSON = "Illegal JSON";
    private static final String MSG_JSON_FORMAT_ERROR = "JSON Format Error";
    private final OrbServiceProvider service;
    private static final String TAG = ScalarApiInvoker.class.getSimpleName();
    private static final List<ApiInfo> MANDATORY_METHODS = Collections.unmodifiableList(new ArrayList<ApiInfo>() { // from class: com.sony.mexi.orb.service.ScalarApiInvoker.1
        {
            ApiAttribute attribute = new ApiAttribute();
            attribute.version = "1.0";
            ApiInfo apiMethodTypes = new ApiInfo();
            apiMethodTypes.name = Name.GET_METHOD_TYPES;
            apiMethodTypes.versions = new ApiAttribute[]{attribute};
            add(apiMethodTypes);
            ApiInfo apiVersions = new ApiInfo();
            apiVersions.name = Name.GET_VERSIONS;
            apiVersions.versions = new ApiAttribute[]{attribute};
            add(apiVersions);
        }
    });
    private static final List<ApiInfo> SWITCHNOTIFICATIONS_METHODS = Collections.unmodifiableList(new ArrayList<ApiInfo>() { // from class: com.sony.mexi.orb.service.ScalarApiInvoker.2
        {
            ApiAttribute attribute = new ApiAttribute();
            attribute.version = "1.0";
            attribute.protocols = new String[]{"websocket:jsonizer"};
            ApiInfo apiSwitchNotifications = new ApiInfo();
            apiSwitchNotifications.name = "switchNotifications";
            apiSwitchNotifications.versions = new ApiAttribute[]{attribute};
            add(apiSwitchNotifications);
        }
    });
    private transient JSONArray infoJSONArray = null;
    private final TreeMap<String, OrbAbstractVersion> versionMap = new TreeMap<>(new OrbVersionComparator());

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum NotificationStatus {
        ALLOWED,
        INSUFFICIENT_AUTH_LEVEL,
        UNSUPPORTED
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum SwitchOperation {
        ENABLED,
        DISABLED
    }

    public ScalarApiInvoker(OrbServiceProvider service) {
        this.service = service;
    }

    public void sendRequestFailed(OrbAbstractClient client, MethodCallInfoCollector collector, Status status) {
        client.sendError(status);
        collector.onRequestFailed(status);
    }

    public void sendRequestFailed(OrbAbstractClient client, MethodCallInfoCollector collector, Status status, String message) {
        if (Status.ILLEGAL_REQUEST.equals(status)) {
            if (client instanceof WebSocketEndPoint) {
                ((WebSocketEndPoint) client).close(WebSocketCloseCode.POLICY_VIOLATION.toInt(), WebSocketCloseCode.POLICY_VIOLATION.toMessage());
            } else {
                client.setId(-1L);
                client.sendError(status.toInt(), message);
            }
        } else {
            client.sendError(status.toInt(), message);
        }
        collector.onRequestFailed(status, message);
    }

    public void sendRequestSuccess(MethodCallInfoCollector collector) {
        collector.onRequestSuccess();
    }

    private void invokeMethod(OrbAbstractClient client, String name, JSONArray params, String versionVal, MethodCallInfoCollector collector) throws IOException {
        String versionStr = versionVal == null ? this.versionMap.firstKey() : versionVal;
        OrbAbstractVersion version = this.versionMap.get(versionStr);
        if (version == null) {
            sendRequestFailed(client, collector, Status.UNSUPPORTED_VERSION, versionStr);
            return;
        }
        OrbAbstractMethod method = version.getMethod(name);
        if (method == null) {
            sendRequestFailed(client, collector, Status.NO_SUCH_METHOD, name);
            return;
        }
        String accessLevel = method.getAccessLevel();
        Authenticator authenticator = this.service.getAuthenticator();
        if (!accessLevel.equals("none") && authenticator != null) {
            Authenticator.AuthResult authRet = authenticator.checkAuthentication(accessLevel, client);
            collector.setAuthInfo(authRet);
            if (!authRet.isAuthorized()) {
                sendRequestFailed(client, collector, Status.FORBIDDEN);
                return;
            }
        }
        method.invoke(client, params);
        sendRequestSuccess(collector);
    }

    public void addVersion(OrbAbstractVersion version) {
        addVersion(version.getServiceVersion(), version);
    }

    public void addVersion(String name, OrbAbstractVersion version) {
        if (name.compareTo(version.getServiceVersion()) != 0 || this.versionMap.get(name) != null) {
            throw new RuntimeException();
        }
        this.versionMap.put(name, version);
        version.setOrbAuthenticator(this.service.getAuthenticator());
        version.init(this.service);
    }

    public void setAuthenticator(Authenticator authenticator) {
        for (Map.Entry<String, OrbAbstractVersion> version : this.versionMap.entrySet()) {
            version.getValue().setOrbAuthenticator(authenticator);
        }
    }

    public void onWebApiRequest(OrbAbstractClient client, String body, MethodCallInfoCollector collector) throws IOException {
        DebugLogger.debug(TAG, body);
        if (body.length() == 0) {
            sendJsonFormatError(client, collector);
            return;
        }
        try {
            JSONObject bodyVal = new JSONObject(body);
            Authenticator auth = this.service.getAuthenticator();
            if (auth != null) {
                client.setAuthErrorHandler(auth.getAuthErrorHandler());
            }
            String requestURI = client.getRequestURI();
            int id = JsonUtil.getInt(bodyVal, "id", -1);
            if (id <= 0) {
                sendRequestFailed(client, collector, Status.ILLEGAL_REQUEST, MSG_ILLEGAL_JSON);
                return;
            }
            client.setId(id);
            String name = JsonUtil.getString(bodyVal, "method", null);
            String version = JsonUtil.getString(bodyVal, "version", null);
            collector.setMethodInfo(name, version);
            JSONArray params = JsonUtil.getArray(bodyVal, "params", null);
            if (params == null) {
                sendRequestFailed(client, collector, Status.ILLEGAL_REQUEST, MSG_ILLEGAL_JSON);
                return;
            }
            if (name == null) {
                sendRequestFailed(client, collector, Status.ILLEGAL_REQUEST, MSG_ILLEGAL_JSON);
                return;
            }
            if (!this.service.authorize(name, client)) {
                sendRequestFailed(client, collector, Status.FORBIDDEN);
                return;
            }
            if (name.equals(Name.GET_VERSIONS)) {
                invokeGetVersions(client, params, collector);
                return;
            }
            if (name.equals(Name.GET_METHOD_TYPES)) {
                invokeGetMethodTypes(client, params, collector);
                return;
            }
            if (name.equals("switchNotifications")) {
                invokeSwitchNotifications(client, params, collector);
                return;
            }
            if (version == null) {
                sendRequestFailed(client, collector, Status.ILLEGAL_REQUEST, MSG_ILLEGAL_JSON);
                return;
            }
            if (requestURI == null) {
                sendRequestFailed(client, collector, Status.BAD_REQUEST);
                return;
            }
            if (isGuide(requestURI)) {
                Set<OrbServiceGroup> serviceGp = SupportedServiceGpHolder.getInstance().getServiceGps();
                if (serviceGp.size() == 0) {
                    sendRequestFailed(client, collector, Status.UNSUPPORTED_OPERATION);
                    return;
                }
                if (this.infoJSONArray == null) {
                    ServiceInfo[] infoList = collectProcess(serviceGp);
                    this.infoJSONArray = JsonUtil.toJsonArray(infoList, ServiceInfo.Converter.REF);
                }
                JsonUtil.put(params, this.infoJSONArray);
            }
            invokeMethod(client, name, params, version, collector);
        } catch (JsonArgumentException e) {
            sendRequestFailed(client, collector, Status.ILLEGAL_REQUEST, MSG_ILLEGAL_JSON);
        } catch (JSONException e2) {
            sendJsonFormatError(client, collector);
        }
    }

    private void sendJsonFormatError(OrbAbstractClient client, MethodCallInfoCollector collector) {
        if (client instanceof WebSocketEndPoint) {
            ((WebSocketEndPoint) client).close(WebSocketCloseCode.POLICY_VIOLATION.toInt(), MSG_JSON_FORMAT_ERROR);
        } else if (client instanceof HttpEndPoint) {
            ((HttpEndPoint) client).sendErrorRaw(Status.BAD_REQUEST.toInt(), MSG_JSON_FORMAT_ERROR);
        }
        collector.onRequestFailed(Status.BAD_REQUEST, MSG_JSON_FORMAT_ERROR);
    }

    public boolean isGuide(String requestURI) {
        String uri = "";
        try {
            uri = PathValidator.toNormalizedPath(requestURI);
        } catch (URISyntaxException e) {
        }
        return uri.endsWith("/guide");
    }

    private ServiceInfo[] collectProcess(Set<OrbServiceGroup> serviceGps) {
        Map<String, ServiceInfo> infoTree = new TreeMap<>();
        for (OrbServiceGroup serviceGp : serviceGps) {
            serviceGp.syncInitServiceInformation();
            Map<String, OrbServiceInformation[]> targetMap = serviceGp.getServiceInformationMap();
            if (targetMap != null) {
                addInformationMap(infoTree, targetMap);
            }
        }
        addMandatoryMethods(infoTree);
        return (ServiceInfo[]) infoTree.values().toArray(new ServiceInfo[infoTree.size()]);
    }

    private void addInformationMap(Map<String, ServiceInfo> intoMap, Map<String, OrbServiceInformation[]> targetMap) {
        for (Map.Entry<String, OrbServiceInformation[]> entry : targetMap.entrySet()) {
            String relativeUrl = entry.getKey();
            ServiceInfo info = intoMap.get(relativeUrl);
            if (info == null) {
                info = new ServiceInfo();
                intoMap.put(relativeUrl, info);
                info.service = relativeUrl;
            }
            addVersion(info, entry.getValue());
        }
    }

    private void addVersion(ServiceInfo info, OrbServiceInformation[] targetList) {
        List<String> protocols = new ArrayList<>();
        Map<String, ApiInfo> apis = new HashMap<>();
        Map<String, NotificationInfo> notifications = new HashMap<>();
        into(protocols, info.protocols);
        into(apis, info.apis);
        into(notifications, info.notifications);
        for (OrbServiceInformation target : targetList) {
            addInformation(protocols, target);
            addApiInfo(apis, target);
            addNotifications(notifications, target);
        }
        info.protocols = (String[]) protocols.toArray(new String[protocols.size()]);
        info.apis = (ApiInfo[]) apis.values().toArray(new ApiInfo[apis.size()]);
        info.notifications = (NotificationInfo[]) notifications.values().toArray(new NotificationInfo[notifications.size()]);
    }

    private void into(List<String> list, String[] arrays) {
        if (arrays != null) {
            List<String> buf = Arrays.asList(arrays);
            list.addAll(buf);
        }
    }

    private void into(Map<String, ApiInfo> map, ApiInfo[] arrays) {
        if (arrays != null) {
            for (ApiInfo buf : arrays) {
                map.put(buf.name, buf);
            }
        }
    }

    private void into(Map<String, NotificationInfo> map, NotificationInfo[] arrays) {
        if (arrays != null) {
            for (NotificationInfo buf : arrays) {
                map.put(buf.name, buf);
            }
        }
    }

    private void addInformation(List<String> info, OrbServiceInformation target) {
        for (String protocol : ((OrbServiceInformationBase) target).getProtocols()) {
            if (!info.contains(protocol)) {
                info.add(protocol);
            }
        }
        Collections.sort(info);
    }

    private void addApiInfo(Map<String, ApiInfo> info, OrbServiceInformation target) {
        for (ApiInfo method : ((OrbServiceInformationBase) target).getMethods()) {
            if (!info.containsKey(method.name)) {
                info.put(method.name, method);
            } else {
                addAttribute(info.get(method.name), method);
            }
        }
    }

    private void addNotifications(Map<String, NotificationInfo> info, OrbServiceInformation target) {
        for (NotificationInfo notification : ((OrbServiceInformationBase) target).getNotifications()) {
            if (!info.containsKey(notification.name)) {
                info.put(notification.name, notification);
            } else {
                addNAttribute(info.get(notification.name), notification);
            }
        }
    }

    private void addAttribute(ApiInfo info, ApiInfo target) {
        Map<String, ApiAttribute> map = new HashMap<>();
        ApiAttribute[] arr$ = info.versions;
        for (ApiAttribute infoAttribute : arr$) {
            map.put(infoAttribute.version, infoAttribute);
        }
        ApiAttribute[] arr$2 = target.versions;
        for (ApiAttribute targetAttribute : arr$2) {
            if (!map.containsKey(targetAttribute.version)) {
                map.put(targetAttribute.version, targetAttribute);
            } else {
                List<String> list = new ArrayList<>();
                String[] arr$3 = map.get(targetAttribute.version).protocols;
                for (String protocol : arr$3) {
                    list.add(protocol);
                }
                String[] arr$4 = targetAttribute.protocols;
                for (String protocol2 : arr$4) {
                    list.add(protocol2);
                }
                Collections.sort(list);
                map.get(targetAttribute.version).protocols = (String[]) list.toArray(new String[list.size()]);
            }
        }
        info.versions = (ApiAttribute[]) map.values().toArray(new ApiAttribute[map.size()]);
    }

    private void addNAttribute(NotificationInfo info, NotificationInfo target) {
        Map<String, NotificationAttribute> map = new HashMap<>();
        NotificationAttribute[] arr$ = info.versions;
        for (NotificationAttribute infoAttribute : arr$) {
            map.put(infoAttribute.version, infoAttribute);
        }
        NotificationAttribute[] arr$2 = target.versions;
        for (NotificationAttribute targetAttribute : arr$2) {
            if (!map.containsKey(targetAttribute.version)) {
                map.put(targetAttribute.version, targetAttribute);
            }
        }
        info.versions = (NotificationAttribute[]) map.values().toArray(new NotificationAttribute[map.size()]);
    }

    private void addMandatoryMethods(Map<String, ServiceInfo> intoMap) {
        for (ServiceInfo info : intoMap.values()) {
            List<ApiInfo> mandatoryMethods = new ArrayList<>(MANDATORY_METHODS);
            String[] arr$ = info.protocols;
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ < len$) {
                    String protocol = arr$[i$];
                    if (!protocol.equals("websocket:jsonizer")) {
                        i$++;
                    } else {
                        mandatoryMethods.addAll(SWITCHNOTIFICATIONS_METHODS);
                        break;
                    }
                }
            }
            List<ApiInfo> versionsMethods = Arrays.asList(info.apis);
            mandatoryMethods.addAll(versionsMethods);
            info.apis = (ApiInfo[]) mandatoryMethods.toArray(new ApiInfo[mandatoryMethods.size()]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VersionHandlerImpl extends OrbAbstractClientCallbacks implements VersionHandler {
        OrbAbstractClient client;

        VersionHandlerImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.mexi.webapi.mandatory.v1_0.VersionHandler
        public void handleVersions(String[] versions) {
            JSONArray orbResponse = new JSONArray();
            orbResponse.put(JsonUtil.toJsArray(versions));
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

    private void invokeGetVersions(OrbAbstractClient client, JSONArray params, MethodCallInfoCollector collector) {
        if (params.length() == 0) {
            VersionHandlerImpl callbacks = new VersionHandlerImpl(client);
            callbacks.handleVersions(getVersions());
            sendRequestSuccess(collector);
            return;
        }
        sendRequestFailed(client, collector, Status.ILLEGAL_ARGUMENT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MethodTypeHandlerImpl extends OrbAbstractClientCallbacks implements MethodTypeHandler {
        OrbAbstractClient client;

        MethodTypeHandlerImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.mexi.webapi.mandatory.v1_0.MethodTypeHandler
        public void handleMethodType(String methodName, String[] parameterTypes, String[] resultTypes, String version) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, methodName);
            JsonUtil.put(orbResponse, JsonUtil.toJsArray(parameterTypes));
            JsonUtil.put(orbResponse, JsonUtil.toJsArray(resultTypes));
            JsonUtil.put(orbResponse, version);
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

    private void invokeGetMethodTypes(OrbAbstractClient client, JSONArray params, MethodCallInfoCollector collector) throws IOException {
        if (params.length() == 1) {
            try {
                String version = JsonUtil.getString(params, 0);
                MethodTypeHandlerImpl callbacks = new MethodTypeHandlerImpl(client);
                if (version == null || version.equals("")) {
                    String[] keys = getVersions();
                    for (String version2 : keys) {
                        OrbAbstractVersion target = this.versionMap.get(version2);
                        if (target != null) {
                            target.invokeGetMethodTypes(callbacks, version2);
                        }
                        if (version2.equals("1.0")) {
                            Protocol protocol = client instanceof WebSocketEndPoint ? Protocol.WEBSOCKET : Protocol.HTTP_POST;
                            handleMandatoryMethods(callbacks, protocol);
                        }
                    }
                } else if (version.equals("1.0")) {
                    OrbAbstractVersion target2 = this.versionMap.get(version);
                    if (target2 != null) {
                        target2.invokeGetMethodTypes(callbacks, version);
                    }
                    Protocol protocol2 = client instanceof WebSocketEndPoint ? Protocol.WEBSOCKET : Protocol.HTTP_POST;
                    handleMandatoryMethods(callbacks, protocol2);
                } else {
                    OrbAbstractVersion target3 = this.versionMap.get(version);
                    if (target3 == null) {
                        sendRequestFailed(client, collector, Status.UNSUPPORTED_VERSION, version);
                        return;
                    }
                    target3.invokeGetMethodTypes(callbacks, version);
                }
                client.flushResults();
                sendRequestSuccess(collector);
                return;
            } catch (JsonArgumentException e) {
                sendRequestFailed(client, collector, Status.ILLEGAL_ARGUMENT);
                return;
            }
        }
        sendRequestFailed(client, collector, Status.ILLEGAL_ARGUMENT);
    }

    public final OrbAbstractMethod inherit(String name, OrbAbstractMethod parameter) {
        OrbAbstractMethod method = null;
        Iterator<OrbAbstractVersion> itr = this.versionMap.values().iterator();
        while (itr.hasNext() && method == null) {
            OrbAbstractVersion version = itr.next();
            method = version.getMethodBySignature(name, parameter);
        }
        if (method == null) {
            throw new IllegalArgumentException("not found super method");
        }
        return method;
    }

    public final OrbServiceInformationBase[] getServiceInformation() {
        OrbServiceInformationBase[] info = new OrbServiceInformationBase[this.versionMap.size()];
        String[] versions = (String[]) this.versionMap.keySet().toArray(new String[this.versionMap.size()]);
        for (int i = 0; i < versions.length; i++) {
            info[i] = this.versionMap.get(versions[i]).getServiceInformation();
        }
        return info;
    }

    public int handleMandatoryMethods(MethodTypeHandler handler, Protocol protocol) {
        String[] getMethodTypesParamTypes = {ResponseType.STRING};
        String[] getMethodTypesResultTypes = {ResponseType.STRING, ResponseType.STRING_ARRAY, ResponseType.STRING_ARRAY, ResponseType.STRING};
        handler.handleMethodType(Name.GET_METHOD_TYPES, getMethodTypesParamTypes, getMethodTypesResultTypes, "1.0");
        String[] getVersionsParamTypes = new String[0];
        String[] getVersionsResultTypes = {ResponseType.STRING_ARRAY};
        handler.handleMethodType(Name.GET_VERSIONS, getVersionsParamTypes, getVersionsResultTypes, "1.0");
        if (protocol == Protocol.WEBSOCKET) {
            String[] switchNotificationsParamTypes = {"NotificationStatusRequest"};
            String[] switchNotificationsResultTypes = {"NotificationStatusResponse"};
            handler.handleMethodType("switchNotifications", switchNotificationsParamTypes, switchNotificationsResultTypes, "1.0");
        }
        return 0;
    }

    public final String[] getVersions() {
        Set<String> versionSet = new HashSet<>();
        versionSet.addAll(this.versionMap.keySet());
        if (!versionSet.contains("1.0")) {
            versionSet.add("1.0");
        }
        String[] versions = (String[]) versionSet.toArray(new String[versionSet.size()]);
        Arrays.sort(versions);
        return versions;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NotificationStatusHandlerImpl extends OrbAbstractClientCallbacks implements NotificationStatusHandler {
        OrbAbstractClient client;

        NotificationStatusHandlerImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.mexi.webapi.mandatory.v1_0.NotificationStatusHandler
        public void handleNotificationStatus(NotificationStatusResponse res) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, NotificationStatusResponse.Converter.REF.toJson(res));
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

    private void invokeSwitchNotifications(OrbAbstractClient client, JSONArray params, MethodCallInfoCollector collector) throws IOException {
        if (!(client instanceof WebSocketEndPoint)) {
            sendRequestFailed(client, collector, Status.NO_SUCH_METHOD, "switchNotifications");
            return;
        }
        if (params.length() == 1) {
            try {
                NotificationStatusRequest request = NotificationStatusRequest.Converter.REF.fromJson(JsonUtil.getObject(params, 0));
                NotificationStatusResponse response = new NotificationStatusResponse();
                List<ApiIdentity> resEnabled = new ArrayList<>();
                List<ApiIdentity> resDisabled = new ArrayList<>();
                Map<String, ApiIdentity> resRejected = new HashMap<>();
                Map<String, ApiIdentity> resUnsupported = new HashMap<>();
                NotificationStatusHandlerImpl callbacks = new NotificationStatusHandlerImpl(client);
                if (request == null) {
                    request = new NotificationStatusRequest();
                }
                if (request.enabled != null) {
                    if (request.enabled.length == 0) {
                        switchNotificationsAll(SwitchOperation.ENABLED, resRejected, client);
                    } else {
                        for (ApiIdentity apiIdentity : request.enabled) {
                            switchNotifications(SwitchOperation.ENABLED, apiIdentity, resRejected, resUnsupported, client);
                        }
                    }
                }
                if (request.disabled != null) {
                    if (request.disabled.length == 0) {
                        switchNotificationsAll(SwitchOperation.DISABLED, resRejected, client);
                    } else {
                        ApiIdentity[] arr$ = request.disabled;
                        for (ApiIdentity apiIdentity2 : arr$) {
                            switchNotifications(SwitchOperation.DISABLED, apiIdentity2, resRejected, resUnsupported, client);
                        }
                    }
                }
                String[] keys = getVersions();
                for (String version : keys) {
                    OrbAbstractVersion target = this.versionMap.get(version);
                    if (target != null) {
                        for (OrbAbstractNotification notification : target.getNotifications()) {
                            ApiIdentity identity = new ApiIdentity();
                            identity.name = notification.getName();
                            identity.version = notification.getVersion();
                            if (notification.isEnabled()) {
                                resEnabled.add(identity);
                            } else {
                                resDisabled.add(identity);
                            }
                        }
                    }
                }
                response.enabled = (ApiIdentity[]) resEnabled.toArray(new ApiIdentity[resEnabled.size()]);
                response.disabled = (ApiIdentity[]) resDisabled.toArray(new ApiIdentity[resDisabled.size()]);
                response.rejected = null;
                if (!resRejected.isEmpty()) {
                    response.rejected = (ApiIdentity[]) resRejected.values().toArray(new ApiIdentity[resRejected.size()]);
                }
                response.unsupported = null;
                if (!resUnsupported.isEmpty()) {
                    response.unsupported = (ApiIdentity[]) resUnsupported.values().toArray(new ApiIdentity[resUnsupported.size()]);
                }
                callbacks.handleNotificationStatus(response);
                sendRequestSuccess(collector);
                return;
            } catch (JsonArgumentException e) {
                sendRequestFailed(client, collector, Status.ILLEGAL_ARGUMENT);
                return;
            }
        }
        sendRequestFailed(client, collector, Status.ILLEGAL_ARGUMENT);
    }

    private void switchNotificationsAll(SwitchOperation operation, Map<String, ApiIdentity> rejected, OrbAbstractClient client) {
        String[] keys = getVersions();
        for (String version : keys) {
            OrbAbstractVersion target = this.versionMap.get(version);
            if (target != null) {
                Collection<OrbAbstractNotification> notificationtList = target.getNotifications();
                for (OrbAbstractNotification notification : notificationtList) {
                    ApiIdentity identity = new ApiIdentity();
                    identity.name = notification.getName();
                    identity.version = notification.getVersion();
                    NotificationStatus status = getNotificationCapability(notification, identity, client);
                    if (checkNotificationStatus(status, identity, rejected, null)) {
                        setNotificationClient(operation, notification, client);
                    }
                }
            }
        }
    }

    private void switchNotifications(SwitchOperation operation, ApiIdentity identity, Map<String, ApiIdentity> rejected, Map<String, ApiIdentity> unsupported, OrbAbstractClient client) {
        OrbAbstractVersion target = this.versionMap.get(identity.version);
        if (target == null) {
            checkNotificationStatus(NotificationStatus.UNSUPPORTED, identity, rejected, unsupported);
            return;
        }
        OrbAbstractNotification notification = target.getNotification(identity.name);
        NotificationStatus status = getNotificationCapability(notification, identity, client);
        if (checkNotificationStatus(status, identity, rejected, unsupported)) {
            setNotificationClient(operation, notification, client);
        }
    }

    private NotificationStatus getNotificationCapability(OrbAbstractNotification notification, ApiIdentity identity, OrbAbstractClient client) {
        if (notification == null) {
            return NotificationStatus.UNSUPPORTED;
        }
        if (!notification.getVersion().equals(identity.version)) {
            return NotificationStatus.UNSUPPORTED;
        }
        String accessLevel = notification.getAuthLevel();
        Authenticator authenticator = this.service.getAuthenticator();
        if (!accessLevel.equals("none") && authenticator != null) {
            Authenticator.AuthResult authRet = authenticator.checkAuthentication(accessLevel, client);
            if (!authRet.isAuthorized()) {
                return NotificationStatus.INSUFFICIENT_AUTH_LEVEL;
            }
        }
        return NotificationStatus.ALLOWED;
    }

    private boolean checkNotificationStatus(NotificationStatus status, ApiIdentity identity, Map<String, ApiIdentity> rejected, Map<String, ApiIdentity> unsupported) {
        String key = identity.name + ":" + identity.version;
        switch (status) {
            case ALLOWED:
                return true;
            case INSUFFICIENT_AUTH_LEVEL:
                rejected.put(key, identity);
                return false;
            case UNSUPPORTED:
                unsupported.put(key, identity);
                return false;
            default:
                throw new UnsupportedOperationException(status.toString() + " is not supported.");
        }
    }

    private void setNotificationClient(SwitchOperation operation, OrbAbstractNotification notification, OrbAbstractClient client) {
        switch (operation) {
            case ENABLED:
                notification.addClient(client);
                return;
            case DISABLED:
                notification.removeClient(client);
                return;
            default:
                throw new UnsupportedOperationException(operation.toString() + " is not supported.");
        }
    }
}
