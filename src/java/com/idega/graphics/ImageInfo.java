package com.idega.graphics;



/*

 * ImageInfo.java

 *

 * Version 1.1

 *

 * A Java class to determine image width, height and color depth for

 * a number of image file formats.

 *

 * Written by Marco Schmidt <marcoschmidt@users.sourceforge.net>

 *

 * Contributed to the Public Domain.

 *

 * Last modification 2002-03-15

 */



import java.io.DataInput;

import java.io.FileInputStream;

import java.io.InputStream;

import java.io.IOException;



/**

 * Get file format, image resolution, number of bits per pixel and optionally

 * number of images from

 * JPEG, GIF, BMP, PCX, PNG, IFF, RAS, PBM, PGM, PPM and PSD files

 * (or input streams).

 * <p>

 * Use the class like this:

 * <pre>

 * ImageInfo ii = new ImageInfo();

 * ii.setInput(in); // in can be InputStream or RandomAccessFile

 * ii.setDetermineImageNumber(true); // default is false

 * if (!ii.check()) {

 *   System.err.println("Not a supported image file format.");

 * } else {

 *  System.out.println(ii.getFormatName() + ", " + ii.getMimeType() +

 *   ", " + ii.getWidth() + " x " + ii.getHeight() + " pixels, " +

 *   ii.getBitsPerPixel() + " bits per pixel, " + ii.getNumberOfImages() +

 *   " image(s).");

 * }

 * </pre>

 * You can also use this class as a command line program.

 * Call it with a number of image file names as parameters:

 * <pre>

 *   java ImageInfo *.jpg *.png *.gif

 * </pre>

 * or call it without parameters and pipe data to it:

 * <pre>

 *   cat image.jpg | java ImageInfo

 * </pre>

 * <p>

 * Known limitations:

 * <ul>

 * <li>When the determination of the number of images is turned off, GIF bits

 *  per pixel are only read from the global header.

 *  For some GIFs, local palettes change this to a typically larger

 *  value. To be certain to get the correct color depth, call

 *  setDetermineImageNumber(true) before calling check().

 *  The complete scan over the GIF file will take additional time.</li>

 * <li>Transparency information is not included in the bits per pixel count.

 *  Actually, it was my decision not to include those bits, so it's a feature! ;-)</li>

 * </ul>

 * <p>

 * Requirements:

 * <ul>

 * <li>Java 1.1 or higher</li>

 * </ul>

 * <p>

 * The latest version can be found at <a href="http://www.geocities.com/marcoschmidt.geo/image-info.html">http://www.geocities.com/marcoschmidt.geo/image-info.html</a>.

 * <p>

 * Written by <a href="mailto:marcoschmidt@users.sourceforge.net">Marco Schmidt</a>.

 * <p>

 * This class is contributed to the Public Domain.

 * Use it at your own risk.

 * <p>

 * Last modification 2002-03-15.

 * <p>

 * History:

 * <ul>

 * <li><strong>2001-08-24</strong> Initial version.</li>

 * <li><strong>2001-10-13</strong> Added support for the file formats BMP and PCX.</li>

 * <li><strong>2001-10-16</strong> Fixed bug in read(int[], int, int) that returned

 * <li><strong>2002-01-22</strong> Added support for file formats Amiga IFF and Sun Raster (RAS).</li>

 * <li><strong>2002-01-24</strong> Added support for file formats Portable Bitmap / Graymap / Pixmap (PBM, PGM, PPM) and Adobe Photoshop (PSD).

 *   Added new method getMimeType() to return the MIME type associated with a particular file format.</li>

 * <li><strong>2002-03-15</strong> Added support to recognize number of images in file. Only works with GIF.

 *   Use {@link #setDetermineImageNumber} with <code>true</code> as argument to identify animated GIFs

 *   ({@link #getNumberOfImages()} will return a value larger than <code>1</code>).</li>

 * </ul>

 */

public class ImageInfo {

