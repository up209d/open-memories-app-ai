package com.google.zxing.aztec;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.aztec.decoder.Decoder;
import com.google.zxing.aztec.detector.Detector;
import com.google.zxing.common.DecoderResult;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class AztecReader implements Reader {
    @Override // com.google.zxing.Reader
    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
        return decode(image, null);
    }

    @Override // com.google.zxing.Reader
    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
        ResultPointCallback rpcb;
        NotFoundException notFoundException = null;
        FormatException formatException = null;
        Detector detector = new Detector(image.getBlackMatrix());
        ResultPoint[] points = null;
        DecoderResult decoderResult = null;
        try {
            AztecDetectorResult detectorResult = detector.detect(false);
            points = detectorResult.getPoints();
            decoderResult = new Decoder().decode(detectorResult);
        } catch (FormatException e) {
            formatException = e;
        } catch (NotFoundException e2) {
            notFoundException = e2;
        }
        if (decoderResult == null) {
            try {
                AztecDetectorResult detectorResult2 = detector.detect(true);
                points = detectorResult2.getPoints();
                decoderResult = new Decoder().decode(detectorResult2);
            } catch (FormatException e3) {
                if (notFoundException != null) {
                    throw notFoundException;
                }
                if (formatException != null) {
                    throw formatException;
                }
                throw e3;
            } catch (NotFoundException e4) {
                if (notFoundException != null) {
                    throw notFoundException;
                }
                if (formatException != null) {
                    throw formatException;
                }
                throw e4;
            }
        }
        if (hints != null && (rpcb = (ResultPointCallback) hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) != null) {
            ResultPoint[] arr$ = points;
            for (ResultPoint point : arr$) {
                rpcb.foundPossibleResultPoint(point);
            }
        }
        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.AZTEC);
        List<byte[]> byteSegments = decoderResult.getByteSegments();
        if (byteSegments != null) {
            result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
        }
        String ecLevel = decoderResult.getECLevel();
        if (ecLevel != null) {
            result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
        }
        return result;
    }

    @Override // com.google.zxing.Reader
    public void reset() {
    }
}
