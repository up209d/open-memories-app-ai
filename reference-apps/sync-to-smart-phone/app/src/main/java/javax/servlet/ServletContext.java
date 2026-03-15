package javax.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/* loaded from: classes.dex */
public interface ServletContext {
    Object getAttribute(String str);

    Enumeration getAttributeNames();

    ServletContext getContext(String str);

    String getContextPath();

    String getInitParameter(String str);

    Enumeration getInitParameterNames();

    int getMajorVersion();

    String getMimeType(String str);

    int getMinorVersion();

    RequestDispatcher getNamedDispatcher(String str);

    String getRealPath(String str);

    RequestDispatcher getRequestDispatcher(String str);

    URL getResource(String str) throws MalformedURLException;

    InputStream getResourceAsStream(String str);

    Set getResourcePaths(String str);

    String getServerInfo();

    Servlet getServlet(String str) throws ServletException;

    String getServletContextName();

    Enumeration getServletNames();

    Enumeration getServlets();

    void log(Exception exc, String str);

    void log(String str);

    void log(String str, Throwable th);

    void removeAttribute(String str);

    void setAttribute(String str, Object obj);
}
