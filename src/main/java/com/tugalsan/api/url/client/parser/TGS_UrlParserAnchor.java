package com.tugalsan.api.url.client.parser;

import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.TGS_Url;
import java.io.Serializable;

public class TGS_UrlParserAnchor implements Serializable {

    public TGS_UrlParserAnchor() {//DTO

    }
    private TGS_UrlParserProtocol protocol;
    private TGS_UrlParserHost host;
    private TGS_UrlParserPath path;
    private TGS_UrlParserQuary quary;

    public TGS_UrlParserAnchor(TGS_UrlParserProtocol protocol, TGS_UrlParserHost host,
            TGS_UrlParserPath path, TGS_UrlParserQuary quary, TGS_Url url) {
        this.protocol = protocol;
        this.host = host;
        this.path = path;
        this.quary = quary;
        var urls = url.toString();
        var idxA = urls.indexOf("#");
        if (idxA != -1) {
            value = urls.substring(idxA + 1);
        }
    }
    public String value;

    @Override
    public String toString() {
        return value == null ? "" : ("#" + value);
    }

    public String toString_url() {
        var pr = protocol.toString();
        var ho = host.toString();
        var pa = path.toString();
        var qu = quary.toString();
        var an = toString();
//        System.out.println("pr: " + pr);
//        System.out.println("ho  : " + ho);
//        System.out.println("pa: " + pa);
//        System.out.println("qu: " + qu);
//        System.out.println("an: " + an);
        return TGS_StringUtils.concat(pr, ho, pa, qu, an);
    }
}
