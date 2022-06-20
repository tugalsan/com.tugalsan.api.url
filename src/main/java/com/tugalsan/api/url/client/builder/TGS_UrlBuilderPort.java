package com.tugalsan.api.url.client.builder;

import com.tugalsan.api.string.client.*;

public class TGS_UrlBuilderPort {

    public TGS_UrlBuilderPort(CharSequence protocol, CharSequence domain, Integer port) {
        this.protocol = protocol;
        this.domain = domain;
        this.port = port;
    }
    final public CharSequence protocol;
    final public CharSequence domain;
    final public Integer port;

    @Override
    public String toString() {
        return TGS_StringUtils.concat(protocol, "://", domain, (port == null ? "" : (":" + port)), "/");
    }

    public TGS_UrlBuilderDirectory directory(CharSequence directory) {
        return new TGS_UrlBuilderDirectory(this, directory);
    }

    public TGS_UrlBuilderDirectory directoryNone() {
        return new TGS_UrlBuilderDirectory(this, null);
    }
}
