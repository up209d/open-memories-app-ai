package com.google.zxing.oned.rss;

import com.sony.imaging.app.base.playback.LogHelper;

/* loaded from: classes.dex */
public class DataCharacter {
    private final int checksumPortion;
    private final int value;

    public DataCharacter(int value, int checksumPortion) {
        this.value = value;
        this.checksumPortion = checksumPortion;
    }

    public final int getValue() {
        return this.value;
    }

    public final int getChecksumPortion() {
        return this.checksumPortion;
    }

    public final String toString() {
        return this.value + LogHelper.MSG_OPEN_BRACKET + this.checksumPortion + ')';
    }

    public final boolean equals(Object o) {
        if (!(o instanceof DataCharacter)) {
            return false;
        }
        DataCharacter that = (DataCharacter) o;
        return this.value == that.value && this.checksumPortion == that.checksumPortion;
    }

    public final int hashCode() {
        return this.value ^ this.checksumPortion;
    }
}
