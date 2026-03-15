package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.FormatException;

/* loaded from: classes.dex */
final class DecodedNumeric extends DecodedObject {
    static final int FNC1 = 10;
    private final int firstDigit;
    private final int secondDigit;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DecodedNumeric(int newPosition, int firstDigit, int secondDigit) throws FormatException {
        super(newPosition);
        if (firstDigit < 0 || firstDigit > 10 || secondDigit < 0 || secondDigit > 10) {
            throw FormatException.getFormatInstance();
        }
        this.firstDigit = firstDigit;
        this.secondDigit = secondDigit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFirstDigit() {
        return this.firstDigit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSecondDigit() {
        return this.secondDigit;
    }

    int getValue() {
        return (this.firstDigit * 10) + this.secondDigit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFirstDigitFNC1() {
        return this.firstDigit == 10;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSecondDigitFNC1() {
        return this.secondDigit == 10;
    }

    boolean isAnyFNC1() {
        return this.firstDigit == 10 || this.secondDigit == 10;
    }
}
