package com.sony.mexi.webapi.auth;

/* loaded from: classes.dex */
public class AuthManager {
    private AuthDB mAuthDB;

    private AuthManager() {
    }

    public AuthManager(AuthDB authDB) {
        this();
        this.mAuthDB = authDB;
    }

    public AuthManager(AbstractSessionStorage sessionStore, AbstractSessionIdGenerator sidGenerator) {
        this(new AuthDB(sessionStore, sidGenerator));
    }

    public boolean isAuthorized(String token, String sessionId, String origin, String accessLevel) {
        if (this.mAuthDB == null) {
            return true;
        }
        if (token != null) {
            return this.mAuthDB.isValidToken(token);
        }
        if (sessionId == null || accessLevel == null) {
            return false;
        }
        String[] accessLevels = this.mAuthDB.get(sessionId, origin);
        if (accessLevels == null) {
            return false;
        }
        for (String allowedLevel : accessLevels) {
            if (accessLevel.equals(allowedLevel)) {
                return true;
            }
        }
        return false;
    }
}
