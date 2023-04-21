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

import org.junit.Assert;
import org.junit.Test;
import sk.antons.json.parse.JsonParser;

/**
 *
 * @author antons
 */
public class CheckTest {


    @Test
	public void eq() throws Exception {
        String json = "{\"eq\": \"100\"}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("100"));
        Assert.assertEquals(false, check.validate("10"));
        json = "{\"not eq\": \"100\"}";
        check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(false, check.validate("100"));
        Assert.assertEquals(true, check.validate("10"));
    }

    @Test
	public void contains() throws Exception {
        String json = "{\"contains\": \"100\"}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("000 100 200"));
        Assert.assertEquals(false, check.validate("10"));
        json = "{\"not contains\": \"100\"}";
        check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(false, check.validate("000 100 200"));
        Assert.assertEquals(true, check.validate("10"));
    }

    @Test
	public void empty() throws Exception {
        String json = "{\"empty\": \"100\"}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate(null));
        Assert.assertEquals(false, check.validate("10"));
        json = "{\"not empty\": \"100\"}";
        check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(false, check.validate(null));
        Assert.assertEquals(true, check.validate("10"));
    }

    @Test
	public void ends() throws Exception {
        String json = "{\"ends\": \"100\"}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("000 100"));
        Assert.assertEquals(false, check.validate("10"));
        json = "{\"not ends\": \"100\"}";
        check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(false, check.validate("000 100"));
        Assert.assertEquals(true, check.validate("10"));
    }

    @Test
	public void starts() throws Exception {
        String json = "{\"starts\": \"100\"}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("10000"));
        Assert.assertEquals(false, check.validate("10"));
        json = "{\"not starts\": \"100\"}";
        check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(false, check.validate("10000"));
        Assert.assertEquals(true, check.validate("10"));
    }

    @Test
	public void gt() throws Exception {
        String json = "{\"gt\": \"100\"}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("200"));
        Assert.assertEquals(false, check.validate("10"));
        json = "{\"not gt\": \"100\"}";
        check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(false, check.validate("200"));
        Assert.assertEquals(true, check.validate("10"));
    }

    @Test
	public void lt() throws Exception {
        String json = "{\"lt\": \"100\"}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("20"));
        Assert.assertEquals(false, check.validate("1000"));
        json = "{\"not lt\": \"100\"}";
        check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(false, check.validate("20"));
        Assert.assertEquals(true, check.validate("1000"));
    }

    @Test
	public void match() throws Exception {
        String json = "{\"match\": \".*2.*\"}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("20"));
        Assert.assertEquals(false, check.validate("1000"));
        json = "{\"not match\": \".*2.*\"}";
        check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(false, check.validate("20"));
        Assert.assertEquals(true, check.validate("1000"));
    }

    @Test
	public void and() throws Exception {
        String json = "{\"and\": [{\"gt\": \"10\"}, {\"lt\": \"100\"}]}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("20"));
        Assert.assertEquals(false, check.validate("1000"));
    }

    @Test
	public void or() throws Exception {
        String json = "{\"or\": [{\"lt\": \"10\"}, {\"gt\": \"100\"}]}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("1000"));
        Assert.assertEquals(false, check.validate("20"));
    }

    @Test
	public void not() throws Exception {
        String json = "{\"not\": {\"gt\": \"100\"}}";
        Check check = Check.parse(JsonParser.parse(json));
        Assert.assertNotNull(check);
        Assert.assertEquals(true, check.validate("10"));
        Assert.assertEquals(false, check.validate("204"));
    }
}
