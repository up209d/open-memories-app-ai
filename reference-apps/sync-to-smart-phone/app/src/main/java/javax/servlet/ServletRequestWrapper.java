package javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public class ServletRequestWrapper implements ServletRequest {
    private ServletRequest request;

    public ServletRequestWrapper(ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.request = request;
    }

    public ServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.request = request;
    }

    @Override // javax.servlet.ServletRequest
    public Object getAttribute(String name) {
        return this.request.getAttribute(name);
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration getAttributeNames() {
        return this.request.getAttributeNames();
    }

    @Override // javax.servlet.ServletRequest
    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding();
    }

    @Override // javax.servlet.ServletRequest
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        this.request.setCharacterEncoding(enc);
    }

    @Override // javax.servlet.ServletRequest
    public int getContentLength() {
        return this.request.getContentLength();
    }

    @Override // javax.servlet.ServletRequest
    public String getContentType() {
        return this.request.getContentType();
    }

    @Override // javax.servlet.ServletRequest
    public ServletInputStream getInputStream() throws IOException {
        return this.request.getInputStream();
    }

    @Override // javax.servlet.ServletRequest
    public String getParameter(String name) {
        return this.request.getParameter(name);
    }

    @Override // javax.servlet.ServletRequest
    public Map getParameterMap() {
        return this.request.getParameterMap();
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration getParameterNames() {
        return this.request.getParameterNames();
    }

    @Override // javax.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        return this.request.getParameterValues(name);
    }

    @Override // javax.servlet.ServletRequest
    public String getProtocol() {
        return this.request.getProtocol();
    }

    @Override // javax.servlet.ServletRequest
    public String getScheme() {
        return this.request.getScheme();
    }

    @Override // javax.servlet.ServletRequest
    public String getServerName() {
        return this.request.getServerName();
    }

    @Override // javax.servlet.ServletRequest
    public int getServerPort() {
        return this.request.getServerPort();
    }

    @Override // javax.servlet.ServletRequest
    public BufferedReader getReader() throws IOException {
        return this.request.getReader();
    }

    @Override // javax.servlet.ServletRequest
    public String getRemoteAddr() {
        return this.request.getRemoteAddr();
    }

    @Override // javax.servlet.ServletRequest
    public String getRemoteHost() {
        return this.request.getRemoteHost();
    }

    @Override // javax.servlet.ServletRequest
    public void setAttribute(String name, Object o) {
        this.request.setAttribute(name, o);
    }

    @Override // javax.servlet.ServletRequest
    public void removeAttribute(String name) {
        this.request.removeAttribute(name);
    }

    @Override // javax.servlet.ServletRequest
    public Locale getLocale() {
        return this.request.getLocale();
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration getLocales() {
        return this.request.getLocales();
    }

    @Override // javax.servlet.ServletRequest
    public boolean isSecure() {
        return this.request.isSecure();
    }

    @Override // javax.servlet.ServletRequest
    public RequestDispatcher getRequestDispatcher(String path) {
        return this.request.getRequestDispatcher(path);
    }

    @Override // javax.servlet.ServletRequest
    public String getRealPath(String path) {
        return this.request.getRealPath(path);
    }

    @Override // javax.servlet.ServletRequest
    public int getRemotePort() {
        return this.request.getRemotePort();
    }

    @Override // javax.servlet.ServletRequest
    public String getLocalName() {
        return this.request.getLocalName();
    }

    @Override // javax.servlet.ServletRequest
    public String getLocalAddr() {
        return this.request.getLocalAddr();
    }

    @Override // javax.servlet.ServletRequest
    public int getLocalPort() {
        return this.request.getLocalPort();
    }
}
