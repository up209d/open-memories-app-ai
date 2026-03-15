package com.sony.mexi.orb.service;

import com.sony.mexi.orb.server.OrbClient;
import com.sony.mexi.orb.server.http.HttpDefs;
import com.sony.mexi.webapi.auth.AbstractSessionIdGenerator;
import com.sony.mexi.webapi.auth.AbstractSessionStorage;
import com.sony.mexi.webapi.auth.AuthCookie;
import com.sony.mexi.webapi.auth.AuthDB;
import com.sony.mexi.webapi.auth.AuthManager;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class Authenticator {
    private static final String AUTH_PSK_HEADER = "x-auth-psk";
    private static final String AUTH_PSK_QUERY = "webapi_auth_psk";
    private static final String AUTH_TOKEN_HEADER = "x-application-token";
    private static final String AUTH_TOKEN_QUERY = "webapi_access_token";
    private final AuthDB mAuthDB;
    private AuthErrorHandler mAuthErrorHandler;
    private final AuthManager mAuthManager;
    private final EnumSet<AuthMode> mAuthModes;
    private String mValidPSK;

    /* loaded from: classes.dex */
    public interface AuthErrorHandler {
        Map<String, String> getAuthURL();
    }

    public Authenticator(AbstractSessionStorage sessionStore, AbstractSessionIdGenerator sidGenerator, List<String> authModes) {
        this(sessionStore, sidGenerator, authModes, null);
    }

    public Authenticator(AbstractSessionStorage sessionStore, AbstractSessionIdGenerator sidGenerator, List<String> authModes, String psk) {
        this.mAuthDB = new AuthDB(sessionStore, sidGenerator);
        this.mAuthManager = new AuthManager(this.mAuthDB);
        this.mAuthModes = EnumSet.noneOf(AuthMode.class);
        this.mValidPSK = null;
        this.mAuthErrorHandler = null;
        for (String authMode : authModes) {
            if (authMode.equals("TOKEN")) {
                this.mAuthModes.add(AuthMode.TOKEN);
            } else if (authMode.equals("COOKIE")) {
                this.mAuthModes.add(AuthMode.COOKIE);
            } else if (authMode.equals("PSK")) {
                this.mAuthModes.add(AuthMode.PSK);
                this.mValidPSK = psk;
            }
        }
    }

    @Deprecated
    public String createSessionId(String sessionId, String origin, List<String> authLevels) {
        return createSessionId(Arrays.asList(sessionId), origin, authLevels);
    }

    @Deprecated
    public String createSessionId(String sessionId, String origin, List<String> authLevels, boolean isNonVolatile, int expiry) {
        return createSessionId(Arrays.asList(sessionId), origin, authLevels, isNonVolatile, expiry);
    }

    public String createSessionId(List<String> sessionIds, String origin, List<String> authLevels, boolean isNonVolatile, int expiry) {
        return this.mAuthDB.add(sessionIds, origin, authLevels, isNonVolatile, expiry);
    }

    public String createSessionId(List<String> sessionIds, String origin, List<String> authLevels) {
        return this.mAuthDB.add(sessionIds, origin, authLevels);
    }

    public String createToken() {
        return this.mAuthDB.allocateToken();
    }

    /* loaded from: classes.dex */
    public static class AuthResult {
        private AuthMode authorizedMode;
        private final EnumSet<AuthMode> checkedModes = EnumSet.noneOf(AuthMode.class);
        private boolean isAuthorized;
        private String sessionId;

        /* JADX INFO: Access modifiers changed from: private */
        public void setAuthorized(boolean isAuthorized, AuthMode authorizedMode) {
            this.isAuthorized = isAuthorized;
            this.authorizedMode = authorizedMode;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addCheckedAuthMode(AuthMode mode) {
            this.checkedModes.add(mode);
        }

        public boolean isAuthorized() {
            return this.isAuthorized;
        }

        public AuthMode getAuthorizedMode() {
            return this.authorizedMode;
        }

        public String getSessionId() {
            return this.sessionId;
        }

        public EnumSet<AuthMode> getCheckedAuthModes() {
            return this.checkedModes;
        }
    }

    public AuthResult checkAuthentication(String accessLevel, OrbClient client) {
        String remoteAddress = client.getRemoteAddress();
        return checkAuthentication(accessLevel, client, Boolean.valueOf(isLocalHost(remoteAddress)));
    }

    public AuthResult checkAuthentication(String accessLevel, OrbClient client, Boolean isLocalAccess) {
        AuthResult ret = new AuthResult();
        if (this.mAuthModes.isEmpty()) {
            ret.setAuthorized(true, AuthMode.NONE);
        } else {
            Map<String, String> queryList = getQuery(client.getRequestURI());
            if (this.mAuthModes.contains(AuthMode.PSK)) {
                ret.addCheckedAuthMode(AuthMode.PSK);
                String psk = null;
                if (queryList != null) {
                    String psk2 = queryList.get(AUTH_PSK_QUERY);
                    psk = psk2;
                }
                if (psk == null) {
                    psk = client.getRequestHeader(AUTH_PSK_HEADER);
                }
                if (psk != null && psk.equals(this.mValidPSK)) {
                    ret.setAuthorized(true, AuthMode.PSK);
                }
            }
            String token = null;
            if (this.mAuthModes.contains(AuthMode.TOKEN) && isLocalAccess.booleanValue()) {
                ret.addCheckedAuthMode(AuthMode.TOKEN);
                if (queryList != null) {
                    token = queryList.get(AUTH_TOKEN_QUERY);
                }
                if (token == null) {
                    token = client.getRequestHeader(AUTH_TOKEN_HEADER);
                }
                if (token != null && token.length() == 0) {
                    token = null;
                }
            }
            List<String> sessionIds = Collections.emptyList();
            String origin = client.getRequestHeader(HttpDefs.HEADER_ORIGIN);
            if (this.mAuthModes.contains(AuthMode.COOKIE)) {
                ret.addCheckedAuthMode(AuthMode.COOKIE);
                String cookies = client.getRequestHeader("cookie");
                sessionIds = AuthCookie.getValues(cookies);
            }
            boolean isAuthorized = false;
            String authorizedSessionId = null;
            if (sessionIds.isEmpty()) {
                isAuthorized = this.mAuthManager.isAuthorized(token, null, origin, accessLevel);
            } else {
                Iterator i$ = sessionIds.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    String sessionId = i$.next();
                    isAuthorized = this.mAuthManager.isAuthorized(token, sessionId, origin, accessLevel);
                    if (isAuthorized) {
                        authorizedSessionId = sessionId;
                        break;
                    }
                }
            }
            if (!isAuthorized || token == null) {
                ret.setAuthorized(isAuthorized, AuthMode.COOKIE);
                ret.setSessionId(authorizedSessionId);
            } else {
                ret.setAuthorized(true, AuthMode.TOKEN);
            }
        }
        return ret;
    }

    private boolean isLocalHost(String address) {
        try {
            InetAddress addr = InetAddress.getByName(address);
            return addr.isLoopbackAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private Map<String, String> getQuery(String uri) {
        try {
            URI aURI = new URI(uri);
            String queryString = aURI.getQuery();
            if (queryString == null) {
                return null;
            }
            Map<String, String> queryList = new HashMap<>();
            String[] queries = queryString.split("&");
            for (String query : queries) {
                int delimIndex = query.indexOf(61);
                if (delimIndex < 0) {
                    queryList.put(query, "");
                } else {
                    String key = query.substring(0, delimIndex);
                    String value = query.substring(delimIndex + 1);
                    queryList.put(key, value);
                }
            }
            return queryList;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public AuthErrorHandler getAuthErrorHandler() {
        return this.mAuthErrorHandler;
    }

    public void setAuthErrorHandler(AuthErrorHandler mAuthErrorHandler) {
        this.mAuthErrorHandler = mAuthErrorHandler;
    }
}
