package com.sony.mexi.orb.server;

import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;

/* loaded from: classes.dex */
public class OrbEntityTemplate extends EntityTemplate {
    private long contentLength;

    public OrbEntityTemplate(ContentProducer contentproducer) {
        super(contentproducer);
        this.contentLength = -1L;
    }

    @Override // org.apache.http.entity.EntityTemplate, org.apache.http.HttpEntity
    public long getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(long len) {
        this.contentLength = len;
    }
}
