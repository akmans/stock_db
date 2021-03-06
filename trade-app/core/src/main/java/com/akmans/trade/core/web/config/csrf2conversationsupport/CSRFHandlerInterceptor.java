package com.akmans.trade.core.web.config.csrf2conversationsupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.akmans.trade.core.web.utils.PathConstants;

/**
 * A Spring MVC <code>HandlerInterceptor</code> which is responsible to enforce
 * CSRF token validity on incoming posts requests. The interceptor should be
 * registered with Spring MVC servlet using the following syntax:
 * 
 * <pre>
 *   &lt;mvc:interceptors&gt;
 *        &lt;bean class="com.eyallupu.blog.springmvc.controller.csrf.CSRFHandlerInterceptor"/&gt;
 *   &lt;/mvc:interceptors&gt;
 * </pre>
 * 
 * @author Eyal Lupu
 * @see CSRFRequestDataValueProcessor
 *
 */
public class CSRFHandlerInterceptor extends HandlerInterceptorAdapter {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CSRFHandlerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.debug("Firing CSRFHandler intercept.");
		if (!request.getMethod().equalsIgnoreCase("POST")
				|| request.getRequestURI().contains(PathConstants.PATH_ACCESS_DENIED)) {
			logger.debug("<TOKEN> No check!" + request.getRequestURI());
			// Not a POST - allow the request
			return true;
		} else {
			// This is a POST request - need to check the CSRF token
			String sessionToken = CSRFTokenManager.getTokenForSession(request.getSession());
			String requestToken = CSRFTokenManager.getTokenFromRequest(request);
			if (sessionToken.equals(requestToken)) {
				logger.debug("<TOKEN> Check success!");
				return true;
			} else {
				logger.debug("<TOKEN> Check failed!");
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				request.getRequestDispatcher(PathConstants.PATH_ACCESS_DENIED).forward(request, response);
				return false;
			}
		}
	}

}
