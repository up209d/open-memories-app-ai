package com.sony.imaging.app.base.shooting.camera.moviesetting;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class EachDatabaseAccessor extends DatabaseAccessorBase {
    private static final int COPY_BYTE_VAL = 1024;
    private static final String DB_DLAPP_NAME = "DLAppMovieSetting.db";
    protected static final String FIRM_BASE_DIR = "/system/etc/setting/";
    protected static final int PF_VER_SUPPORTS_PROP_UI_MODEL_NAME = 14;
    private static final String TAG = EachDatabaseAccessor.class.getSimpleName();
    protected String mAppName;
    protected Context mContext;
    protected String mPackageName;

    public EachDatabaseAccessor(String appName) {
        this.mAppName = appName;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseAccessorBase
    public void initialize(Context context) {
        super.initialize(context);
        this.mContext = context;
        this.mPackageName = context.getPackageName();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseAccessorBase
    protected SQLiteDatabase setDataBase() {
        String filename = "libMovieSetting_" + this.mAppName + SRCtrlConstants.URI_CONTENT_ID_SEPARATOR;
        Pair<Integer, String> pf = getPfLatestVersion(FIRM_BASE_DIR, filename);
        Pair<Integer, String> own = getAppLatestVersion(filename);
        String path = null;
        if (pf != null || own != null) {
            if (pf == null) {
                path = (String) own.second;
            } else if (own == null) {
                path = (String) pf.second;
            } else if (((Integer) pf.first).intValue() < ((Integer) own.first).intValue()) {
                path = (String) own.second;
            } else {
                path = (String) pf.second;
            }
        }
        if (path != null) {
            try {
                return SQLiteDatabase.openDatabase(path, null, 17);
            } catch (SQLException e) {
                Log.i(TAG, "cannot open database");
            }
        }
        return null;
    }

    protected Pair<Integer, String> getPfLatestVersion(String path, String prefix) {
        Log.i(TAG, "getPfLatestVersion");
        File file = new File(path);
        Filter filter = new Filter(prefix);
        File[] files = file.listFiles(filter);
        int count = files == null ? 0 : files.length;
        int maxVersion = -1;
        File target = null;
        for (int i = 0; i < count; i++) {
            int version = Integer.parseInt(files[i].getName().substring(prefix.length(), prefix.length() + 2));
            Log.d(TAG, "db found : " + version);
            if (maxVersion < version) {
                maxVersion = version;
                target = files[i];
            }
        }
        if (-1 == maxVersion) {
            return null;
        }
        Log.i(TAG, "getPfLatestVersion  : " + target.getPath() + ", " + maxVersion);
        Pair<Integer, String> ret = new Pair<>(Integer.valueOf(maxVersion), target.getPath());
        return ret;
    }

    protected Pair<Integer, String> getAppLatestVersion(String prefix) {
        Log.i(TAG, "getAppLatestVersion");
        String path = "/data/data/" + this.mPackageName + "/lib/";
        File file = new File(path);
        Filter filter = new Filter(prefix);
        File[] files = file.listFiles(filter);
        int count = files == null ? 0 : files.length;
        int maxVersion = -1;
        String target = null;
        if (count <= 0) {
            return null;
        }
        String str = prefix + "(\\d+)\\.so";
        Pattern pattern = Pattern.compile(str);
        Pattern pattern_each_model = null;
        if (Environment.getVersionPfAPI() >= 14) {
            String model = ScalarProperties.getString("ui.model.mame");
            String str2 = prefix + model + "_(\\d+)\\.so";
            pattern_each_model = Pattern.compile(str2);
        }
        Log.i(TAG, "compile pattern");
        for (int i = 0; i < count; i++) {
            String name = files[i].getName();
            Matcher m = pattern.matcher(name);
            if (m.find()) {
                int version = Integer.parseInt(m.group(1));
                Log.d(TAG, "db found : " + name);
                if (maxVersion < version) {
                    maxVersion = version;
                    target = files[i].getPath();
                }
            } else if (pattern_each_model != null) {
                Matcher m2 = pattern_each_model.matcher(name);
                if (m2.find()) {
                    Log.i(TAG, "db found : " + name);
                    int version2 = Integer.parseInt(m2.group(1));
                    if (maxVersion <= version2) {
                        maxVersion = version2;
                        target = files[i].getPath();
                    }
                }
            }
        }
        if (-1 == maxVersion) {
            return null;
        }
        Log.i(TAG, "getAppLatestVersion  : " + target + ", " + maxVersion);
        Pair<Integer, String> ret = new Pair<>(Integer.valueOf(maxVersion), target);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Filter implements FilenameFilter {
        private String mPrefix;

        public Filter(String prefix) {
            this.mPrefix = prefix;
        }

        @Override // java.io.FilenameFilter
        public boolean accept(File dir, String name) {
            boolean ret = name.startsWith(this.mPrefix);
            return ret;
        }
    }

    private void createDatabase(String src) {
        boolean dbExist = checkDataBaseExist();
        Log.i(TAG, "createDatabase dbExist:" + dbExist);
        try {
            copyDataBaseFromAsset(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDataBaseExist() {
        File f = this.mContext.getFileStreamPath(DB_DLAPP_NAME);
        String path = f.getAbsolutePath();
        File dbFile = new File(path);
        return dbFile.exists();
    }

    private void copyDataBaseFromAsset(String src) throws IOException {
        Log.i(TAG, "[START]copyDataBaseFromAsset");
        InputStream mInput = this.mContext.getAssets().open(src);
        OutputStream mOutput = this.mContext.openFileOutput(DB_DLAPP_NAME, 0);
        byte[] buffer = new byte[COPY_BYTE_VAL];
        while (true) {
            int size = mInput.read(buffer);
            if (size >= 0) {
                mOutput.write(buffer, 0, size);
            } else {
                mOutput.flush();
                mOutput.close();
                mInput.close();
                Log.i(TAG, "[END]copyDataBaseFromAsset");
                return;
            }
        }
    }
}
