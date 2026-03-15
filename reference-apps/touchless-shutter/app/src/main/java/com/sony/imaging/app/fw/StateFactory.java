package com.sony.imaging.app.fw;

/* loaded from: classes.dex */
public abstract class StateFactory extends BaseFactory {
    @Override // com.sony.imaging.app.fw.BaseFactory
    public abstract void init();

    @Override // com.sony.imaging.app.fw.BaseFactory
    public State get(String name) {
        return (State) super.get(name);
    }
}
