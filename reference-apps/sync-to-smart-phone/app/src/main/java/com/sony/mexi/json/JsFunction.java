package com.sony.mexi.json;

import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class JsFunction implements Serializable {
    private static final long serialVersionUID = 1;

    public abstract JsValue invoke(JsArray jsArray);
}
