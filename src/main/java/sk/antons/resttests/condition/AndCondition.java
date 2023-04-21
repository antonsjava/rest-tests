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
 * 'and' condition. Ensures that all given conditions are true.
 * Use form {"and": [{}, {}, {}, ...]}
 * @author antons
 */
public class AndCondition extends ContainerCondition {

    @Override
    public boolean validate(HttpResponse response) {
        for(Condition condition : conditions) {
            if(!condition.validate(response)) return false;
        }
        return true;
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
    public String name() { return "and"; }

    public static AndCondition of(JsonValue jv) {
        if(jv == null) return null;
        if(!jv.isArray()) throw new IllegalArgumentException("and condition must be array " + jv.toCompactString());
        AndCondition c = new AndCondition();
        for(JsonValue jsonValue : jv.asArray().toList()) {
            c.conditions.add(RootCondition.of(jsonValue));
        }
        return c;
    }

}
