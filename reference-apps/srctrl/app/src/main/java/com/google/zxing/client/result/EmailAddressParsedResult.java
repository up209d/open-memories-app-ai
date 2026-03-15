package com.google.zxing.client.result;

/* loaded from: classes.dex */
public final class EmailAddressParsedResult extends ParsedResult {
    private final String body;
    private final String emailAddress;
    private final String mailtoURI;
    private final String subject;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EmailAddressParsedResult(String emailAddress, String subject, String body, String mailtoURI) {
        super(ParsedResultType.EMAIL_ADDRESS);
        this.emailAddress = emailAddress;
        this.subject = subject;
        this.body = body;
        this.mailtoURI = mailtoURI;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getBody() {
        return this.body;
    }

    public String getMailtoURI() {
        return this.mailtoURI;
    }

    @Override // com.google.zxing.client.result.ParsedResult
    public String getDisplayResult() {
        StringBuilder result = new StringBuilder(30);
        maybeAppend(this.emailAddress, result);
        maybeAppend(this.subject, result);
        maybeAppend(this.body, result);
        return result.toString();
    }
}
