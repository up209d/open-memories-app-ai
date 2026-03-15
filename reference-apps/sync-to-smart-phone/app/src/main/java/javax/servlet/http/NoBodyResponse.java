package javax.servlet.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import javax.servlet.ServletOutputStream;

/* compiled from: HttpServlet.java */
/* loaded from: classes.dex */
class NoBodyResponse implements HttpServletResponse {
    private boolean didSetContentLength;
    private NoBodyOutputStream noBody = new NoBodyOutputStream();
    private HttpServletResponse resp;
    private PrintWriter writer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NoBodyResponse(HttpServletResponse r) {
        this.resp = r;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContentLength() {
        if (!this.didSetContentLength) {
            this.resp.setContentLength(this.noBody.getContentLength());
        }
    }

    @Override // javax.servlet.ServletResponse
    public void setContentLength(int len) {
        this.resp.setContentLength(len);
        this.didSetContentLength = true;
    }

    @Override // javax.servlet.ServletResponse
    public void setCharacterEncoding(String charset) {
        this.resp.setCharacterEncoding(charset);
    }

    @Override // javax.servlet.ServletResponse
    public void setContentType(String type) {
        this.resp.setContentType(type);
    }

    @Override // javax.servlet.ServletResponse
    public String getContentType() {
        return this.resp.getContentType();
    }

    @Override // javax.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        return this.noBody;
    }

    @Override // javax.servlet.ServletResponse
    public String getCharacterEncoding() {
        return this.resp.getCharacterEncoding();
    }

    @Override // javax.servlet.ServletResponse
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (this.writer == null) {
            OutputStreamWriter w = new OutputStreamWriter(this.noBody, getCharacterEncoding());
            this.writer = new PrintWriter(w);
        }
        return this.writer;
    }

    @Override // javax.servlet.ServletResponse
    public void setBufferSize(int size) throws IllegalStateException {
        this.resp.setBufferSize(size);
    }

    @Override // javax.servlet.ServletResponse
    public int getBufferSize() {
        return this.resp.getBufferSize();
    }

    @Override // javax.servlet.ServletResponse
    public void reset() throws IllegalStateException {
        this.resp.reset();
    }

    @Override // javax.servlet.ServletResponse
    public void resetBuffer() throws IllegalStateException {
        this.resp.resetBuffer();
    }

    @Override // javax.servlet.ServletResponse
    public boolean isCommitted() {
        return this.resp.isCommitted();
    }

    @Override // javax.servlet.ServletResponse
    public void flushBuffer() throws IOException {
        this.resp.flushBuffer();
    }

    @Override // javax.servlet.ServletResponse
    public void setLocale(Locale loc) {
        this.resp.setLocale(loc);
    }

    @Override // javax.servlet.ServletResponse
    public Locale getLocale() {
        return this.resp.getLocale();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        this.resp.addCookie(cookie);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public boolean containsHeader(String name) {
        return this.resp.containsHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setStatus(int sc, String sm) {
        this.resp.setStatus(sc, sm);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setStatus(int sc) {
        this.resp.setStatus(sc);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        this.resp.setHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        this.resp.setIntHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long date) {
        this.resp.setDateHeader(name, date);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        this.resp.sendError(sc, msg);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        this.resp.sendError(sc);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        this.resp.sendRedirect(location);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeURL(String url) {
        return this.resp.encodeURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeRedirectURL(String url) {
        return this.resp.encodeRedirectURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        this.resp.addHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long value) {
        this.resp.addDateHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        this.resp.addIntHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }
}
