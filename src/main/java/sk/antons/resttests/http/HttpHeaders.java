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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import sk.antons.jaul.Is;
import sk.antons.jaul.pojo.JsonString;
import sk.antons.jaul.pojo.ToJsonString;

/**
 * All request / response header values.
 * @author antons
 */
public class HttpHeaders implements ToJsonString {
    private List<HttpHeader> headers;

    public static HttpHeaders of(HttpHeader... headers) {
        HttpHeaders h = new HttpHeaders();
        if(headers != null) {
            for(HttpHeader header : headers) {
                h.add(header);
            }
        }
        return h;
    }

    public boolean isEmpty() { return Is.empty(headers); }

    public HttpHeaders add(HttpHeader header) {
        if(header != null) {
            if(headers == null) headers = new ArrayList<>();
            headers.add(header);
        }
        return this;
    }

    public HttpHeaders add(String name, String value) {
        return add(HttpHeader.of(name, value));
    }


    /**
     * All header values.
     * @return All header values.
     */
    public List<HttpHeader> all() {
        if(headers == null) return List.of();
        return new ArrayList<>(headers);
    }

    /**
     * All header values with given name.
     * @param name Name to search (case insensitive)
     * @return Header values with given name.
     */
    public List<HttpHeader> all(final String name) {
        if(headers == null) return List.of();
        if(name == null) return List.of();
        return headers.stream()
                    .filter(h -> name.equalsIgnoreCase(h.name()))
                    .collect(Collectors.toList());
    }

    /**
     * First header value with given name.
     * @param name Name to search (case insensitive)
     * @return First value
     */
    public Optional<HttpHeader> first(final String name) {
        if(headers == null) return Optional.empty();
        if(name == null) return Optional.empty();
        return headers.stream()
                    .filter(h -> name.equalsIgnoreCase(h.name()))
                    .findFirst();
    }

    @Override
    public void toJsonString(JsonString js, boolean mo) {
        if(mo) js.objectStart();
        js.attr("headers", headers);
        if(mo) js.objectEnd();
    }

    @Override
    public String toString() {
        JsonString js = JsonString.instance();
        toJsonString(js, true);
        return js.toString();
    }

    /**
     * All values of given header name. Values are sparated by comma.
     * @param name Name to search
     * @return String with all values.
     */
    public Optional<String> allAsString(String name) {
        if(headers == null) return Optional.empty();
        if(name == null) return Optional.empty();
        return Optional.of(headers.stream()
                    .filter(h -> name.equalsIgnoreCase(h.name()))
                    .map(h -> h.value())
                    .collect(Collectors.joining(", ")));
    }

}
