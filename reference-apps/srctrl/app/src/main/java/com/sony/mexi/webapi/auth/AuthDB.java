package com.sony.mexi.webapi.auth;

import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class AuthDB {
    private static final int FLAG_NONVOLATILE = 1;
    private static final char[] HEX_VALUE = "0123456789ABCDEF".toCharArray();
    private static final char KEY_DELIMITER = ':';
    public static final String NO_ORIGIN = "noorigin";
    private static final char VALUE_AUTHLEVELS_DELIMITER = ';';
    private static final char VALUE_DELIMITER = ',';
    private final AbstractSessionStorage mSessionStore;
    private final AbstractSessionIdGenerator mSidGenerator;

    public AuthDB(AbstractSessionStorage sessionStore, AbstractSessionIdGenerator sidGenerator) {
        this.mSessionStore = sessionStore;
        this.mSidGenerator = sidGenerator;
        if (sessionStore == null) {
            throw new RuntimeException("AuthDB: null sessionStore");
        }
        if (sidGenerator == null) {
            throw new RuntimeException("AuthDB: null SessionIdGenerator");
        }
    }

    public String add(List<String> sessionIds, String origin, List<String> authLevels, boolean isNonVolatile, int expiry) {
        String newSessionId;
        if (authLevels == null || authLevels.size() == 0 || expiry <= 0 || (newSessionId = toHex(this.mSidGenerator.generate())) == null) {
            return null;
        }
        int keyLength = newSessionId.length() + (origin == null ? 0 : origin.length());
        if (keyLength <= 0 || keyLength > 250) {
            return null;
        }
        String encodedOrigin = AuthUtil.encode(origin);
        if (sessionIds != null && !sessionIds.isEmpty()) {
            for (String sessionId : sessionIds) {
                delete(sessionId, origin);
                Map<String, String> sessionDump = this.mSessionStore.dump();
                for (Map.Entry<String, String> sessionEntry : sessionDump.entrySet()) {
                    String key = sessionEntry.getKey();
                    if (key.startsWith(sessionId) && key.lastIndexOf(58) == sessionId.length()) {
                        this.mSessionStore.delete(key);
                        String key2 = key.replace(sessionId, newSessionId);
                        String value = sessionEntry.getValue();
                        this.mSessionStore.set(key2, value, getTimeToExpiry(value));
                    }
                }
            }
        }
        int now = AuthUtil.now();
        long maxAge = now + expiry;
        String key3 = newSessionId + KEY_DELIMITER + encodedOrigin;
        StringBuilder value2 = new StringBuilder(100);
        value2.append(maxAge);
        value2.append(VALUE_DELIMITER);
        int flag = isNonVolatile ? 1 : 0;
        value2.append(flag);
        value2.append(VALUE_DELIMITER);
        boolean first = true;
        for (String authLevel : authLevels) {
            if (first) {
                first = false;
            } else {
                value2.append(VALUE_AUTHLEVELS_DELIMITER);
            }
            value2.append(authLevel);
        }
        this.mSessionStore.set(key3, value2.toString(), expiry);
        return newSessionId;
    }

    public String add(List<String> sessionIds, String origin, List<String> authLevels) {
        return add(sessionIds, origin, authLevels, true, 1209600);
    }

    public int delete(String sessionId, String origin) {
        String encodedOrigin = AuthUtil.encode(origin);
        return this.mSessionStore.delete(sessionId + KEY_DELIMITER + encodedOrigin);
    }

    public String[] get(String sessionId, String origin) {
        int pos;
        int pos2;
        if (sessionId == null) {
            return null;
        }
        String encodedOrigin = AuthUtil.encode(origin);
        String key = sessionId + KEY_DELIMITER + encodedOrigin;
        String value = this.mSessionStore.get(key);
        int expiration = getTimeToExpiry(value);
        if (expiration < 0 || (pos = value.indexOf(44)) == -1 || (pos2 = value.indexOf(44, pos + 1)) == -1 || pos2 >= value.length() - 1) {
            return null;
        }
        String authLevelsString = value.substring(pos2 + 1);
        return authLevelsString.split(String.valueOf(VALUE_AUTHLEVELS_DELIMITER));
    }

    public String allocateToken() {
        String token = toHex(this.mSidGenerator.generate());
        if (token == null) {
            return null;
        }
        long now = AuthUtil.now();
        long expiry = now + 86400;
        String value = String.valueOf(expiry) + VALUE_DELIMITER + "TOKEN";
        this.mSessionStore.set(token, value, AuthConfig.TOKEN_EXPIRATION);
        return token;
    }

    public void invalidateToken(String token) {
        this.mSessionStore.delete(token);
    }

    public boolean isValidToken(String token) {
        String value = this.mSessionStore.get(token);
        int timeToExpiry = getTimeToExpiry(value);
        if (timeToExpiry < 0 || !isToken(value)) {
            return false;
        }
        long now = AuthUtil.now();
        long expiry = now + 86400;
        String newValue = String.valueOf(expiry) + VALUE_DELIMITER + "TOKEN";
        this.mSessionStore.set(token, newValue, AuthConfig.TOKEN_EXPIRATION);
        return true;
    }

    public String dump(boolean nonVolatileOnly) {
        return null;
    }

    public void restore(String data) {
    }

    private int getTimeToExpiry(String value) {
        if (value == null) {
            return -1;
        }
        int index = value.indexOf(44);
        String expiryStr = value.substring(0, index);
        int expiry = Integer.parseInt(expiryStr);
        int nowSeconds = AuthUtil.now();
        int timeToExpiry = expiry - nowSeconds;
        if (timeToExpiry < 0) {
            timeToExpiry = -1;
        }
        return timeToExpiry;
    }

    private boolean isToken(String value) {
        int pos = value.indexOf(44);
        if (pos < -1) {
            return false;
        }
        String typeField = value.substring(pos + 1);
        return typeField.equals("TOKEN");
    }

    private String toHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder byteBuf = new StringBuilder(bytes.length * 2);
        char[] ch = new char[2];
        for (byte b : bytes) {
            int left = (b & LiveviewCommon.COMMON_HEADER_START_BYTE) >>> 4;
            ch[0] = HEX_VALUE[left];
            ch[1] = HEX_VALUE[b & 15];
            byteBuf.append(ch);
        }
        return byteBuf.toString();
    }
}
