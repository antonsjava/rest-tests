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

import sk.antons.json.JsonAttribute;
import sk.antons.json.JsonFactory;
import sk.antons.json.JsonObject;
import sk.antons.json.JsonValue;
import sk.antons.resttests.http.HttpResponse;

/**
 * Just like 'and' condition. Ensures that all given conditions are true.
 * It simplified 'and' for individual selector conditions. Each json attribute
 * is one given condition. It is root condition for all conditions defined for
 * testing.
 * Use form {"selector1": {check1}, "selector2": {check2}, ...}
 * @author antons
 */
public class RootCondition extends ContainerCondition {

    @Override
    public String name() { return ""; }


    @Override
    public boolean validate(HttpResponse response) {
        for(Condition condition : conditions) {
            if(!condition.validate(response)) return false;
        }
        return true;
    }

    public static RootCondition of(JsonValue jv) {
        if(jv == null) return null;
        if(!jv.isObject()) throw new IllegalArgumentException("root condition must be object " + jv.toCompactString());
        RootCondition c = new RootCondition();
        for(JsonAttribute attr : jv.asObject().toList()) {
            String aname = attr.name().stringValue();
            Condition cc = instanceByName(aname, attr.value());
            c.conditions.add(cc);
        }
        return c;
    }


    @Override
    public JsonValue toJson() {
        JsonObject jo = JsonFactory.object();
        for(Condition condition : conditions) {
            jo.add(condition.name(), condition.toJson());
        }
        return jo;
    }





}
