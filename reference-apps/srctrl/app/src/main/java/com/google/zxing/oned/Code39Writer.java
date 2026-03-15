package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

/* loaded from: classes.dex */
public final class Code39Writer extends OneDimensionalCodeWriter {
    @Override // com.google.zxing.oned.OneDimensionalCodeWriter, com.google.zxing.Writer
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.CODE_39) {
            throw new IllegalArgumentException("Can only encode CODE_39, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }

    @Override // com.google.zxing.oned.OneDimensionalCodeWriter
    public boolean[] encode(String contents) {
        int length = contents.length();
        if (length > 80) {
            throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
        }
        int[] widths = new int[9];
        int codeWidth = length + 25;
        for (int i = 0; i < length; i++) {
            int indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i));
            if (indexInString < 0) {
                throw new IllegalArgumentException("Bad contents: " + contents);
            }
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths);
            for (int width : widths) {
                codeWidth += width;
            }
        }
        boolean[] result = new boolean[codeWidth];
        toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
        int pos = appendPattern(result, 0, widths, true);
        int[] narrowWhite = {1};
        int pos2 = pos + appendPattern(result, pos, narrowWhite, false);
        for (int i2 = length - 1; i2 >= 0; i2--) {
            toIntArray(Code39Reader.CHARACTER_ENCODINGS["0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i2))], widths);
            int pos3 = pos2 + appendPattern(result, pos2, widths, true);
            pos2 = pos3 + appendPattern(result, pos3, narrowWhite, false);
        }
        toIntArray(Code39Reader.CHARACTER_ENCODINGS[39], widths);
        int appendPattern = pos2 + appendPattern(result, pos2, widths, true);
        return result;
    }

    private static void toIntArray(int a, int[] toReturn) {
        for (int i = 0; i < 9; i++) {
            int temp = a & (1 << i);
            toReturn[i] = temp == 0 ? 1 : 2;
        }
    }
}
