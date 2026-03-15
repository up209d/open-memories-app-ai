package javax.servlet;

import java.io.IOException;

/* loaded from: classes.dex */
public interface RequestDispatcher {
    void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;

    void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;
}
