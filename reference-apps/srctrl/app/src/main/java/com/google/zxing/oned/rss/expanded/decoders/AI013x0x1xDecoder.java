package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;
import com.sony.imaging.app.base.common.EVDialDetector;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AI013x0x1xDecoder extends AI01weightDecoder {
    private static final int DATE_SIZE = 16;
    private static final int HEADER_SIZE = 8;
    private static final int WEIGHT_SIZE = 20;
    private final String dateCode;
    private final String firstAIdigits;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AI013x0x1xDecoder(BitArray information, String firstAIdigits, String dateCode) {
        super(information);
        this.dateCode = dateCode;
        this.firstAIdigits = firstAIdigits;
    }

    @Override // com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder
    public String parseInformation() throws NotFoundException {
        if (getInformation().getSize() != 84) {
            throw NotFoundException.getNotFoundInstance();
        }
        StringBuilder buf = new StringBuilder();
        encodeCompressedGtin(buf, 8);
        encodeCompressedWeight(buf, 48, 20);
        encodeCompressedDate(buf, 68);
        return buf.toString();
    }

    private void encodeCompressedDate(StringBuilder buf, int currentPos) {
        int numericDate = getGeneralDecoder().extractNumericValueFromBitArray(currentPos, 16);
        if (numericDate != 38400) {
            buf.append('(');
            buf.append(this.dateCode);
            buf.append(')');
            int day = numericDate % 32;
            int numericDate2 = numericDate / 32;
            int month = (numericDate2 % 12) + 1;
            int numericDate3 = numericDate2 / 12;
            if (numericDate3 / 10 == 0) {
                buf.append('0');
            }
            buf.append(numericDate3);
            if (month / 10 == 0) {
                buf.append('0');
            }
            buf.append(month);
            if (day / 10 == 0) {
                buf.append('0');
            }
            buf.append(day);
        }
    }

    @Override // com.google.zxing.oned.rss.expanded.decoders.AI01weightDecoder
    protected void addWeightCode(StringBuilder buf, int weight) {
        int lastAI = weight / EVDialDetector.INVALID_EV_CODE;
        buf.append('(');
        buf.append(this.firstAIdigits);
        buf.append(lastAI);
        buf.append(')');
    }

    @Override // com.google.zxing.oned.rss.expanded.decoders.AI01weightDecoder
    protected int checkWeight(int weight) {
        return weight % EVDialDetector.INVALID_EV_CODE;
    }
}
