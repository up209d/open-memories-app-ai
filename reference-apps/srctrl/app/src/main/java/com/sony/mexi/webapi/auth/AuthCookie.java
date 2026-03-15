package com.sony.mexi.webapi.auth;

import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class AuthCookie {
    private int mExpiry;
    private String mPath;
    private String mSessionId;

    private static int indexOfAuth(String cookies, int index) {
        char skip;
        if (cookies == null || cookies.length() <= index) {
            return -1;
        }
        int iterator = index;
        boolean found = false;
        boolean trim = true;
        boolean match = false;
        while (!found && iterator < cookies.length()) {
            char ch = cookies.charAt(iterator);
            if (ch == ';' || ch == ',') {
                if (match) {
                    return -1;
                }
                trim = true;
                match = false;
                iterator++;
            } else if (match && ch == '=') {
                found = true;
                iterator++;
            } else if (trim && ch == ' ') {
                iterator++;
            } else {
                trim = false;
                if (match || !cookies.startsWith(AuthConfig.COOKIE_NAME, iterator)) {
                    do {
                        iterator++;
                        if (iterator < cookies.length() && (skip = cookies.charAt(iterator)) != ';') {
                        }
                    } while (skip != ',');
                } else {
                    match = true;
                    trim = true;
                    iterator += AuthConfig.COOKIE_NAME.length();
                }
            }
        }
        if (!found || iterator == cookies.length()) {
            return -1;
        }
        return iterator;
    }

    private static String getValue(String cookies, int startIndex) {
        boolean blank = false;
        boolean token = false;
        for (int iterator = startIndex; iterator < cookies.length(); iterator++) {
            char val = cookies.charAt(iterator);
            switch (val) {
                case ' ':
                    blank = true;
                    break;
                case '\"':
                    if (token) {
                        return null;
                    }
                    int iterator2 = iterator + 1;
                    while (iterator2 < cookies.length() && cookies.charAt(iterator2) != '\"') {
                        iterator2++;
                    }
                    if (iterator2 < cookies.length()) {
                        return cookies.substring(iterator2, iterator2);
                    }
                    return null;
                case HandoffOperationInfo.GET_CONTENT_COUNT /* 44 */:
                case HandoffOperationInfo.STOP_LIVEVIEW /* 59 */:
                    return cookies.substring(startIndex, iterator).trim();
                default:
                    if (!token || !blank) {
                        token = true;
                        break;
                    } else {
                        return null;
                    }
            }
        }
        return cookies.substring(startIndex).trim();
    }

    public static List<String> getValues(String cookies) {
        if (cookies == null) {
            return Collections.emptyList();
        }
        ArrayList<String> list = new ArrayList<>();
        int index = 0;
        while (true) {
            int index2 = indexOfAuth(cookies, index);
            if (index2 != -1) {
                String value = getValue(cookies, index2);
                if (value == null) {
                    return Collections.emptyList();
                }
                list.add(value);
                index = index2 + value.length();
            } else {
                return Collections.unmodifiableList(list);
            }
        }
    }

    private AuthCookie() {
    }

    public AuthCookie(String sessionId, String path) {
        this(sessionId, path, 1209600);
    }

    public AuthCookie(String sessionId, String path, int expiry) {
        this();
        this.mSessionId = sessionId;
        this.mPath = path;
        this.mExpiry = expiry;
    }

    public String sessionId() {
        return this.mSessionId;
    }

    public String path() {
        return this.mPath;
    }

    public int getMaxAge() {
        return this.mExpiry;
    }

    public String toSetCookieValue() {
        if (this.mSessionId == null) {
            return null;
        }
        StringBuffer setCookie = new StringBuffer(100);
        setCookie.append(AuthConfig.COOKIE_NAME);
        setCookie.append('=');
        setCookie.append(this.mSessionId);
        setCookie.append(';');
        setCookie.append(" Path=");
        setCookie.append(this.mPath);
        setCookie.append(';');
        setCookie.append(" Max-Age=");
        setCookie.append(this.mExpiry);
        setCookie.append(';');
        if (this.mExpiry >= 0) {
            setCookie.append(" Expires=");
            Date dt = new Date();
            long time = dt.getTime();
            Date dt1 = new Date((this.mExpiry * 1000) + time);
            DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            setCookie.append(df.format(dt1));
        }
        return setCookie.toString();
    }
}
