package com.tugalsan.api.url.client;

import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.url.client.builder.*;

public class TGS_Url implements IsSerializable {

    public TGS_Url(CharSequence url) {
        this.url = url;
    }
    public CharSequence url;

    public CharSequence getUrl() {
        return url;
    }

    public void setUrl(CharSequence url) {
        this.url = url;
    }

    public static TGS_Url of(CharSequence url) {
        return new TGS_Url(url);
    }

    public static TGS_Url of(TGS_UrlBuilderParameter url) {
        return new TGS_Url(url.toString());
    }

    @Override
    public String toString() {
        return url == null ? null : url.toString();
    }
}
