package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.provider.MediaStore;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import java.io.IOException;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MediaContentInfo extends ContentInfo {
    private static final HashMap<String, String> TAG_TBL = new HashMap<String, String>() { // from class: com.sony.imaging.app.base.playback.contents.MediaContentInfo.1
        private static final long serialVersionUID = 9083656278346202334L;

        public HashMap<String, String> init() {
            put("ImageWidth", "ImageWidth");
            put("ImageLength", "ImageLength");
            put("DateTime", "DateTime");
            put("FocalLength", "FocalLength");
            put("Orientation", "Orientation");
            put(WhiteBalanceController.WHITEBALANCE, WhiteBalanceController.WHITEBALANCE);
            return this;
        }
    }.init();
    private ExifInterface mExifInterface;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaContentInfo(ContentResolver cr, ContentsIdentifier id) {
        super(cr, id);
        this.mExifInterface = null;
    }

    protected boolean makeData() {
        if (this.mExifInterface != null) {
            return true;
        }
        try {
            this.mExifInterface = new ExifInterface(this.mId.data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public int getInt(String tag) {
        int result = super.getInt(tag);
        if (Integer.MIN_VALUE != result) {
            return result;
        }
        String aTag = TAG_TBL.get(tag);
        if (aTag != null && makeData()) {
            result = this.mExifInterface.getAttributeInt(aTag, Integer.MIN_VALUE);
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public double getDouble(String tag) {
        double result = super.getDouble(tag);
        if (Double.MIN_VALUE != result) {
            return result;
        }
        String aTag = TAG_TBL.get(tag);
        if (aTag != null && makeData()) {
            result = this.mExifInterface.getAttributeDouble(aTag, Double.MIN_VALUE);
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public String getString(String tag) {
        String result = super.getString(tag);
        if (result != null) {
            return result;
        }
        String aTag = TAG_TBL.get(tag);
        if (aTag != null && makeData()) {
            result = this.mExifInterface.getAttribute(aTag);
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentInfo
    public Bitmap getThumbnail(ContentsManager.ThumbnailOption option) {
        BitmapFactory.Options factoryOption = new BitmapFactory.Options();
        Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(this.mResolver, this.mId._id, 3, factoryOption);
        return bmp;
    }
}
