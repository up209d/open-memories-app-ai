package com.sony.imaging.app.util;

import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.TimeUtil;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class ScalarCalendar extends GregorianCalendar {
    public ScalarCalendar() {
        setScalarTime();
        addScalarTimeZone();
        setTimeZone(new ScalarTimeZone());
    }

    public ScalarCalendar(int year, int month, int dayOfMonth) {
        super(new ScalarTimeZone());
        clear();
        set(year, month, dayOfMonth);
    }

    public ScalarCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        super(new ScalarTimeZone());
        clear();
        set(year, month, dayOfMonth, hourOfDay, minute);
    }

    public ScalarCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        super(new ScalarTimeZone());
        clear();
        set(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    public ScalarCalendar(Locale locale) {
        super(locale);
        setScalarTime();
        addScalarTimeZone();
        setTimeZone(new ScalarTimeZone());
    }

    public ScalarCalendar(TimeZone zone) {
        setScalarTime();
        addScalarTimeZone();
        setTimeZone(zone);
    }

    public ScalarCalendar(TimeZone zone, Locale locale) {
        super(locale);
        setScalarTime();
        addScalarTimeZone();
        setTimeZone(zone);
    }

    private void addScalarTimeZone() {
        addScalarTimeZone(TimeUtil.getCurrentTimeZone());
    }

    private void addScalarTimeZone(PlainTimeZone zone) {
        int diffGMT = zone.gmtDiff;
        int diffSummerTime = zone.summerTimeDiff;
        add(12, (-diffGMT) - diffSummerTime);
    }

    private void setScalarTime() {
        setScalarTime(TimeUtil.getCurrentCalendar());
    }

    private void setScalarTime(PlainCalendar pCalendar) {
        clear();
        set(pCalendar.year, pCalendar.month - 1, pCalendar.day, pCalendar.hour, pCalendar.minute, pCalendar.second);
    }
}
