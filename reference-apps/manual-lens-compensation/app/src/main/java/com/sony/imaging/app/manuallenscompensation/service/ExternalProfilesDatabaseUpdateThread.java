package com.sony.imaging.app.manuallenscompensation.service;

import android.util.Log;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.FileInfoDetails;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* loaded from: classes.dex */
public class ExternalProfilesDatabaseUpdateThread implements Runnable {
    private static String TAG = "ExternalProfilesDatabaseUpdateThread";
    private static FilenameFilter sFileNameFilter = new FilenameFilter() { // from class: com.sony.imaging.app.manuallenscompensation.service.ExternalProfilesDatabaseUpdateThread.1
        @Override // java.io.FilenameFilter
        public boolean accept(File dir, String name) {
            try {
                boolean accept = OCUtil.getInstance().isValidName(name);
                return accept;
            } catch (Exception e) {
                return false;
            }
        }
    };
    String[] shorted_filename_list = null;

    private void syncExternalMemoryWithDB() {
        Log.d("syncExternalMemoryWithDB", "START");
        String filePath = OCUtil.getInstance().getFilePathOnMedia();
        if (filePath != null) {
            File externalProfileDir = new File(filePath);
            this.shorted_filename_list = externalProfileDir.list(sFileNameFilter);
            if (this.shorted_filename_list != null) {
                int k = this.shorted_filename_list.length <= 200 ? this.shorted_filename_list.length : 200;
                Log.d("syncExternalMemoryWithDB", "START check point FileArray started");
                getSortedFileArray(filePath, k);
                Log.d("syncExternalMemoryWithDB", "START check point ANALYZERArray end");
                createByteAnalyzerForFileArray();
            }
            this.shorted_filename_list = null;
            Log.d("syncExternalMemoryWithDB", "END");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0054  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0058 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void createByteAnalyzerForFileArray() {
        /*
            r11 = this;
            java.lang.String r7 = com.sony.imaging.app.manuallenscompensation.service.ExternalProfilesDatabaseUpdateThread.TAG
            java.lang.String r8 = com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog.getMethodName()
            com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog.enter(r7, r8)
            com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil r7 = com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil.getInstance()
            java.util.ArrayList r7 = r7.getFileArray()
            int r7 = r7.size()
            int r6 = r7 + (-1)
        L17:
            if (r6 < 0) goto L9e
            com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil r7 = com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil.getInstance()
            java.util.ArrayList r7 = r7.getFileArray()
            java.lang.Object r7 = r7.get(r6)
            com.sony.imaging.app.manuallenscompensation.commonUtil.FileInfoDetails r7 = (com.sony.imaging.app.manuallenscompensation.commonUtil.FileInfoDetails) r7
            java.io.File r3 = r7.getFile()
            r7 = 496(0x1f0, float:6.95E-43)
            byte[] r1 = new byte[r7]
            com.sony.imaging.app.manuallenscompensation.commonUtil.ByteDataAnalyser r0 = new com.sony.imaging.app.manuallenscompensation.commonUtil.ByteDataAnalyser
            r0.<init>(r1)
            r4 = 0
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch: java.io.FileNotFoundException -> L71 java.io.IOException -> L81 java.lang.Throwable -> L91
            r5.<init>(r3)     // Catch: java.io.FileNotFoundException -> L71 java.io.IOException -> L81 java.lang.Throwable -> L91
            r5.read(r1)     // Catch: java.lang.Throwable -> La8 java.io.IOException -> Lab java.io.FileNotFoundException -> Lae
            r5.close()     // Catch: java.lang.Throwable -> La8 java.io.IOException -> Lab java.io.FileNotFoundException -> Lae
            r4 = 0
            if (r4 == 0) goto L47
            r4.close()     // Catch: java.io.IOException -> L6c
        L46:
            r4 = 0
        L47:
            r0.setDataBytes(r1)
            long r7 = r3.length()
            r9 = 496(0x1f0, double:2.45E-321)
            int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r7 == 0) goto L58
            r7 = 0
            r0.setProfileValidation(r7)
        L58:
            com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil r7 = com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil.getInstance()
            java.util.ArrayList r7 = r7.getFileArray()
            java.lang.Object r7 = r7.get(r6)
            com.sony.imaging.app.manuallenscompensation.commonUtil.FileInfoDetails r7 = (com.sony.imaging.app.manuallenscompensation.commonUtil.FileInfoDetails) r7
            r7.setByteDataAnalyser(r0)
            int r6 = r6 + (-1)
            goto L17
        L6c:
            r2 = move-exception
            r2.printStackTrace()
            goto L46
        L71:
            r2 = move-exception
        L72:
            r2.printStackTrace()     // Catch: java.lang.Throwable -> L91
            if (r4 == 0) goto L47
            r4.close()     // Catch: java.io.IOException -> L7c
        L7a:
            r4 = 0
            goto L47
        L7c:
            r2 = move-exception
            r2.printStackTrace()
            goto L7a
        L81:
            r2 = move-exception
        L82:
            r2.printStackTrace()     // Catch: java.lang.Throwable -> L91
            if (r4 == 0) goto L47
            r4.close()     // Catch: java.io.IOException -> L8c
        L8a:
            r4 = 0
            goto L47
        L8c:
            r2 = move-exception
            r2.printStackTrace()
            goto L8a
        L91:
            r7 = move-exception
        L92:
            if (r4 == 0) goto L98
            r4.close()     // Catch: java.io.IOException -> L99
        L97:
            r4 = 0
        L98:
            throw r7
        L99:
            r2 = move-exception
            r2.printStackTrace()
            goto L97
        L9e:
            java.lang.String r7 = com.sony.imaging.app.manuallenscompensation.service.ExternalProfilesDatabaseUpdateThread.TAG
            java.lang.String r8 = com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog.getMethodName()
            com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog.exit(r7, r8)
            return
        La8:
            r7 = move-exception
            r4 = r5
            goto L92
        Lab:
            r2 = move-exception
            r4 = r5
            goto L82
        Lae:
            r2 = move-exception
            r4 = r5
            goto L72
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.manuallenscompensation.service.ExternalProfilesDatabaseUpdateThread.createByteAnalyzerForFileArray():void");
    }

    private void getSortedFileArray(String filePath, int length) {
        AppLog.enter(TAG, AppLog.getMethodName());
        ArrayList<FileInfoDetails> arrayList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            File f = new File(filePath + File.separator + this.shorted_filename_list[i]);
            FileInfoDetails fileInfoDetails = new FileInfoDetails(f, null);
            arrayList.add(fileInfoDetails);
        }
        Collections.sort(arrayList, new Comparator<FileInfoDetails>() { // from class: com.sony.imaging.app.manuallenscompensation.service.ExternalProfilesDatabaseUpdateThread.2
            @Override // java.util.Comparator
            public int compare(FileInfoDetails details1, FileInfoDetails details2) {
                return Long.valueOf(details1.getFile().lastModified()).compareTo(Long.valueOf(details2.getFile().lastModified()));
            }
        });
        OCUtil.getInstance().setFileArray(arrayList);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // java.lang.Runnable
    public void run() {
        AppLog.enter(TAG, AppLog.getMethodName());
        try {
            syncExternalMemoryWithDB();
        } catch (Exception e) {
            AppLog.info(TAG, e.getMessage());
            Log.d("syncExternalMemoryWithDB", "END Exception");
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
