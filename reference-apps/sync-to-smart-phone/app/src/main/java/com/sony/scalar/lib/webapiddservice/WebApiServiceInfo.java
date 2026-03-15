package com.sony.scalar.lib.webapiddservice;

/* loaded from: classes.dex */
public class WebApiServiceInfo {
    private String accessType;
    private String actionListUrl;
    private String serviceType;

    public WebApiServiceInfo() {
        this.serviceType = "";
        this.actionListUrl = "";
        this.accessType = "";
    }

    public WebApiServiceInfo(String serviceType, String actionListUrl, String accessType) {
        this.serviceType = "";
        this.actionListUrl = "";
        this.accessType = "";
        this.serviceType = serviceType;
        this.actionListUrl = actionListUrl;
        this.accessType = accessType;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getActionListUrl() {
        return this.actionListUrl;
    }

    public void setActionListUrl(String actionListUrl) {
        this.actionListUrl = actionListUrl;
    }

    public String getAccessType() {
        return this.accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
}
