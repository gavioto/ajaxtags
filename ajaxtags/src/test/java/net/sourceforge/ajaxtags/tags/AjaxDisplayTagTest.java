/*
 * Copyright 2007-2011 AjaxTags-Team
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

import org.junit.Before;
import org.junit.Test;

/**
 * Test for AjaxDisplayTag.
 */
public class AjaxDisplayTagTest extends AbstractTagTest<AjaxDisplayTag> {

    private static final String BASE_URL = "http://localhost:8080/test/test.do";

    /**
     * Set up.
     *
     * @throws Exception
     *             if setUp fails
     */
    @Before
    public void setUp() throws Exception {
        setUp(AjaxDisplayTag.class);
        tag.setPagelinksClass("pagelinks");
        tag.setColumnClass("sortable");
        tag.setId("table");
    }

    @Test
    public void testNoRewriteAnchors() throws Exception {
        String html = "HTML content";
        String expected = wrap(html);
        assertContent(expected, tag.rewriteAnchors(html, "table"));

        html = "html <a>link</a>";
        expected = wrap(html);
        assertContent(expected, tag.rewriteAnchors(html, "table"));

        html = "html <a href=\"" + BASE_URL + "\">testRewriteAnchors</a>";
        expected = wrap(html);
        assertContent(expected, tag.rewriteAnchors(html, "table"));

        html = wrap("<span><a href='href1'>1</a></span> " + "<th><a href='href2'>2</a></th> "
                + "<th class='x'><a href='href'>3</a></th>"
                + "<th class='xsortable'><a href='href'>4</a></th>");
        expected = wrap(html);
        assertContent(expected, tag.rewriteAnchors(html, "table"));
    }

    @Test
    public void testRewriteAnchors() throws Exception {
        String onclick = " onclick=\"new AjaxJspTag.OnClick({" + "baseUrl: &quot;href&quot;"
                + DELIMITER + "eventBase: this" + DELIMITER + "requestHeaders: ['"
                + BaseAjaxBodyTag.TARGET_HEADER + "', 'table']" + DELIMITER
                + "target: &quot;table&quot;" + "});" + " return false;\"";
        String href = " href='javascript://nop/'";

        String html = wrap("<span class='pagelinks'><a href='href'>1</a></span> "
                + "<th class='sortable'><a href='href'>2</a></th>"
                + "<th class='sortable sorted'><a href='href'>3</a></th>");
        String expected = wrap(wrap("<span class='pagelinks'><a" + onclick + href
                + ">1</a></span> " + "<th class='sortable'><a" + onclick + href + ">2</a></th>"
                + "<th class='sortable sorted'><a" + onclick + href + ">3</a></th>"));
        assertContent(expected, tag.rewriteAnchors(html, "table"));
    }

}
