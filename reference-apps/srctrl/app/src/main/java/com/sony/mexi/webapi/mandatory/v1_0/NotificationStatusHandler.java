package com.sony.mexi.webapi.mandatory.v1_0;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.mexi.webapi.NotificationStatusResponse;

/* loaded from: classes.dex */
public interface NotificationStatusHandler extends Callbacks {
    void handleNotificationStatus(NotificationStatusResponse notificationStatusResponse);
}
