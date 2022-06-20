module com.tugalsan.api.url {
    requires gwt.user;
    requires com.tugalsan.api.executable;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.network;
    requires com.tugalsan.api.cast;
    requires com.tugalsan.api.stream;
    requires com.tugalsan.api.crypto;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.list;
    requires com.tugalsan.api.pack;
    requires com.tugalsan.api.string;
    requires com.tugalsan.api.random;
    exports com.tugalsan.api.url.client;
    exports com.tugalsan.api.url.client.builder;
    exports com.tugalsan.api.url.client.parser;
    exports com.tugalsan.api.url.server;
}
