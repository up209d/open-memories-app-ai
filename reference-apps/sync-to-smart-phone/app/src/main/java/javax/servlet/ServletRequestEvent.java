package javax.servlet;

import java.util.EventObject;

/* loaded from: classes.dex */
public class ServletRequestEvent extends EventObject {
    private ServletRequest request;

    public ServletRequestEvent(ServletContext sc, ServletRequest request) {
        super(sc);
        this.request = request;
    }

    public ServletRequest getServletRequest() {
        return this.request;
    }

    public ServletContext getServletContext() {
        return (ServletContext) super.getSource();
    }
}
