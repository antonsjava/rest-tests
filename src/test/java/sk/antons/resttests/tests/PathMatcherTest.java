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

/**
 *
 * @author antons
 */
public class PathMatcherTest {


    @Test
	public void serializing() throws Exception {
        Assert.assertTrue(PathMatcher.instance("").match("/"));
        Assert.assertTrue(PathMatcher.instance(null).match(""));
        Assert.assertTrue(PathMatcher.instance(null).match(null));
        Assert.assertTrue(PathMatcher.instance("/jablko/hruska.txt").match("/jablko/hruska.txt"));
        Assert.assertTrue(PathMatcher.instance("/*ab*lk*/h*ruska.t*xt").match("/jablko/hruska.txt"));
        Assert.assertTrue(PathMatcher.instance("/*/hruska.txt").match("/jablko/hruska.txt"));
        Assert.assertTrue(PathMatcher.instance("/*/hr*a.*").match("/jablko/hruska.txt"));
        Assert.assertTrue(PathMatcher.instance("**/test/**/hr*a.*").match("/p1/p2/test/t4/jablko/hruska.txt"));
        Assert.assertFalse(PathMatcher.instance("**/test/**/hr*a.*").match("/p1/p2/test/t4/jablko/hruskb.txt"));
        Assert.assertFalse(PathMatcher.instance("**/test/**/hr*a.*").match("/p1/p2/test1/t4/jablko/hruska.txt"));
        Assert.assertFalse(PathMatcher.instance("**/*.-testdd2.json").match("/home/projects/xit/zvjs/integration/tests/src/rest-tests/example/example-post-json-test.json"));
    }
}
