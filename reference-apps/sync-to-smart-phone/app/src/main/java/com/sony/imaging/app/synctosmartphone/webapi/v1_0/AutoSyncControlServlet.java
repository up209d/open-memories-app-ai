package com.sony.imaging.app.synctosmartphone.webapi.v1_0;

import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import com.sony.imaging.app.synctosmartphone.webapi.definition.StatusCode;
import com.sony.imaging.app.synctosmartphone.webapi.util.ApiCallLog;
import com.sony.mexi.orb.servlet.autosynccontrol.v1_0.AutoSyncControlServletBase;
import com.sony.scalar.webapi.service.contentsync.v1_0.ActPairingCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.GetInterfaceInformationCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.NotifySyncStatusCallback;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.InterfaceInformation;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.Pairing;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.SyncStatus;
import com.sony.scalar.webapi.service.contentsync.v1_0.common.struct.SyncStatusSource;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class AutoSyncControlServlet extends AutoSyncControlServletBase {
    private static final String TAG = AutoSyncControlServlet.class.getSimpleName();
    private static int cntDownloaded = 0;
    private static int numberOfReservationFiles = 0;
    private static final long serialVersionUID = 1;

    @Override // com.sony.scalar.webapi.service.contentsync.v1_0.ActPairing
    public int actPairing(Pairing pair, ActPairingCallback returnCb) {
        ApiCallLog apiCallLog = null;
        int errorCode = StatusCode.OK.toInt();
        BaseApp baseApp = (BaseApp) NetworkStateUtil.getInstance().getState().getActivity();
        boolean bBootState = baseApp.getStartApp().equals(ConstantsSync.BOOT_STATE);
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.v(TAG, "*** actPairing ***");
                    Log.v(TAG, "pair.uuid         = " + pair.uuid);
                    Log.v(TAG, "pair.friendlyName = " + pair.friendlyName);
                    Log.d(TAG, "bootState         = " + baseApp.getStartApp());
                    Pattern p = Pattern.compile(ConstantsSync.UUID_V4_PATTERN, 2);
                    Matcher m = p.matcher(pair.uuid);
                    if (!bBootState) {
                        Log.e(TAG, "Server device is not under pairing mode.");
                        errorCode = StatusCode.NOT_UNDER_PAIRING_MODE.toInt();
                        returnCb.handleStatus(errorCode, "Server device is not under pairing mode.");
                    } else if (m.find()) {
                        SyncBackUpUtil.getInstance().setRegister(true);
                        SyncBackUpUtil.getInstance().setSmartphone(pair.friendlyName);
                        SyncBackUpUtil.getInstance().setSmartphoneInfo(pair.uuid);
                        returnCb.returnCb();
                    } else {
                        Log.e(TAG, "actPairing's uuid is not match for v4.");
                        errorCode = StatusCode.ILLEGAL_ARGUMENT.toInt();
                        returnCb.handleStatus(errorCode, "Unsupported Version");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                    NetworkStateUtil.getInstance().setActPairingResult(errorCode);
                    return 0;
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    int errorCode2 = StatusCode.ANY.toInt();
                    returnCb.handleStatus(errorCode2, "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setActPairingResult(errorCode2);
                    return 0;
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    int errorCode3 = StatusCode.TIMEOUT.toInt();
                    returnCb.handleStatus(errorCode3, "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setActPairingResult(errorCode3);
                    return 0;
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setActPairingResult(errorCode);
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.scalar.webapi.service.contentsync.v1_0.GetInterfaceInformation
    public int getInterfaceInformation(GetInterfaceInformationCallback returnCb) {
        ApiCallLog apiCallLog = null;
        int errorCode = StatusCode.OK.toInt();
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.v(TAG, "*** getInterfaceInformation ***");
                    InterfaceInformation result = new InterfaceInformation();
                    result.productCategory = ConstantsSync.PRODUCT_CATEGORY;
                    result.productName = "";
                    result.modelName = "";
                    result.serverName = ConstantsSync.SERVER_NAME;
                    result.interfaceVersion = ConstantsSync.INTERFACE_VERSION;
                    Log.v(TAG, "*** getInterfaceInformation cb ***");
                    Log.v(TAG, "InterfaceInformation.productCategory  = " + result.productCategory);
                    Log.v(TAG, "InterfaceInformation.productName      = " + result.productName);
                    Log.v(TAG, "InterfaceInformation.modelName        = " + result.modelName);
                    Log.v(TAG, "InterfaceInformation.serverName       = " + result.serverName);
                    Log.v(TAG, "InterfaceInformation.interfaceVersion = " + result.interfaceVersion);
                    returnCb.returnCb(result);
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                    NetworkStateUtil.getInstance().setGetInterfaceInformationResult(errorCode);
                    return 0;
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    e.printStackTrace();
                    int errorCode2 = StatusCode.ANY.toInt();
                    returnCb.handleStatus(errorCode2, "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setGetInterfaceInformationResult(errorCode2);
                    return 0;
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    int errorCode3 = StatusCode.TIMEOUT.toInt();
                    returnCb.handleStatus(errorCode3, "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setGetInterfaceInformationResult(errorCode3);
                    return 0;
                } catch (Throwable th) {
                    th = th;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setGetInterfaceInformationResult(errorCode);
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.sony.scalar.webapi.service.contentsync.v1_0.NotifySyncStatus
    public int notifySyncStatus(SyncStatusSource syncStatus, NotifySyncStatusCallback returnCb) {
        Throwable th;
        ApiCallLog apiCallLog = null;
        int errorCode = StatusCode.OK.toInt();
        boolean bCanceled = NetworkStateUtil.getInstance().isCanceledTransferring();
        boolean isDownloadedNull = false;
        try {
            try {
                ApiCallLog apiCallLog2 = new ApiCallLog();
                try {
                    apiCallLog2.init();
                    Log.v(TAG, "*** notifySyncStatus, bCanceled = " + bCanceled + " ***");
                    Log.v(TAG, "syncStatusSource.uuid   = " + syncStatus.uuid);
                    Log.v(TAG, "syncStatusSource.status = " + syncStatus.status);
                    AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
                    boolean flg = SyncBackUpUtil.getInstance().getRegister(false);
                    String uuid = SyncBackUpUtil.getInstance().getSmartphoneInfo(null);
                    boolean bUuidEnable = uuid.equals(syncStatus.uuid);
                    if (true == flg && true == bUuidEnable) {
                        SyncStatus result = new SyncStatus();
                        if (syncStatus.status.equals("start")) {
                            cntDownloaded = 0;
                            if (bCanceled) {
                                result.totalCnt = 0;
                                result.remainingCnt = 0;
                                result.downloadableUrl = "";
                                result.contentType = "image/jpg";
                                result.fileName = "";
                                logNotifySyncStatusCb(result);
                                returnCb.returnCb(result);
                            } else {
                                long startUTC = SyncBackUpUtil.getInstance().getStartUTC(0L);
                                numberOfReservationFiles = dbUtil.beginGetReservationFiles(startUTC);
                                result.totalCnt = numberOfReservationFiles;
                                result.remainingCnt = numberOfReservationFiles;
                                if (numberOfReservationFiles <= 0 || bCanceled) {
                                    result.downloadableUrl = "";
                                    result.contentType = "image/jpg";
                                    result.fileName = "";
                                } else {
                                    result.downloadableUrl = AutoSyncConstants.CONTENTSYNC_BASE_URL + dbUtil.getCurrentUrl();
                                    result.contentType = "image/jpg";
                                    result.fileName = dbUtil.getCurrentFileName();
                                    cntDownloaded++;
                                }
                                logNotifySyncStatusCb(result);
                                returnCb.returnCb(result);
                            }
                        } else if (syncStatus.status.equals("downloaded")) {
                            if (bCanceled) {
                                result.totalCnt = numberOfReservationFiles;
                                result.remainingCnt = numberOfReservationFiles - cntDownloaded;
                                result.downloadableUrl = "";
                                result.contentType = "";
                                result.fileName = "";
                                isDownloadedNull = true;
                                logNotifySyncStatusCb(result);
                                returnCb.returnCb(result);
                            } else if (cntDownloaded == numberOfReservationFiles) {
                                dbUtil.moveToNextReservationFile();
                                dbUtil.endGetReservationFiles();
                                result.totalCnt = numberOfReservationFiles;
                                result.remainingCnt = numberOfReservationFiles - cntDownloaded;
                                result.downloadableUrl = "";
                                result.contentType = "";
                                result.fileName = "";
                                isDownloadedNull = true;
                                logNotifySyncStatusCb(result);
                                returnCb.returnCb(result);
                            } else {
                                boolean bret = dbUtil.moveToNextReservationFile();
                                boolean bCanceled2 = NetworkStateUtil.getInstance().isCanceledTransferring();
                                if (true != bret || bCanceled2) {
                                    if (!bCanceled2) {
                                        dbUtil.endGetReservationFiles();
                                    }
                                    result.totalCnt = numberOfReservationFiles;
                                    result.remainingCnt = numberOfReservationFiles - cntDownloaded;
                                    result.downloadableUrl = "";
                                    result.contentType = "";
                                    result.fileName = "";
                                    isDownloadedNull = true;
                                } else {
                                    result.totalCnt = numberOfReservationFiles;
                                    result.remainingCnt = numberOfReservationFiles - cntDownloaded;
                                    result.downloadableUrl = AutoSyncConstants.CONTENTSYNC_BASE_URL + dbUtil.getCurrentUrl();
                                    result.contentType = "image/jpg";
                                    result.fileName = dbUtil.getCurrentFileName();
                                    cntDownloaded++;
                                }
                                logNotifySyncStatusCb(result);
                                returnCb.returnCb(result);
                            }
                        } else if (syncStatus.status.equals("skip")) {
                            if (bCanceled) {
                                result.totalCnt = numberOfReservationFiles;
                                result.remainingCnt = numberOfReservationFiles - cntDownloaded;
                                result.downloadableUrl = "";
                                result.contentType = "";
                                result.fileName = "";
                                logNotifySyncStatusCb(result);
                                returnCb.returnCb(result);
                            } else if (cntDownloaded + 1 < numberOfReservationFiles) {
                                boolean bret2 = dbUtil.moveToNextReservationFile();
                                if (bret2) {
                                    cntDownloaded++;
                                    bret2 = dbUtil.moveToNextReservationFile();
                                }
                                if (true == bret2) {
                                    result.totalCnt = numberOfReservationFiles;
                                    result.remainingCnt = numberOfReservationFiles - cntDownloaded;
                                    result.downloadableUrl = AutoSyncConstants.CONTENTSYNC_BASE_URL + dbUtil.getCurrentUrl();
                                    result.contentType = "image/jpg";
                                    result.fileName = dbUtil.getCurrentFileName();
                                    cntDownloaded++;
                                    logNotifySyncStatusCb(result);
                                    returnCb.returnCb(result);
                                } else {
                                    dbUtil.endGetReservationFiles();
                                    errorCode = StatusCode.UNSUPPORTED_OPERATION.toInt();
                                    Log.e(TAG, "*** notifySyncStatus cb error" + errorCode + " (Unsupported Operation) ***");
                                    returnCb.handleStatus(errorCode, "Unsupported Operation.");
                                }
                            } else {
                                dbUtil.endGetReservationFiles();
                                errorCode = StatusCode.UNSUPPORTED_OPERATION.toInt();
                                Log.e(TAG, "*** notifySyncStatus cb error" + errorCode + " (Unsupported Operation) ***");
                                returnCb.handleStatus(errorCode, "Unsupported Operation.");
                            }
                        } else if (syncStatus.status.equals("memoryFull") || syncStatus.status.equals("batteryLow") || syncStatus.status.equals("end")) {
                            if (!bCanceled) {
                                dbUtil.endGetReservationFiles();
                            }
                            cntDownloaded--;
                            result.totalCnt = numberOfReservationFiles;
                            result.remainingCnt = numberOfReservationFiles - cntDownloaded;
                            result.downloadableUrl = "";
                            result.contentType = "";
                            result.fileName = "";
                            logNotifySyncStatusCb(result);
                            returnCb.returnCb(result);
                        } else {
                            if (!bCanceled) {
                                dbUtil.endGetReservationFiles();
                            }
                            errorCode = StatusCode.UNSUPPORTED_OPERATION.toInt();
                            Log.e(TAG, "*** notifySyncStatus cb error " + errorCode + " (Unsupported Operation) ***");
                            returnCb.handleStatus(errorCode, "Unsupported Operation.");
                        }
                    } else {
                        boolean bret3 = dbUtil.isNeedUpdateDatabase();
                        long lastStartTime = SyncBackUpUtil.getInstance().getStartUTC(0L);
                        if (bret3) {
                            dbUtil.updateDatabase(false);
                        } else {
                            dbUtil.importDatabase();
                        }
                        numberOfReservationFiles = dbUtil.getNumberOfTransferReservationFiles(lastStartTime);
                        cntDownloaded = 0;
                        errorCode = StatusCode.CLIENT_NOT_PAIRED.toInt();
                        Log.e(TAG, "*** notifySyncStatus cb error " + errorCode + " (Not paired yet.) ***");
                        returnCb.handleStatus(errorCode, "Client application is not already paired.");
                    }
                    if (apiCallLog2 != null) {
                        apiCallLog2.clear();
                        apiCallLog = null;
                    } else {
                        apiCallLog = apiCallLog2;
                    }
                    NetworkStateUtil.getInstance().setNotifySyncStatusResult(errorCode, syncStatus.status, numberOfReservationFiles, cntDownloaded, isDownloadedNull);
                    return 0;
                } catch (InterruptedException e) {
                    e = e;
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was interrupted.");
                    int errorCode2 = StatusCode.ANY.toInt();
                    e.printStackTrace();
                    returnCb.handleStatus(errorCode2, "Interrupted Error");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setNotifySyncStatusResult(errorCode2, syncStatus.status, numberOfReservationFiles, cntDownloaded, false);
                    return 0;
                } catch (TimeoutException e2) {
                    apiCallLog = apiCallLog2;
                    Log.e(TAG, "Method call of " + apiCallLog.getMethodName() + " was timeout.");
                    int errorCode3 = StatusCode.TIMEOUT.toInt();
                    returnCb.handleStatus(errorCode3, "Timed out");
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setNotifySyncStatusResult(errorCode3, syncStatus.status, numberOfReservationFiles, cntDownloaded, false);
                    return 0;
                } catch (Throwable th2) {
                    th = th2;
                    apiCallLog = apiCallLog2;
                    if (apiCallLog != null) {
                        apiCallLog.clear();
                    }
                    NetworkStateUtil.getInstance().setNotifySyncStatusResult(errorCode, syncStatus.status, numberOfReservationFiles, cntDownloaded, isDownloadedNull);
                    throw th;
                }
            } catch (InterruptedException e3) {
                e = e3;
            } catch (TimeoutException e4) {
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private void logNotifySyncStatusCb(SyncStatus result) {
        Log.v(TAG, "*** notifySyncStatus cb ***");
        Log.v(TAG, "syncStatus.totalCnt        = " + result.totalCnt);
        Log.v(TAG, "syncStatus.remainingCnt    = " + result.remainingCnt);
        Log.v(TAG, "syncStatus.downloadableUrl = " + result.downloadableUrl);
        Log.v(TAG, "syncStatus.contentType     = " + result.contentType);
        Log.v(TAG, "syncStatus.fileName        = " + result.fileName);
    }
}
