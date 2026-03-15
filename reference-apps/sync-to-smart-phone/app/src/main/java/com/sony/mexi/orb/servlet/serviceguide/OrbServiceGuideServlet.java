package com.sony.mexi.orb.servlet.serviceguide;

import com.sony.mexi.orb.servlet.OrbMethodInvoker;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/* loaded from: classes.dex */
public class OrbServiceGuideServlet extends OrbMethodInvoker {
    private static final long serialVersionUID = 1;
    Map<String, HttpServlet> servlets;

    public OrbServiceGuideServlet(Map<String, HttpServlet> servlets) {
        this.servlets = servlets;
    }

    public final String name() {
        return "*/guide";
    }

    public void setServlets(Map<String, HttpServlet> servlets) {
        this.servlets = servlets;
    }

    @Override // javax.servlet.GenericServlet
    public final void init() throws ServletException {
        addVersion(new com.sony.mexi.orb.servlet.serviceguide.v1_0.OrbServiceGuideServlet(this.servlets));
        addVersion(new com.sony.mexi.orb.servlet.serviceguide.v1_1.OrbServiceGuideServlet(this.servlets));
    }
}
