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
package sk.antons.resttests.call;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import sk.antons.resttests.http.HttpMethod;
import sk.antons.resttests.http.HttpPayload;
import sk.antons.resttests.http.HttpRequest;
import sk.antons.resttests.http.HttpResponse;
import sk.antons.resttests.http.call.SimpleHttpCall;

/**
 *
 * @author antons
 */
@Ignore
public class CallTest {


    @Test
	public void post() throws Exception {
        HttpRequest request = HttpRequest.of("https://echo.zuplo.io")
            .method(HttpMethod.POST)
            .payload(HttpPayload.of("{\"pokus\": \"hodnota\"}"))
            ;
        System.out.println("--------------------------------");
        System.out.println(request);
        SimpleHttpCall call = SimpleHttpCall.instance();
        HttpResponse response = call.call(request);
        System.out.println("--------------------------------");
        System.out.println(response);

        Assert.assertNotNull("no response", response);
        Assert.assertEquals("status", 200, response.status());

    }
}
