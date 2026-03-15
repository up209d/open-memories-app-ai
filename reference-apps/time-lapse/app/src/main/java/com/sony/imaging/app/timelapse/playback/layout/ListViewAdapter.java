package com.sony.imaging.app.timelapse.playback.layout;

import android.R;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.databaseutil.TimeLapseBO;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ListViewAdapter extends ArrayAdapter<TimeLapseBO> {
    private static final int OFFSET_INVISIBLE = 5;
    private static String TAG = "ListViewAdapter";
    Bitmap[] mBitmapListArray;
    private String mContentNumber;
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<TimeLapseBO> mList;
    ImageView mRecordedModeView;
    private ContentResolver mResolver;
    ImageView mThumbnailView;

    public ListViewAdapter(Context context, int textViewResourceId, ArrayList<TimeLapseBO> items) {
        super(context, textViewResourceId, items);
        this.mInflater = null;
        this.mList = null;
        this.mContentNumber = null;
        this.mBitmapListArray = null;
        this.mThumbnailView = null;
        this.mRecordedModeView = null;
        this.mList = items;
        this.mBitmapListArray = new Bitmap[this.mList.size()];
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mResolver = this.mContext.getContentResolver();
        this.mContentNumber = this.mContext.getResources().getString(R.string.restr_pin_incorrect);
        setNotifyOnChange(true);
        this.mContext = context;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        return this.mList.size();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public long getItemId(int id) {
        return id;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup arg2) {
        Log.e("INSIDE", "getView : " + position);
        if (convertView == null) {
            convertView = this.mInflater.inflate(com.sony.imaging.app.timelapse.R.layout.pb_list_view_style, (ViewGroup) null);
        }
        this.mThumbnailView = (ImageView) convertView.findViewById(com.sony.imaging.app.timelapse.R.id.thumbnail);
        TextView dateTime = (TextView) convertView.findViewById(com.sony.imaging.app.timelapse.R.id.start_date_time);
        TextView theme = (TextView) convertView.findViewById(com.sony.imaging.app.timelapse.R.id.theme_name);
        this.mRecordedModeView = (ImageView) convertView.findViewById(com.sony.imaging.app.timelapse.R.id.rec_mode);
        TextView contentNum = (TextView) convertView.findViewById(com.sony.imaging.app.timelapse.R.id.shooting_number);
        TimeLapseBO mObject = null;
        try {
            if (this.mList != null && position < this.mList.size()) {
                mObject = this.mList.get(position);
            }
        } catch (Exception e) {
            Log.d("ListViewAdapter", e.toString());
            mObject = null;
        }
        if (mObject != null) {
            String formattedDate = TLCommonUtil.getInstance().getFormatteddate(mObject.getStartDate());
            String formattedTime = TLCommonUtil.getInstance().getFormattedTime(mObject.getStartTime());
            String themeName = this.mContext.getString(TLCommonUtil.getInstance().getThemeNameResID(mObject.getThemeName()));
            String rec_Mode = mObject.getMovieMode();
            if (this.mBitmapListArray[position] == null) {
                Bitmap bitmap = PlayBackController.getInstance().getBitmap(mObject, this.mResolver);
                if (ScalarProperties.getInt("device.panel.aspect") == 169 && bitmap != null) {
                    this.mBitmapListArray[position] = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 3) / 4, bitmap.getHeight(), false);
                    bitmap.recycle();
                } else {
                    this.mBitmapListArray[position] = bitmap;
                }
            }
            if (this.mBitmapListArray[position] == null) {
                this.mThumbnailView.setImageResource(com.sony.imaging.app.timelapse.R.drawable.p_16_dd_parts_2ndapp_play_index_thumbnail_no_image);
            } else {
                this.mThumbnailView.setImageBitmap(this.mBitmapListArray[position]);
            }
            this.mThumbnailView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            releaseHideImages(position);
            dateTime.setText(formattedDate + ExposureModeController.SOFT_SNAP + formattedTime);
            theme.setText(themeName);
            contentNum.setText(String.format(this.mContentNumber, Integer.valueOf(mObject.getShootingNumber())));
            if (rec_Mode.equalsIgnoreCase(TimeLapseConstants.TIME_LAPSE_SHOOT_MODE_MPEG24)) {
                this.mRecordedModeView.setImageResource(com.sony.imaging.app.timelapse.R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
            } else if (rec_Mode.equalsIgnoreCase(TimeLapseConstants.TIME_LAPSE_SHOOT_MODE_MPEG30)) {
                this.mRecordedModeView.setImageResource(com.sony.imaging.app.timelapse.R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
            } else {
                this.mRecordedModeView.setImageResource(com.sony.imaging.app.timelapse.R.drawable.p_16_dd_parts_tm_recordingmode_still_menu_normal);
            }
        }
        return convertView;
    }

    private void releaseHideImages(int position) {
        if (position >= 4) {
            int upperPosition = position - 5;
            if (upperPosition >= 0) {
                Bitmap bmp = this.mBitmapListArray[upperPosition];
                if (bmp != null && !bmp.isRecycled()) {
                    bmp.recycle();
                }
                this.mBitmapListArray[upperPosition] = null;
            }
            int lowerPosition = position + 5;
            if (lowerPosition <= this.mBitmapListArray.length - 1) {
                Bitmap bmp2 = this.mBitmapListArray[lowerPosition];
                if (bmp2 != null && !bmp2.isRecycled()) {
                    bmp2.recycle();
                }
                this.mBitmapListArray[lowerPosition] = null;
            }
        }
    }

    public void updateImageAdapter(ArrayList<TimeLapseBO> updatedList) {
        this.mList = updatedList;
        releaseBitmapMemory();
        this.mBitmapListArray = new Bitmap[this.mList.size()];
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }

    public void clearMemory() {
        AppLog.trace(TAG, "clearMemory Bitmap Recycled");
        releaseBitmapMemory();
        this.mInflater = null;
        this.mContext = null;
        this.mList = null;
        this.mResolver = null;
        this.mContentNumber = null;
        releaseImageViewDrawable(this.mThumbnailView);
        releaseImageViewDrawable(this.mRecordedModeView);
        System.gc();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    private void releaseBitmapMemory() {
        for (int i = 0; i < this.mBitmapListArray.length; i++) {
            Bitmap bmp = this.mBitmapListArray[i];
            if (bmp != null && !bmp.isRecycled()) {
                AppLog.trace(TAG, "releaseBitmapMemory Bitmap Recycled" + this.mBitmapListArray[i]);
                bmp.recycle();
            }
        }
        this.mBitmapListArray = null;
    }
}
