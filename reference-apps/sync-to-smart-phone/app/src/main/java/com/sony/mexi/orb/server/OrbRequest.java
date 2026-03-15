package com.sony.mexi.orb.server;

import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;

/* loaded from: classes.dex */
public class OrbRequest implements HttpServletRequest {
    private final String characterEncoding = AutoSyncConstants.TEXT_ENCODING_UTF8;
    private HttpRequest req;

    public OrbRequest(HttpRequest req) {
        this.req = req;
    }

    @Override // javax.servlet.ServletRequest
    public String getCharacterEncoding() {
        return AutoSyncConstants.TEXT_ENCODING_UTF8;
    }

    @Override // javax.servlet.ServletRequest
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        if (!env.equals(AutoSyncConstants.TEXT_ENCODING_UTF8)) {
            throw new UnsupportedEncodingException();
        }
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getHeader(String name) {
        Header header = this.req.getLastHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    @Override // javax.servlet.ServletRequest
    public int getContentLength() {
        RequestLine rl = this.req.getRequestLine();
        if (rl != null && rl.getMethod().equals("POST")) {
            HttpEntityEnclosingRequest entityreq = (HttpEntityEnclosingRequest) this.req;
            HttpEntity entity = entityreq.getEntity();
            if (entity != null) {
                return (int) entity.getContentLength();
            }
        }
        return Integer.parseInt(getHeader("Content-Length"));
    }

    @Override // javax.servlet.ServletRequest
    public String getContentType() {
        return getHeader("Content-Type");
    }

    @Override // javax.servlet.ServletRequest
    public String getProtocol() {
        String protocol;
        ProtocolVersion pv = this.req.getProtocolVersion();
        if (pv == null || (protocol = pv.getProtocol()) == null) {
            return null;
        }
        int major = pv.getMajor();
        int minor = pv.getMinor();
        return String.valueOf(protocol) + "/" + major + "." + minor;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getMethod() {
        RequestLine rl = this.req.getRequestLine();
        if (rl == null) {
            return null;
        }
        return rl.getMethod();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRequestURI() {
        RequestLine rl = this.req.getRequestLine();
        if (rl == null) {
            return null;
        }
        try {
            URI uri = new URI(rl.getUri());
            return uri.getRawPath();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getQueryString() {
        RequestLine rl = this.req.getRequestLine();
        if (rl == null) {
            return null;
        }
        try {
            URI uri = new URI(rl.getUri());
            return uri.getRawQuery();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override // javax.servlet.ServletRequest
    public String getScheme() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public String getServerName() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public int getServerPort() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public String getRemoteHost() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public void setAttribute(String name, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<?> getLocales() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public boolean isSecure() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public int getRemotePort() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public String getLocalAddr() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public int getLocalPort() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getAuthType() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Enumeration<?> getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Enumeration<?> getHeaderNames() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public int getIntHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getPathInfo() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRemoteUser() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getServletPath() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<?> getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public ServletInputStream getInputStream() throws IOException {
        RequestLine rl = this.req.getRequestLine();
        if (rl != null && rl.getMethod().equals("POST")) {
            HttpEntityEnclosingRequest entityreq = (HttpEntityEnclosingRequest) this.req;
            HttpEntity entity = entityreq.getEntity();
            if (entity != null) {
                return new OrbInputStream(entity.getContent());
            }
        }
        return null;
    }

    @Override // javax.servlet.ServletRequest
    public String getParameter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<?> getParameterNames() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletRequest
    public Map<?, ?> getParameterMap() {
        throw new UnsupportedOperationException();
    }
}
