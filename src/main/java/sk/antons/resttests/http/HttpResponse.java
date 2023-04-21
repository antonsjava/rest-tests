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
 * One individual response
 * @author antons
 */
public class HttpResponse implements ToJsonString {
    private HttpRequest request;
    private int status;
    private HttpHeaders headers = HttpHeaders.of();
    private String body;

    public HttpResponse(HttpRequest request) {
        this.request = request;
    }

    public static HttpResponse of(HttpRequest request) { return new HttpResponse(request); }
    public HttpResponse headers(HttpHeaders value) { this.headers = value; return this; }
    public HttpResponse header(HttpHeader value) { this.headers.add(value); return this; }
    public HttpResponse body(String value) { this.body = value; return this; }
    public HttpResponse status(int value) { this.status = value; return this; }

    public HttpRequest request() { return this.request; }
    public int status() { return this.status; }
    public HttpHeaders headers() { return this.headers; }
    public String body() { return this.body; }

    @Override
    public void toJsonString(JsonString js, boolean mo) {
        if(mo) js.objectStart();
        js.attr("request", request);
        js.attr("status", status);
        js.attr("headers", headers);
        js.attr("body", Get.size(body));
        if(mo) js.objectEnd();
    }

    @Override
    public String toString() {
        JsonString js = JsonString.instance();
        toJsonString(js, true);
        return js.toString();
    }



}
