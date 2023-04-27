/*
 * Copyright 2023 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.antons.resttests.http.call;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.AsRuntimeEx;
import sk.antons.resttests.http.HttpHeader;
import sk.antons.resttests.http.HttpRequest;
import sk.antons.resttests.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation for http calls.
 * @author antons
 */
public class SimpleHttpCall implements Function<HttpRequest, HttpResponse> {
    private static Logger log = LoggerFactory.getLogger("sk.antons.resttests.http.call");

    String encoding = "utf-8";
    Duration timeout = null;

    public static SimpleHttpCall instance() { return new SimpleHttpCall(); }
    public SimpleHttpCall encoding(String value) { this.encoding = value; return this; }
    public SimpleHttpCall timeout(Duration value) { this.timeout = value; return this; }

    @Override
    public HttpResponse apply(HttpRequest t) {
        return call(t);
    }



    private static int counter = 1;
    public HttpResponse call(HttpRequest request) {
        int id = counter++;
        java.net.http.HttpRequest req = null;
        try {
            req = req(request);
            if(log.isDebugEnabled()) {
                log.debug("request:\n{}", requestToString(id, req, request.body(), request, encoding));
            }
            java.net.http.HttpResponse<String> httpResponse = httpclient().send(req, BodyHandlers.ofString());
            if(log.isDebugEnabled()) {
                log.debug("response:\n{}", responseToString(id, httpResponse));
            }
            HttpResponse response = res(httpResponse, request);
            return response;
        } catch(Throwable ex) {
            if(log.isDebugEnabled()) {
                log.debug("response:\n{}", requestFailToString(id, req, ex, request));
            }
            throw AsRuntimeEx.argument(ex, "unable to call " + request);
        }

    }

    private static String requestToString(int id, java.net.http.HttpRequest request, String body, HttpRequest orig, String encoding) {
        StringBuilder sb = new StringBuilder();
        sb.append("- req[").append(id).append("] ------------------------------\n");
        if(request == null) {
            sb.append("null request\n");
            if(orig != null) {
                sb.append("original data: \n");
                sb.append("  ").append(orig.method()).append(' ').append(orig.calculateUri(encoding)).append('\n');
                if(orig.headers() != null) {
                    for(HttpHeader header : orig.headers().all()) {
                        sb.append("  header: ").append(header.name()).append('=').append(header.value().replace("\n", "\\n")).append('\n');
                    }
                }
                if(!Is.empty(orig.body())) {
                    sb.append("-------------\n");
                    sb.append(orig.body()).append('\n');
                    sb.append("-------------\n");
                }
            }
        } else {
            sb.append(request.method()).append(' ').append(request.uri()).append('\n');
            if(request.headers() != null) {
                for(Map.Entry<String, List<String>> entry : request.headers().map().entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();
                    if(!Is.empty(values)) {
                        for(String value : values) {
                            sb.append("header: ").append(name).append('=').append(value.replace("\n", "\\n")).append('\n');
                        }
                    }
                }
            }
            if(!Is.empty(body)) {
                sb.append("-------------\n");
                sb.append(body).append('\n');
                sb.append("-------------\n");
            }
        }

        return sb.toString();
    }

    private static String requestFailToString(int id, java.net.http.HttpRequest request, Throwable t, HttpRequest orig) {
        StringBuilder sb = new StringBuilder();
        sb.append("- res[").append(id).append("] ------------------------------\n");
        if(request == null) {
            sb.append("null request\n");
            if(orig != null) {
                sb.append("original data: \n");
                sb.append("  ").append(orig.method()).append(' ').append(orig.calculateUri("utf-8")).append('\n');
                if(orig.headers() != null) {
                    for(HttpHeader header : orig.headers().all()) {
                        sb.append("  header: ").append(header.name()).append('=').append(header.value().replace("\n", "\\n")).append('\n');
                    }
                }
                if(!Is.empty(orig.body())) {
                    sb.append("-------------\n");
                    sb.append(orig.body()).append('\n');
                    sb.append("-------------\n");
                }
            }
        } else {
            sb.append(request.method()).append(' ').append(request.uri()).append('\n');
        }
        if(!Is.empty(t)) {
            sb.append("-------------\n");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            sb.append(sw.toString()).append('\n');
            sb.append("-------------\n");
        }
        return sb.toString();
    }

    private static String responseToString(int id, java.net.http.HttpResponse<String> response) {
        StringBuilder sb = new StringBuilder();
        sb.append("- res[").append(id).append("] ------------------------------\n");
        if(response == null) {
            sb.append("null response\n");
        } else {
            sb.append(response.request().method()).append(' ').append(response.uri()).append(' ').append(response.version()).append('\n');
            sb.append("status: ").append(response.statusCode()).append('\n');
            if(response.headers() != null) {
                for(Map.Entry<String, List<String>> entry : response.headers().map().entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();
                    if(!Is.empty(values)) {
                        for(String value : values) {
                            sb.append("header: ").append(name).append('=').append(value.replace("\n", "\\n")).append('\n');
                        }
                    }
                }
            }
            if(!Is.empty(response.body())) {
                sb.append("-------------\n");
                sb.append(response.body()).append('\n');
                sb.append("-------------\n");
            }
        }

        return sb.toString();
    }

    private HttpResponse res(java.net.http.HttpResponse<String> res, HttpRequest request) {
        HttpResponse response = HttpResponse.of(request);
        if(res != null) {
            response.status(res.statusCode());
            String body = res.body();
            response.body(body);
            if(res.headers() != null) {
                for(Map.Entry<String, List<String>> entry : res.headers().map().entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();
                    if(!Is.empty(values)) {
                        for(String value : values) {
                            response.headers().add(name, value);
                        }
                    }
                }
            }
        }
        return response;
    }

    private java.net.http.HttpRequest req(HttpRequest request) {
        java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder()
            .timeout(Duration.ofSeconds(10));

        String uri = request.calculateUri(encoding);
        builder.uri(URI.create(uri));
        String body = request.body();
        if((request.headers() != null) && (!request.headers().isEmpty())) {
            for(HttpHeader httpHeader : request.headers().all()) {
                builder.header(httpHeader.name(), httpHeader.value());
            }
        }

        switch(request.method()) {
            case DELETE: builder.DELETE(); break;
            case HEAD: builder.GET(); break;
            case GET: builder.GET(); break;
            case POST: builder.POST(body == null
                ? java.net.http.HttpRequest.BodyPublishers.noBody()
                : java.net.http.HttpRequest.BodyPublishers.ofString(body)); break;
            case PUT: builder.POST(body == null
                ? java.net.http.HttpRequest.BodyPublishers.noBody()
                : java.net.http.HttpRequest.BodyPublishers.ofString(body)); break;
            default: throw new AssertionError("Unsupported method " +request.method());
        }

        return builder.build();

    }

    HttpClient client = null;
    private synchronized HttpClient httpclient() {
        if(client == null) {
            client = HttpClient.newBuilder()
                .connectTimeout(timeout == null ? Duration.ofSeconds(60) : timeout)
                .sslContext(trustSelfSignedSSL())
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        }
        return client;
    }

    private static SSLContext trustSelfSignedSSL() {
        try {
            SSLContext ctx = SSLContext.getInstance("SSL");
            ctx.init(null, new TrustManager[] { DUMMY_TRUST_MANAGER }, null);
            //HttpsURLConnection.setDefaultHostnameVerifier( SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //SSLContext.setDefault(ctx);
            return ctx;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static final TrustManager DUMMY_TRUST_MANAGER = new X509ExtendedTrustManager() {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException { }
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException { }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException { }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException { }
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException { }
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException { }
    };
}
