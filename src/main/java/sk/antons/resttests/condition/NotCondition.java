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
 * 'not' condition. Negate result of other condition.
 * Use form {'not': {}}
 * @author antons
 */
public class NotCondition extends ContainerCondition {

    @Override
    public boolean validate(HttpResponse response) {
        for(Condition condition : conditions) {
            return !condition.validate(response);
        }
        return false;
    }

    @Override
    public JsonValue toJson() {
        JsonArray arr = JsonFactory.array();
        for(Condition condition : conditions) {
            return condition.toJson();
        }
        return null;
    }

    @Override
    public String name() { return "not"; }

    public static NotCondition of(JsonValue jv) {
        if(jv == null) return null;
        if(!jv.isObject()) throw new IllegalArgumentException("not condition must be object " + jv.toCompactString());
        NotCondition c = new NotCondition();
        c.conditions.add(RootCondition.of(jv));
        return c;
    }

}
