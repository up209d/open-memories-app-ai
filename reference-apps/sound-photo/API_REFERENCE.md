# Sound Photo - API Reference

## 1. App Overview
| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.soundphoto` |
| **Purpose** | Records ambient audio before/after shutter press alongside still photo capture |
| **Description** | Sound Photo captures a still image combined with a configurable duration of audio (pre-shutter and post-shutter). It continuously records audio in a circular buffer, and when the shutter is pressed, it captures audio from before the moment (backward data) and continues recording after (forward data). The audio is encoded via DSP using LPCM encoding with optional zoom noise reduction, then embedded into the JPEG file using a native JNI library (ComposeAudioImage). The app supports multiple microphone types (inner, shoe, zoom, wireless, external, line-in) and manages audio through Sony's scalar AudioManager and AudioRecord APIs. |

## 2. Architecture
### Main Activity
- **SoundPhoto** (`SoundPhoto.java`) - Extends BaseApp, handles boot flow, audio recorder initialization, database management, and lifecycle.

### Key Classes
| Class | Path | Role |
|-------|------|------|
| SoundPhoto | `.../soundphoto/SoundPhoto.java` | Main activity, boot logic, audio recorder lifecycle |
| AudioRecorder | `.../shooting/audiorecorder/AudioRecorder.java` | Core audio recording engine - DSP setup, circular buffer, LPCM encoding |
| SPFMicrophoneCtrl | `.../microphone/SPFMicrophoneCtrl.java` | Microphone type detection and reference level management |
| CompositProcess | `.../shooting/CompositProcess.java` | Multi-exposure capture with CameraSequence for raw development |
| SPShootingState | `.../shooting/state/SPShootingState.java` | Shooting state machine management |
| DataBaseAdapter | `.../database/DataBaseAdapter.java` | Database adapter for sound photo metadata |
| DataBaseOperations | `.../database/DataBaseOperations.java` | Database CRUD operations, XML export/import |
| SoundPhotoDataBaseUtil | `.../database/SoundPhotoDataBaseUtil.java` | Database sync utilities |
| SPUtil | `.../util/SPUtil.java` | General app utilities |
| AudioTrackControl | `.../playback/audiotrack/controller/AudioTrackControl.java` | Audio playback controller |
| SPFactory | `.../SPFactory.java` | Factory class for app component creation |
| SPExecutorCreator | `.../shooting/SPExecutorCreator.java` | Capture executor setup |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `burstableTakePicture()` | none | void | Triggers photo capture (called up to 3 times for multi-exposure) | CompositProcess.java |
| `startSelfTimerShutter()` | none | void | Starts self-timer triggered capture | CompositProcess.java |

### com.sony.scalar.hardware.CameraEx.StoreImageInfo
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `DirectoryName` | String | Directory path of stored image | AudioRecorder.java |
| `FileName` | String | Filename of stored image (without extension) | AudioRecorder.java |

### com.sony.scalar.hardware.CameraSequence
| Method/Class | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `CameraSequence.Options()` | none | Options | Creates sequence options | CompositProcess.java |
| `Options.setOption("MEMORY_MAP_FILE", path)` | key, value | void | Sets memory map for `libSingleCap.so` | CompositProcess.java |
| `Options.setOption("EXPOSURE_COUNT", 3)` | key, value | void | Sets 3 exposures per sequence | CompositProcess.java |
| `Options.setOption("RECORD_COUNT", 1)` | key, value | void | Records 1 final image | CompositProcess.java |
| `Options.setOption("INTERIM_PRE_REVIEW_ENABLED", true)` | key, value | void | Enables interim preview | CompositProcess.java |
| `Options.setOption("AUTO_RELEASE_LOCK_ENABLED", false)` | key, value | void | Keeps AE/AF locked between exposures | CompositProcess.java |
| `CameraSequence.DefaultDevelopFilter()` | none | DefaultDevelopFilter | Creates raw development filter | CompositProcess.java |
| `DefaultDevelopFilter.setSource(RawData, boolean)` | raw, flag | void | Sets raw source for development | CompositProcess.java |
| `DefaultDevelopFilter.execute()` | none | void | Executes raw-to-JPEG development | CompositProcess.java |
| `DefaultDevelopFilter.getOutput()` | none | OptimizedImage | Gets developed image | CompositProcess.java |
| `DefaultDevelopFilter.release()` | none | void | Releases development filter | CompositProcess.java |
| `sequence.storeImage(OptimizedImage, boolean)` | img, flag | void | Saves image to media | CompositProcess.java |
| `RawData.release()` | none | void | Releases raw data buffer | CompositProcess.java |

### com.sony.scalar.hardware.DSP (Audio Encoding)
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor("sony-di-dsp")` | type | DSP | Creates DSP for LPCM audio encoder | AudioRecorder.java |
| `setProgram("SA_PREINSTALLED_PRG:SA_A_ENC_LPCM")` | program | void | Loads preinstalled LPCM encoder DSP program | AudioRecorder.java |
| `setProgram("SA_PREINSTALLED_PRG:SA_A_ENC_ZNR")` | program | void | Loads preinstalled zoom noise reduction DSP program | AudioRecorder.java |
| `directCreateBuffer(int)` | size | DeviceBuffer | Creates DMA-accessible buffer for DSP parameters | AudioRecorder.java |
| `setArg(int, DeviceBuffer)` | index, buffer | void | Sets DSP argument (0=boot params, 1=program params) | AudioRecorder.java |
| `getPropertyAsInt(DeviceBuffer, String)` | buffer, property | int | Gets buffer property ("memory-address", "memory-size") | AudioRecorder.java |
| `release()` | none | void | Releases DSP processor | AudioRecorder.java |

