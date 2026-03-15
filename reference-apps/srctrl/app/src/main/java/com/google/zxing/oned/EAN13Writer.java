package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

/* loaded from: classes.dex */
public final class EAN13Writer extends UPCEANWriter {
    private static final int CODE_WIDTH = 95;

    @Override // com.google.zxing.oned.OneDimensionalCodeWriter, com.google.zxing.Writer
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.EAN_13) {
            throw new IllegalArgumentException("Can only encode EAN_13, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }

    @Override // com.google.zxing.oned.OneDimensionalCodeWriter
    public boolean[] encode(String contents) {
        if (contents.length() != 13) {
            throw new IllegalArgumentException("Requested contents should be 13 digits long, but got " + contents.length());
        }
        try {
            if (!UPCEANReader.checkStandardUPCEANChecksum(contents)) {
                throw new IllegalArgumentException("Contents do not pass checksum");
            }
            int firstDigit = Integer.parseInt(contents.substring(0, 1));
            int parities = EAN13Reader.FIRST_DIGIT_ENCODINGS[firstDigit];
            boolean[] result = new boolean[95];
            int pos = 0 + appendPattern(result, 0, UPCEANReader.START_END_PATTERN, true);
            for (int i = 1; i <= 6; i++) {
                int digit = Integer.parseInt(contents.substring(i, i + 1));
                if (((parities >> (6 - i)) & 1) == 1) {
                    digit += 10;
                }
                pos += appendPattern(result, pos, UPCEANReader.L_AND_G_PATTERNS[digit], false);
            }
            int pos2 = pos + appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, false);
            for (int i2 = 7; i2 <= 12; i2++) {
                pos2 += appendPattern(result, pos2, UPCEANReader.L_PATTERNS[Integer.parseInt(contents.substring(i2, i2 + 1))], true);
            }
            int appendPattern = pos2 + appendPattern(result, pos2, UPCEANReader.START_END_PATTERN, true);
            return result;
        } catch (FormatException e) {
            throw new IllegalArgumentException("Illegal contents");
        }
    }
}
