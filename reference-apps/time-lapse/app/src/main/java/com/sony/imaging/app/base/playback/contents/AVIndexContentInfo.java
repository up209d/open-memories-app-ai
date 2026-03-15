package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.aviadapter.IMedia;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.media.AvindexContentInfo;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class AVIndexContentInfo extends ContentInfo {
    private static final String MSG_CREATE_SAME_BITMAP = "createBitmap returns same instance";
    private static final String MSG_END_MAKEDATA = "makeData End";
    private static final String MSG_GETVALUE_UNKNOWN_TYPE = "getValue UNKNOWN Type : ";
    private static final String MSG_GET_THUMB_FAIL = "getThumbnail returns null : ";
    private static final String MSG_MAKEDATA_ILLEGAL_ID = "makeData illegal id : ";
    private static final String MSG_PREFIX_START_MAKEDATA = "makeData Start : ";
    private static final String MSG_PREFIX_THUMB_CLIP = "thumb clip Img(";
    private static final String MSG_SEPARATOR_BRACKET_ARROW = ") -> (";
    private static final float ROTATE_ANGLE_180 = 180.0f;
    private static final float ROTATE_ANGLE_270 = 270.0f;
    private static final float ROTATE_ANGLE_90 = 90.0f;
    private static final String TAG = "AVIndexContentInfo";
    private static final int THUMB_HEIGHT = 120;
    private static final int THUMB_WIDTH = 160;
    protected AvindexContentInfo mInfo;

    /* JADX INFO: Access modifiers changed from: protected */
    public AVIndexContentInfo(ContentResolver cr, ContentsIdentifier id) {
        super(cr, id);
        this.mInfo = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setData(AvindexContentInfo info) {
        this.mInfo = info;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public boolean makeData() {
        if (this.mId == null) {
            return false;
        }
        if (this.mInfo != null) {
            return true;
        }
        if (this.mId._id >= 0) {
            Log.d(TAG, LogHelper.getScratchBuilder(MSG_PREFIX_START_MAKEDATA).append(this.mId._id).append(" : ").append(Thread.currentThread()).toString());
            IMedia adapter = this.mId.getMediaAdapter();
            if (adapter == null) {
                this.mInfo = AvindexStore.Images.Media.getImageInfo(this.mId.data);
            } else {
                this.mInfo = adapter.getContentInfo(this.mId.data);
            }
            Log.d(TAG, MSG_END_MAKEDATA);
        } else {
            Log.w(TAG, LogHelper.getScratchBuilder(MSG_MAKEDATA_ILLEGAL_ID).append(this.mId._id).toString());
        }
        return this.mInfo != null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public Object getValue(String tag) {
        if (makeData()) {
            switch (this.mInfo.getAttributeType(tag)) {
                case 1:
                    return Integer.valueOf(this.mInfo.getAttributeInt(tag, AudioVolumeController.INVALID_VALUE));
                case 2:
                    return Long.valueOf(this.mInfo.getAttributeLong(tag, Long.MIN_VALUE));
                case 3:
                    return Double.valueOf(this.mInfo.getAttributeDouble(tag, Double.MIN_VALUE));
                case 4:
                    return this.mInfo.getAttribute(tag);
                default:
                    Log.w(TAG, LogHelper.getScratchBuilder(MSG_GETVALUE_UNKNOWN_TYPE).append(tag).toString());
                    break;
            }
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public int getInt(String tag) {
        int result = super.getInt(tag);
        if (Integer.MIN_VALUE != result) {
            return result;
        }
        if ("content_type".equals(tag)) {
            return this.mId.contentType;
        }
        if (makeData()) {
            result = this.mInfo.getAttributeInt(tag, AudioVolumeController.INVALID_VALUE);
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public double getDouble(String tag) {
        double result = super.getDouble(tag);
        if (Double.MIN_VALUE != result) {
            return result;
        }
        if (makeData()) {
            result = this.mInfo.getAttributeDouble(tag, Double.MIN_VALUE);
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public long getLong(String tag) {
        long result = super.getLong(tag);
        if (Long.MIN_VALUE != result) {
            return result;
        }
        if (makeData()) {
            result = this.mInfo.getAttributeLong(tag, Long.MIN_VALUE);
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public String getString(String tag) {
        String result = super.getString(tag);
        if (result != null) {
            return result;
        }
        if (makeData()) {
            result = this.mInfo.getAttribute(tag);
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public boolean hasThumbnail() {
        if (!makeData()) {
            return false;
        }
        boolean result = this.mInfo.hasThumbnail();
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public Bitmap getThumbnail(ContentsManager.ThumbnailOption option) {
        Bitmap bmp = null;
        if (makeData()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            byte[] data = this.mInfo.getThumbnail();
            if (data == null) {
                Log.w(TAG, LogHelper.getScratchBuilder(MSG_GET_THUMB_FAIL).append(this.mId.data).toString());
                return null;
            }
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (bmp == null) {
                return null;
            }
            int y = 0;
            int x = 0;
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            boolean needsRecreate = false;
            Matrix matrix = new Matrix();
            if (option != null && option.clipByAspect) {
                int imgWidth = (int) getLong("ImageWidth");
                int imgHeight = (int) getLong("ImageLength");
                int meta_aspect = getInt("AspectRatio");
                if (meta_aspect != 0 || THUMB_WIDTH != width || THUMB_HEIGHT != height) {
                    int diff = (width * imgHeight) - (height * imgWidth);
                    if (diff < 0) {
                        height = (width * imgHeight) / imgWidth;
                        y = (bmp.getHeight() - height) >> 1;
                        needsRecreate = true;
                    } else if (diff > 0) {
                        width = (height * imgWidth) / imgHeight;
                        x = (bmp.getWidth() - width) >> 1;
                        needsRecreate = true;
                    }
                    Log.d(TAG, LogHelper.getScratchBuilder(MSG_PREFIX_THUMB_CLIP).append(imgWidth).append(", ").append(imgHeight).append(MSG_SEPARATOR_BRACKET_ARROW).append(x).append(", ").append(y).append(", ").append(width).append(", ").append(height).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                }
            }
            if (option != null && option.rotateIt) {
                int meta_angle = getInt("Orientation");
                float rotateAngle = TimeLapseConstants.INVALID_APERTURE_VALUE;
                switch (meta_angle) {
                    case 3:
                        rotateAngle = ROTATE_ANGLE_180;
                        break;
                    case 6:
                        rotateAngle = ROTATE_ANGLE_90;
                        break;
                    case 8:
                        rotateAngle = ROTATE_ANGLE_270;
                        break;
                }
                if (rotateAngle != TimeLapseConstants.INVALID_APERTURE_VALUE) {
                    matrix.setRotate(rotateAngle);
                    needsRecreate = true;
                }
            }
            if (option != null && option.postScale != null) {
                matrix.postScale(option.postScale.x, option.postScale.y);
                needsRecreate = true;
            }
            if (needsRecreate) {
                bmp = Bitmap.createBitmap(bmp, x, y, width, height, matrix, true);
                if (bmp != bmp) {
                    bmp.recycle();
                } else {
                    Log.w(TAG, MSG_CREATE_SAME_BITMAP);
                }
            } else if (Environment.DEVICE_TYPE == 4) {
                bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false);
                if (bmp != bmp) {
                    bmp.recycle();
                } else {
                    Log.w(TAG, MSG_CREATE_SAME_BITMAP);
                }
            }
            if (Environment.DEVICE_TYPE == 4) {
                Canvas canvas = new Canvas(bmp);
                Paint paint = new TextPaint();
                paint.setTextSize(20.0f);
                paint.setAntiAlias(true);
                paint.setColor(-1);
                paint.setTypeface(Typeface.DEFAULT);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(Long.toString(this.mId._id), bmp.getWidth() / 2.0f, bmp.getHeight() / 2.0f, paint);
            }
        }
        return bmp;
    }

    public static boolean isPanorama(ContentInfo info) {
        try {
            int contentType = info.getInt("ContentType");
            switch (contentType) {
                case 9:
                case 12:
                    return true;
                case 10:
                default:
                    return false;
                case 11:
                    int aspect = info.getInt("AspectRatio");
                    if (1 != aspect) {
                        return false;
                    }
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        e.printStackTrace();
        return false;
    }
}
