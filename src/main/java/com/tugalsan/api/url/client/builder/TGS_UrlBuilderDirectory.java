package com.tugalsan.api.url.client.builder;

import com.tugalsan.api.string.client.*;

public class TGS_UrlBuilderDirectory {

    final private CharSequence directory;

    public TGS_UrlBuilderDirectory(TGS_UrlBuilderDirectory parent, CharSequence directory) {
        this.parent = parent;
        this.directory = directory;
    }
    public TGS_UrlBuilderDirectory parent;

    public TGS_UrlBuilderDirectory(TGS_UrlBuilderPort port, CharSequence directory) {
        this.port = port;
        this.directory = directory;
    }
    private TGS_UrlBuilderPort port;

    @Override
    public String toString() {
        if (directory == null) {
            return TGS_StringUtils.cmn().concat(port != null ? port.toString() : parent.toString());
        }
        return TGS_StringUtils.cmn().concat(port != null ? port.toString() : parent.toString(), directory, "/");
    }

    public TGS_UrlBuilderDirectory directory(CharSequence directory) {
        return new TGS_UrlBuilderDirectory(this, directory);
    }

    public TGS_UrlBuilderFileOrServlet fileOrServlet(CharSequence filename) {
        return new TGS_UrlBuilderFileOrServlet(this, filename);
    }

    public TGS_UrlBuilderFileOrServlet fileOrServletNone() {
        return fileOrServlet(null);
    }
}