	/** Return value of {@link #getFormat()} for JPEG streams. */

	public static final int FORMAT_JPEG = 0;

	/** Return value of {@link #getFormat()} for GIF streams. */

	public static final int FORMAT_GIF = 1;

	/** Return value of {@link #getFormat()} for PNG streams. */

	public static final int FORMAT_PNG = 2;

	/** Return value of {@link #getFormat()} for BMP streams. */

	public static final int FORMAT_BMP = 3;

	/** Return value of {@link #getFormat()} for PCX streams. */

	public static final int FORMAT_PCX = 4;

	/** Return value of {@link #getFormat()} for IFF streams. */

	public static final int FORMAT_IFF = 5;

	/** Return value of {@link #getFormat()} for RAS streams. */

	public static final int FORMAT_RAS = 6;

	/** Return value of {@link #getFormat()} for PBM streams. */

	public static final int FORMAT_PBM = 7;

	/** Return value of {@link #getFormat()} for PBM streams. */

	public static final int FORMAT_PGM = 8;

	/** Return value of {@link #getFormat()} for PBM streams. */

	public static final int FORMAT_PPM = 9;

	/** Return value of {@link #getFormat()} for PBM streams. */

	public static final int FORMAT_PSD = 10;

	private static final String[] FORMAT_NAMES =

		{"JPEG", "GIF", "PNG", "BMP", "PCX",

		 "IFF", "RAS", "PBM", "PGM", "PPM",

		 "PSD"};

	private static final String[] MIME_TYPE_STRINGS =

		{"image/jpeg", "image/gif", "image/png", "image/bmp", "image/pcx",

		 "image/iff", "image/ras", "image/x-portable-bitmap", "image/x-portable-graymap", "image/x-portable-pixmap",

		 "image/psd"};

	private int width;

	private int height;

	private int bitsPerPixel;

	private int format;

	private InputStream in;

	private DataInput din;

	private boolean determineNumberOfImages;

	private int numberOfImages;



	/**

	 * Call this method after you have provided an input stream or file

	 * using {@link #setInput(InputStream)} or {@link #setInput(DataInput)}.

	 * If true is returned, the file format was known and you information

	 * about its content can be retrieved using the various getXyz methods.

	 * @return if information could be retrieved from input

	 */

	public boolean check() {

		format = -1;

		width = -1;

		height = -1;

		bitsPerPixel = -1;

		numberOfImages = 1;

		try {

			int b1 = read() & 0xff;

			int b2 = read() & 0xff;

			if (b1 == 0x47 && b2 == 0x49) {

				return checkGif();

			}

			else

			if (b1 == 0x89 && b2 == 0x50) {

				return checkPng();

			}

			else

			if (b1 == 0xff && b2 == 0xd8) {

				return checkJpeg();

			}

			else

			if (b1 == 0x42 && b2 == 0x4d) {

				return checkBmp();

			}

			else

			if (b1 == 0x0a && b2 < 0x06) {

				return checkPcx();

			}

			else

			if (b1 == 0x46 && b2 == 0x4f) {

				return checkIff();

			}

			else

			if (b1 == 0x59 && b2 == 0xa6) {

				return checkRas();

			}

			else

			if (b1 == 0x50 && b2 >= 0x31 && b2 <= 0x36) {

				return checkPnm(b2 - '0');

			}

			else

			if (b1 == 0x38 && b2 == 0x42) {

				return checkPsd();

			}

			else {

				return false;

			}

		} catch (IOException ioe) {

			return false;

		}

	}



	private boolean checkBmp() throws IOException {

		byte[] a = new byte[28];

		if (read(a) != a.length) {

			return false;

		}

		width = getIntLittleEndian(a, 16);

		height = getIntLittleEndian(a, 20);

		bitsPerPixel = getShortLittleEndian(a, 26);

		if (width < 1 || height < 1) {

			return false;

		}

		if (bitsPerPixel != 1 && bitsPerPixel != 4 &&

		    bitsPerPixel != 8 && bitsPerPixel != 16 &&

		    bitsPerPixel != 24 && bitsPerPixel != 32) {

		    return false;

		}

		format = FORMAT_BMP;

		return true;

	}



