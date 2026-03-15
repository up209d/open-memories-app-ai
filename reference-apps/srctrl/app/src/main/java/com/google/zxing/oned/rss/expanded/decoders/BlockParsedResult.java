package com.google.zxing.oned.rss.expanded.decoders;

/* loaded from: classes.dex */
final class BlockParsedResult {
    private final DecodedInformation decodedInformation;
    private final boolean finished;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BlockParsedResult(boolean finished) {
        this(null, finished);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BlockParsedResult(DecodedInformation information, boolean finished) {
        this.finished = finished;
        this.decodedInformation = information;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DecodedInformation getDecodedInformation() {
        return this.decodedInformation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFinished() {
        return this.finished;
    }
}
