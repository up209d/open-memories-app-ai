package javax.servlet.http;

import java.security.Principal;
import java.util.Enumeration;
import javax.servlet.ServletRequest;

/* loaded from: classes.dex */
public interface HttpServletRequest extends ServletRequest {
    public static final String BASIC_AUTH = "BASIC";
    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";
    public static final String DIGEST_AUTH = "DIGEST";
    public static final String FORM_AUTH = "FORM";

    String getAuthType();

    String getContextPath();

    Cookie[] getCookies();

    long getDateHeader(String str);

    String getHeader(String str);

    Enumeration getHeaderNames();

    Enumeration getHeaders(String str);

    int getIntHeader(String str);

    String getMethod();

    String getPathInfo();

    String getPathTranslated();

    String getQueryString();

    String getRemoteUser();

    String getRequestURI();

    StringBuffer getRequestURL();

    String getRequestedSessionId();

    String getServletPath();

    HttpSession getSession();

    HttpSession getSession(boolean z);

    Principal getUserPrincipal();

    boolean isRequestedSessionIdFromCookie();

    boolean isRequestedSessionIdFromURL();

    boolean isRequestedSessionIdFromUrl();

    boolean isRequestedSessionIdValid();

    boolean isUserInRole(String str);
}