	private boolean checkGif() throws IOException {

		final byte[] GIF_MAGIC_87A = {0x46, 0x38, 0x37, 0x61};

		final byte[] GIF_MAGIC_89A = {0x46, 0x38, 0x39, 0x61};

		byte[] a = new byte[11]; // 4 from the GIF signature + 7 from the global header

		if (read(a) != 11) {

			return false;

		}

		if ((!equals(a, 0, GIF_MAGIC_89A, 0, 4)) &&

			(!equals(a, 0, GIF_MAGIC_87A, 0, 4))) {

			return false;

		}

		format = FORMAT_GIF;

		width = getShortLittleEndian(a, 4);

		height = getShortLittleEndian(a, 6);

		int flags = a[8] & 0xff;

		bitsPerPixel = ((flags >> 4) & 0x07) + 1;

		if (!determineNumberOfImages) {

			return true;

		}

		// skip global color palette

		if ((flags & 0x80) != 0) {

			int tableSize = (1 << ((flags & 7) + 1)) * 3;

			skip(tableSize);

		}

		numberOfImages = 0;

		int blockType;

		do

		{

			blockType = in.read();

			switch(blockType)

			{

				case(0x2c): // image separator

				{

					if (read(a, 0, 9) != 9) {

						return false;

					}

					flags = a[8] & 0xff;

					int localBitsPerPixel = (flags & 0x07) + 1;

					if (localBitsPerPixel > bitsPerPixel) {

						bitsPerPixel = localBitsPerPixel;

					}

					if ((flags & 0x80) != 0) {

						skip((1 << localBitsPerPixel) * 3);

					}

					skip(1); // initial code length

					int n;

					do

					{

						n = in.read();

						if (n > 0) {

							in.skip(n);

						}

						else

						if (n == -1) {

							return false;

						}

					}

					while (n > 0);

					numberOfImages++;

					break;

				}

				case(0x21): // extension

				{

					skip(1); // extension type

					int n;

					do

					{

						n = in.read();

						if (n > 0) {

							in.skip(n);

						}

						else

						if (n == -1) {

							return false;

						}

					}

					while (n > 0);

					break;

				}

				case(0x3b): // end of file

				{

					break;

				}

				default:

				{

					return false;

				}

			}

		}

		while (blockType != 0x3b);

		return true;

	}



	private boolean checkIff() throws IOException {

		byte[] a = new byte[10];

		// read remaining 2 bytes of file id, 4 bytes file size

		// and 4 bytes IFF subformat

		if (read(a, 0, 10) != 10) {

			return false;

		}

		final byte[] IFF_RM = {0x52, 0x4d};

		if (!equals(a, 0, IFF_RM, 0, 2)) {

			return false;

		}

		int type = getIntBigEndian(a, 6);

		if (type != 0x494c424d && // type must be ILBM...

		    type != 0x50424d20) { // ...or PBM

		    return false;

		}

		// loop chunks to find BMHD chunk

		do {

			if (read(a, 0, 8) != 8) {

				return false;

			}

			int chunkId = getIntBigEndian(a, 0);

			int size = getIntBigEndian(a, 4);

			if ((size & 1) == 1) {

				size++;

			}

			if (chunkId == 0x424d4844) { // BMHD chunk

				if (read(a, 0, 9) != 9) {

					return false;

				}

				format = FORMAT_IFF;

				width = getShortBigEndian(a, 0);

				height = getShortBigEndian(a, 2);

				bitsPerPixel = a[8] & 0xff;

				return (width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel < 33);

			} else {

				skip(size);

			}

		} while (true);

	}



