package com.sony.mexi.orb.service.camera.v1_0;

import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.imaging.app.srctrl.webapi.definition.ResponseType;
import com.sony.mexi.orb.service.OrbAbstractClient;
import com.sony.mexi.orb.service.OrbAbstractClientCallbacks;
import com.sony.mexi.orb.service.OrbAbstractMethod;
import com.sony.mexi.orb.service.OrbAbstractVersion;
import com.sony.mexi.orb.service.OrbServiceProvider;
import com.sony.mexi.webapi.Status;
import com.sony.mexi.webapi.json.JsonArgumentException;
import com.sony.mexi.webapi.json.JsonUtil;
import com.sony.scalar.webapi.service.camera.v1_0.bulbshooting.StartBulbShootingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.bulbshooting.StopBulbShootingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetAvailableCameraFunctionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetCameraFunctionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetSupportedCameraFunctionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.SetCameraFunctionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingModeAvailableParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingModeSupportedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedAvailableParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ContShootingSpeedSupportedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventAELockParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventAvailableApiListParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventBeepModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventBracketShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventCameraFunctionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventCameraFunctionResultParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventCameraStatusParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventContinuousErrorParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventCreativeStyleParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventExposureCompensationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventExposureModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventFNumberParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventFlashModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventFocusModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventFormatStatusParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventIsoSpeedRateParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventLiveviewOrientationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventLiveviewStatusParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventMovieQualityParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventPictureEffectParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventPostviewImageSizeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventProgramShiftParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventSceneRecognitionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventSelfTimerParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventShootModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventShutterSpeedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventSteadyModeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventStillSizeParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventStorageInformationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventTakePictureParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventTouchAFPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventTriggeredErrorParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventViewAngleParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventWhiteBalanceParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.GetEventZoomInformationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.LiveviewFrameInfoParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingAvailableParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.SilentShootingSettingSupportedParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.StorageInformationParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFCurrentPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParamCandidate;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ZoomSettingAvailableParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ZoomSettingParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.ZoomSettingSupportedParams;
import com.sony.scalar.webapi.service.camera.v1_0.contshooting.StartContShootingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshooting.StopContShootingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetAvailableContShootingModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetContShootingModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetSupportedContShootingModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.SetContShootingModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetAvailableContShootingSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetContShootingSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetSupportedContShootingSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.SetContShootingSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetAvailableExposureCompensationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetExposureCompensationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetSupportedExposureCompensationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.SetExposureCompensationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetAvailableExposureModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetExposureModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetSupportedExposureModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.exposuremode.SetExposureModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetAvailableFlashModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetFlashModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetSupportedFlashModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.flashmode.SetFlashModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetAvailableFNumberCallback;
import com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetFNumberCallback;
import com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetSupportedFNumberCallback;
import com.sony.scalar.webapi.service.camera.v1_0.fnumber.SetFNumberCallback;
import com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetAvailableFocusModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetFocusModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetSupportedFocusModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.focusmode.SetFocusModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.getevent.GetEventCallback;
import com.sony.scalar.webapi.service.camera.v1_0.halfpressshutter.ActHalfPressShutterCallback;
import com.sony.scalar.webapi.service.camera.v1_0.halfpressshutter.CancelHalfPressShutterCallback;
import com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetAvailableIsoSpeedRateCallback;
import com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetIsoSpeedRateCallback;
import com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetSupportedIsoSpeedRateCallback;
import com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.SetIsoSpeedRateCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.GetAvailableLiveviewSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.GetLiveviewSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.GetSupportedLiveviewSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.StartLiveviewCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveview.StopLiveviewCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveviewframeinfo.GetLiveviewFrameInfoCallback;
import com.sony.scalar.webapi.service.camera.v1_0.liveviewframeinfo.SetLiveviewFrameInfoCallback;
import com.sony.scalar.webapi.service.camera.v1_0.misc.GetApplicationInfoCallback;
import com.sony.scalar.webapi.service.camera.v1_0.misc.GetAvailableApiListCallback;
import com.sony.scalar.webapi.service.camera.v1_0.movierec.StartMovieRecCallback;
import com.sony.scalar.webapi.service.camera.v1_0.movierec.StopMovieRecCallback;
import com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetAvailablePostviewImageSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetPostviewImageSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetSupportedPostviewImageSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.postviewimage.SetPostviewImageSizeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.programshift.GetSupportedProgramShiftCallback;
import com.sony.scalar.webapi.service.camera.v1_0.programshift.SetProgramShiftCallback;
import com.sony.scalar.webapi.service.camera.v1_0.recmode.StartRecModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.recmode.StopRecModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetAvailableSelfTimerCallback;
import com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetSelfTimerCallback;
import com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetSupportedSelfTimerCallback;
import com.sony.scalar.webapi.service.camera.v1_0.selftimer.SetSelfTimerCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetAvailableShootModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetShootModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetSupportedShootModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shootmode.SetShootModeCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetAvailableShutterSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetShutterSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetSupportedShutterSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.SetShutterSpeedCallback;
import com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetAvailableSilentShootingSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetSilentShootingSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetSupportedSilentShootingSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.silentshooting.SetSilentShootingSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.storageinformation.GetStorageInformationCallback;
import com.sony.scalar.webapi.service.camera.v1_0.takepicture.ActTakePictureCallback;
import com.sony.scalar.webapi.service.camera.v1_0.takepicture.AwaitTakePictureCallback;
import com.sony.scalar.webapi.service.camera.v1_0.touchafposition.CancelTouchAFPositionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.touchafposition.GetTouchAFPositionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.touchafposition.SetTouchAFPositionCallback;
import com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetAvailableWhiteBalanceCallback;
import com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetSupportedWhiteBalanceCallback;
import com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetWhiteBalanceCallback;
import com.sony.scalar.webapi.service.camera.v1_0.whitebalance.SetWhiteBalanceCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.ActZoomCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.GetAvailableZoomSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.GetSupportedZoomSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.GetZoomSettingCallback;
import com.sony.scalar.webapi.service.camera.v1_0.zoom.SetZoomSettingCallback;
import org.json.JSONArray;

/* loaded from: classes.dex */
public abstract class SmartRemoteControlServiceBase extends OrbAbstractVersion {
    private static final String SERVICE_VERSION = "1.0";

    public abstract void actHalfPressShutter(ActHalfPressShutterCallback actHalfPressShutterCallback);

    public abstract void actTakePicture(ActTakePictureCallback actTakePictureCallback);

    public abstract void actZoom(String str, String str2, ActZoomCallback actZoomCallback);

    public abstract void awaitTakePicture(AwaitTakePictureCallback awaitTakePictureCallback);

    public abstract void cancelHalfPressShutter(CancelHalfPressShutterCallback cancelHalfPressShutterCallback);

    public abstract void cancelTouchAFPosition(CancelTouchAFPositionCallback cancelTouchAFPositionCallback);

    public abstract void getApplicationInfo(GetApplicationInfoCallback getApplicationInfoCallback);

    public abstract void getAvailableApiList(GetAvailableApiListCallback getAvailableApiListCallback);

    public abstract void getAvailableCameraFunction(GetAvailableCameraFunctionCallback getAvailableCameraFunctionCallback);

    public abstract void getAvailableContShootingMode(GetAvailableContShootingModeCallback getAvailableContShootingModeCallback);

    public abstract void getAvailableContShootingSpeed(GetAvailableContShootingSpeedCallback getAvailableContShootingSpeedCallback);

    public abstract void getAvailableExposureCompensation(GetAvailableExposureCompensationCallback getAvailableExposureCompensationCallback);

    public abstract void getAvailableExposureMode(GetAvailableExposureModeCallback getAvailableExposureModeCallback);

    public abstract void getAvailableFNumber(GetAvailableFNumberCallback getAvailableFNumberCallback);

    public abstract void getAvailableFlashMode(GetAvailableFlashModeCallback getAvailableFlashModeCallback);

    public abstract void getAvailableFocusMode(GetAvailableFocusModeCallback getAvailableFocusModeCallback);

    public abstract void getAvailableIsoSpeedRate(GetAvailableIsoSpeedRateCallback getAvailableIsoSpeedRateCallback);

    public abstract void getAvailableLiveviewSize(GetAvailableLiveviewSizeCallback getAvailableLiveviewSizeCallback);

    public abstract void getAvailablePostviewImageSize(GetAvailablePostviewImageSizeCallback getAvailablePostviewImageSizeCallback);

    public abstract void getAvailableSelfTimer(GetAvailableSelfTimerCallback getAvailableSelfTimerCallback);

    public abstract void getAvailableShootMode(GetAvailableShootModeCallback getAvailableShootModeCallback);

    public abstract void getAvailableShutterSpeed(GetAvailableShutterSpeedCallback getAvailableShutterSpeedCallback);

    public abstract void getAvailableSilentShootingSetting(GetAvailableSilentShootingSettingCallback getAvailableSilentShootingSettingCallback);

    public abstract void getAvailableWhiteBalance(GetAvailableWhiteBalanceCallback getAvailableWhiteBalanceCallback);

    public abstract void getAvailableZoomSetting(GetAvailableZoomSettingCallback getAvailableZoomSettingCallback);

    public abstract void getCameraFunction(GetCameraFunctionCallback getCameraFunctionCallback);

    public abstract void getContShootingMode(GetContShootingModeCallback getContShootingModeCallback);

    public abstract void getContShootingSpeed(GetContShootingSpeedCallback getContShootingSpeedCallback);

    public abstract void getEvent(boolean z, GetEventCallback getEventCallback);

    public abstract void getExposureCompensation(GetExposureCompensationCallback getExposureCompensationCallback);

    public abstract void getExposureMode(GetExposureModeCallback getExposureModeCallback);

    public abstract void getFNumber(GetFNumberCallback getFNumberCallback);

    public abstract void getFlashMode(GetFlashModeCallback getFlashModeCallback);

    public abstract void getFocusMode(GetFocusModeCallback getFocusModeCallback);

    public abstract void getIsoSpeedRate(GetIsoSpeedRateCallback getIsoSpeedRateCallback);

    public abstract void getLiveviewFrameInfo(GetLiveviewFrameInfoCallback getLiveviewFrameInfoCallback);

    public abstract void getLiveviewSize(GetLiveviewSizeCallback getLiveviewSizeCallback);

