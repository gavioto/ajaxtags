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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Fake HttpServletResponse to test tags.
 */
public class FakeHttpServletResponse implements HttpServletResponse {

    private String characterEncoding = "ISO-8859-1";
    private String contentType;
    private final Map<String, String> headers = new HashMap<String, String>();
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private final ServletOutputStream outputStream;
    private int contentLength;
    private PrintWriter writer;
    private boolean committed;
    private int bufferSize = 4096;
    private Locale locale = Locale.getDefault();

    public FakeHttpServletResponse() {
        outputStream = new FakeServletOutputStream(this);
    }

    public void addCookie(final Cookie cookie) {
        // TODO Auto-generated method stub
    }

    public void addDateHeader(final String name, final long date) {
        // TODO Auto-generated method stub
    }

    public void addHeader(final String name, final String value) {
        // TODO Auto-generated method stub
    }

    public void addIntHeader(final String name, final int value) {
        // TODO Auto-generated method stub
    }

    public boolean containsHeader(final String name) {
        return headers.containsKey(name);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String encodeRedirectURL(final String url) {
        // TODO Auto-generated method stub
        return url;
    }

    public String encodeRedirectUrl(final String url) {
        return encodeRedirectURL(url);
    }

    public String encodeURL(final String url) {
        // TODO Auto-generated method stub
        return url;
    }

    public String encodeUrl(final String url) {
        return encodeURL(url);
    }

    public void sendError(final int sc) throws IOException {
        // TODO Auto-generated method stub
    }

    public void sendError(final int sc, final String msg) throws IOException {
        // TODO Auto-generated method stub
    }

    public void sendRedirect(final String location) throws IOException {
        // TODO Auto-generated method stub
    }

    public void setDateHeader(final String name, final long date) {
        // TODO Auto-generated method stub
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void setIntHeader(final String name, final int value) {
        // TODO Auto-generated method stub
    }

    public void setStatus(final int sc) {
        // TODO Auto-generated method stub
    }

    public void setStatus(final int sc, final String sm) {
        // TODO Auto-generated method stub
    }

    public void flushBuffer() {
        setCommitted(true);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public Locale getLocale() {
        return locale;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            Writer targetWriter = characterEncoding != null ? new OutputStreamWriter(content,
                    characterEncoding) : new OutputStreamWriter(content);
            writer = new ResponsePrintWriter(targetWriter);
        }
        return writer;
    }

    ByteArrayOutputStream getContent() {
        return content;
    }

    public byte[] getContentAsByteArray() {
        flushBuffer();
        return content.toByteArray();
    }

    public String getContentAsString() throws UnsupportedEncodingException {
        flushBuffer();
        return characterEncoding != null ? content.toString(characterEncoding) : content.toString();
    }

    void setCommittedIfBufferSizeExceeded() {
        int bufSize = getBufferSize();
        if (bufSize > 0 && content.size() > bufSize) {
            setCommitted(true);
        }
    }

    void setCommitted(final boolean committed) {
        this.committed = committed;
    }

    public boolean isCommitted() {
        return committed;
    }

    public void reset() {
        // TODO Auto-generated method stub
    }

    public void resetBuffer() {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot reset buffer - response is already committed");
        }
        content.reset();
    }

    public void setBufferSize(final int size) {
        bufferSize = size;
    }

    public void setCharacterEncoding(final String charset) {
        characterEncoding = charset;
    }

    public void setContentLength(final int len) {
        contentLength = len;
    }

    public void setContentType(final String type) {
        contentType = type;
    }

    public void setLocale(final Locale loc) {
        locale = loc;
    }

    /**
     * Inner class that adapts the PrintWriter to mark the response as committed once the buffer
     * size is exceeded.
     */
    private class ResponsePrintWriter extends PrintWriter {

        public ResponsePrintWriter(final Writer out) {
            super(out, true);
        }

        @Override
        public void write(final char buf[], final int off, final int len) {
            super.write(buf, off, len);
            super.flush();
            setCommittedIfBufferSizeExceeded();
        }

        @Override
        public void write(final String s, final int off, final int len) {
            super.write(s, off, len);
            super.flush();
            setCommittedIfBufferSizeExceeded();
        }

        @Override
        public void write(final int c) {
            super.write(c);
            super.flush();
            setCommittedIfBufferSizeExceeded();
        }

        @Override
        public void flush() {
            super.flush();
            setCommitted(true);
        }
    }

}
