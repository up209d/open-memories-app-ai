# Sync to Smart Phone - API Reference

## 1. App Overview
| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.synctosmartphone` |
| **Purpose** | Automatically transfers photos to a registered smartphone when camera powers off |
| **Description** | Sync to Smart Phone operates in two modes: (1) a launcher/pairing mode where users register a smartphone via Wi-Fi Direct + SSDP device discovery, and (2) a power-off transfer mode triggered by `param_off_factor` intent extras (`OFF_BY_KEY` or `OFF_BY_UM`). When the camera powers down, the app activates Wi-Fi, creates a Wi-Fi Direct group owner, starts an SSDP server with device description XML, binds a web server hosting Sony's WebAPI content sync service, and serves images to the paired smartphone. Images are queried from `AvindexStore` based on UTC time ranges stored in a private SQLite database. Supports 2M screennail transfers and original file transfers. Uses a native library (`GetScreennail`) for JPEG screennail extraction. Version: 1.2012. |

## 2. Architecture
### Main Activity
- **AutoSyncSettingApp** (`com.sony.imaging.app.synctosmartphone.AutoSyncSettingApp`) extends `BaseApp`
- Boots into `BOOT_STATE` (pairing) or `POWEROFF_STATE` (auto-transfer) based on `param_off_factor` intent extra
- Disables `SystemReady` on create; manages system wait view with 5-second timeout

### Key Classes
| Class | Path | Role |
|-------|------|------|
| `AutoSyncSettingApp` | `synctosmartphone/AutoSyncSettingApp.java` | Main activity, boot mode detection, lifecycle |
| `AutoSyncDataBaseUtil` | `synctosmartphone/database/AutoSyncDataBaseUtil.java` | Image reservation queries, screennail extraction, database management |
| `NetworkStateUtil` | `synctosmartphone/commonUtil/NetworkStateUtil.java` | Wi-Fi/Wi-Fi Direct state machine, SSDP server, transfer progress |
| `WsController` | `synctosmartphone/webapi/util/WsController.java` | Web server lifecycle, AuthLibManager setup, servlet binding |
| `AutoSyncControlServlet` | `synctosmartphone/webapi/v1_0/AutoSyncControlServlet.java` | WebAPI servlet: actPairing, getInterfaceInformation, notifySyncStatus |
| `SyncBackUpUtil` | `synctosmartphone/commonUtil/SyncBackUpUtil.java` | Persistent settings: registered device, start UTC, image start num |
| `SyncKikiLogUtil` | `synctosmartphone/commonUtil/SyncKikiLogUtil.java` | Diagnostic logging for sync events |
| `DataBaseAdapter` | `synctosmartphone/database/DataBaseAdapter.java` | SQLite open/close/query wrapper with AES encryption |
| `DeviceBufferInputStream` | `synctosmartphone/database/DeviceBufferInputStream.java` | InputStream adapter over DeviceBuffer for serving image data |
| `ContentSyncResourceLoader` | `synctosmartphone/webapi/servlet/ContentSyncResourceLoader.java` | HTTP resource loader for image file content |
| `TransferringStatusLayoutSync` | `synctosmartphone/layout/TransferringStatusLayoutSync.java` | Transfer progress UI |
| `AutoSyncDeviceDescription` | `synctosmartphone/webapi/util/AutoSyncDeviceDescription.java` | SSDP device description XML generator |

## 3. Sony Scalar API Usage

### com.sony.scalar.provider.AvindexStore
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AvindexStore.getExternalMediaIds()` | none | `String[]` | Returns external media IDs for storage query | `AutoSyncDataBaseUtil.java` |
| `AvindexStore.loadMedia(String, int)` | `String mediaId, int mode` (1) | `void` | Initiates media index loading | `AutoSyncDataBaseUtil.java` |
| `AvindexStore.waitLoadMediaComplete(String)` | `String mediaId` | `boolean` | Blocks until media index is fully loaded; returns false if cancelled | `AutoSyncDataBaseUtil.java` |
| `AvindexStore.Images.Media.getContentUri(String)` | `String mediaId` | `Uri` | Gets content URI for image content resolver queries | `AutoSyncDataBaseUtil.java` |
| `AvindexStore.Images.Media.getImageInfo(String)` | `String data` | `AvindexContentInfo` | Gets detailed image metadata from `_data` column | `AutoSyncDataBaseUtil.java` |

### com.sony.scalar.media.AvindexContentInfo
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getAttribute(String)` | `"DCF_TBLDirName"`, `"DCF_TBLFileName"` | `String` | Gets DCF directory name and file name for URL construction | `AutoSyncDataBaseUtil.java` |

### com.sony.scalar.media.MediaInfo
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getMediaType()` | none | `int` | Returns media type: 1=SD, 2=MemoryStick (used for path selection) | `AutoSyncDataBaseUtil.java` |

### com.sony.scalar.hardware.DSP
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| (Used indirectly via native `GetScreennail` library) | -- | -- | DSP imported but screennail processing done via JNI native methods | `AutoSyncDataBaseUtil.java` |

