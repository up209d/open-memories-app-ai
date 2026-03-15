package javax.servlet;

import java.util.EventListener;

/* loaded from: classes.dex */
public interface ServletRequestListener extends EventListener {
    void requestDestroyed(ServletRequestEvent servletRequestEvent);

    void requestInitialized(ServletRequestEvent servletRequestEvent);
}
