package com.sony.imaging.app.srctrl.util;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.aviadapter.FilesMedia;
import com.sony.imaging.app.base.playback.contents.aviadapter.IMedia;
import com.sony.imaging.app.base.playback.contents.aviadapter.ImagesMedia;
import com.sony.imaging.app.util.Environment;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/* loaded from: classes.dex */
public class SRCtrlPlaybackUtil {
    private static final int CONTENT_TYPE = 2;
    private static final int MEDIA_ID = 3;
    private static final String MOD_PREFIX_UNIQID = "index";
    private static final String ORG_PREFIX_UNIQID = "avindex";
    private static final String TAG = SRCtrlPlaybackUtil.class.getName();
    private static final int UNIQ_ID = 0;
    private static final int URI_LENGTH = 4;
    private static final int _ID = 1;
    private static final Object sLock;
    private static final IMedia sMediaAdapter;

    static {
        sMediaAdapter = Environment.isAvindexFilesSupported() ? new FilesMedia() : new ImagesMedia();
        sLock = new Object();
    }

    private SRCtrlPlaybackUtil() {
    }

    public static Object getContentTransferLock() {
        return sLock;
    }

    public static String encUniqId(String orgUniqId) {
        if (orgUniqId == null || -1 == orgUniqId.indexOf(ORG_PREFIX_UNIQID)) {
            return null;
        }
        String encUniqId = orgUniqId.replace(ORG_PREFIX_UNIQID, MOD_PREFIX_UNIQID);
        return encUniqId;
    }

    public static String decUniqId(String encUniqId) {
        if (encUniqId == null || -1 == encUniqId.indexOf(MOD_PREFIX_UNIQID)) {
            return null;
        }
        String orgUniqId = encUniqId.replace(MOD_PREFIX_UNIQID, ORG_PREFIX_UNIQID);
        return orgUniqId;
    }

    public static String urlEncode(String orgUrl) {
        String encUrl = null;
        if (orgUrl != null && (encUrl = encUniqId(orgUrl)) != null) {
            try {
                encUrl = URLEncoder.encode(encUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                encUrl = null;
            }
            Log.i(TAG, "uri Encode = " + encUrl + "/" + orgUrl);
        }
        return encUrl;
    }

    public static String urlDecode(String encUrl) {
        String decUrl;
        String modUrl = null;
        if (encUrl != null && (decUrl = decUniqId(encUrl)) != null) {
            try {
                String decUrl2 = URLDecoder.decode(decUrl, "UTF-8");
                int index = decUrl2.indexOf(ORG_PREFIX_UNIQID);
                modUrl = decUrl2.substring(index);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "uri Decode = " + modUrl + "/" + encUrl);
        }
        return modUrl;
    }

    public static String getContentType(String url) {
        if (-1 != url.indexOf(SRCtrlConstants.CONTENTTRANSFER_ORG)) {
            Log.i(TAG, "getContentType = ORG(" + url + LogHelper.MSG_CLOSE_BRACKET);
            return SRCtrlConstants.IMAGE_TYPE_ORG;
        }
        if (-1 != url.indexOf(SRCtrlConstants.CONTENTTRANSFER_ORGJPEG)) {
            Log.i(TAG, "getContentType = ORGJPEG(" + url + LogHelper.MSG_CLOSE_BRACKET);
            return SRCtrlConstants.IMAGE_TYPE_ORGJPEG;
        }
        if (-1 != url.indexOf(SRCtrlConstants.CONTENTTRANSFER_ORGRAW)) {
            Log.i(TAG, "getContentType = ORGRAW(" + url + LogHelper.MSG_CLOSE_BRACKET);
            return SRCtrlConstants.IMAGE_TYPE_ORGRAW;
        }
        if (-1 != url.indexOf(SRCtrlConstants.CONTENTTRANSFER_THUMB)) {
            Log.i(TAG, "getContentType = THUMB(" + url + LogHelper.MSG_CLOSE_BRACKET);
            return "Thumbnail";
        }
        if (-1 != url.indexOf(SRCtrlConstants.CONTENTTRANSFER_LARGE)) {
            Log.i(TAG, "getContentType = SCN(" + url + LogHelper.MSG_CLOSE_BRACKET);
            return "Scn";
        }
        if (-1 == url.indexOf(SRCtrlConstants.CONTENTTRANSFER_SMALL)) {
            return "Unknown";
        }
        Log.i(TAG, "getContentType = VGA(" + url + LogHelper.MSG_CLOSE_BRACKET);
        return SRCtrlConstants.IMAGE_TYPE_SMALL;
    }

    public static ContentsIdentifier getContentsIdentifier(String url) {
        String[] urlAry = url.split(SRCtrlConstants.URI_CONTENT_ID_SEPARATOR);
        if (4 == urlAry.length) {
            return new ContentsIdentifier(Long.valueOf(urlAry[1]).longValue(), urlAry[0], urlAry[3], Integer.valueOf(urlAry[2]).intValue(), sMediaAdapter);
        }
        return null;
    }

    public static String getUniqId(String url) {
        String[] urlAry = url.split(SRCtrlConstants.URI_CONTENT_ID_SEPARATOR);
        if (4 == urlAry.length) {
            return urlAry[0];
        }
        return null;
    }

    public static long getRecOrderId(String url) {
        String[] urlAry = url.split(SRCtrlConstants.URI_CONTENT_ID_SEPARATOR);
        if (4 == urlAry.length) {
            return Long.valueOf(urlAry[1]).longValue();
        }
        return -1L;
    }
}
