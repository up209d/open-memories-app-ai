package javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public interface ServletRequest {
    Object getAttribute(String str);

    Enumeration getAttributeNames();

    String getCharacterEncoding();

    int getContentLength();

    String getContentType();

    ServletInputStream getInputStream() throws IOException;

    String getLocalAddr();

    String getLocalName();

    int getLocalPort();

    Locale getLocale();

    Enumeration getLocales();

    String getParameter(String str);

    Map getParameterMap();

    Enumeration getParameterNames();

    String[] getParameterValues(String str);

    String getProtocol();

    BufferedReader getReader() throws IOException;

    String getRealPath(String str);

    String getRemoteAddr();

    String getRemoteHost();

    int getRemotePort();

    RequestDispatcher getRequestDispatcher(String str);

    String getScheme();

    String getServerName();

    int getServerPort();

    boolean isSecure();

    void removeAttribute(String str);

    void setAttribute(String str, Object obj);

    void setCharacterEncoding(String str) throws UnsupportedEncodingException;
}
