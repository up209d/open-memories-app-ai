package com.sony.mexi.orb.server;

import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.mexi.orb.servlet.MimeType;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;

/* loaded from: classes.dex */
public class OrbResponse implements HttpServletResponse {
    private OrbChunkedOutputStream chunkedOutStream;
    private OrbHttpServerConnection connection;
    private OrbDivisionOutputStream divisionOutStream;
    private HttpResponse res;
    private long contentLength = -1;
    private boolean committed = false;
    private OrbOutputStream outStream = new OrbOutputStream();
    private PrintWriter printWriter = new PrintWriter(this.outStream);
    private boolean chunked = false;
    private boolean division = false;

    public OrbResponse(HttpResponse res, OrbHttpServerConnection connection) {
        this.res = res;
        this.connection = connection;
    }

    protected String getHeader(String name) {
        Header header = this.res.getLastHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    @Override // javax.servlet.ServletResponse
    public String getCharacterEncoding() {
        return getHeader("Content-Encoding");
    }

    @Override // javax.servlet.ServletResponse
    public String getContentType() {
        return getHeader("Content-Type");
    }

    @Override // javax.servlet.ServletResponse
    public Locale getLocale() {
        return this.res.getLocale();
    }

    @Override // javax.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        return this.outStream;
    }

    @Override // javax.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        return this.printWriter;
    }

    @Override // javax.servlet.ServletResponse
    public boolean isCommitted() {
        return this.committed;
    }

    @Override // javax.servlet.ServletResponse
    public void setCharacterEncoding(String value) {
        if (!this.committed) {
            this.res.setHeader("Content-Encoding", value);
        }
    }

    @Override // javax.servlet.ServletResponse
    public void setContentLength(int value) {
        if (!this.committed) {
            this.contentLength = value;
            this.res.setHeader("Content-Length", Integer.toString(value));
        }
    }

    @Override // javax.servlet.ServletResponse
    public void setContentType(String value) {
        if (!this.committed) {
            this.res.setHeader("Content-Type", value);
        }
    }

    @Override // javax.servlet.ServletResponse
    public void setLocale(Locale loc) {
        if (!this.committed) {
            this.res.setLocale(loc);
        }
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        if (!this.committed) {
            this.res.addHeader(name, value);
        }
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        if (!this.committed) {
            this.res.addHeader(name, Integer.toString(value));
        }
    }

    @Override // javax.servlet.http.HttpServletResponse
    public boolean containsHeader(String name) {
        return this.res.containsHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    @Override // javax.servlet.ServletResponse
    public void reset() {
        if (this.committed) {
            throw new IllegalStateException();
        }
        this.res.setStatusCode(200);
        Header[] headers = this.res.getAllHeaders();
        for (Header header : headers) {
            this.res.removeHeader(header);
        }
        this.outStream = new OrbOutputStream();
        this.printWriter = new PrintWriter(this.outStream);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendError(int code) throws IOException {
        sendError(code, null);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendError(int code, String msg) throws IOException {
        if (this.committed) {
            throw new IllegalStateException();
        }
        reset();
        this.res.setStatusCode(code);
        if (msg != null) {
            setContentType(MimeType.TEXT_HTML);
            setContentLength(msg.getBytes(AutoSyncConstants.TEXT_ENCODING_UTF8).length);
            this.printWriter.print(msg);
        }
        commit();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        if (!this.committed) {
            this.res.setHeader(name, value);
        }
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        if (!this.committed) {
            this.res.setHeader(name, Integer.toString(value));
        }
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setStatus(int code) {
        this.res.setStatusCode(code);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setStatus(int code, String reason) {
        this.res.setStatusLine(this.res.getStatusLine().getProtocolVersion(), code, reason);
    }

    public void commit() {
        if (this.committed) {
            throw new IllegalStateException();
        }
        this.printWriter.flush();
        HttpEntity entity = new OrbEntityTemplate(new ContentProducer() { // from class: com.sony.mexi.orb.server.OrbResponse.1
            @Override // org.apache.http.entity.ContentProducer
            public void writeTo(OutputStream out) throws IOException {
                OrbResponse.this.outStream.writeTo(out);
            }
        });
        ((OrbEntityTemplate) entity).setContentLength(this.contentLength);
        ((EntityTemplate) entity).setContentType(getContentType());
        this.res.setEntity(entity);
        this.committed = true;
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeURL(String url) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long date) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long date) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletResponse
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletResponse
    public int getBufferSize() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletResponse
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override // javax.servlet.ServletResponse
    public void flushBuffer() throws IOException {
        if (this.division) {
            this.outStream.writeDivisionTo(this.divisionOutStream);
            this.outStream.flush();
        } else {
            if (this.chunked) {
                this.outStream.writeChunkTo(this.chunkedOutStream);
                this.outStream.flush();
                return;
            }
            throw new IllegalStateException();
        }
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendRedirect(String arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void sendResponseHeaders() throws IOException {
        try {
            this.connection.sendResponseHeader(this.res);
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    public void setChunked() {
        this.chunked = true;
        this.chunkedOutStream = this.connection.getChunkedOutputStream();
    }

    public void setDivision() {
        this.division = true;
        this.divisionOutStream = this.connection.getDivisionOutputStream();
    }

    public void unsetChunked() throws IOException {
        if (this.chunked) {
            this.connection.unsetChunked();
            this.chunkedOutStream.close();
            this.chunked = false;
        }
    }

    public void unsetDivision() throws IOException {
        if (this.division) {
            this.connection.unsetChunked();
            this.divisionOutStream.close();
            this.division = false;
        }
    }

    public void closeConnection() throws IOException {
        this.connection.shutdown();
    }
}
