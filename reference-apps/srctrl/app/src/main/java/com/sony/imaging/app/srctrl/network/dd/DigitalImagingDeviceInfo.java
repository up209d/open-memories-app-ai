package com.sony.imaging.app.srctrl.network.dd;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.UtilPFWorkaround;
import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.imaging.app.srctrl.webapi.util.AttachedLensInfo;
import com.sony.imaging.app.util.PfBugAvailability;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class DigitalImagingDeviceInfo {
    private static final String ATTR_SENDEVENTS = "sendEvents";
    private static final String ATTR_XMLNS = "xmlns";
    private static final String CATEGORY_CAM = "CAM";
    private static final String CATEGORY_DSC = "DSC";
    private static final String CATEGORY_ILCA = "ILCA";
    private static final String CATEGORY_ILCE = "ILCE";
    private static final String CATEGORY_LSC = "LSC";
    private static final String FILENAME = "DigitalImagingDesc.xml";
    private static final String TAG = "DigitalImagingDeviceInfo";
    private static final String TAG_APPNAME = "X_PlayMemoriesCameraApps_AppName";
    private static final String TAG_APPVERSION = "X_PlayMemoriesCameraApps_AppVersion";
    private static final String TAG_DATATYPE = "dataType";
    private static final String TAG_DEVICEINFO = "X_DeviceInfo";
    private static final String TAG_DIGITALIMAGINGDEVICEINFO = "X_DigitalImagingDeviceInfo";
    private static final String TAG_MAJOR = "major";
    private static final String TAG_MINOR = "minor";
    private static final String TAG_NAME = "name";
    private static final String TAG_SCPD = "scpd";
    private static final String TAG_SERVICESTATETABLE = "serviceStateTable";
    private static final String TAG_SPECVERSION = "specVersion";
    private static final String TAG_STATEVARIABLE = "stateVariable";
    private static final String VALUE_ATTR_SENDEVENTS = "no";
    private static final String VALUE_ATTR_XMLNS = "urn:schemas-upnp-org:service-1-0";
    private static final String VALUE_CHARSET = "UTF-8";
    private static final String VALUE_DATATYPE = "string";
    private static final String VALUE_MAJOR = "1";
    private static final String VALUE_MINOR = "0";
    private static final String VALUE_NAME = "X_DigitalImagingDeviceInfo";
    private static Context mContext;
    private static final List<String> packageBlackList = Arrays.asList("com.sony.scalar.dlsys.scalaramarket", "com.sony.scalar.dlsys.scalaraappmanager", "com.sony.scalar.dlsys.scalaradisclaimer");

    /* JADX INFO: Access modifiers changed from: private */
    public static void addElement(XmlSerializer ref, String tagName, String text) throws IllegalArgumentException, IllegalStateException, IOException {
        ref.startTag("", tagName);
        ref.text(text);
        ref.endTag("", tagName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addMethodElement(XmlSerializer ref, String apiName, String apiVersion) throws IllegalArgumentException, IllegalStateException, IOException {
        ref.startTag("", "X_ScalarWebAPI_Method");
        addElement(ref, "X_ScalarWebAPI_APIName", apiName);
        addElement(ref, "X_ScalarWebAPI_APIVersion", apiVersion);
        ref.endTag("", "X_ScalarWebAPI_Method");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum e_TagsScalarWebAPICurrentInformation {
        CAMERASERVICE("X_ScalarWebAPI_CameraService") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsScalarWebAPICurrentInformation.1
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsScalarWebAPICurrentInformation
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                ref.startTag("", this.tag);
                ref.startTag("", "X_ScalarWebAPI_GetVersions");
                DigitalImagingDeviceInfo.addElement(ref, "X_ScalarWebAPI_Version", "1.0,1.1,1.2,1.3,1.4");
                ref.endTag("", "X_ScalarWebAPI_GetVersions");
                ref.startTag("", "X_ScalarWebAPI_GetApplicationInfo");
                DigitalImagingDeviceInfo.addElement(ref, "X_ScalarWebAPI_ApplicationName", SRCtrlEnvironment.getInstance().getServerName());
                DigitalImagingDeviceInfo.addElement(ref, "X_ScalarWebAPI_ApplicationVersion", SRCtrlEnvironment.getInstance().getServerVersion());
                ref.endTag("", "X_ScalarWebAPI_GetApplicationInfo");
                ref.startTag("", "X_ScalarWebAPI_GetMethodTypes");
                DigitalImagingDeviceInfo.addMethodElement(ref, Name.GET_EVENT, "1.1,1.2,1.3,1.4");
                ref.endTag("", "X_ScalarWebAPI_GetMethodTypes");
                ref.endTag("", this.tag);
            }
        },
        AVCONTENTSERVICE("X_ScalarWebAPI_AvContentService") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsScalarWebAPICurrentInformation.2
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsScalarWebAPICurrentInformation
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                if (!PfBugAvailability.encodeAtPlay) {
                    ref.startTag("", this.tag);
                    ref.startTag("", "X_ScalarWebAPI_GetVersions");
                    DigitalImagingDeviceInfo.addElement(ref, "X_ScalarWebAPI_Version", "1.0,1.1,1.2,1.3");
                    ref.endTag("", "X_ScalarWebAPI_GetVersions");
                    ref.startTag("", "X_ScalarWebAPI_GetMethodTypes");
                    DigitalImagingDeviceInfo.addMethodElement(ref, Name.GET_SCHEME_LIST, "1.0");
                    DigitalImagingDeviceInfo.addMethodElement(ref, Name.GET_SOURCE_LIST, "1.0");
                    DigitalImagingDeviceInfo.addMethodElement(ref, Name.GET_CONTENT_COUNT, "1.2");
                    DigitalImagingDeviceInfo.addMethodElement(ref, Name.GET_CONTENT_LIST, "1.3");
                    DigitalImagingDeviceInfo.addMethodElement(ref, Name.DELETE_CONTENT, "1.1");
                    if (SRCtrlEnvironment.getInstance().isStreamingPlaybackAPISupported()) {
                        DigitalImagingDeviceInfo.addMethodElement(ref, Name.SET_STREAMING_CONTENT, "1.0");
                        DigitalImagingDeviceInfo.addMethodElement(ref, Name.START_STREAMING, "1.0");
                        DigitalImagingDeviceInfo.addMethodElement(ref, Name.PAUSE_STREAMING, "1.0");
                        DigitalImagingDeviceInfo.addMethodElement(ref, Name.SEEK_STREAMING_POSITION, "1.0");
                        DigitalImagingDeviceInfo.addMethodElement(ref, Name.STOP_STREAMING, "1.0");
                        DigitalImagingDeviceInfo.addMethodElement(ref, Name.REQUEST_TO_NOTIFY_STREAMING_STATUS, "1.0");
                    }
                    ref.endTag("", "X_ScalarWebAPI_GetMethodTypes");
                    ref.endTag("", this.tag);
                }
            }
        };

        public final String tag;

        e_TagsScalarWebAPICurrentInformation(String tag) {
            this.tag = tag;
        }

        public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum e_TagsDeviceInfo {
        MODELNAME("X_ModelName") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo.1
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                ref.startTag("", this.tag);
                ref.text(ScalarProperties.getString(UtilPFWorkaround.PROP_MODEL_NAME));
                ref.endTag("", this.tag);
            }
        },
        FIRMWAREVERSION("X_FirmwareVersion") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo.2
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                ref.startTag("", this.tag);
                ref.text(ScalarProperties.getFirmwareVersion());
                ref.endTag("", this.tag);
            }
        },
        PLAYMEMORIESCAMERAAPPS("X_InstalledPlayMemoriesCameraApps") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo.3
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                ref.startTag("", this.tag);
                e_TagsPlayMemoriesCameraApps[] arr$ = e_TagsPlayMemoriesCameraApps.values();
                for (e_TagsPlayMemoriesCameraApps tag : arr$) {
                    tag.build(ref);
                }
                ref.endTag("", this.tag);
            }
        },
        PLATFORMVERSION("X_PlayMemoriesCameraApps_PlatformVersion") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo.4
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                ref.startTag("", this.tag);
                ref.text(ScalarProperties.getString("version.platform"));
                ref.endTag("", this.tag);
            }
        },
        LENSMODELNUMBER("X_LensModelNumber") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo.5
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                String lensID = AttachedLensInfo.getLensID();
                if (lensID != null && lensID.length() != 0) {
                    ref.startTag("", this.tag);
                    ref.text(SecurityUtils.escapeXMLText(lensID));
                    ref.endTag("", this.tag);
                }
            }
        },
        LENSMODELNAME("X_LensModelName") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo.6
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                String lensName = AttachedLensInfo.getLensName();
                if (lensName != null && lensName.length() != 0) {
                    ref.startTag("", this.tag);
                    ref.text(SecurityUtils.escapeXMLText(lensName));
                    ref.endTag("", this.tag);
                }
            }
        },
        LENSFIRMWAREVERSION("X_LensFirmwareVersion") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo.7
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                String lensVersion = AttachedLensInfo.getLensVersion();
                if (lensVersion != null && lensVersion.length() != 0) {
                    ref.startTag("", this.tag);
                    ref.text(SecurityUtils.escapeXMLText(lensVersion));
                    ref.endTag("", this.tag);
                }
            }
        },
        CATEGORY("X_Category") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo.8
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsDeviceInfo
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                int categoryNum = ScalarProperties.getInt("model.category");
                String category = null;
                switch (categoryNum) {
                    case 0:
                        category = DigitalImagingDeviceInfo.CATEGORY_ILCA;
                        break;
                    case 1:
                        category = DigitalImagingDeviceInfo.CATEGORY_ILCE;
                        break;
                    case 2:
                        category = DigitalImagingDeviceInfo.CATEGORY_DSC;
                        break;
                    case 3:
                        category = DigitalImagingDeviceInfo.CATEGORY_CAM;
                        break;
                    case 4:
                        category = DigitalImagingDeviceInfo.CATEGORY_LSC;
                        break;
                }
                if (category != null) {
                    ref.startTag("", this.tag);
                    ref.text(category);
                    ref.endTag("", this.tag);
                }
            }
        };

        public final String tag;

        e_TagsDeviceInfo(String tag) {
            this.tag = tag;
        }

        public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
        }
    }

    /* loaded from: classes.dex */
    private enum e_TagsPlayMemoriesCameraApps {
        APP("X_PlayMemoriesCameraApps_App") { // from class: com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsPlayMemoriesCameraApps.1
            @Override // com.sony.imaging.app.srctrl.network.dd.DigitalImagingDeviceInfo.e_TagsPlayMemoriesCameraApps
            public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
                PackageManager pm;
                if (DigitalImagingDeviceInfo.mContext != null && (pm = DigitalImagingDeviceInfo.mContext.getPackageManager()) != null) {
                    Locale defaultlocale = Locale.getDefault();
                    List<ApplicationInfo> applicationInfoList = new ArrayList<>();
                    List<String> packageList = new ArrayList<>();
                    Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
                    intent.addCategory("android.intent.category.LAUNCHER");
                    List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
                    for (ResolveInfo resolveInfo : resolveInfoList) {
                        applicationInfoList.add(resolveInfo.activityInfo.applicationInfo);
                        packageList.add(resolveInfo.activityInfo.applicationInfo.packageName);
                    }
                    List<ApplicationInfo> thirdPartyAppInfoList = pm.getInstalledApplications(8704);
                    for (ApplicationInfo thirdPartyAppInfo : thirdPartyAppInfoList) {
                        if ((thirdPartyAppInfo.flags & 1) != 1 || (thirdPartyAppInfo.flags & LiveviewCommon.PAYLOAD_HEADER_SIZE) == 128) {
                            if (!packageList.contains(thirdPartyAppInfo.packageName)) {
                                applicationInfoList.add(thirdPartyAppInfo);
                            }
                        }
                    }
                    int count = 1;
                    DecimalFormat countFormat = new DecimalFormat("#00");
                    for (ApplicationInfo applicationInfo : applicationInfoList) {
                        if (!DigitalImagingDeviceInfo.packageBlackList.contains(applicationInfo.packageName)) {
                            Resources resource = null;
                            try {
                                resource = pm.getResourcesForApplication(applicationInfo);
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.e(DigitalImagingDeviceInfo.TAG, "NameNotFoundException");
                            }
                            CharSequence charName = null;
                            if (resource != null) {
                                e_TagsPlayMemoriesCameraApps.setLocale(resource, Locale.ENGLISH);
                                charName = pm.getApplicationLabel(applicationInfo);
                                e_TagsPlayMemoriesCameraApps.setLocale(resource, defaultlocale);
                            }
                            if (charName != null) {
                                String name = charName.toString().replaceAll("\n", ExposureModeController.SOFT_SNAP);
                                String version = null;
                                try {
                                    version = pm.getPackageInfo(applicationInfo.packageName, 1).versionName;
                                } catch (PackageManager.NameNotFoundException e2) {
                                    Log.e(DigitalImagingDeviceInfo.TAG, "NameNotFoundException");
                                }
                                ref.startTag("", this.tag);
                                ref.attribute("", "num", countFormat.format(count));
                                ref.startTag("", DigitalImagingDeviceInfo.TAG_APPNAME);
                                ref.text(name);
                                ref.endTag("", DigitalImagingDeviceInfo.TAG_APPNAME);
                                ref.startTag("", DigitalImagingDeviceInfo.TAG_APPVERSION);
                                ref.text(version);
                                ref.endTag("", DigitalImagingDeviceInfo.TAG_APPVERSION);
                                ref.endTag("", this.tag);
                                count++;
                            }
                        }
                    }
                }
            }
        };

        public final String tag;

        e_TagsPlayMemoriesCameraApps(String tag) {
            this.tag = tag;
        }

        public void build(XmlSerializer ref) throws IllegalArgumentException, IllegalStateException, IOException {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void setLocale(Resources resources, Locale locale) {
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            resources.updateConfiguration(config, null);
        }
    }

    public DigitalImagingDeviceInfo(Context context) {
        mContext = context;
    }

    public boolean createDigitalImagingDeviceInfoFile(String dirPath) {
        boolean ret = false;
        if (mContext != null) {
            try {
                String FILEPATH = dirPath + "/" + FILENAME;
                FileOutputStream out = new FileOutputStream(FILEPATH, false);
                ret = createDigitalImagingDeviceInfo(out);
                if (true == ret) {
                    try {
                        out.flush();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException");
                    }
                }
                out.close();
            } catch (FileNotFoundException e2) {
                Log.e(TAG, "FileNotFoundException");
            }
        }
        return ret;
    }

    private boolean createDigitalImagingDeviceInfo(FileOutputStream out) {
        boolean ret;
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(out, "UTF-8");
            serializer.startDocument("UTF-8", null);
            serializer.startTag("", TAG_SCPD);
            serializer.attribute("", ATTR_XMLNS, VALUE_ATTR_XMLNS);
            serializer.startTag("", TAG_SPECVERSION);
            serializer.startTag("", TAG_MAJOR);
            serializer.text(VALUE_MAJOR);
            serializer.endTag("", TAG_MAJOR);
            serializer.startTag("", TAG_MINOR);
            serializer.text("0");
            serializer.endTag("", TAG_MINOR);
            serializer.endTag("", TAG_SPECVERSION);
            serializer.startTag("", TAG_SERVICESTATETABLE);
            serializer.startTag("", TAG_STATEVARIABLE);
            serializer.attribute("", ATTR_SENDEVENTS, VALUE_ATTR_SENDEVENTS);
            serializer.startTag("", TAG_NAME);
            serializer.text("X_DigitalImagingDeviceInfo");
            serializer.endTag("", TAG_NAME);
            serializer.startTag("", TAG_DATATYPE);
            serializer.text("string");
            serializer.endTag("", TAG_DATATYPE);
            serializer.endTag("", TAG_STATEVARIABLE);
            serializer.endTag("", TAG_SERVICESTATETABLE);
            serializer.startTag("", "X_DigitalImagingDeviceInfo");
            serializer.startTag("", "X_ScalarWebAPI_CurrentInformation");
            e_TagsScalarWebAPICurrentInformation[] arr$ = e_TagsScalarWebAPICurrentInformation.values();
            for (e_TagsScalarWebAPICurrentInformation tag : arr$) {
                tag.build(serializer);
            }
            serializer.endTag("", "X_ScalarWebAPI_CurrentInformation");
            serializer.startTag("", TAG_DEVICEINFO);
            e_TagsDeviceInfo[] arr$2 = e_TagsDeviceInfo.values();
            for (e_TagsDeviceInfo tag2 : arr$2) {
                tag2.build(serializer);
            }
            serializer.endTag("", TAG_DEVICEINFO);
            serializer.endTag("", "X_DigitalImagingDeviceInfo");
            serializer.endTag("", TAG_SCPD);
            serializer.endDocument();
            ret = true;
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            ret = false;
        } catch (IllegalArgumentException e2) {
            Log.e(TAG, "IllegalArgumentException");
            ret = false;
        } catch (IllegalStateException e3) {
            Log.e(TAG, "IllegalStateException");
            ret = false;
        }
        mContext = null;
        return ret;
    }
}
