package com.sony.mexi.webapi.auth;

import java.util.Map;

/* loaded from: classes.dex */
public interface AbstractSessionStorage {
    int delete(String str);

    Map<String, String> dump();

    String get(String str);

    int set(String str, String str2, int i);
}
