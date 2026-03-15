package com.sony.imaging.app.manuallenscompensation.commonUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/* loaded from: classes.dex */
public class ByteDataAnalyser {
    byte[] dataBytes;
    boolean isValidProfile = true;

    public void setProfileValidation(boolean valid) {
        this.isValidProfile = valid;
    }

    public ByteDataAnalyser(byte[] dataBytes) {
        this.dataBytes = null;
        if (dataBytes.length == 496) {
            this.dataBytes = dataBytes;
        } else {
            this.dataBytes = null;
        }
    }

    public void setDataBytes(byte[] setdataBytes) {
        this.dataBytes = setdataBytes;
    }

    public byte[] getDataBytes() {
        return this.dataBytes;
    }

    public String getAppVersion() {
        byte app_major_byte = this.dataBytes[0];
        byte app_minor_byte = this.dataBytes[1];
        return String.valueOf((int) app_major_byte) + "." + String.valueOf((int) app_minor_byte);
    }

    public String getDbVersion() {
        byte db_major_byte = this.dataBytes[2];
        byte db_minor_byte = this.dataBytes[3];
        return String.valueOf((int) db_major_byte) + "." + String.valueOf((int) db_minor_byte);
    }

    public Short getNextVersionOffset() {
        byte[] version_offset = {this.dataBytes[4], this.dataBytes[5]};
        ByteBuffer bb = ByteBuffer.wrap(version_offset).order(ByteOrder.BIG_ENDIAN);
        ShortBuffer sb = bb.asShortBuffer();
        return Short.valueOf(sb.get());
    }

    public Short getLensNameLength() {
        byte[] name_length = {this.dataBytes[6], this.dataBytes[7]};
        ByteBuffer bb = ByteBuffer.wrap(name_length).order(ByteOrder.BIG_ENDIAN);
        ShortBuffer sb = bb.asShortBuffer();
        return Short.valueOf(sb.get());
    }

