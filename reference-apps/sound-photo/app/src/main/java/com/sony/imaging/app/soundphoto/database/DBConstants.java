package com.sony.imaging.app.soundphoto.database;

import com.sony.imaging.app.soundphoto.util.AppContext;

/* loaded from: classes.dex */
public class DBConstants {
    public static final String COL_CONTENT_ID = "content_id";
    public static final String COL_FILE_NUMBER = "file_number";
    public static final String COL_FILE_PATH = "file_path";
    public static final String COL_FOLDER_NUMBER = "folder_number";
    public static final String COL_UPDATE_STATUS = "update_status";
    public static final int DATABASE_MODE_READ = 2;
    public static final int DATABASE_MODE_WRITE = 1;
    public static final String DATABASE_NAME = "soundphoto.db";
    public static final String DATABASE_PROPERTIES_FILENAME = "dbProperties.sql";
    private static String DATA_DATA = "/data/data/";
    public static final String INTERNAL_DATABASE_PATH = DATA_DATA + AppContext.getAppContext().getPackageName() + "/databases/";
    public static final String PRIVATE_FILE_NAME = "APP00001.BIN";
    public static final int STATUS_NOTUPDATED = 0;
    public static final int STATUS_UPDATED = 1;
    public static final String TAB_TABLE_NAME = "tblSoundPhoto";
}
