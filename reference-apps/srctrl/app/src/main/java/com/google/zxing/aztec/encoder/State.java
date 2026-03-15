package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;
import java.util.Deque;
import java.util.LinkedList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class State {
    static final State INITIAL_STATE = new State(Token.EMPTY, 0, 0, 0);
    private final int binaryShiftByteCount;
    private final int bitCount;
    private final int mode;
    private final Token token;

    private State(Token token, int mode, int binaryBytes, int bitCount) {
        this.token = token;
        this.mode = mode;
        this.binaryShiftByteCount = binaryBytes;
        this.bitCount = bitCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMode() {
        return this.mode;
    }

    Token getToken() {
        return this.token;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getBinaryShiftByteCount() {
        return this.binaryShiftByteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getBitCount() {
        return this.bitCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public State latchAndAppend(int mode, int value) {
        int bitCount = this.bitCount;
        Token token = this.token;
        if (mode != this.mode) {
            int latch = HighLevelEncoder.LATCH_TABLE[this.mode][mode];
            token = token.add(65535 & latch, latch >> 16);
            bitCount += latch >> 16;
        }
        int latchModeBitCount = mode == 2 ? 4 : 5;
        return new State(token.add(value, latchModeBitCount), mode, 0, bitCount + latchModeBitCount);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public State shiftAndAppend(int mode, int value) {
        Token token = this.token;
        int thisModeBitCount = this.mode == 2 ? 4 : 5;
        return new State(token.add(HighLevelEncoder.SHIFT_TABLE[this.mode][mode], thisModeBitCount).add(value, 5), this.mode, 0, this.bitCount + thisModeBitCount + 5);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public State addBinaryShiftChar(int index) {
        int deltaBitCount;
        Token token = this.token;
        int mode = this.mode;
        int bitCount = this.bitCount;
        if (this.mode == 4 || this.mode == 2) {
            int latch = HighLevelEncoder.LATCH_TABLE[mode][0];
            token = token.add(65535 & latch, latch >> 16);
            bitCount += latch >> 16;
            mode = 0;
        }
        if (this.binaryShiftByteCount == 0 || this.binaryShiftByteCount == 31) {
            deltaBitCount = 18;
        } else {
            deltaBitCount = this.binaryShiftByteCount == 62 ? 9 : 8;
        }
        State result = new State(token, mode, this.binaryShiftByteCount + 1, bitCount + deltaBitCount);
        if (result.binaryShiftByteCount == 2078) {
            return result.endBinaryShift(index + 1);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public State endBinaryShift(int index) {
        if (this.binaryShiftByteCount != 0) {
            Token token = this.token;
            return new State(token.addBinaryShift(index - this.binaryShiftByteCount, this.binaryShiftByteCount), this.mode, 0, this.bitCount);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isBetterThanOrEqualTo(State other) {
        int mySize = this.bitCount + (HighLevelEncoder.LATCH_TABLE[this.mode][other.mode] >> 16);
        if (other.binaryShiftByteCount > 0 && (this.binaryShiftByteCount == 0 || this.binaryShiftByteCount > other.binaryShiftByteCount)) {
            mySize += 10;
        }
        return mySize <= other.bitCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitArray toBitArray(byte[] text) {
        Deque<Token> symbols = new LinkedList<>();
        for (Token token = endBinaryShift(text.length).token; token != null; token = token.getPrevious()) {
            symbols.addFirst(token);
        }
        BitArray bitArray = new BitArray();
        for (Token symbol : symbols) {
            symbol.appendTo(bitArray, text);
        }
        return bitArray;
    }

    public String toString() {
        return String.format("%s bits=%d bytes=%d", HighLevelEncoder.MODE_NAMES[this.mode], Integer.valueOf(this.bitCount), Integer.valueOf(this.binaryShiftByteCount));
    }
}
