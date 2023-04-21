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

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import sk.antons.jaul.Is;
import sk.antons.resttests.http.HttpRequest;
import sk.antons.resttests.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.resttests.http.call.HttpRequestEnhancer;
import sk.antons.resttests.template.Resources;
import sk.antons.resttests.template.Template;
import sk.antons.resttests.template.TemplateLoader;


/**
 * RestTest
 * @author antons
 */
public class RestTests {
    private static Logger log = LoggerFactory.getLogger(RestTests.class);

    private Resources resources;
    private String encoding = "utf-8";
    private List<RestTest> tests = new ArrayList<>();
    private List<HttpRequestEnhancer> enhancers = new ArrayList<>();


    public RestTests add(RestTest test) {
        if(test != null) this.tests.add(test);
        return this;
    }
    public RestTests enhancer(HttpRequestEnhancer enhancer) {
        if(enhancer != null) this.enhancers.add(enhancer);
        return this;
    }

    public void processTests(Function<HttpRequest, HttpResponse> caller, RestTestProcessingListener... listeners) {
        initTests();
        for(RestTest test : tests) {
            try {
                test.enhancers(enhancers);
                test.resources(resources);
                test.encoding(encoding);
                test.process(caller, listeners);
            } catch(Throwable e) {
                log.info("test {}.{} processing failed {}", test.category(), test.name(), e.toString());
            }
        }
    }

    private void initTests() {
        if(Is.empty(tests)) return;
        String defaultCategory = "tests-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        for(int i = 0; i < tests.size(); i++) {
            RestTest test = tests.get(i);
            if(test.category() == null) test.category(defaultCategory);
            if(test.name()== null) test.name("test-" + (i+1) );
        }
    }


    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private Resources resources;
        private Properties props;
        private String encoding = "utf-8";

        private List<RestTest> directTests = new ArrayList<>();
        private List<String> files = new ArrayList<>();
        private List<PathMatcher> includes = new ArrayList<>();
        private List<PathMatcher> excludes = new ArrayList<>();

        public Builder resources(Resources value) { this.resources = value; return this; }
        public Builder properties(Properties value) { this.props = value; return this; }
        public Builder encoding(String value) { this.encoding = value; return this; }
        public Builder from(RestTest... tests) {
            if(!Is.empty(tests)) {
                for(RestTest test : tests) {
                    if(test != null) directTests.add(test);
                }
            }
            return this;
        }
        public Builder from(String filename) {
            if(!Is.empty(filename)) files.add(filename);
            return this;
        }
        public Builder include(String filenamepattern) {
            if(!Is.empty(filenamepattern)) includes.add(PathMatcher.instance(filenamepattern));
            return this;
        }
        public Builder exclude(String filenamepattern) {
            if(!Is.empty(filenamepattern)) excludes.add(PathMatcher.instance(filenamepattern));
            return this;
        }

        private boolean match(File file) {
            boolean includeok = true;
            if(!Is.empty(includes)) {
                includeok = false;
                for(PathMatcher include : includes) {
                    if(include.isDirectoryMatch()) {
                        if(include.match(file.getAbsolutePath())) {
                            includeok = true;
                            break;
                        }
                    } else {
                        if(include.match(file.getName())) {
                            includeok = true;
                            break;
                        }
                    }
                }
            }
            if(!includeok) return false;
            if(!Is.empty(excludes)) {
                for(PathMatcher include : excludes) {
                    if(include.isDirectoryMatch()) {
                        if(include.match(file.getAbsolutePath())) {
                            return false;
                        }
                    } else {
                        if(include.match(file.getName())) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private void init() {
            if(encoding == null) encoding = "utf-8";
            if(props == null) props = new Properties();
            if(resources == null) resources = Resources.builder().build();
        }

        public RestTests buildEmpty() {
            init();
            RestTests rt = new RestTests();
            rt.resources = resources;
            rt.encoding = encoding;
            return rt;
        }

        public RestTests build() {
            init();
            RestTests rt = new RestTests();
            rt.resources = resources;
            rt.encoding = encoding;
            if(!Is.empty(directTests)) {
                for(RestTest test : directTests) {
                    if(test != null) rt.tests.add(test);
                }
            }

            if(!Is.empty(files)) {
                for(String file : files) {
                    File f = new File(file);
                    if(!f.exists()) {
                        log.debug("specified test root not exist {}", file);
                        continue;
                    }
                    if(f.isFile()) {
                        RestTest test = fromFile(f);
                        if(test != null) rt.tests.add(test);
                    } else {
                        traverse(rt, f);
                    }
                }
            }

            return rt;
        }


        private RestTest fromFile(File file) {
            try {
                TemplateLoader loader = TemplateLoader.instance().encoding(encoding).resources(resources);
                Template template = loader.load(file.getAbsolutePath());
                RestTest test = RestTestDeserializer.test(template.content(props));
                test.identification(file.getAbsolutePath());
                if(Is.empty(test.name())) test.name(file.getName());
                if(Is.empty(test.category())) test.category(file.getParent());
                log.debug("file loaded as test {}", file.getAbsolutePath());
                return test;
            } catch(Throwable e) {
                log.info("unable to load file {} because of {}", file.getAbsolutePath(), e);
                return null;
            }
        }

        private void traverse(RestTests tests, File directory) {
            File[] files = directory.listFiles();
            if(files == null) return;
            Arrays.sort(files, FileComparator.instance());
            for(File file : files) {
                if(!file.isFile()) continue;
                if(!match(file)) continue;
                RestTest test = fromFile(file);
                if(test == null) continue;
                if(Is.empty(test.category())) test.category(directory.getAbsolutePath());
                if(Is.empty(test.name())) test.name(file.getName());
                tests.add(test);
            }
            for(File file : files) {
                if(!file.isDirectory()) continue;
                traverse(tests, file);
            }
        }
    }

    private static class FileComparator implements Comparator<File> {

        public static FileComparator instance() { return new FileComparator(); }

        @Override
        public int compare(File f1, File f2) {
            String s1 = f1 == null ? "" : f1.getName();
            String s2 = f2 == null ? "" : f2.getName();
            return s1.compareTo(s2);
        }

    }


    public static void main(String[] argv) {
        System.out.println(" ---------------------------------");
        RestTests tests = RestTests.builder().include("**/*.json").exclude("*post*").from("src/test").build();
        for(RestTest test : tests.tests) {
            System.out.println(" -- test: " + test.category() + "." + test.name());
        }
        System.out.println(" ---------------------------------");
    }

}
