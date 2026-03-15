package com.sony.mexi.webapi.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/* loaded from: classes.dex */
public final class AuthUtil {
    private static final String SYM_UTF8 = "UTF8";

    private AuthUtil() {
    }

    public static int now() {
        return (int) (new Date().getTime() / 1000);
    }

    public static String encode(String str) {
        if (str == null) {
            return "";
        }
        try {
            return URLEncoder.encode(str, SYM_UTF8);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
