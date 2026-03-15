package com.google.zxing.datamatrix.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;

/* loaded from: classes.dex */
final class BitMatrixParser {
    private final BitMatrix mappingBitMatrix;
    private final BitMatrix readMappingMatrix;
    private final Version version;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitMatrixParser(BitMatrix bitMatrix) throws FormatException {
        int dimension = bitMatrix.getHeight();
        if (dimension < 8 || dimension > 144 || (dimension & 1) != 0) {
            throw FormatException.getFormatInstance();
        }
        this.version = readVersion(bitMatrix);
        this.mappingBitMatrix = extractDataRegion(bitMatrix);
        this.readMappingMatrix = new BitMatrix(this.mappingBitMatrix.getWidth(), this.mappingBitMatrix.getHeight());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Version getVersion() {
        return this.version;
    }

    private static Version readVersion(BitMatrix bitMatrix) throws FormatException {
        int numRows = bitMatrix.getHeight();
        int numColumns = bitMatrix.getWidth();
        return Version.getVersionForDimensions(numRows, numColumns);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] readCodewords() throws FormatException {
        int resultOffset;
        int resultOffset2;
        byte[] result = new byte[this.version.getTotalCodewords()];
        int row = 4;
        int column = 0;
        int numRows = this.mappingBitMatrix.getHeight();
        int numColumns = this.mappingBitMatrix.getWidth();
        boolean corner1Read = false;
        boolean corner2Read = false;
        boolean corner3Read = false;
        boolean corner4Read = false;
        int resultOffset3 = 0;
        while (true) {
            if (row == numRows && column == 0 && !corner1Read) {
                resultOffset2 = resultOffset3 + 1;
                result[resultOffset3] = (byte) readCorner1(numRows, numColumns);
                row -= 2;
                column += 2;
                corner1Read = true;
            } else if (row == numRows - 2 && column == 0 && (numColumns & 3) != 0 && !corner2Read) {
                resultOffset2 = resultOffset3 + 1;
                result[resultOffset3] = (byte) readCorner2(numRows, numColumns);
                row -= 2;
                column += 2;
                corner2Read = true;
            } else if (row == numRows + 4 && column == 2 && (numColumns & 7) == 0 && !corner3Read) {
                resultOffset2 = resultOffset3 + 1;
                result[resultOffset3] = (byte) readCorner3(numRows, numColumns);
                row -= 2;
                column += 2;
                corner3Read = true;
            } else if (row == numRows - 2 && column == 0 && (numColumns & 7) == 4 && !corner4Read) {
                resultOffset2 = resultOffset3 + 1;
                result[resultOffset3] = (byte) readCorner4(numRows, numColumns);
                row -= 2;
                column += 2;
                corner4Read = true;
            } else {
                while (true) {
                    if (row >= numRows || column < 0 || this.readMappingMatrix.get(column, row)) {
                        resultOffset = resultOffset3;
                    } else {
                        resultOffset = resultOffset3 + 1;
                        result[resultOffset3] = (byte) readUtah(row, column, numRows, numColumns);
                    }
                    row -= 2;
                    column += 2;
                    if (row < 0 || column >= numColumns) {
                        break;
                    }
                    resultOffset3 = resultOffset;
                }
                int row2 = row + 1;
                int column2 = column + 3;
                int resultOffset4 = resultOffset;
                while (true) {
                    if (row2 < 0 || column2 >= numColumns || this.readMappingMatrix.get(column2, row2)) {
                        resultOffset2 = resultOffset4;
                    } else {
                        resultOffset2 = resultOffset4 + 1;
                        result[resultOffset4] = (byte) readUtah(row2, column2, numRows, numColumns);
                    }
                    row2 += 2;
                    column2 -= 2;
                    if (row2 >= numRows || column2 < 0) {
                        break;
                    }
                    resultOffset4 = resultOffset2;
                }
                row = row2 + 3;
                column = column2 + 1;
            }
            if (row >= numRows && column >= numColumns) {
                break;
            }
            resultOffset3 = resultOffset2;
        }
        if (resultOffset2 != this.version.getTotalCodewords()) {
            throw FormatException.getFormatInstance();
        }
        return result;
    }

    boolean readModule(int row, int column, int numRows, int numColumns) {
        if (row < 0) {
            row += numRows;
            column += 4 - ((numRows + 4) & 7);
        }
        if (column < 0) {
            column += numColumns;
            row += 4 - ((numColumns + 4) & 7);
        }
        this.readMappingMatrix.set(column, row);
        return this.mappingBitMatrix.get(column, row);
    }

    int readUtah(int row, int column, int numRows, int numColumns) {
        int currentByte = 0;
        if (readModule(row - 2, column - 2, numRows, numColumns)) {
            currentByte = 0 | 1;
        }
        int currentByte2 = currentByte << 1;
        if (readModule(row - 2, column - 1, numRows, numColumns)) {
            currentByte2 |= 1;
        }
        int currentByte3 = currentByte2 << 1;
        if (readModule(row - 1, column - 2, numRows, numColumns)) {
            currentByte3 |= 1;
        }
        int currentByte4 = currentByte3 << 1;
        if (readModule(row - 1, column - 1, numRows, numColumns)) {
            currentByte4 |= 1;
        }
        int currentByte5 = currentByte4 << 1;
        if (readModule(row - 1, column, numRows, numColumns)) {
            currentByte5 |= 1;
        }
        int currentByte6 = currentByte5 << 1;
        if (readModule(row, column - 2, numRows, numColumns)) {
            currentByte6 |= 1;
        }
        int currentByte7 = currentByte6 << 1;
        if (readModule(row, column - 1, numRows, numColumns)) {
            currentByte7 |= 1;
        }
        int currentByte8 = currentByte7 << 1;
        if (readModule(row, column, numRows, numColumns)) {
            return currentByte8 | 1;
        }
        return currentByte8;
    }

    int readCorner1(int numRows, int numColumns) {
        int currentByte = 0;
        if (readModule(numRows - 1, 0, numRows, numColumns)) {
            currentByte = 0 | 1;
        }
        int currentByte2 = currentByte << 1;
        if (readModule(numRows - 1, 1, numRows, numColumns)) {
            currentByte2 |= 1;
        }
        int currentByte3 = currentByte2 << 1;
        if (readModule(numRows - 1, 2, numRows, numColumns)) {
            currentByte3 |= 1;
        }
        int currentByte4 = currentByte3 << 1;
        if (readModule(0, numColumns - 2, numRows, numColumns)) {
            currentByte4 |= 1;
        }
        int currentByte5 = currentByte4 << 1;
        if (readModule(0, numColumns - 1, numRows, numColumns)) {
            currentByte5 |= 1;
        }
        int currentByte6 = currentByte5 << 1;
        if (readModule(1, numColumns - 1, numRows, numColumns)) {
            currentByte6 |= 1;
        }
        int currentByte7 = currentByte6 << 1;
        if (readModule(2, numColumns - 1, numRows, numColumns)) {
            currentByte7 |= 1;
        }
        int currentByte8 = currentByte7 << 1;
        if (readModule(3, numColumns - 1, numRows, numColumns)) {
            return currentByte8 | 1;
        }
        return currentByte8;
    }

    int readCorner2(int numRows, int numColumns) {
        int currentByte = 0;
        if (readModule(numRows - 3, 0, numRows, numColumns)) {
            currentByte = 0 | 1;
        }
        int currentByte2 = currentByte << 1;
        if (readModule(numRows - 2, 0, numRows, numColumns)) {
            currentByte2 |= 1;
        }
        int currentByte3 = currentByte2 << 1;
        if (readModule(numRows - 1, 0, numRows, numColumns)) {
            currentByte3 |= 1;
        }
        int currentByte4 = currentByte3 << 1;
        if (readModule(0, numColumns - 4, numRows, numColumns)) {
            currentByte4 |= 1;
        }
        int currentByte5 = currentByte4 << 1;
        if (readModule(0, numColumns - 3, numRows, numColumns)) {
            currentByte5 |= 1;
        }
        int currentByte6 = currentByte5 << 1;
        if (readModule(0, numColumns - 2, numRows, numColumns)) {
            currentByte6 |= 1;
        }
        int currentByte7 = currentByte6 << 1;
        if (readModule(0, numColumns - 1, numRows, numColumns)) {
            currentByte7 |= 1;
        }
        int currentByte8 = currentByte7 << 1;
        if (readModule(1, numColumns - 1, numRows, numColumns)) {
            return currentByte8 | 1;
        }
        return currentByte8;
    }

    int readCorner3(int numRows, int numColumns) {
        int currentByte = 0;
        if (readModule(numRows - 1, 0, numRows, numColumns)) {
            currentByte = 0 | 1;
        }
        int currentByte2 = currentByte << 1;
        if (readModule(numRows - 1, numColumns - 1, numRows, numColumns)) {
            currentByte2 |= 1;
        }
        int currentByte3 = currentByte2 << 1;
        if (readModule(0, numColumns - 3, numRows, numColumns)) {
            currentByte3 |= 1;
        }
        int currentByte4 = currentByte3 << 1;
        if (readModule(0, numColumns - 2, numRows, numColumns)) {
            currentByte4 |= 1;
        }
        int currentByte5 = currentByte4 << 1;
        if (readModule(0, numColumns - 1, numRows, numColumns)) {
            currentByte5 |= 1;
        }
        int currentByte6 = currentByte5 << 1;
        if (readModule(1, numColumns - 3, numRows, numColumns)) {
            currentByte6 |= 1;
        }
        int currentByte7 = currentByte6 << 1;
        if (readModule(1, numColumns - 2, numRows, numColumns)) {
            currentByte7 |= 1;
        }
        int currentByte8 = currentByte7 << 1;
        if (readModule(1, numColumns - 1, numRows, numColumns)) {
            return currentByte8 | 1;
        }
        return currentByte8;
    }

    int readCorner4(int numRows, int numColumns) {
        int currentByte = 0;
        if (readModule(numRows - 3, 0, numRows, numColumns)) {
            currentByte = 0 | 1;
        }
        int currentByte2 = currentByte << 1;
        if (readModule(numRows - 2, 0, numRows, numColumns)) {
            currentByte2 |= 1;
        }
        int currentByte3 = currentByte2 << 1;
        if (readModule(numRows - 1, 0, numRows, numColumns)) {
            currentByte3 |= 1;
        }
        int currentByte4 = currentByte3 << 1;
        if (readModule(0, numColumns - 2, numRows, numColumns)) {
            currentByte4 |= 1;
        }
        int currentByte5 = currentByte4 << 1;
        if (readModule(0, numColumns - 1, numRows, numColumns)) {
            currentByte5 |= 1;
        }
        int currentByte6 = currentByte5 << 1;
        if (readModule(1, numColumns - 1, numRows, numColumns)) {
            currentByte6 |= 1;
        }
        int currentByte7 = currentByte6 << 1;
        if (readModule(2, numColumns - 1, numRows, numColumns)) {
            currentByte7 |= 1;
        }
        int currentByte8 = currentByte7 << 1;
        if (readModule(3, numColumns - 1, numRows, numColumns)) {
            return currentByte8 | 1;
        }
        return currentByte8;
    }

    BitMatrix extractDataRegion(BitMatrix bitMatrix) {
        int symbolSizeRows = this.version.getSymbolSizeRows();
        int symbolSizeColumns = this.version.getSymbolSizeColumns();
        if (bitMatrix.getHeight() != symbolSizeRows) {
            throw new IllegalArgumentException("Dimension of bitMarix must match the version size");
        }
        int dataRegionSizeRows = this.version.getDataRegionSizeRows();
        int dataRegionSizeColumns = this.version.getDataRegionSizeColumns();
        int numDataRegionsRow = symbolSizeRows / dataRegionSizeRows;
        int numDataRegionsColumn = symbolSizeColumns / dataRegionSizeColumns;
        int sizeDataRegionRow = numDataRegionsRow * dataRegionSizeRows;
        int sizeDataRegionColumn = numDataRegionsColumn * dataRegionSizeColumns;
        BitMatrix bitMatrixWithoutAlignment = new BitMatrix(sizeDataRegionColumn, sizeDataRegionRow);
        for (int dataRegionRow = 0; dataRegionRow < numDataRegionsRow; dataRegionRow++) {
            int dataRegionRowOffset = dataRegionRow * dataRegionSizeRows;
            for (int dataRegionColumn = 0; dataRegionColumn < numDataRegionsColumn; dataRegionColumn++) {
                int dataRegionColumnOffset = dataRegionColumn * dataRegionSizeColumns;
                for (int i = 0; i < dataRegionSizeRows; i++) {
                    int readRowOffset = ((dataRegionSizeRows + 2) * dataRegionRow) + 1 + i;
                    int writeRowOffset = dataRegionRowOffset + i;
                    for (int j = 0; j < dataRegionSizeColumns; j++) {
                        int readColumnOffset = ((dataRegionSizeColumns + 2) * dataRegionColumn) + 1 + j;
                        if (bitMatrix.get(readColumnOffset, readRowOffset)) {
                            int writeColumnOffset = dataRegionColumnOffset + j;
                            bitMatrixWithoutAlignment.set(writeColumnOffset, writeRowOffset);
                        }
                    }
                }
            }
        }
        return bitMatrixWithoutAlignment;
    }
}
