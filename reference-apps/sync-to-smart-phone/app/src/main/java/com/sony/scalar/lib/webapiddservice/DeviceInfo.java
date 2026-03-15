package com.sony.scalar.lib.webapiddservice;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class DeviceInfo implements Parcelable {
    public static final Parcelable.Creator<DeviceInfo> CREATOR = new Parcelable.Creator<DeviceInfo>() { // from class: com.sony.scalar.lib.webapiddservice.DeviceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DeviceInfo createFromParcel(Parcel in) {
            return new DeviceInfo(in, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };
    String mFriendlyName;
    ArrayList<IconInfo> mIconInfoList;
    String mIpAddress;
    String mModelDescription;
    String mModelName;
    String mUdn;
    private final String mWebApiVersion;
    ArrayList<WebApiServiceInfo> mWebServiceInfoList;

    public DeviceInfo() {
        this.mFriendlyName = "";
        this.mModelDescription = "";
        this.mModelName = "";
        this.mUdn = "";
        this.mIpAddress = "";
        this.mWebApiVersion = "1.0";
        this.mIconInfoList = new ArrayList<>();
        this.mWebServiceInfoList = new ArrayList<>();
    }

    public String getFriendlyName() {
        return this.mFriendlyName;
    }

    public String getModelDescription() {
        return this.mModelDescription;
    }

    public String getModelName() {
        return this.mModelName;
    }

    public String getUdn() {
        return this.mUdn;
    }

    public String getIpAddress() {
        return this.mIpAddress;
    }

    public String getWebApiVersion() {
        return "1.0";
    }

    public ArrayList<IconInfo> getIconInfoList() {
        return this.mIconInfoList;
    }

    public ArrayList<WebApiServiceInfo> getWebServiceInfo() {
        return this.mWebServiceInfoList;
    }

    public void setFriendlyName(String friendlyName) {
        this.mFriendlyName = friendlyName;
    }

    public void setModelDescription(String modelDescription) {
        this.mModelDescription = modelDescription;
    }

    public void setModelName(String modelName) {
        this.mModelName = modelName;
    }

    public void setUdn(String udn) {
        this.mUdn = udn;
    }

    public void setIpAddress(String ipAddress) {
        this.mIpAddress = ipAddress;
    }

    public void setIconInfoList(ArrayList<IconInfo> list) {
        this.mIconInfoList = list;
    }

    public void setWebServiceInfo(ArrayList<WebApiServiceInfo> mWebServiceInfoList) {
        this.mWebServiceInfoList = mWebServiceInfoList;
    }

    public void addServiceInfo(WebApiServiceInfo mServiceInfo) {
        this.mWebServiceInfoList.add(mServiceInfo);
    }

    public WebApiServiceInfo getWebServiceInfo(String serviceType) {
        Iterator<WebApiServiceInfo> it = this.mWebServiceInfoList.iterator();
        while (it.hasNext()) {
            WebApiServiceInfo s = it.next();
            if (s.getServiceType().equals(serviceType)) {
                return s;
            }
        }
        return null;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int arg1) {
        out.writeString(this.mFriendlyName);
        out.writeString(this.mModelDescription);
        out.writeString(this.mModelName);
        out.writeString(this.mUdn);
        out.writeString(this.mIpAddress);
        out.writeInt(this.mIconInfoList.size());
        Iterator<IconInfo> it = this.mIconInfoList.iterator();
        while (it.hasNext()) {
            IconInfo icon = it.next();
            out.writeString(icon.getUrl());
            out.writeString(icon.getMimetype());
            out.writeInt(icon.getWidth().intValue());
            out.writeInt(icon.getHeight().intValue());
            out.writeInt(icon.getDepth().intValue());
        }
        out.writeInt(this.mWebServiceInfoList.size());
        Iterator<WebApiServiceInfo> it2 = this.mWebServiceInfoList.iterator();
        while (it2.hasNext()) {
            WebApiServiceInfo webService = it2.next();
            out.writeString(webService.getServiceType());
            out.writeString(webService.getActionListUrl());
            out.writeString(webService.getAccessType());
        }
    }

    public void readFromParcel(Parcel in) {
        this.mFriendlyName = in.readString();
        this.mModelDescription = in.readString();
        this.mModelName = in.readString();
        this.mUdn = in.readString();
        this.mIpAddress = in.readString();
        int iconNum = in.readInt();
        this.mIconInfoList = new ArrayList<>();
        for (int i = 0; i < iconNum; i++) {
            IconInfo icon = new IconInfo();
            icon.setUrl(in.readString());
            icon.setMimetype(in.readString());
            icon.setWidth(in.readInt());
            icon.setHeight(in.readInt());
            icon.setDepth(in.readInt());
            this.mIconInfoList.add(icon);
        }
        int webServiceNum = in.readInt();
        this.mWebServiceInfoList = new ArrayList<>();
        for (int i2 = 0; i2 < webServiceNum; i2++) {
            WebApiServiceInfo webService = new WebApiServiceInfo();
            webService.setServiceType(in.readString());
            webService.setActionListUrl(in.readString());
            webService.setAccessType(in.readString());
            this.mWebServiceInfoList.add(webService);
        }
    }

    private DeviceInfo(Parcel in) {
        this.mFriendlyName = "";
        this.mModelDescription = "";
        this.mModelName = "";
        this.mUdn = "";
        this.mIpAddress = "";
        this.mWebApiVersion = "1.0";
        this.mIconInfoList = new ArrayList<>();
        this.mWebServiceInfoList = new ArrayList<>();
        readFromParcel(in);
    }

    /* synthetic */ DeviceInfo(Parcel parcel, DeviceInfo deviceInfo) {
        this(parcel);
    }
}
