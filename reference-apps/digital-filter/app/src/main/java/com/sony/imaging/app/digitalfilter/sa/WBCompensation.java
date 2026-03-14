package com.sony.imaging.app.digitalfilter.sa;

import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.digitalfilter.common.AppLog;

/* loaded from: classes.dex */
public class WBCompensation {
    public double mR = Double.NaN;
    public double mB = Double.NaN;
    public double mAB = Double.NaN;
    public double mGM = Double.NaN;

    private double log2(double a) {
        double log10_2 = Math.log10(2.0d);
        return Math.log10(a) / log10_2;
    }

    public void calcWBabgm(double R, double B) {
        double C1;
        double ab = (1.0d / ((-0.05238095238095238d) - 0.04523809523809524d)) * (((-2.0d) - log2(R)) + log2(B));
        double gm = (1.0d / ((-0.05238095238095238d) - 0.04523809523809524d)) * (((log2(R) - 8.0d) * (-0.05238095238095238d)) - ((log2(B) - 10.0d) * 0.04523809523809524d));
        if (gm >= 0.0d) {
            C1 = 0.04285714285714286d;
        } else {
            C1 = -(-0.07142857142857142d);
        }
        double gm2 = gm / C1;
        this.mAB = ab;
        this.mGM = gm2;
        this.mR = R;
        this.mB = B;
        AppLog.info("taka", "[" + ab + "," + gm2 + "] = calcWBabgm(" + R + "," + B + LogHelper.MSG_CLOSE_BRACKET);
    }

    public void calcWBrb(double ab, double gm) {
        double m;
        double g;
        if (gm >= 0.0d) {
            m = gm;
            g = 0.0d;
        } else {
            m = 0.0d;
            g = -gm;
        }
        double R = Math.pow(2.0d, 8.0d + ((-0.07142857142857142d) * g) + (0.04285714285714286d * m) + (0.04523809523809524d * ab));
        double B = Math.pow(2.0d, 10.0d + ((-0.07142857142857142d) * g) + (0.04285714285714286d * m) + ((-0.05238095238095238d) * ab));
        this.mR = R;
        this.mB = B;
        this.mAB = ab;
        this.mGM = gm;
        AppLog.info("taka", "[" + R + "," + B + "] = calcWBrb(" + ab + "," + gm + LogHelper.MSG_CLOSE_BRACKET);
    }
}
