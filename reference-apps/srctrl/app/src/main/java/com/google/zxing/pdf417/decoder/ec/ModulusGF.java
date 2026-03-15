package com.google.zxing.pdf417.decoder.ec;

import com.google.zxing.pdf417.PDF417Common;

/* loaded from: classes.dex */
public final class ModulusGF {
    public static final ModulusGF PDF417_GF = new ModulusGF(PDF417Common.NUMBER_OF_CODEWORDS, 3);
    private final int[] expTable;
    private final int[] logTable;
    private final int modulus;
    private final ModulusPoly one;
    private final ModulusPoly zero;

    private ModulusGF(int modulus, int generator) {
        this.modulus = modulus;
        this.expTable = new int[modulus];
        this.logTable = new int[modulus];
        int x = 1;
        for (int i = 0; i < modulus; i++) {
            this.expTable[i] = x;
            x = (x * generator) % modulus;
        }
        for (int i2 = 0; i2 < modulus - 1; i2++) {
            this.logTable[this.expTable[i2]] = i2;
        }
        this.zero = new ModulusPoly(this, new int[]{0});
        this.one = new ModulusPoly(this, new int[]{1});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly getZero() {
        return this.zero;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly getOne() {
        return this.one;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly buildMonomial(int degree, int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return this.zero;
        }
        int[] coefficients = new int[degree + 1];
        coefficients[0] = coefficient;
        return new ModulusPoly(this, coefficients);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int add(int a, int b) {
        return (a + b) % this.modulus;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int subtract(int a, int b) {
        return ((this.modulus + a) - b) % this.modulus;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int exp(int a) {
        return this.expTable[a];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int log(int a) {
        if (a == 0) {
            throw new IllegalArgumentException();
        }
        return this.logTable[a];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int inverse(int a) {
        if (a == 0) {
            throw new ArithmeticException();
        }
        return this.expTable[(this.modulus - this.logTable[a]) - 1];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int multiply(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return this.expTable[(this.logTable[a] + this.logTable[b]) % (this.modulus - 1)];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSize() {
        return this.modulus;
    }
}