    public abstract void getPostviewImageSize(GetPostviewImageSizeCallback getPostviewImageSizeCallback);

    public abstract void getSelfTimer(GetSelfTimerCallback getSelfTimerCallback);

    public abstract void getShootMode(GetShootModeCallback getShootModeCallback);

    public abstract void getShutterSpeed(GetShutterSpeedCallback getShutterSpeedCallback);

    public abstract void getSilentShootingSetting(GetSilentShootingSettingCallback getSilentShootingSettingCallback);

    public abstract void getStorageInformation(GetStorageInformationCallback getStorageInformationCallback);

    public abstract void getSupportedCameraFunction(GetSupportedCameraFunctionCallback getSupportedCameraFunctionCallback);

    public abstract void getSupportedContShootingMode(GetSupportedContShootingModeCallback getSupportedContShootingModeCallback);

    public abstract void getSupportedContShootingSpeed(GetSupportedContShootingSpeedCallback getSupportedContShootingSpeedCallback);

    public abstract void getSupportedExposureCompensation(GetSupportedExposureCompensationCallback getSupportedExposureCompensationCallback);

    public abstract void getSupportedExposureMode(GetSupportedExposureModeCallback getSupportedExposureModeCallback);

    public abstract void getSupportedFNumber(GetSupportedFNumberCallback getSupportedFNumberCallback);

    public abstract void getSupportedFlashMode(GetSupportedFlashModeCallback getSupportedFlashModeCallback);

    public abstract void getSupportedFocusMode(GetSupportedFocusModeCallback getSupportedFocusModeCallback);

    public abstract void getSupportedIsoSpeedRate(GetSupportedIsoSpeedRateCallback getSupportedIsoSpeedRateCallback);

    public abstract void getSupportedLiveviewSize(GetSupportedLiveviewSizeCallback getSupportedLiveviewSizeCallback);

    public abstract void getSupportedPostviewImageSize(GetSupportedPostviewImageSizeCallback getSupportedPostviewImageSizeCallback);

    public abstract void getSupportedProgramShift(GetSupportedProgramShiftCallback getSupportedProgramShiftCallback);

    public abstract void getSupportedSelfTimer(GetSupportedSelfTimerCallback getSupportedSelfTimerCallback);

    public abstract void getSupportedShootMode(GetSupportedShootModeCallback getSupportedShootModeCallback);

    public abstract void getSupportedShutterSpeed(GetSupportedShutterSpeedCallback getSupportedShutterSpeedCallback);

    public abstract void getSupportedSilentShootingSetting(GetSupportedSilentShootingSettingCallback getSupportedSilentShootingSettingCallback);

    public abstract void getSupportedWhiteBalance(GetSupportedWhiteBalanceCallback getSupportedWhiteBalanceCallback);

    public abstract void getSupportedZoomSetting(GetSupportedZoomSettingCallback getSupportedZoomSettingCallback);

    public abstract void getTouchAFPosition(GetTouchAFPositionCallback getTouchAFPositionCallback);

    public abstract void getWhiteBalance(GetWhiteBalanceCallback getWhiteBalanceCallback);

    public abstract void getZoomSetting(GetZoomSettingCallback getZoomSettingCallback);

    public abstract void setCameraFunction(String str, SetCameraFunctionCallback setCameraFunctionCallback);

    public abstract void setContShootingMode(ContShootingModeParams contShootingModeParams, SetContShootingModeCallback setContShootingModeCallback);

    public abstract void setContShootingSpeed(ContShootingSpeedParams contShootingSpeedParams, SetContShootingSpeedCallback setContShootingSpeedCallback);

    public abstract void setExposureCompensation(int i, SetExposureCompensationCallback setExposureCompensationCallback);

    public abstract void setExposureMode(String str, SetExposureModeCallback setExposureModeCallback);

    public abstract void setFNumber(String str, SetFNumberCallback setFNumberCallback);

    public abstract void setFlashMode(String str, SetFlashModeCallback setFlashModeCallback);

    public abstract void setFocusMode(String str, SetFocusModeCallback setFocusModeCallback);

    public abstract void setIsoSpeedRate(String str, SetIsoSpeedRateCallback setIsoSpeedRateCallback);

    public abstract void setLiveviewFrameInfo(LiveviewFrameInfoParams liveviewFrameInfoParams, SetLiveviewFrameInfoCallback setLiveviewFrameInfoCallback);

    public abstract void setPostviewImageSize(String str, SetPostviewImageSizeCallback setPostviewImageSizeCallback);

    public abstract void setProgramShift(int i, SetProgramShiftCallback setProgramShiftCallback);

    public abstract void setSelfTimer(int i, SetSelfTimerCallback setSelfTimerCallback);

    public abstract void setShootMode(String str, SetShootModeCallback setShootModeCallback);

    public abstract void setShutterSpeed(String str, SetShutterSpeedCallback setShutterSpeedCallback);

    public abstract void setSilentShootingSetting(SilentShootingSettingParams silentShootingSettingParams, SetSilentShootingSettingCallback setSilentShootingSettingCallback);

    public abstract void setTouchAFPosition(double d, double d2, SetTouchAFPositionCallback setTouchAFPositionCallback);

    public abstract void setWhiteBalance(String str, boolean z, int i, SetWhiteBalanceCallback setWhiteBalanceCallback);

    public abstract void setZoomSetting(ZoomSettingParams zoomSettingParams, SetZoomSettingCallback setZoomSettingCallback);

    public abstract void startBulbShooting(StartBulbShootingCallback startBulbShootingCallback);

    public abstract void startContShooting(StartContShootingCallback startContShootingCallback);

    public abstract void startLiveview(StartLiveviewCallback startLiveviewCallback);

    public abstract void startLiveviewWithSize(String str, StartLiveviewCallback startLiveviewCallback);

    public abstract void startMovieRec(StartMovieRecCallback startMovieRecCallback);

    public abstract void startRecMode(StartRecModeCallback startRecModeCallback);

    public abstract void stopBulbShooting(StopBulbShootingCallback stopBulbShootingCallback);

    public abstract void stopContShooting(StopContShootingCallback stopContShootingCallback);

    public abstract void stopLiveview(StopLiveviewCallback stopLiveviewCallback);

    public abstract void stopMovieRec(StopMovieRecCallback stopMovieRecCallback);

    public abstract void stopRecMode(StopRecModeCallback stopRecModeCallback);

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final String getServiceVersion() {
        return SERVICE_VERSION;
    }

    /* loaded from: classes.dex */
    private static class StartBulbShootingCallbackImpl extends OrbAbstractClientCallbacks implements StartBulbShootingCallback {
        OrbAbstractClient client;

        StartBulbShootingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.bulbshooting.StartBulbShootingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StopBulbShootingCallbackImpl extends OrbAbstractClientCallbacks implements StopBulbShootingCallback {
        OrbAbstractClient client;

        StopBulbShootingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.bulbshooting.StopBulbShootingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableCameraFunctionCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableCameraFunctionCallback {
        OrbAbstractClient client;

        GetAvailableCameraFunctionCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetAvailableCameraFunctionCallback
        public void returnCb(String currentCameraFunction, String[] cameraFunctionCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentCameraFunction);
            JsonUtil.put(orbResponse, cameraFunctionCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetCameraFunctionCallbackImpl extends OrbAbstractClientCallbacks implements GetCameraFunctionCallback {
        OrbAbstractClient client;

        GetCameraFunctionCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetCameraFunctionCallback
        public void returnCb(String currentCameraFunction) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentCameraFunction);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedCameraFunctionCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedCameraFunctionCallback {
        OrbAbstractClient client;

        GetSupportedCameraFunctionCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.GetSupportedCameraFunctionCallback
        public void returnCb(String[] cameraFunctionCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, cameraFunctionCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetCameraFunctionCallbackImpl extends OrbAbstractClientCallbacks implements SetCameraFunctionCallback {
        OrbAbstractClient client;

        SetCameraFunctionCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.cameraFunction.SetCameraFunctionCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StartContShootingCallbackImpl extends OrbAbstractClientCallbacks implements StartContShootingCallback {
        OrbAbstractClient client;

        StartContShootingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshooting.StartContShootingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StopContShootingCallbackImpl extends OrbAbstractClientCallbacks implements StopContShootingCallback {
        OrbAbstractClient client;

        StopContShootingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshooting.StopContShootingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableContShootingModeCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableContShootingModeCallback {
        OrbAbstractClient client;

        GetAvailableContShootingModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetAvailableContShootingModeCallback
        public void returnCb(ContShootingModeAvailableParams availableContShootingMode) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ContShootingModeAvailableParams.Converter.REF.toJson(availableContShootingMode));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetContShootingModeCallbackImpl extends OrbAbstractClientCallbacks implements GetContShootingModeCallback {
        OrbAbstractClient client;

        GetContShootingModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetContShootingModeCallback
        public void returnCb(ContShootingModeParams currentContShootingMode) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ContShootingModeParams.Converter.REF.toJson(currentContShootingMode));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedContShootingModeCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedContShootingModeCallback {
        OrbAbstractClient client;

        GetSupportedContShootingModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.GetSupportedContShootingModeCallback
        public void returnCb(ContShootingModeSupportedParams contShootingModeCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ContShootingModeSupportedParams.Converter.REF.toJson(contShootingModeCandidates));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetContShootingModeCallbackImpl extends OrbAbstractClientCallbacks implements SetContShootingModeCallback {
        OrbAbstractClient client;

        SetContShootingModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshootingmode.SetContShootingModeCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableContShootingSpeedCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableContShootingSpeedCallback {
        OrbAbstractClient client;

        GetAvailableContShootingSpeedCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetAvailableContShootingSpeedCallback
        public void returnCb(ContShootingSpeedAvailableParams availableContShootingSpeed) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ContShootingSpeedAvailableParams.Converter.REF.toJson(availableContShootingSpeed));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetContShootingSpeedCallbackImpl extends OrbAbstractClientCallbacks implements GetContShootingSpeedCallback {
        OrbAbstractClient client;

        GetContShootingSpeedCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetContShootingSpeedCallback
        public void returnCb(ContShootingSpeedParams currentContShootingSpeed) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ContShootingSpeedParams.Converter.REF.toJson(currentContShootingSpeed));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedContShootingSpeedCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedContShootingSpeedCallback {
        OrbAbstractClient client;

        GetSupportedContShootingSpeedCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.GetSupportedContShootingSpeedCallback
        public void returnCb(ContShootingSpeedSupportedParams contShootingSpeedCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ContShootingSpeedSupportedParams.Converter.REF.toJson(contShootingSpeedCandidates));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetContShootingSpeedCallbackImpl extends OrbAbstractClientCallbacks implements SetContShootingSpeedCallback {
        OrbAbstractClient client;

        SetContShootingSpeedCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.contshootingspeed.SetContShootingSpeedCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableExposureCompensationCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableExposureCompensationCallback {
        OrbAbstractClient client;

        GetAvailableExposureCompensationCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetAvailableExposureCompensationCallback
        public void returnCb(int currentExposureCompensation, int maxExposureCompensation, int minExposureCompensation, int stepIndexOfExposureCompensation) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentExposureCompensation);
            JsonUtil.put(orbResponse, maxExposureCompensation);
            JsonUtil.put(orbResponse, minExposureCompensation);
            JsonUtil.put(orbResponse, stepIndexOfExposureCompensation);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetExposureCompensationCallbackImpl extends OrbAbstractClientCallbacks implements GetExposureCompensationCallback {
        OrbAbstractClient client;

        GetExposureCompensationCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetExposureCompensationCallback
        public void returnCb(int exposureCompensation) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, exposureCompensation);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedExposureCompensationCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedExposureCompensationCallback {
        OrbAbstractClient client;

        GetSupportedExposureCompensationCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.GetSupportedExposureCompensationCallback
        public void returnCb(int[] maxExposureCompensations, int[] minExposureCompensations, int[] stepIndexesOfExposureCompensation) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, maxExposureCompensations);
            JsonUtil.put(orbResponse, minExposureCompensations);
            JsonUtil.put(orbResponse, stepIndexesOfExposureCompensation);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetExposureCompensationCallbackImpl extends OrbAbstractClientCallbacks implements SetExposureCompensationCallback {
        OrbAbstractClient client;

        SetExposureCompensationCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.exposurecompensation.SetExposureCompensationCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableExposureModeCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableExposureModeCallback {
        OrbAbstractClient client;

        GetAvailableExposureModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetAvailableExposureModeCallback
        public void returnCb(String currentExposureMode, String[] exposureModeCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentExposureMode);
            JsonUtil.put(orbResponse, exposureModeCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetExposureModeCallbackImpl extends OrbAbstractClientCallbacks implements GetExposureModeCallback {
        OrbAbstractClient client;

        GetExposureModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetExposureModeCallback
        public void returnCb(String currentExposureMode) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentExposureMode);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedExposureModeCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedExposureModeCallback {
        OrbAbstractClient client;

        GetSupportedExposureModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.exposuremode.GetSupportedExposureModeCallback
        public void returnCb(String[] exposureModeCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, exposureModeCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetExposureModeCallbackImpl extends OrbAbstractClientCallbacks implements SetExposureModeCallback {
        OrbAbstractClient client;

        SetExposureModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.exposuremode.SetExposureModeCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableFlashModeCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableFlashModeCallback {
        OrbAbstractClient client;

        GetAvailableFlashModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetAvailableFlashModeCallback
        public void returnCb(String current, String[] flash) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, current);
            JsonUtil.put(orbResponse, flash);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetFlashModeCallbackImpl extends OrbAbstractClientCallbacks implements GetFlashModeCallback {
        OrbAbstractClient client;

        GetFlashModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetFlashModeCallback
        public void returnCb(String flash) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, flash);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedFlashModeCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedFlashModeCallback {
        OrbAbstractClient client;

        GetSupportedFlashModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.flashmode.GetSupportedFlashModeCallback
        public void returnCb(String[] flash) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, flash);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetFlashModeCallbackImpl extends OrbAbstractClientCallbacks implements SetFlashModeCallback {
        OrbAbstractClient client;

        SetFlashModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.flashmode.SetFlashModeCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableFNumberCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableFNumberCallback {
        OrbAbstractClient client;

        GetAvailableFNumberCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetAvailableFNumberCallback
        public void returnCb(String currentFNumber, String[] fNumberCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentFNumber);
            JsonUtil.put(orbResponse, fNumberCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetFNumberCallbackImpl extends OrbAbstractClientCallbacks implements GetFNumberCallback {
        OrbAbstractClient client;

        GetFNumberCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetFNumberCallback
        public void returnCb(String currentFNumber) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentFNumber);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedFNumberCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedFNumberCallback {
        OrbAbstractClient client;

        GetSupportedFNumberCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.fnumber.GetSupportedFNumberCallback
        public void returnCb(String[] fNumberCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, fNumberCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetFNumberCallbackImpl extends OrbAbstractClientCallbacks implements SetFNumberCallback {
        OrbAbstractClient client;

        SetFNumberCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.fnumber.SetFNumberCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableFocusModeCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableFocusModeCallback {
        OrbAbstractClient client;

        GetAvailableFocusModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetAvailableFocusModeCallback
        public void returnCb(String currentFocusMode, String[] focusModeCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentFocusMode);
            JsonUtil.put(orbResponse, focusModeCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetFocusModeCallbackImpl extends OrbAbstractClientCallbacks implements GetFocusModeCallback {
        OrbAbstractClient client;

        GetFocusModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetFocusModeCallback
        public void returnCb(String currentFocusMode) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentFocusMode);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedFocusModeCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedFocusModeCallback {
        OrbAbstractClient client;

        GetSupportedFocusModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.focusmode.GetSupportedFocusModeCallback
        public void returnCb(String[] focusModeCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, focusModeCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetFocusModeCallbackImpl extends OrbAbstractClientCallbacks implements SetFocusModeCallback {
        OrbAbstractClient client;

        SetFocusModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.focusmode.SetFocusModeCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetEventCallbackImpl extends OrbAbstractClientCallbacks implements GetEventCallback {
        OrbAbstractClient client;

        GetEventCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.getevent.GetEventCallback
        public void returnCb(GetEventAvailableApiListParams availableApiList, GetEventCameraStatusParams cameraStatus, GetEventZoomInformationParams zoomInformation, GetEventLiveviewStatusParams liveviewStatus, GetEventLiveviewOrientationParams liveviewOrientation, GetEventTakePictureParams[] takePicture, GetEventContinuousErrorParams[] continuousError, GetEventTriggeredErrorParams triggeredError, GetEventSceneRecognitionParams sceneRecognition, GetEventFormatStatusParams formatStatus, GetEventStorageInformationParams[] storageInformation, GetEventBeepModeParams beepMode, GetEventCameraFunctionParams cameraFunction, GetEventMovieQualityParams movieQuality, GetEventStillSizeParams stillSize, GetEventCameraFunctionResultParams cameraFunctionResult, GetEventSteadyModeParams steadyMode, GetEventViewAngleParams viewAngle, GetEventExposureModeParams exposureMode, GetEventPostviewImageSizeParams postviewImageSize, GetEventSelfTimerParams selfTimer, GetEventShootModeParams shootMode, GetEventAELockParams aeLock, GetEventBracketShootModeParams bracketShootMode, GetEventCreativeStyleParams creativeStyle, GetEventExposureCompensationParams exposureCompensation, GetEventFlashModeParams flashMode, GetEventFNumberParams fNumber, GetEventFocusModeParams focusMode, GetEventIsoSpeedRateParams isoSpeedRate, GetEventPictureEffectParams pictureEffect, GetEventProgramShiftParams programShift, GetEventShutterSpeedParams shutterSpeed, GetEventWhiteBalanceParams whiteBalance, GetEventTouchAFPositionParams touchAFPosition) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, GetEventAvailableApiListParams.Converter.REF.toJson(availableApiList));
            JsonUtil.put(orbResponse, GetEventCameraStatusParams.Converter.REF.toJson(cameraStatus));
            JsonUtil.put(orbResponse, GetEventZoomInformationParams.Converter.REF.toJson(zoomInformation));
            JsonUtil.put(orbResponse, GetEventLiveviewStatusParams.Converter.REF.toJson(liveviewStatus));
            JsonUtil.put(orbResponse, GetEventLiveviewOrientationParams.Converter.REF.toJson(liveviewOrientation));
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(takePicture, GetEventTakePictureParams.Converter.REF));
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(continuousError, GetEventContinuousErrorParams.Converter.REF));
            JsonUtil.put(orbResponse, GetEventTriggeredErrorParams.Converter.REF.toJson(triggeredError));
            JsonUtil.put(orbResponse, GetEventSceneRecognitionParams.Converter.REF.toJson(sceneRecognition));
            JsonUtil.put(orbResponse, GetEventFormatStatusParams.Converter.REF.toJson(formatStatus));
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(storageInformation, GetEventStorageInformationParams.Converter.REF));
            JsonUtil.put(orbResponse, GetEventBeepModeParams.Converter.REF.toJson(beepMode));
            JsonUtil.put(orbResponse, GetEventCameraFunctionParams.Converter.REF.toJson(cameraFunction));
            JsonUtil.put(orbResponse, GetEventMovieQualityParams.Converter.REF.toJson(movieQuality));
            JsonUtil.put(orbResponse, GetEventStillSizeParams.Converter.REF.toJson(stillSize));
            JsonUtil.put(orbResponse, GetEventCameraFunctionResultParams.Converter.REF.toJson(cameraFunctionResult));
            JsonUtil.put(orbResponse, GetEventSteadyModeParams.Converter.REF.toJson(steadyMode));
            JsonUtil.put(orbResponse, GetEventViewAngleParams.Converter.REF.toJson(viewAngle));
            JsonUtil.put(orbResponse, GetEventExposureModeParams.Converter.REF.toJson(exposureMode));
            JsonUtil.put(orbResponse, GetEventPostviewImageSizeParams.Converter.REF.toJson(postviewImageSize));
            JsonUtil.put(orbResponse, GetEventSelfTimerParams.Converter.REF.toJson(selfTimer));
            JsonUtil.put(orbResponse, GetEventShootModeParams.Converter.REF.toJson(shootMode));
            JsonUtil.put(orbResponse, GetEventAELockParams.Converter.REF.toJson(aeLock));
            JsonUtil.put(orbResponse, GetEventBracketShootModeParams.Converter.REF.toJson(bracketShootMode));
            JsonUtil.put(orbResponse, GetEventCreativeStyleParams.Converter.REF.toJson(creativeStyle));
            JsonUtil.put(orbResponse, GetEventExposureCompensationParams.Converter.REF.toJson(exposureCompensation));
            JsonUtil.put(orbResponse, GetEventFlashModeParams.Converter.REF.toJson(flashMode));
            JsonUtil.put(orbResponse, GetEventFNumberParams.Converter.REF.toJson(fNumber));
            JsonUtil.put(orbResponse, GetEventFocusModeParams.Converter.REF.toJson(focusMode));
            JsonUtil.put(orbResponse, GetEventIsoSpeedRateParams.Converter.REF.toJson(isoSpeedRate));
            JsonUtil.put(orbResponse, GetEventPictureEffectParams.Converter.REF.toJson(pictureEffect));
            JsonUtil.put(orbResponse, GetEventProgramShiftParams.Converter.REF.toJson(programShift));
            JsonUtil.put(orbResponse, GetEventShutterSpeedParams.Converter.REF.toJson(shutterSpeed));
            JsonUtil.put(orbResponse, GetEventWhiteBalanceParams.Converter.REF.toJson(whiteBalance));
            JsonUtil.put(orbResponse, GetEventTouchAFPositionParams.Converter.REF.toJson(touchAFPosition));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class ActHalfPressShutterCallbackImpl extends OrbAbstractClientCallbacks implements ActHalfPressShutterCallback {
        OrbAbstractClient client;

        ActHalfPressShutterCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.halfpressshutter.ActHalfPressShutterCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class CancelHalfPressShutterCallbackImpl extends OrbAbstractClientCallbacks implements CancelHalfPressShutterCallback {
        OrbAbstractClient client;

        CancelHalfPressShutterCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.halfpressshutter.CancelHalfPressShutterCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableIsoSpeedRateCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableIsoSpeedRateCallback {
        OrbAbstractClient client;

        GetAvailableIsoSpeedRateCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetAvailableIsoSpeedRateCallback
        public void returnCb(String currentIso, String[] isoCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentIso);
            JsonUtil.put(orbResponse, isoCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetIsoSpeedRateCallbackImpl extends OrbAbstractClientCallbacks implements GetIsoSpeedRateCallback {
        OrbAbstractClient client;

        GetIsoSpeedRateCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetIsoSpeedRateCallback
        public void returnCb(String currentIso) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentIso);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedIsoSpeedRateCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedIsoSpeedRateCallback {
        OrbAbstractClient client;

        GetSupportedIsoSpeedRateCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.GetSupportedIsoSpeedRateCallback
        public void returnCb(String[] isoCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, isoCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetIsoSpeedRateCallbackImpl extends OrbAbstractClientCallbacks implements SetIsoSpeedRateCallback {
        OrbAbstractClient client;

        SetIsoSpeedRateCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.isospeedrate.SetIsoSpeedRateCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableLiveviewSizeCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableLiveviewSizeCallback {
        OrbAbstractClient client;

        GetAvailableLiveviewSizeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.liveview.GetAvailableLiveviewSizeCallback
        public void returnCb(String size, String[] candidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, size);
            JsonUtil.put(orbResponse, candidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetLiveviewSizeCallbackImpl extends OrbAbstractClientCallbacks implements GetLiveviewSizeCallback {
        OrbAbstractClient client;

        GetLiveviewSizeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.liveview.GetLiveviewSizeCallback
        public void returnCb(String size) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, size);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedLiveviewSizeCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedLiveviewSizeCallback {
        OrbAbstractClient client;

        GetSupportedLiveviewSizeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.liveview.GetSupportedLiveviewSizeCallback
        public void returnCb(String[] candidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, candidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StartLiveviewCallbackImpl extends OrbAbstractClientCallbacks implements StartLiveviewCallback {
        OrbAbstractClient client;

        StartLiveviewCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.liveview.StartLiveviewCallback
        public void returnCb(String url) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, url);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StopLiveviewCallbackImpl extends OrbAbstractClientCallbacks implements StopLiveviewCallback {
        OrbAbstractClient client;

        StopLiveviewCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.liveview.StopLiveviewCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetLiveviewFrameInfoCallbackImpl extends OrbAbstractClientCallbacks implements GetLiveviewFrameInfoCallback {
        OrbAbstractClient client;

        GetLiveviewFrameInfoCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.liveviewframeinfo.GetLiveviewFrameInfoCallback
        public void returnCb(LiveviewFrameInfoParams currentLiveviewFrameInfo) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, LiveviewFrameInfoParams.Converter.REF.toJson(currentLiveviewFrameInfo));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetLiveviewFrameInfoCallbackImpl extends OrbAbstractClientCallbacks implements SetLiveviewFrameInfoCallback {
        OrbAbstractClient client;

        SetLiveviewFrameInfoCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.liveviewframeinfo.SetLiveviewFrameInfoCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetApplicationInfoCallbackImpl extends OrbAbstractClientCallbacks implements GetApplicationInfoCallback {
        OrbAbstractClient client;

        GetApplicationInfoCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.misc.GetApplicationInfoCallback
        public void returnCb(String name, String version) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, name);
            JsonUtil.put(orbResponse, version);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableApiListCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableApiListCallback {
        OrbAbstractClient client;

        GetAvailableApiListCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.misc.GetAvailableApiListCallback
        public void returnCb(String[] names) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, names);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StartMovieRecCallbackImpl extends OrbAbstractClientCallbacks implements StartMovieRecCallback {
        OrbAbstractClient client;

        StartMovieRecCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.movierec.StartMovieRecCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StopMovieRecCallbackImpl extends OrbAbstractClientCallbacks implements StopMovieRecCallback {
        OrbAbstractClient client;

        StopMovieRecCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.movierec.StopMovieRecCallback
        public void returnCb(String url) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, url);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailablePostviewImageSizeCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailablePostviewImageSizeCallback {
        OrbAbstractClient client;

        GetAvailablePostviewImageSizeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetAvailablePostviewImageSizeCallback
        public void returnCb(String size, String[] candidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, size);
            JsonUtil.put(orbResponse, candidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetPostviewImageSizeCallbackImpl extends OrbAbstractClientCallbacks implements GetPostviewImageSizeCallback {
        OrbAbstractClient client;

        GetPostviewImageSizeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetPostviewImageSizeCallback
        public void returnCb(String size) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, size);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedPostviewImageSizeCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedPostviewImageSizeCallback {
        OrbAbstractClient client;

        GetSupportedPostviewImageSizeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.postviewimage.GetSupportedPostviewImageSizeCallback
        public void returnCb(String[] candidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, candidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetPostviewImageSizeCallbackImpl extends OrbAbstractClientCallbacks implements SetPostviewImageSizeCallback {
        OrbAbstractClient client;

        SetPostviewImageSizeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.postviewimage.SetPostviewImageSizeCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedProgramShiftCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedProgramShiftCallback {
        OrbAbstractClient client;

        GetSupportedProgramShiftCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.programshift.GetSupportedProgramShiftCallback
        public void returnCb(int[] stepRange) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, stepRange);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetProgramShiftCallbackImpl extends OrbAbstractClientCallbacks implements SetProgramShiftCallback {
        OrbAbstractClient client;

        SetProgramShiftCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.programshift.SetProgramShiftCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StartRecModeCallbackImpl extends OrbAbstractClientCallbacks implements StartRecModeCallback {
        OrbAbstractClient client;

        StartRecModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.recmode.StartRecModeCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class StopRecModeCallbackImpl extends OrbAbstractClientCallbacks implements StopRecModeCallback {
        OrbAbstractClient client;

        StopRecModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.recmode.StopRecModeCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableSelfTimerCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableSelfTimerCallback {
        OrbAbstractClient client;

        GetAvailableSelfTimerCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetAvailableSelfTimerCallback
        public void returnCb(int currentSelfTimer, int[] selfTimerCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentSelfTimer);
            JsonUtil.put(orbResponse, selfTimerCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSelfTimerCallbackImpl extends OrbAbstractClientCallbacks implements GetSelfTimerCallback {
        OrbAbstractClient client;

        GetSelfTimerCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetSelfTimerCallback
        public void returnCb(int currentSelfTimer) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentSelfTimer);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedSelfTimerCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedSelfTimerCallback {
        OrbAbstractClient client;

        GetSupportedSelfTimerCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.selftimer.GetSupportedSelfTimerCallback
        public void returnCb(int[] selfTimerCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, selfTimerCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetSelfTimerCallbackImpl extends OrbAbstractClientCallbacks implements SetSelfTimerCallback {
        OrbAbstractClient client;

        SetSelfTimerCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.selftimer.SetSelfTimerCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableShootModeCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableShootModeCallback {
        OrbAbstractClient client;

        GetAvailableShootModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetAvailableShootModeCallback
        public void returnCb(String current, String[] mode) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, current);
            JsonUtil.put(orbResponse, mode);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetShootModeCallbackImpl extends OrbAbstractClientCallbacks implements GetShootModeCallback {
        OrbAbstractClient client;

        GetShootModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetShootModeCallback
        public void returnCb(String mode) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, mode);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedShootModeCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedShootModeCallback {
        OrbAbstractClient client;

        GetSupportedShootModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.shootmode.GetSupportedShootModeCallback
        public void returnCb(String[] mode) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, mode);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetShootModeCallbackImpl extends OrbAbstractClientCallbacks implements SetShootModeCallback {
        OrbAbstractClient client;

        SetShootModeCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.shootmode.SetShootModeCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableShutterSpeedCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableShutterSpeedCallback {
        OrbAbstractClient client;

        GetAvailableShutterSpeedCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetAvailableShutterSpeedCallback
        public void returnCb(String currentShutterSpeed, String[] shutterSpeedCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentShutterSpeed);
            JsonUtil.put(orbResponse, shutterSpeedCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetShutterSpeedCallbackImpl extends OrbAbstractClientCallbacks implements GetShutterSpeedCallback {
        OrbAbstractClient client;

        GetShutterSpeedCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetShutterSpeedCallback
        public void returnCb(String currentShutterSpeed) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, currentShutterSpeed);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedShutterSpeedCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedShutterSpeedCallback {
        OrbAbstractClient client;

        GetSupportedShutterSpeedCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.GetSupportedShutterSpeedCallback
        public void returnCb(String[] shutterSpeedCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, shutterSpeedCandidates);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetShutterSpeedCallbackImpl extends OrbAbstractClientCallbacks implements SetShutterSpeedCallback {
        OrbAbstractClient client;

        SetShutterSpeedCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.shutterspeed.SetShutterSpeedCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableSilentShootingSettingCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableSilentShootingSettingCallback {
        OrbAbstractClient client;

        GetAvailableSilentShootingSettingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetAvailableSilentShootingSettingCallback
        public void returnCb(SilentShootingSettingAvailableParams availableSilentShootingSetting) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, SilentShootingSettingAvailableParams.Converter.REF.toJson(availableSilentShootingSetting));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSilentShootingSettingCallbackImpl extends OrbAbstractClientCallbacks implements GetSilentShootingSettingCallback {
        OrbAbstractClient client;

        GetSilentShootingSettingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetSilentShootingSettingCallback
        public void returnCb(SilentShootingSettingParams currentSilentShootingSetting) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, SilentShootingSettingParams.Converter.REF.toJson(currentSilentShootingSetting));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedSilentShootingSettingCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedSilentShootingSettingCallback {
        OrbAbstractClient client;

        GetSupportedSilentShootingSettingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.silentshooting.GetSupportedSilentShootingSettingCallback
        public void returnCb(SilentShootingSettingSupportedParams silentShootingSettingCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, SilentShootingSettingSupportedParams.Converter.REF.toJson(silentShootingSettingCandidates));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetSilentShootingSettingCallbackImpl extends OrbAbstractClientCallbacks implements SetSilentShootingSettingCallback {
        OrbAbstractClient client;

        SetSilentShootingSettingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.silentshooting.SetSilentShootingSettingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetStorageInformationCallbackImpl extends OrbAbstractClientCallbacks implements GetStorageInformationCallback {
        OrbAbstractClient client;

        GetStorageInformationCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.storageinformation.GetStorageInformationCallback
        public void returnCb(StorageInformationParams[] currentStorageInformation) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(currentStorageInformation, StorageInformationParams.Converter.REF));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class ActTakePictureCallbackImpl extends OrbAbstractClientCallbacks implements ActTakePictureCallback {
        OrbAbstractClient client;

        ActTakePictureCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.takepicture.ActTakePictureCallback
        public void returnCb(String[] url) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, url);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class AwaitTakePictureCallbackImpl extends OrbAbstractClientCallbacks implements AwaitTakePictureCallback {
        OrbAbstractClient client;

        AwaitTakePictureCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.takepicture.AwaitTakePictureCallback
        public void returnCb(String[] url) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, url);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class CancelTouchAFPositionCallbackImpl extends OrbAbstractClientCallbacks implements CancelTouchAFPositionCallback {
        OrbAbstractClient client;

        CancelTouchAFPositionCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.touchafposition.CancelTouchAFPositionCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetTouchAFPositionCallbackImpl extends OrbAbstractClientCallbacks implements GetTouchAFPositionCallback {
        OrbAbstractClient client;

        GetTouchAFPositionCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.touchafposition.GetTouchAFPositionCallback
        public void returnCb(TouchAFCurrentPositionParams position) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, TouchAFCurrentPositionParams.Converter.REF.toJson(position));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetTouchAFPositionCallbackImpl extends OrbAbstractClientCallbacks implements SetTouchAFPositionCallback {
        OrbAbstractClient client;

        SetTouchAFPositionCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.touchafposition.SetTouchAFPositionCallback
        public void returnCb(int ret, TouchAFPositionParams position) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            JsonUtil.put(orbResponse, TouchAFPositionParams.Converter.REF.toJson(position));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableWhiteBalanceCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableWhiteBalanceCallback {
        OrbAbstractClient client;

        GetAvailableWhiteBalanceCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetAvailableWhiteBalanceCallback
        public void returnCb(WhiteBalanceParams currentWhiteBalance, WhiteBalanceParamCandidate[] whiteBalanceCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, WhiteBalanceParams.Converter.REF.toJson(currentWhiteBalance));
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(whiteBalanceCandidates, WhiteBalanceParamCandidate.Converter.REF));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedWhiteBalanceCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedWhiteBalanceCallback {
        OrbAbstractClient client;

        GetSupportedWhiteBalanceCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetSupportedWhiteBalanceCallback
        public void returnCb(WhiteBalanceParamCandidate[] whiteBalanceCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, JsonUtil.toJsonArray(whiteBalanceCandidates, WhiteBalanceParamCandidate.Converter.REF));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetWhiteBalanceCallbackImpl extends OrbAbstractClientCallbacks implements GetWhiteBalanceCallback {
        OrbAbstractClient client;

        GetWhiteBalanceCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.whitebalance.GetWhiteBalanceCallback
        public void returnCb(WhiteBalanceParams currentWhiteBalance) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, WhiteBalanceParams.Converter.REF.toJson(currentWhiteBalance));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetWhiteBalanceCallbackImpl extends OrbAbstractClientCallbacks implements SetWhiteBalanceCallback {
        OrbAbstractClient client;

        SetWhiteBalanceCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.whitebalance.SetWhiteBalanceCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class ActZoomCallbackImpl extends OrbAbstractClientCallbacks implements ActZoomCallback {
        OrbAbstractClient client;

        ActZoomCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.zoom.ActZoomCallback
        public void returnCb(int ret) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ret);
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetAvailableZoomSettingCallbackImpl extends OrbAbstractClientCallbacks implements GetAvailableZoomSettingCallback {
        OrbAbstractClient client;

        GetAvailableZoomSettingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.zoom.GetAvailableZoomSettingCallback
        public void returnCb(ZoomSettingAvailableParams availableZoomSetting) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ZoomSettingAvailableParams.Converter.REF.toJson(availableZoomSetting));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetSupportedZoomSettingCallbackImpl extends OrbAbstractClientCallbacks implements GetSupportedZoomSettingCallback {
        OrbAbstractClient client;

        GetSupportedZoomSettingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.zoom.GetSupportedZoomSettingCallback
        public void returnCb(ZoomSettingSupportedParams zoomSettingCandidates) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ZoomSettingSupportedParams.Converter.REF.toJson(zoomSettingCandidates));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class GetZoomSettingCallbackImpl extends OrbAbstractClientCallbacks implements GetZoomSettingCallback {
        OrbAbstractClient client;

        GetZoomSettingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.zoom.GetZoomSettingCallback
        public void returnCb(ZoomSettingParams currentZoomSetting) {
            JSONArray orbResponse = new JSONArray();
            JsonUtil.put(orbResponse, ZoomSettingParams.Converter.REF.toJson(currentZoomSetting));
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private static class SetZoomSettingCallbackImpl extends OrbAbstractClientCallbacks implements SetZoomSettingCallback {
        OrbAbstractClient client;

        SetZoomSettingCallbackImpl(OrbAbstractClient client) {
            this.client = client;
        }

        @Override // com.sony.scalar.webapi.service.camera.v1_0.zoom.SetZoomSettingCallback
        public void returnCb() {
            JSONArray orbResponse = new JSONArray();
            this.client.sendResult(orbResponse);
        }

        @Override // com.sony.mexi.webapi.CallbackHandler
        public void handleStatus(int code, String message) {
            this.client.sendError(code, message);
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractClientCallbacks
        public OrbAbstractClient getClient() {
            return this.client;
        }

        @Override // com.sony.mexi.orb.service.Callbacks
        public void sendRaw(String result) {
            this.client.sendResultRaw(result);
        }
    }

    /* loaded from: classes.dex */
    private class MethodActHalfPressShutter extends OrbAbstractMethod {
        private MethodActHalfPressShutter() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                ActHalfPressShutterCallbackImpl callbacks = new ActHalfPressShutterCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.actHalfPressShutter(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodActTakePicture extends OrbAbstractMethod {
        private MethodActTakePicture() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                ActTakePictureCallbackImpl callbacks = new ActTakePictureCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.actTakePicture(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodActZoom extends OrbAbstractMethod {
        private MethodActZoom() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 2) {
                ActZoomCallbackImpl callbacks = new ActZoomCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.actZoom(JsonUtil.getString(params, 0), JsonUtil.getString(params, 1), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodAwaitTakePicture extends OrbAbstractMethod {
        private MethodAwaitTakePicture() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                AwaitTakePictureCallbackImpl callbacks = new AwaitTakePictureCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.awaitTakePicture(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodCancelHalfPressShutter extends OrbAbstractMethod {
        private MethodCancelHalfPressShutter() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                CancelHalfPressShutterCallbackImpl callbacks = new CancelHalfPressShutterCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.cancelHalfPressShutter(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodCancelTouchAFPosition extends OrbAbstractMethod {
        private MethodCancelTouchAFPosition() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                CancelTouchAFPositionCallbackImpl callbacks = new CancelTouchAFPositionCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.cancelTouchAFPosition(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetApplicationInfo extends OrbAbstractMethod {
        private MethodGetApplicationInfo() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetApplicationInfoCallbackImpl callbacks = new GetApplicationInfoCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getApplicationInfo(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableApiList extends OrbAbstractMethod {
        private MethodGetAvailableApiList() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableApiListCallbackImpl callbacks = new GetAvailableApiListCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableApiList(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableCameraFunction extends OrbAbstractMethod {
        private MethodGetAvailableCameraFunction() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableCameraFunctionCallbackImpl callbacks = new GetAvailableCameraFunctionCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableCameraFunction(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableContShootingMode extends OrbAbstractMethod {
        private MethodGetAvailableContShootingMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"contShootingMode\":\"string\", \"candidate\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableContShootingModeCallbackImpl callbacks = new GetAvailableContShootingModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableContShootingMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableContShootingSpeed extends OrbAbstractMethod {
        private MethodGetAvailableContShootingSpeed() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"contShootingSpeed\":\"string\", \"candidate\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableContShootingSpeedCallbackImpl callbacks = new GetAvailableContShootingSpeedCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableContShootingSpeed(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableExposureCompensation extends OrbAbstractMethod {
        private MethodGetAvailableExposureCompensation() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT, ResponseType.INT, ResponseType.INT, ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableExposureCompensationCallbackImpl callbacks = new GetAvailableExposureCompensationCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableExposureCompensation(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableExposureMode extends OrbAbstractMethod {
        private MethodGetAvailableExposureMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableExposureModeCallbackImpl callbacks = new GetAvailableExposureModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableExposureMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableFlashMode extends OrbAbstractMethod {
        private MethodGetAvailableFlashMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableFlashModeCallbackImpl callbacks = new GetAvailableFlashModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableFlashMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableFNumber extends OrbAbstractMethod {
        private MethodGetAvailableFNumber() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableFNumberCallbackImpl callbacks = new GetAvailableFNumberCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableFNumber(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableFocusMode extends OrbAbstractMethod {
        private MethodGetAvailableFocusMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableFocusModeCallbackImpl callbacks = new GetAvailableFocusModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableFocusMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableIsoSpeedRate extends OrbAbstractMethod {
        private MethodGetAvailableIsoSpeedRate() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableIsoSpeedRateCallbackImpl callbacks = new GetAvailableIsoSpeedRateCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableIsoSpeedRate(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableLiveviewSize extends OrbAbstractMethod {
        private MethodGetAvailableLiveviewSize() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableLiveviewSizeCallbackImpl callbacks = new GetAvailableLiveviewSizeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableLiveviewSize(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailablePostviewImageSize extends OrbAbstractMethod {
        private MethodGetAvailablePostviewImageSize() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailablePostviewImageSizeCallbackImpl callbacks = new GetAvailablePostviewImageSizeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailablePostviewImageSize(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableSelfTimer extends OrbAbstractMethod {
        private MethodGetAvailableSelfTimer() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT, ResponseType.INT_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableSelfTimerCallbackImpl callbacks = new GetAvailableSelfTimerCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableSelfTimer(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableShootMode extends OrbAbstractMethod {
        private MethodGetAvailableShootMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableShootModeCallbackImpl callbacks = new GetAvailableShootModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableShootMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableShutterSpeed extends OrbAbstractMethod {
        private MethodGetAvailableShutterSpeed() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING, ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableShutterSpeedCallbackImpl callbacks = new GetAvailableShutterSpeedCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableShutterSpeed(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableSilentShootingSetting extends OrbAbstractMethod {
        private MethodGetAvailableSilentShootingSetting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"silentShooting\":\"string\", \"candidate\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableSilentShootingSettingCallbackImpl callbacks = new GetAvailableSilentShootingSettingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableSilentShootingSetting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableWhiteBalance extends OrbAbstractMethod {
        private MethodGetAvailableWhiteBalance() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"whiteBalanceMode\":\"string\", \"colorTemperature\":\"int\"}", "{\"whiteBalanceMode\":\"string\", \"colorTemperatureRange\":\"int*\"}*"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableWhiteBalanceCallbackImpl callbacks = new GetAvailableWhiteBalanceCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableWhiteBalance(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetAvailableZoomSetting extends OrbAbstractMethod {
        private MethodGetAvailableZoomSetting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"zoom\":\"string\", \"candidate\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetAvailableZoomSettingCallbackImpl callbacks = new GetAvailableZoomSettingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getAvailableZoomSetting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetCameraFunction extends OrbAbstractMethod {
        private MethodGetCameraFunction() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetCameraFunctionCallbackImpl callbacks = new GetCameraFunctionCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getCameraFunction(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetContShootingMode extends OrbAbstractMethod {
        private MethodGetContShootingMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"contShootingMode\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetContShootingModeCallbackImpl callbacks = new GetContShootingModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getContShootingMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetContShootingSpeed extends OrbAbstractMethod {
        private MethodGetContShootingSpeed() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"contShootingSpeed\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetContShootingSpeedCallbackImpl callbacks = new GetContShootingSpeedCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getContShootingSpeed(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetEvent extends OrbAbstractMethod {
        private MethodGetEvent() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"type\":\"string\", \"names\":\"string*\"}", "{\"type\":\"string\", \"cameraStatus\":\"string\"}", "{\"type\":\"string\", \"zoomPosition\":\"int\", \"zoomNumberBox\":\"int\", \"zoomIndexCurrentBox\":\"int\", \"zoomPositionCurrentBox\":\"int\"}", "{\"type\":\"string\", \"liveviewStatus\":\"bool\"}", "{\"type\":\"string\", \"liveviewOrientation\":\"string\"}", "{\"type\":\"string\", \"takePictureUrl\":\"string*\"}*", "{\"type\":\"string\", \"continuousError\":\"string\", \"isContinued\":\"bool\"}*", "{\"type\":\"string\", \"triggeredError\":\"string*\"}", "{\"type\":\"string\", \"sceneRecognition\":\"string\", \"steadyRecognition\":\"string\", \"motionRecognition\":\"string\"}", "{\"type\":\"string\", \"formatResult\":\"string\"}", "{\"type\":\"string\", \"storageID\":\"string\", \"recordTarget\":\"bool\", \"numberOfRecordableImages\":\"int\", \"recordableTime\":\"int\", \"storageDescription\":\"string\"}*", "{\"type\":\"string\", \"currentBeepMode\":\"string\", \"beepModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentCameraFunction\":\"string\", \"cameraFunctionCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentMovieQuality\":\"string\", \"movieQualityCandidates\":\"string*\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentAspect\":\"string\", \"currentSize\":\"string\"}", "{\"type\":\"string\", \"cameraFunctionResult\":\"string\"}", "{\"type\":\"string\", \"currentSteadyMode\":\"string\", \"steadyModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentViewAngle\":\"int\", \"viewAngleCandidates\":\"int*\"}", "{\"type\":\"string\", \"currentExposureMode\":\"string\", \"exposureModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentPostviewImageSize\":\"string\", \"postviewImageSizeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentSelfTimer\":\"int\", \"selfTimerCandidates\":\"int*\"}", "{\"type\":\"string\", \"currentShootMode\":\"string\", \"shootModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentAELock\":\"bool\", \"aeLockCandidates\":\"bool*\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentBracketShootMode\":\"string\", \"currentBracketShootModeOption\":\"string\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentCreativeStyle\":\"string\", \"currentCreativeStyleContrast\":\"int\", \"currentCreativeStyleSaturation\":\"int\", \"currentCreativeStyleSharpness\":\"int\"}", "{\"type\":\"string\", \"currentExposureCompensation\":\"int\", \"maxExposureCompensation\":\"int\", \"minExposureCompensation\":\"int\", \"stepIndexOfExposureCompensation\":\"int\"}", "{\"type\":\"string\", \"currentFlashMode\":\"string\", \"flashModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentFNumber\":\"string\", \"fNumberCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentFocusMode\":\"string\", \"focusModeCandidates\":\"string*\"}", "{\"type\":\"string\", \"currentIsoSpeedRate\":\"string\", \"isoSpeedRateCandidates\":\"string*\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentPictureEffect\":\"string\", \"currentPictureEffectOption\":\"string\"}", "{\"type\":\"string\", \"isShifted\":\"bool\"}", "{\"type\":\"string\", \"currentShutterSpeed\":\"string\", \"shutterSpeedCandidates\":\"string*\"}", "{\"type\":\"string\", \"checkAvailability\":\"bool\", \"currentWhiteBalanceMode\":\"string\", \"currentColorTemperature\":\"int\"}", "{\"type\":\"string\", \"currentSet\":\"bool\", \"currentTouchCoordinates\":\"double*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.BOOL};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                GetEventCallbackImpl callbacks = new GetEventCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getEvent(JsonUtil.getBoolean(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetExposureCompensation extends OrbAbstractMethod {
        private MethodGetExposureCompensation() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetExposureCompensationCallbackImpl callbacks = new GetExposureCompensationCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getExposureCompensation(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetExposureMode extends OrbAbstractMethod {
        private MethodGetExposureMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetExposureModeCallbackImpl callbacks = new GetExposureModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getExposureMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetFlashMode extends OrbAbstractMethod {
        private MethodGetFlashMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetFlashModeCallbackImpl callbacks = new GetFlashModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getFlashMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetFNumber extends OrbAbstractMethod {
        private MethodGetFNumber() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetFNumberCallbackImpl callbacks = new GetFNumberCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getFNumber(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetFocusMode extends OrbAbstractMethod {
        private MethodGetFocusMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetFocusModeCallbackImpl callbacks = new GetFocusModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getFocusMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetIsoSpeedRate extends OrbAbstractMethod {
        private MethodGetIsoSpeedRate() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetIsoSpeedRateCallbackImpl callbacks = new GetIsoSpeedRateCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getIsoSpeedRate(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetLiveviewFrameInfo extends OrbAbstractMethod {
        private MethodGetLiveviewFrameInfo() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"frameInfo\":\"bool\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetLiveviewFrameInfoCallbackImpl callbacks = new GetLiveviewFrameInfoCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getLiveviewFrameInfo(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetLiveviewSize extends OrbAbstractMethod {
        private MethodGetLiveviewSize() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetLiveviewSizeCallbackImpl callbacks = new GetLiveviewSizeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getLiveviewSize(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetPostviewImageSize extends OrbAbstractMethod {
        private MethodGetPostviewImageSize() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetPostviewImageSizeCallbackImpl callbacks = new GetPostviewImageSizeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getPostviewImageSize(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSelfTimer extends OrbAbstractMethod {
        private MethodGetSelfTimer() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSelfTimerCallbackImpl callbacks = new GetSelfTimerCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSelfTimer(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetShootMode extends OrbAbstractMethod {
        private MethodGetShootMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetShootModeCallbackImpl callbacks = new GetShootModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getShootMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetShutterSpeed extends OrbAbstractMethod {
        private MethodGetShutterSpeed() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetShutterSpeedCallbackImpl callbacks = new GetShutterSpeedCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getShutterSpeed(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSilentShootingSetting extends OrbAbstractMethod {
        private MethodGetSilentShootingSetting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"silentShooting\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSilentShootingSettingCallbackImpl callbacks = new GetSilentShootingSettingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSilentShootingSetting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetStorageInformation extends OrbAbstractMethod {
        private MethodGetStorageInformation() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"storageID\":\"string\", \"recordTarget\":\"bool\", \"numberOfRecordableImages\":\"int\", \"recordableTime\":\"int\", \"storageDescription\":\"string\"}*"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetStorageInformationCallbackImpl callbacks = new GetStorageInformationCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getStorageInformation(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedCameraFunction extends OrbAbstractMethod {
        private MethodGetSupportedCameraFunction() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedCameraFunctionCallbackImpl callbacks = new GetSupportedCameraFunctionCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedCameraFunction(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedContShootingMode extends OrbAbstractMethod {
        private MethodGetSupportedContShootingMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"candidate\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedContShootingModeCallbackImpl callbacks = new GetSupportedContShootingModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedContShootingMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedContShootingSpeed extends OrbAbstractMethod {
        private MethodGetSupportedContShootingSpeed() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"candidate\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedContShootingSpeedCallbackImpl callbacks = new GetSupportedContShootingSpeedCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedContShootingSpeed(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedExposureCompensation extends OrbAbstractMethod {
        private MethodGetSupportedExposureCompensation() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT_ARRAY, ResponseType.INT_ARRAY, ResponseType.INT_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedExposureCompensationCallbackImpl callbacks = new GetSupportedExposureCompensationCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedExposureCompensation(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedExposureMode extends OrbAbstractMethod {
        private MethodGetSupportedExposureMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedExposureModeCallbackImpl callbacks = new GetSupportedExposureModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedExposureMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedFlashMode extends OrbAbstractMethod {
        private MethodGetSupportedFlashMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedFlashModeCallbackImpl callbacks = new GetSupportedFlashModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedFlashMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedFNumber extends OrbAbstractMethod {
        private MethodGetSupportedFNumber() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedFNumberCallbackImpl callbacks = new GetSupportedFNumberCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedFNumber(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedFocusMode extends OrbAbstractMethod {
        private MethodGetSupportedFocusMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedFocusModeCallbackImpl callbacks = new GetSupportedFocusModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedFocusMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedIsoSpeedRate extends OrbAbstractMethod {
        private MethodGetSupportedIsoSpeedRate() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedIsoSpeedRateCallbackImpl callbacks = new GetSupportedIsoSpeedRateCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedIsoSpeedRate(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedLiveviewSize extends OrbAbstractMethod {
        private MethodGetSupportedLiveviewSize() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedLiveviewSizeCallbackImpl callbacks = new GetSupportedLiveviewSizeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedLiveviewSize(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedPostviewImageSize extends OrbAbstractMethod {
        private MethodGetSupportedPostviewImageSize() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedPostviewImageSizeCallbackImpl callbacks = new GetSupportedPostviewImageSizeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedPostviewImageSize(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedProgramShift extends OrbAbstractMethod {
        private MethodGetSupportedProgramShift() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedProgramShiftCallbackImpl callbacks = new GetSupportedProgramShiftCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedProgramShift(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedSelfTimer extends OrbAbstractMethod {
        private MethodGetSupportedSelfTimer() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedSelfTimerCallbackImpl callbacks = new GetSupportedSelfTimerCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedSelfTimer(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedShootMode extends OrbAbstractMethod {
        private MethodGetSupportedShootMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedShootModeCallbackImpl callbacks = new GetSupportedShootModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedShootMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedShutterSpeed extends OrbAbstractMethod {
        private MethodGetSupportedShutterSpeed() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING_ARRAY};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedShutterSpeedCallbackImpl callbacks = new GetSupportedShutterSpeedCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedShutterSpeed(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedSilentShootingSetting extends OrbAbstractMethod {
        private MethodGetSupportedSilentShootingSetting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"candidate\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedSilentShootingSettingCallbackImpl callbacks = new GetSupportedSilentShootingSettingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedSilentShootingSetting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedWhiteBalance extends OrbAbstractMethod {
        private MethodGetSupportedWhiteBalance() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"whiteBalanceMode\":\"string\", \"colorTemperatureRange\":\"int*\"}*"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedWhiteBalanceCallbackImpl callbacks = new GetSupportedWhiteBalanceCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedWhiteBalance(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetSupportedZoomSetting extends OrbAbstractMethod {
        private MethodGetSupportedZoomSetting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"candidate\":\"string*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetSupportedZoomSettingCallbackImpl callbacks = new GetSupportedZoomSettingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getSupportedZoomSetting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetTouchAFPosition extends OrbAbstractMethod {
        private MethodGetTouchAFPosition() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"set\":\"bool\", \"touchCoordinates\":\"double*\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetTouchAFPositionCallbackImpl callbacks = new GetTouchAFPositionCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getTouchAFPosition(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetWhiteBalance extends OrbAbstractMethod {
        private MethodGetWhiteBalance() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"whiteBalanceMode\":\"string\", \"colorTemperature\":\"int\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetWhiteBalanceCallbackImpl callbacks = new GetWhiteBalanceCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getWhiteBalance(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodGetZoomSetting extends OrbAbstractMethod {
        private MethodGetZoomSetting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{"{\"zoom\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                GetZoomSettingCallbackImpl callbacks = new GetZoomSettingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.getZoomSetting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetCameraFunction extends OrbAbstractMethod {
        private MethodSetCameraFunction() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetCameraFunctionCallbackImpl callbacks = new SetCameraFunctionCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setCameraFunction(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetContShootingMode extends OrbAbstractMethod {
        private MethodSetContShootingMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"contShootingMode\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetContShootingModeCallbackImpl callbacks = new SetContShootingModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setContShootingMode(ContShootingModeParams.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetContShootingSpeed extends OrbAbstractMethod {
        private MethodSetContShootingSpeed() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"contShootingSpeed\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetContShootingSpeedCallbackImpl callbacks = new SetContShootingSpeedCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setContShootingSpeed(ContShootingSpeedParams.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetExposureCompensation extends OrbAbstractMethod {
        private MethodSetExposureCompensation() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetExposureCompensationCallbackImpl callbacks = new SetExposureCompensationCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setExposureCompensation(JsonUtil.getInt(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetExposureMode extends OrbAbstractMethod {
        private MethodSetExposureMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetExposureModeCallbackImpl callbacks = new SetExposureModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setExposureMode(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetFlashMode extends OrbAbstractMethod {
        private MethodSetFlashMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetFlashModeCallbackImpl callbacks = new SetFlashModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setFlashMode(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetFNumber extends OrbAbstractMethod {
        private MethodSetFNumber() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetFNumberCallbackImpl callbacks = new SetFNumberCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setFNumber(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetFocusMode extends OrbAbstractMethod {
        private MethodSetFocusMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetFocusModeCallbackImpl callbacks = new SetFocusModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setFocusMode(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetIsoSpeedRate extends OrbAbstractMethod {
        private MethodSetIsoSpeedRate() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetIsoSpeedRateCallbackImpl callbacks = new SetIsoSpeedRateCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setIsoSpeedRate(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetLiveviewFrameInfo extends OrbAbstractMethod {
        private MethodSetLiveviewFrameInfo() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"frameInfo\":\"bool\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetLiveviewFrameInfoCallbackImpl callbacks = new SetLiveviewFrameInfoCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setLiveviewFrameInfo(LiveviewFrameInfoParams.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetPostviewImageSize extends OrbAbstractMethod {
        private MethodSetPostviewImageSize() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetPostviewImageSizeCallbackImpl callbacks = new SetPostviewImageSizeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setPostviewImageSize(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetProgramShift extends OrbAbstractMethod {
        private MethodSetProgramShift() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetProgramShiftCallbackImpl callbacks = new SetProgramShiftCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setProgramShift(JsonUtil.getInt(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetSelfTimer extends OrbAbstractMethod {
        private MethodSetSelfTimer() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetSelfTimerCallbackImpl callbacks = new SetSelfTimerCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setSelfTimer(JsonUtil.getInt(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetShootMode extends OrbAbstractMethod {
        private MethodSetShootMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetShootModeCallbackImpl callbacks = new SetShootModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setShootMode(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetShutterSpeed extends OrbAbstractMethod {
        private MethodSetShutterSpeed() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetShutterSpeedCallbackImpl callbacks = new SetShutterSpeedCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setShutterSpeed(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetSilentShootingSetting extends OrbAbstractMethod {
        private MethodSetSilentShootingSetting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"silentShooting\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetSilentShootingSettingCallbackImpl callbacks = new SetSilentShootingSettingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setSilentShootingSetting(SilentShootingSettingParams.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetTouchAFPosition extends OrbAbstractMethod {
        private MethodSetTouchAFPosition() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT, "{\"AFResult\":\"bool\", \"AFType\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"double", "double"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 2) {
                SetTouchAFPositionCallbackImpl callbacks = new SetTouchAFPositionCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setTouchAFPosition(JsonUtil.getDouble(params, 0), JsonUtil.getDouble(params, 1), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetWhiteBalance extends OrbAbstractMethod {
        private MethodSetWhiteBalance() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING, ResponseType.BOOL, ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 3) {
                SetWhiteBalanceCallbackImpl callbacks = new SetWhiteBalanceCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setWhiteBalance(JsonUtil.getString(params, 0), JsonUtil.getBoolean(params, 1), JsonUtil.getInt(params, 2), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodSetZoomSetting extends OrbAbstractMethod {
        private MethodSetZoomSetting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{"{\"zoom\":\"string\"}"};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                SetZoomSettingCallbackImpl callbacks = new SetZoomSettingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.setZoomSetting(ZoomSettingParams.Converter.REF.fromJson(JsonUtil.getObject(params, 0)), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStartBulbShooting extends OrbAbstractMethod {
        private MethodStartBulbShooting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StartBulbShootingCallbackImpl callbacks = new StartBulbShootingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.startBulbShooting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStartContShooting extends OrbAbstractMethod {
        private MethodStartContShooting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StartContShootingCallbackImpl callbacks = new StartContShootingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.startContShooting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStartLiveview extends OrbAbstractMethod {
        private MethodStartLiveview() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StartLiveviewCallbackImpl callbacks = new StartLiveviewCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.startLiveview(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStartLiveviewWithSize extends OrbAbstractMethod {
        private MethodStartLiveviewWithSize() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 1) {
                StartLiveviewCallbackImpl callbacks = new StartLiveviewCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.startLiveviewWithSize(JsonUtil.getString(params, 0), callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStartMovieRec extends OrbAbstractMethod {
        private MethodStartMovieRec() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StartMovieRecCallbackImpl callbacks = new StartMovieRecCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.startMovieRec(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStartRecMode extends OrbAbstractMethod {
        private MethodStartRecMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StartRecModeCallbackImpl callbacks = new StartRecModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.startRecMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStopBulbShooting extends OrbAbstractMethod {
        private MethodStopBulbShooting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StopBulbShootingCallbackImpl callbacks = new StopBulbShootingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.stopBulbShooting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStopContShooting extends OrbAbstractMethod {
        private MethodStopContShooting() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StopContShootingCallbackImpl callbacks = new StopContShootingCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.stopContShooting(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStopLiveview extends OrbAbstractMethod {
        private MethodStopLiveview() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StopLiveviewCallbackImpl callbacks = new StopLiveviewCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.stopLiveview(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStopMovieRec extends OrbAbstractMethod {
        private MethodStopMovieRec() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.STRING};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StopMovieRecCallbackImpl callbacks = new StopMovieRecCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.stopMovieRec(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    /* loaded from: classes.dex */
    private class MethodStopRecMode extends OrbAbstractMethod {
        private MethodStopRecMode() {
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] resultTypes() {
            return new String[]{ResponseType.INT};
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public final String[] parameterTypes() {
            return new String[0];
        }

        @Override // com.sony.mexi.orb.service.OrbAbstractMethod
        public void invoke(OrbAbstractClient client, JSONArray params) {
            if (params.length() == 0) {
                StopRecModeCallbackImpl callbacks = new StopRecModeCallbackImpl(client);
                try {
                    SmartRemoteControlServiceBase.this.stopRecMode(callbacks);
                    return;
                } catch (JsonArgumentException e) {
                    client.sendError(Status.ILLEGAL_ARGUMENT);
                    return;
                }
            }
            client.sendError(Status.ILLEGAL_ARGUMENT);
        }
    }

    @Override // com.sony.mexi.orb.service.OrbAbstractVersion
    public final void init(OrbServiceProvider service) {
        addMethod(Name.ACT_HALFPRESSSHUTTER, new MethodActHalfPressShutter());
        addMethod(Name.ACT_TAKE_PICTURE, new MethodActTakePicture());
        addMethod(Name.ACT_ZOOM, new MethodActZoom());
        addMethod(Name.AWAIT_TAKE_PICTURE, new MethodAwaitTakePicture());
        addMethod(Name.CANCEL_HALFPRESSSHUTTER, new MethodCancelHalfPressShutter());
        addMethod(Name.CANCEL_TOUCHAFPOSITION, new MethodCancelTouchAFPosition());
        addMethod(Name.GET_APPLICATION_INFO, new MethodGetApplicationInfo());
        addMethod(Name.GET_AVAILABLE_API_LIST, new MethodGetAvailableApiList());
        addMethod(Name.GET_AVAILABLE_CAMERA_FUNCTION, new MethodGetAvailableCameraFunction());
        addMethod(Name.GET_AVAILABLE_CONSHOOTING_MODE, new MethodGetAvailableContShootingMode());
        addMethod(Name.GET_AVAILABLE_CONSHOOTING_SPEED, new MethodGetAvailableContShootingSpeed());
        addMethod(Name.GET_AVAILABLE_EXPOSURE_COMPENSATION, new MethodGetAvailableExposureCompensation());
        addMethod(Name.GET_AVAILABLE_EXPOSURE_MODE, new MethodGetAvailableExposureMode());
        addMethod(Name.GET_AVAILABLE_FLASH_MODE, new MethodGetAvailableFlashMode());
        addMethod(Name.GET_AVAILABLE_FNUMBER, new MethodGetAvailableFNumber());
        addMethod(Name.GET_AVAILABLE_FOCUS_MODE, new MethodGetAvailableFocusMode());
        addMethod(Name.GET_AVAILABLE_ISOSPEEDRATE, new MethodGetAvailableIsoSpeedRate());
        addMethod(Name.GET_AVAILABLE_LIVEVIEW_SIZE, new MethodGetAvailableLiveviewSize());
        addMethod(Name.GET_AVAILABLE_POSTVIEWIMAGE_SIZE, new MethodGetAvailablePostviewImageSize());
        addMethod(Name.GET_AVAILABLE_SELF_TIMER, new MethodGetAvailableSelfTimer());
        addMethod(Name.GET_AVAILABLE_SHOOTMODE, new MethodGetAvailableShootMode());
        addMethod(Name.GET_AVAILABLE_SHUTTERSPEED, new MethodGetAvailableShutterSpeed());
        addMethod(Name.GET_AVAILABLE_SILENT_SHOOTING, new MethodGetAvailableSilentShootingSetting());
        addMethod(Name.GET_AVAILABLE_WHITEBALANCE, new MethodGetAvailableWhiteBalance());
        addMethod(Name.GET_AVAILABLE_ZOOM_SETTING, new MethodGetAvailableZoomSetting());
        addMethod(Name.GET_CAMERA_FUNCTION, new MethodGetCameraFunction());
        addMethod(Name.GET_CONTSHOOTING_MODE, new MethodGetContShootingMode());
        addMethod(Name.GET_CONTSHOOTING_SPEED, new MethodGetContShootingSpeed());
        addMethod(Name.GET_EVENT, new MethodGetEvent());
        addMethod(Name.GET_EXPOSURE_COMPENSATION, new MethodGetExposureCompensation());
        addMethod(Name.GET_EXPOSURE_MODE, new MethodGetExposureMode());
        addMethod(Name.GET_FLASH_MODE, new MethodGetFlashMode());
        addMethod(Name.GET_FNUMBER, new MethodGetFNumber());
        addMethod(Name.GET_FOCUS_MODE, new MethodGetFocusMode());
        addMethod(Name.GET_ISOSPEEDRATE, new MethodGetIsoSpeedRate());
        addMethod(Name.GET_LIVEVIEW_FRAME_INFO, new MethodGetLiveviewFrameInfo());
        addMethod(Name.GET_LIVEVIEW_SIZE, new MethodGetLiveviewSize());
        addMethod(Name.GET_POSTVIEWIMAGE_SIZE, new MethodGetPostviewImageSize());
        addMethod(Name.GET_SELF_TIMER, new MethodGetSelfTimer());
        addMethod(Name.GET_SHOOTMODE, new MethodGetShootMode());
        addMethod(Name.GET_SHUTTERSPEED, new MethodGetShutterSpeed());
        addMethod(Name.GET_SILENT_SHOOTING, new MethodGetSilentShootingSetting());
        addMethod(Name.GET_STORAGE_INFORMATION, new MethodGetStorageInformation());
        addMethod(Name.GET_SUPPORTED_CAMERA_FUNCTION, new MethodGetSupportedCameraFunction());
        addMethod(Name.GET_SUPPORTED_CONSHOOTING_MODE, new MethodGetSupportedContShootingMode());
        addMethod(Name.GET_SUPPORTED_CONSHOOTING_SPEED, new MethodGetSupportedContShootingSpeed());
        addMethod(Name.GET_SUPPORTED_EXPOSURE_COMPENSATION, new MethodGetSupportedExposureCompensation());
        addMethod(Name.GET_SUPPORTED_EXPOSURE_MODE, new MethodGetSupportedExposureMode());
        addMethod(Name.GET_SUPPORTED_FLASH_MODE, new MethodGetSupportedFlashMode());
        addMethod(Name.GET_SUPPORTED_FNUMBER, new MethodGetSupportedFNumber());
        addMethod(Name.GET_SUPPORTED_FOCUS_MODE, new MethodGetSupportedFocusMode());
        addMethod(Name.GET_SUPPORTED_ISOSPEEDRATE, new MethodGetSupportedIsoSpeedRate());
        addMethod(Name.GET_SUPPORTED_LIVEVIEW_SIZE, new MethodGetSupportedLiveviewSize());
        addMethod(Name.GET_SUPPORTED_POSTVIEWIMAGE_SIZE, new MethodGetSupportedPostviewImageSize());
        addMethod(Name.GET_SUPPORTED_PROGRAMSHIFT, new MethodGetSupportedProgramShift());
        addMethod(Name.GET_SUPPORTED_SELF_TIMER, new MethodGetSupportedSelfTimer());
        addMethod(Name.GET_SUPPORTED_SHOOTMODE, new MethodGetSupportedShootMode());
        addMethod(Name.GET_SUPPORTED_SHUTTERSPEED, new MethodGetSupportedShutterSpeed());
        addMethod(Name.GET_SUPPORTED_SILENT_SHOOTING, new MethodGetSupportedSilentShootingSetting());
        addMethod(Name.GET_SUPPORTED_WHITEBALANCE, new MethodGetSupportedWhiteBalance());
        addMethod(Name.GET_SUPPORTED_ZOOM_SETTING, new MethodGetSupportedZoomSetting());
        addMethod(Name.GET_TOUCHAFPOSITION, new MethodGetTouchAFPosition());
        addMethod(Name.GET_WHITEBALANCE, new MethodGetWhiteBalance());
        addMethod(Name.GET_ZOOM_SETTING, new MethodGetZoomSetting());
        addMethod(Name.SET_CAMERA_FUNCTION, new MethodSetCameraFunction());
        addMethod(Name.SET_CONTSHOOTING_MODE, new MethodSetContShootingMode());
        addMethod(Name.SET_CONTSHOOTING_SPEED, new MethodSetContShootingSpeed());
        addMethod(Name.SET_EXPOSURE_COMPENSATION, new MethodSetExposureCompensation());
        addMethod(Name.SET_EXPOSURE_MODE, new MethodSetExposureMode());
        addMethod(Name.SET_FLASH_MODE, new MethodSetFlashMode());
        addMethod(Name.SET_FNUMBER, new MethodSetFNumber());
        addMethod("setFocusMode", new MethodSetFocusMode());
        addMethod(Name.SET_ISOSPEEDRATE, new MethodSetIsoSpeedRate());
        addMethod(Name.SET_LIVEVIEW_FRAME_INFO, new MethodSetLiveviewFrameInfo());
        addMethod(Name.SET_POSTVIEWIMAGE_SIZE, new MethodSetPostviewImageSize());
        addMethod(Name.SET_PROGRAMSHIFT, new MethodSetProgramShift());
        addMethod(Name.SET_SELF_TIMER, new MethodSetSelfTimer());
        addMethod(Name.SET_SHOOTMODE, new MethodSetShootMode());
        addMethod(Name.SET_SHUTTERSPEED, new MethodSetShutterSpeed());
        addMethod(Name.SET_SILENT_SHOOTING, new MethodSetSilentShootingSetting());
        addMethod(Name.SET_TOUCHAFPOSITION, new MethodSetTouchAFPosition());
        addMethod(Name.SET_WHITEBALANCE, new MethodSetWhiteBalance());
        addMethod(Name.SET_ZOOM_SETTING, new MethodSetZoomSetting());
        addMethod(Name.START_BULB_SHOOTING, new MethodStartBulbShooting());
        addMethod(Name.START_CONTSHOOTING, new MethodStartContShooting());
        addMethod(Name.START_LIVEVIEW, new MethodStartLiveview());
        addMethod(Name.START_LIVEVIEW_WITH_SIZE, new MethodStartLiveviewWithSize());
        addMethod(Name.START_MOVIE_REC, new MethodStartMovieRec());
        addMethod(Name.START_REC_MODE, new MethodStartRecMode());
        addMethod(Name.STOP_BULB_SHOOTING, new MethodStopBulbShooting());
        addMethod(Name.STOP_CONTSHOOTING, new MethodStopContShooting());
        addMethod(Name.STOP_LIVEVIEW, new MethodStopLiveview());
        addMethod("stopMovieRec", new MethodStopMovieRec());
        addMethod(Name.STOP_REC_MODE, new MethodStopRecMode());
    }
}
