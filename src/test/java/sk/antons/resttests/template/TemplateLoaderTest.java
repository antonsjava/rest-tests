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
import sk.antons.jaul.util.Resource;
import sk.antons.jaul.util.TextFile;

/**
 *
 * @author antons
 */
public class TemplateLoaderTest {

    @Test
	public void load() throws Exception {
        TemplateLoader loader = TemplateLoader.instance()
            .resources(Resources.builder().addSource("classpath:templates").build());
        Template template = loader.load("parent.txt");
        Assert.assertNotNull(template);
        Assert.assertEquals("parent.txt", template.url());
        Assert.assertEquals("name: Frank Ing. ", template.content());
    }

    @Test
	public void resources() throws Exception {
        Resources resources = Resources.builder().addSource("classpath:templates").build();
        Resources.ResourceWithUrl<String> text = resources.string("parent.txt", "utf-8");
        Assert.assertNotNull(text);
        Assert.assertEquals("name: @{./lists/name.list|1} ", text.content());
    }

    @Test
	public void resource() throws Exception {
        String text = TextFile.read(Resource.url("classpath:templates/parent.txt").inputStream(), "utf-8");
        Assert.assertNotNull(text);
        Assert.assertEquals("name: @{./lists/name.list|1} ", text);
    }
}
