/*
 * Created on Jan 7, 2004
 *
 */
package com.idega.core.file.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.idega.core.file.util.MimeTypeUtil;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;
import com.idega.util.SortedProperties;

/**
 * The <code>FileIconSupplier </code> supplies icon URIs from a properties file
 * (core.bundle/resources/icons/themes/"youthemename"/...) by a
 * "mime-type/category=iconfilename" pairs mappings or tries to guess the
 * mimetype if you only have the filename<br>
 * . Use the getInstance methods and supply a theme name (no special characters
 * please...) if you don't want the default file system icon set
 * 
 * @author <a href="aron@idega.is>Aron</a>,<a href="eiki@idega.is>Eirikur S.
 *         Hrafnsson</>
 * @version 1.5
 */
public class FileIconSupplier {

	protected static final String FOLDER_NAME_THEMES = "themes";
	protected static final String FOLDER_NAME_ICONS = "icons";

	public static final String DEFAULT_THEME_NAME = "iw";
	public static final String DEFAULT_ICON_FILE_NAME_FOLDER_CLOSED = "folder-closed.gif";
	public static final String DEFAULT_ICON_FILE_NAME_FOLDER_OPEN = "folder-open.gif";
	public static final String DEFAULT_ICON_FILE_NAME_FOLDER = "folder.gif";

	public static final String FAKE_MIME_TYPE_FOLDER_CLOSED = "folder-closed";
	public static final String FAKE_MIME_TYPE_FOLDER_OPEN = "folder-open";
	public static final String FAKE_MIME_TYPE_FOLDER = "folder";

	public static final String DEFAULT_ICON_FILE_NAME_BINARY = "binary.gif";
	public static final String DEFAULT_ICON_FILE_NAME_AUDIO = "audio.gif";
	public static final String DEFAULT_ICON_FILE_NAME_COMPRESSED = "compressed.gif";
	public static final String DEFAULT_ICON_FILE_NAME_DOCUMENT = "document.gif";
	public static final String DEFAULT_ICON_FILE_NAME_IMAGE = "image.gif";
	public static final String DEFAULT_ICON_FILE_NAME_VECTOR = "vector.gif";
	public static final String DEFAULT_ICON_FILE_NAME_VIDEO = "video.gif";

	public static final String DEFAULT_ICON_FILE_NAME_WORD = "word.gif";
	public static final String DEFAULT_ICON_FILE_NAME_EXCEL = "excel.gif";
	public static final String DEFAULT_ICON_FILE_NAME_POWERPOINT = "powerpoint.gif";
	public static final String DEFAULT_ICON_FILE_NAME_PDF = "pdf.gif";

	private static final FileNameMap fileNameMap = URLConnection.getFileNameMap();
	private static final String ICON_SUPPLIER_APPLICATION_PROP_PREFIX = "iw_file_icon_supplier";
	private String theme;
	private String resourceURI;
	private String resourceURL;

	private Properties properties;
	private static final String PROPS_FILE_NAME_PREFIX = "mimetype-icon-";
	private static final String PROPS_FILE_NAME_SUFFIX = ".props";

	protected FileIconSupplier() {
		this(DEFAULT_THEME_NAME);
	}

	protected FileIconSupplier(String themeName) {
		this.theme = themeName;
	}

	/**
	 * Gets the default icon set.
	 * 
	 * @param iwac
	 * @return
	 */
	public static FileIconSupplier getInstance() {
		return getInstance(DEFAULT_THEME_NAME);
	}

	/**
	 * Gets a custom set of mimetype to icon mapper (Theme). <br>
	 * The mimetype-iconfile properties file for the theme will be created
	 * automatically with default values<br>
	 * if not present under core.bundle/properties with the name: theme-mimetype
	 * 
	 * @param themeName
	 * @return
	 */
	public static FileIconSupplier getInstance(String themeName) {
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		String supplier = ICON_SUPPLIER_APPLICATION_PROP_PREFIX + themeName;
		FileIconSupplier theme = (FileIconSupplier) iwac.getApplicationAttribute(supplier);
		if (theme == null) {
			theme = new FileIconSupplier(themeName);
			iwac.setApplicationAttribute(supplier, theme);
		}

		return theme;
	}

	/**
	 * Returns the uri (relative to the context) to the icon file for the
	 * guestimated mimetype of the filename you supply
	 * 
	 * @param fileName
	 *            The file name, must contain a suffix e.g. ".jpg", ".doc" etc.
	 * @return the uri (relative to the context) to the icon file for the files
	 *         guestimated mimetype
	 */
	public String getFileIconURIByFileName(String fileName) {
		return getFileIconURIByMimeType(guessMimeTypeFromFileName(fileName));
	}

	/**
	 * Returns the url (with the context) to the icon file for the guestimated
	 * mimetype of the filename you supply
	 * 
	 * @param fileName
	 *            The file name, must contain a suffix e.g. ".jpg", ".doc" etc.
	 * @return the url (with the context) to the icon file for the files
	 *         guestimated mimetype
	 */
	public String getFileIconURLByFileName(String fileName) {
		return getFileIconURLByMimeType(guessMimeTypeFromFileName(fileName));
	}