### com.sony.scalar.hardware.DeviceBuffer
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| (Used for image data transfer) | -- | -- | Provides direct memory buffer for serving image data to web clients. Max size: 2097152 (2MB) | `AutoSyncDataBaseUtil.java`, `DeviceBufferInputStream.java`, `ContentSyncResourceLoader.java` |

### com.sony.scalar.sysutil.ScalarProperties
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("model.name")` | `"model.name"` | `String` | Gets camera model name for Wi-Fi Direct device naming | `NetworkStateUtil.java` |
| `ScalarProperties.getString("net.service.device.name")` | `"net.service.device.name"` | `String` | Gets user-configured device name (API >= 8) | `NetworkStateUtil.java` |
| `ScalarProperties.getInt("net.service.device.name.valid")` | `"net.service.device.name.valid"` | `int` | 0=use model name, 1=use custom device name | `NetworkStateUtil.java` |
| `ScalarProperties.getString("version.platform")` | `"version.platform"` | `String` | Platform version for API level detection | `AutoSyncDataBaseUtil.java` |

### com.sony.scalar.sysutil.TimeUtil
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `TimeUtil.getCurrentCalendar()` | none | `PlainCalendar` | Gets current camera date/time for UTC calculations | `AutoSyncDataBaseUtil.java` |
| `TimeUtil.getCurrentTimeZone()` | none | `PlainTimeZone` | Gets timezone info for UTC offset computation | `AutoSyncDataBaseUtil.java`, `TransferringStatusLayoutSync.java` |

### com.sony.scalar.sysutil.PlainCalendar
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `year, month, day, hour, minute, second` | `int` | Calendar fields for UTC time computation | `AutoSyncDataBaseUtil.java` |

### com.sony.scalar.sysutil.PlainTimeZone
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `gmtDiff` | `int` | GMT offset in minutes | `AutoSyncDataBaseUtil.java` |
| `summerTimeDiff` | `int` | Summer time offset in minutes | `AutoSyncDataBaseUtil.java` |

### com.sony.scalar.sysutil.didep.Kikilog
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `Kikilog.setUserLog(int, Options)` | `int subId, Options opts` | `void` | Logs sync events (launch, off-by-key, off-by-um, transfer success, connect failure, media full) | `SyncKikiLogUtil.java` |

### com.sony.scalar.sysnetutil.ScalarWifiInfo
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarWifiInfo.getProductCode()` | none | `String` | Gets product code for SSID postfix in Wi-Fi Direct group | `NetworkStateUtil.java` |

### com.sony.scalar.lib.ssdpdevice.SsdpDevice
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new SsdpDevice(Context, ServerConf, DdStatusListener)` | context, config, listener | instance | Creates SSDP device discovery server | `NetworkStateUtil.java` |
| `startServer()` | none | `boolean` | Starts SSDP/UPnP device description server | `NetworkStateUtil.java` |
| `stopServer()` | none | `void` | Stops SSDP server | `NetworkStateUtil.java` |

### com.sony.scalar.lib.ssdpdevice.ServerConf
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `deviceDescription` | `String` | XML device description content | `NetworkStateUtil.java` |
| `descriptionPath` | `String` | Path to store DD XML on device | `NetworkStateUtil.java` |
| `descriptionFile` | `String` | DD XML filename (`/dd.xml`) | `NetworkStateUtil.java` |
| `retry` | `int` | Number of SSDP retry attempts (3) | `NetworkStateUtil.java` |

### com.sony.scalar.lib.ssdpdevice.DdStatus
| Value | Description | Source File |
|-------|-------------|-------------|
| `DdStatus.OK` | Server started successfully | `NetworkStateUtil.java` |
| `DdStatus.ERROR_SERVICE_START` | Failed to start service | `NetworkStateUtil.java` |
| `DdStatus.ERROR_FROM_SERVICE` | Service-level error | `NetworkStateUtil.java` |
| `DdStatus.ERROR_NETWORK` | Network error | `NetworkStateUtil.java` |
| `DdStatus.ERROR_SERVICE_NOT_FOUND` | Service not found | `NetworkStateUtil.java` |
| `DdStatus.ERROR_DEVICE_NAME` | Device name error | `NetworkStateUtil.java` |

### com.sony.scalar.lib.ssdpdevice.DevLog
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DevLog.enable(boolean)` | `true` | `void` | Enables SSDP debug logging | `NetworkStateUtil.java` |

