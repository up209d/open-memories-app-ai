package com.sony.imaging.app.srctrl.webapi;

import com.sony.scalar.webapi.lib.authlib.AuthApiHandler;

/* loaded from: classes.dex */
public class SRCtrlServlet extends AuthApiHandler {
    private static final long serialVersionUID = 1;

    @Override // com.sony.scalar.webapi.lib.authlib.AuthApiHandler, com.sony.mexi.orb.service.OrbServiceProvider
    public void initService() {
        addVersion("1.0", new com.sony.imaging.app.srctrl.webapi.v1_0.SRCtrlServlet());
        addVersion("1.1", new com.sony.imaging.app.srctrl.webapi.v1_1.SRCtrlServlet());
        addVersion("1.2", new com.sony.imaging.app.srctrl.webapi.v1_2.SRCtrlServlet());
        addVersion("1.3", new com.sony.imaging.app.srctrl.webapi.v1_3.SRCtrlServlet());
        addVersion("1.4", new com.sony.imaging.app.srctrl.webapi.v1_4.SRCtrlServlet());
    }
}
