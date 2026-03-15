package com.google.zxing.oned.rss.expanded;

import com.google.zxing.oned.rss.DataCharacter;
import com.google.zxing.oned.rss.FinderPattern;

/* loaded from: classes.dex */
final class ExpandedPair {
    private final FinderPattern finderPattern;
    private final DataCharacter leftChar;
    private final boolean mayBeLast;
    private final DataCharacter rightChar;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExpandedPair(DataCharacter leftChar, DataCharacter rightChar, FinderPattern finderPattern, boolean mayBeLast) {
        this.leftChar = leftChar;
        this.rightChar = rightChar;
        this.finderPattern = finderPattern;
        this.mayBeLast = mayBeLast;
    }

    boolean mayBeLast() {
        return this.mayBeLast;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataCharacter getLeftChar() {
        return this.leftChar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataCharacter getRightChar() {
        return this.rightChar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FinderPattern getFinderPattern() {
        return this.finderPattern;
    }

    public boolean mustBeLast() {
        return this.rightChar == null;
    }

    public String toString() {
        return "[ " + this.leftChar + " , " + this.rightChar + " : " + (this.finderPattern == null ? "null" : Integer.valueOf(this.finderPattern.getValue())) + " ]";
    }

    public boolean equals(Object o) {
        if (!(o instanceof ExpandedPair)) {
            return false;
        }
        ExpandedPair that = (ExpandedPair) o;
        return equalsOrNull(this.leftChar, that.leftChar) && equalsOrNull(this.rightChar, that.rightChar) && equalsOrNull(this.finderPattern, that.finderPattern);
    }

    private static boolean equalsOrNull(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public int hashCode() {
        return (hashNotNull(this.leftChar) ^ hashNotNull(this.rightChar)) ^ hashNotNull(this.finderPattern);
    }

    private static int hashNotNull(Object o) {
        if (o == null) {
            return 0;
        }
        return o.hashCode();
    }
}
