package com.tugalsan.api.url.client.builder;

public class TGS_UrlBuilderProtocol {

    public TGS_UrlBuilderProtocol(CharSequence protocol) {
        this.protocol = protocol;
    }
    final private CharSequence protocol;

    public TGS_UrlBuilderDomain domain(CharSequence domain) {
        return new TGS_UrlBuilderDomain(protocol, domain);
    }
}
