package com.github.up209d.cubelut;

public class LutEntry {
    public String filename;     // e.g. "cinematic_warm.cube"
    public String title;        // from TITLE metadata or filename
    public String description;  // from DESCRIPTION metadata or empty
    public String cubePath;     // full path to .cube file (or asset path)
    public String jpgPath;      // full path to .jpg preview (nullable)
    public boolean previewValid; // true if .jpg exists and is 128x128
    public boolean isBundled;   // true if from APK assets
    public boolean previewWrongSize; // true if .jpg exists but wrong dimensions

    public LutEntry() {
        filename = "";
        title = "";
        description = "";
        cubePath = "";
        jpgPath = null;
        previewValid = false;
        isBundled = false;
        previewWrongSize = false;
    }

    public String getDisplayTitle() {
        if (title != null && !title.isEmpty() && !title.equals("Untitled")) {
            return title;
        }
        // Strip .cube extension from filename
        if (filename.endsWith(".cube")) {
            return filename.substring(0, filename.length() - 5);
        }
        return filename;
    }

    public String getAbbreviation() {
        String t = getDisplayTitle();
        StringBuilder sb = new StringBuilder();
        // Get first letter of each word
        boolean newWord = true;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (c == ' ' || c == '_' || c == '-') {
                newWord = true;
            } else if (newWord) {
                sb.append(Character.toUpperCase(c));
                newWord = false;
            }
        }
        String abbr = sb.toString();
        return abbr.length() > 3 ? abbr.substring(0, 3) : abbr;
    }
}