	private boolean checkJpeg() throws IOException {

		byte[] data = new byte[6];

		while (true) {

			if (read(data, 0, 4) != 4) {

				return false;

			}

			int marker = getShortBigEndian(data, 0);

			int size = getShortBigEndian(data, 2);

			if ((marker & 0xff00) != 0xff00) {

				return false; // not a valid marker

			}

			if (marker >= 0xffc0 && marker <= 0xffcf && marker != 0xffc4 && marker != 0xffc8) {

				if (read(data) != 6) {

					return false;

				}

				format = FORMAT_JPEG;

				bitsPerPixel = (data[0] & 0xff) * (data[5] & 0xff);

				width = getShortBigEndian(data, 3);

				height = getShortBigEndian(data, 1);

				return true;

			} else {

				skip(size - 2);

			}

		}

	}



	private boolean checkPcx() throws IOException {

		byte[] a = new byte[64];

		if (read(a) != a.length) {

			return false;

		}

		if (a[0] != 1) { // encoding, 1=RLE is only valid value

			return false;

		}

		// width / height

		int x1 = getShortLittleEndian(a, 2);

		int y1 = getShortLittleEndian(a, 4);

		int x2 = getShortLittleEndian(a, 6);

		int y2 = getShortLittleEndian(a, 8);

		if (x1 < 0 || x2 < x1 || y1 < 0 || y2 < y1) {

			return false;

		}

		width = x2 - x1 + 1;

		height = y2 - y1 + 1;

		// color depth

		int bits = a[1];

		int planes = a[63];

		if (planes == 1 &&

		    (bits == 1 || bits == 2 || bits == 4 || bits == 8)) {

			// paletted

			bitsPerPixel = bits;

		} else

		if (planes == 3 && bits == 8) {

			// RGB truecolor

			bitsPerPixel = 24;

		} else {

			return false;

		}

		format = FORMAT_PCX;

		return true;

	}



	private boolean checkPng() throws IOException {

		final byte[] PNG_MAGIC = {0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};

		byte[] a = new byte[24];

		if (read(a) != 24) {

			return false;

		}

		if (!equals(a, 0, PNG_MAGIC, 0, 6)) {

			return false;

		}

		format = FORMAT_PNG;

		width = getIntBigEndian(a, 14);

		height = getIntBigEndian(a, 18);

		bitsPerPixel = a[22] & 0xff;

		int colorType = a[23] & 0xff;

		if (colorType == 2 || colorType == 6) {

			bitsPerPixel *= 3;

		}

		return true;

	}



	private boolean checkPnm(int id) throws IOException {

		if (id < 1 || id > 6) {

			return false;

		}

		final int[] PNM_FORMATS = {FORMAT_PBM, FORMAT_PGM, FORMAT_PPM};

		format = PNM_FORMATS[(id - 1) % 3];

		boolean hasPixelResolution = false;

		String s;

		while (true)

		{

			s = readLine();

			if (s != null) {

				s = s.trim();

			}

			if (s == null || s.length() < 1) {

				continue;

			}

			if (s.charAt(0) == '#') { // comment

				continue;

			}

			if (!hasPixelResolution) { // split "343 966" into width=343, height=966

				int spaceIndex = s.indexOf(' ');

				if (spaceIndex == -1) {

					return false;

				}

				String widthString = s.substring(0, spaceIndex);

				spaceIndex = s.lastIndexOf(' ');

				if (spaceIndex == -1) {

					return false;

				}

				String heightString = s.substring(spaceIndex + 1);

				try {

					width = Integer.parseInt(widthString);

					height = Integer.parseInt(heightString);

				} catch (NumberFormatException nfe) {

					return false;

				}

				if (width < 1 || height < 1) {

					return false;

				}

				if (format == FORMAT_PBM) {

					bitsPerPixel = 1;

					return true;

				}

				hasPixelResolution = true;

			}

			else

			{

				int maxSample;

				try {

					maxSample = Integer.parseInt(s);

				} catch (NumberFormatException nfe) {

					return false;

				}

				if (maxSample < 0) {

					return false;

				}

				for (int i = 0; i < 25; i++) {

					if (maxSample < (1 << (i + 1))) {

						bitsPerPixel = i + 1;

						if (format == FORMAT_PPM) {

							bitsPerPixel *= 3;

						}

						return true;

					}

				}

				return false;

			}

		}

	}



