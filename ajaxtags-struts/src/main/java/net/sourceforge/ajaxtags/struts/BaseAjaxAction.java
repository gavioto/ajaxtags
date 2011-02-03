package net.sourceforge.ajaxtags.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.ajaxtags.servlets.AjaxActionHelper;
import net.sourceforge.ajaxtags.servlets.BaseAjaxXmlAction;

import org.apache.commons.lang.StringEscapeUtils;
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

    /** Form-bean. Filled in execute(). */
    private ActionForm form;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            setForm(form);
            final String xml = AjaxActionHelper.invoke(this, request, response);
            if (xml != null) {
                // response.setCharacterEncoding(getXMLEncoding());
                final PrintWriter writer = response.getWriter();
                try {
                    writer.write(xml);
                } finally {
                    // IOUtils.closeQuietly(writer);
                    writer.close();
                }
                // logger.debug(xml.length() + " characters written to XML response");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, StringEscapeUtils
                    .escapeHtml(getErrorMessage(e)));
        }
        setForm(null);
        return null;
    }

    protected String getErrorMessage() {
        return ERROR_MESSAGE;
    }

    /**
     * Override this method to add error logging or change error message etc.
     *
     * @param throwable
     *            error cause
     * @return error message
     */
    protected String getErrorMessage(final Throwable throwable) {
        // This can reveal server implementation details to remote user (hacker)
        // return ERROR_MESSAGE + " (" + throwable.getLocalizedMessage() + ")";
        return ERROR_MESSAGE;
    }

    /**
     * @return default response encoding
     */
    public String getXMLEncoding() {
        return "UTF-8";
    }

    public ActionForm getForm() {
        return form;
    }

    public void setForm(ActionForm form) {
        this.form = form;
    }

}
