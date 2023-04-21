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

import sk.antons.json.JsonFactory;
import sk.antons.json.JsonObject;
import sk.antons.json.JsonValue;

/**
 * One individual header value.
 * @author antons
 */
public abstract class LiteralCheck extends SingleCheck {

    String pattern;

    public LiteralCheck(boolean negate) {
        super(negate);
    }


    @Override
    public JsonValue toJson() {
        JsonObject jo = JsonFactory.object();
        jo.add(name(), JsonFactory.stringLiteral(pattern));
        if(value != null) jo.add("value", JsonFactory.stringLiteral(value));
        if(result != null) jo.add("result", JsonFactory.boolLiteral(result));
        return jo;
    }

    @Override
    protected void parseValue(JsonValue jv) {
        if(jv == null) throw new IllegalArgumentException(this.getClass().getSimpleName() + " check must be not null");
        if(!jv.isLiteral()) throw new IllegalArgumentException(this.getClass().getSimpleName() + " check must be literal " + jv.toCompactString());
        if(jv.isNullLiteral()) {
            pattern = "";
        } else {
            pattern = jv.asLiteral().stringValue();
        }
    }


}
