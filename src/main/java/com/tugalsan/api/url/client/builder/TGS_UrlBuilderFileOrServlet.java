package com.tugalsan.api.url.client.builder;

public class TGS_UrlBuilderFileOrServlet {

    public TGS_UrlBuilderFileOrServlet(TGS_UrlBuilderDirectory directory, CharSequence fileOrServlet) {
        this.directory = directory;
        this.fileOrServlet = fileOrServlet;
    }
    private final TGS_UrlBuilderDirectory directory;
    private final CharSequence fileOrServlet;

    @Override
    public String toString() {
        return fileOrServlet == null ? directory.toString() : directory.toString().concat(fileOrServlet.toString());
    }

    public TGS_UrlBuilderParameter parameter(CharSequence paramName, CharSequence paramValue) {
        return new TGS_UrlBuilderParameter(this, paramName, paramValue);
    }

    //NO PURPOSE
//    public TGS_UrlBuilderParameter parameterNone() {
//        return new TGS_UrlBuilderParameter(this, null, null);
//    }
}
