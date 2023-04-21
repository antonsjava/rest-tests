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

import org.junit.Assert;
import org.junit.Test;
import sk.antons.json.parse.JsonParser;
import sk.antons.resttests.http.HttpHeader;
import sk.antons.resttests.http.HttpMethod;
import sk.antons.resttests.http.HttpRequest;
import sk.antons.resttests.http.HttpResponse;

/**
 *
 * @author antons
 */
public class ConditionTest {

    private HttpRequest request() {
        return HttpRequest.of("niekam").method(HttpMethod.GET);
    }
    private HttpResponse response() {
        return HttpResponse.of(request())
            .status(200)
            .header(HttpHeader.of("pokus", "pokusna"))
            .body("{\n" +
"  \"param1\" : \"value1\",\n" +
"  \"result\" : [\n" +
"    {\"a\" : \"aa1\"},\n" +
"    {\"a\" : \"aa2\"},\n" +
"    {\"a\" : \"aa3\"}\n" +
"  ]\n" +
"}")
            ;
    }

    private static String okcond = "{\n" +
"  \"status\": {\"eq\": \"200\"},\n" +
"  \"header.pokus\": {\"eq\": \"pokusna\"},\n" +
"  \"body\": {\"contains\": \"value1\"},\n" +
"  \"body.param1\": {\"contains\": \"value1\"},\n" +
"  \"and\": [\n" +
"    {\"body.result.*.a.first\": {\"eq\": \"aa1\"}},\n" +
"    {\"body.result.*.a.last\": {\"eq\": \"aa3\"}},\n" +
"    {\"body.result.*.a.size\": {\"eq\": \"3\"}}\n" +
"  ]\n" +
"}";

    private static String failcond = "{\n" +
"  \"status\": {\"eq\": \"200\"},\n" +
"  \"header.pokus\": {\"eq\": \"pokusna\"},\n" +
"  \"body\": {\"contains\": \"value1\"},\n" +
"  \"body.param1\": {\"contains\": \"value1\"},\n" +
"  \"and\": [\n" +
"    {\"body.result.*.a.first\": {\"eq\": \"aa1\"}},\n" +
"    {\"body.result.*.a.last\": {\"eq\": \"aa3\"}},\n" +
"    {\"body.result.*.a.size\": {\"eq\": \"2\"}}\n" +
"  ]\n" +
"}";

    @Test
	public void okcond() throws Exception {
        Condition cond = RootCondition.of(JsonParser.parse(okcond));
        Assert.assertNotNull(cond);
        boolean result = cond.validate(response());
        Assert.assertTrue(result);
    }

    @Test
	public void failcond() throws Exception {
        Condition cond = RootCondition.of(JsonParser.parse(failcond));
        Assert.assertNotNull(cond);
        boolean result = cond.validate(response());
        Assert.assertFalse(result);
    }
}
