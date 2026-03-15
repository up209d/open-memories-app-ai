package com.google.zxing.pdf417.encoder;

import com.google.zxing.WriterException;
import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;
import java.math.BigInteger;
import java.util.Arrays;

/* loaded from: classes.dex */
final class PDF417HighLevelEncoder {
    private static final int BYTE_COMPACTION = 1;
    private static final int LATCH_TO_BYTE = 924;
    private static final int LATCH_TO_BYTE_PADDED = 901;
    private static final int LATCH_TO_NUMERIC = 902;
    private static final int LATCH_TO_TEXT = 900;
    private static final int NUMERIC_COMPACTION = 2;
    private static final int SHIFT_TO_BYTE = 913;
    private static final int SUBMODE_ALPHA = 0;
    private static final int SUBMODE_LOWER = 1;
    private static final int SUBMODE_MIXED = 2;
    private static final int SUBMODE_PUNCTUATION = 3;
    private static final int TEXT_COMPACTION = 0;
    private static final byte[] TEXT_MIXED_RAW = {48, 49, 50, 51, 52, LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE2, 54, 55, 56, 57, 38, 13, 9, 44, 58, 35, 45, 46, LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE1, 47, 43, 37, 42, 61, 94, 0, 32, 0, 0, 0};
    private static final byte[] TEXT_PUNCTUATION_RAW = {59, 60, 62, 64, 91, 92, 93, 95, 96, 126, 33, 13, 9, 44, 58, 10, 45, 46, LiveviewCommon.PAYLOAD_HEADER_START_CODE_BYTE1, 47, 34, 124, 42, 40, 41, 63, 123, 125, 39, 0};
    private static final byte[] MIXED = new byte[LiveviewCommon.PAYLOAD_HEADER_SIZE];
    private static final byte[] PUNCTUATION = new byte[LiveviewCommon.PAYLOAD_HEADER_SIZE];

    static {
        Arrays.fill(MIXED, (byte) -1);
        for (byte i = 0; i < TEXT_MIXED_RAW.length; i = (byte) (i + 1)) {
            byte b = TEXT_MIXED_RAW[i];
            if (b > 0) {
                MIXED[b] = i;
            }
        }
        Arrays.fill(PUNCTUATION, (byte) -1);
        for (byte i2 = 0; i2 < TEXT_PUNCTUATION_RAW.length; i2 = (byte) (i2 + 1)) {
            byte b2 = TEXT_PUNCTUATION_RAW[i2];
            if (b2 > 0) {
                PUNCTUATION[b2] = i2;
            }
        }
    }

    private PDF417HighLevelEncoder() {
    }

