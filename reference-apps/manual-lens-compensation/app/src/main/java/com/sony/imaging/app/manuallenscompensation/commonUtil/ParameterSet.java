package com.sony.imaging.app.manuallenscompensation.commonUtil;

/* loaded from: classes.dex */
public class ParameterSet {
    String ApurtureValue;
    int ChromaticAberrationBlue;
    int ChromaticAberrationRed;
    int Distortion;
    int ShadingBlue;
    int ShadingBrightness;
    int ShadingRed;
    int decApurtureValue;
    int intApurtureValue;

    public ParameterSet(int intApurtureValue, int decApurtureValue, int ShadingBrightness, int ShadingRed, int ShadingBlue, int ChromaticAberrationRed, int ChromaticAberrationBlue, int Distortion) {
        this.intApurtureValue = intApurtureValue;
        this.decApurtureValue = decApurtureValue;
        this.ApurtureValue = String.valueOf(intApurtureValue) + "." + String.valueOf(decApurtureValue);
        this.ShadingBrightness = ShadingBrightness;
        this.ShadingRed = ShadingRed;
        this.ShadingBlue = ShadingBlue;
        this.ChromaticAberrationRed = ChromaticAberrationRed;
        this.ChromaticAberrationBlue = ChromaticAberrationBlue;
        this.Distortion = Distortion;
    }
}
