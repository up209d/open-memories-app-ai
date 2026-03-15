package com.sony.imaging.app.timelapse.databaseutil;

/* loaded from: classes.dex */
public class DBConstants {
    public static final String COL_END_UTC_DATETIME = "EndUTCDateTime";
    public static final String COL_FILE_PATH = "FilePath";
    public static final String COL_FPS = "Fps";
    public static final String COL_HEIGHT = "Height";
    public static final String COL_ID = "ID";
    public static final String COL_MOVIE_MODE = "MovieMode";
    public static final String COL_SHOOTINGNUMBER = "ShootingNumber";
    public static final String COL_SHOOTING_MODE = "ShootingMode";
    public static final String COL_START_DATE = "StartDate";
    public static final String COL_START_TIME = "StartTime";
    public static final String COL_START_UTC_DATETIME = "StartUTCDateTime";
    public static final String COL_THEMENAME = "ThemeName";
    public static final String COL_WIDTH = "Width";
    public static final int DATABASE_MODE_READ = 2;
    public static final int DATABASE_MODE_WRITE = 1;
    public static final String DATABASE_NAME = "timelapse.db";
    public static final String DATABASE_PROPERTIES_FILENAME = "dbProperties.sql";
    private static String DATA_DATA = "/data/data/";
    public static final String INTERNAL_DATABASE_PATH = DATA_DATA + AppContext.getAppContext().getPackageName() + "/databases/";
    public static final String PRIVATE_FILE_NAME = "APP00001.BIN";
    public static final String SHOOTING_MODE_MOVIE = "MOVIE";
    public static final String SHOOTING_MODE_STILL = "STILL";
    public static final String TAB_TABLE_NAME = "tblPlay";
}
