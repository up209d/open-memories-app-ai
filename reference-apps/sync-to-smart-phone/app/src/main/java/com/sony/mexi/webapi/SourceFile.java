package com.sony.mexi.webapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/* loaded from: classes.dex */
public class SourceFile {
    private URLClassLoader classLoader = null;
    private String className;
    private String sourcePath;
    private String tempClassDir;

    public SourceFile(String sourcePath) {
        this.sourcePath = sourcePath;
        this.tempClassDir = System.getProperty("java.io.tmpdir");
        if (this.tempClassDir.charAt(this.tempClassDir.length() - 1) != File.separatorChar) {
            this.tempClassDir = String.valueOf(this.tempClassDir) + File.separatorChar;
        }
        this.tempClassDir = String.valueOf(this.tempClassDir) + "MEXI_InterfaceCompiler";
    }

    public String getSourcePath() {
        return this.sourcePath;
    }

    public String getClassName() {
        if (this.className == null) {
            File file = new File(this.sourcePath);
            if (file.isFile()) {
                try {
                    FileReader in = new FileReader(file);
                    if (in != null) {
                        BufferedReader reader = new BufferedReader(in);
                        Pattern pattern = Pattern.compile("\\s*package\\s+([\\w+\\.]+)\\s*;");
                        String packageName = null;
                        while (packageName == null) {
                            String line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            Matcher matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                packageName = matcher.group(1);
                            }
                        }
                        if (packageName != null) {
                            String s = file.getName();
                            this.className = String.valueOf(packageName) + "." + s.substring(0, s.indexOf(46));
                        }
                    }
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return this.className;
    }

    public int compile() {
        List<String> args = new ArrayList<>();
        args.add(0, "-d");
        args.add(1, this.tempClassDir);
        String[] paths = this.sourcePath.split(File.pathSeparator);
        for (String path : paths) {
            findFile(args, new File(path), ".java");
        }
        new File(this.tempClassDir).mkdirs();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        return compiler.run((InputStream) null, (OutputStream) null, (OutputStream) null, (String[]) args.toArray(new String[0]));
    }

    public URLClassLoader getClassLoader() {
        if (this.classLoader == null) {
            URL[] urls = new URL[1];
            try {
                urls[0] = new File(this.tempClassDir).toURI().toURL();
            } catch (Exception e) {
                urls[0] = null;
            }
            this.classLoader = URLClassLoader.newInstance(urls);
        }
        return this.classLoader;
    }

    public Class<?> getClassObject() {
        URLClassLoader loader = getClassLoader();
        String name = getClassName();
        if (loader == null || name == null) {
            return null;
        }
        try {
            Class<?> clazz = loader.loadClass(name);
            return clazz;
        } catch (Exception e) {
            return null;
        }
    }

    private void findFile(List<String> files, File file, String suffix) {
        if (file.isDirectory()) {
            for (File tmp : file.listFiles()) {
                findFile(files, tmp, suffix);
            }
            return;
        }
        if (file.isFile() && file.toString().endsWith(suffix)) {
            files.add(file.getAbsolutePath());
        }
    }
}
