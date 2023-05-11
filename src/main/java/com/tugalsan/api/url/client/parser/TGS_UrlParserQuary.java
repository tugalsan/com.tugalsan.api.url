package com.tugalsan.api.url.client.parser;

import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.TGS_Url;
import java.util.*;
import java.util.stream.*;

public class TGS_UrlParserQuary implements IsSerializable {

    public TGS_UrlParserQuary() {//DTO
    }

    private TGS_UrlParserProtocol protocol;
    private TGS_UrlParserHost host;
    private TGS_UrlParserPath path;

    public TGS_UrlParserQuary(TGS_UrlParserProtocol protocol, TGS_UrlParserHost host,
            TGS_UrlParserPath path, TGS_Url url) {
        this.protocol = protocol;
        this.host = host;
        this.path = path;
        params = TGS_ListUtils.of();
        var urls = url.toString();
        var idxQ = urls.indexOf("?");
        if (idxQ == -1) {
            return;
        }
        var value = urls.substring(idxQ + 1);
        var idxA = value.indexOf("#");
        if (idxA != -1) {
            value = value.substring(0, idxA);
        }
        List<String> pairs = TGS_ListUtils.of(value.split("&"));
        if (pairs.size() == 1 && pairs.get(0).isEmpty()) {
            return;
        }
        pairs.forEach(pair -> params.add(new TGS_UrlParserParamUrlSafe(pair)));

    }
    public List<TGS_UrlParserParamUrlSafe> params;

    public TGS_UrlParserParamUrlSafe getParameterByName(CharSequence name) {
        return params.stream().filter(pair -> Objects.equals(pair.name, name)).findAny().orElse(null);
    }

    public TGS_UrlParserQuary delParameterByName(CharSequence name) {
        var found = getParameterByName(name);
        if (found != null) {
            params.remove(found);
        }
        return this;
    }

    public TGS_UrlParserQuary setParameterValueUrlSafe(CharSequence name, CharSequence valueUrlSafe) {
        if (TGS_StringUtils.isNullOrEmpty(name)) {
            return this;
        }
        if (TGS_StringUtils.isNullOrEmpty(valueUrlSafe)) {
            delParameterByName(name);
            return this;
        }
        var found = getParameterByName(name);
        if (found != null) {
            found.valueUrlSafe = valueUrlSafe;
            return this;
        }
        params.add(new TGS_UrlParserParamUrlSafe(name, valueUrlSafe));
        return this;
    }

    public TGS_UrlParserQuary setParameter(TGS_UrlParserParamUrlSafe pair) {
        var found = getParameterByName(pair.name);
        if (found != null) {
            params.remove(found);
        }
        params.add(pair);
        return this;
    }

    @Override
    public String toString() {
        return params.isEmpty() ? "" : params.stream().map(pair -> String.valueOf(pair)).collect(Collectors.joining("&", "?", ""));
    }

    public TGS_Url toUrl(){
        return TGS_Url.of(toString_url());
    }
    
    public String toString_url() {
        var pr = protocol.toString();
        var ho = host.toString();
        var pa = path.toString();
        var qu = toString();
//        System.out.println("pr: " + pr);
//        System.out.println("ho  : " + ho);
//        System.out.println("pa: " + pa);
//        System.out.println("qu: " + qu);
        return TGS_StringUtils.concat(pr, ho, pa, qu);
    }
}