    private static byte[] getBytesForMessage(String msg) {
        return msg.getBytes();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String encodeHighLevel(String msg, Compaction compaction) throws WriterException {
        byte[] bytes = null;
        StringBuilder sb = new StringBuilder(msg.length());
        int len = msg.length();
        int p = 0;
        int textSubMode = 0;
        if (compaction == Compaction.TEXT) {
            encodeText(msg, 0, len, sb, 0);
        } else if (compaction == Compaction.BYTE) {
            byte[] bytes2 = getBytesForMessage(msg);
            encodeBinary(bytes2, 0, bytes2.length, 1, sb);
        } else if (compaction == Compaction.NUMERIC) {
            sb.append((char) 902);
            encodeNumeric(msg, 0, len, sb);
        } else {
            int encodingMode = 0;
            while (p < len) {
                int n = determineConsecutiveDigitCount(msg, p);
                if (n >= 13) {
                    sb.append((char) 902);
                    encodingMode = 2;
                    textSubMode = 0;
                    encodeNumeric(msg, p, n, sb);
                    p += n;
                } else {
                    int t = determineConsecutiveTextCount(msg, p);
                    if (t >= 5 || n == len) {
                        if (encodingMode != 0) {
                            sb.append((char) 900);
                            encodingMode = 0;
                            textSubMode = 0;
                        }
                        textSubMode = encodeText(msg, p, t, sb, textSubMode);
                        p += t;
                    } else {
                        if (bytes == null) {
                            bytes = getBytesForMessage(msg);
                        }
                        int b = determineConsecutiveBinaryCount(msg, bytes, p);
                        if (b == 0) {
                            b = 1;
                        }
                        if (b == 1 && encodingMode == 0) {
                            encodeBinary(bytes, p, 1, 0, sb);
                        } else {
                            encodeBinary(bytes, p, b, encodingMode, sb);
                            encodingMode = 1;
                            textSubMode = 0;
                        }
                        p += b;
                    }
                }
            }
        }
        return sb.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x001e, code lost:            r3 = r3 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0020, code lost:            if (r3 < r13) goto L93;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0022, code lost:            r1 = 0;        r4 = r8.length();        r2 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0028, code lost:            if (r2 >= r4) goto L99;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x002c, code lost:            if ((r2 % 2) == 0) goto L60;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x002e, code lost:            r6 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x002f, code lost:            if (r6 == false) goto L61;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0031, code lost:            r1 = (char) ((r1 * 30) + r8.charAt(r2));        r14.append(r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x003c, code lost:            r2 = r2 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0127, code lost:            r1 = r8.charAt(r2);     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0124, code lost:            r6 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x012f, code lost:            if ((r4 % 2) == 0) goto L65;     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0131, code lost:            r14.append((char) ((r1 * 30) + 29));     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0139, code lost:            return r7;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int encodeText(java.lang.CharSequence r11, int r12, int r13, java.lang.StringBuilder r14, int r15) {
        /*
            Method dump skipped, instructions count: 324
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.encoder.PDF417HighLevelEncoder.encodeText(java.lang.CharSequence, int, int, java.lang.StringBuilder, int):int");
    }

    private static void encodeBinary(byte[] bytes, int startpos, int count, int startmode, StringBuilder sb) {
        if (count == 1 && startmode == 0) {
            sb.append((char) 913);
        } else {
            boolean sixpack = count % 6 == 0;
            if (sixpack) {
                sb.append((char) 924);
            } else {
                sb.append((char) 901);
            }
        }
        int idx = startpos;
        if (count >= 6) {
            char[] chars = new char[5];
            while ((startpos + count) - idx >= 6) {
                long t = 0;
                for (int i = 0; i < 6; i++) {
                    t = (t << 8) + (bytes[idx + i] & LiveviewCommon.COMMON_HEADER_START_BYTE);
                }
                for (int i2 = 0; i2 < 5; i2++) {
                    chars[i2] = (char) (t % 900);
                    t /= 900;
                }
                for (int i3 = chars.length - 1; i3 >= 0; i3--) {
                    sb.append(chars[i3]);
                }
                idx += 6;
            }
        }
        for (int i4 = idx; i4 < startpos + count; i4++) {
            int ch = bytes[i4] & LiveviewCommon.COMMON_HEADER_START_BYTE;
            sb.append((char) ch);
        }
    }

    private static void encodeNumeric(String msg, int startpos, int count, StringBuilder sb) {
        int idx = 0;
        StringBuilder tmp = new StringBuilder((count / 3) + 1);
        BigInteger num900 = BigInteger.valueOf(900L);
        BigInteger num0 = BigInteger.valueOf(0L);
        while (idx < count - 1) {
            tmp.setLength(0);
            int len = Math.min(44, count - idx);
            String part = '1' + msg.substring(startpos + idx, startpos + idx + len);
            BigInteger bigint = new BigInteger(part);
            do {
                tmp.append((char) bigint.mod(num900).intValue());
                bigint = bigint.divide(num900);
            } while (!bigint.equals(num0));
            for (int i = tmp.length() - 1; i >= 0; i--) {
                sb.append(tmp.charAt(i));
            }
            idx += len;
        }
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isAlphaUpper(char ch) {
        return ch == ' ' || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isAlphaLower(char ch) {
        return ch == ' ' || (ch >= 'a' && ch <= 'z');
    }

    private static boolean isMixed(char ch) {
        return MIXED[ch] != -1;
    }

    private static boolean isPunctuation(char ch) {
        return PUNCTUATION[ch] != -1;
    }

    private static boolean isText(char ch) {
        return ch == '\t' || ch == '\n' || ch == '\r' || (ch >= ' ' && ch <= '~');
    }

    private static int determineConsecutiveDigitCount(CharSequence msg, int startpos) {
        int count = 0;
        int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            char ch = msg.charAt(idx);
            while (isDigit(ch) && idx < len) {
                count++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
        }
        return count;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x0028, code lost:            return (r1 - r7) - r3;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int determineConsecutiveTextCount(java.lang.CharSequence r6, int r7) {
        /*
            r5 = 13
            int r2 = r6.length()
            r1 = r7
        L7:
            if (r1 >= r2) goto L35
            char r0 = r6.charAt(r1)
            r3 = 0
        Le:
            if (r3 >= r5) goto L23
            boolean r4 = isDigit(r0)
            if (r4 == 0) goto L23
            if (r1 >= r2) goto L23
            int r3 = r3 + 1
            int r1 = r1 + 1
            if (r1 >= r2) goto Le
            char r0 = r6.charAt(r1)
            goto Le
        L23:
            if (r3 < r5) goto L29
            int r4 = r1 - r7
            int r4 = r4 - r3
        L28:
            return r4
        L29:
            if (r3 > 0) goto L7
            char r0 = r6.charAt(r1)
            boolean r4 = isText(r0)
            if (r4 != 0) goto L38
        L35:
            int r4 = r1 - r7
            goto L28
        L38:
            int r1 = r1 + 1
            goto L7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.encoder.PDF417HighLevelEncoder.determineConsecutiveTextCount(java.lang.CharSequence, int):int");
    }

    private static int determineConsecutiveBinaryCount(CharSequence msg, byte[] bytes, int startpos) throws WriterException {
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx);
            int numericCount = 0;
            while (numericCount < 13 && isDigit(ch)) {
                numericCount++;
                int i = idx + numericCount;
                if (i >= len) {
                    break;
                }
                ch = msg.charAt(i);
            }
            if (numericCount >= 13) {
                return idx - startpos;
            }
            int textCount = 0;
            while (textCount < 5 && isText(ch)) {
                textCount++;
                int i2 = idx + textCount;
                if (i2 >= len) {
                    break;
                }
                ch = msg.charAt(i2);
            }
            if (textCount >= 5) {
                return idx - startpos;
            }
            char ch2 = msg.charAt(idx);
            if (bytes[idx] == 63 && ch2 != '?') {
                throw new WriterException("Non-encodable character detected: " + ch2 + " (Unicode: " + ((int) ch2) + ')');
            }
            idx++;
        }
        return idx - startpos;
    }
}
