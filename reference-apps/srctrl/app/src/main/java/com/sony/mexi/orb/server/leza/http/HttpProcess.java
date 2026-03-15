package com.sony.mexi.orb.server.leza.http;

import com.sony.mexi.orb.server.OrbServiceGroup;
import com.sony.mexi.orb.server.OrbTransport;
import com.sony.mexi.orb.server.OriginalRemoteAddressHandler;
import com.sony.mexi.orb.server.PathValidator;
import com.sony.mexi.orb.server.SupportedServiceGpHolder;
import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.orb.server.http.OrbHttpHandler;
import com.sony.mexi.orb.server.http.OrbHttpHandlerGroup;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.orb.server.leza.ServerProcess;
import com.sony.mexi.server.jni.HttpServerJni;
import com.sony.mexi.server.jni.HttpServletJni;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/* loaded from: classes.dex */
public class HttpProcess implements ServerProcess {
    private static final String PROTOCOL = "xhrpost:jsonizer";
    private static final OrbTransport SERVER_PROTOCOL = OrbTransport.HTTP;
    private static final String TYPE = "servlet";
    private ExecutorService mExecutorService;
    private OrbHttpHandlerGroup mHandlerGroup;
    private boolean mIsCORSSupported;
    private OriginalRemoteAddressHandler mRemoteAddressHandler;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OrbHttpHandleRunner implements Runnable {
        OrbHttpHandler mHandler;
        HttpRequest mRequest;
        HttpResponse mResponse;

        OrbHttpHandleRunner(OrbHttpHandler handler, HttpRequest request, HttpResponse response) {
            this.mHandler = handler;
            this.mRequest = request;
            this.mResponse = response;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mHandler.serve(this.mRequest, this.mResponse);
        }
    }

    public HttpProcess(OrbHttpHandlerGroup handlerGroup, ExecutorService executorService, OriginalRemoteAddressHandler remoteAddressHandler, boolean isCORSSupported) {
        this.mHandlerGroup = null;
        this.mHandlerGroup = handlerGroup;
        this.mExecutorService = executorService;
        this.mRemoteAddressHandler = remoteAddressHandler;
        this.mIsCORSSupported = isCORSSupported;
    }

    @Override // com.sony.mexi.orb.server.leza.ServerProcess
    public void init(HttpServerJni httpServerJni, final int maxBodySize) {
        httpServerJni.setOnHttpRequestListener(new HttpServerJni.OnHttpRequestListener() { // from class: com.sony.mexi.orb.server.leza.http.HttpProcess.1
            @Override // com.sony.mexi.server.jni.HttpServerJni.OnHttpRequestListener
            public void onRequest(HttpServletJni httpServletJni) {
                HttpRequest httpRequest = new HttpRequest(httpServletJni, HttpProcess.this.mRemoteAddressHandler, maxBodySize);
                HttpResponse httpResponse = new HttpResponse(httpServletJni);
                httpResponse.setHeader(HttpDefs.HEADER_CONNECTION, HttpDefs.VALUE_CLOSE);
                HttpProcess.this.handleRequest(httpRequest, httpResponse);
            }
        });
        this.mHandlerGroup.init();
        this.mHandlerGroup.initServiceInformation(protocol());
        SupportedServiceGpHolder.getInstance().addServiceGp(this.mHandlerGroup);
    }

    @Override // com.sony.mexi.orb.server.leza.ServerProcess
    public void shutdown() {
        SupportedServiceGpHolder.getInstance().removeServiceGp(this.mHandlerGroup);
    }

    @Override // com.sony.mexi.orb.server.OrbProcess
    public OrbTransport getTransport() {
        return SERVER_PROTOCOL;
    }

    @Override // com.sony.mexi.orb.server.OrbProcess
    public String protocol() {
        return "xhrpost:jsonizer";
    }

    @Override // com.sony.mexi.orb.server.OrbProcess
    public OrbServiceGroup getServiceGroup() {
        return this.mHandlerGroup;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRequest(HttpRequest request, HttpResponse response) {
        try {
            boolean isPreflight = processCORS(request, response);
            if (!isPreflight) {
                OrbHttpHandler handler = lookupHandler(request.getRequestURI());
                if (handler == null) {
                    sendError(response, StatusCode.NOT_FOUND);
                } else if (request.continueWithRequest()) {
                    OrbHttpHandleRunner httpHandleRunner = new OrbHttpHandleRunner(handler, request, response);
                    try {
                        this.mExecutorService.execute(httpHandleRunner);
                    } catch (RejectedExecutionException e) {
                        sendError(response, StatusCode.SERVICE_UNAVAILABLE);
                    }
                }
            }
        } catch (IOException e2) {
        }
    }

    private OrbHttpHandler lookupHandler(String uri) {
        try {
            String path = PathValidator.toNormalizedPath(uri);
            OrbHttpHandler handler = this.mHandlerGroup.getHandlerMap().get(path);
            if (handler == null) {
                for (Map.Entry<String, ? extends OrbHttpHandler> entry : this.mHandlerGroup.getWildcardedHandlerMap().entrySet()) {
                    if (path.equals(entry.getKey()) || path.startsWith(entry.getKey() + "/")) {
                        return entry.getValue();
                    }
                }
                return null;
            }
            return handler;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private boolean processCORS(HttpRequest request, HttpResponse response) throws IOException {
        if (!this.mIsCORSSupported) {
            return false;
        }
        String method = request.getMethod();
        String originHeader = request.getHeader(HttpDefs.HEADER_ORIGIN);
        if (originHeader != null) {
            response.setHeader(HttpDefs.HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, originHeader);
            response.setHeader(HttpDefs.HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
        if (!method.equals(HttpDefs.METHOD_OPTIONS)) {
            return false;
        }
        if (originHeader != null) {
            String allowMethods = request.getHeader(HttpDefs.HEADER_ACCESS_CONTROL_REQUEST_METHOD);
            if (allowMethods != null) {
                response.setHeader(HttpDefs.HEADER_ACCESS_CONTROL_ALLOW_METHODS, allowMethods);
            }
            String allowHeaders = request.getHeader(HttpDefs.HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
            if (allowHeaders != null) {
                response.setHeader(HttpDefs.HEADER_ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
            }
            response.sendStatusAsResponse(StatusCode.OK);
            response.end();
        } else {
            response.sendStatusAsResponse(StatusCode.NOT_IMPLEMENTED);
            response.end();
        }
        return true;
    }

    private void sendError(HttpResponse response, StatusCode statusCode) {
        try {
            response.sendStatusAsResponse(statusCode);
        } catch (IOException e) {
        }
    }
}
