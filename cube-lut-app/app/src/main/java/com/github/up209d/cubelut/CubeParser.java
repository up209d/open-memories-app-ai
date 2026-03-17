package com.github.up209d.cubelut;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CubeParser {
    private static final String TAG = "CubeParser";
    private static final int REQUIRED_SIZE = 33;
    private static final int TOTAL_ENTRIES = REQUIRED_SIZE * REQUIRED_SIZE * REQUIRED_SIZE; // 35937
    private static final int HEADER_SCAN_LINES = 50;

    public static class LutMetadata {
        public String title;
        public String description;
        public int lutSize;
        public boolean valid;

        public LutMetadata() {
            title = "";
            description = "";
            lutSize = 0;
            valid = false;
        }
    }

    public static class LutData {
        public LutMetadata metadata;
        public float[] data; // flat array: R,G,B,R,G,B,... (size * size * size * 3)

        public LutData() {
            metadata = new LutMetadata();
            data = null;
        }
    }

    public static LutMetadata parseMetadataOnly(InputStream is) {
        LutMetadata meta = new LutMetadata();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < HEADER_SCAN_LINES) {
                lineCount++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("TITLE")) {
                    meta.title = extractQuotedString(line, "TITLE");
                } else if (line.startsWith("DESCRIPTION")) {
                    meta.description = extractQuotedString(line, "DESCRIPTION");
                } else if (line.startsWith("LUT_3D_SIZE")) {
                    try {
                        String sizeStr = line.substring("LUT_3D_SIZE".length()).trim();
                        meta.lutSize = Integer.parseInt(sizeStr);
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Invalid LUT_3D_SIZE: " + line);
                    }
                }
                // Stop scanning once we hit data lines (3 floats)
                if (isDataLine(line)) {
                    break;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading metadata", e);
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) { /* ignore */ }
            }
        }

        meta.valid = (meta.lutSize == REQUIRED_SIZE);
        if (meta.title.isEmpty()) {
            meta.title = "Untitled";
        }
        return meta;
    }

    public static LutData parseFull(InputStream is) {
        LutData lut = new LutData();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            lut.data = new float[TOTAL_ENTRIES * 3]; // 107811 floats (~431KB)
            String line;
            int dataIndex = 0;
            boolean headerDone = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (!headerDone) {
                    if (line.startsWith("TITLE")) {
                        lut.metadata.title = extractQuotedString(line, "TITLE");
                        continue;
                    } else if (line.startsWith("DESCRIPTION")) {
                        lut.metadata.description = extractQuotedString(line, "DESCRIPTION");
                        continue;
                    } else if (line.startsWith("LUT_3D_SIZE")) {
                        try {
                            String sizeStr = line.substring("LUT_3D_SIZE".length()).trim();
                            lut.metadata.lutSize = Integer.parseInt(sizeStr);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Invalid LUT_3D_SIZE", e);
                            return lut;
                        }
                        if (lut.metadata.lutSize != REQUIRED_SIZE) {
                            Log.w(TAG, "Unsupported LUT size: " + lut.metadata.lutSize);
                            return lut;
                        }
                        continue;
                    } else if (line.startsWith("DOMAIN_MIN") || line.startsWith("DOMAIN_MAX")) {
                        continue;
                    } else if (isDataLine(line)) {
                        headerDone = true;
                        // fall through to parse this data line
                    } else {
                        continue; // skip unknown header keywords
                    }
                }

                // Parse data line: "R G B" (space-separated floats)
                if (dataIndex >= TOTAL_ENTRIES * 3) {
                    break; // enough data
                }
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    try {
                        lut.data[dataIndex]     = Float.parseFloat(parts[0]);
                        lut.data[dataIndex + 1] = Float.parseFloat(parts[1]);
                        lut.data[dataIndex + 2] = Float.parseFloat(parts[2]);
                        dataIndex += 3;
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Bad data line: " + line);
                    }
                }
            }

            if (dataIndex == TOTAL_ENTRIES * 3) {
                lut.metadata.valid = true;
            } else {
                Log.w(TAG, "Incomplete LUT data: got " + (dataIndex / 3) + " entries, expected " + TOTAL_ENTRIES);
                lut.data = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error parsing LUT", e);
            lut.data = null;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory parsing LUT", e);
            lut.data = null;
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) { /* ignore */ }
            }
        }

        if (lut.metadata.title.isEmpty()) {
            lut.metadata.title = "Untitled";
        }
        return lut;
    }

    private static String extractQuotedString(String line, String keyword) {
        String value = line.substring(keyword.length()).trim();
        // Remove surrounding quotes if present
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static boolean isDataLine(String line) {
        if (line.isEmpty()) return false;
        char c = line.charAt(0);
        return (c >= '0' && c <= '9') || c == '-' || c == '.';
    }
}
