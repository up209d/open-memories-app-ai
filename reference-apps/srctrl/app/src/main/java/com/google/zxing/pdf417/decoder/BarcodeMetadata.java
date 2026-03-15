package com.google.zxing.pdf417.decoder;

/* loaded from: classes.dex */
final class BarcodeMetadata {
    private final int columnCount;
    private final int errorCorrectionLevel;
    private final int rowCount;
    private final int rowCountLowerPart;
    private final int rowCountUpperPart;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BarcodeMetadata(int columnCount, int rowCountUpperPart, int rowCountLowerPart, int errorCorrectionLevel) {
        this.columnCount = columnCount;
        this.errorCorrectionLevel = errorCorrectionLevel;
        this.rowCountUpperPart = rowCountUpperPart;
        this.rowCountLowerPart = rowCountLowerPart;
        this.rowCount = rowCountUpperPart + rowCountLowerPart;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getColumnCount() {
        return this.columnCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getErrorCorrectionLevel() {
        return this.errorCorrectionLevel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRowCount() {
        return this.rowCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRowCountUpperPart() {
        return this.rowCountUpperPart;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRowCountLowerPart() {
        return this.rowCountLowerPart;
    }
}
