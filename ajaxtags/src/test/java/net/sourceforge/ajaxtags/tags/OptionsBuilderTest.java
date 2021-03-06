/*
 * Copyright 2007-2012 AjaxTags-Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.ajaxtags.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for OptionsBuilder.
 */
public class OptionsBuilderTest {

    private static final String DELIMITER = OptionsBuilder.getOptionsBuilder()
            .getOptionsDelimiter();

    private OptionsBuilder options;

    /**
     * Set up.
     */
    @Before
    public void setUp() {
        options = OptionsBuilder.getOptionsBuilder();
    }

    /**
     * Test method for {@link OptionsBuilder#getOptionsBuilder(OptionsBuilder)}.
     */
    @Test
    public void testGetOptionsBuilder() {
        assertNotNull("OptionsBuilder must be created successfully", options);
        assertEquals("Empty OptionsBuilder 1", "", options.toString());
        final OptionsBuilder ob1 = OptionsBuilder.getOptionsBuilder(options);
        assertEquals("Empty OptionsBuilder 2", "", ob1.toString());
    }

    /**
     * Test method for {@link OptionsBuilder#add(String, boolean)}.
     */
    @Test
    public void testAddStringBoolean() {
        options.add("parameter", true);
        options.add("parameter with space", false);
        assertEquals("toString", "parameter: true" + DELIMITER + "parameter with space: false",
                options.toString());
    }

    /**
     * Test method for {@link OptionsBuilder#add(String, int)}.
     */
    @Test
    public void testAddStringInt() {
        final String param1 = "test1-param1";
        final String expected1 = "test1-param1: -1";

        options.add(param1, -1);
        assertEquals("int option", expected1, options.toString());
        options.add(param1, 0); // immutable option, should not change
        assertEquals("int immutable option", expected1, options.toString());

        options.add("test1-param2", 0).add("test1-param3", 1);
        assertEquals("int options", expected1 + DELIMITER + "test1-param2: 0" + DELIMITER
                + "test1-param3: 1", options.toString());
    }

    /**
     * Test method for {@link OptionsBuilder#add(String, String, boolean)}.
     */
    @Test
    public void testAddStringStringBoolean() {
        options.add("test2-param1", "", false);
        options.add("test2-param2", "", true);
        options.add("test2-param3", "string3", false);
        options.add("test2-param4", "string4", true);

        assertEquals("string options", StringUtils.join(Arrays.asList("test2-param1: ",
                "test2-param2: \"\"", "test2-param3: string3", "test2-param4: \"string4\""),
                DELIMITER), options.toString());
    }

    /**
     * Test method for {@link OptionsBuilder#remove(String)}.
     */
    @Test
    public void testRemove() {
        final String param1 = "test3-param1";

        options.remove("test3-param123");
        assertEquals("Empty OptionsBuilder 3", "", options.toString());
        options.add(param1, true).remove(param1);
        assertEquals("Empty OptionsBuilder 4", "", options.toString());
        options.add(param1, true).add("test3-param2", true).remove(param1);
        assertEquals("OptionsBuilder", "test3-param2: true", options.toString());
    }

}
