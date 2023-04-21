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

import sk.antons.jaul.Get;
import sk.antons.jaul.Is;

/**
 * Check if string interpreted as number is less or equal than given value.
 * Use form {"lte": "100"}
 * @author antons
 */
public class LteCheck extends LiteralCheck {


    public LteCheck(boolean negate) {
        super(negate);
    }

    @Override
    protected boolean validateImpl(String value) {
        if(Is.empty(value)) value = "";
        if(pattern.contains(".") || value.contains(".")) {
            return Get.from(value, true, true).doubleValue(Integer.MAX_VALUE) <= Get.from(pattern, true, true).doubleValue(Integer.MIN_VALUE);
        } else {
            return Get.from(value, true, true).intValue(Integer.MAX_VALUE) <= Get.from(pattern, true, true).intValue(Integer.MIN_VALUE);
        }
    }

    @Override
    protected String simpleName() { return "lte"; }


}
