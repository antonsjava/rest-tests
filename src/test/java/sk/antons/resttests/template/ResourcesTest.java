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
package sk.antons.resttests.template;

import org.junit.Assert;
import org.junit.Test;
import sk.antons.resttests.template.Resources.ResourceWithUrl;

/**
 *
 * @author antons
 */
public class ResourcesTest {

    @Test
	public void file() throws Exception {
        Resources resources = Resources.builder()
            .addSource("./src/test/example")
            .addSource("./src/test/example/simple")
            .build();
        ResourceWithUrl<String> txt = resources.string("resource-body.txt", "utf-8");
        Assert.assertNotNull(txt);
        Assert.assertNotNull(txt.content());
        txt = resources.string("template/books.txt", "utf-8");
        Assert.assertNotNull(txt);
        Assert.assertNotNull(txt.content());
        txt = resources.string("../template/books.txt", "utf-8");
        Assert.assertNotNull(txt);
        Assert.assertNotNull(txt.content());
    }

    @Test
	public void classpath() throws Exception {
        Resources resources = Resources.builder()
            .addSource("classpath:")
            .addSource("classpath:templates/lists")
            .build();
        ResourceWithUrl<String> txt = resources.string("titles.list", "utf-8");
        Assert.assertNotNull(txt);
        Assert.assertNotNull(txt.content());
        txt = resources.string("example-post.json", "utf-8");
        Assert.assertNotNull(txt);
        Assert.assertNotNull(txt.content());
        txt = resources.string("../parent.txt", "utf-8");
        Assert.assertNotNull(txt);
        Assert.assertNotNull(txt.content());
    }
}
