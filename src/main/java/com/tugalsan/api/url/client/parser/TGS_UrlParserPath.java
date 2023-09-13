package com.tugalsan.api.url.client.parser;

import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.TGS_Url;
import java.util.*;

public class TGS_UrlParserPath implements IsSerializable {

    public TGS_UrlParserPath() {//DTO
    }

    private TGS_UrlParserProtocol protocol;
    private TGS_UrlParserHost host;

    public TGS_UrlParserPath(TGS_UrlParserProtocol protocol, TGS_UrlParserHost host, TGS_Url url) {
        this.protocol = protocol;
        this.host = host;
        var urls = url.toString();
        {//Cleanse anchor
            var idx = urls.indexOf("#");
            if (idx != -1) {
                urls = urls.substring(0, idx);
            }
        }
        {//Cleanse query
            var idx = urls.indexOf("?");
            if (idx != -1) {
                urls = urls.substring(0, idx);
            }
        }

        var idxHostDomainStart = urls.indexOf("//");
//        System.out.println("idxHostDomainStart: " + idxHostDomainStart);
        if (idxHostDomainStart == -1) {
            paths = TGS_ListUtils.of();
            return;
        }
        var idxHostPathStart = urls.indexOf("/", idxHostDomainStart + 2);
//        System.out.println("idxHostPathStart: " + idxHostPathStart);
        if (idxHostPathStart == -1) {
            paths = TGS_ListUtils.of();
            return;
        }
        var fullPath = urls.substring(idxHostPathStart + 1);
//        System.out.println("fullPath: " + fullPath);
        var idxS = fullPath.lastIndexOf("/");
        if (idxS != -1) {
            fileOrServletName = fullPath.substring(idxS + 1);
            fullPath = fullPath.substring(0, idxS);
        }
        paths = TGS_ListUtils.of(fullPath.split("/"));
        if (paths.size() == 1 && paths.get(0).isEmpty()) {
            paths.clear();
        }
    }
    public List<String> paths;
    public String fileOrServletName;

    public String toStringPath() {
        return paths.isEmpty() ? "" : (String.join("/", paths) + "/");
    }

    public String toStringfileOrServletName() {
        return fileOrServletName == null ? "" : fileOrServletName;
    }

    @Override
    public String toString() {
        var pa = toStringPath();
        var fs = toStringfileOrServletName();
        return pa + fs;
    }

    public String toString_url() {
        var pr = protocol.toString();
        var ho = host.toString();
        var pa = toString();
        return TGS_StringUtils.concat(pr, ho, pa);
    }
}
