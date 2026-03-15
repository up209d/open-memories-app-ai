package com.sony.imaging.app.manuallenscompensation.commonUtil;

import java.io.File;

/* loaded from: classes.dex */
public class FileInfoDetails {
    private ByteDataAnalyser byteDataAnalyser;
    private File file;

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ByteDataAnalyser getByteDataAnalyser() {
        return this.byteDataAnalyser;
    }

    public void setByteDataAnalyser(ByteDataAnalyser byteDataAnalyser) {
        this.byteDataAnalyser = byteDataAnalyser;
    }

    public FileInfoDetails(File file, ByteDataAnalyser byteDataAnalyser) {
        this.file = null;
        this.byteDataAnalyser = null;
        this.file = file;
        this.byteDataAnalyser = byteDataAnalyser;
    }
}
