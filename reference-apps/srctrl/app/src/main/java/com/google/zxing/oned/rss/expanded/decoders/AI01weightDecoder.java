package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.common.BitArray;
import com.sony.imaging.app.base.common.EVDialDetector;

/* loaded from: classes.dex */
abstract class AI01weightDecoder extends AI01decoder {
    protected abstract void addWeightCode(StringBuilder sb, int i);

    protected abstract int checkWeight(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AI01weightDecoder(BitArray information) {
        super(information);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void encodeCompressedWeight(StringBuilder buf, int currentPos, int weightSize) {
        int originalWeightNumeric = getGeneralDecoder().extractNumericValueFromBitArray(currentPos, weightSize);
        addWeightCode(buf, originalWeightNumeric);
        int weightNumeric = checkWeight(originalWeightNumeric);
        int currentDivisor = EVDialDetector.INVALID_EV_CODE;
        for (int i = 0; i < 5; i++) {
            if (weightNumeric / currentDivisor == 0) {
                buf.append('0');
            }
            currentDivisor /= 10;
        }
        buf.append(weightNumeric);
    }
}
