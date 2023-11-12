package com.tugalsan.api.url.client.parser;

import com.tugalsan.api.url.client.TGS_Url;
import java.io.Serializable;
import java.util.Objects;

public class TGS_UrlParserProtocol implements Serializable {

    public TGS_UrlParserProtocol() {//DTO
        value = "";
    }

    public TGS_UrlParserProtocol(TGS_Url url) {
        var urls = url.toString();
        if (urls.startsWith("http://")) {
            this.value = "http://";
            return;
        }
        if (urls.startsWith("https://")) {
            this.value = "https://";
            return;
        }
        if (urls.startsWith("ftp://")) {
            this.value = "ftp://";
            return;
        }
        if (urls.startsWith("ftps://")) {
            this.value = "ftps://";
            return;
        }
        if (urls.startsWith("file://")) {
            this.value = "file://";
            return;
        }
    }
    public String value;

    @Override
    public String toString() {
        return value;
    }

    public boolean http() {
        return Objects.equals(value, "http://");
    }

    public boolean https() {
        return Objects.equals(value, "https://");
    }

    public boolean ftp() {
        return Objects.equals(value, "ftp://");
    }

    public boolean ftps() {
        return Objects.equals(value, "ftps://");
    }

    public boolean file() {
        return Objects.equals(value, "file://");
    }
}
