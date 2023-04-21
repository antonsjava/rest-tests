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

import sk.antons.jaul.Is;

/**
 * Check if string contains given value
 * Use form {"contains": "given value"}
 * @author antons
 */
public class ContainsCheck extends LiteralCheck {


    public ContainsCheck(boolean negate) {
        super(negate);
    }

    @Override
    protected boolean validateImpl(String value) {
        if(Is.empty(value)) value = "";
        return value.contains(pattern);
    }

    @Override
    protected String simpleName() { return "contains"; }


}
