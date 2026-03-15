package com.sony.mexi.orb.service.http;

import com.sony.mexi.orb.server.OrbClient;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.orb.service.AuthenticationLevel;
import com.sony.mexi.orb.service.Authenticator;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class AuthenticatedHttpRequestHandler extends GenericHttpRequestHandler {
    private final Authenticator mAuthenticator;

    protected abstract AuthenticationLevel getAuthLevel(OrbClient orbClient);

    /* loaded from: classes.dex */
    private static class ServiceClient implements OrbClient {
        private final HttpRequest mRequest;

        public ServiceClient(HttpRequest request) {
            this.mRequest = request;
        }

        @Override // com.sony.mexi.orb.server.OrbClient
        public String getRequestHeader(String name) {
            return this.mRequest.getHeader(name);
        }

        @Override // com.sony.mexi.orb.server.OrbClient
        public String getRemoteAddress() {
            return this.mRequest.getRemoteAddress();
        }

        @Override // com.sony.mexi.orb.server.OrbClient
        public String getRequestURI() {
            return this.mRequest.getRequestURI();
        }

        @Override // com.sony.mexi.orb.server.OrbClient
        public void setResponseHeader(String name, String value) {
            throw new UnsupportedOperationException("SetResponseHeader is not supported in this context");
        }
    }

    public AuthenticatedHttpRequestHandler(Authenticator authenticator) {
        if (authenticator == null) {
            throw new IllegalArgumentException("Authenticator MUST NOT be null");
        }
        this.mAuthenticator = authenticator;
    }

    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public void onHttpRequest(HttpRequest req, HttpResponse res) throws IOException {
        ServiceClient client = new ServiceClient(req);
        if (isAuthorized(client)) {
            super.onHttpRequest(req, res);
        } else {
            res.sendStatusAsResponse(StatusCode.FORBIDDEN);
        }
    }

    private boolean isAuthorized(OrbClient client) {
        AuthenticationLevel authLevel = getAuthLevel(client);
        if (authLevel == AuthenticationLevel.NONE) {
            return true;
        }
        Authenticator.AuthResult result = this.mAuthenticator.checkAuthentication(authLevel.value(), client);
        return result.isAuthorized();
    }
}
