package com.sony.scalar.lib.ddserver;

import android.os.Environment;
import com.sony.scalar.lib.webapiddservice.IconInfo;
import com.sony.scalar.lib.webapiddservice.WebApiServiceInfo;

/* loaded from: classes.dex */
public class ServerConf {
    public IconInfo[] iconInfo;
    public WebApiServiceInfo[] serviceInfo;
    public String uniqueServiceId;
    public boolean debugOnPhone = false;
    public String ddServiceIntent = "com.sony.scalar.lib.webapiddservice.aidl.IDdService";
    public String ddServicePackage = "com.sony.scalar.lib.webapiddservice";
    public String descriptionPath = "/data/data/" + this.ddServicePackage;
    public String descriptionPathPhone = Environment.getExternalStorageDirectory() + "/DdService";
    public String descriptionFile = "/scalarwebapi_dd.xml";
    public int ssdpPort = 61000;
    public String modelDescription = "SonyDigitalMediaServer";
    public String modelName = "SonyImagingDevice";
}
