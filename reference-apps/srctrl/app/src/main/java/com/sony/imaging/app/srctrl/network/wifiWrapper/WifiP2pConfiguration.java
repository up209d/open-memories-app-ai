package com.sony.imaging.app.srctrl.network.wifiWrapper;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class WifiP2pConfiguration implements Parcelable {
    public static final Parcelable.Creator<WifiP2pConfiguration> CREATOR = new Parcelable.Creator<WifiP2pConfiguration>() { // from class: com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pConfiguration createFromParcel(Parcel in) {
            return new WifiP2pConfiguration(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pConfiguration[] newArray(int size) {
            return new WifiP2pConfiguration[size];
        }
    };
    private int _networkId;
    private String _preSharedKey;
    private String _ssid;

    public void setNetworkId(int networkId) {
        this._networkId = networkId;
    }

    public int getNetworkId() {
        return this._networkId;
    }

    public void setSsid(String ssid) {
        this._ssid = ssid;
    }

    public String getSsid() {
        return this._ssid;
    }

    public void setPreSharedKey(String preSharedKey) {
        this._preSharedKey = preSharedKey;
    }

    public String getPreSharedKey() {
        return this._preSharedKey;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this._networkId);
        parcel.writeString(this._ssid);
        parcel.writeString(this._preSharedKey);
    }

    public WifiP2pConfiguration() {
    }

    public WifiP2pConfiguration(Parcel parcel) {
        this._networkId = parcel.readInt();
        this._ssid = parcel.readString();
        this._preSharedKey = parcel.readString();
    }

    /* loaded from: classes.dex */
    public class ConfigurationMethod {
        public static final int DISPLAY = 2;
        public static final int KEYPAD = 3;
        public static final int PUSH_BUTTON = 1;

        public ConfigurationMethod() {
        }
    }
}
