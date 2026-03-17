package com.github.up209d.cubelut;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LutFileManager {
    private static final String TAG = "LutFileManager";
    private static final String SD_LUT_FOLDER = "luts";
    private static final String ASSET_LUT_FOLDER = "luts";

    private final Context context;
    private boolean usingBundled;
    private String sourcePath;

    public LutFileManager(Context context) {
        this.context = context;
        this.usingBundled = false;
        this.sourcePath = "";
    }

    public boolean isUsingBundled() {
        return usingBundled;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public List<LutEntry> scanLuts() {
        List<LutEntry> entries = new ArrayList<LutEntry>();

        // Try SD card first
        File sdLutDir = new File(Environment.getExternalStorageDirectory(), SD_LUT_FOLDER);
        if (sdLutDir.exists() && sdLutDir.isDirectory()) {
            File[] cubeFiles = sdLutDir.listFiles();
            if (cubeFiles != null && cubeFiles.length > 0) {
                List<File> validCubes = new ArrayList<File>();
                for (File f : cubeFiles) {
                    if (f.getName().toLowerCase().endsWith(".cube")) {
                        validCubes.add(f);
                    }
                }
                if (!validCubes.isEmpty()) {
                    Collections.sort(validCubes, new Comparator<File>() {
                        @Override
                        public int compare(File a, File b) {
                            return a.getName().compareToIgnoreCase(b.getName());
                        }
                    });
                    for (File cubeFile : validCubes) {
                        LutEntry entry = scanSdCardFile(cubeFile, sdLutDir);
                        if (entry != null) {
                            entries.add(entry);
                        }
                    }
                    if (!entries.isEmpty()) {
                        usingBundled = false;
                        sourcePath = sdLutDir.getAbsolutePath();
                        FileLogger.log("SCAN", "Found " + entries.size()
                                + " LUTs on SD card at " + sourcePath);
                        return entries;
                    }
                }
            }
        }

        // Fall back to bundled assets
        entries = scanBundledAssets();
        usingBundled = true;
        sourcePath = "assets/" + ASSET_LUT_FOLDER;
        FileLogger.log("SCAN", "Using " + entries.size() + " bundled LUTs (SD card not available)");
        return entries;
    }

    private LutEntry scanSdCardFile(File cubeFile, File lutDir) {
        InputStream is = null;
        try {
            is = new FileInputStream(cubeFile);
            CubeParser.LutMetadata meta = CubeParser.parseMetadataOnly(is);
            if (!meta.valid) {
                FileLogger.log("SCAN", "Skipping " + cubeFile.getName()
                        + " (size=" + meta.lutSize + ", need 33)");
                return null;
            }

            LutEntry entry = new LutEntry();
            entry.filename = cubeFile.getName();
            entry.title = meta.title;
            entry.description = meta.description;
            entry.cubePath = cubeFile.getAbsolutePath();
            entry.isBundled = false;

            // Check for matching .jpg preview
            String baseName = cubeFile.getName();
            baseName = baseName.substring(0, baseName.lastIndexOf('.'));
            File jpgFile = new File(lutDir, baseName + ".jpg");
            if (jpgFile.exists()) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(jpgFile.getAbsolutePath(), opts);
                if (opts.outWidth == 128 && opts.outHeight == 128) {
                    entry.jpgPath = jpgFile.getAbsolutePath();
                    entry.previewValid = true;
                } else {
                    entry.jpgPath = jpgFile.getAbsolutePath();
                    entry.previewValid = false;
                    entry.previewWrongSize = true;
                }
            }

            FileLogger.log("SCAN", "Found: " + entry.filename + " (" + entry.title + ")");
            return entry;
        } catch (IOException e) {
            FileLogger.logError("SCAN", e);
            return null;
        } finally {
            if (is != null) {
                try { is.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    private List<LutEntry> scanBundledAssets() {
        List<LutEntry> entries = new ArrayList<LutEntry>();
        AssetManager am = context.getAssets();
        try {
            String[] files = am.list(ASSET_LUT_FOLDER);
            if (files == null) return entries;

            // Collect .cube filenames
            List<String> cubeNames = new ArrayList<String>();
            for (String f : files) {
                if (f.toLowerCase().endsWith(".cube")) {
                    cubeNames.add(f);
                }
            }
            Collections.sort(cubeNames);

            // Build set of available .jpg files for quick lookup
            List<String> jpgNames = new ArrayList<String>();
            for (String f : files) {
                if (f.toLowerCase().endsWith(".jpg")) {
                    jpgNames.add(f);
                }
            }

            for (String cubeName : cubeNames) {
                InputStream is = null;
                try {
                    is = am.open(ASSET_LUT_FOLDER + "/" + cubeName);
                    CubeParser.LutMetadata meta = CubeParser.parseMetadataOnly(is);
                    if (!meta.valid) {
                        continue;
                    }

                    LutEntry entry = new LutEntry();
                    entry.filename = cubeName;
                    entry.title = meta.title;
                    entry.description = meta.description;
                    entry.cubePath = ASSET_LUT_FOLDER + "/" + cubeName;
                    entry.isBundled = true;

                    // Check for matching .jpg
                    String baseName = cubeName.substring(0, cubeName.lastIndexOf('.'));
                    String jpgName = baseName + ".jpg";
                    if (jpgNames.contains(jpgName)) {
                        InputStream jpgIs = null;
                        try {
                            jpgIs = am.open(ASSET_LUT_FOLDER + "/" + jpgName);
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inJustDecodeBounds = true;
                            BitmapFactory.decodeStream(jpgIs, null, opts);
                            if (opts.outWidth == 128 && opts.outHeight == 128) {
                                entry.jpgPath = ASSET_LUT_FOLDER + "/" + jpgName;
                                entry.previewValid = true;
                            } else {
                                entry.jpgPath = ASSET_LUT_FOLDER + "/" + jpgName;
                                entry.previewValid = false;
                                entry.previewWrongSize = true;
                            }
                        } finally {
                            if (jpgIs != null) {
                                try { jpgIs.close(); } catch (IOException e) { /* ignore */ }
                            }
                        }
                    }

                    entries.add(entry);
                } catch (IOException e) {
                    Log.w(TAG, "Error scanning asset: " + cubeName, e);
                } finally {
                    if (is != null) {
                        try { is.close(); } catch (IOException e) { /* ignore */ }
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error listing assets", e);
        }
        return entries;
    }

    public InputStream openLutStream(LutEntry entry) throws IOException {
        if (entry.isBundled) {
            return context.getAssets().open(entry.cubePath);
        } else {
            return new FileInputStream(new File(entry.cubePath));
        }
    }

    public InputStream openPreviewStream(LutEntry entry) throws IOException {
        if (entry.jpgPath == null) return null;
        if (entry.isBundled) {
            return context.getAssets().open(entry.jpgPath);
        } else {
            return new FileInputStream(new File(entry.jpgPath));
        }
    }
}
