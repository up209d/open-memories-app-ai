package com.sony.imaging.app.util;

import android.content.Context;
import android.util.Pair;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.File;
import java.io.FilenameFilter;

/* loaded from: classes.dex */
public class MemoryMapFile {
    private static final String PREFIXOWN = "/android";
    private static final String PREFIXPF = "/android/system";
    private String mPath;

    public MemoryMapFile(Context context, String appName, String index) {
        File file;
        String prefix;
        this.mPath = null;
        String megaPixel = ScalarProperties.getString("mem.rawimage.size.in.mega.pixel");
        String deviceMemory = ScalarProperties.getString("device.memory");
        String filename = "lib" + deviceMemory + megaPixel + "m_" + appName + "_" + index + "_";
        Pair<Integer, File> pf = getLatestVersion("/etc/memmap/", filename);
        Pair<Integer, File> own = getLatestVersion("/data/data/" + context.getPackageName() + "/lib/", filename);
        if (pf == null) {
            file = (File) own.second;
            prefix = PREFIXOWN;
        } else if (own == null) {
            file = (File) pf.second;
            prefix = PREFIXPF;
        } else if (((Integer) pf.first).intValue() < ((Integer) own.first).intValue()) {
            file = (File) own.second;
            prefix = PREFIXOWN;
        } else {
            file = (File) pf.second;
            prefix = PREFIXPF;
        }
        this.mPath = prefix + file.getPath();
    }

    private Pair<Integer, File> getLatestVersion(String path, String prefix) {
        File file = new File(path);
        Filter filter = new Filter(prefix);
        File[] files = file.listFiles(filter);
        int count = files == null ? 0 : files.length;
        int maxVersion = -1;
        File target = null;
        for (int i = 0; i < count; i++) {
            int version = Integer.parseInt(files[i].getName().substring(prefix.length(), prefix.length() + 2));
            if (maxVersion < version) {
                maxVersion = version;
                target = files[i];
            }
        }
        if (-1 == maxVersion) {
            return null;
        }
        Pair<Integer, File> ret = new Pair<>(Integer.valueOf(maxVersion), target);
        return ret;
    }

    public String getPath() {
        return this.mPath;
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
}