### com.sony.scalar.hardware.DeviceBuffer
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `write(ByteBuffer)` | data | void | Writes parameter data to device buffer | AudioRecorder.java |
| `release()` | none | void | Releases device buffer | AudioRecorder.java |

### com.sony.scalar.media.AudioRecord
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new AudioRecord()` | none | AudioRecord | Creates audio recorder instance | AudioRecorder.java |
| `createAudioBuffer(DeviceBuffer, int, int, EncoderParameters)` | recBuff, offset, size, params | AudioBuffer | Creates audio recording buffer with encoder config | AudioRecorder.java |
| `setAudioBuffer(AudioBuffer)` | buffer | void | Sets the active audio buffer | AudioRecorder.java |
| `setDspPlugin(int, DSP)` | index, dsp | void | Attaches DSP plugin (0=encoder, 1=zoom NR) | AudioRecorder.java |
| `setPositionNotificationPeriod(int)` | period | void | Sets notification interval in frames | AudioRecorder.java |
| `startEE()` | none | void | Starts audio encode engine | AudioRecorder.java |
| `startRecording()` | none | void | Begins audio recording | AudioRecorder.java |
| `stopRecording()` | none | void | Stops audio recording | AudioRecorder.java |
| `stopEE()` | none | void | Stops audio encode engine | AudioRecorder.java |
| `getWriteMarkerPosition()` | none | long | Gets current write position in circular buffer | AudioRecorder.java |
| `setReadMarkerPosition(long)` | position | void | Sets read marker (protects data from overwrite) | AudioRecorder.java |
| `addNotificationMarkerPosition(long)` | position | void | Adds a frame position to trigger notification callback | AudioRecorder.java |
| `getRecordingDateTime()` | none | PlainCalendar | Gets recording start timestamp | AudioRecorder.java |
| `loadZoomNrTable(DeviceBuffer)` | table | void | Loads zoom noise reduction lookup table | AudioRecorder.java |
| `release()` | none | void | Releases audio recorder | AudioRecorder.java |

### com.sony.scalar.media.AudioRecord.EncoderParameters
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `setParamter("AUDIO_FRAME_MAX_SIZE", 5760)` | key, value | void | Max audio frame size | AudioRecorder.java |
| `setParamter("AUDIO_ENCODER_TYPE", 0)` | key, value | void | Encoder type (0=LPCM) | AudioRecorder.java |
| `setParamter("SHORT_FADER_LENGTH", 0)` | key, value | void | Short fade length | AudioRecorder.java |
| `setParamter("LPCM_PROC_SIZE", int)` | key, value | void | LPCM processing size (5760 or 6144 with ZNR) | AudioRecorder.java |
| `setParamter("ES_PROC_SIZE", int)` | key, value | void | Elementary stream processing size (5760 or 11520 with ZNR) | AudioRecorder.java |
| `setParamter("AAUG_PROC_MAX_NUM", int)` | key, value | void | Max AAUG processing count (1 or 2 with ZNR) | AudioRecorder.java |
| `setParamter("OVERWRITE_MODE", false)` | key, value | void | Circular buffer overwrite mode | AudioRecorder.java |

### com.sony.scalar.media.AudioRecord.AudioBuffer
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getControlInfoOffset()` | none | int | Gets control info offset in buffer | AudioRecorder.java |
| `getControlBufferSize()` | none | int | Gets control buffer size | AudioRecorder.java |
| `getEsBufferOffset()` | none | int | Gets elementary stream buffer offset | AudioRecorder.java |
| `getEsBufferSize()` | none | int | Gets elementary stream buffer size | AudioRecorder.java |
| `getAaugiBufferOffset()` | none | int | Gets AAUG info buffer offset | AudioRecorder.java |
| `getAaugiBufferSize()` | none | int | Gets AAUG info buffer size | AudioRecorder.java |
| `getFrameCount()` | none | int | Gets total frame count in buffer | AudioRecorder.java |
| `release()` | none | void | Releases audio buffer | AudioRecorder.java |

