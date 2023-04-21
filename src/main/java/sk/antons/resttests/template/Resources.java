/*
 *
 */
package sk.antons.resttests.template;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import sk.antons.jaul.util.Resource;
import sk.antons.jaul.util.TextFile;

/**
 * Cumulates several roots for resource loading. It can be file root or classpath root.
 * File roots '/home/user/store', './src/resources', ....
 * Classpath roots 'classpath:META-INF', ...
 * @author antons
 */
public class Resources {


    private List<String> sources = new ArrayList();

    private Resources() {}

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Reads resource as string.
     * @param url url (possible relative to root sources) of respource
     * @param encoding encoding of created string
     * @return string value of resource
     */
    public ResourceWithUrl<String> string(String url, String encoding) {
        ResourceWithUrl<InputStream> is = is(url);
        if(is == null) return null;
        String content = TextFile.read(is.content(), encoding);
        return ResourceWithUrl.of(is.url(), content);
    }

    /**
     * Reads resource as input stream.
     * @param url url (possible relative to root sources) of resource
     * @return string value of input stream
     */
    public ResourceWithUrl<InputStream> is(String url) {
        if(url == null) return null;
        if(url.startsWith("/") || url.startsWith("classpath:") || url.startsWith("file:")) {
            InputStream is = Resource.url(url).inputStream();
            return is == null ? null : ResourceWithUrl.of(url, is);
        }
        InputStream is = null;
        String nu = null;
        for(String source : sources) {
            nu = Resource.mergeUrls(source, url);
            try {
                is = Resource.url(nu).inputStream();
            } catch(Exception e) {
            }
            if(is != null) break;
        }
        return is == null ? null : ResourceWithUrl.of(nu, is);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String source : sources) {
            sb.append(source).append(", ");
        }
        return sb.toString();
    }

    public static class ResourceWithUrl<T> {
        private String url;
        private T content;

        public ResourceWithUrl(String url, T content) {
            this.url = url;
            this.content = content;
        }
        public static <TT> ResourceWithUrl<TT> of(String url, TT content) { return new ResourceWithUrl(url, content); }

        public String url() { return url; }
        public T content() { return content; }
    }

    public static class Builder {

        Resources conf = new Resources();

        public Builder addSource(String url) {
            if(url != null) {
                if("classpath:".equals(url)) url = "classpath:.";
                if(!url.endsWith("/")) url = url + "/";
                if(!conf.sources.contains(url)) conf.sources.add(url);
            }
            return this;
        }

        public Builder from(Resources resources) {
            if(resources != null) {
                conf.sources.addAll(resources.sources);
            }
            return this;
        }

        public Resources build() {
            return conf;
        }

    }


}
