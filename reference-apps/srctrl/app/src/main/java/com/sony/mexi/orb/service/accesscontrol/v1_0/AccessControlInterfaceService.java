package com.sony.mexi.orb.service.accesscontrol.v1_0;

import com.sony.mexi.orb.server.DebugLogger;
import com.sony.mexi.webapi.Status;
import com.sony.scalar.webapi.lib.authlib.AuthLibManager;
import com.sony.scalar.webapi.lib.authlib.EnableMethodsState;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.ActEnableMethodsCallback;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.DeveloperInfo;
import com.sony.scalar.webapi.service.accesscontrol.v1_0.common.struct.DeveloperKey;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public class AccessControlInterfaceService extends AccessControlInterfaceServiceBase {
    private static final String TAG = AccessControlInterfaceService.class.getSimpleName();

    @Override // com.sony.mexi.orb.service.accesscontrol.v1_0.AccessControlInterfaceServiceBase
    public void actEnableMethods(DeveloperInfo developerInfo, ActEnableMethodsCallback returnCb) {
        DebugLogger.debug(TAG, "actEnableMethods");
        if (developerInfo == null) {
            returnCb.handleStatus(Status.ILLEGAL_ARGUMENT.toInt(), Status.ILLEGAL_ARGUMENT.toMessage());
            return;
        }
        String devName = developerInfo.developerName;
        String devId = developerInfo.developerID;
        String methods = developerInfo.methods;
        String sg = developerInfo.sg;
        AuthLibManager authMgr = AuthLibManager.getInstance();
        if (!authMgr.isReady()) {
            authMgr.notifyError(Status.SERVICE_UNAVAILABLE.toInt(), devName, devId, methods, sg);
            returnCb.handleStatus(Status.SERVICE_UNAVAILABLE.toInt(), "service unavailable");
            return;
        }
        authMgr.setReadyFlag(false);
        String hashCode = "";
        EnableMethodsState state = authMgr.getEnableMethodsState();
        if (sg.equals("")) {
            hashCode = authMgr.generateHashCode();
            state = EnableMethodsState.INITIAL;
        }
        if (authMgr.hookHandler(devName, devId, methods, sg, hashCode)) {
            String[] list = methods.split(":", 0);
            ArrayList<String> methodList = new ArrayList<>(Arrays.asList(list));
            authMgr.setEnableMethods(methodList);
            DeveloperKey key = new DeveloperKey();
            key.dg = hashCode;
            authMgr.setReadyFlag(true);
            returnCb.returnCb(key);
            return;
        }
        DeveloperKey key2 = new DeveloperKey();
        if (sg.equals("")) {
            authMgr.clearEnableMethods();
            authMgr.setEnableMethodsState(state.accept(), devName, devId, methods, sg);
            authMgr.setDg(hashCode);
            DebugLogger.debug(TAG, "hash: " + hashCode);
            key2.dg = hashCode;
            authMgr.setReadyFlag(true);
            returnCb.returnCb(key2);
            return;
        }
        if (!authMgr.verifyAuthentication(devName, devId, methods, sg)) {
            authMgr.clearEnableMethods();
            authMgr.setEnableMethodsState(state.error(), devName, devId, methods, sg);
            authMgr.setReadyFlag(true);
            returnCb.handleStatus(Status.ILLEGAL_REQUEST.toInt(), "illegal request");
            return;
        }
        authMgr.setEnableMethodsState(state.accept(), devName, devId, methods, sg);
        String[] list2 = methods.split(":", 0);
        ArrayList<String> methodList2 = new ArrayList<>(Arrays.asList(list2));
        authMgr.setEnableMethods(methodList2);
        key2.dg = "";
        authMgr.setReadyFlag(true);
        returnCb.returnCb(key2);
    }
}
