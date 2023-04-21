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
package sk.antons.resttests.tests;

import org.junit.Assert;
import org.junit.Test;
import sk.antons.jaul.util.Resource;
import sk.antons.jaul.util.TextFile;
import sk.antons.json.util.JsonFormat;
import sk.antons.resttests.http.HttpMethod;
import sk.antons.resttests.http.HttpPayload;

/**
 *
 * @author antons
 */
public class SerializationTest {


    @Test
	public void serializing() throws Exception {
        String json = TextFile.read(Resource.url("classpath:example-post.json").inputStream(), "utf-8");
        RestTest testik = RestTestDeserializer.test(json);
        System.out.println(" -----------");
        System.out.println(RestTestSerializer.string(testik));
        System.out.println(" -----------");
        Assert.assertNotNull("no test", testik);
        Assert.assertNotNull("no request", testik.request());
        Assert.assertEquals("url", "www.google.com", testik.request().url());
        Assert.assertEquals("method", HttpMethod.POST, testik.request().method());
        Assert.assertEquals("query", 2, testik.request().queryparams().all().size());
        Assert.assertEquals("header", 2, testik.request().headers().all().size());
        Assert.assertEquals("payload", HttpPayload.Type.JSON, testik.request().payload().type());

        String tojson = RestTestSerializer.json(testik).toCompactString();
        System.out.println(" ====== "+ json);
        System.out.println(" ====== "+ tojson);
        Assert.assertEquals("all", JsonFormat.from(json).noindent().toText(), tojson);
    }
}
