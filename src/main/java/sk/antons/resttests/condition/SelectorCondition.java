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
package sk.antons.resttests.condition;

import sk.antons.json.JsonValue;
import sk.antons.resttests.condition.check.Check;
import sk.antons.resttests.http.HttpResponse;

/**
 * Simple atomic condition which performs individual check.
 * SelectorCondition is pair one selector for identifying text which must be
 * checked and one simple check which checks that value.
 *
 * Selector is interpreted by TextResolver instance to determine value from response.
 * Check is individual check, which validate resulted text.
 *
 *
 * Use form {"": {check1}, "selector2": {check2}, ...}
 * @author antons
 */
public class SelectorCondition extends Condition {

    String selector;
    Check check;

    @Override
    public boolean validate(HttpResponse response) {
        String text = resolver().resolve(response, selector);
        return check.validate(text);
    }

    @Override
    public JsonValue toJson() {
        return check.toJson();
    }

    @Override
    public String name() {
        return selector;
    }

    public static SelectorCondition of(String name, JsonValue jv) {
        if(jv == null) throw new IllegalArgumentException("selector condition must be object null" );
        if(!jv.isObject()) throw new IllegalArgumentException("root condition must be object " + jv.toCompactString());
        SelectorCondition c = new SelectorCondition();
        c.selector = name;
        c.check = Check.parse(jv);
        return c;
    }

}