### com.sony.scalar.webapi.lib.authlib.AuthLibManager
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AuthLibManager.getInstance()` | none | `AuthLibManager` | Gets singleton auth library manager | `WsController.java` |
| `initialize()` | none | `void` | Initializes auth library | `WsController.java` |
| `uninitialize()` | none | `void` | Shuts down auth library | `WsController.java` |
| `clearEnableMethods()` | none | `void` | Clears enabled API methods list | `WsController.java` |
| `clearEnableMethodsHookHandler()` | none | `void` | Clears method hook handlers | `WsController.java` |
| `clearPrivateMethods()` | none | `void` | Clears private methods list | `WsController.java` |
| `setPrivateMethods(ArrayList<String>)` | list of API names | `void` | Sets private/restricted API methods | `WsController.java` |
| `addEnableMethodsStateListener(EnableMethodsStateListener)` | listener | `void` | Registers auth state change listener | `WsController.java` |
| `setServlets(Map<String, HttpServlet>)` | servlet map | `void` | Registers HTTP servlets with auth manager | `WsController.java` |

### com.sony.scalar.webapi.service.contentsync.v1_0 (WebAPI Content Sync)
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `actPairing(Pairing, ActPairingCallback)` | pairing info, callback | `int` | Handles device pairing request from smartphone | `AutoSyncControlServlet.java` |
| `getInterfaceInformation(GetInterfaceInformationCallback)` | callback | `int` | Returns server interface info (product category, version) | `AutoSyncControlServlet.java` |
| `notifySyncStatus(SyncStatusSource, NotifySyncStatusCallback)` | status source, callback | `int` | Handles sync progress: start/downloaded/skip/memoryFull/batteryLow/end | `AutoSyncControlServlet.java` |

### com.sony.scalar.hardware.avio.DisplayManager
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| (Used in PowerOffState for display control) | -- | -- | Display management during power-off transfer | `PowerOffState.java` |

## 4. Image/Video Processing Pipeline
1. **Power-off trigger** -- Camera sends intent with `param_off_factor` = `OFF_BY_KEY` or `OFF_BY_UM`
2. **Database check** -- `AutoSyncDataBaseUtil.importDatabase()` loads sync session database from memory card
3. **Reservation count** -- `getNumberOfTransferReservationFiles()` queries `AvindexStore` for images in UTC time ranges from database records
4. **Wi-Fi initialization** -- `NetworkStateUtil.start()` disables then re-enables Wi-Fi, enables Wi-Fi Direct
5. **Group owner** -- `wifiP2pManager.startGo()` creates Wi-Fi Direct group with SSID containing `ScalarWifiInfo.getProductCode()`
6. **SSDP server** -- `SsdpDevice.startServer()` advertises camera via UPnP device description XML
7. **Web server** -- `WsController.start()` initializes `AuthLibManager`, binds `OrbAndroidServiceEx` with content sync servlets
8. **Smartphone connects** -- `STA_CONNECTED_ACTION` triggers transition to connecting/registering state
9. **Pairing** -- `actPairing()` validates UUID v4, stores smartphone name/UUID via `SyncBackUpUtil`
10. **Transfer start** -- `notifySyncStatus("start")` calls `beginGetReservationFiles()`, queries images, returns first `downloadableUrl`
11. **Image serving** -- `ContentSyncResourceLoader` serves JPEG data from `DeviceBuffer` (2MB screennails or originals)
12. **Progress** -- `notifySyncStatus("downloaded")` advances cursor, provides next URL; repeated per image
13. **Completion** -- `notifySyncStatus("end")` or disconnect triggers cleanup, UTC time update, database export
14. **Wi-Fi shutdown** -- `invokeFinishProcess()` unbinds web server, stops SSDP, disables Wi-Fi Direct and Wi-Fi

## 5. Button Handling
| Key Constant | Action |
|-------------|--------|
| N/A | This app runs primarily in power-off mode with no direct button interaction during transfer |
| (Menu navigation) | Standard menu navigation keys used only in pairing/settings UI mode |

## 6. Unique APIs
| API | Description |
|-----|-------------|
| `AuthLibManager` | Sony WebAPI authentication library managing device pairing, method access control, and servlet registration |
| `SsdpDevice` / `ServerConf` | SSDP/UPnP device discovery server for advertising camera to smartphones on the local network |
| `ScalarWifiInfo.getProductCode()` | Camera product code used as Wi-Fi Direct SSID postfix for device identification |
| `AvindexContentInfo.getAttribute("DCF_TBLDirName/FileName")` | DCF table attributes for constructing downloadable image URLs |
| `DeviceBuffer` (2MB max) | Direct memory buffer for high-performance image data transfer to web clients |
| Native `GetScreennail` library | JNI library with methods: `openMSL`, `closeMSL`, `createScreennail`, `getScreennailDataSize`, `setScreennailDevieceBuffer`, `checkSoundPhoto`, `getSoundPhoto`, `getRotateInfoMSL`, `changeRotateInfoMSL`, `directBuffer` |
| `EnableMethodsStateListener` | Auth callback interface notifying of API method enable/disable state changes |
| `com.android.server.DAConnectionManagerService.apo` | Broadcast to disable auto power-off (`APO/NO`) during sync operation |
| `param_off_factor` intent extra | Boot mode detection: `OFF_BY_KEY` (power button) or `OFF_BY_UM` (system) triggers auto-transfer |
| Encrypted database | `DatabaseUtil.initialize(adapter, SEED_AES, SECRET_WORDS_SHA)` with AES encryption for sync session data |
