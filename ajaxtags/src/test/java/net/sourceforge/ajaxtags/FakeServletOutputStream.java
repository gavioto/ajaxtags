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
package net.sourceforge.ajaxtags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

/**
 * Fake ServletOutputStream to test tags.
 */
public class FakeServletOutputStream extends ServletOutputStream {

    // ByteArrayOutputStream stream = new ByteArrayOutputStream();
    FakeHttpServletResponse response;

    public FakeServletOutputStream() {
        response = new FakeHttpServletResponse();
    }

    public FakeServletOutputStream(final FakeHttpServletResponse response) {
        this.response = response;
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException {
        // stream.write(b);
        response.getContent().write(b);
        response.setCommittedIfBufferSizeExceeded();
    }

    /**
     * Creates a newly allocated byte array. Its size is the current size of this output stream and
     * the valid contents of the buffer have been copied into it.
     *
     * @return the current contents of this output stream, as a byte array.
     * @see FakeServletOutputStream#size()
     */
    public synchronized byte[] toByteArray() {
        // return stream.toByteArray();
        return response.getContentAsByteArray();
    }

    /**
     * Returns the current size of the buffer.
     *
     * @return the number of valid bytes in this output stream.
     * @see java.io.ByteArrayOutputStream#size()
     */
    public synchronized int size() {
        // return stream.size();
        return response.getContent().size();
    }

    /**
     * Converts the buffer's contents into a string decoding bytes using the platform's default
     * character set. The length of the new <tt>String</tt> is a function of the character set, and
     * hence may not be equal to the size of the buffer.
     *
     * <p>
     * This method always replaces malformed-input and unmappable-character sequences with the
     * default replacement string for the platform's default character set. The
     * {@linkplain java.nio.charset.CharsetDecoder} class should be used when more control over the
     * decoding process is required.
     *
     * @return String decoded from the buffer's contents.
     */
    @Override
    public synchronized String toString() {
        // return stream.toString();
        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        super.flush();
        response.setCommitted(true);
    }
}
