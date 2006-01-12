/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 - 2004 Greg Luck.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by Greg Luck
 *       (http://sourceforge.net/users/gregluck) and contributors.
 *       See http://sourceforge.net/project/memberlist.php?group_id=93232
 *       for a list of contributors"
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "EHCache" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For written
 *    permission, please contact Greg Luck (gregluck at users.sourceforge.net).
 *
 * 5. Products derived from this software may not be called "EHCache"
 *    nor may "EHCache" appear in their names without prior written
 *    permission of Greg Luck.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL GREG LUCK OR OTHER
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by contributors
 * individuals on behalf of the EHCache project.  For more
 * information on EHCache, please see <http://ehcache.sourceforge.net/>.
 *
 */
package com.idega.core.cache.filter;

import net.sf.ehcache.constructs.web.ResponseHeadersNotModifiableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic {@link javax.servlet.Filter} with most of what we need done.
 * <p/>
 * Participates in the Template Method pattern with {@link javax.servlet.Filter}.
 *
 * @author <a href="mailto:gluck@thoughtworks.com">Greg Luck</a>
 * @version $Id: Filter.java,v 1.1 2006/01/12 15:22:18 tryggvil Exp $
 */
public abstract class Filter implements javax.servlet.Filter {
    /**
     * If a request attribute NO_FILTER is set, then filtering will be skipped
     */
    public static final String NO_FILTER = "NO_FILTER";
    private static final Log LOG = LogFactory.getLog(Filter.class.getName());

    /**
     * The filter configuration.
     */
    protected FilterConfig filterConfig;

    /**
     * The exceptions to log differently, as a comma separated list
     */
    protected String exceptionsToLogDifferently;


    /**
     * A the level of the exceptions which will be logged differently
     */
    protected String exceptionsToLogDifferentlyLevel;


    /**
     * Most {@link Throwable}s in Web applications propagate to the user. Usually they are logged where they first
     * happened. Printing the stack trace once a {@link Throwable} as propagated to the servlet is sometimes
     * just clutters the log.
     * <p/>
     * This field corresponds to an init-param of the same name. If set to true stack traces will be suppressed.
     */
    protected boolean suppressStackTraces;


    /**
     * Performs the filtering.  This method calls template method
     * {@link #doFilter(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain) } which does the filtering.
     * This method takes care of error reporting and handling.
     * Errors are reported at {@link Log#warn(Object)} level because http tends to produce lots of errors.
     */
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            //NO_FILTER set for RequestDispatcher forwards to avoid double gzipping
            if (filterNotDisabled(httpRequest)) {
                doFilter(httpRequest, httpResponse, chain);
            } else {
                chain.doFilter(request, response);
            }
            // Flush the response
            httpResponse.getOutputStream().flush();

        } catch (final Throwable throwable) {
            logThrowable(throwable, httpRequest);
        }
    }

    /**
     * Filters can be disabled programmatically by adding a {@link #NO_FILTER} parameter to the request.
     * This parameter is normally added to make RequestDispatcher include and forwards work.
     * todo this should not be necessary
     *
     * @param httpRequest the request
     * @return true if NO_FILTER is not set.
     */
    private boolean filterNotDisabled(final HttpServletRequest httpRequest) {
        return httpRequest.getAttribute(NO_FILTER) == null;
    }


    private void logThrowable(final Throwable throwable, final HttpServletRequest httpRequest) throws ServletException {
        StringBuffer messageBuffer = new StringBuffer("Throwable thrown during doFilter on request with URI: ")
                    .append(httpRequest.getRequestURI())
                    .append(" and Query: ")
                    .append(httpRequest.getQueryString());
        String message = messageBuffer.toString();
        boolean matchFound = matches(throwable);
        if (matchFound) {
            try {
                if (suppressStackTraces) {
                    Method method = Log.class.getMethod(exceptionsToLogDifferentlyLevel, new Class[]{Object.class});
                    method.invoke(LOG, new Object[]{throwable.getMessage()});
                } else {
                    Method method = Log.class.getMethod(exceptionsToLogDifferentlyLevel,
                            new Class[]{Object.class, Throwable.class});
                    method.invoke(LOG, new Object[]{throwable.getMessage(), throwable});
                }
            } catch (Exception e) {
                LOG.fatal("Could not invoke Log method for " + exceptionsToLogDifferentlyLevel, e);
            }
            throw new ServletException(message, throwable);
        } else {

            if (suppressStackTraces) {
                LOG.warn(messageBuffer.append(throwable.getMessage()).append("\nTop StackTraceElement: ")
                        .append(throwable.getStackTrace()[0].toString()));
            } else {
                LOG.warn(messageBuffer.append(throwable.getMessage()), throwable);
            }
            throw new ServletException(throwable);
        }
    }

    /**
     * Checks whether a throwable, its root cause if it is a {@link ServletException}, or its cause, if it is a
     * Chained Exception matches an entry in the exceptionsToLogDifferently list
     *
     * @param throwable
     * @return true if the class name of any of the throwables is found in the exceptions to log differently
     */
    private boolean matches(Throwable throwable) {
        if (exceptionsToLogDifferently == null) {
            return false;
        }
        if (exceptionsToLogDifferently.indexOf(throwable.getClass().getName()) != -1) {
            return true;
        }
        if (throwable instanceof ServletException) {
            Throwable rootCause = (((ServletException) throwable).getRootCause());
            if (exceptionsToLogDifferently.indexOf(rootCause.getClass().getName()) != -1) {
                return true;
            }
        }
        if (throwable.getCause() != null) {
            Throwable cause = throwable.getCause();
            if (exceptionsToLogDifferently.indexOf(cause.getClass().getName()) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialises the filter.  Calls template method {@link #doInit()} to perform any filter specific initialisation.
     */
    public final void init(final FilterConfig config) throws ServletException {
        try {

            this.filterConfig = config;
            processInitParams(config);


            // Attempt to initialise this filter
            doInit();
        } catch (final Exception e) {
            LOG.fatal("Could not initialise servlet filter.", e);
            throw new ServletException("Could not initialise servlet filter.", e);
        }
    }

    private void processInitParams(final FilterConfig config) throws ServletException {
        String exceptions = config.getInitParameter("exceptionsToLogDifferently");
        String level = config.getInitParameter("exceptionsToLogDifferentlyLevel");
        String suppressStackTracesString = config.getInitParameter("suppressStackTraces");
        suppressStackTraces = Boolean.valueOf(suppressStackTracesString).booleanValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suppression of stack traces enabled for " + this.getClass().getName());
        }

        if (exceptions != null) {
            validateMandatoryParameters(exceptions, level);
            validateLevel(level);
            exceptionsToLogDifferentlyLevel = level;
            exceptionsToLogDifferently = exceptions;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Different logging levels configured for " + this.getClass().getName());
            }
        }
    }

    private void validateMandatoryParameters(String exceptions, String level) throws ServletException {
        if ((exceptions != null && level == null) || (level != null && exceptions == null)) {
            throw new ServletException("Invalid init-params. Both exceptionsToLogDifferently"
                    + " and exceptionsToLogDifferentlyLevelvalue should be specified if one is"
                    + " specified.");
        }
    }

    private void validateLevel(String level) throws ServletException {
        //Check correct level set
        if (!(level.equals("debug")
                || level.equals("info")
                || level.equals("warn")
                || level.equals("error")
                || level.equals("fatal"))) {
            throw new ServletException("Invalid init-params value for \"exceptionsToLogDifferentlyLevel\"."
                    + "Must be one of debug, info, warn, error or fatal.");
        }
    }

    /**
     * Destroys the filter. Calls template method {@link #doDestroy()}  to perform any filter specific
     * destruction tasks.
     */
    public final void destroy() {
        this.filterConfig = null;
        doDestroy();
    }

    /**
     * Checks if request accepts the named encoding.
     */
    protected boolean acceptsEncoding(final HttpServletRequest request, final String name) {
        final boolean accepts = headerContains(request, "Accept-Encoding", name);
        return accepts;
    }

    /**
     * Checks if request contains the header value.
     */
    private boolean headerContains(final HttpServletRequest request, final String header, final String value) {

        logRequestHeaders(request);

        final Enumeration accepted = request.getHeaders(header);
        while (accepted.hasMoreElements()) {
            final String headerValue = (String) accepted.nextElement();
            if (headerValue.indexOf(value) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Logs the request headers, if debug is enabled.
     *
     * @param request
     */
    protected void logRequestHeaders(final HttpServletRequest request) {
        if (LOG.isDebugEnabled()) {
            Map headers = new HashMap();
            Enumeration enumeration = request.getHeaderNames();
            StringBuffer logLine = new StringBuffer();
            logLine.append("Request Headers");
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String headerValue = request.getHeader(name);
                headers.put(name, headerValue);
                logLine.append(": ").append(name).append(" -> ").append(headerValue);
            }
            LOG.debug(logLine);
        }
    }

    /**
     * Adds the gzip HTTP header to the response. This is need when a gzipped body
     * is returned so that browsers can properly decompress it.
     * @throws ResponseHeadersNotModifiableException Either the response is committed or we were called using the include method
     * from a {@link javax.servlet.RequestDispatcher#include(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
     * method and the set set header is ignored.
     */
    protected void addGzipHeader(final HttpServletResponse response) throws ResponseHeadersNotModifiableException {
        response.setHeader("Content-Encoding", "gzip");
        boolean containsEncoding = response.containsHeader("Content-Encoding");
        if (!containsEncoding) {
            throw new ResponseHeadersNotModifiableException("Failure when attempting to set "
                    + "Content-Encoding: gzip");
        }
    }

    /**
     * A template method that performs any Filter specific destruction tasks.
     * Called from {@link #destroy()}
     */
    protected abstract void doDestroy();


    /**
     * A template method that performs the filtering for a request.
     * Called from {@link #doFilter(ServletRequest, ServletResponse, FilterChain)}.
     */
    protected abstract void doFilter(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
                                     final FilterChain chain) throws Throwable;

    /**
     * A template method that performs any Filter specific initialisation tasks.
     * Called from {@link #init(FilterConfig)}.
     */
    protected abstract void doInit() throws Exception;

    /**
     * Returns the filter config.
     */
    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    /**
     * Determine whether the user agent accepts GZIP encoding. This feature is part of HTTP1.1.
     * If a browser accepts GZIP encoding it will advertise this by including in its HTTP header:
     * <p/>
     * <code>
     * Accept-Encoding: gzip
     * </code>
     * <p/>
     * Requests which do not accept GZIP encoding fall into the following categories:
     * <ul>
     * <li>Old browsers, notably IE 5 on Macintosh.
     * <li>Search robots such as yahoo. While there are quite a few bots, they only hit individual
     * pages once or twice a day. Note that Googlebot as of August 2004 now accepts GZIP.
     * <li>Monitoring Scripts. The one by Steve Sulman was requesting once every few seconds and did
     * not accept GZIP. This amounts to a Denial of Service.
     * <li>Internet Explorer through a proxy. By default HTTP1.1 is enabled but disabled when going
     * through a proxy. 90% of non gzip requests are caused by this.
     * </ul>
     * As of September 2004, about 34% of requests coming from the Internet did not accept GZIP encoding.
     *
     * @param request
     * @return true, if the User Agent request accepts GZIP encoding
     */
    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        return acceptsEncoding(request, "gzip");
    }

}

