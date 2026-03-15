# SRCtrl (Smart Remote Control) - API Reference

## 1. App Overview
| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.srctrl` |
| **Purpose** | WebAPI server allowing smartphone to remotely control camera (shutter, exposure, live view, content browsing) |
| **Description** | SRCtrl is a WebAPI server application that runs on Sony cameras and exposes a JSON-RPC HTTP API for remote control from smartphones and tablets. It implements Sony's Camera Remote API with versioned endpoints (v1.0 through v1.4), SSDP service discovery, NFC touch pairing, live view streaming, postview image transfer, content browsing, and movie streaming. The app uses the Mexi ORB server framework (Leza HTTP server) with Sony's AuthLib for API access control. Unlike other camera apps, SRCtrl does NOT directly process images - it bridges WebAPI requests to the camera's native shooting, exposure, and playback controls. |

## 2. Architecture
### Main Activity
- **SRCtrl** (`SRCtrl.java`) - Extends BaseApp, handles boot, model detection, airplane mode check, WebAPI server initialization, and NFC setup.

### Key Classes
| Class | Path | Role |
|-------|------|------|
| SRCtrl | `.../srctrl/SRCtrl.java` | Main activity, boot flow, model detection |
| OrbAndroidServiceEx | `.../webapi/service/OrbAndroidServiceEx.java` | Android Service hosting the Leza HTTP server, registers all servlets |
| SRCtrlServlet | `.../webapi/SRCtrlServlet.java` | Main camera API servlet (extends AuthApiHandler), registers v1.0-v1.4 |
| SRCtrlAvContentServlet | `.../webapi/SRCtrlAvContentServlet.java` | Content browsing/streaming API servlet |
| AccessControlInterfaceServlet | `.../webapi/AccessControlInterfaceServlet.java` | Access control API servlet |
| ModelInfo | `.../util/ModelInfo.java` | Model-specific feature detection (movie support, NFC, airplane mode) |
| Name | `.../webapi/definition/Name.java` | WebAPI method name constants (90+ API methods) |
| ShootingHandler | `.../webapi/specific/ShootingHandler.java` | Still/movie capture handler for WebAPI requests |
| ServerEventHandler | `.../webapi/specific/ServerEventHandler.java` | Server-sent events for getEvent long polling |
| LiveviewCommon | `.../liveview/LiveviewCommon.java` | Live view streaming protocol constants and utilities |
| LiveviewChunkTransfer | `.../webapi/servlet/LiveviewChunkTransfer.java` | Live view JPEG chunk streaming servlet |
| StreamingChunkTransfer | `.../webapi/servlet/StreamingChunkTransfer.java` | Movie streaming servlet |
| ContentsTransfer | `.../webapi/servlet/ContentsTransfer.java` | Content download transfer servlet |
| ParamsGenerator | `.../webapi/availability/ParamsGenerator.java` | Generates available parameter lists for WebAPI getEvent |
| AvailabilityDetector | `.../webapi/availability/AvailabilityDetector.java` | Detects available/supported WebAPI methods |
| NfcCtrlManager | `.../util/NfcCtrlManager.java` | NFC touch-to-connect pairing manager |
| SRCtrlEnvironment | `.../util/SRCtrlEnvironment.java` | Environment/capability detection |
| AFFrameConverter | `.../liveview/AFFrameConverter.java` | Converts AF frame coordinates between camera and WebAPI coordinate systems |
| Factory | `.../Factory.java` | Factory class for app components |
| SRCtrlExecutorCreator | `.../shooting/SRCtrlExecutorCreator.java` | Capture executor setup |

## 3. Sony Scalar API Usage

### com.sony.scalar.sysutil.ScalarProperties
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getString(String)` | property | String | Gets model name ("model.name"), UI model name ("ui.model.mame") | SRCtrl.java, ModelInfo.java |
| `getInt(String)` | property | int | Gets integer property ("device.nfc.supported": 1=yes) | ModelInfo.java |

