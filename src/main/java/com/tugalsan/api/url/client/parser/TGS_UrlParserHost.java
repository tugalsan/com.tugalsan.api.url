package com.tugalsan.api.url.client.parser;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.TGS_Url;
import java.io.Serializable;

public class TGS_UrlParserHost implements Serializable {

    public TGS_UrlParserHost() {//DTO

    }

    public TGS_UrlParserHost(TGS_UrlParserProtocol protocol, TGS_Url url) {
        this.protocol = protocol;
        var urls = url.toString();
        var idxHostDomainStart = urls.indexOf("//");
//        System.out.println("idxHostDomainStart: " + idxHostDomainStart);
        if (idxHostDomainStart == -1) {
            return;
        }
        idxHostDomainStart += 2;
//        System.out.println("idxHostDomainStart: " + idxHostDomainStart);
        var idxHostEnd = urls.indexOf("/", idxHostDomainStart);
//        System.out.println("idxHostEnd: " + idxHostEnd);
        if (idxHostEnd == -1) {
            idxHostEnd = urls.indexOf("?");
//            System.out.println("idxHostEnd: " + idxHostEnd);
            if (idxHostEnd == -1) {
                return;
            }
        }
        domain = urls.substring(idxHostDomainStart, idxHostEnd + 1);
//        System.out.println("name: " + name);
        var idxPort = domain.indexOf(":");
//        System.out.println("idxPort: " + idxPort);
        if (idxPort == -1) {
            if (domain.endsWith("/")) {
                domain = domain.substring(0, domain.length() - 1);
                if (protocol.http()) {
                    port = 80;
                } else if (protocol.https()) {
                    port = 443;
                } else if (protocol.ftp()) {
                    port = 21;
                } else if (protocol.ftps()) {
                    port = 990;
                }
            }
        } else {
            var domainPort = domain.substring(idxPort + 1);
            if (domainPort.endsWith("/")) {
                domainPort = domainPort.substring(0, domainPort.length() - 1);
            }
//            System.out.println("hostPort: " + hostPort);
            port = TGS_CastUtils.toInteger(domainPort);
//            System.out.println("port: " + port);
            domain = domain.substring(0, idxPort);
//            System.out.println("name: " + name);
        }
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
        return TGS_StringUtils.cmn().concat(pr, ho);
    }
}
