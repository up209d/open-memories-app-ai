package com.sony.mexi.orb.servlet.serviceguide.v1_0;

import com.sony.imaging.app.synctosmartphone.webapi.definition.ServiceType;
import com.sony.mexi.orb.servlet.OrbMethodInvoker;
import com.sony.mexi.webapi.serviceguide.v1_0.ProtocolHandler;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import javax.servlet.http.HttpServlet;

/* loaded from: classes.dex */
public class OrbServiceGuideServlet extends OrbServiceGuideServletBase {
    private static final long serialVersionUID = 1;
    private Map<String, HttpServlet> servlets;

    public OrbServiceGuideServlet(Map<String, HttpServlet> servlets) {
        this.servlets = servlets;
    }

    @Override // com.sony.mexi.orb.servlet.serviceguide.v1_0.OrbServiceGuideServletBase
    public int getServiceProtocols(String uri, ProtocolHandler handler) {
        String baseURI = null;
        HttpServlet guide = this.servlets.get("*/guide");
        if (guide != null && (guide instanceof OrbMethodInvoker)) {
            baseURI = uri.substring(0, uri.length() - ServiceType.GUIDE.length());
        }
        if (baseURI != null) {
            TreeSet<String> set = new TreeSet<>();
            for (String url : this.servlets.keySet()) {
                HttpServlet servlet = this.servlets.get(url);
                if (servlet instanceof OrbMethodInvoker) {
                    if (url.startsWith(baseURI)) {
                        set.add(url.substring(baseURI.length()));
                    } else if (url.endsWith(ServiceType.GUIDE)) {
                        set.add(ServiceType.GUIDE);
                    }
                }
            }
            String[] protocols = {"xhrpost:jsonizer"};
            Iterator<String> itr = set.iterator();
            while (itr.hasNext()) {
                handler.handleProtocols(itr.next(), protocols);
            }
        }
        return 0;
    }
}
