package com.sony.imaging.app.avi.util;

import android.os.Environment;
import android.util.Log;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.scalar.provider.AvindexStore;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MediaFileHelper {
    private static final HashMap<String, DatabaseUtil.MediaStatus> DBUTIL_MEDIA_STATE_MAP = new HashMap<>();
    private static final boolean DEBUG = true;
    private static final String EMPTY_STRING = "";
    private static final String TAG = "MediaFileHelper";
    private static final int WAIT_DURATION = 600;
    static final String mMediaId;

    public static boolean exists(File file) {
        Log.d(TAG, "exists : " + file.getPath());
        boolean result = file.exists();
        if (!result) {
            _wait();
        }
        return result;
    }

    public static boolean existsAndTouchIfNotExist(File file, String touchableFilePath) {
        Log.d(TAG, "existsAndTouchIfNotExist : " + file.getPath());
        File touchable = new File(touchableFilePath);
        boolean isTouchableExists = touchable.exists();
        if (!isTouchableExists) {
            Log.w(TAG, "touchableFile does not exist");
            _wait();
        }
        boolean result = file.exists();
        if (!result) {
            if (!isTouchableExists) {
                _wait();
            } else {
                try {
                    FileInputStream stream = new FileInputStream(touchableFilePath);
                    stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    _wait();
                } catch (IOException e2) {
                    e2.printStackTrace();
                    _wait();
                }
            }
        }
        return result;
    }

    public static boolean mkdirs(File directory) {
        Log.d(TAG, "mkdirs : " + directory.getAbsolutePath());
        File root = Environment.getExternalStorageDirectory();
        String rootPath = root.getAbsolutePath();
        if (directory.getAbsolutePath().indexOf(rootPath) != 0) {
            return directory.mkdirs();
        }
        String[] separatedPath = directory.getAbsolutePath().substring(rootPath.length()).split(File.separator);
        boolean isCreated = false;
        StringBuilder builder = new StringBuilder(rootPath);
        int c = separatedPath.length;
        for (int i = 0; i < c; i++) {
            if (!"".equals(separatedPath[i])) {
                String path = builder.append(File.separator).append(separatedPath[i]).toString();
                File dir = new File(path);
                if (isCreated && !dir.mkdir()) {
                    return false;
                }
                if (!dir.exists()) {
                    if (_wait() && dir.mkdir()) {
                        isCreated = true;
                    }
                    return false;
                }
                continue;
            }
        }
        return isCreated;
    }

    public static boolean createFileWithMkDirs(File file) throws IOException {
        Log.d(TAG, "createFileWithMkDirs : " + file.getAbsolutePath());
        File parent = file.getParentFile();
        if (parent != null) {
            if (mkdirs(parent)) {
                return file.createNewFile();
            }
            if (!parent.isDirectory()) {
                _wait();
                throw new IOException("failed to create directory");
            }
        }
        if (file.exists()) {
            return true;
        }
        if (!_wait()) {
            throw new IOException("failed to create directory");
        }
        return file.createNewFile();
    }

    public static boolean _wait() {
        DatabaseUtil.MediaStatus mediaStatus;
        long current;
        long target = System.currentTimeMillis() + 600;
        Log.e(TAG, "[DB] wait 600...");
        checkMediaStatus();
        while (true) {
            try {
                current = System.currentTimeMillis();
                Log.i(TAG, " _wait(): target = " + target + " current = " + current);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                checkMediaStatus();
            }
            if (current >= target) {
                break;
            }
            Thread.sleep(50L);
            mediaStatus = checkMediaStatus();
            if (!isReadableMedia(mediaStatus)) {
                break;
            }
        }
        return isReadableMedia(mediaStatus);
    }

    static {
        DBUTIL_MEDIA_STATE_MAP.put("removed", DatabaseUtil.MediaStatus.NO_CARD);
        DBUTIL_MEDIA_STATE_MAP.put("unmounted", DatabaseUtil.MediaStatus.NO_CARD);
        DBUTIL_MEDIA_STATE_MAP.put("checking", DatabaseUtil.MediaStatus.NO_CARD);
        DBUTIL_MEDIA_STATE_MAP.put("nofs", DatabaseUtil.MediaStatus.ERROR);
        DBUTIL_MEDIA_STATE_MAP.put("mounted", DatabaseUtil.MediaStatus.MOUNTED);
        DBUTIL_MEDIA_STATE_MAP.put("unmountable", DatabaseUtil.MediaStatus.ERROR);
        DBUTIL_MEDIA_STATE_MAP.put("shared", DatabaseUtil.MediaStatus.NO_CARD);
        DBUTIL_MEDIA_STATE_MAP.put("bad_removal", DatabaseUtil.MediaStatus.NO_CARD);
        DBUTIL_MEDIA_STATE_MAP.put("mounted_ro", DatabaseUtil.MediaStatus.READ_ONLY);
        mMediaId = AvindexStore.getExternalMediaIds()[0];
    }

    public static DatabaseUtil.MediaStatus checkMediaStatus() {
        String envExtState = Environment.getExternalStorageState();
        DatabaseUtil.MediaStatus mediaStatus = DBUTIL_MEDIA_STATE_MAP.get(envExtState);
        Log.i(TAG, "[DB] mediaStatus is " + mediaStatus);
        return mediaStatus;
    }

    public static boolean isReadableMedia(DatabaseUtil.MediaStatus s) {
        return s == DatabaseUtil.MediaStatus.MOUNTED || s == DatabaseUtil.MediaStatus.READ_ONLY;
    }

    public static boolean isWritableMedia(DatabaseUtil.MediaStatus s) {
        return s == DatabaseUtil.MediaStatus.MOUNTED;
    }

    public static boolean isNoMedia(DatabaseUtil.MediaStatus s) {
        return !isReadableMedia(s);
    }

    public static boolean isErrorMedia(DatabaseUtil.MediaStatus s) {
        return s == DatabaseUtil.MediaStatus.ERROR;
    }
}