    public String getLensName() {
        byte[] lense_name = new byte[getLensNameLength().shortValue()];
        System.arraycopy(this.dataBytes, 8, lense_name, 0, getLensNameLength().shortValue());
        try {
            String str = new String(lense_name, "UTF-8");
            return str;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getMinFNumberFlag() {
        byte flag = this.dataBytes[OCConstants.THREE_NINETY_FIVE];
        return flag;
    }

    public int getMaxFNumberFlag() {
        byte flag = this.dataBytes[OCConstants.FOUR_HUNDRED_THREE];
        return flag;
    }

    public int getFocalLengthFlag() {
        byte flag = this.dataBytes[OCConstants.THREE_NINETY_TWO];
        return flag;
    }

    public Short getFocalLength() {
        byte[] focal_length = {this.dataBytes[OCConstants.THREE_NINETY_THREE], this.dataBytes[OCConstants.THREE_NINETY_FOUR]};
        ByteBuffer bb = ByteBuffer.wrap(focal_length).order(ByteOrder.BIG_ENDIAN);
        ShortBuffer sb = bb.asShortBuffer();
        return Short.valueOf(sb.get());
    }

    public String getMinFNumber() {
        if (getMinFNumberFlag() == 0) {
            return null;
        }
        byte integer = this.dataBytes[OCConstants.THREE_NINETY_SIX];
        byte decimal = this.dataBytes[OCConstants.THREE_NINETY_SEVEN];
        double dec_val = integer + (decimal / 100.0d);
        return String.valueOf(dec_val);
    }

    public int getFMinLightVignettingCorrection() {
        byte flag = this.dataBytes[OCConstants.THREE_NINETY_EIGHT];
        return flag;
    }

    public int getFMinRedColorVignettingCorrection() {
        byte flag = this.dataBytes[OCConstants.THREE_NINETY_NINE];
        return flag;
    }

    public int getFMinBlueColorVignettingCorrection() {
        byte flag = this.dataBytes[OCConstants.FOUR_HUNDRED];
        return flag;
    }

    public int getFMinRedChromaticAberationCorrection() {
        byte flag = this.dataBytes[OCConstants.FOUR_HUNDRED_ONE];
        return flag;
    }

    public int getFMinBlueChromaticAberationCorrection() {
        byte flag = this.dataBytes[OCConstants.FOUR_HUNDRED_TWO];
        return flag;
    }

    public int getFMinDistortionCorrection() {
        byte flag = this.dataBytes[OCConstants.FOUR_HUNDRED_THREE];
        return flag;
    }

    public String getMaxFNumber() {
        byte integer = this.dataBytes[OCConstants.FOUR_HUNDRED_SIX];
        byte decimal = this.dataBytes[OCConstants.FOUR_HUNDRED_SEVEN];
        return String.valueOf((int) integer) + "." + String.valueOf((int) decimal);
    }

    public String getEvStepValue() {
        int CASE = this.dataBytes[OCConstants.FOUR_HUNDRED_EIGHT];
        switch (CASE) {
            case 1:
                String mReturnEvStepVal = OCConstants.EVSTEP_ZERO_TO_THREE;
                return mReturnEvStepVal;
            case 2:
                String mReturnEvStepVal2 = OCConstants.EVSTEP_ZERO_TO_FIVE;
                return mReturnEvStepVal2;
            case 3:
                String mReturnEvStepVal3 = OCConstants.EVSTEP_ONE_TO_ZERO;
                return mReturnEvStepVal3;
            default:
                return null;
        }
    }

    public int getParameterCount() {
        byte flag = this.dataBytes[OCConstants.FOUR_HUNDRED_NINE];
        return flag;
    }

    public ParameterSet getParameterSet(int set_Number) {
        int start_position = OCConstants.FOUR_HUNDRED_TEN + (set_Number * 12);
        int IntApurtureValue = this.dataBytes[start_position];
        int DecApurtureValue = this.dataBytes[start_position + 1];
        int ShadingBrightness = this.dataBytes[start_position + 2];
        int ShadingRed = this.dataBytes[start_position + 3];
        int ShadingBlue = this.dataBytes[start_position + 4];
        int ChromaticAberrationRed = this.dataBytes[start_position + 5];
        int ChromaticAberrationBlue = this.dataBytes[start_position + 6];
        int Distortion = this.dataBytes[start_position + 7];
        System.out.println((int) this.dataBytes[start_position]);
        System.out.println(" set_Number " + set_Number + " IntApurtureValue " + IntApurtureValue + " DecApurtureValue" + DecApurtureValue + " ShadingBrightness " + ShadingBrightness + " ShadingRed" + ShadingRed + " ShadingBlue " + ShadingBlue + " ChromaticAberrationRed " + ChromaticAberrationRed + " ChromaticAberrationBlue " + ChromaticAberrationBlue + " Distortion " + Distortion);
        return new ParameterSet(IntApurtureValue, DecApurtureValue, ShadingBrightness, ShadingRed, ShadingBlue, ChromaticAberrationRed, ChromaticAberrationBlue, Distortion);
    }

    public int isValid() {
        int validPoint = 0;
        if (!this.isValidProfile || getDataBytes() == null) {
            return 2;
        }
        if (Integer.parseInt(getDbVersion().split("\\.")[0]) != 1) {
            return 1;
        }
        if (getNextVersionOffset().shortValue() != 490) {
            validPoint = 2;
        } else if (getLensNameLength().shortValue() < 0 || getLensNameLength().shortValue() > OCConstants.MAX_NAME_LENGTH.shortValue()) {
            validPoint = 2;
        } else if (((short) getLensName().getBytes().length) != getLensNameLength().shortValue()) {
            validPoint = 2;
        } else if (getFocalLengthFlag() < 0 || getFocalLengthFlag() > 1) {
            validPoint = 2;
        } else if (getFocalLength().shortValue() < 0 || getLensNameLength().shortValue() > OCConstants.DIGIT_MAX_F_LENGTH_VALUE.shortValue()) {
            validPoint = 2;
        } else if (getFocalLengthFlag() == 1 && (getMinFNumberFlag() < 0 || getMinFNumberFlag() > 1)) {
            validPoint = 2;
        }
        if (getMinFNumber() != null) {
            String[] fNumChk = getMinFNumber().split("\\.");
            if ((Integer.parseInt(fNumChk[0]) < 0 || Integer.parseInt(fNumChk[0]) > 99) && Integer.parseInt(fNumChk[1]) >= 0 && Integer.parseInt(fNumChk[1]) <= 99) {
                return 2;
            }
            return validPoint;
        }
        if (getFMinLightVignettingCorrection() < -16 || getFMinLightVignettingCorrection() > 16 || getFMinRedColorVignettingCorrection() < -16 || getFMinRedColorVignettingCorrection() > 16 || getFMinBlueColorVignettingCorrection() < -16 || getFMinBlueColorVignettingCorrection() > 16 || getFMinRedChromaticAberationCorrection() < -16 || getFMinRedChromaticAberationCorrection() > 16 || getFMinBlueChromaticAberationCorrection() < -16 || getFMinBlueChromaticAberationCorrection() > 16 || getFMinDistortionCorrection() < -16 || getFMinDistortionCorrection() > 16) {
            return 2;
        }
        return validPoint;
    }
}
