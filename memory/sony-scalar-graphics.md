# Sony Scalar Graphics API - Detailed Reference

## OptimizedImage (com.sony.scalar.graphics.OptimizedImage)
- Core image container, ALL 7 apps use it
- Internal format: YUV422 (2 bytes per pixel)
- NEVER constructed directly - from factories/filters/camera
- Methods: getWidth(), getHeight(), isValid(), release()
- Canvas dimensions may differ from image (stride padding)
- Lifecycle: always check isValid() before release()

## OptimizedImageFactory (com.sony.scalar.graphics.OptimizedImageFactory)
- decodeImage(String filePath, Options) -> OptimizedImage
- Options.imageType: 2=screen-size, 3=full-size
- Options.bBasicInfo/bCamInfo/bExtCamInfo/bGpsInfo: metadata decode flags
- Options.outContentInfo: populated after decode with metadata

## ImageAnalyzer (com.sony.scalar.graphics.ImageAnalyzer)
- Face detection on OptimizedImage
- findFaces(OptimizedImage, AnalyzedFace[8]) -> int faceCount
- AnalyzedFace.face.rect -> Rect in range -1000 to +1000
- Convert: (rect.left + 1000) / scaleX = pixel position
- Always release() after use

## JpegExporter (com.sony.scalar.graphics.JpegExporter)
- encode(OptimizedImage, String mediaId, Options) -> saves JPEG
- Options.quality: 1=Standard, 2=Fine, 3=Extra Fine
- mediaId from AvindexStore.getExternalMediaIds()[0]
- Always release() after use

## Image Filters - Common Pattern
All follow: new -> setSource(img, releaseAfter) -> configure -> execute() -> getOutput() -> clearSources() -> release()

### ScaleImageFilter
- setDestSize(w, h) - target dimensions, (0,0) = auto
### CropImageFilter
- setSrcRect(Rect) - crop rectangle
- Full-rect crop = image copy (no clone method exists)
### RotateImageFilter
- setTrimMode(3) - always 3
- setRotation(ROTATION_DEGREE.DEGREE_90/180/270) or setRotation(double angle)
- EXIF mapping: 6->90, 8->270, 3->180
### FaceNRImageFilter (Face Noise Reduction / Soft Skin)
- setFaceList(Vector<Rect>), setISOValue(int), setNRLevel(1-5)
### MiniatureImageFilter (Tilt-Shift)
- setMiniatureArea(1-6): 1=HCenter, 2=VCenter, 3=Left, 4=Right, 5=Upper, 6=Lower
### Additional Filters (from stubs)
- ContrastPlusImageFilter, RedEyeImageFilter, SoftFocusImageFilter, SuperResolutionImageFilter

## OptimizedImageView (com.sony.scalar.widget.OptimizedImageView)
- Standard size: 640x480 pixels (matches camera LCD)
- setOptimizedImage(image/null), setDisplayType(DISPLAY_TYPE_CENTER_INNER)
- setScale(float, BOUND_TYPE_LONG_EDGE), setDisplayRotationAngle(0/90/180/270)
- setDisplayPosition(Point, POS_TYPE_CENTER_BOUNDED)
- translate(Point, TRANS_TYPE_INNER_CENTER), getTranslatability()
- getLayoutInfo() -> clipSize, drawSize, imageSize, viewSize
- setOnDisplayEventListener(callback) for async render completion
- setPivot(Point), redraw(), release()
