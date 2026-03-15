package com.sony.imaging.app.synctosmartphone.network.wifiwrapper;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.BitSet;

/* loaded from: classes.dex */
public class WifiP2pDeviceInfo implements Parcelable {
    public static final Parcelable.Creator<WifiP2pDeviceInfo> CREATOR = new Parcelable.Creator<WifiP2pDeviceInfo>() { // from class: com.sony.imaging.app.synctosmartphone.network.wifiwrapper.WifiP2pDeviceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pDeviceInfo createFromParcel(Parcel in) {
            return new WifiP2pDeviceInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pDeviceInfo[] newArray(int size) {
            return new WifiP2pDeviceInfo[size];
        }
    };
    private BitSet _configMethod;
    private boolean _isOperatingModeGO;
    private String _name;
    private String _p2PDevAddress;

    public void setName(String name) {
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public boolean isOperatingModeGO() {
        return this._isOperatingModeGO;
    }

    public void setIsOperatingModeGO(boolean enable) {
        this._isOperatingModeGO = enable;
    }

    public String getP2PDevAddress() {
        return this._p2PDevAddress;
    }

    public void setP2PDevAddress(String p2PDevAddress) {
        this._p2PDevAddress = p2PDevAddress;
    }

    public BitSet getConfigMethod() {
        return this._configMethod;
    }

    public void setConfigMethod(BitSet configMethod) {
        this._configMethod = configMethod;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeByte((byte) (this._isOperatingModeGO ? 1 : 0));
        parcel.writeString(this._name);
        parcel.writeString(this._p2PDevAddress);
        parcel.writeSerializable(this._configMethod);
    }

    public WifiP2pDeviceInfo() {
    }

    public WifiP2pDeviceInfo(Parcel parcel) {
        this._isOperatingModeGO = parcel.readByte() == 1;
        this._name = parcel.readString();
        this._p2PDevAddress = parcel.readString();
        this._configMethod = (BitSet) parcel.readSerializable();
    }
}
