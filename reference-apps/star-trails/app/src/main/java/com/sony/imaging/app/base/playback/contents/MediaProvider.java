package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.io.File;

@Deprecated
/* loaded from: classes.dex */
public class MediaProvider extends ProviderHelper {
    private static final int FILE_DIGIT = 4;
    private static final int IMG_TARGET_HEIGHT = 480;
    private static final int IMG_TARGET_WIDTH = 800;
    private static String TAG = "CommonProvider";
    private String[] QUERY_PROJECTION = {"_id", "_data", "_display_name", "datetaken"};
    private String mCachedFile = null;
    private MediaContentInfo mCachedInfo = null;

    MediaProvider() {
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public void initialize(ContentResolver resolver) {
        super.initialize(resolver);
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public int getInt(Cursor c, String tag) {
        if (!tag.equals(IFolder.DCF_FOLDER_NUMBER)) {
            return super.getInt(c, tag);
        }
        String fname = c.getString(c.getColumnIndex("_display_name"));
        int index = fname.lastIndexOf(StringBuilderThreadLocal.PERIOD);
        String numString = fname.substring(index - 4, index);
        return Integer.valueOf(numString).intValue();
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public Bitmap getJpeg(ContentsIdentifier id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(id.data, options);
        Log.i(TAG, "getJpeg org size: " + options.outWidth + ", " + options.outHeight);
        int sampling = Math.max(options.outWidth / IMG_TARGET_WIDTH, options.outHeight / 480);
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampling;
        Bitmap bitmap = BitmapFactory.decodeFile(id.data, options);
        Log.i(TAG, "getJpeg scaled size: " + options.outWidth + ", " + options.outHeight);
        return bitmap;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public boolean delete(ContentsIdentifier id) {
        Uri uri = ContentUris.appendId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon(), id._id).build();
        Log.d(TAG, "delete entry : " + uri);
        this.mResolver.delete(uri, null, null);
        String path = id.data;
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        boolean result = file.delete();
        Log.d(TAG, "delete file : " + path + LogHelper.MSG_HYPHEN + result);
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public MediaContentInfo getContentInfo(ContentsIdentifier id) {
        if (id == null) {
            return null;
        }
        if (id.data.equals(this.mCachedFile)) {
            return this.mCachedInfo;
        }
        MediaContentInfo info = new MediaContentInfo(this.mResolver, id);
        this.mCachedInfo = info;
        this.mCachedFile = id.data;
        return info;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public void invalidateCachedInfo() {
        invalidateCachedInfo(7);
    }

    @Override // com.sony.imaging.app.base.playback.contents.ProviderHelper
    public void invalidateCachedInfo(int kind) {
        if ((kind & 1) != 0) {
            this.mCachedFile = null;
            this.mCachedInfo = null;
        }
    }
}
