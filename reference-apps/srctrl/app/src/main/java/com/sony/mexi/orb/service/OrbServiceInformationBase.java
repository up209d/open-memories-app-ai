package com.sony.mexi.orb.service;

import com.sony.mexi.orb.server.OrbServiceInformation;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiAttribute;
import com.sony.mexi.webapi.guide.v1_0.common.struct.ApiInfo;
import com.sony.mexi.webapi.guide.v1_0.common.struct.NotificationInfo;
import java.util.Collection;

/* loaded from: classes.dex */
public class OrbServiceInformationBase implements OrbServiceInformation {
    public Collection<ApiInfo> methods;
    public Collection<NotificationInfo> notifications;
    public Collection<String> protocols;
    public String version;

    public void setVersion(String serviceVersion) {
        this.version = serviceVersion;
    }

    public String getVersion() {
        return this.version;
    }

    public void setProtocols(Collection<String> serviceProtocols) {
        this.protocols = serviceProtocols;
    }

    public Collection<String> getProtocols() {
        return this.protocols;
    }

    public void setMethods(Collection<ApiInfo> serviceMethods) {
        this.methods = serviceMethods;
    }

    public Collection<ApiInfo> getMethods() {
        return this.methods;
    }

    public void setNotifications(Collection<NotificationInfo> serviceNotifications) {
        this.notifications = serviceNotifications;
    }

    public Collection<NotificationInfo> getNotifications() {
        return this.notifications;
    }

    public void setMethodProtocols(Collection<String> methodProtocols) {
        for (ApiInfo method : this.methods) {
            ApiAttribute[] arr$ = method.versions;
            for (ApiAttribute attribute : arr$) {
                attribute.protocols = (String[]) methodProtocols.toArray(new String[methodProtocols.size()]);
            }
        }
    }
}
