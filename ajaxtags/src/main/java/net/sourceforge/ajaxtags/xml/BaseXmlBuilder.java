/**
 * Copyright 2009 Jens Kapitza
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
package net.sourceforge.ajaxtags.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to build valid XML as a base for all xmlbuilder.
 *
 * @author Jens Kapitza
 * @version $Revision: 86 $ $Date: 2007/07/22 16:58:28 $ $Author: jenskapitza $
 * @param <V>
 *            Listtype (Item, TreeItem)
 */
public abstract class BaseXmlBuilder<V> {

    private List<V> liste = new ArrayList<V>();

    /**
     * Default encoding is utf-8.
     */
    private String encoding = "UTF-8";

    protected void setListe(List<V> liste) {
        this.liste = liste;
    }

    /**
     * @return the xml encoding
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Set the xml encoding.
     *
     * @param encoding
     *            the xml encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected List<V> getListe() {
        return this.liste;
    }

    /**
     *
     * @return the item list
     */
    protected List<V> getItems() {
        return getListe();
    }

    /**
     * @return the xml body, xml encoding is added by {@link #toString()}
     */
    protected abstract String getXMLString();

    /**
     * @return the full XML document
     */
    @Override
    public String toString() {
        StringBuffer xml = new StringBuffer().append("<?xml version=\"1.0\"");
        if (getEncoding() != null) {
            xml.append(" encoding=\"").append(getEncoding()).append("\"");
        }
        xml.append(" ?>");
        xml.append(getXMLString());
        return xml.toString();
    }

    /**
     * Add item to list.
     *
     * @param item
     *            the item to add
     * @return BaseXmlBuilder
     * @see ArrayList#add(Object)
     */
    public BaseXmlBuilder<V> add(final V item) {
        this.liste.add(item);
        return this;
    }

    /**
     * Delete all items.
     */
    public void clear() {
        this.liste.clear();
    }

    /**
     * Return the item at index.
     *
     * @param index
     *            the index
     * @return the item at index
     */
    public V get(int index) {
        return this.liste.get(index);
    }

    /**
     * Check if itemlist is empty.
     *
     * @return true if it is empty else false
     */
    public boolean isEmpty() {
        return this.liste.isEmpty();
    }

    /**
     * @return the item count
     */
    public int size() {
        return this.liste.size();
    }

}
