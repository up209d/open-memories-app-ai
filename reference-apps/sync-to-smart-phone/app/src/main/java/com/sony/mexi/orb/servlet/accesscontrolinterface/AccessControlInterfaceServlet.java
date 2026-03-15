package com.sony.mexi.orb.servlet.accesscontrolinterface;

import android.util.Log;
import com.sony.scalar.webapi.servlet.MethodInvokerBase;
import javax.servlet.ServletException;

/* loaded from: classes.dex */
public class AccessControlInterfaceServlet extends MethodInvokerBase {
    private static final long serialVersionUID = 1;

    @Override // javax.servlet.GenericServlet
    public final void init() throws ServletException {
        Log.v("AcceccControlInterface", "init");
        addVersion("1.0", new com.sony.mexi.orb.servlet.accesscontrolinterface.v1_0.AccessControlInterfaceServlet());
    }
}
