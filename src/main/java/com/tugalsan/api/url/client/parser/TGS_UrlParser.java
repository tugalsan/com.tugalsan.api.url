package com.tugalsan.api.url.client.parser;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.api.url.client.*;

public class TGS_UrlParser implements IsSerializable {

    public TGS_UrlParser() {//DTO
    }

    public static TGS_UrlParser of(CharSequence url) {
        return new TGS_UrlParser(url);
    }

    public static TGS_UrlParser of(TGS_Url url) {
        return of(url.url);
    }

    //https://localhost:8443/res-common/
    public TGS_UrlParser(CharSequence url) {
        protocol = new TGS_UrlParserProtocol(url);
        host = new TGS_UrlParserHost(protocol, url);
        path = new TGS_UrlParserPath(protocol, host, url);
        quary = new TGS_UrlParserQuary(protocol, host, path, url);
        anchor = new TGS_UrlParserAnchor(protocol, host, path, quary, url);
    }
    public TGS_UrlParserProtocol protocol;
    public TGS_UrlParserHost host;
    public TGS_UrlParserPath path;
    public TGS_UrlParserQuary quary;
    public TGS_UrlParserAnchor anchor;

    @Override
    public String toString() {
        return anchor.toString_url();
    }

    public TGS_UrlParser cloneIt() {
        return new TGS_UrlParser(toString());
    }

    public TGS_Url toUrl() {
        return TGS_Url.of(toString());
    }
}
