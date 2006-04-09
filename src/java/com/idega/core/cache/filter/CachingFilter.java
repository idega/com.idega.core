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

import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.GenericResponseWrapper;
import net.sf.ehcache.constructs.web.PageInfo;
import net.sf.ehcache.constructs.web.ResponseHeadersNotModifiableException;
import net.sf.ehcache.constructs.web.SerializableCookie;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.DataFormatException;

/**
 * An abstract CachingFilter.
 * <p/>
 * This class should be sub-classed for each page to be cached.
 * <p/>
 * The filters must be declared in the web.xml deployment descriptor. Then a mapping from a web resource,
 * such as a JSP, Servlet or static resouce needs to be defined. Finally, a succession of mappings can be used
 * to create a filter chain. See SRV.6 of the Servlet 2.3 specification for more details.
 * <p/>
 * Care should be taken not to define a filter chain such that the same {@link CachingFilter} class is reentered.
 * The {@link CachingFilter} uses the {@link net.sf.ehcache.constructs.blocking.BlockingCache}. It blocks until the thread which
 * did a get which results in a null does a put. If reentry happens a second get happens before the first put. The second
 * get could wait indefinitely. This situation is monitored and if it happens, an IllegalStateException will be thrown.
 *
 * @author @author Greg Luck
 * @version $Id: CachingFilter.java,v 1.3 2006/04/09 12:13:19 laddi Exp $
 */
public abstract class CachingFilter extends Filter {
    private static final Log LOG = LogFactory.getLog(CachingFilter.class.getName());


    /**
     * The cache holding the web pages
     */
    private BlockingCache blockingCache;

    /**
     * Initialises blockingCache to use
     *
     * @throws CacheException The most likely cause is that a cache has not been
     *                        configured in ehcache's configuration file ehcache.xml for the filter name
     */
    public void doInit() throws CacheException {
        final String cacheName = getCacheName();
        setBlockingCache(new BlockingCache(cacheName));
    }


    /**
     * Destroys the filter.
     */
    protected void doDestroy() {
        //noop
    }

    /**
     * Performs the filtering for a request.
     * @param request
     * @param response
     * @param chain
     * @throws AlreadyGzippedException if a double gzip is attempted
     * @throws AlreadyCommittedException if the response was committed on the way in or the on the way back
     * @throws FilterNonReentrantException if an attempt is made to reenter this filter in the same request.
     * @throws Exception for all other exceptions. They will be caught and logged in
     * {@link Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
     */
    protected void doFilter(final HttpServletRequest request, final HttpServletResponse response,
                            final FilterChain chain)
            throws AlreadyGzippedException,
                    AlreadyCommittedException,
                    FilterNonReentrantException,
                    Exception {
        if (response.isCommitted()) {
            throw new AlreadyCommittedException("Response already committed before doing buildPage.");
        }
        logRequestHeaders(request);
        PageInfo pageInfo = buildPageInfo(request, response, chain);
        if (response.isCommitted()) {
            throw new AlreadyCommittedException("Response already committed after doing buildPage"
                    + "but before writing response from PageInfo.");
        }
        writeResponse(request, response, pageInfo);
    }


