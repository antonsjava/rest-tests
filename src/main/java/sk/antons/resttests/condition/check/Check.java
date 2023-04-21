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
package sk.antons.resttests.condition.check;

import java.util.ArrayList;
import java.util.List;
import sk.antons.jaul.Get;
import sk.antons.json.JsonAttribute;
import sk.antons.json.JsonValue;

/**
 * Simple check for text value. Ti returns true of false for given text.
 * @author antons
 */
public abstract class Check {


    public abstract boolean validate(String value);

    protected abstract String name();
    public abstract JsonValue toJson();
    protected abstract void parseValue(JsonValue value);


    public static Check parse(JsonValue jv) {
        if(jv == null) return null;
        if(!jv.isObject()) throw new IllegalArgumentException("check must be object " + jv.toCompactString());

        List<String> attrs = new ArrayList<>();
        for(JsonAttribute attr : jv.asObject().toList()) {
            attrs.add(attr.name().stringValue());
        }
        attrs.remove("result");
        if(attrs.size() != 1) throw new IllegalArgumentException("check must have only one attr" + jv.toCompactString());
        JsonAttribute attr = jv.asObject().attr(Get.first(attrs));
        Check c = instanceByName(attr.name().stringValue());
        c.parseValue(attr.value());
        return c;
    }

    protected static Check instanceByName(String name) {
        if(name == null) throw new IllegalArgumentException("unknown check name " + name);
        boolean negate = false;
        if(name.startsWith("not ")) {
            name = name.substring(4);
            negate = true;
        }
        Check c = null;
        switch(name) {
            case "and": c = new AndCheck(); break;
            case "or": c = new OrCheck(); break;
            case "not": c = new NotCheck(); break;
            case "eq": c = new EqCheck(negate); break;
            case "contains": c = new ContainsCheck(negate); break;
            case "match": c = new MatchCheck(negate); break;
            case "empty": c = new EmptyCheck(negate); break;
            case "ends": c = new EndsCheck(negate); break;
            case "starts": c = new StartsCheck(negate); break;
            case "gt": c = new GtCheck(negate); break;
            case "gte": c = new GteCheck(negate); break;
            case "lt": c = new LtCheck(negate); break;
            case "lte": c = new LteCheck(negate); break;
            default: throw new IllegalArgumentException("unknown check name " + name);
        }
        if((!(c instanceof SingleCheck)) && negate) {
            throw new IllegalArgumentException("it is not possible to negate check " + name);
        }
        return c;
    }
}
