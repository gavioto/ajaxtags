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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test for AjaxAnchorsTag.
 */
public class AjaxAnchorsTagTest extends AbstractTagTest<AjaxAnchorsTag> {

    private static final String BASE_URL = "http://localhost:8080/test/test.do";

    /**
     * Set up.
     *
     * @throws Exception
     *             if setUp fails
     */
    @Before
    public void setUp() throws Exception {
        setUp(AjaxAnchorsTag.class);
    }

    /**
     * Test method for {@link AjaxAnchorsTag#doEndTag()}.
     *
     * @throws JspException
     *             on errors
     * @throws IOException
     *             on BodyContent errors
     * @throws SAXException
     *             if any parse errors occur
     * @throws TransformerException
     *             if it is not possible to transform document to string
     */
    @Test
    public void testDoEndTag() throws JspException, IOException, TransformerException, SAXException {
        tag.setTarget("target");
        assertStartTagEvalBody();

        final String html = "<a href=\"" + BASE_URL + "\">testDoEndTag</a>";
        final String expected = wrap("<a href=\"javascript://nop/\" "
                + "onclick=\"new AjaxJspTag.OnClick({" + "baseUrl: &quot;" + BASE_URL + "&quot;"
                + DELIMITER + "eventBase: this" + DELIMITER + "requestHeaders: ['"
                + BaseAjaxBodyTag.TARGET_HEADER + "', 'target']" + DELIMITER
                + "target: &quot;target&quot;" + "});" + " return false;\">testDoEndTag</a>");

        setBodyContent(html);

        assertAfterBody();
        assertEndTag();

        assertContent(expected);
    }

    /**
     * Test method for {@link AjaxAnchorsTag#rewriteAnchors(String, String, String)}.
     *
     * @throws SAXException
     *             if any parse errors occur
     * @throws TransformerException
     *             if it is not possible to transform document to string
     * @throws XPathExpressionException
     *             on XPath errors
     */
    @Test
    public void testNoRewriteAnchors() throws TransformerException, SAXException,
            XPathExpressionException {
        String html = "HTML content";
        String expected = wrap(html);
        assertContent(expected, tag.rewriteAnchors(html, "target", null));

        html = "html <a>link</a>";
        expected = wrap(html);
        assertContent(expected, tag.rewriteAnchors(html, "target", null));

        html = "html <a href=\"" + BASE_URL + "\">testRewriteAnchors</a>";
        expected = wrap(html);
        assertContent(expected, tag.rewriteAnchors(html, "target", "ajaxAnchor"));
    }

    /**
     * Test method for {@link AjaxAnchorsTag#rewriteAnchors(String, String, String)}.
     *
     * @throws SAXException
     *             if any parse errors occur
     * @throws TransformerException
     *             if it is not possible to transform document to string
     * @throws XPathExpressionException
     *             on XPath errors
     */
    @Test
    public void testRewriteAnchors() throws TransformerException, SAXException,
            XPathExpressionException {
        String onclick = "onclick=\"new AjaxJspTag.OnClick({" + "baseUrl: &quot;" + BASE_URL
                + "&quot;" + DELIMITER + "eventBase: this" + DELIMITER + "requestHeaders: ['"
                + BaseAjaxBodyTag.TARGET_HEADER + "', 'target']" + DELIMITER
                + "target: &quot;target&quot;" + "});" + " return false;\"";

        String html = "html <a href=\"" + BASE_URL + "\">testRewriteAnchors</a>";
        String expected = wrap("html <a href=\"javascript://nop/\" " + onclick
                + ">testRewriteAnchors</a>");
        assertContent(expected, tag.rewriteAnchors(html, "target", null));

        html = "html <a class=\"ajaxAnchor\" href=\"" + BASE_URL + "\">testRewriteAnchors</a>";
        expected = wrap("html <a class=\"ajaxAnchor\" href=\"javascript://nop/\" " + onclick
                + ">testRewriteAnchors</a>");
        assertContent(expected, tag.rewriteAnchors(html, "target", "ajaxAnchor"));

        html = "links: <p><a class=\"ajaxAnchor\" href=\"" + BASE_URL
                + "\">link 1</a>, <a class=\"ajaxAnchor\" href=\"" + BASE_URL + "\">link 2</a></p>";
        expected = wrap("links: <p><a class=\"ajaxAnchor\" href=\"javascript://nop/\" " + onclick
                + ">link 1</a>, <a class=\"ajaxAnchor\" href=\"javascript://nop/\" " + onclick
                + ">link 2</a></p>");
        assertContent(expected, tag.rewriteAnchors(html, "target", "ajaxAnchor"));
    }

}
