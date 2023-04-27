/*
 *
 */
package sk.antons.resttests;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import sk.antons.resttests.template.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.TransitiveProperties;
import sk.antons.resttests.http.HttpRequest;
import sk.antons.resttests.http.HttpResponse;
import sk.antons.resttests.http.call.HttpRequestEnhancer;
import sk.antons.resttests.http.call.SimpleHttpCall;
import sk.antons.resttests.tests.RestTest;
import sk.antons.resttests.tests.RestTestProcessingListener;
import sk.antons.resttests.tests.RestTests;
/**
 * Simple App builder
 * @author antons
 */
public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);

    private Resources resources = Resources.builder().build();
    private Properties properties = new Properties();
    private RestTests tests = null;
    private RestTests.Builder testsBuilder = RestTests.builder();
    private String sourceEncoding = null;
    private String urlEncoding = null;
    private String matcher = null;

    private List<RestTestProcessingListener> listeners = new ArrayList<>();
    private List<HttpRequestEnhancer> enhancers = new ArrayList<>();
    Function<HttpRequest, HttpResponse> httpCaller = null;

    private App() {}
    public static App instance() { return new App(); }

    public Resources resources() { return resources; }
    public Properties properties() { return properties; }
    public RestTests tests() { return tests; }
    public String sourceEncoding() { return sourceEncoding == null ? "utf-8" : sourceEncoding; }
    public String urlEncoding() { return urlEncoding == null ? "utf-8" : urlEncoding; }
    public List<RestTestProcessingListener> listeners() { return listeners; }
    public App sourceEncoding(String value) { this.sourceEncoding = value; return this; }
    public App urlEncoding(String value) { this.urlEncoding = value; return this; }
    public App resources(Resources value) {
        this.resources = Resources.builder().from(resources).from(value).build();
        return this;
    }
    public App resource(String value) {
        this.resources = Resources.builder().from(resources).addSource(value).build();
        return this;
    }
    public App properties(Properties value) {
        if(value != null) {
            for(String name : value.stringPropertyNames()) {
                properties.setProperty(name, value.getProperty(name));
            }
        }
        return this;
    }
    public App property(String name, String value) {
        if(!Is.empty(name)) {
            if(value == null) properties.remove(name);
            else properties.setProperty(name, value);
        }
        return this;
    }
    public App from(RestTest... value) { this.testsBuilder.from(value); return this; }
    public App from(String value) { this.testsBuilder.from(value); return this; }
    public App include(String value) { this.testsBuilder.include(value); return this; }
    public App exclude(String value) { this.testsBuilder.exclude(value); return this; }

    public App listener(HttpRequestEnhancer value) {
        if(value != null) this.enhancers.add(value);
        return this;
    }

    public App listener(RestTestProcessingListener value) {
        if(value != null) this.listeners.add(value);
        return this;
    }

    public Function<HttpRequest, HttpResponse> httpCaller() {
        if(httpCaller == null) return SimpleHttpCall.instance().encoding(urlEncoding());
        else return httpCaller;
    }

    public void start() {

        TransitiveProperties.makeClosure(properties);

        RestTests tests = testsBuilder
            .encoding(sourceEncoding)
            .resources(resources)
            .properties(properties)
            .build();

        for(HttpRequestEnhancer enhancer : enhancers) {
            tests.enhancer(enhancer);
        }

        tests.processTests( httpCaller(), listeners.toArray(new RestTestProcessingListener[] {}));

    }


}
