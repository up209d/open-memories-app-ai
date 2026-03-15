package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

/* loaded from: classes.dex */
public final class EAN8Reader extends UPCEANReader {
    private final int[] decodeMiddleCounters = new int[4];

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.zxing.oned.UPCEANReader
    public int decodeMiddle(BitArray row, int[] startRange, StringBuilder result) throws NotFoundException {
        int[] counters = this.decodeMiddleCounters;
        counters[0] = 0;
        counters[1] = 0;
        counters[2] = 0;
        counters[3] = 0;
        int end = row.getSize();
        int rowOffset = startRange[1];
        for (int x = 0; x < 4 && rowOffset < end; x++) {
            int bestMatch = decodeDigit(row, counters, rowOffset, L_PATTERNS);
            result.append((char) (bestMatch + 48));
            for (int counter : counters) {
                rowOffset += counter;
            }
        }
        int[] middleRange = findGuardPattern(row, rowOffset, true, MIDDLE_PATTERN);
        int rowOffset2 = middleRange[1];
        for (int x2 = 0; x2 < 4 && rowOffset2 < end; x2++) {
            int bestMatch2 = decodeDigit(row, counters, rowOffset2, L_PATTERNS);
            result.append((char) (bestMatch2 + 48));
            for (int counter2 : counters) {
                rowOffset2 += counter2;
            }
        }
        return rowOffset2;
    }

    @Override // com.google.zxing.oned.UPCEANReader
    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.EAN_8;
    }
}
