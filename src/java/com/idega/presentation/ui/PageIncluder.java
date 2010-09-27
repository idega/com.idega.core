package com.idega.presentation.ui;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.util.FileUtil;
import com.idega.util.Index;
import com.idega.util.IndexComparator;
import com.idega.util.URLUtil;
import com.idega.util.text.TextSoap;

/**
 * A presentationObject that uses FileUtil.getStringFromURL to serverside
 * include a given URL
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */
public class PageIncluder extends PresentationObject implements Index {

	private static final String IB_PAGE_PARAMETER = ICBuilderConstants.IB_PAGE_PARAMETER;

	private String URL = null;
	private String BASEURL = null;
	private String BASEURLHTTPS = null;
	private String RELATIVEURL = null;
	private String pageIncluderPrefix = null;
	private String _label = null;
	private String _sendToLabel = null;
	private ICPage _sendToPage = null;
	private String _sendToPageIfSet = null;

	private String sessionId = null;
	private String sessionURL = null;
	private String token = null;
	private String tokenReplacer = null;
	private String serverName = null;
	private String httpPrefix = null;
	private String httpsPrefix = null;

	private Map findReplaceStrings = null;

	private String out;
	private int index = 1000;

	private static final String PAGE_INCLUDER_PARAMETER_NAME = "iw_uri_";
	private static final String PAGE_INCLUDER_SESSION_NAME = "iw_session_token";
	private int instanceId;
	private boolean forceFrame = false;
	private boolean useSecureLinks = false;

	private boolean changeURL = false;
	private final String symbol = "$";

	private Map allowedDomainsAndIPNumberMap;

	private String pageEncoding = "UTF-8";

	public String getPageEncoding() {
		return pageEncoding;
	}

	public void setPageEncoding(String pageEncoding) {
		this.pageEncoding = pageEncoding;
	}

	public PageIncluder() {
		super();
	}

	public PageIncluder(String URL) {
		this();
		this.URL = URL;
	}

	private void sortAndProcess(IWContext iwc) {

		// sort
		Page parent = this.getParentPage();
		/** @todo get in main **/
		List objects = parent.getChildrenRecursive();
		ArrayList includers = new ArrayList();
		Iterator iter = objects.iterator();
		while (iter.hasNext()) {
			Object item = iter.next();
			if (item instanceof PageIncluder) {
				includers.add(item);
			}
		}

		IndexComparator indexer = new IndexComparator(
				IndexComparator.ORDER_BY_INDEX);
		includers = indexer.sortedArrayList(includers);

		// process
		Iterator iter2 = includers.iterator();
		while (iter2.hasNext()) {
			PageIncluder item = (PageIncluder) iter2.next();
			try {
				item.process(iwc);
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
		}

	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

	public void main(IWContext iwc) throws Exception {
		Page fromPage = this.getParentPage();
		this.instanceId = getICObjectInstanceID();
		this.changeURL = (iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME
				+ this._label))
				|| (iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME
						+ this.instanceId));

		if (this.changeURL) {
			this.changeURL = canChangeURLFromRequest(iwc);
		}

		if (this.changeURL && (this._sendToPage != null)
				&& iwc.isParameterSet(this._sendToPageIfSet)) {// forwarding
			forwardToIBPage(fromPage, this._sendToPage, iwc);
		} else if (this.out == null) {
			sortAndProcess(iwc);// ususal
		}

	}

