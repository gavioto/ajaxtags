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
package net.sourceforge.ajaxtags.helpers;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for XMLUtils.
 *
 * @version $Id$
 */
public class XMLUtilsTest {

    private static final String VALID_NAME_START_CHARS = ":AZ_az\u00C0\u00D6\u00D8\u00F6\u00F8\u02FF"
            + "\u0370\u037D\u037F\u1FFF\u200C\u200D\u2070\u218F\u2C00\u2FEF"
            + "\u3001\uD7FF\uF900\uFDCF\uFDF0\uFFFD";

    private static final String INVALID_NAME_START_CHARS = "-.09\u00D7\u00F7\u0300\u037E\u2000\u3000\uD800";

    private static final String VALID_NAME_CHARS = VALID_NAME_START_CHARS + "-.09\u00B7"
            + "\u0300\u036F\u203F\u2040";

    /**
     * Test method for
     * {@link net.sourceforge.ajaxtags.helpers.XMLUtils#isCharInRange(int, int, int)}.
     */
    @Test
    public void testIsCharInRange() {
        assertTrue(XMLUtils.isCharInRange(0, 0, 0));
        assertTrue(XMLUtils.isCharInRange('0', '0', '9'));
    }

    /**
     * Test method for {@link net.sourceforge.ajaxtags.helpers.XMLUtils#isValidNameStartChar(int)}.
     */
    @Test
    public void testIsValidXmlNameStartChar() {
        // [\u10000-\uEFFFF] ?
        for (int i = 0; i < VALID_NAME_START_CHARS.length(); i++) {
            assertTrue(XMLUtils.isValidXmlNameStartChar(VALID_NAME_START_CHARS.codePointAt(i)));
        }
        for (int i = 0; i < INVALID_NAME_START_CHARS.length(); i++) {
            assertFalse(XMLUtils.isValidXmlNameStartChar(INVALID_NAME_START_CHARS.codePointAt(i)));
        }
    }

    /**
     * Test method for {@link net.sourceforge.ajaxtags.helpers.XMLUtils#isValidNameChar(int)}.
     */
    @Test
    public void testIsValidXmlNameChar() {
        for (int i = 0; i < VALID_NAME_CHARS.length(); i++) {
            assertTrue(XMLUtils.isValidXmlNameChar(VALID_NAME_CHARS.codePointAt(i)));
        }
    }

}