	private boolean checkPsd() throws IOException {

		byte[] a = new byte[24];

		if (read(a) != a.length) {

			return false;

		}

		final byte[] PSD_MAGIC = {0x50, 0x53};

		if (!equals(a, 0, PSD_MAGIC, 0, 2)) {

			return false;

		}

		format = FORMAT_PSD;

		width = getIntBigEndian(a, 16);

		height = getIntBigEndian(a, 12);

		int channels = getShortBigEndian(a, 10);

		int depth = getShortBigEndian(a, 20);

		bitsPerPixel = channels * depth;

		return (width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel <= 64);

	}



	private boolean checkRas() throws IOException {

		byte[] a = new byte[14];

		if (read(a) != a.length) {

			return false;

		}

		final byte[] RAS_MAGIC = {0x6a, (byte)0x95};

		if (!equals(a, 0, RAS_MAGIC, 0, 2)) {

			return false;

		}

		format = FORMAT_RAS;

		width = getIntBigEndian(a, 2);

		height = getIntBigEndian(a, 6);

		bitsPerPixel = getIntBigEndian(a, 10);

		return (width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel <= 24);

	}



	private boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int num) {

		while (num-- > 0) {

			if (a1[offs1++] != a2[offs2++]) {

				return false;

			}

		}

		return true;

	}



	/**

	 * If {@link #check()} was successful, returns the image's number of bits per pixel.

	 * Does not include transparency information like the alpha channel.

	 * @return number of bits per image pixel

	 */

	public int getBitsPerPixel() {

		return bitsPerPixel;

	}



	/**

	 * If {@link #check()} was successful, returns the image format as one

	 * of the FORMAT_xyz constants from this class.

	 * Use {@link #getFormatName()} to get a textual description of the file format.

	 * @return file format as a FORMAT_xyz constant

	 */

	public int getFormat() {

		return format;

	}



	/**

	 * If {@link #check()} was successful, returns the image format's name.

	 * Use {@link #getFormat()} to get a unique number.

	 * @return file format name

	 */

	public String getFormatName() {

		if (format >= 0 && format < FORMAT_NAMES.length) {

			return FORMAT_NAMES[format];

		} else {

			return "?";

		}

	}



	/**

	 * If {@link #check()} was successful, returns one the image's vertical

	 * resolution in pixels.

	 * @return image height in pixels

	 */

	public int getHeight() {

		return height;

	}



	private int getIntBigEndian(byte[] a, int offs) {

		return

			(a[offs] & 0xff) << 24 |

			(a[offs + 1] & 0xff) << 16 |

			(a[offs + 2] & 0xff) << 8 |

			a[offs + 3] & 0xff;

	}



	private int getIntLittleEndian(byte[] a, int offs) {

		return

			(a[offs + 3] & 0xff) << 24 |

			(a[offs + 2] & 0xff) << 16 |

			(a[offs + 1] & 0xff) << 8 |

			a[offs] & 0xff;

	}



	/**

	 * If {@link #check()} was successful, returns a String with the

	 * MIME type of the format.

	 * @return MIME type, e.g. <code>image/jpeg</code>

	 */

	public String getMimeType() {

		if (format >= 0 && format < MIME_TYPE_STRINGS.length) {

			return MIME_TYPE_STRINGS[format];

		} else {

			return null;

		}

	}



	/**

	 * Returns the number of images in the examined file

	 * if <code>setDetermineImageNumber(true);</code> was called before

	 * a successful call to {@link #check()}.

	 * @return number of images in file

	 */

	public int getNumberOfImages()

	{

		return numberOfImages;

	}



	private int getShortBigEndian(byte[] a, int offs) {

		return

			(a[offs] & 0xff) << 8 |

			(a[offs + 1] & 0xff);

	}



	private int getShortLittleEndian(byte[] a, int offs) {

		return (a[offs] & 0xff) | (a[offs + 1] & 0xff) << 8;

	}



	/**

	 * If {@link #check()} was successful, returns one the image's horizontal

	 * resolution in pixels.

	 * @return image width in pixels

	 */

	public int getWidth() {

		return width;

	}



	/**

	 * To use this class as a command line application, give it either

	 * some file names as parameters (information on them will be

	 * printed to standard output, one line per file) or call

	 * it with no parameters. It will then check data given to it

	 * via standard input.

	 * @param args the program arguments which must be file names

	 */

	public static void main(String[] args) {

		ImageInfo imageInfo = new ImageInfo();

		if (args.length == 0) {

			run(System.in, imageInfo);

		} else {

			int index = 0;

			while (index < args.length) {

				FileInputStream in = null;

				try {

					String filename = args[index++];

					System.out.print(filename + ";");

					in = new FileInputStream(filename);

					run(in, imageInfo);

					in.close();

				} catch (Exception e) {

					System.out.println(e);

					try {

						in.close();

					} catch (Exception ee) {

					}

				}

			}

		}

	}



	private int read() throws IOException {

		if (in != null) {

			return in.read();

		} else {

			return din.readByte();

		}

	}



	private int read(byte[] a) throws IOException {

		if (in != null) {

			return in.read(a);

		} else {

			din.readFully(a);

			return a.length;

		}

	}



	private int read(byte[] a, int offset, int num) throws IOException {

		if (in != null) {

			return in.read(a, offset, num);

		} else {

			din.readFully(a, offset, num);

			return num;

		}

	}



	private String readLine() throws IOException {

		return readLine(new StringBuffer());

	}



	private String readLine(StringBuffer sb) throws IOException {

		boolean finished;

		do {

			int value = read();

			finished = (value == -1 || value == 10);

			if (!finished) {

				sb.append((char)value);

			}

		} while (!finished);

		return sb.toString();

	}



	private String readLine(int firstChar) throws IOException {

		StringBuffer result = new StringBuffer();

		result.append((char)firstChar);

		return readLine(result);

	}



	private static void run(InputStream in, ImageInfo imageInfo) {

		imageInfo.setInput(in);

		imageInfo.setDetermineImageNumber(true);

		if (imageInfo.check()) {

			System.out.println(

				imageInfo.getFormatName() + ";" +

				imageInfo.getMimeType() + ";" +

				imageInfo.getWidth() + ";" +

				imageInfo.getHeight() + ";" +

				imageInfo.getBitsPerPixel() + ";" +

				imageInfo.getNumberOfImages());

		} else {

			System.out.println("?");

		}

	}



	/**

	 * Specify whether the number of images in a file are to be

	 * determined - default is <code>false</code>.

	 * Will only make a difference with file formats that do support

	 * more than one image like GIF.

	 * If this method is called with <code>true</code> as argument,

	 * the actual number of images can be queried via

	 * {@link #getNumberOfImages()} after a successful call to

	 * {@link #check()}.

	 * @param newValue will the number of images be determined?

	 */

	public void setDetermineImageNumber(boolean newValue)

	{

		determineNumberOfImages = newValue;

	}



	/**

	 * Set the input stream to the argument stream (or file).

	 * Note that {@link java.io.RandomAccessFile} implements

	 * {@link java.io.DataInput}.

	 * @param dataInput the input stream to read from

	 */

	public void setInput(DataInput dataInput) {

		din = dataInput;

		in = null;

	}



	/**

	 * Set the input stream to the argument stream (or file).

	 * @param inputStream the input stream to read from

	 */

	public void setInput(InputStream inputStream) {

		in = inputStream;

		din = null;

	}



	private void skip(int num) throws IOException {

		if (in != null) {

			in.skip(num);

		} else {

			din.skipBytes(num);

		}

	}

}

