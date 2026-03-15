package com.sony.imaging.app.synctosmartphone.webapi;

import com.sony.imaging.app.synctosmartphone.webapi.v1_0.AutoSyncControlServlet;
import com.sony.scalar.webapi.servlet.MethodInvokerBase;
import javax.servlet.ServletException;

/* loaded from: classes.dex */
public class SyncServlet extends MethodInvokerBase {
    private static final long serialVersionUID = 1;

    @Override // javax.servlet.GenericServlet
    public final void init() throws ServletException {
        addVersion("1.0", new AutoSyncControlServlet());
    }
}
