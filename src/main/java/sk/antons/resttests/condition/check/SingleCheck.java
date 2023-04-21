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

/**
 * Generic class for individual checks. Each individual check name can be
 * prefixed with 'not ' to simply negate result. (like 'eq' check can be
 * 'not eq')
 * @author antons
 */
public abstract class SingleCheck extends Check {

    protected boolean negate = false;
    protected Boolean result;
    protected String value;
    public Boolean result() { return result; }

    public SingleCheck(boolean negate) { this.negate = negate; }

    protected abstract String simpleName();

    protected abstract boolean validateImpl(String value);

    @Override
    public boolean validate(String value) {
        this.value = value;
        boolean rv = validateImpl(value);
        result = negate ? !rv : rv;
        return result;
    }



    @Override
    protected String name() {
        return (negate ? "not " : "") + simpleName();
    }

}
