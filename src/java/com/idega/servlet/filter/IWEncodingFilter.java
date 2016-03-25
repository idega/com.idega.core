package com.idega.servlet.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.IFrame;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IOUtil;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

import net.sf.ehcache.constructs.web.GenericResponseWrapper;

/**
 *
 * This filter should always be the first filter in the filter chain. It sets
 * the correct encoding to the request and response that has to be done before
 * anything is written to the headers or gotten from the request.
 *
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 */
public class IWEncodingFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(IWEncodingFilter.class.getName());

	private IWMainApplicationSettings getSettings() {
		IWMainApplication application = IWMainApplication.getDefaultIWMainApplication();
		if (application != null) {
			return application.getSettings();
		}

		return null;
	}

	private boolean isGZIPEnabled() {
		IWMainApplicationSettings settings = getSettings();
		if (settings != null) {
			return settings.getBoolean("GZIP_compression_enabled", Boolean.FALSE);
		}

		return false;
	}

	/**
	 *
	 * <p>Checks if this {@link IWEncodingFilter} was called by IWJspViewHandler.
	 * If true, it is possible, that {@link IFrame} is rendered, so we don't need
	 * to encode part, we need to encode whole page.</p>
	 */
	public boolean isJSPRenderProcess() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			String className = stackTraceElement.getClassName();
			if (className.contains("IWJspViewHandler")) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	private boolean isUIBeingRendered(HttpServletRequest request) {
		if (request == null) {
			return false;
		}

		String requestURI = request.getRequestURI();
		if (StringUtil.isEmpty(requestURI)) {
			return false;
		}

		if (requestURI.startsWith(CoreConstants.PAGES_URI_PREFIX) || requestURI.startsWith("/idegaweb") || requestURI.startsWith("/workspace") || requestURI.startsWith("/dwr")) {
			return true;
		}

		return false;
	}

	@Override
	public void doFilter(ServletRequest myRequest, ServletResponse myResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) myRequest;
		HttpServletResponse response = (HttpServletResponse) myResponse;

		ByteArrayOutputStream bytes = null;
		boolean gzip = isGZIPEnabled() && !isJSPRenderProcess() && isUIBeingRendered(request);
		if (gzip) {
			bytes = new ByteArrayOutputStream();
			response = new GenericResponseWrapper(response, bytes);
		}

		String requestURI = request.getRequestURI();
		boolean print = requestURI.indexOf(CoreConstants.DOT) == -1 && IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("measure_page_performance", Boolean.FALSE);
		String key = null;
		Long start = null;
		if (print) {
			key = "URI: " + requestURI + ", session ID: " + request.getSession(true).getId();
			start = System.currentTimeMillis();
		}

		IWContext.setCharactersetEncoding(request);

		setRequestResponseProvider(request, response);

		String viewStateParam = "javax.faces.ViewState";
		String viewStateParamValue = request.getParameter(viewStateParam);
		if (!StringUtil.isEmpty(viewStateParamValue)) {
			IWContext iwc = CoreUtil.getIWContext();
			if (iwc == null) {
				iwc = new IWContext(request, response, servletConfig == null ? request.getSession().getServletContext() : servletConfig.getServletContext());
			}

			if (!iwc.isLoggedOn()) {
				Logger.getLogger(getClass().getName()).info("User is not logged in, not allowing to use param '" + viewStateParam + "'");
				String redirectUri = IWMainApplication.getDefaultIWMainApplication().getSettings()
						.getProperty("view_state_param_redirect_page", CoreConstants.PAGES_URI_PREFIX);
				if (!StringUtil.isEmpty(redirectUri)) {
					LOGGER.info("Redirecting to '" + redirectUri + "' because of parameter '" +
							viewStateParam + "' and it's value: " + viewStateParamValue);
					response.sendRedirect(redirectUri);
					return;
				}
			}
		}

		doDetectMobileBrowser(request);

		chain.doFilter(request, response);

		if (gzip) {
			if (response instanceof GenericResponseWrapper) {
				((GenericResponseWrapper) response).flush();
			}

			IOUtil.close(bytes);

			// return on error or redirect code, because response is already
			// committed
			int statusCode = response.getStatus();
			if (statusCode != HttpServletResponse.SC_OK) {
				return;
			}

			ByteArrayOutputStream compressed = new ByteArrayOutputStream();
			GZIPOutputStream gzout = new GZIPOutputStream(compressed);
			gzout.write(bytes.toByteArray());
			gzout.close();

			byte[] compressedBytes = compressed.toByteArray();

			// Write the zipped body
			HttpServletResponse responseToCompress = (HttpServletResponse) myResponse;
			responseToCompress.setHeader("Content-Encoding", "gzip");
			responseToCompress.setContentLength(compressedBytes.length);
			responseToCompress.getOutputStream().write(compressedBytes);
		}

		if (print) {
			long time = System.currentTimeMillis() - start;
			if (time >= 100) {
				LOGGER.info("### served " + key + " in " + time + " ms");
			}
		}
	}

	private void doDetectMobileBrowser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		String pageViewType = request.getParameter(CoreConstants.PARAMETER_PAGE_VIEW_TYPE);
		if (!StringUtil.isEmpty(pageViewType))
			session.setAttribute(CoreConstants.PARAMETER_PAGE_VIEW_TYPE, pageViewType);

		if (CoreConstants.PAGE_VIEW_TYPE_MOBILE.equals(pageViewType))
			return;
		if (CoreConstants.PAGE_VIEW_TYPE_REGULAR.equals(pageViewType))
			return;

		String userAgent = RequestUtil.getUserAgent(request);
		if (StringUtil.isEmpty(userAgent))
			return;

		userAgent = userAgent.toLowerCase();
		boolean mobileBrowser = userAgent.contains("ios") || userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod") ||
			userAgent.contains("android");
		if (mobileBrowser)
			session.setAttribute(CoreConstants.PARAMETER_PAGE_VIEW_TYPE, CoreConstants.PAGE_VIEW_TYPE_MOBILE);
	}

	private void setRequestResponseProvider(HttpServletRequest request, HttpServletResponse response) {
		RequestResponseProvider requestProvider = null;
		try {
			requestProvider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
			requestProvider.setRequest(request);
			requestProvider.setResponse(response);
		} catch(Exception e) {}
	}

	private ServletConfig servletConfig;

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		if (config instanceof ServletConfig) {
			servletConfig = (ServletConfig) config;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

}