package com.tugalsan.api.url.client.parser;

import com.tugalsan.api.url.client.*;
import java.io.Serializable;

public class TGS_UrlParser implements Serializable {

    public TGS_UrlParser() {//DTO
    }

    public static TGS_UrlParser of(TGS_Url url) {
        return new TGS_UrlParser(url);
    }

    //https://localhost:8443/res-common/
    private TGS_UrlParser(TGS_Url url) {
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

    public TGS_Url toUrl() {
        return TGS_Url.of(toString());
    }

    public TGS_UrlParser cloneIt() {
        return new TGS_UrlParser(toUrl());
    }

}