### com.sony.scalar.sysutil.ScalarInput
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getKeyStatus(int)` | keyCode | KeyStatus | Water housing detection at boot | SRCtrl.java |

### com.sony.scalar.sysutil.didep.Nfc
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new Nfc()` | none | Nfc | Creates NFC controller instance | NfcCtrlManager.java |
| `release()` | none | void | Releases NFC controller | NfcCtrlManager.java |
| (standby/stop methods) | - | - | Start/stop NFC touch detection | NfcCtrlManager.java |

### com.sony.scalar.hardware.CameraEx
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| (used via ShootingHandler) | - | - | Still capture triggered by WebAPI actTakePicture | ShootingHandler.java |

### com.sony.scalar.webapi.lib.authlib.AuthLibManager
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getInstance()` | none | AuthLibManager | Gets singleton AuthLib manager | OrbAndroidServiceEx.java |
| `initialize()` | none | void | Initializes authentication library | OrbAndroidServiceEx.java |
| `uninitialize()` | none | void | Shuts down authentication library | OrbAndroidServiceEx.java |
| `clearEnableMethods()` | none | void | Clears enabled API method list | OrbAndroidServiceEx.java |
| `clearEnableMethodsHookHandler()` | none | void | Clears method hook handlers | OrbAndroidServiceEx.java |
| `clearPrivateMethods()` | none | void | Clears private API list | OrbAndroidServiceEx.java |
| `setPrivateMethods(ArrayList<String>)` | methods | void | Sets which APIs require authentication | OrbAndroidServiceEx.java |
| `addEnableMethodsStateListener(listener)` | listener | void | Registers for auth state change events | OrbAndroidServiceEx.java |
| `removeEnableMethodsEventListener(listener)` | listener | void | Unregisters auth state listener | OrbAndroidServiceEx.java |

### com.sony.scalar.webapi.lib.authlib.AuthApiHandler
| Usage | Description | Source File |
|-------|-------------|-------------|
| Base class for SRCtrlServlet | Versioned API handler with authentication | SRCtrlServlet.java |
| `addVersion(String, handler)` | Registers versioned servlet (v1.0 through v1.4) | SRCtrlServlet.java |

### com.sony.scalar.webapi.lib.authlib.EnableMethodsStateListener
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `onStateChange(state, devName, devId, methods, sg)` | - | void | Called when API access state changes | OrbAndroidServiceEx.java |
| `onError(responseCode, devName, devId, methods, sg)` | - | void | Called on auth error | OrbAndroidServiceEx.java |

### com.sony.mexi.orb.server.leza.Server (Leza HTTP Server)
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ServerBuilder(int port)` | port | ServerBuilder | Creates server builder with port | OrbAndroidServiceEx.java |
| `ServerBuilder.setBacklog(int)` | backlog | ServerBuilder | Sets connection backlog | OrbAndroidServiceEx.java |
| `ServerBuilder.setMaxBodySizeInByte(int)` | size | ServerBuilder | Sets max request body size | OrbAndroidServiceEx.java |
| `ServerBuilder.setServerStatusListener(listener)` | listener | ServerBuilder | Sets server status callback | OrbAndroidServiceEx.java |
| `ServerBuilder.setMaxConnections(int)` | count | ServerBuilder | Sets max concurrent connections (10) | OrbAndroidServiceEx.java |
| `ServerBuilder.addProcess(HttpProcess)` | process | ServerBuilder | Adds HTTP processing pipeline | OrbAndroidServiceEx.java |
| `ServerBuilder.build()` | none | Server | Builds server instance | OrbAndroidServiceEx.java |
| `server.startup()` | none | void | Starts HTTP server | OrbAndroidServiceEx.java |
| `server.shutdown()` | none | void | Stops HTTP server | OrbAndroidServiceEx.java |

## 4. Image Processing Pipeline
SRCtrl is a WebAPI server, not a direct image processor. Its pipeline is:

