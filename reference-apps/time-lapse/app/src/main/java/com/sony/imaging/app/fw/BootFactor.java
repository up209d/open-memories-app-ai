package com.sony.imaging.app.fw;

/* loaded from: classes.dex */
public class BootFactor {
    public static final int APO = 1;
    public static final int LUNCHER = 0;
    public static final int POWERON = 2;
    private static final String _APO = "APO";
    private static final String _PON = "PON";
    private static String mBootKey;
    public int bootFactor;
    public String bootKey;

    BootFactor(String factor) {
        this.bootKey = factor;
        this.bootFactor = 0;
        if (factor != null) {
            int count = factor.length();
            String bootFactorString = factor.substring(count - 3, count);
            if (bootFactorString.equals(_PON)) {
                this.bootFactor = 2;
            } else if (bootFactorString.equals(_APO)) {
                this.bootFactor = 1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setBootFactor(String b) {
        mBootKey = b;
    }

    public static BootFactor get() {
        return new BootFactor(mBootKey);
    }
}
