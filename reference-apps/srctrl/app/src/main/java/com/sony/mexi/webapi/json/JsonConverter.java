package com.sony.mexi.webapi.json;

import org.json.JSONObject;

/* loaded from: classes.dex */
public interface JsonConverter<T> {
    T fromJson(JSONObject jSONObject);

    JSONObject toJson(T t);
}
