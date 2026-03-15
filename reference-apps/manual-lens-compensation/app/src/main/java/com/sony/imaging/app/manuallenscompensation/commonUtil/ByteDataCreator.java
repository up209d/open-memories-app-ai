package com.sony.imaging.app.manuallenscompensation.commonUtil;

import com.sony.imaging.app.manuallenscompensation.shooting.controller.DeleteFocusMagnifierController;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ByteDataCreator {
    private byte[] dataBytes = null;

    public ByteDataCreator() {
        setDataBytes(new byte[OCConstants.FOUR_NINETY_SIX]);
    }

    public void setAppVersion(String app_version) {
        String[] ver_string = app_version.split("\\.");
        getDataBytes()[0] = (byte) Integer.parseInt(ver_string[0]);
        getDataBytes()[1] = (byte) Integer.parseInt(ver_string[1]);
    }

    public void setDbVersion(String db_version) {
        String[] ver_string = db_version.split("\\.");
        getDataBytes()[2] = (byte) Integer.parseInt(ver_string[0]);
        getDataBytes()[3] = (byte) Integer.parseInt(ver_string[1]);
    }

    public void setNextVersionOffset(Short next_version_offset) {
        byte[] length_bytes = ByteBuffer.allocate(2).putShort(next_version_offset.shortValue()).array();
        getDataBytes()[4] = length_bytes[0];
        getDataBytes()[5] = length_bytes[1];
    }

    public void setLensName(String name) {
        byte[] length_bytes = ByteBuffer.allocate(2).putShort((short) name.getBytes().length).array();
        getDataBytes()[6] = length_bytes[0];
        getDataBytes()[7] = length_bytes[1];
        byte[] name_bytes = name.getBytes();
        System.arraycopy(name_bytes, 0, getDataBytes(), 8, name_bytes.length);
    }

    public void setFocalLengthFlag(int flag) {
        getDataBytes()[OCConstants.THREE_NINETY_TWO] = (byte) flag;
    }

    public void setFocalLength(Short focal_length) {
        if (focal_length.shortValue() == 0) {
            setFocalLengthFlag(0);
        } else {
            setFocalLengthFlag(1);
        }
        byte[] length_bytes = ByteBuffer.allocate(2).putShort(focal_length.shortValue()).array();
        getDataBytes()[OCConstants.THREE_NINETY_THREE] = length_bytes[0];
        getDataBytes()[OCConstants.THREE_NINETY_FOUR] = length_bytes[1];
    }

    public void setMinFNumberFlag(int flag) {
        getDataBytes()[OCConstants.THREE_NINETY_FIVE] = (byte) flag;
    }

    public void setMinFNumber(String f_number) {
        if (f_number.equalsIgnoreCase(OCConstants.BLANK_FVALUE_STR)) {
            setMinFNumberFlag(0);
        } else {
            setMinFNumberFlag(1);
        }
        String[] f_number_string = f_number.split("\\.");
        if (f_number_string[0].equalsIgnoreCase(" ")) {
            getDataBytes()[OCConstants.THREE_NINETY_SIX] = 0;
        } else {
            getDataBytes()[OCConstants.THREE_NINETY_SIX] = (byte) Integer.parseInt(f_number_string[0]);
        }
        if (f_number_string[1].equalsIgnoreCase(" ")) {
            getDataBytes()[OCConstants.THREE_NINETY_SEVEN] = 0;
            return;
        }
        int dec_string_length = f_number_string[1].length();
        int dec_value = Integer.parseInt(f_number_string[1]);
        if (dec_string_length == 1 && dec_value < 10) {
            dec_value *= 10;
        }
        getDataBytes()[OCConstants.THREE_NINETY_SEVEN] = (byte) dec_value;
    }

    public void setFMinLightVignettingCorrection(int value) {
        getDataBytes()[OCConstants.THREE_NINETY_EIGHT] = (byte) value;
    }

    public void setFMinRedColorVignettingCorrection(int value) {
        getDataBytes()[OCConstants.THREE_NINETY_NINE] = (byte) value;
    }

    public void setFMinBlueColorVignettingCorrection(int value) {
        getDataBytes()[OCConstants.FOUR_HUNDRED] = (byte) value;
    }

    public void setFMinRedChromaticAberationCorrection(int value) {
        getDataBytes()[OCConstants.FOUR_HUNDRED_ONE] = (byte) value;
    }

    public void setFMinBlueChromaticAberationCorrection(int value) {
        getDataBytes()[OCConstants.FOUR_HUNDRED_TWO] = (byte) value;
    }

    public void setFMinDistortionCorrection(int value) {
        getDataBytes()[OCConstants.FOUR_HUNDRED_THREE] = (byte) value;
    }

    public void setMaxFNumber(String f_number) {
        String[] f_number_string = f_number.split(DeleteFocusMagnifierController.FOCUS_MAGNIFIER_OFF);
        getDataBytes()[OCConstants.FOUR_HUNDRED_SIX] = (byte) Integer.parseInt(f_number_string[0]);
        getDataBytes()[OCConstants.FOUR_HUNDRED_SEVEN] = (byte) Integer.parseInt(f_number_string[1]);
    }

    public void setEvStepValue(String EvStepValue) {
        if (EvStepValue.equalsIgnoreCase(OCConstants.EVSTEP_ZERO_TO_THREE)) {
            getDataBytes()[OCConstants.FOUR_HUNDRED_EIGHT] = 1;
        } else if (EvStepValue.equalsIgnoreCase(OCConstants.EVSTEP_ZERO_TO_FIVE)) {
            getDataBytes()[OCConstants.FOUR_HUNDRED_EIGHT] = 2;
        } else if (EvStepValue.equalsIgnoreCase(OCConstants.EVSTEP_ONE_TO_ZERO)) {
            getDataBytes()[OCConstants.FOUR_HUNDRED_EIGHT] = 3;
        }
    }

    public void setMaxFNumberFlag(int flag) {
        getDataBytes()[OCConstants.FOUR_HUNDRED_FOUR] = (byte) flag;
    }

    public void setParameterCount(int count) {
        getDataBytes()[OCConstants.FOUR_HUNDRED_NINE] = (byte) count;
    }

    public void setParameterSet(ParameterSet parameterSet, int set_number) {
        int start_position = OCConstants.FOUR_HUNDRED_TEN + (set_number * 12);
        byte[] parameterSetBytes = {(byte) parameterSet.intApurtureValue, (byte) parameterSet.decApurtureValue, (byte) parameterSet.ShadingBrightness, (byte) parameterSet.ShadingRed, (byte) parameterSet.ShadingBlue, (byte) parameterSet.ChromaticAberrationRed, (byte) parameterSet.ChromaticAberrationBlue, (byte) parameterSet.Distortion};
        System.arraycopy(parameterSetBytes, 0, getDataBytes(), start_position, 12);
    }

    public byte[] getDataBytes() {
        return this.dataBytes;
    }

    public void setDataBytes(byte[] dataBytes) {
        this.dataBytes = dataBytes;
    }

    public void releaseAllocatedMemory() {
        setDataBytes(null);
    }
}
