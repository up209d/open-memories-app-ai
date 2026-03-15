package com.sony.mexi.orb.servlet.autosynccontrol.v1_0;

import com.sony.imaging.app.synctosmartphone.webapi.definition.Name;
import com.sony.mexi.json.JsArray;
import com.sony.mexi.json.JsError;
import com.sony.mexi.json.JsObject;
import com.sony.mexi.json.JsValue;
import com.sony.mexi.orb.servlet.OrbClientCallbacks;
import com.sony.mexi.orb.servlet.OrbMethod;
import com.sony.mexi.orb.servlet.OrbMethodInvoker;
import com.sony.mexi.orb.servlet.OrbVersionBase;
import com.sony.mexi.webapi.Status;
import com.sony.scalar.webapi.interfaces.server.AutoSyncControl.v1_0.AutoSyncControl;
import com.sony.scalar.webapi.service.contentsync.v1_0.ActPairingCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.GetInterfaceInformationCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.NotifySyncStatusCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.InterfaceInformation;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.Pairing;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.SyncStatus;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.SyncStatusSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public abstract class AutoSyncControlServletBase extends OrbVersionBase implements AutoSyncControl {
    public static final String SERVICE_NAME = "com.sony.scalar.webapi.interfaces.server.AutoSyncControl.v1_0.AutoSyncControl";
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
    private class ActPairingCallbackImpl extends OrbClientCallbacks implements ActPairingCallback {
        private ActPairingCallbackImpl() {
        }

        @Override // com.sony.scalar.webapi.service.contentsync.v1_0.ActPairingCallback
        public void returnCb() {
            this.response = new JsArray(new JsValue[0]);
        }
    }

    /* loaded from: classes.dex */
    private class GetInterfaceInformationCallbackImpl extends OrbClientCallbacks implements GetInterfaceInformationCallback {
        private GetInterfaceInformationCallbackImpl() {
        }

        @Override // com.sony.scalar.webapi.service.contentsync.v1_0.GetInterfaceInformationCallback
        public void returnCb(InterfaceInformation interfaceInformation) {
            this.response = new JsArray(new JsValue[0]);
            ((JsArray) this.response).add(interfaceInformation == null ? null : new JsObject(interfaceInformation));
        }
    }

    /* loaded from: classes.dex */
    private class NotifySyncStatusCallbackImpl extends OrbClientCallbacks implements NotifySyncStatusCallback {
        private NotifySyncStatusCallbackImpl() {
        }

        @Override // com.sony.scalar.webapi.service.contentsync.v1_0.NotifySyncStatusCallback
        public void returnCb(SyncStatus syncStatus) {
            this.response = new JsArray(new JsValue[0]);
            ((JsArray) this.response).add(syncStatus == null ? null : new JsObject(syncStatus));
        }
    }

    /* loaded from: classes.dex */
    private class MethodActPairing extends OrbMethod {
        private static final long serialVersionUID = 1;

        private MethodActPairing() {
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"uuid\":\"string\", \"friendlyName\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public JsValue invoke(HttpServletRequest req, HttpServletResponse res, JsArray params) {
            if (params.length() == 1 && (params.get(0).isObject() || params.get(0).isNull())) {
                ActPairingCallbackImpl callbacks = new ActPairingCallbackImpl();
                Pairing javaObject0 = null;
                if (params.get(0) instanceof JsObject) {
                    javaObject0 = new Pairing();
                    JsObject jsObject0 = (JsObject) params.get(0);
                    JsValue uuidVal = jsObject0.get("uuid");
                    if (uuidVal != null && uuidVal.isString()) {
                        javaObject0.uuid = uuidVal.toJavaString();
                        JsValue friendlyNameVal = jsObject0.get("friendlyName");
                        if (friendlyNameVal != null && friendlyNameVal.isString()) {
                            javaObject0.friendlyName = friendlyNameVal.toJavaString();
                        } else {
                            return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                        }
                    } else {
                        return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                    }
                }
                AutoSyncControlServletBase.this.actPairing(javaObject0, callbacks);
                JsValue result = callbacks.getServerResponse();
                if (result == null) {
                    return new JsError(Status.UNSUPPORTED_OPERATION.toInt(), "no result");
                }
                return result;
            }
            return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetInterfaceInformation extends OrbMethod {
        private static final long serialVersionUID = 1;

        private MethodGetInterfaceInformation() {
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] resultTypes() {
            return new String[]{"{\"productCategory\":\"string\", \"productName\":\"string\", \"modelName\":\"string\", \"serverName\":\"string\", \"interfaceVersion\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public JsValue invoke(HttpServletRequest req, HttpServletResponse res, JsArray params) {
            if (params.length() == 0) {
                GetInterfaceInformationCallbackImpl callbacks = new GetInterfaceInformationCallbackImpl();
                AutoSyncControlServletBase.this.getInterfaceInformation(callbacks);
                JsValue result = callbacks.getServerResponse();
                if (result == null) {
                    return new JsError(Status.UNSUPPORTED_OPERATION.toInt(), "no result");
                }
                return result;
            }
            return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
        }
    }

    /* loaded from: classes.dex */
    private class MethodNotifySyncStatus extends OrbMethod {
        private static final long serialVersionUID = 1;

        private MethodNotifySyncStatus() {
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] resultTypes() {
            return new String[]{"{\"totalCnt\":\"int\", \"remainingCnt\":\"int\", \"downloadableUrl\":\"string\", \"contentType\":\"string\", \"fileName\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"uuid\":\"string\", \"status\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public JsValue invoke(HttpServletRequest req, HttpServletResponse res, JsArray params) {
            if (params.length() == 1 && (params.get(0).isObject() || params.get(0).isNull())) {
                NotifySyncStatusCallbackImpl callbacks = new NotifySyncStatusCallbackImpl();
                SyncStatusSource javaObject0 = null;
                if (params.get(0) instanceof JsObject) {
                    javaObject0 = new SyncStatusSource();
                    JsObject jsObject0 = (JsObject) params.get(0);
                    JsValue uuidVal = jsObject0.get("uuid");
                    if (uuidVal != null && uuidVal.isString()) {
                        javaObject0.uuid = uuidVal.toJavaString();
                        JsValue statusVal = jsObject0.get("status");
                        if (statusVal != null && statusVal.isString()) {
                            javaObject0.status = statusVal.toJavaString();
                        } else {
                            return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                        }
                    } else {
                        return new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                    }
                }
                AutoSyncControlServletBase.this.notifySyncStatus(javaObject0, callbacks);
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
        addMethod(Name.ACT_PAIRING, new MethodActPairing(), servlet, false);
        addMethod("getInterfaceInformation", new MethodGetInterfaceInformation(), servlet, false);
        addMethod(Name.NOTIFY_SYNC_STATUS, new MethodNotifySyncStatus(), servlet, false);
    }
}
