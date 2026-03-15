package com.google.zxing.pdf417.decoder.ec;

import com.sony.imaging.app.base.playback.LogHelper;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ModulusPoly {
    private final int[] coefficients;
    private final ModulusGF field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly(ModulusGF field, int[] coefficients) {
        if (coefficients.length == 0) {
            throw new IllegalArgumentException();
        }
        this.field = field;
        int coefficientsLength = coefficients.length;
        if (coefficientsLength > 1 && coefficients[0] == 0) {
            int firstNonZero = 1;
            while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0) {
                firstNonZero++;
            }
            if (firstNonZero == coefficientsLength) {
                this.coefficients = field.getZero().coefficients;
                return;
            } else {
                this.coefficients = new int[coefficientsLength - firstNonZero];
                System.arraycopy(coefficients, firstNonZero, this.coefficients, 0, this.coefficients.length);
                return;
            }
        }
        this.coefficients = coefficients;
    }

    int[] getCoefficients() {
        return this.coefficients;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDegree() {
        return this.coefficients.length - 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isZero() {
        return this.coefficients[0] == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCoefficient(int degree) {
        return this.coefficients[(this.coefficients.length - 1) - degree];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int evaluateAt(int a) {
        if (a == 0) {
            return getCoefficient(0);
        }
        int size = this.coefficients.length;
        if (a == 1) {
            int result = 0;
            int[] arr$ = this.coefficients;
            for (int coefficient : arr$) {
                result = this.field.add(result, coefficient);
            }
            return result;
        }
        int result2 = this.coefficients[0];
        for (int i = 1; i < size; i++) {
            result2 = this.field.add(this.field.multiply(a, result2), this.coefficients[i]);
        }
        return result2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly add(ModulusPoly other) {
        if (!this.field.equals(other.field)) {
            throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
        }
        if (!isZero()) {
            if (other.isZero()) {
                return this;
            }
            int[] smallerCoefficients = this.coefficients;
            int[] largerCoefficients = other.coefficients;
            if (smallerCoefficients.length > largerCoefficients.length) {
                smallerCoefficients = largerCoefficients;
                largerCoefficients = smallerCoefficients;
            }
            int[] sumDiff = new int[largerCoefficients.length];
            int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
            System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);
            for (int i = lengthDiff; i < largerCoefficients.length; i++) {
                sumDiff[i] = this.field.add(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
            }
            return new ModulusPoly(this.field, sumDiff);
        }
        return other;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly subtract(ModulusPoly other) {
        if (this.field.equals(other.field)) {
            return other.isZero() ? this : add(other.negative());
        }
        throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly multiply(ModulusPoly other) {
        if (!this.field.equals(other.field)) {
            throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
        }
        if (isZero() || other.isZero()) {
            return this.field.getZero();
        }
        int[] aCoefficients = this.coefficients;
        int aLength = aCoefficients.length;
        int[] bCoefficients = other.coefficients;
        int bLength = bCoefficients.length;
        int[] product = new int[(aLength + bLength) - 1];
        for (int i = 0; i < aLength; i++) {
            int aCoeff = aCoefficients[i];
            for (int j = 0; j < bLength; j++) {
                product[i + j] = this.field.add(product[i + j], this.field.multiply(aCoeff, bCoefficients[j]));
            }
        }
        return new ModulusPoly(this.field, product);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly negative() {
        int size = this.coefficients.length;
        int[] negativeCoefficients = new int[size];
        for (int i = 0; i < size; i++) {
            negativeCoefficients[i] = this.field.subtract(0, this.coefficients[i]);
        }
        return new ModulusPoly(this.field, negativeCoefficients);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly multiply(int scalar) {
        if (scalar == 0) {
            return this.field.getZero();
        }
        if (scalar != 1) {
            int size = this.coefficients.length;
            int[] product = new int[size];
            for (int i = 0; i < size; i++) {
                product[i] = this.field.multiply(this.coefficients[i], scalar);
            }
            return new ModulusPoly(this.field, product);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModulusPoly multiplyByMonomial(int degree, int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return this.field.getZero();
        }
        int size = this.coefficients.length;
        int[] product = new int[size + degree];
        for (int i = 0; i < size; i++) {
            product[i] = this.field.multiply(this.coefficients[i], coefficient);
        }
        return new ModulusPoly(this.field, product);
    }

    ModulusPoly[] divide(ModulusPoly other) {
        if (!this.field.equals(other.field)) {
            throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
        }
        if (other.isZero()) {
            throw new IllegalArgumentException("Divide by 0");
        }
        ModulusPoly quotient = this.field.getZero();
        ModulusPoly remainder = this;
        int denominatorLeadingTerm = other.getCoefficient(other.getDegree());
        int inverseDenominatorLeadingTerm = this.field.inverse(denominatorLeadingTerm);
        while (remainder.getDegree() >= other.getDegree() && !remainder.isZero()) {
            int degreeDifference = remainder.getDegree() - other.getDegree();
            int scale = this.field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
            ModulusPoly term = other.multiplyByMonomial(degreeDifference, scale);
            ModulusPoly iterationQuotient = this.field.buildMonomial(degreeDifference, scale);
            quotient = quotient.add(iterationQuotient);
            remainder = remainder.subtract(term);
        }
        return new ModulusPoly[]{quotient, remainder};
    }

    public String toString() {
        StringBuilder result = new StringBuilder(getDegree() * 8);
        for (int degree = getDegree(); degree >= 0; degree--) {
            int coefficient = getCoefficient(degree);
            if (coefficient != 0) {
                if (coefficient < 0) {
                    result.append(LogHelper.MSG_HYPHEN);
                    coefficient = -coefficient;
                } else if (result.length() > 0) {
                    result.append(" + ");
                }
                if (degree == 0 || coefficient != 1) {
                    result.append(coefficient);
                }
                if (degree != 0) {
                    if (degree == 1) {
                        result.append('x');
                    } else {
                        result.append("x^");
                        result.append(degree);
                    }
                }
            }
        }
        return result.toString();
    }
}
