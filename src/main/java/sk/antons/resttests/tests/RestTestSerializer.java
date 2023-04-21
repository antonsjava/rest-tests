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

import sk.antons.json.JsonFactory;
import sk.antons.json.JsonObject;
import sk.antons.json.JsonValue;
import sk.antons.json.parse.JsonParser;
import sk.antons.resttests.condition.Condition;
import sk.antons.resttests.http.HttpHeader;
import sk.antons.resttests.http.HttpQueryParam;
import sk.antons.resttests.http.HttpRequest;
import sk.antons.resttests.http.HttpResponse;

/**
 *
 * @author antons
 */
public class RestTestSerializer {


    public static String string(RestTest test) {
        return json(test).toPrettyString("  ");
    }

    public static JsonValue json(RestTest test) {
        if(test == null) throw new IllegalArgumentException("no json for test");
        JsonObject jo = JsonFactory.object();
        if(test.category() != null) jo.add("category", JsonFactory.stringLiteral(test.category()));
        if(test.name()!= null) jo.add("name", JsonFactory.stringLiteral(test.name()));
        if(test.skip()) jo.add("skip", JsonFactory.boolLiteral(true));
        if(test.reason() != null) jo.add("reason", JsonFactory.stringLiteral(test.reason().toString()));
        if(test.result() != null) jo.add("result", JsonFactory.stringLiteral(test.result().toString()));
        JsonValue request = json(test.request());
        if(request != null) jo.add("request", request);
        JsonValue condition = json(test.condition());
        if(condition != null) jo.add("condition", condition);
        JsonValue response = json(test.response());
        if(response != null) jo.add("response", response);
        return jo;
    }

    private static JsonValue json(Condition value) {
        if(value == null) return null;
        return value.toJson();
    }

    private static JsonValue json(HttpRequest value) {
        if(value == null) return null;
        JsonObject jo = JsonFactory.object();
        jo.add("url", JsonFactory.stringLiteral(value.url()));
        jo.add("method", JsonFactory.stringLiteral("" + value.method()));
        if((value.queryparams() != null) && (!value.queryparams().isEmpty())) {
            JsonObject query = JsonFactory.object();
            jo.add("query", query);
            for(HttpQueryParam item : value.queryparams().all()) {
                query.add(item.name(), JsonFactory.stringLiteral(item.value()));
            }
        }
        if((value.headers() != null) && (!value.headers().isEmpty())) {
            JsonObject header = JsonFactory.object();
            jo.add("header", header);
            for(HttpHeader item : value.headers().all()) {
                header.add(item.name(), JsonFactory.stringLiteral(item.value()));
            }
        }
        if(value.payload() != null) {
            String v = null;
            switch(value.payload().type()) {
                case STRING:
                    v = value.payload().payload();
                    if(v != null) jo.add("payloadAsString", JsonFactory.stringLiteral(v));
                    break;
                case RESOURCE:
                    v = value.payload().payload();
                    if(v != null) jo.add("payloadFromResource", JsonFactory.stringLiteral(v));
                    break;
                case JSON:
                    v = value.payload().payload();
                    if(v != null) jo.add("payload", JsonParser.parse(v));
                    break;
                case QUERY:
                    jo.add("payloadFromQuery", JsonFactory.boolLiteral(true));
                    break;
                default:
            }
        }

        return jo;
    }

    private static JsonValue json(HttpResponse value) {
        if(value == null) return null;
        JsonObject jo = JsonFactory.object();
        jo.add("status", JsonFactory.intLiteral(value.status()));
        if((value.headers() != null) && (!value.headers().isEmpty())) {
            JsonObject header = JsonFactory.object();
            jo.add("header", header);
            for(HttpHeader item : value.headers().all()) {
                header.add(item.name(), JsonFactory.stringLiteral(item.value()));
            }
        }
        if(value.body() != null) jo.add("body", JsonFactory.stringLiteral(value.body()));
        return jo;
    }
}
