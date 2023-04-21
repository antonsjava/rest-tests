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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.AsRuntimeEx;
import sk.antons.jaul.pojo.JsonString;
import sk.antons.jaul.pojo.ToJsonString;

/**
 * All request query parameters.
 * @author antons
 */
public class HttpQueryParams implements ToJsonString {
    private List<HttpQueryParam> params;

    public static HttpQueryParams of(HttpQueryParam... params) {
        HttpQueryParams h = new HttpQueryParams();
        if(h.params != null) {
            for(HttpQueryParam param : params) {
                h.add(param);
            }
        }
        return h;
    }

    public boolean isEmpty() { return Is.empty(params); }

    public HttpQueryParams add(HttpQueryParam param) {
        if(param != null) {
            if(params == null) params = new ArrayList<>();
            params.add(param);
        }
        return this;
    }

    public HttpQueryParams add(String name, String value) {
        return add(HttpQueryParam.of(name, value));
    }

    public List<HttpQueryParam> all() {
        if(params == null) return List.of();
        return new ArrayList<>(params);
    }
    public List<HttpQueryParam> all(final String name) {
        if(params == null) return List.of();
        if(name == null) return List.of();
        return params.stream()
                    .filter(h -> name.equals(h.name()))
                    .collect(Collectors.toList());
    }
    public Optional<HttpQueryParam> first(final String name) {
        if(params == null) return Optional.empty();
        if(name == null) return Optional.empty();
        return params.stream()
                    .filter(h -> name.equals(h.name()))
                    .findFirst();
    }

    public String calculateUri(String encoding) {
        try {
            StringBuilder sb = new StringBuilder();
            if((params != null) && (!params.isEmpty())) {
                for(HttpQueryParam param : params) {
                    if(sb.length()>0) sb.append('&');
                    sb.append(URLEncoder.encode(param.name(), encoding));
                    sb.append('=');
                    sb.append(URLEncoder.encode(param.value(), encoding));
                }
            }
            return sb.toString();
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e, "unable to build query path");
        }
    }

    @Override
    public void toJsonString(JsonString js, boolean mo) {
        if(mo) js.objectStart();
        js.attr("params", params);
        if(mo) js.objectEnd();
    }

    @Override
    public String toString() {
        JsonString js = JsonString.instance();
        toJsonString(js, true);
        return js.toString();
    }


}
