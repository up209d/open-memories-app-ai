package com.sony.imaging.app.util;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.scalar.provider.AvindexStore;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;

/* loaded from: classes.dex */
public class DatabaseUtil implements NotificationListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "DatabaseUtil";
    public static final String TAG_DB_UTIL_STATUS_CHANGE = "com.sony.imaging.app.avi.DatabaseUtil.NotificationManager.DbUtilStatusChange";
    private static byte[] intHash;
    private static DbUtilNotificationManager mDbUtilNotificationManager;
    private static DatabaseUtil sDatabaseUtil;
    private static DatabaseAdapterIf sDbaIf;
    private static boolean sInternalDbValid;
    private static String[] sInvalidateNotifications;
    private static boolean sIsDatabaseChecked;

    /* loaded from: classes.dex */
    public enum CheckHashResult {
        NO_MEDIA_ERROR,
        FILE_ERROR,
        HASH_ERROR,
        SUCCEEDED
    }

    /* loaded from: classes.dex */
    public enum CopyResult {
        NO_FILE_ERROR,
        IMPORT_ERROR,
        EXPORT_ERROR,
        SUCCEEDED
    }

    /* loaded from: classes.dex */
    public enum DbResult {
        SUCCEEDED,
        MEDIA_ERROR,
        NO_MEDIA_ERROR,
        READ_ONLY_MEDIA_ERROR,
        DB_SYNC_ERROR,
        DB_ERROR
    }

    /* loaded from: classes.dex */
    public enum MediaStatus {
        NO_CARD,
        ERROR,
        READ_ONLY,
        MOUNTED
    }

    static {
        $assertionsDisabled = !DatabaseUtil.class.desiredAssertionStatus();
        sDatabaseUtil = new DatabaseUtil();
        sDbaIf = null;
        sInternalDbValid = false;
        sIsDatabaseChecked = false;
        intHash = new byte[20];
        mDbUtilNotificationManager = null;
    }

    public static synchronized DatabaseUtil getInstance() {
        DatabaseUtil databaseUtil;
        synchronized (DatabaseUtil.class) {
            databaseUtil = sDatabaseUtil;
        }
        return databaseUtil;
    }

    /* loaded from: classes.dex */
    private static class DbUtilNotificationManager extends NotificationManager {
        public DbUtilNotificationManager() {
            super(true, true);
        }

        @Override // com.sony.imaging.app.util.NotificationManager
        public Object getValue(String tag) {
            return null;
        }

        public void requestNotify() {
            notify("com.sony.imaging.app.avi.DatabaseUtil.NotificationManager.DbUtilStatusChange");
        }
    }

    public static synchronized void initialize(DatabaseAdapterIf dataBaseAdapter, byte[] seed, byte[] word) {
        synchronized (DatabaseUtil.class) {
            Crypt.setKeys(seed, word);
            if (dataBaseAdapter != null) {
                sDbaIf = dataBaseAdapter;
                sInternalDbValid = false;
                sIsDatabaseChecked = false;
                createNewEmptyInternalDb();
                mDbUtilNotificationManager = new DbUtilNotificationManager();
            }
        }
    }

    public static void terminate() {
        mDbUtilNotificationManager = null;
        try {
            createNewEmptyInternalDb();
        } catch (Exception e) {
        }
        sIsDatabaseChecked = false;
        sInternalDbValid = false;
        sDbaIf = null;
        sInvalidateNotifications = null;
        Crypt.setKeys(null, null);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return sInvalidateNotifications;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        invalidateInternalDb();
        setIsDatabaseChecked(false);
    }

    public static NotificationListener getInvalidator(String[] tags) {
        sInvalidateNotifications = tags;
        return getInstance();
    }

    public static NotificationListener getInvalidator(String tag) {
        String[] tags = {tag};
        sInvalidateNotifications = tags;
        return getInstance();
    }

    public static synchronized boolean isInternalDbValid() {
        boolean z;
        synchronized (DatabaseUtil.class) {
            z = sInternalDbValid;
        }
        return z;
    }

    public static synchronized void invalidateInternalDb() {
        synchronized (DatabaseUtil.class) {
            setInternalDbValid(false);
        }
    }

    private static synchronized void setInternalDbValid(boolean value) {
        synchronized (DatabaseUtil.class) {
            sInternalDbValid = value;
        }
    }

    public static synchronized void setIsDatabaseChecked(boolean value) {
        synchronized (DatabaseUtil.class) {
            sIsDatabaseChecked = value;
        }
    }

    public static synchronized boolean isDatabaseChecked() {
        boolean z;
        synchronized (DatabaseUtil.class) {
            z = sIsDatabaseChecked;
        }
        return z;
    }

    public static synchronized MediaStatus checkMediaStatus() {
        MediaStatus checkMediaStatus;
        synchronized (DatabaseUtil.class) {
            checkMediaStatus = MediaFileHelper.checkMediaStatus();
        }
        return checkMediaStatus;
    }

    public static synchronized DbResult importDatabaseFromMedia(File extDbFile, File intDbFile) {
        DbResult result;
        synchronized (DatabaseUtil.class) {
            result = importDatabaseFromMediaInternal(extDbFile, intDbFile);
            mDbUtilNotificationManager.requestNotify();
        }
        return result;
    }

    private static synchronized DbResult importDatabaseFromMediaInternal(File extDbFile, File intDbFile) {
        DbResult dbResult;
        synchronized (DatabaseUtil.class) {
            Log.i(TAG, "[DB] ***importDatabaseFromMedia()***");
            Log.i(TAG, "[DB]   extDbFile = [" + extDbFile.toString() + "]");
            Log.i(TAG, "[DB]   intDbFile = [" + intDbFile.toString() + "]");
            MediaStatus mediaStatus = MediaFileHelper.checkMediaStatus();
            if (mediaStatus == MediaStatus.ERROR) {
                invalidateInternalDb();
                createNewEmptyInternalDb();
                dbResult = DbResult.MEDIA_ERROR;
            } else if (MediaFileHelper.isNoMedia(mediaStatus) || extDbFile == null || intDbFile == null || extDbFile.toString().equals("") || intDbFile.toString().equals("")) {
                Log.e(TAG, "[DB] argument is null!!!!");
                invalidateInternalDb();
                createNewEmptyInternalDb();
                Log.i(TAG, "[DB]  - IMPORT(NO_MEDIA_ERROR)");
                dbResult = DbResult.NO_MEDIA_ERROR;
            } else if (MediaFileHelper.exists(extDbFile)) {
                CheckHashResult checkHashResult = checkHashCore(extDbFile);
                if (checkHashResult == CheckHashResult.SUCCEEDED) {
                    Log.i(TAG, "[DB]  - IMPORT(SUCCEEDED): Skip DB re-loading.");
                    setInternalDbValid(true);
                    dbResult = DbResult.SUCCEEDED;
                } else if (checkHashResult == CheckHashResult.NO_MEDIA_ERROR) {
                    Log.i(TAG, "[DB]  - IMPORT(NO_MEDIA_ERROR)");
                    invalidateInternalDb();
                    createNewEmptyInternalDb();
                    dbResult = DbResult.NO_MEDIA_ERROR;
                } else if (checkHashResult == CheckHashResult.FILE_ERROR) {
                    Log.i(TAG, "[DB]  - IMPORT(NO_FILE_ERROR)");
                    invalidateInternalDb();
                    createNewEmptyInternalDb();
                    dbResult = DbResult.DB_ERROR;
                } else if (checkHashResult == CheckHashResult.HASH_ERROR) {
                    CopyResult copyResult = copyExternalDbtoInternal(extDbFile, intDbFile);
                    if (copyResult == CopyResult.SUCCEEDED) {
                        Log.i(TAG, "[DB]  - IMPORT(SUCCEEDED): DB loaded.");
                        setInternalDbValid(true);
                        dbResult = DbResult.SUCCEEDED;
                    } else if (copyResult == CopyResult.NO_FILE_ERROR) {
                        invalidateInternalDb();
                        createNewEmptyInternalDb();
                        Log.i(TAG, "[DB]  - IMPORT(NO_MEDIA_ERROR)");
                        dbResult = DbResult.NO_MEDIA_ERROR;
                    } else if (copyResult == CopyResult.IMPORT_ERROR) {
                        createNewEmptyInternalDb();
                        if (copyInternalDbtoExternal(extDbFile, intDbFile)) {
                            Log.e(TAG, "[DB]  - IMPORT&EXPORT(SUCCEEDED): External DB was broken. New DB is stored.");
                            setInternalDbValid(true);
                            dbResult = DbResult.SUCCEEDED;
                        } else {
                            Log.e(TAG, "[DB]  - IMPORT(DB_ERROR): External DB was broken. New DB is not stored.");
                            invalidateInternalDb();
                            dbResult = DbResult.DB_ERROR;
                        }
                    } else {
                        Log.e(TAG, "[DB]  - IMPORT(1.3): UNKNOWN ERROR!!");
                        createNewEmptyInternalDb();
                        invalidateInternalDb();
                        dbResult = DbResult.DB_ERROR;
                    }
                } else {
                    Log.e(TAG, "[DB]  - IMPORT(1.3): UNKNOWN ERROR!!");
                    createNewEmptyInternalDb();
                    invalidateInternalDb();
                    dbResult = DbResult.DB_ERROR;
                }
            } else {
                MediaStatus mediaStatus2 = MediaFileHelper.checkMediaStatus();
                if (MediaFileHelper.isNoMedia(mediaStatus2)) {
                    invalidateInternalDb();
                    createNewEmptyInternalDb();
                    Log.i(TAG, "[DB]  - IMPORT(NO_MEDIA_ERROR)");
                    dbResult = DbResult.NO_MEDIA_ERROR;
                } else {
                    createNewEmptyInternalDb();
                    Log.i(TAG, "[DB]  - IMPORT: Internal DB created.");
                    if (mediaStatus2 == MediaStatus.READ_ONLY) {
                        Log.w(TAG, "[DB]  - IMPORT(READ_ONLY_MEDIA_ERROR): External media is READ ONLY MEDIA!!");
                        invalidateInternalDb();
                        dbResult = DbResult.READ_ONLY_MEDIA_ERROR;
                    } else if (copyInternalDbtoExternal(extDbFile, intDbFile, true)) {
                        Log.w(TAG, "[DB]  - IMPORT(SUCCEEDED): Copy new internal DB to external.");
                        setInternalDbValid(true);
                        dbResult = DbResult.SUCCEEDED;
                    } else {
                        Log.e(TAG, "[DB]  - IMPORT(DB_ERROR): Failed to copy new internal DB to external!!");
                        invalidateInternalDb();
                        dbResult = DbResult.DB_ERROR;
                    }
                }
            }
        }
        return dbResult;
    }

    public static synchronized DbResult exportDatabaseToMedia(File extDbFile, File intDbFile) {
        DbResult result;
        synchronized (DatabaseUtil.class) {
            result = exportDatabaseToMediaInternal(extDbFile, intDbFile);
            mDbUtilNotificationManager.requestNotify();
        }
        return result;
    }

    private static synchronized DbResult exportDatabaseToMediaInternal(File extDbFile, File intDbFile) {
        DbResult dbResult;
        synchronized (DatabaseUtil.class) {
            Log.i(TAG, "[DB] ***exportDatabaseToMedia()***");
            Log.i(TAG, "[DB]   extDbFile = [" + extDbFile.toString() + "]");
            Log.i(TAG, "[DB]   intDbFile = [" + intDbFile.toString() + "]");
            if (extDbFile == null || intDbFile == null || extDbFile.toString().equals("") || intDbFile.toString().equals("")) {
                Log.e(TAG, "[DB] file path contains null!!!!");
                Log.e(TAG, "[DB] *** EXPORT: NO_MEDIA_ERROR!!! ***");
                invalidateInternalDb();
                createNewEmptyInternalDb();
                dbResult = DbResult.NO_MEDIA_ERROR;
            } else if (!isInternalDbValid()) {
                Log.e(TAG, "[DB] *** EXPORT: DB_SYNC_ERROR(internal DB is invalidated)!!! ***");
                dbResult = DbResult.DB_SYNC_ERROR;
            } else {
                MediaStatus mediaStatus = MediaFileHelper.checkMediaStatus();
                if (mediaStatus == MediaStatus.ERROR) {
                    Log.e(TAG, "[DB] *** EXPORT: MEDIA_ERROR!!! ***");
                    invalidateInternalDb();
                    createNewEmptyInternalDb();
                    dbResult = DbResult.MEDIA_ERROR;
                } else if (mediaStatus == MediaStatus.NO_CARD) {
                    Log.e(TAG, "[DB] *** EXPORT: NO_MEDIA_ERROR!!! ***");
                    invalidateInternalDb();
                    createNewEmptyInternalDb();
                    dbResult = DbResult.NO_MEDIA_ERROR;
                } else if (mediaStatus == MediaStatus.READ_ONLY) {
                    Log.e(TAG, "[DB] *** EXPORT: READ_ONLY_MEDIA_ERROR!!! ***");
                    dbResult = DbResult.READ_ONLY_MEDIA_ERROR;
                } else if (!checkHash(extDbFile)) {
                    Log.e(TAG, "[DB] *** EXPORT: DB_SYNC_ERROR(hash error)!!! ***");
                    dbResult = DbResult.DB_SYNC_ERROR;
                } else if (!copyInternalDbtoExternal(extDbFile, intDbFile)) {
                    Log.e(TAG, "[DB] *** EXPORT: DB_ERROR!!! ***");
                    dbResult = DbResult.DB_ERROR;
                } else {
                    Log.i(TAG, "[DB]  - EXPORT(2.0): DB exported.");
                    Log.w(TAG, "[DB]  - EXPORT(---): finish.");
                    dbResult = DbResult.SUCCEEDED;
                }
            }
        }
        return dbResult;
    }

    private static void createNewEmptyInternalDb() {
        if (!$assertionsDisabled && sDbaIf == null) {
            throw new AssertionError();
        }
        sDbaIf.createNewEmptyInternalDb();
    }

    private static CopyResult copyExternalDbtoInternal(File extDbFile, File intDbFile) {
        CopyResult result;
        byte[] dbData;
        CopyResult copyResult = CopyResult.IMPORT_ERROR;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (extDbFile.length() > 262144) {
                return CopyResult.IMPORT_ERROR;
            }
            try {
                byte[] dbData2 = new byte[(int) extDbFile.length()];
                FileInputStream fis2 = new FileInputStream(extDbFile);
                try {
                    fis2.read(dbData2);
                    fis2.close();
                    fis = null;
                    dbData = Crypt.decrypt(dbData2, intHash);
                    debugDisplayHash("Crypt.decrypt int", intHash, 20);
                } catch (FileNotFoundException e) {
                    e = e;
                    fis = fis2;
                } catch (Exception e2) {
                    e = e2;
                    fis = fis2;
                } catch (Throwable th) {
                    th = th;
                    fis = fis2;
                }
            } catch (FileNotFoundException e3) {
                e = e3;
            } catch (Exception e4) {
                e = e4;
            }
            if (dbData == null) {
                CopyResult copyResult2 = CopyResult.IMPORT_ERROR;
                if (0 != 0) {
                    try {
                        fis.close();
                    } catch (IOException e5) {
                        Log.w(TAG, e5.toString());
                        e5.printStackTrace();
                    }
                }
                if (0 == 0) {
                    return copyResult2;
                }
                try {
                    fos.close();
                } catch (IOException e6) {
                    Log.w(TAG, e6.toString());
                    e6.printStackTrace();
                }
                return copyResult2;
            }
            FileOutputStream fos2 = new FileOutputStream(intDbFile);
            try {
                fos2.write(dbData);
                fos2.close();
                FileOutputStream fos3 = null;
                result = CopyResult.SUCCEEDED;
                Log.i(TAG, "[DB] EXTERNAL -> INTERNAL DB copy executed!!");
                if (0 != 0) {
                    try {
                        fis.close();
                    } catch (IOException e7) {
                        Log.w(TAG, e7.toString());
                        e7.printStackTrace();
                    }
                }
                if (0 != 0) {
                    try {
                        fos3.close();
                    } catch (IOException e8) {
                        Log.w(TAG, e8.toString());
                        e8.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e9) {
                e = e9;
                fos = fos2;
                result = CopyResult.NO_FILE_ERROR;
                Log.w(TAG, e.toString());
                e.printStackTrace();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e10) {
                        Log.w(TAG, e10.toString());
                        e10.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e11) {
                        Log.w(TAG, e11.toString());
                        e11.printStackTrace();
                    }
                }
                return result;
            } catch (Exception e12) {
                e = e12;
                fos = fos2;
                result = CopyResult.IMPORT_ERROR;
                Log.w(TAG, e.toString());
                e.printStackTrace();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e13) {
                        Log.w(TAG, e13.toString());
                        e13.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e14) {
                        Log.w(TAG, e14.toString());
                        e14.printStackTrace();
                    }
                }
                return result;
            } catch (Throwable th2) {
                th = th2;
                fos = fos2;
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e15) {
                        Log.w(TAG, e15.toString());
                        e15.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e16) {
                        Log.w(TAG, e16.toString());
                        e16.printStackTrace();
                    }
                }
                throw th;
            }
            return result;
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private static boolean copyInternalDbtoExternal(File extDbFile, File intDbFile) {
        return copyInternalDbtoExternal(extDbFile, intDbFile, false);
    }

    private static boolean copyInternalDbtoExternal(File extDbFile, File intDbFile, boolean safeMode) {
        boolean result;
        boolean createNewFileResult;
        byte[] dbData;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            try {
                createNewFileResult = MediaFileHelper.createFileWithMkDirs(extDbFile);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        if (safeMode && !createNewFileResult) {
            if (0 != 0) {
                try {
                    fis.close();
                } catch (IOException e2) {
                    Log.w(TAG, e2.toString());
                    e2.printStackTrace();
                }
            }
            if (0 == 0) {
                return false;
            }
            try {
                fos.close();
            } catch (IOException e3) {
                Log.w(TAG, e3.toString());
                e3.printStackTrace();
            }
            return false;
        }
        byte[] dbData2 = new byte[(int) intDbFile.length()];
        FileInputStream fis2 = new FileInputStream(intDbFile);
        try {
            fis2.read(dbData2);
            fis2.close();
            fis = null;
            dbData = Crypt.encrypt(dbData2, intHash);
            debugDisplayHash("Crypt.encrypt int", intHash, 20);
        } catch (Exception e4) {
            e = e4;
            fis = fis2;
        } catch (Throwable th2) {
            th = th2;
            fis = fis2;
        }
        if (dbData == null) {
            if (0 != 0) {
                try {
                    fis.close();
                } catch (IOException e5) {
                    Log.w(TAG, e5.toString());
                    e5.printStackTrace();
                }
            }
            if (0 == 0) {
                return false;
            }
            try {
                fos.close();
            } catch (IOException e6) {
                Log.w(TAG, e6.toString());
                e6.printStackTrace();
            }
            return false;
        }
        FileOutputStream fos2 = new FileOutputStream(extDbFile);
        try {
            fos2.write(dbData);
            fos2.close();
            fos = null;
            result = true;
            Log.i(TAG, "[DB] INTERNAL -> EXTERNAL DB copy succeeded!!");
            Log.i(TAG, "[DB] IMDLAPP2-1069 extDbFile.length() = " + extDbFile.length());
            MediaFileHelper._wait();
            Log.i(TAG, "[DB] IMDLAPP2-1069 extDbFile.length() = " + extDbFile.length());
            if (0 != 0) {
                try {
                    fis.close();
                } catch (IOException e7) {
                    Log.w(TAG, e7.toString());
                    e7.printStackTrace();
                }
            }
            if (0 != 0) {
                try {
                    fos.close();
                } catch (IOException e8) {
                    Log.w(TAG, e8.toString());
                    e8.printStackTrace();
                }
            }
        } catch (Exception e9) {
            e = e9;
            fos = fos2;
            result = false;
            Log.e(TAG, e.toString());
            e.printStackTrace();
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e10) {
                    Log.w(TAG, e10.toString());
                    e10.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e11) {
                    Log.w(TAG, e11.toString());
                    e11.printStackTrace();
                }
            }
            return result;
        } catch (Throwable th3) {
            th = th3;
            fos = fos2;
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e12) {
                    Log.w(TAG, e12.toString());
                    e12.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e13) {
                    Log.w(TAG, e13.toString());
                    e13.printStackTrace();
                }
            }
            throw th;
        }
        return result;
    }

    private static boolean checkHash(File extDbFile) {
        boolean result = false;
        Log.i(TAG, "[DB] ***checkHash()***");
        Log.i(TAG, "[DB]   extDbFile = [" + extDbFile.toString() + "]");
        long t0 = System.nanoTime();
        if (!FileHelper.exists(extDbFile)) {
            invalidateInternalDb();
            Log.e(TAG, "[DB] checkHash NG(1)!");
        } else if (!isInternalDbValid()) {
            Log.e(TAG, "[DB] checkHash NG(2)!");
        } else {
            CheckHashResult hashResult = checkHashCore(extDbFile);
            if (hashResult == CheckHashResult.SUCCEEDED) {
                result = true;
            } else {
                invalidateInternalDb();
                result = false;
            }
            long t1 = System.nanoTime();
            Log.i(TAG, "checkHash time = " + ((t1 - t0) / 1000000) + "ms");
            Log.i(TAG, "[DB] checkHash OK!");
        }
        return result;
    }

    private static CheckHashResult checkHashCore(File extDbFile) {
        RandomAccessFile raf;
        CheckHashResult checkHashResult;
        byte[] extHash = new byte[20];
        MediaStatus mediaStatus = MediaFileHelper.checkMediaStatus();
        if (MediaFileHelper.isNoMedia(mediaStatus)) {
            return CheckHashResult.NO_MEDIA_ERROR;
        }
        if (!isInternalDbValid()) {
            return CheckHashResult.HASH_ERROR;
        }
        try {
            try {
                raf = new RandomAccessFile(extDbFile, "r");
                try {
                    int offset = (int) (raf.length() - 20);
                    if (offset < 0) {
                        Log.e(TAG, "[DB] checkHash NG(HASH_ERROR)!");
                        checkHashResult = CheckHashResult.HASH_ERROR;
                        if (raf != null) {
                            try {
                                raf.close();
                            } catch (IOException e) {
                            }
                            return checkHashResult;
                        }
                    } else {
                        raf.seek(offset);
                        raf.read(extHash);
                        debugDisplayHash("ext", extHash, 20);
                        debugDisplayHash("int", intHash, 20);
                        if (Arrays.equals(extHash, intHash)) {
                            if (raf != null) {
                                try {
                                    raf.close();
                                } catch (IOException e2) {
                                }
                            }
                            MediaStatus mediaStatus2 = MediaFileHelper.checkMediaStatus();
                            return MediaFileHelper.isNoMedia(mediaStatus2) ? CheckHashResult.NO_MEDIA_ERROR : CheckHashResult.SUCCEEDED;
                        }
                        Log.e(TAG, "[DB] checkHash NG(HASH_ERROR)!");
                        checkHashResult = CheckHashResult.HASH_ERROR;
                        if (raf != null) {
                            try {
                                raf.close();
                            } catch (IOException e3) {
                            }
                            return checkHashResult;
                        }
                    }
                } catch (IOException e4) {
                    Log.e(TAG, "[DB] checkHash NG(HASH_ERROR)!");
                    checkHashResult = CheckHashResult.HASH_ERROR;
                    if (raf != null) {
                        try {
                            raf.close();
                        } catch (IOException e5) {
                        }
                        return checkHashResult;
                    }
                }
                return checkHashResult;
            } catch (FileNotFoundException e6) {
                Log.e(TAG, "[DB] checkHash NG(NO_FILE_ERROR)!");
                return CheckHashResult.FILE_ERROR;
            } catch (IllegalArgumentException e7) {
                Log.e(TAG, "[DB] checkHash NG(NO_FILE_ERROR)!");
                return CheckHashResult.FILE_ERROR;
            }
        } catch (Throwable th) {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e8) {
                }
            }
            throw th;
        }
    }

    public static String debugDisplayHash(String prefix, byte[] ba, int num) {
        int length = 0;
        if (ba != null) {
            length = ba.length;
        }
        return debugDisplayHash(prefix, ba, 0, length, num);
    }

    public static String debugDisplayHash(String prefix, byte[] ba, int offset, int length, int num) {
        if (ba == null) {
            Log.i(TAG, ExposureModeController.SOFT_SNAP + prefix + "[null]");
            return null;
        }
        StringBuffer strbuf = new StringBuffer(num * 2);
        for (int n = 0; n < length; n++) {
            int value = ba[n + offset] & 255;
            if (value < 16) {
                strbuf.append("0");
            }
            strbuf.append(Integer.toHexString(value));
            if (n % num == num - 1 || n == length - 1) {
                Log.i(TAG, ExposureModeController.SOFT_SNAP + prefix + "[" + ((Object) strbuf) + "]");
                strbuf.delete(0, strbuf.length());
            }
        }
        return "";
    }

    public static synchronized boolean exportDataToMedia(String extPath, String extFileName, byte[] data, String databaseFilePath) {
        boolean result;
        synchronized (DatabaseUtil.class) {
            Log.i(TAG, "[DB] ***exportDataToMedia()***");
            Log.i(TAG, "[DB]   extPath = [" + extPath + "]");
            Log.i(TAG, "[DB]   extFileName = [" + extFileName + "]");
            Log.i(TAG, "[DB]   databaseFilePath = [" + databaseFilePath + "]");
            if (extPath == null || extFileName == null || databaseFilePath == null) {
                Log.e(TAG, "[DB] argument is null!!!!");
                result = false;
            } else {
                result = true;
                File extDir = new File(extPath);
                if (FileHelper.exists(extDir)) {
                    Log.d(TAG, "================= Directory all ready exist to store files==================== ");
                } else if (FileHelper.mkdirs(extDir)) {
                    Log.d(TAG, "================= Directory Created Successfully ==================== ");
                } else {
                    Log.e(TAG, "================= Directory not created ================== ");
                    result = false;
                }
                File extFile = new File(extPath + "/" + extFileName);
                FileOutputStream fos = null;
                try {
                    if (!FileHelper.existsAndTouchIfNotExist(extFile, databaseFilePath)) {
                        try {
                            FileOutputStream fos2 = new FileOutputStream(extFile);
                            try {
                                fos2.write(data);
                                fos2.close();
                                fos = null;
                                if (0 != 0) {
                                    try {
                                        fos.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    fos = null;
                                }
                            } catch (FileNotFoundException e2) {
                                e = e2;
                                fos = fos2;
                                e.printStackTrace();
                                result = false;
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e3) {
                                        e3.printStackTrace();
                                    }
                                    fos = null;
                                }
                                return result;
                            } catch (IOException e4) {
                                e = e4;
                                fos = fos2;
                                e.printStackTrace();
                                result = false;
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e5) {
                                        e5.printStackTrace();
                                    }
                                    fos = null;
                                }
                                return result;
                            } catch (Throwable th) {
                                th = th;
                                fos = fos2;
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e6) {
                                        e6.printStackTrace();
                                    }
                                }
                                throw th;
                            }
                        } catch (FileNotFoundException e7) {
                            e = e7;
                        } catch (IOException e8) {
                            e = e8;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        }
        return result;
    }

    public static void setNotificationListener(NotificationListener listener) {
        mDbUtilNotificationManager.setNotificationListener(listener);
        mDbUtilNotificationManager.dumpListener();
    }

    public static void setNotificationListener(NotificationListener listener, boolean addEvenIfRegistered) {
        mDbUtilNotificationManager.setNotificationListener(listener, addEvenIfRegistered);
        mDbUtilNotificationManager.dumpListener();
    }

    public static void removeNotificationListener(NotificationListener listener) {
        mDbUtilNotificationManager.removeNotificationListener(listener);
        mDbUtilNotificationManager.dumpListener();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MediaFileHelper {
        private static final HashMap<String, MediaStatus> DBUTIL_MEDIA_STATE_MAP = new HashMap<>();
        private static final boolean DEBUG = true;
        private static final String EMPTY_STRING = "";
        private static final String TAG = "MediaFileHelper";
        private static final int WAIT_DURATION = 600;
        static final String mMediaId;

        private MediaFileHelper() {
        }

        public static boolean exists(File file) {
            Log.d(TAG, "exists : " + file.getPath());
            boolean result = file.exists();
            if (!result) {
                _wait();
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
            MediaStatus mediaStatus;
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
            DBUTIL_MEDIA_STATE_MAP.put("removed", MediaStatus.NO_CARD);
            DBUTIL_MEDIA_STATE_MAP.put("unmounted", MediaStatus.NO_CARD);
            DBUTIL_MEDIA_STATE_MAP.put("checking", MediaStatus.NO_CARD);
            DBUTIL_MEDIA_STATE_MAP.put("nofs", MediaStatus.ERROR);
            DBUTIL_MEDIA_STATE_MAP.put("mounted", MediaStatus.MOUNTED);
            DBUTIL_MEDIA_STATE_MAP.put("unmountable", MediaStatus.ERROR);
            DBUTIL_MEDIA_STATE_MAP.put("shared", MediaStatus.NO_CARD);
            DBUTIL_MEDIA_STATE_MAP.put("bad_removal", MediaStatus.NO_CARD);
            DBUTIL_MEDIA_STATE_MAP.put("mounted_ro", MediaStatus.READ_ONLY);
            mMediaId = AvindexStore.getExternalMediaIds()[0];
        }

        public static MediaStatus checkMediaStatus() {
            String envExtState = android.os.Environment.getExternalStorageState();
            MediaStatus mediaStatus = DBUTIL_MEDIA_STATE_MAP.get(envExtState);
            Log.i(TAG, "[DB] mediaStatus is " + mediaStatus);
            return mediaStatus;
        }

        public static boolean isReadableMedia(MediaStatus s) {
            return s == MediaStatus.MOUNTED || s == MediaStatus.READ_ONLY;
        }

        public static boolean isWritableMedia(MediaStatus s) {
            return s == MediaStatus.MOUNTED;
        }

        public static boolean isNoMedia(MediaStatus s) {
            return !isReadableMedia(s);
        }

        public static boolean isErrorMedia(MediaStatus s) {
            return s == MediaStatus.ERROR;
        }
    }
}
