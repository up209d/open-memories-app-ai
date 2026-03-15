package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

/* loaded from: classes.dex */
public final class EAN8Writer extends UPCEANWriter {
    private static final int CODE_WIDTH = 67;

    @Override // com.google.zxing.oned.OneDimensionalCodeWriter, com.google.zxing.Writer
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.EAN_8) {
            throw new IllegalArgumentException("Can only encode EAN_8, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }

    @Override // com.google.zxing.oned.OneDimensionalCodeWriter
    public boolean[] encode(String contents) {
        if (contents.length() != 8) {
            throw new IllegalArgumentException("Requested contents should be 8 digits long, but got " + contents.length());
        }
        boolean[] result = new boolean[CODE_WIDTH];
        int pos = 0 + appendPattern(result, 0, UPCEANReader.START_END_PATTERN, true);
        for (int i = 0; i <= 3; i++) {
            int digit = Integer.parseInt(contents.substring(i, i + 1));
            pos += appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], false);
        }
        int pos2 = pos + appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, false);
        for (int i2 = 4; i2 <= 7; i2++) {
            int digit2 = Integer.parseInt(contents.substring(i2, i2 + 1));
            pos2 += appendPattern(result, pos2, UPCEANReader.L_PATTERNS[digit2], true);
        }
        int appendPattern = pos2 + appendPattern(result, pos2, UPCEANReader.START_END_PATTERN, true);
        return result;
    }
}
