package com.sony.imaging.app.avi.header;

import com.sony.imaging.app.base.common.widget.BatteryIcon;

/* loaded from: classes.dex */
public class StructIO {
    public static int setIntAt(int a, byte[] buf, int n, boolean isBigEndian) {
        if (isBigEndian) {
            buf[n + 0] = (byte) ((a >> 24) & BatteryIcon.BATTERY_STATUS_CHARGING);
            buf[n + 1] = (byte) ((a >> 16) & BatteryIcon.BATTERY_STATUS_CHARGING);
            buf[n + 2] = (byte) ((a >> 8) & BatteryIcon.BATTERY_STATUS_CHARGING);
            buf[n + 3] = (byte) (a & BatteryIcon.BATTERY_STATUS_CHARGING);
            return 4;
        }
        buf[n + 0] = (byte) (a & BatteryIcon.BATTERY_STATUS_CHARGING);
        buf[n + 1] = (byte) ((a >> 8) & BatteryIcon.BATTERY_STATUS_CHARGING);
        buf[n + 2] = (byte) ((a >> 16) & BatteryIcon.BATTERY_STATUS_CHARGING);
        buf[n + 3] = (byte) ((a >> 24) & BatteryIcon.BATTERY_STATUS_CHARGING);
        return 4;
    }

    public static int setShortAt(short a, byte[] buf, int n, boolean isBigEndian) {
        if (isBigEndian) {
            buf[n + 0] = (byte) ((a >> 8) & BatteryIcon.BATTERY_STATUS_CHARGING);
            buf[n + 1] = (byte) (a & 255);
            return 2;
        }
        buf[n + 0] = (byte) (a & 255);
        buf[n + 1] = (byte) ((a >> 8) & BatteryIcon.BATTERY_STATUS_CHARGING);
        return 2;
    }

    public static int setByteArrayAt(byte[] a, byte[] buf, int n, boolean isBigEndian) {
        for (int i = 0; i < a.length; i++) {
            buf[n + i] = a[i];
        }
        return a.length;
    }

    public static int setShortArrayAt(short[] a, byte[] buf, int n, boolean isBigEndian) {
        for (int i = 0; i < a.length; i++) {
            setShortAt(a[i], buf, (i * 2) + n, isBigEndian);
        }
        return a.length * 2;
    }

    public static int setIntArrayAt(int[] a, byte[] buf, int n, boolean isBigEndian) {
        for (int i = 0; i < a.length; i++) {
            setIntAt(a[i], buf, (i * 4) + n, isBigEndian);
        }
        return a.length * 4;
    }

    public static int setStringAt(String s, byte[] buf, int n) {
        byte[] b = s.getBytes();
        int len = b.length;
        System.arraycopy(b, 0, buf, n, len);
        buf[n + len] = 0;
        return len;
    }
}
