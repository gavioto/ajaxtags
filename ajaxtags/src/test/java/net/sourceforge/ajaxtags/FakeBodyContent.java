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
package net.sourceforge.ajaxtags;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * Fake BodyContent to test tags.
 */
public class FakeBodyContent extends BodyContent {

    private static final String NEW_LINE = "\n";

    private StringBuilder content = new StringBuilder();

    protected FakeBodyContent(final JspWriter writer) {
        super(writer);
    }

    /** Constructor. */
    public FakeBodyContent() {
        super(null);
    }

    @Override
    public Reader getReader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getString() {
        return content.toString();
    }

    @Override
    public void writeOut(final Writer out) throws IOException {
        out.write(content.toString());
    }

    @Override
    public void clear() throws IOException {
        content = new StringBuilder();
    }

    @Override
    public void clearBuffer() throws IOException {
        content = new StringBuilder();
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public int getRemaining() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void newLine() throws IOException {
        content.append(NEW_LINE);
    }

    @Override
    public Writer append(final CharSequence csq) throws IOException {
        content.append(csq);
        return this;
    }

    @Override
    public void print(final boolean b) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void print(final char c) throws IOException {
        content.append(c);
    }

    @Override
    public void print(final int i) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void print(final long l) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void print(final float f) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void print(final double d) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void print(final char[] str) throws IOException {
        content.append(str);
    }

    @Override
    public void print(final String str) throws IOException {
        content.append(str);
    }

    @Override
    public void print(final Object obj) throws IOException {
        content.append(obj);
    }

    @Override
    public void println() throws IOException {
        content.append(NEW_LINE);
    }

    @Override
    public void println(final boolean b) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void println(final char c) throws IOException {
        content.append(c).append(NEW_LINE);
    }

    @Override
    public void println(final int i) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void println(final long l) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void println(final float f) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void println(final double d) throws IOException { // NOPMD
        // TODO Auto-generated method stub
    }

    @Override
    public void println(final char[] str) throws IOException {
        content.append(str).append(NEW_LINE);
    }

    @Override
    public void println(final String str) throws IOException {
        content.append(str).append(NEW_LINE);
    }

    @Override
    public void println(final Object obj) throws IOException {
        content.append(obj).append(NEW_LINE);
    }

    @Override
    public void write(final char[] str, final int off, final int len) throws IOException {
        content.append(str, off, len);
    }

}
