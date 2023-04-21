/*
 *
 */
package sk.antons.resttests.template;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sk.antons.jaul.util.Resource;
import sk.antons.jaul.util.TextFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.jaul.Get;
import sk.antons.jaul.Is;
import sk.antons.jaul.Split;



/**
 * Helper class for loading templates
 * @author antons
 */
public class TemplateLoader {
    private static Logger log = LoggerFactory.getLogger(TemplateLoader.class);

    private String encoding = "utf-8";
    private Resources resources;

    public static TemplateLoader instance() { return new TemplateLoader(); }
    public TemplateLoader encoding(String value) { this.encoding = value; return this; }
    public TemplateLoader resources(Resources value) { this.resources = value; return this; }

    public Template load(String url) {
        Template template = Template.instance(url);
        String content = loadContent(url, resources, encoding, new ArrayList<>());
        template.content(content);
        return template;
    }

    private static String loadContent(String urlWithModifier, Resources resources, String encoding, List<String> stack) {

        String url = urlWithModifier;
        String modifier = null;
        int pos = urlWithModifier.indexOf('|');
        if(pos > -1) {
            url = urlWithModifier.substring(0, pos);
            modifier = urlWithModifier.substring(pos+1);
        }

        if(stack.contains(url)) {
            return "template "+url+" not loaded because of loop: " + stack;
        }

        stack.add(url);

        Resources.ResourceWithUrl<String> textcontent = loadResource(url, resources, encoding);
        String text = textcontent == null ? "" : textcontent.content();
        text = modify(text, modifier);

        Set<String> urls = findSubTemplates(text);
        if(!Is.empty(urls)) {
            String parentUrl = Resource.parentUrl(textcontent.url());
            Resources nres = Resources.builder().from(resources).addSource(parentUrl).build();
            for(String u : urls) {
                String txt = loadContent(u, nres, encoding, stack);
                if(txt == null) txt = "unknown: "+u;
                text = text.replace("@{"+u+"}", txt);
            }
        }

        stack.remove(stack.size()-1);

        return text;
    }

    private static Random random = new Random(System.currentTimeMillis());
    private static String modify(String text, String modifier) {
        if(Is.empty(text)) {
            return text;
        } if(Is.empty(modifier)) {
            return text;
        } if("rand".equals(modifier)) {
            List<String> lines = Split.string(text).bySubstringToList("\n");
            int num = random.nextInt(lines.size());
            return Get.nth(lines, num);
        } if(Get.from(modifier, true, true).intValue(-1) != -1) {
            List<String> lines = Split.string(text).bySubstringToList("\n");
            int num = Get.from(modifier, true, true).intValue(Integer.MAX_VALUE);
            return Get.nth(lines, num);
        } else {
            return text;
        }
    }



    private static Set<String> findSubTemplates(String text) {
        if(Is.empty(text)) return Set.of();
        Set<String> set = new HashSet<>();
        Matcher matcher = Pattern.compile("@\\{([^}]+)\\}").matcher(text);
        while(matcher.find()) {
            set.add(matcher.group(1));
        }
        return set;
    }

    private static Resources.ResourceWithUrl<String> loadResource(String url, Resources resources, String encoding) {
        try {
            if(resources != null) {
                return resources.string(url, encoding);
            } else {
                String text = TextFile.read(Resource.url(url).inputStream(), encoding);
                if(text == null) return null;
                return Resources.ResourceWithUrl.of(url, text);
            }
        } catch(Exception e) {
            //log.warn("unable to load {} because of {}", url, e.toString());
            return null;
        }
    }

    public static void main(String[] argv) {
        String text = "toto @{jedne} je pokus o nejake @{druhe}najdenie ";
        System.out.println(" -- " + findSubTemplates(text));
    }

}
