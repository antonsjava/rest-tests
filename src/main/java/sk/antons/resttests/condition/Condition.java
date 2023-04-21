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
import sk.antons.resttests.http.HttpResponse;

/**
 * General class for all types of condifions.
 * @author antons
 */
public abstract class Condition {

    protected TextResolver resolver = null;

    /**
     * Sets utility for resolving strings for individual conditions.
     * SimpleTextResolver is used by default.
     * @param resolver Instance of text resolver.
     * @return this
     */
    public Condition resolver(TextResolver resolver) { this.resolver = resolver; return this; }
    protected TextResolver resolver() {
        if(resolver == null) resolver = new SimpleTextResolver();
        return resolver;
    }

    /**
     * Main implementation method for subclasses. It validates this condition for
     * given response
     * @param response Response for which condition must be evaluated.
     * @return true if condition passes
     */
    public abstract boolean validate(HttpResponse response);

    /**
     * Converts this condition to json.
     * @return json object.
     */
    public abstract JsonValue toJson();

    /**
     * Name used to identify condition. Each subtype defines own name
     * (like 'eq', 'contains', .....)
     * @return name of this condition
     */
    public abstract String name();


    protected static Condition instanceByName(String name, JsonValue jv) {
        if(name == null) throw new IllegalArgumentException("unknown condition name " + name);
        switch(name) {
            case "and": return AndCondition.of(jv);
            case "or": return OrCondition.of(jv);
            case "not": return NotCondition.of(jv);
            default: return SelectorCondition.of(name, jv);
        }
    }
}
