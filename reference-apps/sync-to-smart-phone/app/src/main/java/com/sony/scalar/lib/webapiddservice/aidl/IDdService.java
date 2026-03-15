package com.sony.scalar.lib.webapiddservice.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sony.scalar.lib.webapiddservice.DeviceInfo;
import com.sony.scalar.lib.webapiddservice.aidl.IDdServiceCallback;

/* loaded from: classes.dex */
public interface IDdService extends IInterface {
    void initialize(DeviceInfo deviceInfo, String str, String str2, int i) throws RemoteException;

    void registerCallback(IDdServiceCallback iDdServiceCallback) throws RemoteException;

    void start() throws RemoteException;

    void stop() throws RemoteException;

    void unregisterCallback(IDdServiceCallback iDdServiceCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDdService {
        private static final String DESCRIPTOR = "com.sony.scalar.lib.webapiddservice.aidl.IDdService";
        static final int TRANSACTION_initialize = 3;
        static final int TRANSACTION_registerCallback = 1;
        static final int TRANSACTION_start = 4;
        static final int TRANSACTION_stop = 5;
        static final int TRANSACTION_unregisterCallback = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDdService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDdService)) {
                return (IDdService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            DeviceInfo _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IDdServiceCallback _arg02 = IDdServiceCallback.Stub.asInterface(data.readStrongBinder());
                    registerCallback(_arg02);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IDdServiceCallback _arg03 = IDdServiceCallback.Stub.asInterface(data.readStrongBinder());
                    unregisterCallback(_arg03);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = DeviceInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    String _arg1 = data.readString();
                    String _arg2 = data.readString();
                    int _arg3 = data.readInt();
                    initialize(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    start();
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    stop();
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IDdService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.sony.scalar.lib.webapiddservice.aidl.IDdService
            public void registerCallback(IDdServiceCallback notify) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(notify != null ? notify.asBinder() : null);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sony.scalar.lib.webapiddservice.aidl.IDdService
            public void unregisterCallback(IDdServiceCallback notify) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(notify != null ? notify.asBinder() : null);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sony.scalar.lib.webapiddservice.aidl.IDdService
            public void initialize(DeviceInfo info, String descPath, String descFile, int ssdpPort) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(descPath);
                    _data.writeString(descFile);
                    _data.writeInt(ssdpPort);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sony.scalar.lib.webapiddservice.aidl.IDdService
            public void start() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sony.scalar.lib.webapiddservice.aidl.IDdService
            public void stop() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
