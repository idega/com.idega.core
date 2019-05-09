package com.idega.core.file.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Singleton;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.SortedProperties;
import com.idega.util.StringUtil;

/**
 * Title: MimeTypeUtil Description: Creates and loads a properties file under
 * core.bundle/properties/mime-types.props with "mime-type=category" pairs<br>
 * to make it easy to have mime types categorized into fewer elements.<br>
 * The categories are:<br>
 * <li>binary</li> - for misc. binary formats, applications and unknown
 * mime-types.
 * <li>audio</li> - for audio files.
 * <li>compressed</li> - for compressed files
 * <li>document</li> - for readable or well known document- and text-types
 * such as txt,pdf,rtf,word,excel...
 * <li>folder</li> - for folder and collection like types
 * <li>image</li> - for image files
 * <li>vector</li> - for vector graphics and other vector based multimedia
 * files types
 * <li>video</li> - for video files
 *
 *
 * Copyright: Copyright (c) 2004 Company: Idega Software
 *
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.0
 *
 * Last modified: $Date: 2009/01/20 17:04:16 $ by $Author: valdas $
 */
public class MimeTypeUtil implements Singleton {

	private static final Logger LOGGER = Logger.getLogger(MimeTypeUtil.class.getName());

	public static final String MIME_TYPE_WORD = "application/msword";
	public static final String MIME_TYPE_EXCEL = "application/vnd.ms-excel";
	public static final String MIME_TYPE_EXCEL_2 = "application/x-msexcel";
	public static final String MIME_TYPE_POWERPOINT = "application/vnd.ms-powerpoint";
	public static final String MIME_TYPE_PDF_2 = "application/x-pdf";
	public static final String MIME_TYPE_PDF_1 = "application/pdf";
	public static final String MIME_TYPE_ZIP = "application/zip";
	public static final String MIME_TYPE_XML = "text/xml";
	public static final String MIME_TYPE_CSS = "text/css";
	public static final String MIME_TYPE_HTML = "text/html";
	public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
	public static final String MIME_TYPE_APPLICATION = "application/octet-stream";
	public static final String MIME_TYPE_CALENDAR = "text/calendar";
	public static final String MIME_TYPE_ENCODED_URL = "application/x-www-form-urlencoded";
	public static final String MIME_TYPE_JSON = "application/json";
	public static final String MIME_TYPE_CSV = "text/csv";
	public static final String MIME_TYPE_DOCX = "vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String MIME_TYPE_DOTX = "vnd.openxmlformats-officedocument.wordprocessingml.template";

	private String pathToConfigFile;
	private Properties properties;

	public static final String MIME_TYPE_PROPS_FILE_NAME = "mime-types.props";

	public static final String MIME_TYPE_CATEGORY_BINARY = "binary";
	public static final String MIME_TYPE_CATEGORY_AUDIO = "audio";
	public static final String MIME_TYPE_CATEGORY_COMPRESSED = "compressed";
	public static final String MIME_TYPE_CATEGORY_DOCUMENT = "document";
	public static final String MIME_TYPE_CATEGORY_IMAGE = "image";
	public static final String MIME_TYPE_CATEGORY_VECTOR = "vector";
	public static final String MIME_TYPE_CATEGORY_VIDEO = "video";
	public static final String MIME_TYPE_CATEGORY_FOLDER = "folder";

	// folders
	private String[] folder = { "application/vnd.iw-folder" };

	// executable applications, binaries or unknown types
	private String[] binary = { MIME_TYPE_APPLICATION, "application/acad",
			"application/arj", "application/book", "application/cdf",
			"application/clariscad", "application/commonground",
			"application/drafting", "application/dxf", "application/envoy",
			"application/fractals", "application/freeloader",
			"application/hlp", "application/hta", "application/iges",
			"application/inf", "application/java",
			"application/java-byte-code", "application/lha", "application/lzx",
			"application/mbedlet", "application/mime",
			"application/pkcs-crl",
			"application/pkix-cert", "application/pkix-crl",
			"application/vnd.fdf", "application/vnd.hp-HPGL",
			"application/vnd.ms-pki.seccat", "application/x-aim",
			"application/x-authorware-bin", "application/x-authorware-map",
			"application/x-authorware-seg", "application/x-bcpio",
			"application/x-bsh",
			"application/x-bytecode.elisp (Compiled ELisp)",
			"application/x-cdf", "application/x-chat", "application/x-cocoa",
			"application/x-cpio", "application/x-cpt", "application/x-csh",
			"application/x-deepv", "application/x-dvi", "application/x-elc",
			"application/x-envoy", "application/x-esrehber",
			"application/x-gsp", "application/x-gss", "application/x-hdf",
			"application/x-helpfile", "application/x-httpd-imap",
			"application/x-ima", "application/x-internett-signup",
			"application/x-inventor", "application/x-ip2",
			"application/x-java-class", "application/x-java-commerce",
			"application/x-javascript", "application/x-ksh",
			"application/x-lha", "application/x-lisp",
			"application/x-livescreen", "application/x-magic-cap-package-1.0",
			"application/x-mplayer2", "application/x-navi-animation",
			"application/x-navimap", "application/x-netcdf",
			"application/x-nokia-9000-communicator-add-on-software",
			"application/x-pointplus", "application/x-troff-man",
			"application/x-winhelp", "application/x-x509-ca-cert",
			"application/x-x509-user-cert", "drawing/x-dwf (old)",
			"i-world/i-vrml", "model/iges", "model/vnd.dwf" };

