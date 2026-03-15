package com.sony.mexi.orb.server.leza.http;

import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.mexi.orb.server.OriginalRemoteAddressHandler;
import com.sony.mexi.orb.server.RequestHeaders;
import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.orb.server.http.OrbHttpRequest;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.server.jni.HttpServletJni;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
public class HttpRequest implements OrbHttpRequest {
    private HttpServletJni mHttpServletJni;
    private long mMaxBodySize;
    private OriginalRemoteAddressHandler mRemoteAddressHandler;
    private long mCurrentBodySize = 0;
    private final Set<OrbHttpRequest.DisconnectListener> mDisconnectListeners = new HashSet();
    private boolean mIsClosed = false;
    private HttpRequestInputStream mHttpInputStream = new HttpRequestInputStream();

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpRequest(HttpServletJni httpServletJni, OriginalRemoteAddressHandler remoteAddressHandler, int maxBodySize) {
        this.mHttpServletJni = httpServletJni;
        this.mRemoteAddressHandler = remoteAddressHandler;
        this.mMaxBodySize = maxBodySize;
        initializeListeners();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getCharset() {
        String contentType = getHeader(HttpDefs.HEADER_CONTENT_TYPE);
        if (contentType == null) {
            return null;
        }
        String[] sepa = contentType.split(MovieFormatController.Settings.SEMI_COLON);
        for (String val : sepa) {
            if (val.toLowerCase(Locale.US).startsWith("charset=")) {
                return val.substring("charset=".length());
            }
        }
        return null;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public int getContentLength() {
        try {
            return unsafeGetContentLength();
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    int unsafeGetContentLength() throws NumberFormatException {
        String contentLengthValue = getHeader(HttpDefs.HEADER_CONTENT_LENGTH);
        if (contentLengthValue != null) {
            return Integer.parseInt(contentLengthValue);
        }
        return -1;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getHeader(String name) {
        return this.mHttpServletJni.getHeader(name);
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getLocalAddress() {
        return this.mHttpServletJni.getServerAddress();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public int getLocalPort() {
        return this.mHttpServletJni.getServerPort();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getMethod() {
        return this.mHttpServletJni.getRequestMethod();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getRemoteAddress() {
        if (this.mRemoteAddressHandler != null) {
            HttpRequestHeader reqHeader = new HttpRequestHeader();
            String remoteAddress = this.mRemoteAddressHandler.getRemoteAddress(getRequestURI(), reqHeader);
            if (remoteAddress != null) {
                return remoteAddress;
            }
        }
        return this.mHttpServletJni.getRemoteAddress();
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public InputStream getInputStream() {
        return this.mHttpInputStream;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public boolean addDisconnectListener(OrbHttpRequest.DisconnectListener listener) {
        boolean add;
        synchronized (this.mDisconnectListeners) {
            add = this.mIsClosed ? false : this.mDisconnectListeners.add(listener);
        }
        return add;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public boolean removeDisconnectListener(OrbHttpRequest.DisconnectListener listener) {
        boolean remove;
        synchronized (this.mDisconnectListeners) {
            remove = this.mIsClosed ? false : this.mDisconnectListeners.remove(listener);
        }
        return remove;
    }

    @Override // com.sony.mexi.orb.server.http.OrbHttpRequest
    public String getRequestURI() {
        return this.mHttpServletJni.getUrl();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean continueWithRequest() {
        try {
            int contentLength = unsafeGetContentLength();
            if (contentLength == -1 || this.mMaxBodySize < 0 || contentLength <= this.mMaxBodySize) {
                return true;
            }
            sendError(StatusCode.REQUEST_ENTITY_TOO_LARGE);
            return false;
        } catch (NumberFormatException e) {
            sendError(StatusCode.BAD_REQUEST);
            return false;
        }
    }

    private void initializeListeners() {
        this.mHttpServletJni.setOnIncomingDataListener(new HttpServletJni.OnIncomingDataListener() { // from class: com.sony.mexi.orb.server.leza.http.HttpRequest.1
            @Override // com.sony.mexi.server.jni.HttpServletJni.OnIncomingDataListener
            public void onEnd() {
                HttpRequest.this.mHttpInputStream.end();
            }

            @Override // com.sony.mexi.server.jni.HttpServletJni.OnIncomingDataListener
            public void onData(byte[] chunk) {
                HttpRequest.this.mCurrentBodySize += chunk.length;
                if (HttpRequest.this.mMaxBodySize < 0 || HttpRequest.this.mCurrentBodySize <= HttpRequest.this.mMaxBodySize) {
                    HttpRequest.this.mHttpInputStream.write(chunk);
                } else {
                    HttpRequest.this.sendError(StatusCode.REQUEST_ENTITY_TOO_LARGE);
                }
            }
        });
        this.mHttpServletJni.addOnCloseListener(new HttpServletJni.OnCloseListener() { // from class: com.sony.mexi.orb.server.leza.http.HttpRequest.2
            @Override // com.sony.mexi.server.jni.HttpServletJni.OnCloseListener
            public void onClose() {
                HttpRequest.this.mHttpInputStream.close();
                synchronized (HttpRequest.this.mDisconnectListeners) {
                    HttpRequest.this.mIsClosed = true;
                    for (OrbHttpRequest.DisconnectListener listener : HttpRequest.this.mDisconnectListeners) {
                        listener.invoke();
                    }
                    HttpRequest.this.mDisconnectListeners.clear();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendError(StatusCode status) {
        this.mHttpServletJni.setHeader(HttpDefs.HEADER_CONNECTION, HttpDefs.VALUE_CLOSE);
        this.mHttpServletJni.setHeader(HttpDefs.HEADER_CONTENT_LENGTH, ISOSensitivityController.ISO_AUTO);
        this.mHttpServletJni.writeHead(status.toCode(), status.toReasonPhrase());
        this.mHttpServletJni.end();
    }

    /* loaded from: classes.dex */
    private class HttpRequestHeader implements RequestHeaders {
        private HttpRequestHeader() {
        }

        @Override // com.sony.mexi.orb.server.RequestHeaders
        public String getHeader(String name) {
            return HttpRequest.this.getHeader(name);
        }
    }
}
