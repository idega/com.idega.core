package com.idega.idegaweb.include;

import com.idega.core.file.util.MimeTypeUtil;

/**
 * Constants for page resources
 * 
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2009/04/24 08:39:08 $ by: $Author: valdas $
 *
 */
public class PageResourceConstants {

	public final static String MEDIA_SCREEN="screen";
	public final static String MEDIA_TTY="tty";
	public final static String MEDIA_TV="tv";
	public final static String MEDIA_PROJECTION="projection";
	public final static String MEDIA_HANDHELD="handheld";
	public final static String MEDIA_PRINT="print";
	public final static String MEDIA_BRAILLE="braille";
	public final static String MEDIA_AURAL="aural";
	public final static String MEDIA_ALL="all";
	
	public final static String TYPE_CSS = MimeTypeUtil.MIME_TYPE_CSS;
	public final static String TYPE_RSS = "application/rss+xml";
	public final static String TYPE_ATOM = "application/atom+xml";
	public final static String TYPE_JAVA_SCRIPT = "text/javascript";
	
	protected final static String RELATIONSHIP_STYLESHEET="stylesheet";
	protected final static String RELATIONSHIP_ALTERNATE="alternate";

}
