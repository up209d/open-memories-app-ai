package com.google.zxing.datamatrix.encoder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class C40Encoder implements Encoder {
    @Override // com.google.zxing.datamatrix.encoder.Encoder
    public int getEncodingMode() {
        return 1;
    }

    @Override // com.google.zxing.datamatrix.encoder.Encoder
    public void encode(EncoderContext context) {
        int newMode;
        StringBuilder buffer = new StringBuilder();
        while (true) {
            if (!context.hasMoreCharacters()) {
                break;
            }
            char c = context.getCurrentChar();
            context.pos++;
            int lastCharSize = encodeChar(c, buffer);
            int unwritten = (buffer.length() / 3) * 2;
            int curCodewordCount = context.getCodewordCount() + unwritten;
            context.updateSymbolInfo(curCodewordCount);
            int available = context.getSymbolInfo().getDataCapacity() - curCodewordCount;
            if (!context.hasMoreCharacters()) {
                StringBuilder removed = new StringBuilder();
                if (buffer.length() % 3 == 2 && (available < 2 || available > 2)) {
                    lastCharSize = backtrackOneCharacter(context, buffer, removed, lastCharSize);
                }
                while (buffer.length() % 3 == 1 && ((lastCharSize <= 3 && available != 1) || lastCharSize > 3)) {
                    lastCharSize = backtrackOneCharacter(context, buffer, removed, lastCharSize);
                }
            } else {
                int count = buffer.length();
                if (count % 3 == 0 && (newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode())) != getEncodingMode()) {
                    context.signalEncoderChange(newMode);
                    break;
                }
            }
        }
        handleEOD(context, buffer);
    }

    private int backtrackOneCharacter(EncoderContext context, StringBuilder buffer, StringBuilder removed, int lastCharSize) {
        int count = buffer.length();
        buffer.delete(count - lastCharSize, count);
        context.pos--;
        char c = context.getCurrentChar();
        int lastCharSize2 = encodeChar(c, removed);
        context.resetSymbolInfo();
        return lastCharSize2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeNextTriplet(EncoderContext context, StringBuilder buffer) {
        context.writeCodewords(encodeToCodewords(buffer, 0));
        buffer.delete(0, 3);
    }

    void handleEOD(EncoderContext context, StringBuilder buffer) {
        int unwritten = (buffer.length() / 3) * 2;
        int rest = buffer.length() % 3;
        int curCodewordCount = context.getCodewordCount() + unwritten;
        context.updateSymbolInfo(curCodewordCount);
        int available = context.getSymbolInfo().getDataCapacity() - curCodewordCount;
        if (rest == 2) {
            buffer.append((char) 0);
            while (buffer.length() >= 3) {
                writeNextTriplet(context, buffer);
            }
            if (context.hasMoreCharacters()) {
                context.writeCodeword((char) 254);
            }
        } else if (available == 1 && rest == 1) {
            while (buffer.length() >= 3) {
                writeNextTriplet(context, buffer);
            }
            if (context.hasMoreCharacters()) {
                context.writeCodeword((char) 254);
            }
            context.pos--;
        } else if (rest == 0) {
            while (buffer.length() >= 3) {
                writeNextTriplet(context, buffer);
            }
            if (available > 0 || context.hasMoreCharacters()) {
                context.writeCodeword((char) 254);
            }
        } else {
            throw new IllegalStateException("Unexpected case. Please report!");
        }
        context.signalEncoderChange(0);
    }

    int encodeChar(char c, StringBuilder sb) {
        if (c == ' ') {
            sb.append((char) 3);
            return 1;
        }
        if (c >= '0' && c <= '9') {
            sb.append((char) ((c - '0') + 4));
            return 1;
        }
        if (c >= 'A' && c <= 'Z') {
            sb.append((char) ((c - 'A') + 14));
            return 1;
        }
        if (c >= 0 && c <= 31) {
            sb.append((char) 0);
            sb.append(c);
            return 2;
        }
        if (c >= '!' && c <= '/') {
            sb.append((char) 1);
            sb.append((char) (c - '!'));
            return 2;
        }
        if (c >= ':' && c <= '@') {
            sb.append((char) 1);
            sb.append((char) ((c - ':') + 15));
            return 2;
        }
        if (c >= '[' && c <= '_') {
            sb.append((char) 1);
            sb.append((char) ((c - '[') + 22));
            return 2;
        }
        if (c >= '`' && c <= 127) {
            sb.append((char) 2);
            sb.append((char) (c - '`'));
            return 2;
        }
        if (c >= 128) {
            sb.append("\u0001\u001e");
            int len = 2 + encodeChar((char) (c - 128), sb);
            return len;
        }
        throw new IllegalArgumentException("Illegal character: " + c);
    }

    private static String encodeToCodewords(CharSequence sb, int startPos) {
        char c1 = sb.charAt(startPos);
        char c2 = sb.charAt(startPos + 1);
        char c3 = sb.charAt(startPos + 2);
        int v = (c1 * 1600) + (c2 * '(') + c3 + 1;
        char cw1 = (char) (v / 256);
        char cw2 = (char) (v % 256);
        return new String(new char[]{cw1, cw2});
    }
}
