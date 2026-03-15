package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class Token {
    static final Token EMPTY = new SimpleToken(null, 0, 0);
    private final Token previous;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void appendTo(BitArray bitArray, byte[] bArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Token(Token previous) {
        this.previous = previous;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Token getPrevious() {
        return this.previous;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Token add(int value, int bitCount) {
        return new SimpleToken(this, value, bitCount);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Token addBinaryShift(int start, int byteCount) {
        return new BinaryShiftToken(this, start, byteCount);
    }
}
