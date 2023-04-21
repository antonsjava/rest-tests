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

import java.io.InputStream;
import sk.antons.jaul.util.TextFile;
import sk.antons.jaul.pojo.JsonString;
import sk.antons.jaul.pojo.ToJsonString;

/**
 * Class determine header body.
 * @author antons
 */
public class HttpPayload implements ToJsonString {
    private String payload;
    private Type type = Type.STRING;

    public String payload() { return payload; }
    public Type type() { return type; }

    public static HttpPayload empty() { return new HttpPayload(); }
    public static HttpPayload of(String value) {
        HttpPayload payload =  new HttpPayload();
        payload.payload = value;
        payload.type = Type.STRING;
        return payload;
    }
    public static HttpPayload of(InputStream is, String encoding) {
        HttpPayload payload =  new HttpPayload();
        payload.payload = TextFile.read(is, encoding);
        payload.type = Type.STRING;
        return payload;
    }
    public static HttpPayload ofJson(String value) {
        HttpPayload payload =  new HttpPayload();
        payload.payload = value;
        payload.type = Type.JSON;
        return payload;
    }
    public static HttpPayload ofJson(InputStream is, String encoding) {
        HttpPayload payload =  new HttpPayload();
        payload.payload = TextFile.read(is, encoding);
        payload.type = Type.JSON;
        return payload;
    }
    public static HttpPayload ofQuery() {
        HttpPayload payload =  new HttpPayload();
        payload.type = Type.QUERY;
        return payload;
    }
    public static HttpPayload ofResource(String resource) {
        HttpPayload payload =  new HttpPayload();
        payload.payload = resource;
        payload.type = Type.RESOURCE;
        return payload;
    }


    /**
     * Type of HttpPayload instance.
     */
    public static enum Type {
        /**
         * Payload attribute contains text body as not interpreted string.
         */
        STRING,
        /**
         * Payload attribute contains text body as json string.
         */
        JSON,
        /**
         * Payload attribute not contains text body but body will be
         * calculated from query parameters.
         */
        QUERY,
        /**
         * Payload attribute contains url of resource to be loaded as text body.
         */
        RESOURCE;
    }

    @Override
    public void toJsonString(JsonString js, boolean mo) {
        if(mo) js.objectStart();
        js.attr("payload", payload);
        js.attr("type", type);
        if(mo) js.objectEnd();
    }

    @Override
    public String toString() {
        JsonString js = JsonString.instance();
        toJsonString(js, true);
        return js.toString();
    }

}
