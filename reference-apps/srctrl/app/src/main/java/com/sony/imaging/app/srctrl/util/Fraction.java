package com.sony.imaging.app.srctrl.util;

import android.util.Pair;
import java.util.Comparator;

/* loaded from: classes.dex */
public class Fraction {
    public static final FractionComparator COMPARATOR = new FractionComparator();
    private long denominator;
    private long numerator;

    /* loaded from: classes.dex */
    public static class FractionComparator implements Comparator<Fraction> {
        @Override // java.util.Comparator
        public int compare(Fraction arg0, Fraction arg1) {
            return arg0.compare(arg1);
        }
    }

    private Fraction() {
    }

    public Fraction(String source) {
        int pos = source.indexOf(34);
        source = -1 != pos ? source.substring(0, pos) : source;
        int pos2 = source.indexOf(46);
        if (-1 != pos2) {
            String decimal = source.substring(pos2 + 1, source.length());
            float f = Float.parseFloat(source);
            this.denominator = (long) Math.pow(10.0d, decimal.length());
            this.numerator = ((float) this.denominator) * f;
            return;
        }
        int pos3 = source.indexOf("/");
        if (-1 != pos3) {
            String positive = source.substring(0, pos3);
            String decimal2 = source.substring(pos3 + 1, source.length());
            this.numerator = Long.parseLong(positive);
            this.denominator = Long.parseLong(decimal2);
            return;
        }
        this.numerator = Long.parseLong(source);
        this.denominator = 1L;
    }

    public Fraction(Pair<Integer, Integer> source) throws ArithmeticException {
        this(((Integer) source.first).intValue(), ((Integer) source.second).intValue());
    }

    public Fraction(int numerator, int denominator) throws ArithmeticException {
        if (0 == denominator) {
            throw new ArithmeticException();
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public Fraction(Fraction source) throws ArithmeticException {
        this.numerator = source.numerator;
        this.denominator = source.denominator;
        if (0 == this.denominator) {
            throw new ArithmeticException();
        }
    }

    public int compare(Fraction ref) {
        Fraction l = m1clone();
        Fraction r = ref.m1clone();
        if (l.denominator > r.denominator) {
            if (0 == l.denominator % r.denominator) {
                r.numerator = (r.numerator * l.denominator) / r.denominator;
                r.denominator = l.denominator;
            } else {
                long l_numerator = l.numerator * r.denominator;
                long l_denominator = l.denominator * r.denominator;
                long r_numerator = r.numerator * l.denominator;
                long r_denominator = r.denominator * l.denominator;
                l.numerator = l_numerator;
                l.denominator = l_denominator;
                r.numerator = r_numerator;
                r.denominator = r_denominator;
            }
        }
        if (l.denominator < r.denominator) {
            if (0 == r.denominator % l.denominator) {
                l.numerator = (l.numerator * r.denominator) / l.denominator;
                l.denominator = r.denominator;
            } else {
                long l_numerator2 = l.numerator * r.denominator;
                long l_denominator2 = l.denominator * r.denominator;
                long r_numerator2 = r.numerator * l.denominator;
                long r_denominator2 = r.denominator * l.denominator;
                l.numerator = l_numerator2;
                l.denominator = l_denominator2;
                r.numerator = r_numerator2;
                r.denominator = r_denominator2;
            }
        }
        if (l.numerator == r.numerator) {
            return 0;
        }
        if (l.numerator > r.numerator) {
            return 1;
        }
        return -1;
    }

    public String toString() {
        return this.numerator + "/" + this.denominator;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Fraction m1clone() {
        return new Fraction(this);
    }
}
