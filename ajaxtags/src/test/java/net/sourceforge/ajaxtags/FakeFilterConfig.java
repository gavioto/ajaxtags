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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * Fake FilterConfig to test tags {@link javax.servlet.Filter}.
 */
public class FakeFilterConfig implements FilterConfig {

    private final Map<String, String> parameters = new HashMap<String, String>();

    public void put(final String key, final String value) {
        parameters.put(key, value);
    }

    public String getFilterName() {
        return getClass().getName();
    }

    public String getInitParameter(final String name) {
        return parameters.get(name);
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getInitParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }
}
