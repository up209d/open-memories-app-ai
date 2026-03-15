package com.sony.imaging.app.util;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.TimeUtil;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class ScalarTimeZone extends TimeZone {
    private int dstOffset;
    private int rawOffset;

    public ScalarTimeZone() {
        this(TimeUtil.getCurrentTimeZone());
    }

    private ScalarTimeZone(PlainTimeZone pZone) {
        this(null, pZone.gmtDiff * 60 * SsdpDevice.RETRY_INTERVAL, pZone.summerTimeDiff * 60 * SsdpDevice.RETRY_INTERVAL);
    }

    public ScalarTimeZone(int rawOffset, int dstOffset) {
        this(null, rawOffset, dstOffset);
    }

    public ScalarTimeZone(String ID, int rawOffset, int dstOffset) {
        this.rawOffset = rawOffset;
        this.dstOffset = dstOffset;
        setID(ID == null ? createID(rawOffset) : ID);
    }

    private static String createID(int rawOffset) {
        int rawOffset2 = rawOffset / SRCtrlConstants.RECEIVE_GET_EVENT_TIMEOUT_v1_3;
        int hour = rawOffset2 / 60;
        int minutes = rawOffset2 % 60;
        return "GMT" + String.format("+%02d%02d", Integer.valueOf(hour), Integer.valueOf(minutes));
    }

    @Override // java.util.TimeZone
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int timeOfDayMillis) {
        return this.rawOffset + this.dstOffset;
    }

    @Override // java.util.TimeZone
    public int getRawOffset() {
        return this.rawOffset;
    }

    @Override // java.util.TimeZone
    public boolean inDaylightTime(Date time) {
        return useDaylightTime();
    }

    @Override // java.util.TimeZone
    public void setRawOffset(int offsetMillis) {
        this.rawOffset = offsetMillis;
    }

    @Override // java.util.TimeZone
    public boolean useDaylightTime() {
        return this.dstOffset > 0;
    }

    public boolean equals(Object object) {
        if (!(object instanceof ScalarTimeZone)) {
            return false;
        }
        ScalarTimeZone tz = (ScalarTimeZone) object;
        return getID().equals(tz.getID()) && this.rawOffset == tz.rawOffset && this.dstOffset == tz.dstOffset;
    }

    @Override // java.util.TimeZone
    public Object clone() {
        return super.clone();
    }

    @Override // java.util.TimeZone
    public boolean hasSameRules(TimeZone timeZone) {
        if (!(timeZone instanceof ScalarTimeZone)) {
            return false;
        }
        ScalarTimeZone tz = (ScalarTimeZone) timeZone;
        return this.rawOffset == tz.rawOffset && this.dstOffset == tz.dstOffset;
    }

    public String toString() {
        return getClass().getName() + "[id=" + getID() + ",offset=" + this.rawOffset + ",dstSavings=" + this.dstOffset + "]";
    }

    public static void dumpAvailableIDs() {
        String[] zones = TimeZone.getAvailableIDs();
        int length = zones.length;
        String serialized = "";
        Log.e("ScalarTimeZone", " calendar timeZones ----- ");
        for (int i = 0; i < length; i++) {
            serialized = serialized + i + " -> " + zones[i] + " : ";
            if (i % 10 == 9) {
                Log.e("ScalarTimeZone", serialized);
                serialized = "";
            }
        }
        Log.e("ScalarTimeZone", " calendar timeZones ----- ");
    }
}
