package com.sony.scalar.webapi.service.camera.v1_0.whitebalance;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParamCandidate;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParams;

/* loaded from: classes.dex */
public interface GetAvailableWhiteBalanceCallback extends Callbacks {
    void returnCb(WhiteBalanceParams whiteBalanceParams, WhiteBalanceParamCandidate[] whiteBalanceParamCandidateArr);
}
