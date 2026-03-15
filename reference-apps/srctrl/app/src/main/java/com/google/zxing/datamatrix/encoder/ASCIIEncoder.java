package com.google.zxing.datamatrix.encoder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ASCIIEncoder implements Encoder {
    @Override // com.google.zxing.datamatrix.encoder.Encoder
    public int getEncodingMode() {
        return 0;
    }

    @Override // com.google.zxing.datamatrix.encoder.Encoder
    public void encode(EncoderContext context) {
        int n = HighLevelEncoder.determineConsecutiveDigitCount(context.getMessage(), context.pos);
        if (n >= 2) {
            context.writeCodeword(encodeASCIIDigits(context.getMessage().charAt(context.pos), context.getMessage().charAt(context.pos + 1)));
            context.pos += 2;
            return;
        }
        char c = context.getCurrentChar();
        int newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode());
        if (newMode != getEncodingMode()) {
            switch (newMode) {
                case 1:
                    context.writeCodeword((char) 230);
                    context.signalEncoderChange(1);
                    return;
                case 2:
                    context.writeCodeword((char) 239);
                    context.signalEncoderChange(2);
                    return;
                case 3:
                    context.writeCodeword((char) 238);
                    context.signalEncoderChange(3);
                    return;
                case 4:
                    context.writeCodeword((char) 240);
                    context.signalEncoderChange(4);
                    return;
                case 5:
                    context.writeCodeword((char) 231);
                    context.signalEncoderChange(5);
                    return;
                default:
                    throw new IllegalStateException("Illegal mode: " + newMode);
            }
        }
        if (HighLevelEncoder.isExtendedASCII(c)) {
            context.writeCodeword((char) 235);
            context.writeCodeword((char) ((c - 128) + 1));
            context.pos++;
        } else {
            context.writeCodeword((char) (c + 1));
            context.pos++;
        }
    }

    private static char encodeASCIIDigits(char digit1, char digit2) {
        if (HighLevelEncoder.isDigit(digit1) && HighLevelEncoder.isDigit(digit2)) {
            int num = ((digit1 - '0') * 10) + (digit2 - '0');
            return (char) (num + 130);
        }
        throw new IllegalArgumentException("not digits: " + digit1 + digit2);
    }
}
