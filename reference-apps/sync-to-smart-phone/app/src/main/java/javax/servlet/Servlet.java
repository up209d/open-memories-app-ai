package javax.servlet;

import java.io.IOException;

/* loaded from: classes.dex */
public interface Servlet {
    void destroy();

    ServletConfig getServletConfig();

    String getServletInfo();

    void init(ServletConfig servletConfig) throws ServletException;

    void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;
}
