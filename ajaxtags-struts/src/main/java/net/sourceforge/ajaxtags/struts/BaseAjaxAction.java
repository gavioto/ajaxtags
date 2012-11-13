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
package net.sourceforge.ajaxtags.struts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.ajaxtags.servlets.AjaxActionHelper;
import net.sourceforge.ajaxtags.servlets.BaseAjaxXmlAction;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Base class for AJAX requests via Struts. Returns response with XML or text, or HTTP status code
 * 500 (error). Response encoding is set to UTF-8 by default. Return value of execute() will always
 * be null.
 */
public abstract class BaseAjaxAction extends Action implements BaseAjaxXmlAction {

    /** Error message, sent to client with status code 500. */
    public static final String ERROR_MESSAGE = "Cannot create XML response";

    /** Commons logging instance. */
    protected final Log log = LogFactory.getLog(getClass());

    /** Form-bean. Filled in execute(). */
    private final ThreadLocal<ActionForm> form = new ThreadLocal<ActionForm>();

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        try {
            setForm(form);
            final String xml = AjaxActionHelper.invoke(this, request, response);
            if (xml != null) {
                // response.setCharacterEncoding(getXMLEncoding());
                final PrintWriter writer = response.getWriter();
                writer.write(xml);
                // IOUtils.closeQuietly(writer);
                writer.close();
                if (log.isDebugEnabled()) {
                    log.debug(xml.length() + " characters written to XML response");
                }
            }
        } catch (Exception e) {
            final String message = getErrorMessage(e);
            log.error(message, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, StringEscapeUtils
                    .escapeHtml(message));
        }
        setForm(null);
        return null;
    }

    /**
     * @return error message to send to client
     */
    protected String getErrorMessage() {
        return ERROR_MESSAGE;
    }

    /**
     * Override this method to add error logging or change error message etc.
     *
     * @param throwable
     *            error cause
     * @return error message to send to client
     */
    protected String getErrorMessage(final Throwable throwable) {
        // This can reveal server implementation details to remote user (hacker)
        // return getErrorMessage() + " (" + throwable.getLocalizedMessage() + ")";
        return getErrorMessage();
    }

    /**
     * @return default response encoding
     */
    public String getXMLEncoding() {
        return "UTF-8";
    }

    /**
     * @return form bean (can be null if called outside of execute())
     */
    public ActionForm getForm() {
        return form.get();
    }

    /**
     * Save form bean.
     *
     * @param form
     *            form bean
     */
    public void setForm(final ActionForm form) {
        this.form.set(form);
    }

}
