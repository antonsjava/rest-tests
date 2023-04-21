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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import sk.antons.resttests.condition.Condition;
import sk.antons.resttests.http.HttpRequest;
import sk.antons.resttests.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.Resource;
import sk.antons.resttests.http.HttpPayload;
import sk.antons.resttests.http.call.HttpRequestEnhancer;
import sk.antons.resttests.template.Resources;


/**
 * RestTest instance
 * @author antons
 */
public class RestTest {
    private static Logger log = LoggerFactory.getLogger(RestTest.class);
    private static Logger requestlog = LoggerFactory.getLogger("sk.antons.resttests.tests.requests");

    private String id = UUID.randomUUID().toString();
    private String name;
    private String category;
    private String identification;
    private boolean skip = false;
    private HttpRequest request;
    private HttpResponse response;
    private Condition condition;
    private long time;
    private RestTestResult result;
    private Throwable reason;

    Resources resources;
    String encoding = "utf-8";
    private List<HttpRequestEnhancer> enhancers = new ArrayList<>();

    public RestTest enhancers(List<HttpRequestEnhancer> value) { this.enhancers = value; return this; }
    public RestTest enhancer(HttpRequestEnhancer enhancer) {
        if(enhancer != null) this.enhancers.add(enhancer);
        return this;
    }
    public static RestTest of() { return new RestTest(); }

    public HttpRequest request() { return request; }
    public HttpResponse response() { return response; }
    public Condition condition() { return condition; }
    public String name() { return name; }
    public String category() { return category; }
    public long time() { return time; }
    public RestTestResult result() { return result; }
    public Throwable reason() { return reason; }
    public String id() { return id; }
    public boolean skip() { return skip; }
    public String identification() { return identification; }

    public RestTest request(HttpRequest value) { this.request = value; return this; }
    public RestTest response(HttpResponse value) { this.response = value; return this; }
    public RestTest condition(Condition value) { this.condition = value; return this; }
    public RestTest name(String value) { this.name = value; return this; }
    public RestTest category(String value) { this.category = value; return this; }
    public RestTest result(RestTestResult value) { this.result = value; return this; }
    public RestTest reason(Throwable value) { this.reason = value; return this; }
    public RestTest skip(boolean value) { this.skip = value; return this; }
    public RestTest identification(String value) { this.identification = value; return this; }
    protected RestTest resources(Resources value) { this.resources = value; return this; }
    protected RestTest encoding(String value) { this.encoding = value; return this; }

    public void process(Function<HttpRequest, HttpResponse> caller, RestTestProcessingListener... listeners) {
        time = 0;

        sendEvents(listeners);
        if(skip) {
            result = RestTestResult.SKIPPED;
            reason = new AssertionError("test marked to be skiped");
        } else if(caller == null) {
            result = RestTestResult.SKIPPED;
            reason = new AssertionError("no caller specified");
        } else if(request == null) {
            result = RestTestResult.SKIPPED;
            reason = new AssertionError("no request specified");
        } else if(condition == null) {
            result = RestTestResult.SKIPPED;
            reason = new AssertionError("no condition specified");
        } else {
            try {
                long starttime = System.currentTimeMillis();
                initBody();
                if(!Is.empty(enhancers)) {
                    for(HttpRequestEnhancer enhancer : enhancers) {
                        enhancer.enhance(request);
                    }
                }
                response = caller.apply(request);
                if(response == null) {
                    result = RestTestResult.ABORTED;
                    reason = new AssertionError("null response");
                } else {
                    boolean rv = condition.validate(response);
                    if(rv) {
                        result = RestTestResult.SUCCESSFUL;
                    } else {
                        result = RestTestResult.FAILED;
                        reason = new AssertionError("test failed: " + condition.toJson().toCompactString());
                    }
                }
                long endtime = System.currentTimeMillis();
                time = endtime - starttime;
            } catch(Throwable e) {
                reason = e;
                result = RestTestResult.ABORTED;
            }
        }
        sendEvents(listeners);
        log.debug("test {}.{} result {} in {}ms identification: {} result: {}", category, name, result, time, identification, reason == null ? "" : reason.toString());
        if(requestlog.isDebugEnabled()) {
            requestlog.debug("---------------------------\n{}", RestTestSerializer.string(this));
        }
    }

    private void initBody() {
        if(request != null) {
            if(request.body() == null) {
                if(request.payload() == null) {
                } else if(request.payload().type() == HttpPayload.Type.QUERY) {
                    String body = "";
                    if(request.queryparams() != null) body = request.queryparams().calculateUri(encoding);
                    if(body == null) body = "";
                    request.body(body);
                } else if(request.payload().type() == HttpPayload.Type.RESOURCE) {
                    if(resources == null) resources = Resources.builder().build();
                    Resources nr = Resources.builder()
                                .from(resources)
                                .addSource(Resource.parentUrl(Resource.normalizeUrl(identification)))
                                .build();
                    Resources.ResourceWithUrl<String> bodycontent = nr.string(request.payload().payload(), "utf-8");
                    if(bodycontent == null) request.body("");
                    else request.body(bodycontent.content());
                } else {
                    request.body(request.payload().payload());
                }
                if(request.body() == null) request.body("");
            }
        }
    }

    private void sendEvents(RestTestProcessingListener... listeners) {
        if(Is.empty(listeners)) return;
        for(RestTestProcessingListener listener : listeners) {
            if(listener == null) continue;
            try {
                if(result == null) {
                    listener.testStart(this);
                } else if(result == RestTestResult.ABORTED) {
                    listener.testAbort(this);
                } else if(result == RestTestResult.FAILED) {
                    listener.testFail(this);
                } else if(result == RestTestResult.SKIPPED) {
                    listener.testSkip(this);
                } else if(result == RestTestResult.SUCCESSFUL) {
                    listener.testDone(this);
                } else if(result == RestTestResult.UNFINISHED) {
                    listener.testAbort(this);
                }
            } catch(Throwable e) {
                log.info("test {}.{} event send failed {}  {}", category, name, listener.getClass(), e.toString());
            }
        }
    }

}
