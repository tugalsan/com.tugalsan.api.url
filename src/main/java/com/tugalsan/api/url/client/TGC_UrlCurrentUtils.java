package com.tugalsan.api.url.client;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.*;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;

public class TGC_UrlCurrentUtils {

    public static String getServerDomainOrIp() {
        return Window.Location.getHostName();
    }

    public static String getGWTModuleBase() {
        return GWT.getModuleBaseURL();
    }

    public static TGS_UnionExcuse<Integer> getServerPort() {
        return TGS_CastUtils.toInteger(Window.Location.getPort());
    }

    public static String getServerDomainOrIpAndPort() {//localhost:8443
        return Window.Location.getHost();
    }

    public static String getAppName() {
        return TGC_UrlUtils.getAppName(getUrl());
    }

    public static TGS_Url getUrl() {
        return TGS_Url.of(Document.get().getURL());//Window.Location.getHref();
    }
}
