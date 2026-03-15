package com.sony.imaging.app.snsdirect.util;

import android.util.Log;

/* loaded from: classes.dex */
public enum MimeType {
    IMAGE_JPEG,
    VIDEO_MJPEG,
    IMAGE_SPHOTO;

    private static final String IMAGE_JPEG_STR = "image/jpeg";
    private static final String IMAGE_SPHOTO_STR = "image/sphoto";
    private static final String TAG = MimeType.class.getSimpleName();
    private static final String VIDEO_AVI_STR = "video/avi";
    private static final String VIDEO_MJPEG_STR = "video/mjpeg";

    public static MimeType getMimeType(String mimeTypeStr) {
        if (IMAGE_JPEG_STR.equals(mimeTypeStr)) {
            return IMAGE_JPEG;
        }
        if (IMAGE_SPHOTO_STR.equals(mimeTypeStr)) {
            return IMAGE_SPHOTO;
        }
        if (VIDEO_MJPEG_STR.equals(mimeTypeStr)) {
            return VIDEO_MJPEG;
        }
        Log.e(TAG, "Unsupported MIME Type");
        return null;
    }

    public static String getMimeType(MimeType type) {
        switch (type) {
            case IMAGE_JPEG:
                return IMAGE_JPEG_STR;
            case IMAGE_SPHOTO:
                return IMAGE_SPHOTO_STR;
            case VIDEO_MJPEG:
                return VIDEO_MJPEG_STR;
            default:
                return null;
        }
    }

    public static String getGenuineMimeType(MimeType type) {
        switch (type) {
            case IMAGE_JPEG:
                return IMAGE_JPEG_STR;
            case IMAGE_SPHOTO:
                return IMAGE_SPHOTO_STR;
            case VIDEO_MJPEG:
                return VIDEO_AVI_STR;
            default:
                return null;
        }
    }
}
