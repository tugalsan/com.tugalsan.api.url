package com.tugalsan.api.url.client.builder;

public class TGS_UrlBuilderUtils {

    public static TGS_UrlBuilderProtocol http() {
        return new TGS_UrlBuilderProtocol("http");
    }

    public static TGS_UrlBuilderProtocol https() {
        return new TGS_UrlBuilderProtocol("https");
    }

    public static TGS_UrlBuilderProtocol ftp() {
        return new TGS_UrlBuilderProtocol("ftp");
    }

    public static TGS_UrlBuilderProtocol ftps() {
        return new TGS_UrlBuilderProtocol("ftps");
    }

    public static TGS_UrlBuilderProtocol file() {
        return new TGS_UrlBuilderProtocol("file");
    }
}