### com.sony.scalar.media.AudioRecord.AudioData
| Usage | Description | Source File |
|-------|-------------|-------------|
| `mBackwardData` / `mForwardData` | Audio data segments before and after shutter press | AudioRecorder.java |

### com.sony.scalar.media.AudioManager
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new AudioManager()` | none | AudioManager | Creates audio manager instance | SPFMicrophoneCtrl.java |
| `setSettingChangedListener(SettingChangedListener)` | listener | void | Registers for audio setting change notifications | SPFMicrophoneCtrl.java |
| `enableSettingChangedTypes(int[])` | types | void | Enables notification for specific setting types (1=mic ref level) | SPFMicrophoneCtrl.java |
| `setMicrophoneChangedListener(OnMicrophoneChangedListener)` | listener | void | Registers for microphone hotplug/change notifications | SPFMicrophoneCtrl.java |
| `getParameters()` | none | Parameters | Gets current audio parameters | SPFMicrophoneCtrl.java |
| `release()` | none | void | Releases audio manager | SPFMicrophoneCtrl.java |

### com.sony.scalar.media.AudioManager.Parameters
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getMicrophoneReferenceLevel()` | none | String | Gets mic reference level ("low" or "normal") | SPFMicrophoneCtrl.java |

### com.sony.scalar.media.AudioManager.SettingChangedListener
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `onChanged(int[], Parameters, AudioManager)` | types, param, am | void | Called when audio settings change (type 1 = mic reference level) | SPFMicrophoneCtrl.java |

### com.sony.scalar.media.AudioManager.OnMicrophoneChangedListener
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `onChanged(String, String, AudioManager)` | micType, micChannel, am | void | Called on microphone change. Types: "inner-mic", "shoe-mic", "zoom-mic", "wireless-mic", "external-mic", "line-in" | SPFMicrophoneCtrl.java |

### com.sony.scalar.graphics.OptimizedImage
| Usage | Description | Source File |
|-------|-------------|-------------|
| Output of DefaultDevelopFilter | Holds developed JPEG for storage | CompositProcess.java |

### com.sony.scalar.sysutil.ScalarProperties
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getInt(String)` | property | int | Gets integer property ("dsp.zoomnr.supported": 1=yes) | AudioRecorder.java |

### com.sony.scalar.sysutil.ScalarInput
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getKeyStatus(int)` | keyCode | KeyStatus | Water housing detection | SoundPhoto.java |

