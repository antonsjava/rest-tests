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

import sk.antons.json.JsonArray;
import sk.antons.json.JsonFactory;
import sk.antons.json.JsonObject;
import sk.antons.json.JsonValue;

/**
 * 'and' check which ensures that all given checks returns true.
 * Use form "and": [{"gt": "10"}, {"lt": "100"}]
 * @author antons
 */
public class AndCheck extends ContainerCheck {

    @Override
    public boolean validate(String value) {
        for(Check check : checks) {
            if(!check.validate(value)) return false;
        }
        return true;
    }

    @Override
    protected String name() { return "and"; }

    @Override
    public JsonValue toJson() {
        JsonObject jo = JsonFactory.object();
        JsonArray arr = JsonFactory.array();
        jo.add("and", arr);
        for(Check check : checks) {
            arr.add(check.toJson());
        }
        return jo;
    }

    @Override
    protected void parseValue(JsonValue jv) {
        if(jv == null) throw new IllegalArgumentException(this.getClass().getSimpleName() + " check must be not null");
        if(!jv.isArray()) throw new IllegalArgumentException(this.getClass().getSimpleName() + " check must be array " + jv.toCompactString());
        for(JsonValue jvv : jv.asArray().toList()) {
            checks.add(Check.parse(jvv));
        }
    }




}
