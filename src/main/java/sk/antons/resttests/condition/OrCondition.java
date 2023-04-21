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

import sk.antons.json.JsonArray;
import sk.antons.json.JsonFactory;
import sk.antons.json.JsonValue;
import sk.antons.resttests.http.HttpResponse;

/**
 * 'or' condition. Ensures that at least one given conditions is true.
 * Use form {"or": [{}, {}, {}, ...]}
 */
public class OrCondition extends ContainerCondition {

    @Override
    public boolean validate(HttpResponse response) {
        for(Condition condition : conditions) {
            if(condition.validate(response)) return true;
        }
        return false;
    }

    @Override
    public JsonValue toJson() {
        JsonArray arr = JsonFactory.array();
        for(Condition condition : conditions) {
            arr.add(condition.toJson());
        }
        return arr;
    }

    @Override
    public String name() { return "or"; }

    public static OrCondition of(JsonValue jv) {
        if(jv == null) return null;
        if(!jv.isArray()) throw new IllegalArgumentException("or condition must be array " + jv.toCompactString());
        OrCondition c = new OrCondition();
        for(JsonValue jsonValue : jv.asArray().toList()) {
            c.conditions.add(RootCondition.of(jsonValue));
        }
        return c;
    }

}
