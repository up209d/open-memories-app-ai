package com.sony.imaging.app.util;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public class FileHelper {
    private static final boolean DEBUG = true;
    private static final String EMPTY_STRING = "";
    private static final String TAG = "FileHelper";
    private static final int WAIT_DURATION = 600;

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
        File root = android.os.Environment.getExternalStorageDirectory();
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
                if (dir.exists()) {
                    continue;
                } else {
                    _wait();
                    if (!dir.mkdir()) {
                        return false;
                    }
                    isCreated = DEBUG;
                }
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
            return DEBUG;
        }
        _wait();
        return file.createNewFile();
    }

    private static void _wait() {
        long target = System.currentTimeMillis() + 600;
        while (true) {
            try {
                long duration = target - System.currentTimeMillis();
                if (0 < duration) {
                    Thread.sleep(duration);
                    return;
                }
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
