package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class LabelFileNumber extends LabelFileInfo {
    private static final int DIR_OFFSET_END = 3;
    private static final int DIR_OFFSET_START = 0;
    private static final int FILE_OFFSET_END = 8;
    private static final int FILE_OFFSET_START = 4;
    private static final String FORMAT_DIRECTORY = "%03d";
    private static final String FORMAT_FILE = "%08d";
    private static final String MSG_END_DRAW_OSD = "draw pb OSD";
    private static final String SEPARATOR_SLASH = "/";
    private String mDirName;
    private String mFileName;
    private boolean mIsOverlapped;
    private MediaMountEventListener mMediaListener;
    private MediaNotificationManager mMediaNotifier;
    private boolean mShowOnlyDirNumber;

    public LabelFileNumber(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.RESID_FONTSIZE_SP_PLAY_INFO);
        this.mShowOnlyDirNumber = false;
        this.mIsOverlapped = false;
        this.mDirName = null;
        this.mFileName = null;
        setTextColor(context.getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
        setTypeface(Typeface.UNIVERS);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LabelFileNumber);
        this.mShowOnlyDirNumber = attr.getBoolean(8, false);
        attr.recycle();
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mMediaListener = new MediaMountEventListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mIsOverlapped = false;
        this.mDirName = null;
        this.mFileName = null;
        super.onAttachedToWindow();
        this.mMediaNotifier.setNotificationListener(this.mMediaListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.view.View
    public void onDetachedFromWindow() {
        this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        super.onDetachedFromWindow();
    }

    public void setValue(boolean isOverlapped, long dirName, long fileName) {
        setValue(isOverlapped, String.format("%03d", Long.valueOf(dirName)), String.format(FORMAT_FILE, Long.valueOf(fileName)));
    }

    public void setValue(boolean isOverlapped, String dirName, String fileName) {
        if (!this.mShowOnlyDirNumber) {
            if (dirName != null && fileName != null && dirName.length() >= 3 && fileName.length() >= 8) {
                if (isOverlapped != this.mIsOverlapped || !dirName.equals(this.mDirName) || !fileName.equals(this.mFileName)) {
                    if (!isOverlapped) {
                        String text = String.format(getResources().getString(android.R.string.ringtone_picker_title_alarm), Integer.valueOf(dirName.substring(0, 3)), Integer.valueOf(fileName.substring(4, 8)));
                        setText(text);
                        setCompoundDrawablesWithIntrinsicBounds(17305036, 0, 0, 0);
                        setVisibility(0);
                    } else {
                        String text2 = String.format(getResources().getString(17041901), fileName.substring(0, 8));
                        setText(text2);
                        setVisibility(0);
                    }
                    setVisibility(0);
                }
            } else {
                setVisibility(4);
            }
        } else if (dirName != null && dirName.length() >= 3) {
            if (!dirName.equals(this.mDirName)) {
                String text3 = dirName.substring(0, 3);
                setText(text3);
                setCompoundDrawablesWithIntrinsicBounds(17305036, 0, 0, 0);
                setVisibility(0);
            }
        } else {
            setVisibility(4);
        }
        this.mIsOverlapped = isOverlapped;
        this.mDirName = dirName;
        this.mFileName = fileName;
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        int overlapped = AudioVolumeController.INVALID_VALUE;
        String dirName = null;
        String fileName = null;
        if (info != null) {
            if (Environment.DEVICE_TYPE == 3) {
                overlapped = info.getInt("DCF_TBLFileOverlap");
                dirName = info.getString("DCF_TBLDirName");
                fileName = info.getString("DCF_TBLFileName");
            } else {
                String data = info.getString("_data");
                if (data != null) {
                    String[] buf = data.split(SEPARATOR_SLASH);
                    if (buf.length >= 2) {
                        dirName = buf[buf.length - 2];
                        fileName = buf[buf.length - 1];
                    }
                }
            }
        }
        if (!this.mMediaNotifier.isNoCard()) {
            setValue((overlapped == 0 || overlapped == Integer.MIN_VALUE) ? false : true, dirName, fileName);
            if (info != null) {
                PTag.end(MSG_END_DRAW_OSD);
            }
        }
    }

    public void showFileNumber(boolean showIt) {
        this.mShowOnlyDirNumber = !showIt;
    }

    /* loaded from: classes.dex */
    class MediaMountEventListener implements NotificationListener {
        MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE) && LabelFileNumber.this.mMediaNotifier.isNoCard()) {
                LabelFileNumber.this.setVisibility(4);
            }
        }
    }
}
