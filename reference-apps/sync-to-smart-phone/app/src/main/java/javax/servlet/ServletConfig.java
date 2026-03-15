package javax.servlet;

import java.util.Enumeration;

/* loaded from: classes.dex */
public interface ServletConfig {
    String getInitParameter(String str);

    Enumeration getInitParameterNames();

    ServletContext getServletContext();

    String getServletName();
}
