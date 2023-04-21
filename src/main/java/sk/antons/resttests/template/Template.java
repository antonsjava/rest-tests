/*
 *
 */
package sk.antons.resttests.template;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sk.antons.jaul.Is;

/**
 * Helper class for reading resource with parameters.
 * Parameter is placeholder in template '${placeholder}'. Such placeholder are
 * replaced by given properties values.
 * All occurences of ${unique.request} are replaced by one unique number.
 * Each occurence of ${unique.global} is replaced by globaly unique numbers.
 * @author antons
 */
public class Template {
    String url;
    String content;

    public Template(String url) {
        this.url = url;
    }
    public static Template instance(String url) { return new Template(url); }
    public Template content(String value) { this.content = value; return this; }

    public String url() { return url; }
    public String content() {
        return defaultProps(content);
    }
    public String content(Properties props) {
        if(Is.empty(content)) return content();
        if(props == null) return content();
        String text = defaultProps(content);
        Set<String> placeholders = findPlaceholders(text);
        while(!Is.empty(placeholders)) {
            for(String placeholder : placeholders) {
                String value = props.getProperty(placeholder);
                if(value == null) value = "#{"+placeholder+"}";
                text = text.replace("${"+placeholder+"}", value);
            }
            placeholders = findPlaceholders(text);
        }

        return text;
    }

    private static long counter = System.currentTimeMillis();
    public String defaultProps(String text) {
        if(Is.empty(text)) return text;
        text = text.replace("${unique.request}", "" + counter++);
        int pos = text.indexOf("${unique.global}");
        while(pos > -1) {
            text = text.substring(0, pos) + counter++ + text.substring(pos+16);
            pos = text.indexOf("${unique.global}");
        }
        return text;
    }

    private static Set<String> findPlaceholders(String text) {
        if(Is.empty(text)) return Set.of();
        Set<String> set = new HashSet<>();
        Matcher matcher = Pattern.compile("\\$\\{([^$}]+)\\}").matcher(text);
        while(matcher.find()) {
            set.add(matcher.group(1));
        }
        return set;
    }
}
