package com.tugalsan.api.url.client.builder;

import com.tugalsan.api.random.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.TGS_Url;

public class TGS_UrlBuilderParameter {

    final private CharSequence paramName;
    final private CharSequence paramValue;

    public TGS_UrlBuilderParameter(TGS_UrlBuilderParameter previous, CharSequence paramName, CharSequence paramValue) {
        this.previous = previous;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }
    public TGS_UrlBuilderParameter previous;

    public TGS_UrlBuilderParameter(TGS_UrlBuilderFileOrServlet fileOrServlet, CharSequence paramName, CharSequence paramValue) {
        this.fileOrServlet = fileOrServlet;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }
    private TGS_UrlBuilderFileOrServlet fileOrServlet;

    public TGS_Url toUrl() {
        return TGS_Url.of(toString());
    }

    @Override
    public String toString() {
        if (fileOrServlet != null) {
            if (paramName == null) {
                return fileOrServlet.toString();
            } else {
                return TGS_StringUtils.cmn().concat(fileOrServlet.toString(), "?", paramName, "=", paramValue);
            }
        }
        if (paramName == null) {
            return previous.toString();
        } else {
            return TGS_StringUtils.cmn().concat(previous.toString(), "&", paramName, "=", paramValue);
        }
    }

    public TGS_UrlBuilderParameter parameterRandom(CharSequence paramName, int charCount) {
        return parameter(paramName, TGS_RandomUtils.nextString(charCount, true, true, false, false, null));
    }

    public TGS_UrlBuilderParameter parameter(CharSequence paramName, CharSequence paramValue) {
        return new TGS_UrlBuilderParameter(this, paramName, paramValue);
    }

    public String anchor(CharSequence anchor) {
        return toString() + "#" + anchor;
    }
}
