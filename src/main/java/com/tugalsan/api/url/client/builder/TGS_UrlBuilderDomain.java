package com.tugalsan.api.url.client.builder;

public class TGS_UrlBuilderDomain {

    public TGS_UrlBuilderDomain(CharSequence protocol, CharSequence domain) {
        this.protocol = protocol;
        this.domain = domain;
    }
    final private CharSequence protocol;
    final private CharSequence domain;

    public TGS_UrlBuilderPort port(Integer port) {
        return new TGS_UrlBuilderPort(protocol, domain, port);
    }

    public TGS_UrlBuilderPort portNone() {
        return port(null);
    }
}
