package com.sony.mexi.orb.servlet.accesscontrolinterface.v1_0;

import com.sony.mexi.json.JsArray;
import com.sony.mexi.json.JsError;
import com.sony.mexi.json.JsObject;
import com.sony.mexi.json.JsValue;
import com.sony.mexi.orb.servlet.OrbClientCallbacks;
import com.sony.mexi.orb.servlet.OrbMethod;
import com.sony.mexi.orb.servlet.OrbMethodInvoker;
import com.sony.mexi.orb.servlet.OrbVersionBase;
import com.sony.mexi.webapi.Status;
import com.sony.scalar.webapi.interfaces.server.smartremote.accesscontrol.v1_0.AccessControlInterface;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.ActEnableMethodsCallback;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.DeveloperInfo;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.DeveloperKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public abstract class AccessControlInterfaceServletBase extends OrbVersionBase implements AccessControlInterface {
    public static final String SERVICE_NAME = "com.sony.scalar.webapi.interfaces.server.smartremote.accesscontrol.v1_0.AccessControlInterface";
    public static final String SERVICE_VERSION = "1.0";
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.servlet.OrbVersionBase
    public final String getServiceName() {
        return SERVICE_NAME;
    }

    @Override // com.sony.mexi.orb.servlet.OrbVersionBase
    public final String getServiceVersion() {
        return "1.0";
    }

    /* loaded from: classes.dex */
    private class ActEnableMethodsCallbackImpl extends OrbClientCallbacks implements ActEnableMethodsCallback {
        private ActEnableMethodsCallbackImpl() {
        }

        /* synthetic */ ActEnableMethodsCallbackImpl(AccessControlInterfaceServletBase accessControlInterfaceServletBase, ActEnableMethodsCallbackImpl actEnableMethodsCallbackImpl) {
            this();
        }

        @Override // com.sony.scalar.webapi.service.accesscontrol.v1_0.ActEnableMethodsCallback
        public void returnCb(DeveloperKey developerKey) {
            this.response = new JsArray(new JsValue[0]);
            ((JsArray) this.response).add(developerKey == null ? null : new JsObject(developerKey));
        }
    }

    /* loaded from: classes.dex */
    private class MethodActEnableMethods extends OrbMethod {
        private static final long serialVersionUID = 1;

        private MethodActEnableMethods() {
        }

        /* synthetic */ MethodActEnableMethods(AccessControlInterfaceServletBase accessControlInterfaceServletBase, MethodActEnableMethods methodActEnableMethods) {
            this();
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] resultTypes() {
            return new String[]{"{\"dg\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"developerName\":\"string\", \"developerID\":\"string\", \"sg\":\"string\", \"methods\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public JsValue invoke(HttpServletRequest req, HttpServletResponse res, JsArray params) {
            if (params.length() == 1 && (params.get(0).isObject() || params.get(0).isNull())) {
                ActEnableMethodsCallbackImpl callbacks = new ActEnableMethodsCallbackImpl(AccessControlInterfaceServletBase.this, null);
                DeveloperInfo javaObject0 = null;
                if (params.get(0) instanceof JsObject) {
                    javaObject0 = new DeveloperInfo();
                    JsObject jsObject0 = (JsObject) params.get(0);
                    JsValue developerNameVal = jsObject0.get("developerName");
                    if (developerNameVal != null && developerNameVal.isString()) {
                        javaObject0.developerName = developerNameVal.toJavaString();
                        JsValue developerIDVal = jsObject0.get("developerID");
                        if (developerIDVal != null && developerIDVal.isString()) {
                            javaObject0.developerID = developerIDVal.toJavaString();
                            JsValue sgVal = jsObject0.get("sg");
                            if (sgVal != null && sgVal.isString()) {
                                javaObject0.sg = sgVal.toJavaString();
                                JsValue methodsVal = jsObject0.get("methods");
                                if (methodsVal != null && methodsVal.isString()) {
                                    javaObject0.methods = methodsVal.toJavaString();
                                } else {
                                    return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                                }
                            } else {
                                return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                            }
                        } else {
                            return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                        }
                    } else {
                        return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                    }
                }
                AccessControlInterfaceServletBase.this.actEnableMethods(javaObject0, callbacks);
                JsValue result = callbacks.getServerResponse();
                if (result == null) {
                    return new JsError(Status.UNSUPPORTED_OPERATION.toInt(), "no result");
                }
                return result;
            }
            return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
        }
    }

    @Override // com.sony.mexi.orb.servlet.OrbVersionBase
    public final void init(OrbMethodInvoker servlet) {
        addMethod("actEnableMethods", new MethodActEnableMethods(this, null), servlet, false);
    }
}
