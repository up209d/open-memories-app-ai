package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class Code128Reader extends OneDReader {
    private static final int CODE_CODE_A = 101;
    private static final int CODE_CODE_B = 100;
    private static final int CODE_CODE_C = 99;
    private static final int CODE_FNC_1 = 102;
    private static final int CODE_FNC_2 = 97;
    private static final int CODE_FNC_3 = 96;
    private static final int CODE_FNC_4_A = 101;
    private static final int CODE_FNC_4_B = 100;
    static final int[][] CODE_PATTERNS = {new int[]{2, 1, 2, 2, 2, 2}, new int[]{2, 2, 2, 1, 2, 2}, new int[]{2, 2, 2, 2, 2, 1}, new int[]{1, 2, 1, 2, 2, 3}, new int[]{1, 2, 1, 3, 2, 2}, new int[]{1, 3, 1, 2, 2, 2}, new int[]{1, 2, 2, 2, 1, 3}, new int[]{1, 2, 2, 3, 1, 2}, new int[]{1, 3, 2, 2, 1, 2}, new int[]{2, 2, 1, 2, 1, 3}, new int[]{2, 2, 1, 3, 1, 2}, new int[]{2, 3, 1, 2, 1, 2}, new int[]{1, 1, 2, 2, 3, 2}, new int[]{1, 2, 2, 1, 3, 2}, new int[]{1, 2, 2, 2, 3, 1}, new int[]{1, 1, 3, 2, 2, 2}, new int[]{1, 2, 3, 1, 2, 2}, new int[]{1, 2, 3, 2, 2, 1}, new int[]{2, 2, 3, 2, 1, 1}, new int[]{2, 2, 1, 1, 3, 2}, new int[]{2, 2, 1, 2, 3, 1}, new int[]{2, 1, 3, 2, 1, 2}, new int[]{2, 2, 3, 1, 1, 2}, new int[]{3, 1, 2, 1, 3, 1}, new int[]{3, 1, 1, 2, 2, 2}, new int[]{3, 2, 1, 1, 2, 2}, new int[]{3, 2, 1, 2, 2, 1}, new int[]{3, 1, 2, 2, 1, 2}, new int[]{3, 2, 2, 1, 1, 2}, new int[]{3, 2, 2, 2, 1, 1}, new int[]{2, 1, 2, 1, 2, 3}, new int[]{2, 1, 2, 3, 2, 1}, new int[]{2, 3, 2, 1, 2, 1}, new int[]{1, 1, 1, 3, 2, 3}, new int[]{1, 3, 1, 1, 2, 3}, new int[]{1, 3, 1, 3, 2, 1}, new int[]{1, 1, 2, 3, 1, 3}, new int[]{1, 3, 2, 1, 1, 3}, new int[]{1, 3, 2, 3, 1, 1}, new int[]{2, 1, 1, 3, 1, 3}, new int[]{2, 3, 1, 1, 1, 3}, new int[]{2, 3, 1, 3, 1, 1}, new int[]{1, 1, 2, 1, 3, 3}, new int[]{1, 1, 2, 3, 3, 1}, new int[]{1, 3, 2, 1, 3, 1}, new int[]{1, 1, 3, 1, 2, 3}, new int[]{1, 1, 3, 3, 2, 1}, new int[]{1, 3, 3, 1, 2, 1}, new int[]{3, 1, 3, 1, 2, 1}, new int[]{2, 1, 1, 3, 3, 1}, new int[]{2, 3, 1, 1, 3, 1}, new int[]{2, 1, 3, 1, 1, 3}, new int[]{2, 1, 3, 3, 1, 1}, new int[]{2, 1, 3, 1, 3, 1}, new int[]{3, 1, 1, 1, 2, 3}, new int[]{3, 1, 1, 3, 2, 1}, new int[]{3, 3, 1, 1, 2, 1}, new int[]{3, 1, 2, 1, 1, 3}, new int[]{3, 1, 2, 3, 1, 1}, new int[]{3, 3, 2, 1, 1, 1}, new int[]{3, 1, 4, 1, 1, 1}, new int[]{2, 2, 1, 4, 1, 1}, new int[]{4, 3, 1, 1, 1, 1}, new int[]{1, 1, 1, 2, 2, 4}, new int[]{1, 1, 1, 4, 2, 2}, new int[]{1, 2, 1, 1, 2, 4}, new int[]{1, 2, 1, 4, 2, 1}, new int[]{1, 4, 1, 1, 2, 2}, new int[]{1, 4, 1, 2, 2, 1}, new int[]{1, 1, 2, 2, 1, 4}, new int[]{1, 1, 2, 4, 1, 2}, new int[]{1, 2, 2, 1, 1, 4}, new int[]{1, 2, 2, 4, 1, 1}, new int[]{1, 4, 2, 1, 1, 2}, new int[]{1, 4, 2, 2, 1, 1}, new int[]{2, 4, 1, 2, 1, 1}, new int[]{2, 2, 1, 1, 1, 4}, new int[]{4, 1, 3, 1, 1, 1}, new int[]{2, 4, 1, 1, 1, 2}, new int[]{1, 3, 4, 1, 1, 1}, new int[]{1, 1, 1, 2, 4, 2}, new int[]{1, 2, 1, 1, 4, 2}, new int[]{1, 2, 1, 2, 4, 1}, new int[]{1, 1, 4, 2, 1, 2}, new int[]{1, 2, 4, 1, 1, 2}, new int[]{1, 2, 4, 2, 1, 1}, new int[]{4, 1, 1, 2, 1, 2}, new int[]{4, 2, 1, 1, 1, 2}, new int[]{4, 2, 1, 2, 1, 1}, new int[]{2, 1, 2, 1, 4, 1}, new int[]{2, 1, 4, 1, 2, 1}, new int[]{4, 1, 2, 1, 2, 1}, new int[]{1, 1, 1, 1, 4, 3}, new int[]{1, 1, 1, 3, 4, 1}, new int[]{1, 3, 1, 1, 4, 1}, new int[]{1, 1, 4, 1, 1, 3}, new int[]{1, 1, 4, 3, 1, 1}, new int[]{4, 1, 1, 1, 1, 3}, new int[]{4, 1, 1, 3, 1, 1}, new int[]{1, 1, 3, 1, 4, 1}, new int[]{1, 1, 4, 1, 3, 1}, new int[]{3, 1, 1, 1, 4, 1}, new int[]{4, 1, 1, 1, 3, 1}, new int[]{2, 1, 1, 4, 1, 2}, new int[]{2, 1, 1, 2, 1, 4}, new int[]{2, 1, 1, 2, 3, 2}, new int[]{2, 3, 3, 1, 1, 1, 2}};
    private static final int CODE_SHIFT = 98;
    private static final int CODE_START_A = 103;
    private static final int CODE_START_B = 104;
    private static final int CODE_START_C = 105;
    private static final int CODE_STOP = 106;
    private static final int MAX_AVG_VARIANCE = 64;
    private static final int MAX_INDIVIDUAL_VARIANCE = 179;

    private static int[] findStartPattern(BitArray row) throws NotFoundException {
        int width = row.getSize();
        int rowOffset = row.getNextSet(0);
        int counterPosition = 0;
        int[] counters = new int[6];
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;
        for (int i = rowOffset; i < width; i++) {
            if (row.get(i) ^ isWhite) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition == patternLength - 1) {
                    int bestVariance = MAX_AVG_VARIANCE;
                    int bestMatch = -1;
                    for (int startCode = 103; startCode <= 105; startCode++) {
                        int variance = patternMatchVariance(counters, CODE_PATTERNS[startCode], MAX_INDIVIDUAL_VARIANCE);
                        if (variance < bestVariance) {
                            bestVariance = variance;
                            bestMatch = startCode;
                        }
                    }
                    if (bestMatch >= 0 && row.isRange(Math.max(0, patternStart - ((i - patternStart) / 2)), patternStart, false)) {
                        return new int[]{patternStart, i, bestMatch};
                    }
                    patternStart += counters[0] + counters[1];
                    System.arraycopy(counters, 2, counters, 0, patternLength - 2);
                    counters[patternLength - 2] = 0;
                    counters[patternLength - 1] = 0;
                    counterPosition--;
                } else {
                    counterPosition++;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeCode(BitArray row, int[] counters, int rowOffset) throws NotFoundException {
        recordPattern(row, rowOffset, counters);
        int bestVariance = MAX_AVG_VARIANCE;
        int bestMatch = -1;
        for (int d = 0; d < CODE_PATTERNS.length; d++) {
            int[] pattern = CODE_PATTERNS[d];
            int variance = patternMatchVariance(counters, pattern, MAX_INDIVIDUAL_VARIANCE);
            if (variance < bestVariance) {
                bestVariance = variance;
                bestMatch = d;
            }
        }
        if (bestMatch >= 0) {
            return bestMatch;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:25:0x00ae. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:47:0x00f3. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:63:0x013d. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:82:0x0194. Please report as an issue. */
    @Override // com.google.zxing.oned.OneDReader
    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
        int codeSet;
        boolean convertFNC1 = hints != null && hints.containsKey(DecodeHintType.ASSUME_GS1);
        int[] startPatternInfo = findStartPattern(row);
        int startCode = startPatternInfo[2];
        List<Byte> rawCodes = new ArrayList<>(20);
        rawCodes.add(Byte.valueOf((byte) startCode));
        switch (startCode) {
            case 103:
                codeSet = ExecutorCreator.INITIALIZED;
                break;
            case CODE_START_B /* 104 */:
                codeSet = 100;
                break;
            case 105:
                codeSet = CODE_CODE_C;
                break;
            default:
                throw FormatException.getFormatInstance();
        }
        boolean done = false;
        boolean isNextShifted = false;
        StringBuilder result = new StringBuilder(20);
        int lastStart = startPatternInfo[0];
        int nextStart = startPatternInfo[1];
        int[] counters = new int[6];
        int lastCode = 0;
        int code = 0;
        int checksumTotal = startCode;
        int multiplier = 0;
        boolean lastCharacterWasPrintable = true;
        while (!done) {
            boolean unshift = isNextShifted;
            isNextShifted = false;
            lastCode = code;
            code = decodeCode(row, counters, nextStart);
            rawCodes.add(Byte.valueOf((byte) code));
            if (code != 106) {
                lastCharacterWasPrintable = true;
            }
            if (code != 106) {
                multiplier++;
                checksumTotal += multiplier * code;
            }
            lastStart = nextStart;
            for (int counter : counters) {
                nextStart += counter;
            }
            switch (code) {
                case 103:
                case CODE_START_B /* 104 */:
                case 105:
                    throw FormatException.getFormatInstance();
                default:
                    switch (codeSet) {
                        case CODE_CODE_C /* 99 */:
                            if (code < 100) {
                                if (code < 10) {
                                    result.append('0');
                                }
                                result.append(code);
                                break;
                            } else {
                                if (code != 106) {
                                    lastCharacterWasPrintable = false;
                                }
                                switch (code) {
                                    case 100:
                                        codeSet = 100;
                                        break;
                                    case ExecutorCreator.INITIALIZED /* 101 */:
                                        codeSet = ExecutorCreator.INITIALIZED;
                                        break;
                                    case 102:
                                        if (convertFNC1) {
                                            if (result.length() == 0) {
                                                result.append("]C1");
                                                break;
                                            } else {
                                                result.append((char) 29);
                                                break;
                                            }
                                        }
                                        break;
                                    case 106:
                                        done = true;
                                        break;
                                }
                            }
                        case 100:
                            if (code < CODE_FNC_3) {
                                result.append((char) (code + 32));
                                break;
                            } else {
                                if (code != 106) {
                                    lastCharacterWasPrintable = false;
                                }
                                switch (code) {
                                    case CODE_SHIFT /* 98 */:
                                        isNextShifted = true;
                                        codeSet = ExecutorCreator.INITIALIZED;
                                        break;
                                    case CODE_CODE_C /* 99 */:
                                        codeSet = CODE_CODE_C;
                                        break;
                                    case ExecutorCreator.INITIALIZED /* 101 */:
                                        codeSet = ExecutorCreator.INITIALIZED;
                                        break;
                                    case 102:
                                        if (convertFNC1) {
                                            if (result.length() == 0) {
                                                result.append("]C1");
                                                break;
                                            } else {
                                                result.append((char) 29);
                                                break;
                                            }
                                        }
                                        break;
                                    case 106:
                                        done = true;
                                        break;
                                }
                            }
                        case ExecutorCreator.INITIALIZED /* 101 */:
                            if (code < MAX_AVG_VARIANCE) {
                                result.append((char) (code + 32));
                                break;
                            } else if (code < CODE_FNC_3) {
                                result.append((char) (code - 64));
                                break;
                            } else {
                                if (code != 106) {
                                    lastCharacterWasPrintable = false;
                                }
                                switch (code) {
                                    case CODE_SHIFT /* 98 */:
                                        isNextShifted = true;
                                        codeSet = 100;
                                        break;
                                    case CODE_CODE_C /* 99 */:
                                        codeSet = CODE_CODE_C;
                                        break;
                                    case 100:
                                        codeSet = 100;
                                        break;
                                    case 102:
                                        if (convertFNC1) {
                                            if (result.length() == 0) {
                                                result.append("]C1");
                                                break;
                                            } else {
                                                result.append((char) 29);
                                                break;
                                            }
                                        }
                                        break;
                                    case 106:
                                        done = true;
                                        break;
                                }
                            }
                    }
                    if (unshift) {
                        codeSet = codeSet == 101 ? 100 : ExecutorCreator.INITIALIZED;
                    }
            }
        }
        int lastPatternSize = nextStart - lastStart;
        int nextStart2 = row.getNextUnset(nextStart);
        if (!row.isRange(nextStart2, Math.min(row.getSize(), ((nextStart2 - lastStart) / 2) + nextStart2), false)) {
            throw NotFoundException.getNotFoundInstance();
        }
        if ((checksumTotal - (multiplier * lastCode)) % 103 != lastCode) {
            throw ChecksumException.getChecksumInstance();
        }
        int resultLength = result.length();
        if (resultLength == 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (resultLength > 0 && lastCharacterWasPrintable) {
            if (codeSet == CODE_CODE_C) {
                result.delete(resultLength - 2, resultLength);
            } else {
                result.delete(resultLength - 1, resultLength);
            }
        }
        float left = (startPatternInfo[1] + startPatternInfo[0]) / 2.0f;
        float right = lastStart + (lastPatternSize / 2.0f);
        int rawCodesSize = rawCodes.size();
        byte[] rawBytes = new byte[rawCodesSize];
        for (int i = 0; i < rawCodesSize; i++) {
            rawBytes[i] = rawCodes.get(i).byteValue();
        }
        return new Result(result.toString(), rawBytes, new ResultPoint[]{new ResultPoint(left, rowNumber), new ResultPoint(right, rowNumber)}, BarcodeFormat.CODE_128);
    }
}
