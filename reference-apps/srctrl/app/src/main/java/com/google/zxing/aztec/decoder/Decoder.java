package com.google.zxing.aztec.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.aztec.AztecDetectorResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager;
import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.imaging.app.srctrl.liveview.LiveviewContainer;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class Decoder {
    private AztecDetectorResult ddata;
    private static final String[] UPPER_TABLE = {"CTRL_PS", ExposureModeController.SOFT_SNAP, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", WhiteBalanceController.DISP_TEXT_K, LiveviewContainer.s_LARGE_LIVEVIEW_SIZE, LiveviewContainer.s_DEFAULT_LIVEVIEW_SIZE, DatabaseManager.VAL_NTSC, "O", DatabaseManager.VAL_PAL, "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "CTRL_LL", "CTRL_ML", "CTRL_DL", "CTRL_BS"};
    private static final String[] LOWER_TABLE = {"CTRL_PS", ExposureModeController.SOFT_SNAP, "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "CTRL_US", "CTRL_ML", "CTRL_DL", "CTRL_BS"};
    private static final String[] MIXED_TABLE = {"CTRL_PS", ExposureModeController.SOFT_SNAP, "\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\b", "\t", "\n", "\u000b", "\f", "\r", "\u001b", "\u001c", "\u001d", "\u001e", "\u001f", "@", "\\", "^", SRCtrlConstants.URI_CONTENT_ID_SEPARATOR, "`", "|", "~", "\u007f", "CTRL_LL", "CTRL_UL", "CTRL_PL", "CTRL_BS"};
    private static final String[] PUNCT_TABLE = {"", "\r", "\r\n", ". ", ", ", AbstractSupportedChecker.COLON, "!", "\"", "#", "$", "%", "&", "'", LogHelper.MSG_OPEN_BRACKET, LogHelper.MSG_CLOSE_BRACKET, "*", "+", ",", "-", StringBuilderThreadLocal.PERIOD, "/", ":", MovieFormatController.Settings.SEMI_COLON, "<", MovieFormatController.Settings.EQUAL, ">", "?", "[", "]", "{", "}", "CTRL_UL"};
    private static final String[] DIGIT_TABLE = {"CTRL_PS", ExposureModeController.SOFT_SNAP, ISOSensitivityController.ISO_AUTO, "1", "2", "3", "4", "5", "6", "7", "8", "9", ",", StringBuilderThreadLocal.PERIOD, "CTRL_UL", "CTRL_US"};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Table {
        UPPER,
        LOWER,
        MIXED,
        DIGIT,
        PUNCT,
        BINARY
    }

    public DecoderResult decode(AztecDetectorResult detectorResult) throws FormatException {
        this.ddata = detectorResult;
        BitMatrix matrix = detectorResult.getBits();
        boolean[] rawbits = extractBits(matrix);
        boolean[] correctedBits = correctBits(rawbits);
        String result = getEncodedData(correctedBits);
        return new DecoderResult(null, result, null, null);
    }

    public static String highLevelDecode(boolean[] correctedBits) {
        return getEncodedData(correctedBits);
    }

    private static String getEncodedData(boolean[] correctedBits) {
        int endIndex = correctedBits.length;
        Table latchTable = Table.UPPER;
        Table shiftTable = Table.UPPER;
        StringBuilder result = new StringBuilder(20);
        int index = 0;
        while (index < endIndex) {
            if (shiftTable == Table.BINARY) {
                if (endIndex - index < 5) {
                    break;
                }
                int length = readCode(correctedBits, index, 5);
                index += 5;
                if (length == 0) {
                    if (endIndex - index < 11) {
                        break;
                    }
                    length = readCode(correctedBits, index, 11) + 31;
                    index += 11;
                }
                int charCount = 0;
                while (true) {
                    if (charCount >= length) {
                        break;
                    }
                    if (endIndex - index < 8) {
                        index = endIndex;
                        break;
                    }
                    int code = readCode(correctedBits, index, 8);
                    result.append((char) code);
                    index += 8;
                    charCount++;
                }
                shiftTable = latchTable;
            } else {
                int size = shiftTable == Table.DIGIT ? 4 : 5;
                if (endIndex - index < size) {
                    break;
                }
                int code2 = readCode(correctedBits, index, size);
                index += size;
                String str = getCharacter(shiftTable, code2);
                if (str.startsWith("CTRL_")) {
                    shiftTable = getTable(str.charAt(5));
                    if (str.charAt(6) == 'L') {
                        latchTable = shiftTable;
                    }
                } else {
                    result.append(str);
                    shiftTable = latchTable;
                }
            }
        }
        return result.toString();
    }

    private static Table getTable(char t) {
        switch (t) {
            case 'B':
                return Table.BINARY;
            case 'D':
                return Table.DIGIT;
            case 'L':
                return Table.LOWER;
            case 'M':
                return Table.MIXED;
            case 'P':
                return Table.PUNCT;
            default:
                return Table.UPPER;
        }
    }

    private static String getCharacter(Table table, int code) {
        switch (table) {
            case UPPER:
                return UPPER_TABLE[code];
            case LOWER:
                return LOWER_TABLE[code];
            case MIXED:
                return MIXED_TABLE[code];
            case PUNCT:
                return PUNCT_TABLE[code];
            case DIGIT:
                return DIGIT_TABLE[code];
            default:
                throw new IllegalStateException("Bad table");
        }
    }

    private boolean[] correctBits(boolean[] rawbits) throws FormatException {
        int codewordSize;
        GenericGF gf;
        if (this.ddata.getNbLayers() <= 2) {
            codewordSize = 6;
            gf = GenericGF.AZTEC_DATA_6;
        } else if (this.ddata.getNbLayers() <= 8) {
            codewordSize = 8;
            gf = GenericGF.AZTEC_DATA_8;
        } else if (this.ddata.getNbLayers() <= 22) {
            codewordSize = 10;
            gf = GenericGF.AZTEC_DATA_10;
        } else {
            codewordSize = 12;
            gf = GenericGF.AZTEC_DATA_12;
        }
        int numDataCodewords = this.ddata.getNbDatablocks();
        int numCodewords = rawbits.length / codewordSize;
        int offset = rawbits.length % codewordSize;
        int numECCodewords = numCodewords - numDataCodewords;
        int[] dataWords = new int[numCodewords];
        int i = 0;
        while (i < numCodewords) {
            dataWords[i] = readCode(rawbits, offset, codewordSize);
            i++;
            offset += codewordSize;
        }
        try {
            ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(gf);
            rsDecoder.decode(dataWords, numECCodewords);
            int mask = (1 << codewordSize) - 1;
            int stuffedBits = 0;
            for (int i2 = 0; i2 < numDataCodewords; i2++) {
                int dataWord = dataWords[i2];
                if (dataWord == 0 || dataWord == mask) {
                    throw FormatException.getFormatInstance();
                }
                if (dataWord == 1 || dataWord == mask - 1) {
                    stuffedBits++;
                }
            }
            boolean[] correctedBits = new boolean[(numDataCodewords * codewordSize) - stuffedBits];
            int index = 0;
            for (int i3 = 0; i3 < numDataCodewords; i3++) {
                int dataWord2 = dataWords[i3];
                if (dataWord2 == 1 || dataWord2 == mask - 1) {
                    Arrays.fill(correctedBits, index, (index + codewordSize) - 1, dataWord2 > 1);
                    index += codewordSize - 1;
                } else {
                    int bit = codewordSize - 1;
                    int index2 = index;
                    while (bit >= 0) {
                        int index3 = index2 + 1;
                        correctedBits[index2] = ((1 << bit) & dataWord2) != 0;
                        bit--;
                        index2 = index3;
                    }
                    index = index2;
                }
            }
            return correctedBits;
        } catch (ReedSolomonException e) {
            throw FormatException.getFormatInstance();
        }
    }

    boolean[] extractBits(BitMatrix matrix) {
        boolean compact = this.ddata.isCompact();
        int layers = this.ddata.getNbLayers();
        int baseMatrixSize = compact ? (layers * 4) + 11 : (layers * 4) + 14;
        int[] alignmentMap = new int[baseMatrixSize];
        boolean[] rawbits = new boolean[totalBitsInLayer(layers, compact)];
        if (compact) {
            for (int i = 0; i < alignmentMap.length; i++) {
                alignmentMap[i] = i;
            }
        } else {
            int matrixSize = baseMatrixSize + 1 + ((((baseMatrixSize / 2) - 1) / 15) * 2);
            int origCenter = baseMatrixSize / 2;
            int center = matrixSize / 2;
            for (int i2 = 0; i2 < origCenter; i2++) {
                int newOffset = i2 + (i2 / 15);
                alignmentMap[(origCenter - i2) - 1] = (center - newOffset) - 1;
                alignmentMap[origCenter + i2] = center + newOffset + 1;
            }
        }
        int rowOffset = 0;
        for (int i3 = 0; i3 < layers; i3++) {
            int rowSize = compact ? ((layers - i3) * 4) + 9 : ((layers - i3) * 4) + 12;
            int low = i3 * 2;
            int high = (baseMatrixSize - 1) - low;
            for (int j = 0; j < rowSize; j++) {
                int columnOffset = j * 2;
                for (int k = 0; k < 2; k++) {
                    rawbits[rowOffset + columnOffset + k] = matrix.get(alignmentMap[low + k], alignmentMap[low + j]);
                    rawbits[(rowSize * 2) + rowOffset + columnOffset + k] = matrix.get(alignmentMap[low + j], alignmentMap[high - k]);
                    rawbits[(rowSize * 4) + rowOffset + columnOffset + k] = matrix.get(alignmentMap[high - k], alignmentMap[high - j]);
                    rawbits[(rowSize * 6) + rowOffset + columnOffset + k] = matrix.get(alignmentMap[high - j], alignmentMap[low + k]);
                }
            }
            rowOffset += rowSize * 8;
        }
        return rawbits;
    }

    private static int readCode(boolean[] rawbits, int startIndex, int length) {
        int res = 0;
        for (int i = startIndex; i < startIndex + length; i++) {
            res <<= 1;
            if (rawbits[i]) {
                res++;
            }
        }
        return res;
    }

    private static int totalBitsInLayer(int layers, boolean compact) {
        return ((compact ? 88 : 112) + (layers * 16)) * layers;
    }
}
