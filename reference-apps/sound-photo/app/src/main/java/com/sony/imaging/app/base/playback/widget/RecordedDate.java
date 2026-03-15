package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class RecordedDate extends CompoundFileInfo {
    private static final String AM = "AM";
    private static final String BLANK = "  ";
    private static final int DATETIME_INDEX_NUM = 2;
    private static final int DATE_INDEX_NUM = 3;
    private static final int DAY_INDEX = 2;
    private static final int HOUR_INDEX = 3;
    private static final int MAX_DAY = 31;
    private static final int MAX_HOUR = 23;
    private static final int MAX_MINUTE = 59;
    private static final int MAX_MONTH = 12;
    private static final int MAX_YEAR = 9999;
    private static final int MINUTE_INDEX = 4;
    private static final String[] MONTH = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private static final int MONTH_INDEX = 1;
    private static final int NOON_TIME = 12;
    private static final String PM = "PM";
    private static final int SECOND_INDEX = 5;
    private static final String SEPARATOR_COLON = ":";
    private static final String SEPARATOR_SPACE = " ";
    private static final int TIME_INDEX_NUM = 3;
    private static final int YEAR_INDEX = 0;
    private static final int ZERO_TIME = 24;
    private TextView mDate;
    private String mDateTime;
    private TextView mTime;

    public RecordedDate(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDateTime = null;
        this.mDate = new TextView(context, attrs, R.attr.RESID_FONTSIZE_SP_PLAY_INFO);
        this.mTime = new TextView(context, attrs, R.attr.RESID_FONTSIZE_SP_PLAY_INFO);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.RecordedDate);
        int dWidth = attr.getDimensionPixelSize(0, 0);
        int dHeight = attr.getDimensionPixelSize(1, 0);
        int tWidth = attr.getDimensionPixelSize(2, 0);
        int tHeight = attr.getDimensionPixelSize(3, 0);
        int tAlignLeft = attr.getDimensionPixelSize(4, 0);
        RelativeLayout.LayoutParams paramsDate = new RelativeLayout.LayoutParams(dWidth, dHeight);
        paramsDate.addRule(9);
        addView(this.mDate, paramsDate);
        RelativeLayout.LayoutParams paramsTime = new RelativeLayout.LayoutParams(tWidth, tHeight);
        paramsTime.leftMargin = tAlignLeft;
        addView(this.mTime, paramsTime);
        this.mDate.setTextColor(context.getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
        this.mTime.setTextColor(context.getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
        this.mDate.setTypeface(Typeface.UNIVERS);
        this.mTime.setTypeface(Typeface.UNIVERS);
        this.mDate.setGravity(19);
        this.mTime.setGravity(19);
    }

    private String[] parseExif(String exif) {
        String[] strDataTime;
        String[] ret = null;
        if (exif != null && (strDataTime = exif.split(" ")) != null && 2 == strDataTime.length) {
            String[] strDate = null;
            if (strDataTime[0] != null) {
                strDate = strDataTime[0].split(SEPARATOR_COLON);
            }
            String[] strTime = null;
            if (strDataTime[1] != null) {
                strTime = strDataTime[1].split(SEPARATOR_COLON);
            }
            if (strDate != null && strDate.length == 3 && strTime != null && strTime.length == 3) {
                ret = new String[strDate.length + strTime.length];
                for (int i = 0; i < strDate.length; i++) {
                    ret[i] = strDate[i];
                }
                for (int j = 0; j < strTime.length; j++) {
                    ret[strDate.length + j] = strTime[j];
                }
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.CompoundFileInfo, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        this.mDateTime = null;
        super.onAttachedToWindow();
    }

    @Override // com.sony.imaging.app.base.playback.widget.CompoundFileInfo
    public void setContentInfo(ContentInfo info) {
        String dateTime = null;
        if (info != null) {
            dateTime = info.getString("DateTimeOriginal");
        }
        if (dateTime != null) {
            if (!dateTime.equals(this.mDateTime)) {
                String[] strAry = parseExif(dateTime);
                if (strAry != null) {
                    try {
                        int year = Integer.parseInt(strAry[0]);
                        int month = Integer.parseInt(strAry[1]);
                        int day = Integer.parseInt(strAry[2]);
                        int hour = Integer.parseInt(strAry[3]);
                        int minute = Integer.parseInt(strAry[4]);
                        int second = Integer.parseInt(strAry[5]);
                        if (year >= 0 && year <= MAX_YEAR && 1 <= month && month <= 12 && 1 <= day && day <= 31 && hour >= 0 && hour <= 23 && minute >= 0 && minute <= MAX_MINUTE) {
                            setValue(year, month, day, hour, minute, second);
                        } else {
                            setVisibility(4);
                        }
                    } catch (NumberFormatException e) {
                        setVisibility(4);
                    }
                } else {
                    setVisibility(4);
                }
            }
        } else {
            setVisibility(4);
        }
        this.mDateTime = dateTime;
    }

    public void setValue(int year, int month, int day, int hour, int minute, int second) {
        String exifDate;
        String exifTime;
        BackupReader.RecordedDateMode recordedDateMode = BackupReader.getRecordedDateMode();
        if (recordedDateMode == BackupReader.RecordedDateMode.YY_MM_DD) {
            exifDate = String.format(getResources().getString(android.R.string.ringtone_picker_title), Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
        } else if (recordedDateMode == BackupReader.RecordedDateMode.MM_DD_YY_ENG) {
            exifDate = String.format(getResources().getString(17041723), MONTH[month - 1], Integer.valueOf(day), Integer.valueOf(year));
        } else if (recordedDateMode == BackupReader.RecordedDateMode.MM_DD_YY_NUM) {
            exifDate = String.format(getResources().getString(17041724), Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(year));
        } else {
            exifDate = String.format(getResources().getString(17041724), Integer.valueOf(day), Integer.valueOf(month), Integer.valueOf(year));
        }
        this.mDate.setText(exifDate);
        if (BackupReader.RecordedDateMode.DD_MM_YY == recordedDateMode) {
            exifTime = String.format(getResources().getString(android.R.string.ringtone_silent), Integer.valueOf(hour), Integer.valueOf(minute), BLANK);
        } else if (hour < 12) {
            if (hour == 0) {
                hour = 12;
            }
            exifTime = String.format(getResources().getString(android.R.string.ringtone_silent), Integer.valueOf(hour), Integer.valueOf(minute), AM);
        } else {
            if (hour == 12) {
                hour = 24;
            }
            exifTime = String.format(getResources().getString(android.R.string.ringtone_silent), Integer.valueOf(hour - 12), Integer.valueOf(minute), PM);
        }
        this.mTime.setText(exifTime);
        setVisibility(0);
    }
}
