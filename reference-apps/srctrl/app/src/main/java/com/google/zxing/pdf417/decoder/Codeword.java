package com.google.zxing.pdf417.decoder;

/* loaded from: classes.dex */
final class Codeword {
    private static final int BARCODE_ROW_UNKNOWN = -1;
    private final int bucket;
    private final int endX;
    private int rowNumber = -1;
    private final int startX;
    private final int value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Codeword(int startX, int endX, int bucket, int value) {
        this.startX = startX;
        this.endX = endX;
        this.bucket = bucket;
        this.value = value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasValidRowNumber() {
        return isValidRowNumber(this.rowNumber);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isValidRowNumber(int rowNumber) {
        return rowNumber != -1 && this.bucket == (rowNumber % 3) * 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRowNumberAsRowIndicatorColumn() {
        this.rowNumber = ((this.value / 30) * 3) + (this.bucket / 3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getWidth() {
        return this.endX - this.startX;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getStartX() {
        return this.startX;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getEndX() {
        return this.endX;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getBucket() {
        return this.bucket;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getValue() {
        return this.value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRowNumber() {
        return this.rowNumber;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String toString() {
        return this.rowNumber + "|" + this.value;
    }
}
