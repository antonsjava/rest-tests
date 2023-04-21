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

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import sk.antons.jaul.util.Resource;
import sk.antons.jaul.util.TextFile;

/**
 *
 * @author antons
 */
public class TemplateTest {

    @Test
	public void properties() throws Exception {
        Template template = Template.instance("url").content("toto ${pokus} je ${pokus} ${outer.${number}} pokus");
        Properties properties = new Properties();
        properties.put("pokus", "ppp");
        properties.put("number", "1");
        properties.put("outer.1", "22222");
        String text = template.content(properties);
        Assert.assertNotNull(text);
        Assert.assertEquals("toto ppp je ppp 22222 pokus", text);
    }

    @Test
	public void defproperties() throws Exception {
        Template template = Template.instance("url").content("request ${unique.request} global ${unique.global} je request ${unique.request} global ${unique.global} pokus");
        Properties properties = new Properties();
        properties.put("pokus", "ppp");
        properties.put("number", "1");
        properties.put("outer.1", "22222");
        String text = template.content(properties);
        Assert.assertNotNull(text);
        Assert.assertNotEquals("toto ppp je ppp 22222 pokus", text);
        text = template.content();
        Assert.assertNotNull(text);
        Assert.assertNotEquals("toto ppp je ppp 22222 pokus", text);
    }

}
