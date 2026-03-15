package com.google.zxing.common;

import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;

/* loaded from: classes.dex */
public final class BitSource {
    private int bitOffset;
    private int byteOffset;
    private final byte[] bytes;

    public BitSource(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getBitOffset() {
        return this.bitOffset;
    }

    public int getByteOffset() {
        return this.byteOffset;
    }

    public int readBits(int numBits) {
        if (numBits < 1 || numBits > 32 || numBits > available()) {
            throw new IllegalArgumentException(String.valueOf(numBits));
        }
        int result = 0;
        if (this.bitOffset > 0) {
            int bitsLeft = 8 - this.bitOffset;
            int toRead = numBits < bitsLeft ? numBits : bitsLeft;
            int bitsToNotRead = bitsLeft - toRead;
            int mask = (BatteryIcon.BATTERY_STATUS_CHARGING >> (8 - toRead)) << bitsToNotRead;
            result = (this.bytes[this.byteOffset] & mask) >> bitsToNotRead;
            numBits -= toRead;
            this.bitOffset += toRead;
            if (this.bitOffset == 8) {
                this.bitOffset = 0;
                this.byteOffset++;
            }
        }
        if (numBits > 0) {
            while (numBits >= 8) {
                result = (result << 8) | (this.bytes[this.byteOffset] & LiveviewCommon.COMMON_HEADER_START_BYTE);
                this.byteOffset++;
                numBits -= 8;
            }
            if (numBits > 0) {
                int bitsToNotRead2 = 8 - numBits;
                int mask2 = (BatteryIcon.BATTERY_STATUS_CHARGING >> bitsToNotRead2) << bitsToNotRead2;
                int result2 = (result << numBits) | ((this.bytes[this.byteOffset] & mask2) >> bitsToNotRead2);
                this.bitOffset += numBits;
                return result2;
            }
            return result;
        }
        return result;
    }

    public int available() {
        return ((this.bytes.length - this.byteOffset) * 8) - this.bitOffset;
    }
}
