package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AI01AndOtherAIs extends AI01decoder {
    private static final int HEADER_SIZE = 4;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AI01AndOtherAIs(BitArray information) {
        super(information);
    }

    @Override // com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder
    public String parseInformation() throws NotFoundException, FormatException {
        StringBuilder buff = new StringBuilder();
        buff.append("(01)");
        int initialGtinPosition = buff.length();
        int firstGtinDigit = getGeneralDecoder().extractNumericValueFromBitArray(4, 4);
        buff.append(firstGtinDigit);
        encodeCompressedGtinWithoutAI(buff, 8, initialGtinPosition);
        return getGeneralDecoder().decodeAllCodes(buff, 48);
    }
}