### com.sony.scalar.sysutil.PlainCalendar
| Usage | Description | Source File |
|-------|-------------|-------------|
| Return type of `getRecordingDateTime()` | Recording start timestamp | AudioRecorder.java |

## 4. Image Processing Pipeline
1. **Boot**: `SoundPhoto.onBoot()` initializes microphone settings via `SPFMicrophoneCtrl.initialMicSetting()` and starts continuous audio recording via `AudioRecorder.getInstance().start()`
2. **Continuous recording**: `AudioRecorder.setup()` creates an `AudioRecord`, two DSP processors (LPCM encoder + optional zoom noise reduction), configures encoder parameters, and starts circular buffer recording via `startEE()` + `startRecording()`
3. **Shutter press**: `CompositProcess.takePicture()` triggers `CameraEx.burstableTakePicture()` up to 3 times (EXPOSURE_COUNT=3)
4. **Audio capture**: `AudioRecorder.rec_single(sequenceId)` marks the current write position and calculates backward (pre-shutter) and forward (post-shutter) audio segments based on configurable durations
5. **Image development**: On the 3rd `onShutterSequence()`, `DefaultDevelopFilter` develops the raw to OptimizedImage, then `sequence.storeImage()` saves it
6. **Audio embedding**: After image storage, `storeImageComplete()` gets the JPEG file path and calls the native JNI method `ComposeAudioImage()` to embed the audio data into the JPEG file
7. **Cleanup**: On app pause, `AudioRecorder.stop()` calls `stopRecording()`, `stopEE()`, and releases all DSP/buffer resources

## 5. Button Handling
| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S2` (full shutter) | Triggers photo capture + audio segment marking |
| `ISV_KEY_S1_1` (half-press) | Autofocus |
| `ISV_KEY_ENTER` | Confirm in menus |
| `ISV_KEY_UP/DOWN/LEFT/RIGHT` | Navigate settings (audio duration, etc.) |
| `ISV_KEY_MENU` | Open/close menu |
| `ISV_KEY_DELETE` | Back/cancel |
| `ISV_KEY_PLAY` | Switch to playback (plays back sound photos with audio) |
| `WATER_HOUSING` | Housing detection |

## 6. Unique APIs
| API | Description |
|-----|-------------|
| `com.sony.scalar.media.AudioRecord` | Full audio recording API with circular buffer, frame-level position control, and DSP plugin architecture |
| `AudioRecord.createAudioBuffer()` with `EncoderParameters` | Configurable LPCM encoding with parameters for frame size, processing size, and overwrite mode |
| `AudioRecord.setDspPlugin(index, DSP)` | Pluggable DSP audio processing: slot 0 for LPCM encoder, slot 1 for zoom noise reduction |
| `AudioRecord.getWriteMarkerPosition()` / `setReadMarkerPosition()` / `addNotificationMarkerPosition()` | Frame-precise circular buffer control for pre/post shutter audio capture |
| `DSP.setProgram("SA_PREINSTALLED_PRG:SA_A_ENC_LPCM")` | Preinstalled DSP programs referenced by name rather than file path |
| `DSP.setProgram("SA_PREINSTALLED_PRG:SA_A_ENC_ZNR")` | Zoom noise reduction DSP program (conditional on `ScalarProperties.getInt("dsp.zoomnr.supported")`) |
| `DSP.directCreateBuffer(size)` | Direct DMA buffer creation (vs. `createBuffer` used in image DSP) |
| `DSP.getPropertyAsInt(buffer, "memory-address")` / `"memory-size"` | Queries physical memory properties of device buffers |
| `AudioRecord.loadZoomNrTable(DeviceBuffer)` | Loads zoom NR lookup table into audio pipeline |
| `AudioManager.setMicrophoneChangedListener()` | Hotplug detection for 6 microphone types |
| `ComposeAudioImage` (native JNI) | Embeds LPCM audio data directly into JPEG file structure |
| `CameraSequence.Options("AUTO_RELEASE_LOCK_ENABLED", false)` | Keeps AE/AF lock between multi-exposure sequence steps |