	/**
	 * Security check to stop hackers from using the pageincluder from a domain
	 * that is not allowed
	 * 
	 * @param iwc
	 */
	private boolean canChangeURLFromRequest(IWContext iwc) {
		if (this.allowedDomainsAndIPNumberMap != null) {
			boolean changingTheURLFromRequest = (iwc
					.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME + this._label))
					|| (iwc.isParameterSet(PAGE_INCLUDER_PARAMETER_NAME
							+ this.instanceId));
			if (changingTheURLFromRequest) {
				String url = iwc.getParameter(PAGE_INCLUDER_PARAMETER_NAME
						+ this._label);
				if (url == null || "".equals(url)) {
					url = iwc.getParameter(PAGE_INCLUDER_PARAMETER_NAME
							+ this.instanceId);
				}
				URLUtil util = new URLUtil(url);
				String domainOrIpPart = util.getHost();
				if (!this.allowedDomainsAndIPNumberMap
						.containsKey(domainOrIpPart)) {
					return false;// if the domain is not allowed to ask for
									// pageincluding then return false
				}
			}
		}
		return true;
	}

	public void print(IWContext iwc) throws IOException {
		if (this.URL != null) {
			if (this.out != null) {
				println(this.out);
			}
			this.out = null;
		}
	}

	protected void process(IWContext iwc) throws Exception {
		this.serverName = iwc.getServerName();
		this.instanceId = getICObjectInstanceID();

		// pageincluderprefix httpprefix etc...
		setPrefixes(iwc);

		// get parameters and change the pageincluders url if needed
		String loc = getLocation(iwc);
		// System.out.println("Loc before = "+loc);

		// get a session id from a session creating page
		if ((this.sessionURL != null) && (this.token != null)) {

			if (this.sessionId == null) {
				this.sessionId = (String) iwc
						.getSessionAttribute(PAGE_INCLUDER_SESSION_NAME);
				if (this.sessionId == null) {
					this.sessionId = FileUtil.getStringFromURL(this.sessionURL);
					// debug("Sessions id is : "+sessionId);
				}
			}

			iwc.setSessionAttribute(PAGE_INCLUDER_SESSION_NAME, this.sessionId);

			loc = TextSoap.findAndReplace(loc, this.token, this.sessionId);
			loc = TextSoap.findAndCut(loc, "\r\n");
			loc = TextSoap.findAndCut(loc, "\n");

		} else if ((this.sessionId != null) && (this.token != null)) {
			loc = TextSoap.findAndReplace(loc, this.token, this.sessionId);
		}

		// System.out.println("Location url is: "+loc+" and index is: "+index);

		if (loc != null && !loc.equals("")) {
			this.out = FileUtil.getStringFromURL(loc, getPageEncoding());

			URL url = new URL(loc);
			this.BASEURL = url.getProtocol() + "://" + url.getHost() + "/";

			this.BASEURLHTTPS = "https://" + url.getHost() + "/";

			if (loc.lastIndexOf("/") == 6) {
				loc += "/";
			}
			this.RELATIVEURL = loc.substring(0, loc.lastIndexOf("/") + 1);

			/**
			 * @todo use expressions to make none case sensitive or implement
			 *       using HTMLDocumentLoader (Advanced Swing);
			 * **/

			/*
			 * Finish if needed but make sure it is a configurable option just
			 * an idea to get all javascripts and styles from the real page List
			 * headerTag = TextSoap.FindAllBetween(out, "<head>","</head>");
			 * if(headerTag!=null && headerTag.isEmpty()) { String headerContent
			 * = (String)headerTag.iterator().next(); List styles =
			 * TextSoap.FindAllBetween(out,
			 * "<style type=\"text/css\">\n","</style>\n"); if(styles!=null &&
			 * styles.isEmpty()) { this.getParentPage().setStyleDefinition()...
			 * } }
			 */

			this.out = TextSoap.stripHTMLTagAndChangeBodyTagToTable(this.out);
			this.out = preProcess(this.out, iwc);
			if (this.forceFrame) {
				this.out = encodeQueryStrings(this.out);
			}
			this.out = changeAHrefAttributes(this.out);
			this.out = changeFormActionAttributes(this.out);
			this.out = changeSrcAttributes(this.out);

			this.out = changeJSOpenWindowURL(this.out);

			this.out = postProcess(this.out, iwc);

		}
	}

	protected String preProcess(String html, IWContext iwc) {
		html = TextSoap.findAndReplace(html, "href=\"javascript",
				"httpIW_PREPROCESSED");
		return html;
	}

	protected String postProcess(String html, IWContext iwc) {
		html = TextSoap.findAndReplace(html, "httpIW_PREPROCESSED",
				"href=\"javascript");
		html = findAndReplaceStrings(html);

		// Make images from this server (idegaweb) always follow the protocol
		// being used http/https
		// @todo this is case sensitive and could break! move to IWContext. Also
		// done in Link, SubmitButton, Image and PageIncluder
		if (iwc.getRequest().isSecure()) {
			html = TextSoap.findAndReplace(html, "src=\"http://"
					+ this.serverName, "src=\"https://" + this.serverName);
		}

		return html;
	}

	/**
	 * @todo temporary Strengs fix. We should change the find replace method with
	 *       the simpler:<br>
	 *       1. find all between symbol=".." strings 2. Change them 3. Find
	 *       replace the original found strings with the new ones
	 */

	private String findAndReplaceStrings(String html) {
		if (this.findReplaceStrings != null) {
			Set keys = this.findReplaceStrings.keySet();
			Iterator iter = keys.iterator();
			String key;
			while (iter.hasNext()) {
				key = (String) iter.next();
				html = TextSoap.findAndReplace(html, key,
						(String) this.findReplaceStrings.get(key));
			}

		}
		return html;

	}

	public void setFindAndReplaceString(String stringToFind,
			String stringToReplace) {
		if (this.findReplaceStrings == null) {
			this.findReplaceStrings = new HashMap();
		}
		this.findReplaceStrings.put(stringToFind, stringToReplace);
	}

	protected String changeAHrefAttributes(String html) {
		/*
		 * Possibilities tags: ahref and action src /xxx/xx
		 * prefix+baseurl+/xxx/xx baseurl+/xxx/xx xxx/xx
		 * prefix+relativeurl+/+xxx/xx relative+/+xxx/xx http:// prefix ekkert
		 * //slashdot.org/ prefix+http: http:
		 */
		html = insertPageIncludeInTagIgnoreCase("href", html);
		return html;
	}

	protected String changeFormActionAttributes(String html) {
		html = insertPageIncludeInTagIgnoreCase("action", html);
		return html;
	}

	protected String changeSrcAttributes(String html) {
		html = changeURLToAbsoluteValueIgnoreCase("src", html);
		html = changeURLToAbsoluteValueIgnoreCase("background", html);
		return html;
	}

	protected String insertPageIncludeInTagIgnoreCase(String tag, String html) {
		html = insertPageIncludeInTag(tag.toLowerCase(), html);
		html = insertPageIncludeInTag(tag.toUpperCase(), html);
		return html;
	}

	protected String insertPageIncludeInTag(String tag, String html) {
		// do NOT change the order of replacements!
		// "logic" is as follows
		/*
		 * prefix of url to replace -> what changes to
		 * 
		 * 1. nothing -> http+relativeurl = baseurl // -> baseurl / -> baseurl
		 * http -> donothing = baseurl https -> donothing = httpsbaseurl http to
		 * another server -> do nothing
		 * 
		 * 2. force in frame addon baseurl -> httpprefix+baseurl
		 * 
		 * 
		 * 2b. httpprefix+baseurl -> nothing httpsbaseurl ->
		 * httpsprefix+httpsbaseurl
		 */

		tag = tag + "=\"";
		String prefixHttp = tag + this.httpPrefix;
		String prefixHttps = tag + this.httpsPrefix;

		html = TextSoap.findAndReplace(html, tag + "//", tag + this.BASEURL);// the
																				// //
																				// case
		html = TextSoap.findAndReplace(html, tag + "/", tag + this.BASEURL);
		String[] unchangedUrlsPrefixes = new String[] { "http:", "ftp:",
				"mailto:", "https:" }; // prefixes of urls not to modify, add as
										// needed
		html = TextSoap.findAndReplace(html, tag, unchangedUrlsPrefixes, tag
				+ this.RELATIVEURL);

		// System.out.println("tag+BASEURL"+tag+BASEURL);
		// System.out.println("tag+RELATIVEURL"+tag+RELATIVEURL);

		if (this.forceFrame) {
			html = TextSoap.findAndReplace(html, tag + this.BASEURL, prefixHttp
					+ this.BASEURL);// the http://baseurl case skipped the
									// tailing /
			html = TextSoap.findAndReplace(html, tag + this.BASEURLHTTPS,
					prefixHttps + this.BASEURL);// the https:// case
		}

		return html;
	}

	protected String getCurrentIBPageIDToURLString(IWContext iwc)
			throws Exception {
		BuilderService bservice = getBuilderService(iwc);
		return IB_PAGE_PARAMETER + "=" + bservice.getCurrentPageId(iwc);
	}

	protected String getSendToPageURLString() {
		return IB_PAGE_PARAMETER + "=" + this._sendToPage.getID();
	}

	protected String changeURLToAbsoluteValueIgnoreCase(String tag, String html) {
		html = changeURLToAbsoluteValue(tag.toLowerCase(), html);
		html = changeURLToAbsoluteValue(tag.toUpperCase(), html);
		return html;
	}

	protected String changeURLToAbsoluteValue(String tag, String html) {
		html = TextSoap.findAndReplace(html, tag + "=\"//", tag + "=\""
				+ "http://");// the // case
		html = TextSoap.findAndReplace(html, tag + "=\"/", tag + "=\""
				+ this.BASEURL);// the / case
		html = TextSoap.findAndReplace(html, tag + "=\"", "http://", tag
				+ "=\"" + this.RELATIVEURL);
		return html;
	}

	private String symbolReplace(String html, String tag) {
		return TextSoap.findAndReplace(html, this.symbol + tag, "&" + tag);
	}

	protected String encodeQueryStrings(String html) {
		// laddi changed links in idegaweb to use amp; instead of a &
		// so we need to fix that here!
		html = TextSoap.findAndReplace(html, "&amp;", "&");

		// laddi again, only replacing single &, a javascript issue
		html = TextSoap.findAndReplace(html, "&", "&", this.symbol);

		// fixing this should be done with a HTMLEditor object OR
		// make a single general expression fix
		// by getting all between (textSoap) changing them and then use a find
		// replace on the originals
		html = symbolReplace(html, "eth;");
		html = symbolReplace(html, "ETH;");
		html = symbolReplace(html, "thorn;");
		html = symbolReplace(html, "THORN;");
		html = symbolReplace(html, "aelig;");
		html = symbolReplace(html, "AElig;");
		html = symbolReplace(html, "ouml;");
		html = symbolReplace(html, "Ouml;");
		html = symbolReplace(html, "auml;");
		html = symbolReplace(html, "Auml;");
		html = symbolReplace(html, "euml;");
		html = symbolReplace(html, "Euml;");
		html = symbolReplace(html, "uuml;");
		html = symbolReplace(html, "Uuml;");

		html = symbolReplace(html, "nbsp;");

		// html = symbolReplace(html,"amp;");// a muuu point see top of method

		html = symbolReplace(html, "quot;");
		html = symbolReplace(html, "middot");
		html = symbolReplace(html, "raquo;");
		html = symbolReplace(html, "#149;");
		html = symbolReplace(html, "#039;");
		html = symbolReplace(html, "#169;");
		html = symbolReplace(html, "#8211;");
		html = symbolReplace(html, "gt;");
		html = symbolReplace(html, "pound;");
		html = symbolReplace(html, "yen;");
		html = symbolReplace(html, "copy;");
		html = symbolReplace(html, "reg;");
		html = symbolReplace(html, "szlig;");
		html = symbolReplace(html, "#cedil;");
		html = symbolReplace(html, "ccedil;");
		html = symbolReplace(html, "Ccedil;");
		html = symbolReplace(html, "cedil;");
		html = symbolReplace(html, "oslash;");
		html = symbolReplace(html, "Oslash;");

		html = TextSoap.findAndReplace(html, " " + this.symbol + " ", " & ");
		html = TextSoap.findAndReplace(html, this.symbol + " ", "& ");

		// islenskir broddstafir
		html = symbolReplace(html, "aacute;");
		html = symbolReplace(html, "Aacute;");
		html = symbolReplace(html, "eacute;");
		html = symbolReplace(html, "Eacute;");
		html = symbolReplace(html, "iacute;");
		html = symbolReplace(html, "Iacute;");
		html = symbolReplace(html, "uacute;");
		html = symbolReplace(html, "Uacute;");
		html = symbolReplace(html, "oacute;");
		html = symbolReplace(html, "Oacute;");
		html = symbolReplace(html, "yacute;");
		html = symbolReplace(html, "Yacute;");

		return html;
	}

	protected String decodeQueryString(String query) {
		return TextSoap.findAndReplace(query, this.symbol, "&");
	}

	protected String copyJavaScript(String html, IWContext iwc) {
		return html;
	}

	protected String copyIncludes(String html, IWContext iwc) {
		return html;
	}

	public synchronized Object clone() {
		PageIncluder obj = null;
		try {
			obj = (PageIncluder) super.clone();
			obj.URL = this.URL;
			obj.BASEURL = this.BASEURL;
			obj.RELATIVEURL = this.RELATIVEURL;
			obj.pageIncluderPrefix = this.pageIncluderPrefix;
			obj.instanceId = this.instanceId;
			obj.sessionId = this.sessionId;
			obj.sessionURL = this.sessionURL;
			obj.token = this.token;
			obj.tokenReplacer = this.tokenReplacer;
			obj.out = this.out;
			obj.index = this.index;
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setURLToGetSessionIDFrom(String sessionURL) {
		this.sessionURL = sessionURL;
	}

	public void setTokenToReplaceWithSessionId(String token) {
		this.token = token;
	}

	public void setForceInFrame(boolean forceFrame) {
		this.forceFrame = forceFrame;
	}

	public void setKeepSecure(boolean useSecureLinks) {
		this.useSecureLinks = useSecureLinks;
	}

	public void setLabel(String label) {
		this._label = label;
	}

	/**
	 * Redirects the URL from this PageIncluder to another PageIncluder with the
	 * corresponding label.
	 * 
	 * @param label
	 *            The label of the PageIncluder which we want to redirect to.
	 */
	public void setRedirectTo(String label) {
		this._sendToLabel = label;
	}

	public String getLabel() {
		return this._label;
	}

	public String getRedirectTo() {
		return this._sendToLabel;
	}

	public void setSendToPage(ICPage page) {
		this._sendToPage = page;
	}

	public ICPage getSendToPage() {
		return this._sendToPage;
	}

	public void setSendToPageIfSet(String condition) {
		this._sendToPageIfSet = condition;
	}

	public String getSendToPageIfSet() {
		return this._sendToPageIfSet;
	}

	public void forwardToIBPage(Page fromPage, ICPage page, IWContext iwc)
			throws Exception {
		StringBuffer URL = new StringBuffer();
		BuilderService bservice = getBuilderService(iwc);
		URL.append(bservice.getPageURI(((Integer) page.getPrimaryKeyValue())
				.intValue()));
		URL.append('&');
		String query = getRequest().getQueryString();
		if (this._sendToLabel != null) {
			query = TextSoap.findAndReplace(query, PAGE_INCLUDER_PARAMETER_NAME
					+ this.instanceId, PAGE_INCLUDER_PARAMETER_NAME
					+ this._sendToLabel);
		}
		URL.append(query);

		fromPage.setToRedirect(URL.toString());
		fromPage.empty();
	}

	/**
	 * changes the pageincluders url if needed and adds all parameters from the
	 * request.
	 */
	private String getLocation(IWContext iwc) {
		StringBuffer location = new StringBuffer();
		String query = null;
		StringBuffer queryBuf = new StringBuffer();
		String instanceParam = PAGE_INCLUDER_PARAMETER_NAME + this.instanceId;
		String labelParam = PAGE_INCLUDER_PARAMETER_NAME + this._label;

		// get all parameters even from post actions
		Enumeration enumer = iwc.getParameterNames();
		while (enumer.hasMoreElements()) {
			String param = (String) enumer.nextElement();
			// debug(param+" : "+iwc.getParameter(param));
			if (param.equals(instanceParam) || param.equals(labelParam)) {
				boolean canChangeURL = canChangeURLFromRequest(iwc);
				if (canChangeURL) {
					this.URL = decodeQueryString(iwc.getParameter(param));
				}
				// System.out.println("Changing location to:"+location.toString());
			} else {
				if (param.indexOf(PAGE_INCLUDER_PARAMETER_NAME) == -1) {
					String[] values = iwc.getParameterValues(param);
					for (int i = 0; i < values.length; i++) {
						queryBuf.append(param);
						queryBuf.append("=");
						queryBuf.append(URLEncoder.encode(values[i]));
						queryBuf.append("&");
					}
				}
			}
		}// while ends

		query = queryBuf.toString();

		location.append(this.URL);

		if (!query.equals("")) {
			if (this.URL.endsWith("/")) {// check if the url ends with a slash
				location.append("?");
			} else {// no slash at end
				if (this.URL.indexOf("?") == -1) {// check if the url contains a
													// ?
					if (this.URL.indexOf("/", 8) != -1) {// check if the url
															// contains a slash
						location.append("?");
					} else {
						location.append("/?");
					}
				} else {// just add to the parameters
					location.append("&");
				}
			}
			// add the extra parameters
			location.append(query);
		}

		String finalLocationString = finalizeLocationString(
				location.toString(), iwc);

		return finalLocationString;

	}

	/**
	 * A method for extending classes to influence the location string (the url)
	 * that is sent to the real page. For example to add extra parameters.
	 * 
	 * @param location
	 *            The finished url to the real page the pageincluder is
	 *            including
	 * @return
	 */
	protected String finalizeLocationString(String location, IWContext iwc) {
		return location;
	}

	private void setPrefixes(IWContext iwc) throws Exception {
		if (this.forceFrame) {
			StringBuffer buf = new StringBuffer();
			String uri = iwc.getRequestURI();

			buf.append(uri);
			buf.append('?');

			if (this._sendToPage != null) {

				if ((this._sendToLabel != null)
						&& (this._sendToPageIfSet == null)) {
					buf.append(getSendToPageURLString());
					buf.append('&');
					buf.append(PAGE_INCLUDER_PARAMETER_NAME);
					buf.append(this._sendToLabel);
					buf.append('=');
				} else {
					buf.append(getCurrentIBPageIDToURLString(iwc));
					buf.append('&');
					buf.append(PAGE_INCLUDER_PARAMETER_NAME);
					buf.append(this.instanceId);
					buf.append('=');
				}
			} else {
				buf.append(getCurrentIBPageIDToURLString(iwc));
				buf.append('&');

				if ((this._sendToLabel != null)
						&& (this._sendToPageIfSet == null)) {
					buf.append(PAGE_INCLUDER_PARAMETER_NAME);
					buf.append(this._sendToLabel);
					buf.append('=');
				} else {
					buf.append(PAGE_INCLUDER_PARAMETER_NAME);
					buf.append(this.instanceId);
					buf.append('=');
				}
			}

			this.pageIncluderPrefix = buf.toString();

			if (this.useSecureLinks) {
				buf.append("https://");
				buf.append(iwc.getServerName());
			}

			StringBuffer buf2 = new StringBuffer();

			buf2.append("http://").append(this.serverName)
					.append(this.pageIncluderPrefix);
			// .append("http://");
			this.httpPrefix = buf2.toString();

			// System.out.println("httpPrefix"+httpPrefix);

			this.httpsPrefix = "https://"
					+ this.httpPrefix.substring(7, this.httpPrefix.length());

			// System.out.println("httpSPrefix"+httpsPrefix);
			// System.out.println("PAGEINCLUDER PREFIX = "+pageIncluderPrefix);
		} else {
			this.pageIncluderPrefix = "";
		}
	}

	private String changeJSOpenWindowURL(String html) {
		String regex = ":openwindow\\(\\\'\\/servlet\\/WindowOpener\\??[\\w+\\=\\-?.+\\"
				+ this.symbol + "?]*\\\',";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			StringBuffer sbURL = new StringBuffer();
			StringBuffer sbSymbol = new StringBuffer();
			String rURL = "\\(\\\'\\/servlet";
			Pattern pURL = Pattern.compile(rURL);
			Matcher mURL = pURL.matcher(matcher.group());
			while (mURL.find()) {
				mURL.appendReplacement(sbURL, "('" + this.BASEURL + "servlet");
			}
			mURL.appendTail(sbURL);
			String rSymbol = "\\" + this.symbol;
			Pattern pSymbol = Pattern.compile(rSymbol);
			Matcher mSymbol = pSymbol.matcher(sbURL.toString());
			while (mSymbol.find()) {
				mSymbol.appendReplacement(sbSymbol, "&");
			}
			mSymbol.appendTail(sbSymbol);

			matcher.appendReplacement(sb, sbSymbol.toString());

		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public void setToAddRequiredIPOrDomain(String allowedIpOrDomain) {

		if (this.allowedDomainsAndIPNumberMap == null) {
			this.allowedDomainsAndIPNumberMap = new HashMap();
		}

		this.allowedDomainsAndIPNumberMap.put(allowedIpOrDomain,
				allowedIpOrDomain);// map to avoid adding endlessly into a list
									// in the builder

	}

}