	private String[] audio = { "audio/basic", "audio/x-aiff", "audio/x-wav",
			"audio/x-mpeg", "audio/x-mpeg-2", "audio/mpeg", "audio/aiff",
			"audio/it", "audio/make", "audio/midi", "audio/nspaudio",
			"audio/x-au", "audio/x-gsm", "audio/x-jam", "audio/x-liveaudio",
			"audio/x-mpequrl", "audio/x-nspaudio", "music/x-karaoke",
			"x-music/x-midi", "x-conference/x-cooltalk" };

	private String[] document = { "text/html", "text/plain", MIME_TYPE_XML,
			"text/richtext", "text/enriched",
			MimeTypeUtil.MIME_TYPE_CSS, "application/postscript",
			"application/rtf", MIME_TYPE_PDF_1, MIME_TYPE_PDF_2,
			MIME_TYPE_WORD, "application/mspowerpoint", MIME_TYPE_POWERPOINT,
			"application/vnd.ms-project", MIME_TYPE_EXCEL,
			"application/vnd.ms-works", "application/mac-binhex40",
			"application/x-stuffit", "text/javascript",
			"application/x-javascript", "application/x-gtar",
			"application/x-tar", "image/psd", "application/postscript",
			"application/x-latex", "text/asp", "text/vnd.abc",
			"text/vnd.fmi.flexstor", "text/webviewhtml", "text/x-asm",
			"text/x-audiosoft-intra", "text/x-c", "text/x-component",
			"text/x-fortran", "text/x-h", "text/x-java-source",
			"text/x-la-asf", "text/x-m", "text/x-script", "text/x-script.csh",
			"text/x-script.elisp", "text/x-script.ksh", "text/x-script.lisp",
			"text/x-setext" };

	private String[] image = { "image/gif", "image/x-xbitmap", "image/x-xpix",
			"image/x-png", "image/png", "image/ief", "image/jpeg",
			"image/pjpeg", "image/jpg", "image/jpe", "image/tiff",
			"image/x-pict", "image/pict", "image/x-ms-bmp", "image/bmp",
			"image/x-bmp", "image/pcx", "image/iff", "image/ras",
			"image/x-portable-bitmap", "image/x-portable-graymap",
			"image/x-portable-pixmap", "image/fif", "image/florian",
			"image/g3fax", "image/jutvision", "image/vnd.dwg", "image/vnd.fpx",
			"image/vnd.net-fpx", "image/x-dwg", "image/x-icon", "image/x-jg",
			"image/x-jps", "image/x-windows-bmp" };

	private String[] vector = { "application/futuresplash",
			"application/x-director", "application/x-shockwave-flash" };

	private String[] video = { "video/mpeg", "video/mpeg-2", "video/quicktime",
			"video/x-msvideo", "video/x-sgi-movie", "x-world/x-3dmf",
			"video/animaflex", "video/avi", "video/avs-video", "video/dl",
			"video/fli", "video/gl", "video/msvideo",
			"video/x-atomic3d-feature", "video/x-dl", "video/x-dv",
			"video/x-fli", "video/x-gl", "video/x-isvideo", "video/x-ms-asf",
			"video/x-ms-asf-plugin", "video/x-msvideo",
			"application/x-troff-msvideo" };

	private String[] compressed = { "application/binhex",
			"application/binhex4", "application/mac-binary",
			"application/mac-binhex", "application/mac-binhex40",
			"application/mac-compactpro", "application/macbinary",
			"application/x-binary", "application/x-binhex40",
			"application/x-bzip", "application/x-bzip2",
			"application/x-compactpro", "application/x-compressed",
			"application/x-gtar", "application/x-gzip", "application/x-lzh",
			"application/x-lzx", "application/x-mac-binhex40",
			"application/x-macbinary", "application/x-zip-compressed",
			"application/zip", "multipart/x-gzip" };


