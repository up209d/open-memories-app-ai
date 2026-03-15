package com.sony.mexi.orb.servlet.serviceguide.v1_0;

import com.sony.mexi.json.JsArray;
import com.sony.mexi.json.JsError;
import com.sony.mexi.json.JsString;
import com.sony.mexi.json.JsValue;
import com.sony.mexi.orb.servlet.OrbClientCallbacks;
import com.sony.mexi.orb.servlet.OrbMethod;
import com.sony.mexi.orb.servlet.OrbMethodInvoker;
import com.sony.mexi.orb.servlet.OrbVersionBase;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.serviceguide.v1_0.ProtocolHandler;
import com.sony.mexi.webapi.serviceguide.v1_0.ServiceGuide;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public abstract class OrbServiceGuideServletBase extends OrbVersionBase implements ServiceGuide {
    private static final long serialVersionUID = 1;

    abstract int getServiceProtocols(String str, ProtocolHandler protocolHandler);

    @Override // com.sony.mexi.orb.servlet.OrbVersionBase
    public final String getServiceName() {
        return "com.sony.mexi.webapi.serviceguide.v1_0.ServiceGuide";
    }

    @Override // com.sony.mexi.orb.servlet.OrbVersionBase
    public final String getServiceVersion() {
        return "1.0";
    }

    /* loaded from: classes.dex */
    private class ProtocolHandlerImpl extends OrbClientCallbacks implements ProtocolHandler {
        private JsArray result;

        private ProtocolHandlerImpl() {
        }

        /* synthetic */ ProtocolHandlerImpl(OrbServiceGuideServletBase orbServiceGuideServletBase, ProtocolHandlerImpl protocolHandlerImpl) {
            this();
        }

        @Override // com.sony.mexi.webapi.serviceguide.v1_0.ProtocolHandler
        public void handleProtocols(String relativeUrl, String[] protocols) {
            if (this.response == null) {
                this.response = new JsArray(new JsValue[0]);
            }
            this.result = new JsArray(new JsValue[0]);
            this.result.add(relativeUrl == null ? null : new JsString(relativeUrl));
            this.result.add(protocols != null ? new JsArray(protocols) : null);
            ((JsArray) this.response).add(this.result);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetServiceProtocols extends OrbMethod {
        private static final long serialVersionUID = 1;

        private MethodGetServiceProtocols() {
        }

        /* synthetic */ MethodGetServiceProtocols(OrbServiceGuideServletBase orbServiceGuideServletBase, MethodGetServiceProtocols methodGetServiceProtocols) {
            this();
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public boolean hasContinuousCallbacks() {
            return true;
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] resultTypes() {
            return new String[]{"string", "string*"};
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.servlet.OrbMethod
        public JsValue invoke(HttpServletRequest req, HttpServletResponse res, JsArray params) {
            if (params.length() == 1) {
                ProtocolHandlerImpl callbacks = new ProtocolHandlerImpl(OrbServiceGuideServletBase.this, null);
                try {
                    OrbServiceGuideServletBase.this.getServiceProtocols(params.get(0).toJavaString(), callbacks);
                    JsValue result = callbacks.getServerResponse();
                    if (result == null) {
                        JsValue result2 = new JsError(Status.UNSUPPORTED_OPERATION.toInt(), "no result");
                        return result2;
                    }
                    return result;
                } catch (RuntimeException e) {
                    JsValue result3 = new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
                    return result3;
                }
            }
            JsValue result4 = new JsError(Status.ILLEGAL_ARGUMENT.toInt(), "illegal argument");
            return result4;
        }
    }

    @Override // com.sony.mexi.orb.servlet.OrbVersionBase
    public final void init(OrbMethodInvoker servlet) {
        addMethod("getServiceProtocols", new MethodGetServiceProtocols(this, null), servlet, false);
    }

    @Override // com.sony.mexi.webapi.serviceguide.v1_0.ServiceGuide
    public int getServiceProtocols(ProtocolHandler handler) {
        throw new UnsupportedOperationException();
    }
}
