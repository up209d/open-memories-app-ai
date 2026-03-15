package com.google.zxing.pdf417.decoder;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

/* loaded from: classes.dex */
final class BoundingBox {
    private ResultPoint bottomLeft;
    private ResultPoint bottomRight;
    private BitMatrix image;
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;
    private ResultPoint topLeft;
    private ResultPoint topRight;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BoundingBox(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint topRight, ResultPoint bottomRight) throws NotFoundException {
        if ((topLeft == null && topRight == null) || ((bottomLeft == null && bottomRight == null) || ((topLeft != null && bottomLeft == null) || (topRight != null && bottomRight == null)))) {
            throw NotFoundException.getNotFoundInstance();
        }
        init(image, topLeft, bottomLeft, topRight, bottomRight);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BoundingBox(BoundingBox boundingBox) {
        init(boundingBox.image, boundingBox.topLeft, boundingBox.bottomLeft, boundingBox.topRight, boundingBox.bottomRight);
    }

    private void init(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint topRight, ResultPoint bottomRight) {
        this.image = image;
        this.topLeft = topLeft;
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        calculateMinMaxValues();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BoundingBox merge(BoundingBox leftBox, BoundingBox rightBox) throws NotFoundException {
        if (leftBox == null) {
            return rightBox;
        }
        return rightBox == null ? leftBox : new BoundingBox(leftBox.image, leftBox.topLeft, leftBox.bottomLeft, rightBox.topRight, rightBox.bottomRight);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BoundingBox addMissingRows(int missingStartRows, int missingEndRows, boolean isLeft) throws NotFoundException {
        ResultPoint newTopLeft = this.topLeft;
        ResultPoint newBottomLeft = this.bottomLeft;
        ResultPoint newTopRight = this.topRight;
        ResultPoint newBottomRight = this.bottomRight;
        if (missingStartRows > 0) {
            ResultPoint top = isLeft ? this.topLeft : this.topRight;
            int newMinY = ((int) top.getY()) - missingStartRows;
            if (newMinY < 0) {
                newMinY = 0;
            }
            ResultPoint newTop = new ResultPoint(top.getX(), newMinY);
            if (isLeft) {
                newTopLeft = newTop;
            } else {
                newTopRight = newTop;
            }
        }
        if (missingEndRows > 0) {
            ResultPoint bottom = isLeft ? this.bottomLeft : this.bottomRight;
            int newMaxY = ((int) bottom.getY()) + missingEndRows;
            if (newMaxY >= this.image.getHeight()) {
                newMaxY = this.image.getHeight() - 1;
            }
            ResultPoint newBottom = new ResultPoint(bottom.getX(), newMaxY);
            if (isLeft) {
                newBottomLeft = newBottom;
            } else {
                newBottomRight = newBottom;
            }
        }
        calculateMinMaxValues();
        return new BoundingBox(this.image, newTopLeft, newBottomLeft, newTopRight, newBottomRight);
    }

    private void calculateMinMaxValues() {
        if (this.topLeft == null) {
            this.topLeft = new ResultPoint(0.0f, this.topRight.getY());
            this.bottomLeft = new ResultPoint(0.0f, this.bottomRight.getY());
        } else if (this.topRight == null) {
            this.topRight = new ResultPoint(this.image.getWidth() - 1, this.topLeft.getY());
            this.bottomRight = new ResultPoint(this.image.getWidth() - 1, this.bottomLeft.getY());
        }
        this.minX = (int) Math.min(this.topLeft.getX(), this.bottomLeft.getX());
        this.maxX = (int) Math.max(this.topRight.getX(), this.bottomRight.getX());
        this.minY = (int) Math.min(this.topLeft.getY(), this.topRight.getY());
        this.maxY = (int) Math.max(this.bottomLeft.getY(), this.bottomRight.getY());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMinX() {
        return this.minX;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMaxX() {
        return this.maxX;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMinY() {
        return this.minY;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMaxY() {
        return this.maxY;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultPoint getTopLeft() {
        return this.topLeft;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultPoint getTopRight() {
        return this.topRight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultPoint getBottomLeft() {
        return this.bottomLeft;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultPoint getBottomRight() {
        return this.bottomRight;
    }
}