1. **Boot**: `SRCtrl.onBoot()` detects camera model via `ScalarProperties.getString("model.name")`, checks airplane mode, initializes NFC and temperature management
2. **Server startup**: `OrbAndroidServiceEx.onBind()` creates the Leza HTTP server with servlets:
   - `/sony/camera/` - Camera control API (SRCtrlServlet with v1.0-v1.4)
   - `/sony/avContent/` - Content browsing API (SRCtrlAvContentServlet)
   - `/sony/accessControl/` - Access control API
   - `/liveview/liveviewstream` - Live view MJPEG streaming
   - `/postview` - Postview image download
   - `/continuous` - Continuous shooting image download
   - `/contentstransfer` - Content file transfer
   - `/streaming` - Movie streaming
3. **Remote shooting**: Smartphone sends `actTakePicture` JSON-RPC request, `ShootingHandler.createStillPicture()` triggers camera capture via the base framework
4. **Live view**: `LiveviewChunkTransfer` sends chunked MJPEG stream with 8-byte common header + 128-byte payload header per frame, including AF frame info (up to 39 focus frames)
5. **Content access**: `SRCtrlAvContentServlet` provides `getContentList`, `deleteContent`, and streaming APIs for browsing/downloading captured images

## 5. Button Handling
| Key Constant | Action |
|-------------|--------|
| `WATER_HOUSING` | Housing detection at boot via ScalarInput.getKeyStatus() |
| (Most input is via WebAPI, not physical buttons) | Smartphone sends JSON-RPC commands |
| `actHalfPressShutter` | WebAPI half-press shutter (AF trigger) |
| `cancelHalfPressShutter` | WebAPI cancel half-press |
| `actTakePicture` | WebAPI full capture |
| `startMovieRec` / `stopMovieRec` | WebAPI movie recording control |
| `actZoom` | WebAPI zoom control |

## 6. Unique APIs
| API | Description |
|-----|-------------|
| `com.sony.scalar.webapi.lib.authlib.*` | Full WebAPI authentication library (AuthLibManager, AuthApiHandler, EnableMethodsStateListener) for controlling which APIs require device pairing |
| `com.sony.mexi.orb.server.leza.*` | Leza HTTP server framework (ServerBuilder, HttpProcessBuilder, OrbServiceGroup) - embedded HTTP server running on camera |
| `com.sony.mexi.orb.service.http.*` | HTTP handler framework (GenericHttpRequestHandler, HttpHandlerGroup) for routing requests to servlets |
| `com.sony.scalar.sysutil.didep.Nfc` | NFC controller for touch-to-connect pairing with smartphones |
| Versioned WebAPI (v1.0-v1.4) | 90+ JSON-RPC methods across 5 API versions covering: shooting, exposure, live view, content browsing, streaming, zoom, focus, flash, drive mode, silent shooting, and more |
| Live view streaming protocol | Custom binary protocol: 8-byte common header (start byte 0xFF, payload type, sequence number, timestamp) + 128-byte payload header (start code 0x24356879, JPEG size, padding) with AF frame info overlay (up to 39 frames in 10000x10000 coordinate space) |
| Movie recording support detection | Model-specific: ILCE-7, ILCE-7R, ILCE-5000, ILCE-6000 excluded from movie recording; UI model "DSC-15-03" also excluded |
| `ScalarProperties.getInt("device.nfc.supported")` | NFC hardware capability detection (requires PF API >= 6) |
| SSDP service discovery | Camera advertises itself on the network for automatic smartphone discovery |
| Content management APIs | `getContentList`, `getContentCount`, `deleteContent`, `getSchemeList`, `getSourceList` for browsing camera media |
| Streaming APIs | `setStreamingContent`, `startStreaming`, `pauseStreaming`, `stopStreaming`, `seekStreamingPosition`, `requestToNotifyStreamingStatus` for video playback |
