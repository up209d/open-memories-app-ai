package com.sony.imaging.app.startrails.playback.layout;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.startrails.database.StarTrailsBO;
import com.sony.imaging.app.startrails.playback.controller.PlayBackController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ListViewAdapter extends ArrayAdapter<StarTrailsBO> {
    private static String TAG = "ListViewAdapter";
    Bitmap[] mBitmapListArray;
    private String mContentNumber;
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<StarTrailsBO> mList;
    ImageView mRecordedModeView;
    private ContentResolver mResolver;
    ImageView mThumbnailView;
    private RelativeLayout.LayoutParams mlayoutParams;

    public ListViewAdapter(Context context, int textViewResourceId, ArrayList<StarTrailsBO> items) {
        super(context, textViewResourceId, items);
        this.mInflater = null;
        this.mList = null;
        this.mContentNumber = null;
        this.mlayoutParams = null;
        this.mBitmapListArray = null;
        this.mThumbnailView = null;
        this.mRecordedModeView = null;
        this.mList = items;
        this.mBitmapListArray = new Bitmap[this.mList.size()];
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mResolver = this.mContext.getContentResolver();
        this.mContentNumber = this.mContext.getResources().getString(R.string.restr_pin_incorrect);
        this.mlayoutParams = new RelativeLayout.LayoutParams(-2, -2);
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
        Log.e("INSIDE", "getView");
        if (convertView == null) {
            convertView = this.mInflater.inflate(com.sony.imaging.app.startrails.R.layout.pb_list_view_style, (ViewGroup) null);
        }
        this.mThumbnailView = (ImageView) convertView.findViewById(com.sony.imaging.app.startrails.R.id.thumbnail);
        TextView dateTime = (TextView) convertView.findViewById(com.sony.imaging.app.startrails.R.id.start_date_time);
        TextView theme = (TextView) convertView.findViewById(com.sony.imaging.app.startrails.R.id.theme_name);
        this.mRecordedModeView = (ImageView) convertView.findViewById(com.sony.imaging.app.startrails.R.id.rec_mode);
        TextView contentNum = (TextView) convertView.findViewById(com.sony.imaging.app.startrails.R.id.shooting_number);
        TextView streakLevel = (TextView) convertView.findViewById(com.sony.imaging.app.startrails.R.id.streaklevel);
        StarTrailsBO mObject = null;
        try {
            if (this.mList != null && position < this.mList.size()) {
                mObject = this.mList.get(position);
            }
        } catch (Exception e) {
            Log.d("ListViewAdapter", e.toString());
            mObject = null;
        }
        if (mObject != null) {
            String formattedDate = STUtility.getInstance().getFormatteddate(mObject.getStartDate());
            String formattedTime = STUtility.getInstance().getFormattedTime(mObject.getStartTime());
            String themeName = this.mContext.getString(STUtility.getInstance().getThemeNameResID(mObject.getThemeName()));
            mObject.getMovieMode();
            if (this.mBitmapListArray[position] == null) {
                this.mBitmapListArray[position] = PlayBackController.getInstance().getBitmap(mObject, this.mResolver);
            }
            this.mlayoutParams.leftMargin = 19;
            this.mlayoutParams.topMargin = 20;
            this.mlayoutParams.width = 96;
            this.mlayoutParams.height = 72;
            this.mThumbnailView.setLayoutParams(this.mlayoutParams);
            if (this.mBitmapListArray[position] == null) {
                this.mThumbnailView.setImageResource(com.sony.imaging.app.startrails.R.drawable.p_16_dd_parts_2ndapp_play_index_thumbnail_no_image);
            } else {
                this.mThumbnailView.setImageBitmap(this.mBitmapListArray[position]);
            }
            dateTime.setText(formattedDate + ExposureModeController.SOFT_SNAP + formattedTime);
            theme.setText(themeName);
            contentNum.setText(String.format(this.mContentNumber, Integer.valueOf(mObject.getShootingNumber())));
            CharSequence streakLevelValue = STUtility.getInstance().getStreakValue(mObject.getStreakLevel());
            streakLevel.setText(streakLevelValue);
            if (mObject.getFps() == 0) {
                this.mRecordedModeView.setImageResource(com.sony.imaging.app.startrails.R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
            } else {
                this.mRecordedModeView.setImageResource(com.sony.imaging.app.startrails.R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
            }
        }
        return convertView;
    }

    public void updateImageAdapter(ArrayList<StarTrailsBO> updatedList) {
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
        this.mlayoutParams = null;
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
