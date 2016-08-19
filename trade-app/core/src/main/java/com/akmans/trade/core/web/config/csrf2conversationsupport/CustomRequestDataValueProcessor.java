package com.akmans.trade.core.web.config.csrf2conversationsupport;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * A <code>RequestDataValueProcessor</code> that pushes a hidden field with a CSRF token into forms.
 * This process implements the {@link #getExtraHiddenFields(HttpServletRequest)} method to push the
 * CSRF token obtained from {@link CSRFTokenManager}. To register this processor to automatically process all
 * Spring based forms register it as a Spring bean named 'requestDataValueProcessor' as shown below:
 * <pre>
 *  &lt;bean name="requestDataValueProcessor" class="com.eyallupu.blog.springmvc.controller.csrf.CSRFRequestDataValueProcessor"/&gt;
 * </pre>
 * @author Eyal Lupu
 *
 */
public class CustomRequestDataValueProcessor implements RequestDataValueProcessor {


	public String processAction(HttpServletRequest request, String action, String httpMethod) {
		return action;
	}

	public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
		return value;
	}

	public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
		Map<String, String> hiddenFields = new HashMap<String, String>();
		// Hidden field for CSRF.
		hiddenFields.put(CSRFTokenManager.CSRF_PARAM_NAME, CSRFTokenManager.getTokenForSession(request.getSession()));

		// Hidden field for Conversation.
		if (request.getAttribute(ConversationalSessionAttributeStore.CID_FIELD) != null) {
			hiddenFields.put(ConversationalSessionAttributeStore.CID_FIELD,
					request.getAttribute(ConversationalSessionAttributeStore.CID_FIELD).toString());
		}

		return hiddenFields;
	}

	public String processUrl(HttpServletRequest request, String url) {
		return url;
	}

}
