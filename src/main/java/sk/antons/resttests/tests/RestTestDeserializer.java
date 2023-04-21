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
package sk.antons.resttests.tests;

import java.io.Reader;
import sk.antons.json.JsonAttribute;
import sk.antons.json.JsonValue;
import sk.antons.json.parse.JsonParser;
import sk.antons.resttests.condition.Condition;
import sk.antons.resttests.condition.RootCondition;
import sk.antons.resttests.http.HttpHeader;
import sk.antons.resttests.http.HttpMethod;
import sk.antons.resttests.http.HttpPayload;
import sk.antons.resttests.http.HttpQueryParam;
import sk.antons.resttests.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.jaul.util.AsRuntimeEx;
/**
 *
 * @author antons
 */
public class RestTestDeserializer {
    private static Logger log = LoggerFactory.getLogger(RestTestDeserializer.class);

    public static RestTest test(Reader json) {
        return test(JsonParser.parse(json));
    }

    public static RestTest test(String json) {
        try {
            return test(JsonParser.parse(json));
        } catch(Exception e) {
            log.error("unable to parse json {}", json);
            throw AsRuntimeEx.argument(e);
        }
    }

    public static RestTest test(JsonValue json) {
        if(json == null) throw new IllegalArgumentException("no json for test");
        HttpRequest request = request(json.find("request").first());
        if(request == null) throw new IllegalArgumentException("no request in " + json.toCompactString());
        Condition condition = RootCondition.of(json.find("condition").first());
        if(condition == null) throw new IllegalArgumentException("no condition in " + json.toCompactString());
        String name = json.find("name").firstLiteral();
        String category = json.find("category").firstLiteral();
        boolean skip = "true".equals(json.find("skip").firstLiteral());
        return RestTest.of()
            .category(category)
            .name(name)
            .skip(skip)
            .request(request)
            .condition(condition);
    }

    private static HttpRequest request(JsonValue json) {
        if(json == null) return null;
        String value = json.find("method").firstLiteral();
        HttpMethod method = HttpMethod.fromString(value);
        String url = json.find("url").firstLiteral();
        if(url == null) throw new IllegalArgumentException("no url in " + json.toCompactString());
        HttpRequest request = HttpRequest.of(url).method(method);
        JsonValue query = json.find("query").first();
        if(query != null) {
            if(!query.isObject()) throw new IllegalArgumentException("query is not object " + query.toCompactString());
            for(JsonAttribute jsonAttribute : query.asObject().toList()) {
                request.param(HttpQueryParam.of(jsonAttribute.name().stringValue(), jsonAttribute.value().asLiteral().stringValue()));
            }
        }
        JsonValue header = json.find("header").first();
        if(header != null) {
            if(!header.isObject()) throw new IllegalArgumentException("header is not object " + header.toCompactString());
            for(JsonAttribute jsonAttribute : header.asObject().toList()) {
                request.header(HttpHeader.of(jsonAttribute.name().stringValue(), jsonAttribute.value().asLiteral().stringValue()));
            }
        }
        HttpPayload payload = null;
        if(payload == null) {
            JsonValue jv = json.find("payload").first();
            if(jv != null) payload = HttpPayload.ofJson(jv.toCompactString());
        }
        if(payload == null) {
            String v = json.find("payloadAsString").firstLiteral();
            if(v != null) payload = HttpPayload.of(v);
        }
        if(payload == null) {
            String v = json.find("payloadFromQuery").firstLiteral();
            if(v != null) payload = HttpPayload.ofQuery();
        }
        if(payload == null) {
            String v = json.find("payloadFromResource").firstLiteral();
            if(v != null) payload = HttpPayload.ofResource(v);
        }
        request.payload(payload);

        return request;
    }
}
