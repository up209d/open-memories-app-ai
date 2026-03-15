package com.sony.mexi.orb.service;

import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiAttribute;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiInfo;
import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationAttribute;
import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationInfo;
import com.sony.mexi.webapi.mandatory.v1_0.MethodTypeHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public abstract class OrbAbstractVersion {
    private Authenticator mAuthenticator;
    private final Map<String, OrbAbstractMethod> mMethodMap = new TreeMap();
    private final Map<String, OrbAbstractNotification> mNotificationMap = new TreeMap();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getServiceVersion();

    public void init(OrbServiceProvider service) {
        throw new RuntimeException("Not Applicable");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void addMethod(String name, OrbAbstractMethod method) {
        this.mMethodMap.put(name, method);
    }

    public OrbAbstractMethod getMethod(String name) {
        return this.mMethodMap.get(name);
    }

    public OrbAbstractMethod getMethodBySignature(String name, OrbAbstractMethod parameter) {
        OrbAbstractMethod method = this.mMethodMap.get(name);
        if (method != null) {
            if (!Arrays.equals(method.resultTypes(), parameter.resultTypes()) || !Arrays.equals(method.parameterTypes(), parameter.parameterTypes())) {
                return null;
            }
            return method;
        }
        return method;
    }

    protected void addNotification(String enableAPIName, OrbAbstractNotification notification) {
        this.mNotificationMap.put(enableAPIName, notification);
    }

    public OrbAbstractNotification getNotification(String name) {
        return this.mNotificationMap.get(name);
    }

    public Collection<OrbAbstractNotification> getNotifications() {
        return this.mNotificationMap.values();
    }

    public void invokeGetMethodTypes(MethodTypeHandler callbacks, String version) {
        getMethodTypes(version, callbacks);
    }

    private NotificationAttribute getNotificationAttribute(OrbAbstractNotification notification) {
        NotificationAttribute attribute = new NotificationAttribute();
        attribute.version = notification.getVersion();
        attribute.authLevel = notification.getAuthLevel();
        return attribute;
    }

    public Collection<NotificationInfo> getNotificationInfo() {
        List<NotificationInfo> notificationInfolist = new ArrayList<>();
        for (OrbAbstractNotification notificationEntry : this.mNotificationMap.values()) {
            NotificationInfo notificationInfo = new NotificationInfo();
            notificationInfo.name = notificationEntry.getName();
            notificationInfo.versions = new NotificationAttribute[]{getNotificationAttribute(notificationEntry)};
            notificationInfolist.add(notificationInfo);
        }
        return notificationInfolist;
    }

    public void getMethodTypes(String version, MethodTypeHandler handler) {
        for (Map.Entry<String, OrbAbstractMethod> methodEntry : this.mMethodMap.entrySet()) {
            String name = methodEntry.getKey();
            OrbAbstractMethod method = methodEntry.getValue();
            handler.handleMethodType(name, method.parameterTypes(), method.resultTypes(), version);
        }
    }

    private ApiAttribute getApiAttribute(OrbAbstractMethod method) {
        ApiAttribute attribute = new ApiAttribute();
        attribute.version = getServiceVersion();
        attribute.protocols = null;
        attribute.authLevel = method.getAccessLevel();
        return attribute;
    }

    public Collection<ApiInfo> getApiInfo() {
        List<ApiInfo> apiInfolist = new ArrayList<>();
        for (Map.Entry<String, OrbAbstractMethod> methodEntry : this.mMethodMap.entrySet()) {
            ApiInfo apiinfo = new ApiInfo();
            apiinfo.name = methodEntry.getKey();
            apiinfo.versions = new ApiAttribute[]{getApiAttribute(methodEntry.getValue())};
            apiInfolist.add(apiinfo);
        }
        return apiInfolist;
    }

    public OrbServiceInformationBase getServiceInformation() {
        OrbServiceInformationBase info = new OrbServiceInformationBase();
        info.setVersion(getServiceVersion());
        info.setProtocols(null);
        info.setMethods(getApiInfo());
        info.setNotifications(getNotificationInfo());
        return info;
    }

    public final Authenticator getOrbAuthenticator() {
        return this.mAuthenticator;
    }

    public final void setOrbAuthenticator(Authenticator authenticator) {
        this.mAuthenticator = authenticator;
    }
}
