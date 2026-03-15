package com.sony.imaging.app.srctrl.webapi.availability;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFocusMode;
import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.util.PfBugAvailability;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventFNumberParams;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventZoomSettingParams;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public class AvailabilityDetector {
    private static final String FALSE_STR = ": false";
    private static final String IS_AVAILABLE = "isAvailable ";
    private static final String TRUE_STR = ": true";
    private static final String TAG = AvailabilityDetector.class.getSimpleName();
    private static final String[] PUBLIC_API = {"guide/getServiceProtocols", "guide/getVersions", "guide/getMethodTypes", "camera/getVersions", "camera/getMethodTypes", "accessControl/getVersions", "accessControl/getMethodTypes", "avContent/getVersions", "avContent/getMethodTypes", "camera/getApplicationInfo", "camera/getAvailableApiList", "camera/setShootMode", "camera/getShootMode", "camera/getSupportedShootMode", "camera/getAvailableShootMode", "camera/setFlashMode", "camera/getFlashMode", "camera/getSupportedFlashMode", "camera/getAvailableFlashMode", "camera/setSelfTimer", "camera/getSelfTimer", "camera/getSupportedSelfTimer", "camera/getAvailableSelfTimer", "camera/setExposureCompensation", "camera/getExposureCompensation", "camera/getSupportedExposureCompensation", "camera/getAvailableExposureCompensation", "camera/setPostviewImageSize", "camera/getPostviewImageSize", "camera/getSupportedPostviewImageSize", "camera/getAvailablePostviewImageSize", "camera/setTouchAFPosition", "camera/getTouchAFPosition", "camera/cancelTouchAFPosition", "camera/setFNumber", "camera/getFNumber", "camera/getSupportedFNumber", "camera/getAvailableFNumber", "camera/setShutterSpeed", "camera/getShutterSpeed", "camera/getSupportedShutterSpeed", "camera/getAvailableShutterSpeed", "camera/setIsoSpeedRate", "camera/getIsoSpeedRate", "camera/getSupportedIsoSpeedRate", "camera/getAvailableIsoSpeedRate", "camera/setExposureMode", "camera/getExposureMode", "camera/getSupportedExposureMode", "camera/getAvailableExposureMode", "camera/setWhiteBalance", "camera/getWhiteBalance", "camera/getSupportedWhiteBalance", "camera/getAvailableWhiteBalance", "camera/setProgramShift", "camera/getSupportedProgramShift", "camera/setZoomSetting", "camera/getZoomSetting", "camera/getSupportedZoomSetting", "camera/getAvailableZoomSetting", "camera/setContShootingMode", "camera/getContShootingMode", "camera/getSupportedContShootingMode", "camera/getAvailableContShootingMode", "camera/setContShootingSpeed", "camera/getContShootingSpeed", "camera/getSupportedContShootingSpeed", "camera/getAvailableContShootingSpeed", "camera/startRecMode", "camera/stopRecMode", "camera/startLiveview", "camera/startLiveviewWithSize", "camera/stopLiveview", "camera/getLiveviewSize", "camera/getSupportedLiveviewSize", "camera/getAvailableLiveviewSize", "camera/setLiveviewFrameInfo", "camera/getLiveviewFrameInfo", "camera/actTakePicture", "camera/awaitTakePicture", "camera/startContShooting", "camera/stopContShooting", "camera/startBulbShooting", "camera/stopBulbShooting", "camera/startMovieRec", "camera/stopMovieRec", "camera/actZoom", "camera/setFocusMode", "camera/getFocusMode", "camera/getSupportedFocusMode", "camera/getAvailableFocusMode", "camera/actHalfPressShutter", "camera/cancelHalfPressShutter", "camera/getStorageInformation", "camera/getEvent", "camera/setCameraFunction", "camera/getCameraFunction", "camera/getSupportedCameraFunction", "camera/getAvailableCameraFunction", "camera/setSilentShootingSetting", "camera/getSilentShootingSetting", "camera/getSupportedSilentShootingSetting", "camera/getAvailableSilentShootingSetting", "avContent/getSchemeList", "avContent/getSourceList", "avContent/getContentCount", "avContent/getContentList", "avContent/setStreamingContent", "avContent/startStreaming", "avContent/pauseStreaming", "avContent/seekStreamingPosition", "avContent/stopStreaming", "avContent/requestToNotifyStreamingStatus", "avContent/deleteContent", "accessControl/actEnableMethods"};

    static {
        Arrays.sort(PUBLIC_API);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0077 A[Catch: all -> 0x00da, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0009, B:11:0x000e, B:13:0x003a, B:14:0x003f, B:15:0x0047, B:16:0x004a, B:17:0x0068, B:18:0x006d, B:20:0x0077, B:21:0x00aa, B:22:0x00dd, B:23:0x00e2, B:24:0x00e8, B:25:0x00ee, B:26:0x00f5, B:27:0x00fb, B:28:0x0101, B:29:0x0107, B:30:0x010d, B:31:0x0114, B:32:0x011b, B:33:0x0122, B:34:0x0128, B:35:0x012f, B:36:0x0136), top: B:3:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static synchronized java.lang.String[] getAvailables(boolean r10) {
        /*
            Method dump skipped, instructions count: 370
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.srctrl.webapi.availability.AvailabilityDetector.getAvailables(boolean):java.lang.String[]");
    }

    private static String[] getAvailablesInPreparation(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.START_REC_MODE);
        availableList.add(Name.STOP_REC_MODE);
        addCameraFunction(includeSetter, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingEe(ArrayList<String> availableList, boolean includeSetter) {
        addActTakePicture(availableList);
        addStartBulbShooting(availableList);
        addStartContShooting(availableList);
        addStopContShooting(availableList);
        availableList.add(Name.STOP_REC_MODE);
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addCameraFunction(includeSetter, availableList);
        addActZoom(availableList);
        addAwaitTakePicture(availableList);
        addHalfPressShutter(availableList);
        addSetSelfTimer(includeSetter, availableList);
        addSetContShootingMode(includeSetter, availableList);
        addSetContShootingSpeed(includeSetter, availableList);
        addExposureMode(includeSetter, availableList);
        addExposureCompensation(includeSetter, availableList);
        addFNumber(includeSetter, availableList);
        addIsoNumber(includeSetter, availableList);
        addLiveviewSize(includeSetter, availableList);
        addPostviewImageSize(includeSetter, availableList);
        addProgramShift(includeSetter, availableList);
        addShootMode(includeSetter, availableList);
        addShutterSpeed(includeSetter, availableList);
        addTouchAFPosition(includeSetter, availableList);
        addWhiteBalance(includeSetter, availableList);
        addFlashMode(includeSetter, availableList);
        addFocusMode(includeSetter, availableList);
        addZoomSetting(includeSetter, availableList);
        addStorageInformation(includeSetter, availableList);
        addLiveviewFrameInfo(includeSetter, availableList);
        addSilentShooting(includeSetter, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingMenu(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.STOP_REC_MODE);
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addActZoom(availableList);
        addAwaitTakePicture(availableList);
        addSetSelfTimer(false, availableList);
        addSetContShootingMode(false, availableList);
        addSetContShootingSpeed(false, availableList);
        addExposureMode(false, availableList);
        addExposureCompensation(false, availableList);
        addFNumber(false, availableList);
        addIsoNumber(false, availableList);
        addLiveviewSize(false, availableList);
        addPostviewImageSize(false, availableList);
        addProgramShift(false, availableList);
        addShootMode(false, availableList);
        addShutterSpeed(false, availableList);
        addWhiteBalance(false, availableList);
        addFlashMode(false, availableList);
        addFocusMode(false, availableList);
        addZoomSetting(false, availableList);
        addStorageInformation(false, availableList);
        addLiveviewFrameInfo(false, availableList);
        addSilentShooting(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingStillCapturing(ArrayList<String> availableList, boolean includeSetter) {
        if (!isBlibShootingMode()) {
            availableList.add(Name.START_LIVEVIEW);
            availableList.add(Name.STOP_LIVEVIEW);
            availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        }
        addStartContShooting(availableList);
        return _getAvailablesInShootingInhibit(availableList, includeSetter);
    }

    private static String[] getAvailablesInShootingInhibit(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addActZoom(availableList);
        return _getAvailablesInShootingInhibit(availableList, includeSetter);
    }

    private static String[] _getAvailablesInShootingInhibit(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.STOP_REC_MODE);
        addStopBulbShooting(availableList);
        addStopContShooting(availableList);
        addAwaitTakePicture(availableList);
        addSetSelfTimer(false, availableList);
        addSetContShootingMode(false, availableList);
        addSetContShootingSpeed(false, availableList);
        addExposureMode(false, availableList);
        addExposureCompensation(false, availableList);
        addFNumber(false, availableList);
        addIsoNumber(false, availableList);
        addLiveviewSize(false, availableList);
        addPostviewImageSize(false, availableList);
        addProgramShift(false, availableList);
        addShootMode(false, availableList);
        addShutterSpeed(false, availableList);
        addWhiteBalance(false, availableList);
        addFlashMode(false, availableList);
        addFocusMode(false, availableList);
        addZoomSetting(false, availableList);
        addStorageInformation(false, availableList);
        addLiveviewFrameInfo(false, availableList);
        addSilentShooting(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    protected static String[] getAvailablesInContentsTransfer(ArrayList<String> availableList, boolean includeSetter) {
        addCameraFunction(includeSetter, availableList);
        addStorageInformation(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    protected static String[] getAvailablesInDeleting(ArrayList<String> availableList, boolean includeSetter) {
        addStorageInformation(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    protected static String[] getAvailablesInStreaming(ArrayList<String> availableList, boolean includeSetter) {
        addStorageInformation(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingShootingFocusing(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addCameraFunction(includeSetter, availableList);
        addActTakePicture(availableList);
        addStartBulbShooting(availableList);
        addStartContShooting(availableList);
        addStopContShooting(availableList);
        addAwaitTakePicture(availableList);
        addActZoom(availableList);
        addHalfPressShutter(availableList);
        addSetSelfTimer(false, availableList);
        addSetContShootingMode(false, availableList);
        addSetContShootingSpeed(false, availableList);
        addExposureMode(false, availableList);
        addExposureCompensation(false, availableList);
        addFNumber(false, availableList);
        addIsoNumber(false, availableList);
        addLiveviewSize(false, availableList);
        addPostviewImageSize(false, availableList);
        addProgramShift(false, availableList);
        addShootMode(false, availableList);
        addShutterSpeed(false, availableList);
        addWhiteBalance(false, availableList);
        addFlashMode(false, availableList);
        addFocusMode(false, availableList);
        addZoomSetting(false, availableList);
        addStorageInformation(false, availableList);
        addLiveviewFrameInfo(false, availableList);
        addSilentShooting(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingRemoteTouchAf(ArrayList<String> availableList, boolean includeSetter) {
        getAvailablesInShootingShootingFocusing(availableList, includeSetter);
        addTouchAFPosition(includeSetter, availableList);
        availableList.add(Name.CANCEL_TOUCHAFPOSITION);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingRemoteTouchAfAssist(ArrayList<String> availableList, boolean includeSetter) {
        getAvailablesInShootingShootingFocusing(availableList, includeSetter);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingMovieEe(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.STOP_REC_MODE);
        availableList.add(Name.START_MOVIE_REC);
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addCameraFunction(includeSetter, availableList);
        addActZoom(availableList);
        addShootMode(includeSetter, availableList);
        addExposureMode(includeSetter, availableList);
        addExposureCompensation(includeSetter, availableList);
        addFNumber(includeSetter, availableList);
        addIsoNumber(includeSetter, availableList);
        addLiveviewSize(includeSetter, availableList);
        addShutterSpeed(includeSetter, availableList);
        addWhiteBalance(includeSetter, availableList);
        addFocusMode(includeSetter, availableList);
        addZoomSetting(includeSetter, availableList);
        addStorageInformation(includeSetter, availableList);
        addLiveviewFrameInfo(includeSetter, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingMovieRecording(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.STOP_REC_MODE);
        availableList.add("stopMovieRec");
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addActZoom(availableList);
        addShootMode(false, availableList);
        addExposureMode(false, availableList);
        addExposureCompensation(includeSetter, availableList);
        addFNumber(includeSetter, availableList);
        addIsoNumber(includeSetter, availableList);
        addLiveviewSize(includeSetter, availableList);
        addShutterSpeed(includeSetter, availableList);
        addWhiteBalance(false, availableList);
        addFocusMode(false, availableList);
        addZoomSetting(false, availableList);
        addStorageInformation(false, availableList);
        addLiveviewFrameInfo(includeSetter, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingMovieRecordingMenu(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.STOP_REC_MODE);
        availableList.add("stopMovieRec");
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addShootMode(false, availableList);
        addExposureMode(false, availableList);
        addExposureCompensation(false, availableList);
        addFNumber(false, availableList);
        addIsoNumber(false, availableList);
        addLiveviewSize(false, availableList);
        addShutterSpeed(false, availableList);
        addWhiteBalance(false, availableList);
        addFocusMode(false, availableList);
        addStorageInformation(false, availableList);
        addLiveviewFrameInfo(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingMovieInhibit(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.STOP_REC_MODE);
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addActZoom(availableList);
        addShootMode(false, availableList);
        addExposureMode(false, availableList);
        addExposureCompensation(false, availableList);
        addFNumber(false, availableList);
        addIsoNumber(false, availableList);
        addLiveviewSize(false, availableList);
        addShutterSpeed(false, availableList);
        addWhiteBalance(false, availableList);
        addFocusMode(false, availableList);
        addStorageInformation(false, availableList);
        addLiveviewFrameInfo(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String[] getAvailablesInShootingMovieMenu(ArrayList<String> availableList, boolean includeSetter) {
        availableList.add(Name.STOP_REC_MODE);
        availableList.add(Name.START_LIVEVIEW);
        availableList.add(Name.STOP_LIVEVIEW);
        availableList.add(Name.START_LIVEVIEW_WITH_SIZE);
        addShootMode(false, availableList);
        addExposureMode(false, availableList);
        addExposureCompensation(false, availableList);
        addFNumber(false, availableList);
        addIsoNumber(false, availableList);
        addLiveviewSize(false, availableList);
        addShutterSpeed(false, availableList);
        addWhiteBalance(false, availableList);
        addFocusMode(false, availableList);
        addZoomSetting(false, availableList);
        addStorageInformation(false, availableList);
        addLiveviewFrameInfo(false, availableList);
        return (String[]) availableList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static boolean isContShootingMode() {
        String contShootingMode = ParamsGenerator.peekContShootingModeParams().contShootingMode;
        return (contShootingMode == null || "Single".equals(contShootingMode)) ? false : true;
    }

    private static boolean isBlibShootingMode() {
        String shtterSpeed;
        return (ParamsGenerator.peekShutterSpeedSnapshot() == null || (shtterSpeed = ParamsGenerator.peekShutterSpeedSnapshot().currentShutterSpeed) == null || !"BULB".equals(shtterSpeed)) ? false : true;
    }

    private static void addActTakePicture(ArrayList<String> availableList) {
        if (StateController.RecordingMode.STILL == StateController.getInstance().getRecordingMode() && !isContShootingMode() && !isBlibShootingMode()) {
            availableList.add(Name.ACT_TAKE_PICTURE);
        }
    }

    private static void addStartBulbShooting(ArrayList<String> availableList) {
        if (StateController.RecordingMode.STILL == StateController.getInstance().getRecordingMode() && !isContShootingMode() && isBlibShootingMode()) {
            availableList.add(Name.START_BULB_SHOOTING);
        }
    }

    private static void addStopBulbShooting(ArrayList<String> availableList) {
        if (StateController.RecordingMode.STILL == StateController.getInstance().getRecordingMode() && !isContShootingMode() && isBlibShootingMode()) {
            availableList.add(Name.STOP_BULB_SHOOTING);
        }
    }

    private static void addStartContShooting(ArrayList<String> availableList) {
        if (StateController.RecordingMode.STILL == StateController.getInstance().getRecordingMode() && isContShootingMode()) {
            availableList.add(Name.START_CONTSHOOTING);
        }
    }

    private static void addStopContShooting(ArrayList<String> availableList) {
        if (StateController.RecordingMode.STILL == StateController.getInstance().getRecordingMode() && isContShootingMode()) {
            availableList.add(Name.STOP_CONTSHOOTING);
        }
    }

    private static void addExposureCompensation(boolean includeSetter, ArrayList<String> availableList) {
        String isoNumber;
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null) {
            Log.e(TAG, "The current exposure mode is unknown.");
            return;
        }
        if (includeSetter) {
            boolean bSet = false;
            if (CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(exposureMode) || "Aperture".equals(exposureMode) || "Shutter".equals(exposureMode)) {
                bSet = true;
            } else if ("Manual".equals(exposureMode) && (isoNumber = ParamsGenerator.peekIsoSpeedRateParamsSnapshot().currentIsoSpeedRate) != null && "AUTO".equals(isoNumber)) {
                bSet = true;
            }
            if (bSet) {
                availableList.add(Name.SET_EXPOSURE_COMPENSATION);
            }
        }
        availableList.add(Name.GET_EXPOSURE_COMPENSATION);
        availableList.add(Name.GET_AVAILABLE_EXPOSURE_COMPENSATION);
        availableList.add(Name.GET_SUPPORTED_EXPOSURE_COMPENSATION);
    }

    private static void addSetSelfTimer(boolean includeSetter, ArrayList<String> availableList) {
        if (ParamsGenerator.s_SELFTIMER_VALUE_INITIALIZED) {
            if (includeSetter) {
                availableList.add(Name.SET_SELF_TIMER);
            }
            availableList.add(Name.GET_SELF_TIMER);
            availableList.add(Name.GET_AVAILABLE_SELF_TIMER);
        }
        availableList.add(Name.GET_SUPPORTED_SELF_TIMER);
    }

    private static void addSetContShootingMode(boolean includeSetter, ArrayList<String> availableList) {
        if (ParamsGenerator.s_CONTSHOOTINGMODE_VALUE_INITIALIZED) {
            if (includeSetter) {
                availableList.add(Name.SET_CONTSHOOTING_MODE);
            }
            availableList.add(Name.GET_CONTSHOOTING_MODE);
            availableList.add(Name.GET_AVAILABLE_CONSHOOTING_MODE);
        }
        availableList.add(Name.GET_SUPPORTED_CONSHOOTING_MODE);
    }

    private static void addSetContShootingSpeed(boolean includeSetter, ArrayList<String> availableList) {
        String contShootingMode = ParamsGenerator.peekContShootingModeParams().contShootingMode;
        String[] contShootingSpeedcandidate = ParamsGenerator.peekContShootingSpeed().candidate;
        if (!"Single".equals(contShootingMode) && contShootingSpeedcandidate != null && contShootingSpeedcandidate.length > 0 && ParamsGenerator.s_CONTSHOOTINGSPEED_VALUE_INITIALIZED) {
            availableList.add(Name.SET_CONTSHOOTING_SPEED);
            availableList.add(Name.GET_CONTSHOOTING_SPEED);
            availableList.add(Name.GET_AVAILABLE_CONSHOOTING_SPEED);
        }
        availableList.add(Name.GET_SUPPORTED_CONSHOOTING_SPEED);
    }

    private static void addAwaitTakePicture(ArrayList<String> availableList) {
        ShootingHandler.ShootingStatus status = ShootingHandler.getInstance().getShootingStatus();
        if (ShootingHandler.ShootingStatus.FINISHED.equals(status) || ShootingHandler.ShootingStatus.PROCESSING.equals(status) || ShootingHandler.ShootingStatus.DEVELOPING.equals(status)) {
            availableList.add(Name.AWAIT_TAKE_PICTURE);
        }
    }

    private static void addExposureMode(boolean includeSetter, ArrayList<String> availableList) {
        String[] exposureModeCandidates = ParamsGenerator.peekExposureModeParamsSnapshot().exposureModeCandidates;
        if (exposureModeCandidates != null && exposureModeCandidates.length > 1) {
            if (includeSetter) {
                availableList.add(Name.SET_EXPOSURE_MODE);
            }
            availableList.add(Name.GET_AVAILABLE_EXPOSURE_MODE);
        }
        availableList.add(Name.GET_EXPOSURE_MODE);
        availableList.add(Name.GET_SUPPORTED_EXPOSURE_MODE);
    }

    private static void addFNumber(boolean includeSetter, ArrayList<String> availableList) {
        GetEventFNumberParams fnumberParams;
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null) {
            Log.e(TAG, "The current exposure mode is unknown.");
            return;
        }
        if (includeSetter && (("Aperture".equals(exposureMode) || "Manual".equals(exposureMode)) && (fnumberParams = ParamsGenerator.peekFNumberParamsSnapshot()) != null && fnumberParams.fNumberCandidates != null && fnumberParams.fNumberCandidates.length > 0)) {
            availableList.add(Name.SET_FNUMBER);
        }
        availableList.add(Name.GET_FNUMBER);
        availableList.add(Name.GET_AVAILABLE_FNUMBER);
        availableList.add(Name.GET_SUPPORTED_FNUMBER);
    }

    private static void addIsoNumber(boolean includeSetter, ArrayList<String> availableList) {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null) {
            Log.e(TAG, "The current exposure mode is unknown.");
            return;
        }
        if (includeSetter && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            availableList.add(Name.SET_ISOSPEEDRATE);
        }
        availableList.add(Name.GET_ISOSPEEDRATE);
        availableList.add(Name.GET_AVAILABLE_ISOSPEEDRATE);
        availableList.add(Name.GET_SUPPORTED_ISOSPEEDRATE);
    }

    private static void addLiveviewSize(boolean includeSetter, ArrayList<String> availableList) {
        if (includeSetter) {
        }
        availableList.add(Name.GET_LIVEVIEW_SIZE);
        availableList.add(Name.GET_AVAILABLE_LIVEVIEW_SIZE);
        availableList.add(Name.GET_SUPPORTED_LIVEVIEW_SIZE);
    }

    private static void addPostviewImageSize(boolean includeSetter, ArrayList<String> availableList) {
        if (includeSetter) {
            availableList.add(Name.SET_POSTVIEWIMAGE_SIZE);
        }
        availableList.add(Name.GET_POSTVIEWIMAGE_SIZE);
        availableList.add(Name.GET_AVAILABLE_POSTVIEWIMAGE_SIZE);
        availableList.add(Name.GET_SUPPORTED_POSTVIEWIMAGE_SIZE);
    }

    private static void addProgramShift(boolean includeSetter, ArrayList<String> availableList) {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null) {
            Log.e(TAG, "The current exposure mode is unknown.");
            return;
        }
        if (includeSetter && CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(exposureMode)) {
            availableList.add(Name.SET_PROGRAMSHIFT);
        }
        availableList.add(Name.GET_SUPPORTED_PROGRAMSHIFT);
    }

    private static void addShootMode(boolean includeSetter, ArrayList<String> availableList) {
        if (includeSetter) {
            availableList.add(Name.SET_SHOOTMODE);
        }
        availableList.add(Name.GET_SHOOTMODE);
        availableList.add(Name.GET_AVAILABLE_SHOOTMODE);
        availableList.add(Name.GET_SUPPORTED_SHOOTMODE);
    }

    private static void addShutterSpeed(boolean includeSetter, ArrayList<String> availableList) {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null) {
            Log.e(TAG, "The current exposure mode is unknown.");
            return;
        }
        if (includeSetter && ("Shutter".equals(exposureMode) || "Manual".equals(exposureMode))) {
            availableList.add(Name.SET_SHUTTERSPEED);
        }
        availableList.add(Name.GET_SHUTTERSPEED);
        availableList.add(Name.GET_AVAILABLE_SHUTTERSPEED);
        availableList.add(Name.GET_SUPPORTED_SHUTTERSPEED);
    }

    private static void addTouchAFPosition(boolean includeSetter, ArrayList<String> availableList) {
        boolean matched = false;
        String[] availableAfArea = ParamsGenerator.peekFocusAreaParamsSnapshot().focusAreaCandidates;
        int len$ = availableAfArea.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            String available = availableAfArea[i$];
            if (!"flex-spot".equals(available)) {
                i$++;
            } else {
                matched = true;
                break;
            }
        }
        if (!matched) {
            Log.e(TAG, "The flexible spot af is not available.");
            return;
        }
        String focusMode = ParamsGenerator.peekFocusModeParamsSnapshot().currentFocusMode;
        if (focusMode == null) {
            Log.e(TAG, "The current focus mode is unknown.");
            return;
        }
        if (CameraOperationFocusMode.FOCUS_MODE_MF.equals(focusMode)) {
            Log.v(TAG, "MF disables Touch AF.");
            return;
        }
        FocusModeController fmc = FocusModeController.getInstance();
        if (fmc.isFocusHeld()) {
            Log.v(TAG, "FoucusHeld disables Touch AF.");
        } else {
            if (ParamsGenerator.isFocusControl()) {
                Log.v(TAG, "isFocusControl == true. disables Touch AF.");
                return;
            }
            if (includeSetter) {
                availableList.add(Name.SET_TOUCHAFPOSITION);
            }
            availableList.add(Name.GET_TOUCHAFPOSITION);
        }
    }

    private static void addWhiteBalance(boolean includeSetter, ArrayList<String> availableList) {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null) {
            Log.e(TAG, "The current exposure mode is unknown.");
            return;
        }
        if (includeSetter && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            availableList.add(Name.SET_WHITEBALANCE);
        }
        availableList.add(Name.GET_WHITEBALANCE);
        availableList.add(Name.GET_SUPPORTED_WHITEBALANCE);
        availableList.add(Name.GET_AVAILABLE_WHITEBALANCE);
    }

    private static void addFlashMode(boolean includeSetter, ArrayList<String> availableList) {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null) {
            Log.e(TAG, "The current exposure mode is unknown.");
            return;
        }
        String[] flashModeCandidates = ParamsGenerator.peekFlashModeParamsSnapshot().flashModeCandidates;
        if (flashModeCandidates != null && flashModeCandidates.length > 0) {
            if (includeSetter) {
                availableList.add(Name.SET_FLASH_MODE);
            }
            availableList.add(Name.GET_FLASH_MODE);
            availableList.add(Name.GET_AVAILABLE_FLASH_MODE);
        }
        availableList.add(Name.GET_SUPPORTED_FLASH_MODE);
    }

    private static void addActZoom(ArrayList<String> availableList) {
        int zoomNumberBox = ParamsGenerator.peekZoomInformationParamsSnapshot().zoomNumberBox.intValue();
        if (zoomNumberBox > 0) {
            availableList.add(Name.ACT_ZOOM);
        }
    }

    protected static void addStorageInformation(boolean includeSetter, ArrayList<String> availableList) {
        String storageID = ParamsGenerator.peekStorageInformationParamsSnapshot()[0].storageID;
        if (storageID != null) {
            availableList.add(Name.GET_STORAGE_INFORMATION);
        }
    }

    protected static void addZoomSetting(boolean includeSetter, ArrayList<String> availableList) {
        GetEventZoomSettingParams getEventZoomSettingParams = ParamsGenerator.peekZoomSettingParamsSnapshot();
        if (getEventZoomSettingParams == null) {
            Log.e(TAG, "The current getEventZoomSettingParams is null.");
            return;
        }
        String[] zoomSettingCandidates = getEventZoomSettingParams.candidate;
        Log.v(TAG, "zoomSettingCandidates.length" + zoomSettingCandidates.length);
        if (zoomSettingCandidates != null && zoomSettingCandidates.length > 0) {
            if (includeSetter) {
                availableList.add(Name.SET_ZOOM_SETTING);
            }
            availableList.add(Name.GET_AVAILABLE_ZOOM_SETTING);
            availableList.add(Name.GET_ZOOM_SETTING);
        }
        availableList.add(Name.GET_SUPPORTED_ZOOM_SETTING);
    }

    private static void addFocusMode(boolean includeSetter, ArrayList<String> availableList) {
        String currentFocusMode = ParamsGenerator.peekFocusModeParamsSnapshot().currentFocusMode;
        if (currentFocusMode != null) {
            String[] focusModeCandidates = ParamsGenerator.peekFocusModeParamsSnapshot().focusModeCandidates;
            if (focusModeCandidates != null && focusModeCandidates.length > 0 && includeSetter) {
                availableList.add("setFocusMode");
            }
            availableList.add(Name.GET_FOCUS_MODE);
            availableList.add(Name.GET_AVAILABLE_FOCUS_MODE);
        }
        availableList.add(Name.GET_SUPPORTED_FOCUS_MODE);
    }

    private static void addCameraFunction(boolean includeSetter, ArrayList<String> availableList) {
        if (!PfBugAvailability.encodeAtPlay) {
            if (includeSetter) {
                availableList.add(Name.SET_CAMERA_FUNCTION);
            }
            availableList.add(Name.GET_CAMERA_FUNCTION);
            availableList.add(Name.GET_AVAILABLE_CAMERA_FUNCTION);
        }
    }

    private static void addHalfPressShutter(ArrayList<String> availableList) {
        availableList.add(Name.ACT_HALFPRESSSHUTTER);
        availableList.add(Name.CANCEL_HALFPRESSSHUTTER);
    }

    private static void addLiveviewFrameInfo(boolean includeSetter, ArrayList<String> availableList) {
        availableList.add(Name.SET_LIVEVIEW_FRAME_INFO);
        availableList.add(Name.GET_LIVEVIEW_FRAME_INFO);
    }

    private static void addSilentShooting(boolean includeSetter, ArrayList<String> availableList) {
        String[] SilentModeCandidates = ParamsGenerator.peekFSilentShootingParamsSnapshot().candidate;
        if (SilentModeCandidates != null && SilentModeCandidates.length >= 2) {
            if (includeSetter) {
                availableList.add(Name.SET_SILENT_SHOOTING);
            }
            availableList.add(Name.GET_SILENT_SHOOTING);
            availableList.add(Name.GET_AVAILABLE_SILENT_SHOOTING);
            availableList.add(Name.GET_SUPPORTED_SILENT_SHOOTING);
        }
    }

    public static synchronized boolean isAvailable(String name) {
        String[] availables;
        boolean z = true;
        synchronized (AvailabilityDetector.class) {
            if (name.startsWith(Name.PREFIX_SET) || name.startsWith(Name.PREFIX_GET_AVAILABLE)) {
                availables = getAvailables(true);
            } else {
                availables = getAvailables(false);
            }
            String[] arr$ = availables;
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ < len$) {
                    String value = arr$[i$];
                    if (!name.equals(value)) {
                        i$++;
                    } else {
                        Log.v(TAG, IS_AVAILABLE + name + " in " + StateController.getInstance().getAppCondition() + TRUE_STR);
                        break;
                    }
                } else {
                    Log.v(TAG, IS_AVAILABLE + name + " in " + StateController.getInstance().getAppCondition() + FALSE_STR);
                    z = false;
                    break;
                }
            }
        }
        return z;
    }

    public static ArrayList<String> getPrivateApiList() {
        ArrayList<String> privateApiList = new ArrayList<>();
        String[] arr$ = Name.s_DEFAULT_API_LIST;
        for (String api : arr$) {
            String apiValue = "camera/" + api;
            int result = Arrays.binarySearch(PUBLIC_API, apiValue, Name.s_COMP);
            if (result < 0) {
                privateApiList.add(apiValue);
            }
        }
        String[] arr$2 = Name.s_AV_CONTENT_LIST;
        for (String api2 : arr$2) {
            String apiValue2 = "avContent/" + api2;
            int result2 = Arrays.binarySearch(PUBLIC_API, apiValue2, Name.s_COMP);
            if (result2 < 0) {
                privateApiList.add(apiValue2);
            }
        }
        return privateApiList;
    }
}
