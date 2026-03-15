package com.sony.imaging.app.snsdirect.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class DirectUploadManager {
    private static final String REFERENCE_PKG = "com.sony.imaging.app.snsdirect";
    private static final String TAG = DirectUploadManager.class.getSimpleName();
    private static final String TARGET_ACTIVITY = "com.sony.imaging.app.snsdirect.SNSDirect";
    private String[] installedPackages = null;
    private Boolean isInstalled = null;
    private int launchTargetIndex = -1;

    public boolean launch(Activity activity, MimeType type, Uri file, MovieInfo mInfo) {
        if (activity == null || type == null || file == null) {
            Log.e(TAG, "launch: Some of input argument is null");
            return false;
        }
        if (!isInstalled(activity.getApplicationContext())) {
            return false;
        }
        Intent intent = getIntentForLaunch(activity.getApplicationContext(), type, file);
        if (intent == null) {
            Log.e(TAG, "Intent is null");
            return false;
        }
        switch (type) {
            case IMAGE_JPEG:
            case IMAGE_SPHOTO:
                break;
            case VIDEO_MJPEG:
                if (mInfo == null) {
                    Log.e(TAG, "launch: MovieInfo is null");
                    return false;
                }
                intent = MovieInfo.setMovieInfoExtrasAsPrimitive(intent, mInfo);
                break;
            default:
                Log.e(TAG, "Unsupported MimeType");
                return false;
        }
        return IntentLauncher.launch(activity, intent);
    }

    public boolean launchMulti(Activity activity, MimeType type, ArrayList<Uri> fileList, MovieInfo mInfo) {
        if (activity == null || type == null || fileList == null) {
            Log.e(TAG, "launch: Some of input argument is null");
            return false;
        }
        if (!isInstalled(activity.getApplicationContext())) {
            return false;
        }
        Intent intent = getIntentForLaunchMulti(activity.getApplicationContext(), type, fileList);
        if (intent == null) {
            Log.e(TAG, "Intent is null");
            return false;
        }
        switch (type) {
            case IMAGE_JPEG:
            case IMAGE_SPHOTO:
                break;
            case VIDEO_MJPEG:
                if (mInfo == null) {
                    Log.e(TAG, "launch: MovieInfo is null");
                    return false;
                }
                intent = MovieInfo.setMovieInfoExtrasAsPrimitive(intent, mInfo);
                break;
            default:
                Log.e(TAG, "Unsupported MimeType");
                return false;
        }
        return IntentLauncher.launch(activity, intent);
    }

    public boolean isTypeSupported(Context context, MimeType type) {
        if (context == null || type == null) {
            Log.e(TAG, "isIntentSupported: Some of input argument is null");
            return false;
        }
        if (!isInstalled(context)) {
            return false;
        }
        this.launchTargetIndex = IntentLauncher.getIntentSupportedPackageIndex(context, getBasicIntent(context, type), this.installedPackages);
        if (this.launchTargetIndex == -1) {
            return false;
        }
        Log.d(TAG, "Launch Target is " + this.installedPackages[this.launchTargetIndex]);
        return true;
    }

    public boolean isInstalled(Context context) {
        if (context == null) {
            Log.e(TAG, "isInstalled: Some of input argument is null");
            return false;
        }
        if (this.isInstalled == null) {
            isDirectUploadInstalled(context);
        }
        return this.isInstalled.booleanValue();
    }

    private boolean isDirectUploadInstalled(Context context) {
        List<ApplicationInfo> aInfos = context.getPackageManager().getInstalledApplications(0);
        List<String> foundPkgs = new ArrayList<>();
        if (aInfos.size() != 0) {
            for (ApplicationInfo aInfo : aInfos) {
                String targetPkg = aInfo.packageName;
                if (targetPkg.startsWith(REFERENCE_PKG)) {
                    foundPkgs.add(targetPkg);
                }
            }
        }
        if (foundPkgs.isEmpty()) {
            setInstalled(false, null);
        } else {
            String[] foundArray = (String[]) foundPkgs.toArray(new String[0]);
            Arrays.sort(foundArray, new LengthComparator());
            Log.d(TAG, "Sorted Installed Packages");
            for (String pkg : foundArray) {
                Log.d(TAG, pkg);
            }
            setInstalled(true, foundArray);
        }
        return this.isInstalled.booleanValue();
    }

    private void setInstalled(boolean isInstalled, String[] packageName) {
        this.isInstalled = Boolean.valueOf(isInstalled);
        this.installedPackages = packageName;
    }

    private Intent getBasicIntent(Context context, MimeType type) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType(MimeType.getMimeType(type));
        return intent;
    }

    private Intent getIntentWithTargetActivity(Context context, MimeType type, int index) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType(MimeType.getMimeType(type));
        Log.d(TAG, "getIntentWithTargetActivity type:" + type + ", MimeType.getMimeType(type):" + MimeType.getMimeType(type));
        if (this.installedPackages == null && !isDirectUploadInstalled(context)) {
            Log.e(TAG, "Direct Upload is not installed");
            return null;
        }
        if (this.installedPackages.length - 1 < this.launchTargetIndex) {
            Log.e(TAG, "Index out of bound");
            return null;
        }
        Log.d(TAG, this.installedPackages[index] + " is set on the Intent");
        intent.setClassName(this.installedPackages[index], TARGET_ACTIVITY);
        return intent;
    }

    private Intent getIntentForLaunch(Context context, MimeType type, Uri file) {
        if (this.launchTargetIndex == -1) {
            Log.e(TAG, "Launch target is not set");
            return null;
        }
        Log.d(TAG, "Launch target package is " + this.installedPackages[this.launchTargetIndex]);
        Intent intent = getIntentWithTargetActivity(context, type, this.launchTargetIndex);
        if (intent == null) {
            return null;
        }
        intent.putExtra("android.intent.extra.STREAM", file);
        return intent;
    }

    private Intent getIntentForLaunchMulti(Context context, MimeType type, ArrayList<Uri> fileList) {
        if (this.launchTargetIndex == -1) {
            Log.e(TAG, "Launch target is not set");
            return null;
        }
        Log.d(TAG, "Launch target package is " + this.installedPackages[this.launchTargetIndex]);
        Log.e(TAG, "getIntentFor type:" + type);
        Intent intent = getIntentWithTargetActivity(context, type, this.launchTargetIndex);
        if (intent == null) {
            return null;
        }
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", fileList);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class LengthComparator implements Comparator<String> {
        private LengthComparator() {
        }

        @Override // java.util.Comparator
        public int compare(String lhs, String rhs) {
            return (lhs == null || rhs == null || lhs.length() >= rhs.length()) ? 1 : -1;
        }
    }
}
