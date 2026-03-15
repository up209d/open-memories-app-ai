package com.google.zxing.oned.rss.expanded.decoders;

/* loaded from: classes.dex */
final class DecodedInformation extends DecodedObject {
    private final String newString;
    private final boolean remaining;
    private final int remainingValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DecodedInformation(int newPosition, String newString) {
        super(newPosition);
        this.newString = newString;
        this.remaining = false;
        this.remainingValue = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DecodedInformation(int newPosition, String newString, int remainingValue) {
        super(newPosition);
        this.remaining = true;
        this.remainingValue = remainingValue;
        this.newString = newString;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getNewString() {
        return this.newString;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRemaining() {
        return this.remaining;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRemainingValue() {
        return this.remainingValue;
    }
}
