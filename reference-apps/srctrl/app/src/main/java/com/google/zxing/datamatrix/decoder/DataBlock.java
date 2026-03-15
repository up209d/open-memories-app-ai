package com.google.zxing.datamatrix.decoder;

import com.google.zxing.datamatrix.decoder.Version;

/* loaded from: classes.dex */
final class DataBlock {
    private final byte[] codewords;
    private final int numDataCodewords;

    private DataBlock(int numDataCodewords, byte[] codewords) {
        this.numDataCodewords = numDataCodewords;
        this.codewords = codewords;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DataBlock[] getDataBlocks(byte[] rawCodewords, Version version) {
        int rawCodewordsOffset;
        int rawCodewordsOffset2;
        int rawCodewordsOffset3;
        Version.ECBlocks ecBlocks = version.getECBlocks();
        int totalBlocks = 0;
        Version.ECB[] ecBlockArray = ecBlocks.getECBlocks();
        for (Version.ECB ecb : ecBlockArray) {
            totalBlocks += ecb.getCount();
        }
        DataBlock[] result = new DataBlock[totalBlocks];
        int numResultBlocks = 0;
        for (Version.ECB ecBlock : ecBlockArray) {
            int i = 0;
            while (i < ecBlock.getCount()) {
                int numDataCodewords = ecBlock.getDataCodewords();
                int numBlockCodewords = ecBlocks.getECCodewords() + numDataCodewords;
                result[numResultBlocks] = new DataBlock(numDataCodewords, new byte[numBlockCodewords]);
                i++;
                numResultBlocks++;
            }
        }
        int longerBlocksTotalCodewords = result[0].codewords.length;
        int longerBlocksNumDataCodewords = longerBlocksTotalCodewords - ecBlocks.getECCodewords();
        int shorterBlocksNumDataCodewords = longerBlocksNumDataCodewords - 1;
        int rawCodewordsOffset4 = 0;
        int i2 = 0;
        while (i2 < shorterBlocksNumDataCodewords) {
            int j = 0;
            while (true) {
                rawCodewordsOffset3 = rawCodewordsOffset4;
                if (j < numResultBlocks) {
                    rawCodewordsOffset4 = rawCodewordsOffset3 + 1;
                    result[j].codewords[i2] = rawCodewords[rawCodewordsOffset3];
                    j++;
                }
            }
            i2++;
            rawCodewordsOffset4 = rawCodewordsOffset3;
        }
        boolean specialVersion = version.getVersionNumber() == 24;
        int numLongerBlocks = specialVersion ? 8 : numResultBlocks;
        int j2 = 0;
        while (true) {
            rawCodewordsOffset = rawCodewordsOffset4;
            if (j2 >= numLongerBlocks) {
                break;
            }
            rawCodewordsOffset4 = rawCodewordsOffset + 1;
            result[j2].codewords[longerBlocksNumDataCodewords - 1] = rawCodewords[rawCodewordsOffset];
            j2++;
        }
        int max = result[0].codewords.length;
        int i3 = longerBlocksNumDataCodewords;
        int rawCodewordsOffset5 = rawCodewordsOffset;
        while (i3 < max) {
            int j3 = 0;
            while (true) {
                rawCodewordsOffset2 = rawCodewordsOffset5;
                if (j3 < numResultBlocks) {
                    int iOffset = (!specialVersion || j3 <= 7) ? i3 : i3 - 1;
                    rawCodewordsOffset5 = rawCodewordsOffset2 + 1;
                    result[j3].codewords[iOffset] = rawCodewords[rawCodewordsOffset2];
                    j3++;
                }
            }
            i3++;
            rawCodewordsOffset5 = rawCodewordsOffset2;
        }
        if (rawCodewordsOffset5 != rawCodewords.length) {
            throw new IllegalArgumentException();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNumDataCodewords() {
        return this.numDataCodewords;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getCodewords() {
        return this.codewords;
    }
}
