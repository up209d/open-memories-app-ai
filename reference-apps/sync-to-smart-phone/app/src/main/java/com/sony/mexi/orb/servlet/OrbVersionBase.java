package com.sony.mexi.orb.servlet;

import com.sony.imaging.app.synctosmartphone.webapi.definition.Name;
import com.sony.mexi.webapi.MethodTypeHandler;
import com.sony.mexi.webapi.VersionHandler;
import com.sony.mexi.webapi.serviceguide.v1_1.ServiceInformation;
import java.io.Serializable;
import java.util.Arrays;
import java.util.TreeMap;

/* loaded from: classes.dex */
public abstract class OrbVersionBase implements Serializable {
    private static final long serialVersionUID = 1;
    private TreeMap<String, OrbMethod> methodMap = new TreeMap<>();

    public abstract String getServiceName();

    public abstract String getServiceVersion();

    public void init(OrbMethodInvoker servlet) {
        throw new RuntimeException("Not Applicable");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addMethod(String name, OrbMethod method, OrbMethodInvoker servlet, boolean allowInheritance) {
        if (allowInheritance) {
            this.methodMap.put(name, servlet.inherit(name, method));
        } else {
            this.methodMap.put(name, method);
        }
    }

    public OrbMethod getMethod(String name) {
        return this.methodMap.get(name);
    }

    public OrbMethod getMethodBySignature(String name, OrbMethod parameter) {
        OrbMethod method = this.methodMap.get(name);
        if (method != null) {
            if (!Arrays.equals(method.resultTypes(), parameter.resultTypes()) || !Arrays.equals(method.parameterTypes(), parameter.parameterTypes())) {
                return null;
            }
            return method;
        }
        return method;
    }

    public void invokeGetMethodTypes(OrbMethodTypeHandler callbacks, String version) {
        for (String name : this.methodMap.keySet()) {
            OrbMethod method = this.methodMap.get(name);
            callbacks.handleMethodType(name, method.parameterTypes(), method.resultTypes(), version);
        }
        String[] getMethodTypesParamTypes = {"string"};
        String[] getMethodTypesResultTypes = {"string", "string*", "string*", "string"};
        callbacks.handleMethodType(Name.GET_METHOD_TYPES, getMethodTypesParamTypes, getMethodTypesResultTypes, version);
        String[] getVersionsParamTypes = new String[0];
        String[] getVersionsResultTypes = {"string*"};
        callbacks.handleMethodType(Name.GET_VERSIONS, getVersionsParamTypes, getVersionsResultTypes, version);
    }

    public int getVersions(VersionHandler handler) {
        throw new UnsupportedOperationException();
    }

    public int getMethodTypes(String version, MethodTypeHandler handler) {
        throw new UnsupportedOperationException();
    }

    public ServiceInformation getServiceInformation() {
        ServiceInformation info = new ServiceInformation();
        info.service = getServiceName();
        info.version = getServiceVersion();
        info.protocols = new String[]{"xhrpost:jsonizer"};
        info.signature = "";
        return info;
    }
}
