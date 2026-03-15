package com.sony.imaging.app.srctrl.webapi;

import com.sony.imaging.app.util.PfBugAvailability;
import com.sony.mexi.orb.service.http.HttpScalarApiHandler;

/* loaded from: classes.dex */
public class SRCtrlAvContentServlet extends HttpScalarApiHandler {
    private static final long serialVersionUID = 1;

    @Override // com.sony.mexi.orb.service.OrbServiceProvider
    public void initService() {
        if (!PfBugAvailability.encodeAtPlay) {
            addVersion("1.0", new com.sony.imaging.app.srctrl.webapi.v1_0.SRCtrlAvContentServlet());
            addVersion("1.1", new com.sony.imaging.app.srctrl.webapi.v1_1.SRCtrlAvContentServlet());
            addVersion("1.2", new com.sony.imaging.app.srctrl.webapi.v1_2.SRCtrlAvContentServlet());
            addVersion("1.3", new com.sony.imaging.app.srctrl.webapi.v1_3.SRCtrlAvContentServlet());
        }
    }
}
