package com.tugalsan.api.url.client.parser;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.url.client.TGS_Url;
import java.io.Serializable;

public class TGS_UrlParserHost implements Serializable {

    private TGS_UrlParserHost() {//DTO

    }

    public static TGS_UnionExcuse<TGS_UrlParserHost> of(TGS_UrlParserProtocol protocol, TGS_Url url) {
        var _this = new TGS_UrlParserHost();

        _this.protocol = protocol;
        var urls = url.toString();
        var idxHostDomainStart = urls.indexOf("//");
//        System.out.println("idxHostDomainStart: " + idxHostDomainStart);
        if (idxHostDomainStart == -1) {
            return TGS_UnionExcuse.ofExcuse(TGS_UrlParserHost.class.getSimpleName(), "of", "idxHostDomainStart == -1");
        }
        idxHostDomainStart += 2;
//        System.out.println("idxHostDomainStart: " + idxHostDomainStart);
        var idxHostEnd = urls.indexOf("/", idxHostDomainStart);
//        System.out.println("idxHostEnd: " + idxHostEnd);
        if (idxHostEnd == -1) {
            idxHostEnd = urls.indexOf("?");
//            System.out.println("idxHostEnd: " + idxHostEnd);
            if (idxHostEnd == -1) {
                return TGS_UnionExcuse.ofExcuse(TGS_UrlParserHost.class.getSimpleName(), "of", "idxHostEnd == -1");
            }
        }
        _this.domain = urls.substring(idxHostDomainStart, idxHostEnd + 1);
//        System.out.println("name: " + name);
        var idxPort = _this.domain.indexOf(":");
//        System.out.println("idxPort: " + idxPort);
        if (idxPort == -1) {
            if (_this.domain.endsWith("/")) {
                _this.domain = _this.domain.substring(0, _this.domain.length() - 1);
                if (protocol.http()) {
                    _this.port = 80;
                } else if (protocol.https()) {
                    _this.port = 443;
                } else if (protocol.ftp()) {
                    _this.port = 21;
                } else if (protocol.ftps()) {
                    _this.port = 990;
                }
            }
        } else {
            var domainPort = _this.domain.substring(idxPort + 1);
            if (domainPort.endsWith("/")) {
                domainPort = domainPort.substring(0, domainPort.length() - 1);
            }
//            System.out.println("hostPort: " + hostPort);
            var u_port = TGS_CastUtils.toInteger(domainPort);
            if (u_port.isExcuse()) {
                return u_port.toExcuse();
            }
            _this.port = u_port.value();
//            System.out.println("port: " + port);
            _this.domain = _this.domain.substring(0, idxPort);
//            System.out.println("name: " + name);
        }
        return TGS_UnionExcuse.of(_this);
    }

    private TGS_UrlParserProtocol protocol;
    public String domain;
    public Integer port;

    @Override
    public String toString() {
        var portPrintNeeded = port != null;
        if (portPrintNeeded) {
            if (protocol.http()) {
                portPrintNeeded = port != 80;
            } else if (protocol.https()) {
                portPrintNeeded = port != 443;
            } else if (protocol.ftp()) {
                portPrintNeeded = port != 21;
            } else if (protocol.ftps()) {
                portPrintNeeded = port != 990;
            }
        }
        return portPrintNeeded ? (domain + ":" + port + "/") : (domain + "/");
    }

    public String toString_url() {
        var pr = protocol.toString();
        var ho = toString();
//        System.out.println("pr: " + pr);
//        System.out.println("ho  : " + ho);
//        System.out.println("pa: " + pa);
//        System.out.println("qu: " + qu);
//        System.out.println("an: " + an);
        return TGS_StringUtils.concat(pr, ho);
    }
}
