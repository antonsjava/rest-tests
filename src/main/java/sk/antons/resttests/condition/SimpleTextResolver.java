/*
 *
 */
package sk.antons.resttests.condition;

import java.util.List;
import sk.antons.charmap.alphabet.Any2Ascii;
import sk.antons.jaul.Get;
import sk.antons.jaul.Is;
import sk.antons.jaul.Split;
import sk.antons.json.JsonValue;
import sk.antons.json.parse.JsonParser;
import sk.antons.resttests.http.HttpResponse;

/**
 * Following selectors are recognized.
 * 'status' - identify response status
 * 'header.[header name]' - identifies header with name value if there are more
 * headers with that name they are returned as one string coma separated.
 * 'body' - text of response body
 * 'body.att1.att2.att3' - interprets body as json and use following path to
 * determine subset of body. Individual parts
 *
 * @author antons
 */
public class SimpleTextResolver implements TextResolver {

    @Override
    public String resolve(HttpResponse response, String selector) {
        if(response == null) return null;
        if(selector == null) return null;
        String path = selector;
        String enhancers = "";
        int pos = selector.indexOf(':');
        if(pos > -1) {
            path = selector.substring(0, pos);
            enhancers = selector.substring(pos+1);
        }
        String text = null;
        if("status".equals(path)) {
            text = String.valueOf(response.status());
        } else if("body".equals(path)) {
            text = response.body();
        } else if(path.startsWith("header.")) {
            text = response.headers().allAsString(selector.substring(7)).orElse("");
            //text = response.headers().first(selector.substring(7)).orElse(HttpHeader.of(selector, "")).value();
        } else if(path.startsWith("body.")) {
            path = path.substring(5);
            JsonValue jv = JsonParser.parse(response.body());
            List<JsonValue> list = jv.find(path(path)).all();
            if(Is.empty(list) && path.endsWith(".size")) {
                list = jv.find(path(path.substring(0, path.length()-5))).all();
                text = String.valueOf(Get.size(list));
            } else {
                if(Is.empty(list) && path.endsWith(".first")) {
                    list = jv.find(path(path.substring(0, path.length()-6))).all();
                    JsonValue vvv = Get.first(list);
                    if(vvv != null) list = List.of(vvv);
                }
                if(Is.empty(list) && path.endsWith(".last")) {
                    list = jv.find(path(path.substring(0, path.length()-5))).all();
                    JsonValue vvv = Get.last(list);
                    if(vvv != null) list = List.of(vvv);
                }
                JsonValue jvv = Get.first(list);
                if(jvv == null) {
                    text = "";
                } else if(jvv.isLiteral()) {
                    text = jvv.asLiteral().stringValue();
                } else {
                    text = jvv.toCompactString();
                }
            }
        } else {
            text = "";
        }

        if(text == null) text = "";
        text = enhance(text, enhancers);
        return text;
    }

    protected String enhance(String text, String enhancers) {
        if(Is.empty(text)) return text;
        if(Is.empty(enhancers)) return text;
        List<String> list = Split.string(enhancers).bySubstringToList(",");
        for(String string : list) {
            string = string.trim();
            text = enhancer(text, string);
        }
        return text;
    }

    protected String enhancer(String text, String enhancer) {
        if("tolower".equals(enhancer)) {
            return text.toLowerCase();
        } else if("toupper".equals(enhancer)) {
            return text.toUpperCase();
        } else if("trim".equals(enhancer)) {
            return text.trim();
        } else if("ascii".equals(enhancer)) {
            return Any2Ascii.map(text);
        } else {
            return text;
        }

    }

    protected String[] path(String path) {
        List<String> list = Split.string(path).bySubstringToList(".");
        return list.toArray(new String[]{});
    }
}