	/**
	 * Returns the uri (relative to the context) to the icon file for this
	 * mimetype
	 * 
	 * @param mimeType
	 *            The mime type/content type of the files you want an icon for
	 * @return the uri (relative to the context) to the icon file for this
	 *         mimetype
	 */
	public String getFileIconURIByMimeType(String mimeType) {
		return getFileIconURIorURLByMimeType(mimeType, false);
	}

	/**
	 * Returns the url (with the context) to the icon file for this mimetype
	 * 
	 * @param mimeType
	 *            The mime type/content type of the files you want an icon for
	 * @return the url (with the context) to the icon file for this mimetype
	 */
	public String getFileIconURLByMimeType(String mimeType) {
		return getFileIconURIorURLByMimeType(mimeType, true);
	}

	/**
	 * Returns the uri (relative to the context) or the url (with context) to
	 * the icon file for this mimetype
	 * 
	 * @param mimeType
	 *            The mime type/content type of the files you want an icon for
	 * @param withContext
	 *            should we return an URL (with context, true) or an URI
	 *            (without context,false)
	 * @return an URL (with context, true) or an URI (without context,false) for
	 *         this mimetype
	 */
	protected String getFileIconURIorURLByMimeType(String mimeType, boolean withContext) {
		MimeTypeUtil util = MimeTypeUtil.getInstance();
		String iconURLorURIWithFileName = null;
		// check first for a direct mimetype to iconfilename mapping
		// check then for folder type
		// check then for special cases like pdf,word,excel,powerpoint...
		// then check for other types
		if (mimeType != null) {
			try {
				String value = getThemeSettings().getProperty(mimeType);
				if (value != null) {
					return (withContext) ? getIconURLForIconFileName(value) : getIconURIForIconFileName(value);
				}
			}
			catch (IOException e) {
				// if this fails at least we can fall back on the method below
				e.printStackTrace();
			}
		}

		if (iconURLorURIWithFileName == null) {
			if ((mimeType == null) || util.isFolder(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getFolderIconURL() : getFolderIconURI();
			}
			else if (util.isWord(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_WORD) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_WORD);
			}
			else if (util.isExcel(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_EXCEL) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_EXCEL);
			}
			else if (util.isPowerPoint(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_POWERPOINT) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_POWERPOINT);
			}
			else if (util.isPDF(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_PDF) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_PDF);
			}
			else if (util.isImage(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_IMAGE) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_IMAGE);
			}
			else if (util.isDocument(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_DOCUMENT) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_DOCUMENT);
			}
			else if (util.isCompressed(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_COMPRESSED) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_COMPRESSED);
			}
			else if (util.isAudio(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_AUDIO) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_AUDIO);
			}
			else if (util.isVector(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_VECTOR) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_VECTOR);
			}
			else if (util.isVideo(mimeType)) {
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_VIDEO) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_VIDEO);
			}
			else {
				// everything else is a binary
				// (util.isBinary(mimeType)){
				iconURLorURIWithFileName = (withContext) ? getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_BINARY) : getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_BINARY);
			}
		}

		return iconURLorURIWithFileName;
	}

	/**
	 * Withpout context
	 * 
	 * @param iconName
	 * @return
	 */
	public String getIconURIForIconFileName(String iconName) {
		StringBuffer uri = new StringBuffer(getIconRootFolderURI());
		uri.append(getTheme());
		uri.append("/");
		uri.append(iconName);
		return uri.toString();
	}

	/**
	 * With context
	 * 
	 * @param iconName
	 * @return
	 */
	public String getIconURLForIconFileName(String iconName) {
		StringBuffer url = new StringBuffer(getIconRootFolderURL());
		url.append(getTheme());
		url.append("/");
		url.append(iconName);
		return url.toString();
	}

	/**
	 * Without the context
	 * 
	 * @return
	 */
	public String getIconThemeFolderRealPath() {
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		String realpath = iwma.getCoreBundle().getResourcesRealPath();
		return realpath + getIconThemeFolderInsideResources();
	}

	/**
	 * <p>
	 * TODO menesis describe method getIconThemeFolderInsideResources
	 * </p>
	 * 
	 * @return
	 */
	private String getIconThemeFolderInsideResources() {
		return File.separator + FOLDER_NAME_ICONS + File.separator + FOLDER_NAME_THEMES + File.separator + getTheme() + File.separator;
	}

	/**
	 * Without the context
	 * 
	 * @return
	 */
	public String getIconRootFolderURI() {
		if (this.resourceURI == null) {
			this.resourceURI = getIconRootFolderURL();
			this.resourceURI = IWMainApplication.getDefaultIWMainApplication().getURIFromURL(this.resourceURI);
		}

		return this.resourceURI;
	}

	/**
	 * With the context
	 * 
	 * @return
	 */
	public String getIconRootFolderURL() {
		if (this.resourceURL == null) {
			IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
			initializeThemeFolder();
			this.resourceURL = iwma.getCoreBundle().getResourcesURL() + "/" + FOLDER_NAME_ICONS + "/" + FOLDER_NAME_THEMES + "/";
		}

		return this.resourceURL;
	}

	/**
	 * Creates the theme folder and copies the default images if needed and
	 * creates the mime-type-imagefilename properties file
	 */
	protected void initializeThemeFolder() {
		String themeFolder = getIconThemeFolderRealPath();
		FileUtil.createFolder(themeFolder);

	}

	public String getFolderIconURL() {
		return getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_FOLDER);
	}

	public String getFolderIconURI() {
		return getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_FOLDER);
	}

	public String getFolderOpenIconURL() {
		return getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_FOLDER_OPEN);
	}

	public String getFolderOpenIconURI() {
		return getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_FOLDER_OPEN);
	}

	public String getFolderClosedIconURL() {
		return getIconURLForIconFileName(DEFAULT_ICON_FILE_NAME_FOLDER_CLOSED);
	}

	public String getFolderClosedIconURI() {
		return getIconURIForIconFileName(DEFAULT_ICON_FILE_NAME_FOLDER_CLOSED);
	}

	public String guessMimeTypeFromFileName(String fileName) {
		String theReturn = fileNameMap.getContentTypeFor(fileName);
		if (theReturn == null) {
			// A few additions that aren't covered by the fileNameMap
			if (fileName.endsWith(".css")) {
				return MimeTypeUtil.MIME_TYPE_CSS;
			}
		}
		return theReturn;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String themeName) {
		this.theme = themeName;
	}

	protected synchronized void storeProperties(Properties props, String pathToSettingsFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(pathToSettingsFile);
		props.store(fos, null);
		fos.close();
	}

	public Properties getThemeSettings() throws IOException {
		if (this.properties == null) {
			String pathToFile = getIconThemeFolderRealPath() + PROPS_FILE_NAME_PREFIX + getTheme() + PROPS_FILE_NAME_SUFFIX;
			InputStream in = null;
			try {
				File file = new File(pathToFile);
				if (file.exists()) {
					in = new FileInputStream(file);
				}
				else {
					in = IWMainApplication.getDefaultIWMainApplication().getCoreBundle().getResourceInputStream("resources" + getIconThemeFolderInsideResources() + PROPS_FILE_NAME_PREFIX + getTheme() + PROPS_FILE_NAME_SUFFIX);
				}
				this.properties = new SortedProperties();
				this.properties.load(in);
				return this.properties;
			}
			catch (FileNotFoundException e) {
				// create the file if it does not exist and fill with the data
				System.out.println("[MimeTypeUtil] - No mime-type.props file. Creating mime type properties file : " + pathToFile);

				FileUtil.createFile(pathToFile);
				this.properties = new SortedProperties();
				this.properties.load(new FileInputStream(pathToFile));

				fillProperties(this.properties);

				storeProperties(this.properties, pathToFile);
			}
			finally {
				IOUtils.closeQuietly(in);
			}
		}

		return this.properties;
	}

	/**
	 * Fills the properties with "mime-type/category=iconfilename" pairs
	 * 
	 * @param properties
	 */
	protected void fillProperties(Properties properties) {
		properties.setProperty(MimeTypeUtil.MIME_TYPE_WORD, DEFAULT_ICON_FILE_NAME_WORD);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_EXCEL, DEFAULT_ICON_FILE_NAME_EXCEL);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_POWERPOINT, DEFAULT_ICON_FILE_NAME_POWERPOINT);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_PDF_1, DEFAULT_ICON_FILE_NAME_PDF);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_PDF_2, DEFAULT_ICON_FILE_NAME_PDF);
		properties.setProperty(FAKE_MIME_TYPE_FOLDER, DEFAULT_ICON_FILE_NAME_FOLDER);
		properties.setProperty(FAKE_MIME_TYPE_FOLDER_OPEN, DEFAULT_ICON_FILE_NAME_FOLDER_OPEN);
		properties.setProperty(FAKE_MIME_TYPE_FOLDER_CLOSED, DEFAULT_ICON_FILE_NAME_FOLDER_CLOSED);

		// categories
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_AUDIO, DEFAULT_ICON_FILE_NAME_AUDIO);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_BINARY, DEFAULT_ICON_FILE_NAME_BINARY);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_COMPRESSED, DEFAULT_ICON_FILE_NAME_COMPRESSED);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_DOCUMENT, DEFAULT_ICON_FILE_NAME_DOCUMENT);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_FOLDER, DEFAULT_ICON_FILE_NAME_FOLDER);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_FOLDER, DEFAULT_ICON_FILE_NAME_FOLDER_OPEN);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_FOLDER, DEFAULT_ICON_FILE_NAME_FOLDER_CLOSED);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_IMAGE, DEFAULT_ICON_FILE_NAME_IMAGE);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_VECTOR, DEFAULT_ICON_FILE_NAME_VECTOR);
		properties.setProperty(MimeTypeUtil.MIME_TYPE_CATEGORY_VIDEO, DEFAULT_ICON_FILE_NAME_VIDEO);
	}

}
