package com.sony.mexi.orb.servlet.serviceguide.v1_1;

import com.sony.imaging.app.synctosmartphone.webapi.definition.ServiceType;
import com.sony.mexi.orb.servlet.OrbMethodInvoker;
import com.sony.mexi.webapi.serviceguide.v1_1.InformationHandler;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServlet;

/* loaded from: classes.dex */
public class OrbServiceGuideServlet extends OrbServiceGuideServletBase {
    private static final long serialVersionUID = 1;
    private Map<String, HttpServlet> servlets;

    public OrbServiceGuideServlet(Map<String, HttpServlet> servlets) {
        this.servlets = servlets;
    }

    @Override // com.sony.mexi.orb.servlet.serviceguide.v1_1.OrbServiceGuideServletBase
    public int getServiceInformation(String uri, InformationHandler handler) {
        String baseURI = null;
        HttpServlet guide = this.servlets.get("*/guide");
        if (guide != null && (guide instanceof OrbMethodInvoker)) {
            baseURI = uri.substring(0, uri.length() - ServiceType.GUIDE.length());
        }
        if (baseURI != null) {
            TreeMap<String, OrbMethodInvoker> map = new TreeMap<>();
            for (String url : this.servlets.keySet()) {
                HttpServlet servlet = this.servlets.get(url);
                if (servlet instanceof OrbMethodInvoker) {
                    String relativeUrl = null;
                    if (url.startsWith(baseURI)) {
                        relativeUrl = url.substring(baseURI.length());
                    } else if (url.endsWith(ServiceType.GUIDE)) {
                        relativeUrl = ServiceType.GUIDE;
                    }
                    if (relativeUrl != null) {
                        map.put(relativeUrl, (OrbMethodInvoker) servlet);
                    }
                }
            }
            for (String relativeUrl2 : map.keySet()) {
                handler.handleInformation(relativeUrl2, map.get(relativeUrl2).getServiceInformation());
            }
        }
        return 0;
    }

    @Override // com.sony.mexi.orb.servlet.serviceguide.v1_1.OrbServiceGuideServletBase
    public final boolean usePreviousGetServiceInformation() {
        return false;
    }
}
