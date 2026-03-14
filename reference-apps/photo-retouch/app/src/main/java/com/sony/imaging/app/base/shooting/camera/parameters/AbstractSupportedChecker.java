package com.sony.imaging.app.base.shooting.camera.parameters;

/* loaded from: classes.dex */
public abstract class AbstractSupportedChecker implements ISupportedParameterChecker {
    public static final String ARROW = " -> ";
    public static final String COLON = ": ";
    public static final String SEPARATOR = ",";
    protected static String TAG = "SupportedChecker";
    private ThreadLocal<StringBuilder> mBuilders = new ThreadLocal<>();

    /* JADX INFO: Access modifiers changed from: protected */
    public StringBuilder getBuilder() {
        StringBuilder builder = this.mBuilders.get();
        if (builder == null) {
            StringBuilder builder2 = new StringBuilder();
            this.mBuilders.set(builder2);
            return builder2;
        }
        return builder;
    }
}
