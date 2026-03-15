package com.google.zxing.client.result;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class CalendarParsedResult extends ParsedResult {
    private static final DateFormat DATE_TIME_FORMAT;
    private final String[] attendees;
    private final String description;
    private final Date end;
    private final boolean endAllDay;
    private final double latitude;
    private final String location;
    private final double longitude;
    private final String organizer;
    private final Date start;
    private final boolean startAllDay;
    private final String summary;
    private static final Pattern RFC2445_DURATION = Pattern.compile("P(?:(\\d+)W)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?)?");
    private static final long[] RFC2445_DURATION_FIELD_UNITS = {604800000, 86400000, 3600000, 60000, 1000};
    private static final Pattern DATE_TIME = Pattern.compile("[0-9]{8}(T[0-9]{6}Z?)?");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH);
    }

    public CalendarParsedResult(String summary, String startString, String endString, String durationString, String location, String organizer, String[] attendees, String description, double latitude, double longitude) {
        super(ParsedResultType.CALENDAR);
        this.summary = summary;
        try {
            this.start = parseDate(startString);
            if (endString == null) {
                long durationMS = parseDurationMS(durationString);
                this.end = durationMS < 0 ? null : new Date(this.start.getTime() + durationMS);
            } else {
                try {
                    this.end = parseDate(endString);
                } catch (ParseException pe) {
                    throw new IllegalArgumentException(pe.toString());
                }
            }
            this.startAllDay = startString.length() == 8;
            this.endAllDay = endString != null && endString.length() == 8;
            this.location = location;
            this.organizer = organizer;
            this.attendees = attendees;
            this.description = description;
            this.latitude = latitude;
            this.longitude = longitude;
        } catch (ParseException pe2) {
            throw new IllegalArgumentException(pe2.toString());
        }
    }

    public String getSummary() {
        return this.summary;
    }

    public Date getStart() {
        return this.start;
    }

    public boolean isStartAllDay() {
        return this.startAllDay;
    }

    public Date getEnd() {
        return this.end;
    }

    public boolean isEndAllDay() {
        return this.endAllDay;
    }

    public String getLocation() {
        return this.location;
    }

    public String getOrganizer() {
        return this.organizer;
    }

    public String[] getAttendees() {
        return this.attendees;
    }

    public String getDescription() {
        return this.description;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    @Override // com.google.zxing.client.result.ParsedResult
    public String getDisplayResult() {
        StringBuilder result = new StringBuilder(100);
        maybeAppend(this.summary, result);
        maybeAppend(format(this.startAllDay, this.start), result);
        maybeAppend(format(this.endAllDay, this.end), result);
        maybeAppend(this.location, result);
        maybeAppend(this.organizer, result);
        maybeAppend(this.attendees, result);
        maybeAppend(this.description, result);
        return result.toString();
    }

    private static Date parseDate(String when) throws ParseException {
        if (!DATE_TIME.matcher(when).matches()) {
            throw new ParseException(when, 0);
        }
        if (when.length() == 8) {
            return DATE_FORMAT.parse(when);
        }
        if (when.length() == 16 && when.charAt(15) == 'Z') {
            Date date = DATE_TIME_FORMAT.parse(when.substring(0, 15));
            Calendar calendar = new GregorianCalendar();
            long milliseconds = date.getTime() + calendar.get(15);
            calendar.setTime(new Date(milliseconds));
            Date date2 = new Date(milliseconds + calendar.get(16));
            return date2;
        }
        return DATE_TIME_FORMAT.parse(when);
    }

    private static String format(boolean allDay, Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = allDay ? DateFormat.getDateInstance(2) : DateFormat.getDateTimeInstance(2, 2);
        return format.format(date);
    }

    private static long parseDurationMS(CharSequence durationString) {
        long durationMS = -1;
        if (durationString != null) {
            Matcher m = RFC2445_DURATION.matcher(durationString);
            if (m.matches()) {
                durationMS = 0;
                for (int i = 0; i < RFC2445_DURATION_FIELD_UNITS.length; i++) {
                    String fieldValue = m.group(i + 1);
                    if (fieldValue != null) {
                        durationMS += RFC2445_DURATION_FIELD_UNITS[i] * Integer.parseInt(fieldValue);
                    }
                }
            }
        }
        return durationMS;
    }
}
