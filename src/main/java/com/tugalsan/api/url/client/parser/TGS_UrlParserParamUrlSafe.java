package com.tugalsan.api.url.client.parser;

import com.tugalsan.api.string.client.TGS_StringUtils;
import java.io.Serializable;

public class TGS_UrlParserParamUrlSafe implements Serializable {

    public TGS_UrlParserParamUrlSafe() {//DTO
    }
    public CharSequence name;
    public CharSequence valueSafe;

    public TGS_UrlParserParamUrlSafe(CharSequence pair) {
        var parts = pair.toString().split("=");
        if (parts.length != 2) {
            return;
        }
        name = parts[0];
        valueSafe = parts[1];
    }

    public TGS_UrlParserParamUrlSafe(CharSequence name, CharSequence valueSafe) {
        this.name = name;
        this.valueSafe = valueSafe;
    }

    @Override
    public String toString() {
        if (TGS_StringUtils.cmn().isNullOrEmpty(name) || TGS_StringUtils.cmn().isNullOrEmpty(valueSafe)) {
            return "";
        }
        return name + "=" + valueSafe;
    }
}
