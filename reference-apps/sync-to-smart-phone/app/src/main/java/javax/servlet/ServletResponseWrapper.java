package javax.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/* loaded from: classes.dex */
public class ServletResponseWrapper implements ServletResponse {
    private ServletResponse response;

    public ServletResponseWrapper(ServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        this.response = response;
    }

    public ServletResponse getResponse() {
        return this.response;
    }

    public void setResponse(ServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        this.response = response;
    }

    @Override // javax.servlet.ServletResponse
    public void setCharacterEncoding(String charset) {
        this.response.setCharacterEncoding(charset);
    }

    @Override // javax.servlet.ServletResponse
    public String getCharacterEncoding() {
        return this.response.getCharacterEncoding();
    }

    @Override // javax.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        return this.response.getOutputStream();
    }

    @Override // javax.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        return this.response.getWriter();
    }

    @Override // javax.servlet.ServletResponse
    public void setContentLength(int len) {
        this.response.setContentLength(len);
    }

    @Override // javax.servlet.ServletResponse
    public void setContentType(String type) {
        this.response.setContentType(type);
    }

    @Override // javax.servlet.ServletResponse
    public String getContentType() {
        return this.response.getContentType();
    }

    @Override // javax.servlet.ServletResponse
    public void setBufferSize(int size) {
        this.response.setBufferSize(size);
    }

    @Override // javax.servlet.ServletResponse
    public int getBufferSize() {
        return this.response.getBufferSize();
    }

    @Override // javax.servlet.ServletResponse
    public void flushBuffer() throws IOException {
        this.response.flushBuffer();
    }

    @Override // javax.servlet.ServletResponse
    public boolean isCommitted() {
        return this.response.isCommitted();
    }

    @Override // javax.servlet.ServletResponse
    public void reset() {
        this.response.reset();
    }

    @Override // javax.servlet.ServletResponse
    public void resetBuffer() {
        this.response.resetBuffer();
    }

    @Override // javax.servlet.ServletResponse
    public void setLocale(Locale loc) {
        this.response.setLocale(loc);
    }

    @Override // javax.servlet.ServletResponse
    public Locale getLocale() {
        return this.response.getLocale();
    }
}