	private static Map<String, String> MIME_TYPES_MAPPING = new HashMap<String, String>();

	private static void initializeMimeTypesMap() {
		if (!MIME_TYPES_MAPPING.isEmpty()) return;

		MIME_TYPES_MAPPING.put("doc", MIME_TYPE_WORD);
		MIME_TYPES_MAPPING.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		MIME_TYPES_MAPPING.put("xls", MIME_TYPE_EXCEL);
		MIME_TYPES_MAPPING.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		MIME_TYPES_MAPPING.put("ppt", MIME_TYPE_POWERPOINT);
		MIME_TYPES_MAPPING.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		MIME_TYPES_MAPPING.put("css", "text/css");
		MIME_TYPES_MAPPING.put("js", "text/javascript");
		MIME_TYPES_MAPPING.put("woff", "application/font-woff");
	}

	protected MimeTypeUtil() {
		initializeMimeTypesMap();

		try {
			this.properties = getMimeTypeSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static MimeTypeUtil getInstance() {
		IWApplicationContext iwac = IWMainApplication
				.getDefaultIWApplicationContext();
		MimeTypeUtil util = (MimeTypeUtil) iwac
				.getApplicationAttribute(MIME_TYPE_PROPS_FILE_NAME);
		if (util == null) {
			util = new MimeTypeUtil();
			iwac.setApplicationAttribute(MIME_TYPE_PROPS_FILE_NAME, util);
		}

		return util;
	}

	public Properties getMimeTypeSettings() throws IOException {
		if (this.properties == null) {
			String pathToFile = getRealPathToConfigFiles()
					+ MIME_TYPE_PROPS_FILE_NAME;
			InputStream in = null;
			try {
				File file = new File(pathToFile);
				if (file.exists()) {
					in = new FileInputStream(file);
				} else {
					in = IWMainApplication.getDefaultIWMainApplication()
							.getCoreBundle().getResourceInputStream(
									"properties/" + MIME_TYPE_PROPS_FILE_NAME);
				}
				this.properties = new SortedProperties();
				this.properties.load(in);
				return this.properties;
			} catch (FileNotFoundException e) {
				// create the file if it does not exist and fill with the data
				System.out
						.println("[MimeTypeUtil] - No mime-type.props file. Creating mime type properties file : "
								+ pathToFile);

				FileUtil.createFile(pathToFile);
				this.properties = new SortedProperties();
				this.properties.load(new FileInputStream(pathToFile));

				fillPropertiesWithMimeTypes(this.properties);

				storeProperties(this.properties, pathToFile);
			}
			finally {
				IOUtils.closeQuietly(in);
			}
		}

		return this.properties;
	}

	/**
	 * Fills the properties with "mime-type=category" pairs
	 *
	 * @param properties
	 */
	protected void fillPropertiesWithMimeTypes(Properties properties) {
		addToProperties(this.audio, MIME_TYPE_CATEGORY_AUDIO, properties);
		addToProperties(this.binary, MIME_TYPE_CATEGORY_BINARY, properties);
		addToProperties(this.compressed, MIME_TYPE_CATEGORY_COMPRESSED,
				properties);
		addToProperties(this.document, MIME_TYPE_CATEGORY_DOCUMENT, properties);
		addToProperties(this.folder, MIME_TYPE_CATEGORY_FOLDER, properties);
		addToProperties(this.image, MIME_TYPE_CATEGORY_IMAGE, properties);
		addToProperties(this.vector, MIME_TYPE_CATEGORY_VECTOR, properties);
		addToProperties(this.video, MIME_TYPE_CATEGORY_VIDEO, properties);
	}

	/**
	 * Iterates through the array and adds the mimetype and the category to the
	 * properties
	 *
	 * @param mimeTypes
	 * @param categoryName
	 * @param properties
	 */
	private void addToProperties(String[] mimeTypes, String categoryName,
			Properties properties) {
		for (int i = 0; i < mimeTypes.length; i++) {
			String mime = mimeTypes[i];
			properties.setProperty(mime, categoryName);
		}
	}

	protected String getRealPathToConfigFiles() {
		if (this.pathToConfigFile == null) {
			this.pathToConfigFile = IWMainApplication
					.getDefaultIWMainApplication().getCoreBundle()
					.getPropertiesRealPath()
					+ File.separator;
		}
		return this.pathToConfigFile;
	}

	protected void storeProperties(Properties props,
			String pathToSettingsFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(pathToSettingsFile);
		props.store(fos, null);
		fos.close();
	}

	public boolean isAudio(String mimeType) {
		return isMimeTypeInCategory(mimeType, MIME_TYPE_CATEGORY_AUDIO);
	}

	public boolean isBinary(String mimeType) {
		return isMimeTypeInCategory(mimeType, MIME_TYPE_CATEGORY_BINARY);
	}

	public boolean isCompressed(String mimeType) {
		return isMimeTypeInCategory(mimeType, MIME_TYPE_CATEGORY_COMPRESSED) || "application/x-zip".equals(mimeType);
	}

	public boolean isZip(String mimeType) {
		return MIME_TYPE_ZIP.equals(mimeType);
	}

	public boolean isDocument(String mimeType) {
		return isMimeTypeInCategory(mimeType, MIME_TYPE_CATEGORY_DOCUMENT);
	}

	public boolean isFolder(String mimeType) {
		if ("".equals(mimeType)) {
			return true;
		} else {
			return isMimeTypeInCategory(mimeType, MIME_TYPE_CATEGORY_FOLDER);
		}
	}

	public boolean isImage(String mimeType) {
		return isMimeTypeInCategory(mimeType, MIME_TYPE_CATEGORY_IMAGE);
	}

	public boolean isVector(String mimeType) {
		return isMimeTypeInCategory(mimeType, MIME_TYPE_CATEGORY_VECTOR);
	}

	public boolean isVideo(String mimeType) {
		return isMimeTypeInCategory(mimeType, MIME_TYPE_CATEGORY_VIDEO);
	}

	protected boolean isMimeTypeInCategory(String mimeType, String category) {
		try {
			String cat = getMimeTypeSettings().getProperty(mimeType);
			if (cat != null) {
				return cat.equals(category);
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
		return false;
	}

	public boolean isPDF(String mimeType) {
		return MIME_TYPE_PDF_1.equals(mimeType)
				|| MIME_TYPE_PDF_2.equals(mimeType);
	}

	public boolean isPowerPoint(String mimeType) {
		return MIME_TYPE_POWERPOINT.equals(mimeType);
	}

	public boolean isExcel(String mimeType) {
		return MIME_TYPE_EXCEL.equals(mimeType);
	}

	public boolean isWord(String mimeType) {
		return MIME_TYPE_WORD.equals(mimeType);
	}

	public String[] getMimeTypesInCategory(String category) {
		if (MIME_TYPE_CATEGORY_AUDIO.equals(category)) {
			return this.audio;
		} else if (MIME_TYPE_CATEGORY_BINARY.equals(category)) {
			return this.binary;
		} else if (MIME_TYPE_CATEGORY_COMPRESSED.equals(category)) {
			return this.compressed;
		} else if (MIME_TYPE_CATEGORY_DOCUMENT.equals(category)) {
			return this.document;
		} else if (MIME_TYPE_CATEGORY_FOLDER.equals(category)) {
			return this.folder;
		} else if (MIME_TYPE_CATEGORY_IMAGE.equals(category)) {
			return this.image;
		} else if (MIME_TYPE_CATEGORY_VECTOR.equals(category)) {
			return this.vector;
		} else if (MIME_TYPE_CATEGORY_VIDEO.equals(category)) {
			return this.video;
		}
		return null;
	}

	private static String getMimeType(String name) {
		String fileName = name;
		if (StringUtil.isEmpty(fileName)) {
			LOGGER.warning("Unable to resolve mime type from empty file name");
			return null;
		}

		// reduce filename by removing query string
		// files can be loaded using something like image.png?v=1.2.3
		if (fileName.contains(CoreConstants.QMARK) && fileName.contains(CoreConstants.EQ)) {
			fileName = fileName.substring(0, fileName.lastIndexOf(CoreConstants.QMARK));
		}

		if (StringUtil.isEmpty(fileName)) {
			LOGGER.warning("Unable to resolve mime type from file " + name);
			return null;
		}

		int lastDot = fileName.lastIndexOf(CoreConstants.DOT);
		if (lastDot == -1 || lastDot + 1 > fileName.length()) {
			LOGGER.warning("Unable to resolve mime type from file " + fileName);
			return null;
		}

		String fileType = fileName.substring(lastDot + 1).toLowerCase();
		try {
			MimeType type = MimeType.valueOf(fileType);
			if (type != null)
				return type.getMimeType();
		} catch (Exception e) {}

		initializeMimeTypesMap();

		String mimeType = MIME_TYPES_MAPPING.get(fileType);
		if (!StringUtil.isEmpty(mimeType))
			return mimeType;

		return null;
	}

	public static String resolveMimeTypeFromFileName(String fileName) {
		String mimeType = getMimeType(fileName);
		if (!StringUtil.isEmpty(mimeType))
			return mimeType;

		return StringUtil.isEmpty(mimeType) ? new MimetypesFileTypeMap().getContentType(fileName) : mimeType;
	}

}