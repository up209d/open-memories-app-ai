package com.sony.mexi.orb.servlet;

import com.sony.mexi.json.JsArray;
import com.sony.mexi.json.JsValue;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public abstract class OrbMethod implements Serializable {
    private static final long serialVersionUID = 1;

    public abstract JsValue invoke(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, JsArray jsArray);

    public abstract String[] parameterTypes();

    public abstract String[] resultTypes();

    public boolean hasContinuousCallbacks() {
        return false;
    }
}
