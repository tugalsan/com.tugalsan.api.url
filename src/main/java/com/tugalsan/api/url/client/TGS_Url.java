package com.tugalsan.api.url.client;

import com.tugalsan.api.url.client.builder.*;
import java.io.Serializable;

public class TGS_Url implements Serializable {

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
