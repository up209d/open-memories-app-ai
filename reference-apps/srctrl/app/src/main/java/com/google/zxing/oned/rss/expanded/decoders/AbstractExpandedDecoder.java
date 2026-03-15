package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;

/* loaded from: classes.dex */
public abstract class AbstractExpandedDecoder {
    private final GeneralAppIdDecoder generalDecoder;
    private final BitArray information;

    public abstract String parseInformation() throws NotFoundException, FormatException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractExpandedDecoder(BitArray information) {
        this.information = information;
        this.generalDecoder = new GeneralAppIdDecoder(information);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final BitArray getInformation() {
        return this.information;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final GeneralAppIdDecoder getGeneralDecoder() {
        return this.generalDecoder;
    }

    public static AbstractExpandedDecoder createDecoder(BitArray information) {
        if (information.get(1)) {
            return new AI01AndOtherAIs(information);
        }
        if (!information.get(2)) {
            return new AnyAIDecoder(information);
        }
        int fourBitEncodationMethod = GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 4);
        switch (fourBitEncodationMethod) {
            case 4:
                return new AI013103decoder(information);
            case 5:
                return new AI01320xDecoder(information);
            default:
                int fiveBitEncodationMethod = GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 5);
                switch (fiveBitEncodationMethod) {
                    case 12:
                        return new AI01392xDecoder(information);
                    case 13:
                        return new AI01393xDecoder(information);
                    default:
                        int sevenBitEncodationMethod = GeneralAppIdDecoder.extractNumericValueFromBitArray(information, 1, 7);
                        switch (sevenBitEncodationMethod) {
                            case 56:
                                return new AI013x0x1xDecoder(information, "310", "11");
                            case HandoffOperationInfo.SET_SHOOTING_STATE /* 57 */:
                                return new AI013x0x1xDecoder(information, ISOSensitivityController.ISO_320, "11");
                            case HandoffOperationInfo.START_LIVEVIEW /* 58 */:
                                return new AI013x0x1xDecoder(information, "310", "13");
                            case HandoffOperationInfo.STOP_LIVEVIEW /* 59 */:
                                return new AI013x0x1xDecoder(information, ISOSensitivityController.ISO_320, "13");
                            case HandoffOperationInfo.STOP_BULB_SHOOTING /* 60 */:
                                return new AI013x0x1xDecoder(information, "310", "15");
                            case 61:
                                return new AI013x0x1xDecoder(information, ISOSensitivityController.ISO_320, "15");
                            case 62:
                                return new AI013x0x1xDecoder(information, "310", "17");
                            case 63:
                                return new AI013x0x1xDecoder(information, ISOSensitivityController.ISO_320, "17");
                            default:
                                throw new IllegalStateException("unknown decoder: " + information);
                        }
                }
        }
    }
}
