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

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.antons.jaul.Is;

/**
 *
 * @author antons
 */
public class PathMatcher {
    private static Logger log = LoggerFactory.getLogger(PathMatcher.class);

    private Pattern pattern = null;
    private boolean directory = false;

    public PathMatcher(String matcher) {
        if(!Is.empty(matcher)) {
            directory = matcher.contains("/");
            try {
                boolean skipnextasterix = false;
                StringBuilder sb = new StringBuilder();
                sb.append('^');
                for(int i = 0; i < matcher.length(); i++) {
                    char c = matcher.charAt(i);
                    if(c == '*') {
                        if(skipnextasterix) continue;
                        boolean full = false;
                        if((i+1) < matcher.length()) {
                            char cc = matcher.charAt(i+1);
                            full = (cc == '*');
                        }

                        if(full) {
                            skipnextasterix = true;
                            i++;
                            sb.append(".*");
                            continue;
                        } else {
                            sb.append("[^/]*");
                            continue;
                        }
                    } else if( (c == '.') || (c == '/') || (c == '^') || (c == '$')) {
                        skipnextasterix = false;
                        sb.append("\\").append(c);
                        continue;
                    }
                    skipnextasterix = false;
                    sb.append(c);
                }
                sb.append('$');
                pattern = Pattern.compile(sb.toString());
            } catch(Exception e) {
                log.info("unable to parse path matcher '{}' - accept all", matcher);
            }
        }
    }
    public static PathMatcher instance(String matcher) { return new PathMatcher(matcher); }

    public boolean isDirectoryMatch() { return directory; }

    public boolean match(String value) {
        if(pattern == null) return true;
        if(Is.empty(value)) return false;
        return pattern.matcher(value).matches();
    }

    @Override
    public String toString() {
        return "PathMatcher{" + pattern + '}';
    }


}
