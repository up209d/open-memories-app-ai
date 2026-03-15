package com.sony.mexi.orb.service;

import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class OrbAbstractMethod {
    public abstract void invoke(OrbAbstractClient orbAbstractClient, JSONArray jSONArray);

    public abstract String[] parameterTypes();

    public abstract String[] resultTypes();

    public String getAccessLevel() {
        return "none";
    }
}
