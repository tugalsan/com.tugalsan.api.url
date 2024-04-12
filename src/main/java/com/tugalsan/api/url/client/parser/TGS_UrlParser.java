package com.tugalsan.api.url.client.parser;

import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.url.client.*;
import java.io.Serializable;

public class TGS_UrlParser implements Serializable {

    private TGS_UrlParser() {//DTO
    }

    public static TGS_UnionExcuse<TGS_UrlParser> of(TGS_Url url) {
        var _this = new TGS_UrlParser();
        _this.protocol = new TGS_UrlParserProtocol(url);
        var u_host = TGS_UrlParserHost.of(_this.protocol, url);
        if (u_host.isExcuse()) {
            return u_host.toExcuse();
        }
        _this.host = u_host.value();
        _this.path = new TGS_UrlParserPath(_this.protocol, _this.host, url);
        _this.quary = new TGS_UrlParserQuary(_this.protocol, _this.host, _this.path, url);
        _this.anchor = new TGS_UrlParserAnchor(_this.protocol, _this.host, _this.path, _this.quary, url);
        return TGS_UnionExcuse.of(_this);
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

    public TGS_UnionExcuse<TGS_UrlParser> cloneIt() {
        return TGS_UrlParser.of(toUrl());
    }

}
