package com.google.zxing.oned;

import java.util.Arrays;

/* loaded from: classes.dex */
public final class CodaBarWriter extends OneDimensionalCodeWriter {
    private static final char[] START_END_CHARS = {'A', 'B', 'C', 'D'};
    private static final char[] ALT_START_END_CHARS = {'T', 'N', '*', 'E'};

    /* JADX WARN: Failed to find 'out' block for switch in B:45:0x0138. Please report as an issue. */
    @Override // com.google.zxing.oned.OneDimensionalCodeWriter
    public boolean[] encode(String contents) {
        if (contents.length() < 2) {
            throw new IllegalArgumentException("Codabar should start/end with start/stop symbols");
        }
        char firstChar = Character.toUpperCase(contents.charAt(0));
        char lastChar = Character.toUpperCase(contents.charAt(contents.length() - 1));
        boolean startsEndsNormal = CodaBarReader.arrayContains(START_END_CHARS, firstChar) && CodaBarReader.arrayContains(START_END_CHARS, lastChar);
        boolean startsEndsAlt = CodaBarReader.arrayContains(ALT_START_END_CHARS, firstChar) && CodaBarReader.arrayContains(ALT_START_END_CHARS, lastChar);
        if (!startsEndsNormal && !startsEndsAlt) {
            throw new IllegalArgumentException("Codabar should start/end with " + Arrays.toString(START_END_CHARS) + ", or start/end with " + Arrays.toString(ALT_START_END_CHARS));
        }
        int resultLength = 20;
        char[] charsWhichAreTenLengthEachAfterDecoded = {'/', ':', '+', '.'};
        for (int i = 1; i < contents.length() - 1; i++) {
            if (Character.isDigit(contents.charAt(i)) || contents.charAt(i) == '-' || contents.charAt(i) == '$') {
                resultLength += 9;
            } else if (CodaBarReader.arrayContains(charsWhichAreTenLengthEachAfterDecoded, contents.charAt(i))) {
                resultLength += 10;
            } else {
                throw new IllegalArgumentException("Cannot encode : '" + contents.charAt(i) + '\'');
            }
        }
        boolean[] result = new boolean[resultLength + (contents.length() - 1)];
        int position = 0;
        for (int index = 0; index < contents.length(); index++) {
            char c = Character.toUpperCase(contents.charAt(index));
            if (index == contents.length() - 1) {
                switch (c) {
                    case '*':
                        c = 'C';
                        break;
                    case 'E':
                        c = 'D';
                        break;
                    case 'N':
                        c = 'B';
                        break;
                    case 'T':
                        c = 'A';
                        break;
                }
            }
            int code = 0;
            int i2 = 0;
            while (true) {
                if (i2 < CodaBarReader.ALPHABET.length) {
                    if (c != CodaBarReader.ALPHABET[i2]) {
                        i2++;
                    } else {
                        code = CodaBarReader.CHARACTER_ENCODINGS[i2];
                    }
                }
            }
            boolean color = true;
            int counter = 0;
            int bit = 0;
            while (bit < 7) {
                result[position] = color;
                position++;
                if (((code >> (6 - bit)) & 1) == 0 || counter == 1) {
                    color = !color;
                    bit++;
                    counter = 0;
                } else {
                    counter++;
                }
            }
            if (index < contents.length() - 1) {
                result[position] = false;
                position++;
            }
        }
        return result;
    }
}
