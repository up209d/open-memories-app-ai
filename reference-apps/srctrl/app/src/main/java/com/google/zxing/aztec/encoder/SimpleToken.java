package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;

/* loaded from: classes.dex */
final class SimpleToken extends Token {
    private final short bitCount;
    private final short value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleToken(Token previous, int value, int bitCount) {
        super(previous);
        this.value = (short) value;
        this.bitCount = (short) bitCount;
    }

    @Override // com.google.zxing.aztec.encoder.Token
    void appendTo(BitArray bitArray, byte[] text) {
        bitArray.appendBits(this.value, this.bitCount);
    }

    public String toString() {
        int value = this.value & ((1 << this.bitCount) - 1);
        return '<' + Integer.toBinaryString((1 << this.bitCount) | value | (1 << this.bitCount)).substring(1) + '>';
    }
}