    /**
     * Build page info either using the cache or building the page directly.
     * <p/>
     * Some requests are for page fragments which should never be gzipped, or for
     * other pages which are not gzipped.
     */
    protected PageInfo buildPageInfo(final HttpServletRequest request, final HttpServletResponse response,
                                     final FilterChain chain) throws Exception {
        // Look up the cached page
        final String key = calculateKey(request);
        PageInfo pageInfo = null;
        try {
        		BlockingCache blockingCache = getBlockingCache();
            checkNoReentry(request);
            pageInfo = (PageInfo) blockingCache.get(key);
            if (pageInfo == null) {
                try {
                    // Page is not cached - build the response, cache it, and send to client
                    pageInfo = buildPage(request, response, chain);
                    if (pageInfo.isOk()) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("PageInfo ok. Adding to cache " + blockingCache.getName() + " with key " + key);
                        }
                        blockingCache.put(key, pageInfo);
                    } else {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("PageInfo was not ok(200). Putting null into cache " + blockingCache.getName()
                                    + " with key " + key);
                        }
                        blockingCache.put(key, null);
                    }
                } catch (final Throwable throwable) {
                    // Must unlock the cache if the above fails. Will be logged at Filter
                    blockingCache.put(key, null);
                    throw new Exception(throwable);
                }
            }
        } finally {
            Thread.currentThread().setName("Application Server Thread");
        }
        return pageInfo;
    }


    /**
     * Builds the PageInfo object by passing the request along the filter chain
     *
     * @param request
     * @param response
     * @param chain
     * @return a Serializable value object for the page or page fragment
     * @throws AlreadyGzippedException if an attempt is made to double gzip the body
     * @throws Exception
     */
    protected PageInfo buildPage(final HttpServletRequest request, final HttpServletResponse response,
                                 final FilterChain chain) throws AlreadyGzippedException, Exception {

        // Invoke the next entity in the chain
        final ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        final GenericResponseWrapper wrapper = new GenericResponseWrapper(response, outstr);
        chain.doFilter(request, wrapper);
        try{
        	wrapper.flush();
        }
        catch(IOException e){
        	e.printStackTrace();
        }
        // Return the page info
        return new PageInfo(wrapper.getStatus(), wrapper.getContentType(), wrapper.getHeaders(), wrapper.getCookies(),
                outstr.toByteArray(), true);
    }

    /**
     * Writes the response from a PageInfo object.
     * @param request
     * @param response
     * @param pageInfo
     * @throws IOException
     * @throws DataFormatException
     * @throws ResponseHeadersNotModifiableException
     */
    protected void writeResponse(final HttpServletRequest request, final HttpServletResponse response, final PageInfo pageInfo)
            throws IOException, DataFormatException, ResponseHeadersNotModifiableException {
        boolean requestAcceptsGzipEncoding = acceptsGzipEncoding(request);

        setStatus(response, pageInfo);
        setHeaders(pageInfo, requestAcceptsGzipEncoding, response);
        setCookies(pageInfo, response);
        setContentType(response, pageInfo);
        writeContent(request, response, pageInfo);
    }

    /**
     * Set the content type
     *
     * @param response
     * @param pageInfo
     */
    protected void setContentType(final HttpServletResponse response, final PageInfo pageInfo) {
        response.setContentType(pageInfo.getContentType());
    }

    /**
     * Set the serializableCookies
     *
     * @param pageInfo
     * @param response
     */
    protected void setCookies(final PageInfo pageInfo, final HttpServletResponse response) {

        final Collection cookies = pageInfo.getSerializableCookies();
        for (Iterator iterator = cookies.iterator(); iterator.hasNext();) {
            final Cookie cookie = ((SerializableCookie) iterator.next()).toCookie();
            response.addCookie(cookie);
        }
    }

    /**
     * Status code
     *
     * @param response
     * @param pageInfo
     */
    protected void setStatus(final HttpServletResponse response, final PageInfo pageInfo) {
        response.setStatus(pageInfo.getStatusCode());
    }


    /**
     * Set the headers in the response object, excluding the Gzip header
     *
     * @param pageInfo
     * @param requestAcceptsGzipEncoding
     * @param response
     */
    protected void setHeaders(final PageInfo pageInfo,
                              boolean requestAcceptsGzipEncoding,
                              final HttpServletResponse response) {

        final Collection headers = pageInfo.getHeaders();
        final int header = 0;
        final int value = 1;

        for (Iterator iterator = headers.iterator(); iterator.hasNext();) {
            final String[] headerPair = (String[]) iterator.next();
            response.addHeader(headerPair[header], headerPair[value]);
        }
    }


    /**
     * A meaningful name representative of the JSP page being cached.
     *
     * @return the name of the cache to use for this filter.
     */
    protected abstract String getCacheName();


    /**
     * CachingFilter works off a key.
     * <p/>
     * The key should be unique. Factors to consider in generating a key are:
     * <ul>
     * <li>The various hostnames that a request could come through
     * <li>Whether additional parameters used for referral tracking e.g. google should be excluded
     * to maximise cache hits
     * <li>Additional parameters can be added to any page. The page will still work but will miss the
     * cache. Consider coding defensively around this issue.
     * </ul>
     *
     * @param httpRequest
     * @return the key, generally the URL plus request parameters
     */
    protected abstract String calculateKey(final HttpServletRequest httpRequest);


    /**
     * Writes the response content.
     * This will be gzipped or non gzipped depending on whether the User Agent accepts
     * GZIP encoding.
     * <p/>
     * If the body is written gzipped a gzip header is added.
     *
     * @param response
     * @param pageInfo
     * @throws IOException
     */
    protected void writeContent(final HttpServletRequest request,
                                final HttpServletResponse response, final PageInfo pageInfo)
            throws IOException, ResponseHeadersNotModifiableException {
        byte[] content = null;
        if (acceptsGzipEncoding(request)) {
            addGzipHeader(response);
            content = pageInfo.getGzippedBody();
        } else {
            content = pageInfo.getUngzippedBody();
        }
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
    }

    /**
     * Check that this caching filter is not being reentered by the same recursively.
     * Recursive calls will block indefinitely because the first request has not yet
     * unblocked the cache.
     * <p/>
     * This condition usually indicates an error in filter chaining or RequestDispatcher
     * dispatching.
     *
     * @param httpRequest
     * @throws FilterNonReentrantException if reentry is detected
     */
    protected void checkNoReentry(final HttpServletRequest httpRequest) throws FilterNonReentrantException {
        Thread thread = Thread.currentThread();
        String threadName = thread.getName();
        String filterName = getClass().getName();
        if (thread.getName().indexOf(" been through " + filterName) != -1) {
            throw new FilterNonReentrantException("The request thread is attempting to reenter"
                    + " filter "
                    + filterName
                    + ". URL: "
                    + httpRequest.getRequestURL());
        }
        //Instrument thread name
        thread.setName(thread.getName() + " been through " + filterName);
        String newThreadName = thread.getName();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Thread name changed from " + threadName
                    + " to " + newThreadName);
        }
    }


	
	/**
	 * @return Returns the blockingCache.
	 */
	public BlockingCache getBlockingCache() {
		return this.blockingCache;
	}


	
	/**
	 * @param blockingCache The blockingCache to set.
	 */
	public void setBlockingCache(BlockingCache blockingCache) {
		this.blockingCache = blockingCache;
	}
}
