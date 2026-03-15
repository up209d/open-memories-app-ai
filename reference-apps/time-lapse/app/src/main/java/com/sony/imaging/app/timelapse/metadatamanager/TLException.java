package com.sony.imaging.app.timelapse.metadatamanager;

/* loaded from: classes.dex */
public class TLException extends Exception {
    public static final String TL_FILEPATH_EXCEPTION = "File is invalid";
    public static final String TL_FILE_ALREADY_EXIST_EXCEPTION = "File is invalid";
    public static final String TL_FILE_CANNOT_BE_DELETED_EXCEPTION = "File cannot be deleted";
    public static final String TL_FILE_CANNOT_BE_RENAMED_EXCEPTION = "File cannot be renamed";
    public static final String TL_NUMBER_FORMAT_EXCEPTION = "File is invalid";
    public static final String TL_ROW_CANNOT_BE_DELETED_EXCEPTION = "Row cannot be deleted";
    private static final long serialVersionUID = 1;

    public TLException(String message) {
        super(message);
    }

    public TLException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
