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
package sk.antons.resttests.http;

import sk.antons.jaul.Get;
import sk.antons.jaul.pojo.JsonString;
import sk.antons.jaul.pojo.ToJsonString;

/**
 * One individual request
 * @author antons
 */
public class HttpRequest implements ToJsonString {
    private HttpMethod method = HttpMethod.GET;
    private String url;
    private String body;
    private HttpHeaders headers = HttpHeaders.of();
    private HttpQueryParams queryparams = HttpQueryParams.of();
    private HttpPayload payload;

    public HttpRequest(String url) {
        this.url = url;
    }

    public static HttpRequest of(String url) { return new HttpRequest(url); }
    public HttpRequest method(HttpMethod value) { this.method = value == null ? HttpMethod.GET : value; return this; }
    public HttpRequest headers(HttpHeaders value) { this.headers = value; return this; }
    public HttpRequest header(HttpHeader value) { this.headers.add(value); return this; }
    public HttpRequest queryparams(HttpQueryParams value) { this.queryparams = value; return this; }
    public HttpRequest param(HttpQueryParam value) { this.queryparams.add(value); return this; }
    public HttpRequest payload(HttpPayload value) { this.payload = value; return this; }
    public HttpRequest body(String value) { this.body = value; return this; }


    public String calculateUri(String encoding) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if((payload == null) || (payload.type() != HttpPayload.Type.QUERY)) {
            if((queryparams != null) && (!queryparams.isEmpty())) {
                sb.append((url.contains("?")) ? '&' : '?');
                sb.append(queryparams.calculateUri(encoding));
            }
        }
        return sb.toString();
    }

    public HttpMethod method() { return this.method; }
    public String url() { return this.url; };
    public HttpHeaders headers() { return this.headers; }
    public HttpQueryParams queryparams() { return this.queryparams; }
    public HttpPayload payload() { return this.payload; }
    public String body() { return body; }

    @Override
    public void toJsonString(JsonString js, boolean mo) {
        if(mo) js.objectStart();
        js.attr("method", method);
        js.attr("url", url);
        js.attr("body", Get.size(body));
        js.attr("headers", headers);
        js.attr("queryparams", queryparams);
        js.attr("payload", payload);
        if(mo) js.objectEnd();
    }

    @Override
    public String toString() {
        JsonString js = JsonString.instance();
        toJsonString(js, true);
        return js.toString();
    }



}
