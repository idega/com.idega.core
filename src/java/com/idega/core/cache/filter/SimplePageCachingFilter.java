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

import javax.servlet.http.HttpServletRequest;

/**
 * A simple page {@link CachingFilter} suitable for most uses.
 * <p/>
 * The meaning of <i>page</i> is:
 * <ul>
 * <li>A complete response i.e. not a fragment.
 * <li>A content type suitable for gzipping. e.g. text or text/html
 * </ul>
 * For jsp:included page fragments see {@link SimplePageFragmentCachingFilter}.
 * <h3>Keys</h3>
 * Pages are cached based on their key. The key for this cache is the URI followed by the query string. An example
 * is <code>/admin/SomePage.jsp?id=1234&name=Beagle</code>.
 * <p/>
 * This key technique is suitable for a wide range of uses. It is independent of hostname and port number, so will
 * work well in situations where there are multiple domains which get the same content, or where users access
 * based on different port numbers.
 * <p/>
 * A problem can occur with tracking software, where unique ids are inserted into request query strings. Because
 * each request generates a unique key, there will never be a cache hit. For these situations it is better to
 * parse the request parameters and override {@link #calculateKey(javax.servlet.http.HttpServletRequest)} with
 * an implementation that takes account of only the significant ones.
 * <h3>Configuring Caching with ehcache</h3>
 * A cache entry in ehcache.xml should be configured with the name {@link #NAME}.
 * <p/>
 * Cache attributes including expiry are configured per cache name. To specify a different behaviour simply
 * subclass, specify a new name and create a separate cache entry for it.
 * <h3>Gzipping</h3>
 * Significant network efficiencies can be gained by gzipping responses.
 * <p/>
 * Whether a response can be gzipped depends on:
 * <ul>
 * <li>Whether the user agent can accept GZIP encoding. This feature is part of HTTP1.1.
 * If a browser accepts GZIP encoding it will advertise this by including in its HTTP header:
 * All common browsers except IE 5.2 on Macintosh are capable of accepting gzip encoding. Most search engine
 * robots do not accept gzip encoding.
 * <li>Whether the user agent has advertised its acceptance of gzip encoding. This is on a per request basis. If they
 * will accept a gzip response to their request they must include the following in the HTTP request header:
 * <code>
 * Accept-Encoding: gzip
 * </code>
 * </ul>
 * Responses are automatically gzipped and stored that way in the cache. For requests which do not accept gzip
 * encoding the page is retrieved from the cache, ungzipped and returned to the user agent. The ungzipping is
 * high performance.
 * @author Greg Luck
 * @version $Id: SimplePageCachingFilter.java,v 1.1 2006/01/12 15:22:18 tryggvil Exp $
 */
public class SimplePageCachingFilter extends CachingFilter {

    /**
     * The name of the filter. This should match a cache name in ehcache.xml
     */
    public static final String NAME = "SimplePageCachingFilter";

    /**
     * A meaningful name representative of the JSP page being cached.
     * <p/>
     * The name must match the name of a configured cache in ehcache.xml
     *
     * @return the name of the cache to use for this filter.
     */
    protected String getCacheName() {
        return NAME;
    }

    /**
     * Pages are cached based on their key. The key for this cache is the URI followed by the query string. An example
     * is <code>/admin/SomePage.jsp?id=1234&name=Beagle</code>.
     * <p/>
     * This key technique is suitable for a wide range of uses. It is independent of hostname and port number, so will
     * work well in situations where there are multiple domains which get the same content, or where users access
     * based on different port numbers.
     * <p/>
     * A problem can occur with tracking software, where unique ids are inserted into request query strings. Because
     * each request generates a unique key, there will never be a cache hit. For these situations it is better to
     * parse the request parameters and override {@link #calculateKey(javax.servlet.http.HttpServletRequest)} with
     * an implementation that takes account of only the significant ones.
     * <p/>
     * The key should be unique
     *
     * @param httpRequest
     * @return the key, generally the URI plus request parameters
     */
    protected String calculateKey(HttpServletRequest httpRequest) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(httpRequest.getRequestURI()).append(httpRequest.getQueryString());
        String key = stringBuffer.toString();
        return key;
    }

}
