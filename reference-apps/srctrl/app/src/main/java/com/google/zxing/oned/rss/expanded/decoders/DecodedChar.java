package com.google.zxing.oned.rss.expanded.decoders;

/* loaded from: classes.dex */
final class DecodedChar extends DecodedObject {
    static final char FNC1 = '$';
    private final char value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DecodedChar(int newPosition, char value) {
        super(newPosition);
        this.value = value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public char getValue() {
        return this.value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFNC1() {
        return this.value == '$';
    }
}
