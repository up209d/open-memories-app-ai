package com.sony.imaging.app.photoretouch.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.ControlImageLoadingTask;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class ImageAdapter extends BaseAdapter {
    protected Context mContext;
    protected LinkedList<? extends Object> mGalObjectList;
    protected Gallery.LayoutParams mGalleryParams = new Gallery.LayoutParams(-2, -2);

    public ImageAdapter(Context c, LinkedList<? extends Object> galObjectList) {
        this.mGalObjectList = null;
        this.mContext = c;
        this.mGalObjectList = galObjectList;
    }

    public void setContentInfo(int aspect, int rotation, int panel) {
        if (aspect == 0) {
            if (1 == rotation || 3 == rotation) {
                this.mGalleryParams.height = 180;
                if (panel == 43) {
                    this.mGalleryParams.width = 270;
                    return;
                } else {
                    this.mGalleryParams.width = 202;
                    return;
                }
            }
            this.mGalleryParams.height = 270;
            if (panel == 43) {
                this.mGalleryParams.width = 180;
                return;
            } else {
                this.mGalleryParams.width = 135;
                return;
            }
        }
        if (aspect == 1) {
            if (1 == rotation || 3 == rotation) {
                this.mGalleryParams.height = 152;
                if (panel == 43) {
                    this.mGalleryParams.width = 270;
                    return;
                } else {
                    this.mGalleryParams.width = 202;
                    return;
                }
            }
            this.mGalleryParams.height = 270;
            if (panel == 43) {
                this.mGalleryParams.width = 152;
                return;
            } else {
                this.mGalleryParams.width = 114;
                return;
            }
        }
        if (aspect == 2) {
            if (1 == rotation || 3 == rotation) {
                this.mGalleryParams.height = 204;
                if (panel == 43) {
                    this.mGalleryParams.width = 272;
                    return;
                } else {
                    this.mGalleryParams.width = 204;
                    return;
                }
            }
            this.mGalleryParams.height = 272;
            if (panel == 43) {
                this.mGalleryParams.width = 204;
                return;
            } else {
                this.mGalleryParams.width = 153;
                return;
            }
        }
        if (aspect == 3) {
            if (1 == rotation || 3 == rotation) {
                this.mGalleryParams.height = 270;
                if (panel == 43) {
                    this.mGalleryParams.width = 270;
                    return;
                } else {
                    this.mGalleryParams.width = 202;
                    return;
                }
            }
            this.mGalleryParams.height = 270;
            if (panel == 43) {
                this.mGalleryParams.width = 270;
            } else {
                this.mGalleryParams.width = 202;
            }
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mGalObjectList.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return Integer.valueOf(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public Bitmap getBitmap(int position) {
        ControlImageLoadingTask.GalObject gObj = (ControlImageLoadingTask.GalObject) this.mGalObjectList.get(position);
        return gObj.bmp;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i;
        if (convertView == null) {
            i = new ImageView(this.mContext);
            i.setBackgroundResource(R.drawable.p_16_dd_parts_pr_shadow);
            i.setLayoutParams(this.mGalleryParams);
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            i = (ImageView) convertView;
        }
        Bitmap b = getBitmap(position);
        i.setImageBitmap(b);
        BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
        drawable.setAntiAlias(true);
        return i;
    }
}
